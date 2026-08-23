# Nuclear Reactor Monitoring System (Java · JSP · JavaScript)

A simple full-stack demo web app: a small "fleet" of reactors you can add,
delete, and cycle through statuses (`OFFLINE` → `MAINTENANCE` → `ONLINE`),
with live-simulated temperature and output readings.

## Stack

- **Java** — `ReactorServlet` (`javax.servlet`) holds the request-handling
  logic; `ReactorDAO` is an in-memory data store (no database needed).
- **JSP** — `index.jsp` and `reactors.jsp` render the server-side views,
  using JSTL (`<c:forEach>`, `<c:if>`, `<fmt:formatNumber>`) rather than
  scriptlets.
- **JavaScript** — `js/app.js` polls a small JSON API
  (`GET /reactors?format=json`) every 5 seconds to update readings live,
  toggles a reactor's status over AJAX, and validates the "add reactor"
  form client-side.

## Project layout

```
java-jsp-system/
├── pom.xml
└── src/main/
    ├── java/com/nuclearenergy/
    │   ├── model/Reactor.java        # reactor data model
    │   ├── dao/ReactorDAO.java       # in-memory store + simulated readings
    │   └── servlet/ReactorServlet.java  # GET (view/JSON) + POST (add/delete/toggle)
    └── webapp/
        ├── index.jsp
        ├── reactors.jsp
        ├── css/style.css
        ├── js/app.js
        └── WEB-INF/web.xml
```

## Running it

Requires JDK 11+ and Maven. No separate application server install is
needed — the project bundles the Jetty Maven plugin:

```bash
cd java-jsp-system
mvn jetty:run
```

Then open <http://localhost:8080/> in a browser.

Alternatively, build a deployable WAR and drop it into any Servlet
4.0-compatible container (e.g. Tomcat 9's `webapps/` directory):

```bash
mvn package
# -> target/reactor-monitor.war
```

## How it works

- `GET /reactors` — renders the dashboard (forwards to `reactors.jsp`).
- `GET /reactors?format=json` — returns the current fleet as JSON; this is
  what `app.js` polls for live updates.
- `POST /reactors` with `action=add` — adds a new reactor (starts
  `OFFLINE`), then redirects back to the dashboard (POST/redirect/GET, so a
  page refresh won't resubmit the form).
- `POST /reactors` with `action=delete&id=...` — removes a reactor.
- `POST /reactors` with `action=toggleStatus&ajax=true&id=...` — cycles a
  reactor's status and returns the updated reactor as JSON (used by the
  "Cycle Status" button without reloading the page).

Rows are flagged with an `overheating` style client- and server-side when a
reactor's temperature reaches 400°C.
