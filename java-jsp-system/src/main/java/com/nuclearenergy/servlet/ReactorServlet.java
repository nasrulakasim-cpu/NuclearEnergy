package com.nuclearenergy.servlet;

import com.nuclearenergy.dao.ReactorDAO;
import com.nuclearenergy.model.Reactor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Handles the reactor dashboard: server-rendered JSP view, form-based
 * add/delete actions (POST/redirect/GET pattern), and a small JSON API used
 * by js/app.js to poll live readings and toggle status without a full page
 * reload.
 */
@WebServlet("/reactors")
public class ReactorServlet extends HttpServlet {

    private final ReactorDAO dao = ReactorDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        dao.simulateReadings();

        if ("json".equalsIgnoreCase(req.getParameter("format"))) {
            writeJson(resp, dao.getAll());
            return;
        }

        req.setAttribute("reactors", dao.getAll());
        req.setAttribute("totalOutput", dao.totalOutputMW());
        req.setAttribute("onlineCount", dao.onlineCount());
        req.getRequestDispatcher("/reactors.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        boolean isAjax = "true".equals(req.getParameter("ajax"));

        if ("add".equals(action)) {
            handleAdd(req);
        } else if ("delete".equals(action)) {
            handleDelete(req);
        } else if ("toggleStatus".equals(action)) {
            Optional<Reactor> updated = handleToggleStatus(req);
            if (isAjax) {
                if (updated.isPresent()) {
                    writeJson(resp, List.of(updated.get()));
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Reactor not found");
                }
                return;
            }
        }

        // Plain form submissions redirect back to the dashboard (PRG pattern)
        // so a page refresh doesn't resubmit the action.
        resp.sendRedirect(req.getContextPath() + "/reactors");
    }

    private void handleAdd(HttpServletRequest req) {
        String name = trimOrEmpty(req.getParameter("name"));
        String location = trimOrEmpty(req.getParameter("location"));
        if (name.isEmpty() || location.isEmpty()) {
            return; // server-side guard; the form also validates client-side
        }
        dao.add(name, location, Reactor.OFFLINE, 20.0, 0.0);
    }

    private void handleDelete(HttpServletRequest req) {
        parseInt(req.getParameter("id")).ifPresent(dao::delete);
    }

    private Optional<Reactor> handleToggleStatus(HttpServletRequest req) {
        Optional<Integer> id = parseInt(req.getParameter("id"));
        return id.flatMap(dao::toggleStatus);
    }

    private void writeJson(HttpServletResponse resp, List<Reactor> reactors) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < reactors.size(); i++) {
            Reactor r = reactors.get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append('{')
                    .append("\"id\":").append(r.getId()).append(',')
                    .append("\"name\":\"").append(escape(r.getName())).append("\",")
                    .append("\"location\":\"").append(escape(r.getLocation())).append("\",")
                    .append("\"status\":\"").append(r.getStatus()).append("\",")
                    .append("\"temperatureC\":").append(String.format(Locale.US, "%.1f", r.getTemperatureC())).append(',')
                    .append("\"outputMW\":").append(String.format(Locale.US, "%.1f", r.getOutputMW())).append(',')
                    .append("\"overheating\":").append(r.isOverheating())
                    .append('}');
        }
        json.append(']');
        try (PrintWriter out = resp.getWriter()) {
            out.write(json.toString());
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String trimOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static Optional<Integer> parseInt(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
