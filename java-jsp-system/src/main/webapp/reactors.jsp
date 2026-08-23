<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard &middot; Reactor Monitor</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header class="site-header">
        <h1>☢️ Reactor Dashboard</h1>
        <p class="tagline"><a href="${pageContext.request.contextPath}/index.jsp">&larr; Home</a></p>
    </header>

    <main class="dashboard">

        <section class="stats">
            <div class="stat-box">
                <span class="stat-value">${onlineCount}</span>
                <span class="stat-label">Reactors Online</span>
            </div>
            <div class="stat-box">
                <span class="stat-value" id="total-output"><fmt:formatNumber value="${totalOutput}" maxFractionDigits="1"/></span>
                <span class="stat-label">Total Output (MW)</span>
            </div>
            <div class="stat-box">
                <span class="stat-value">${reactors.size()}</span>
                <span class="stat-label">Total Units</span>
            </div>
        </section>

        <section class="card">
            <h2>Fleet Status</h2>
            <table id="reactor-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Location</th>
                    <th>Status</th>
                    <th>Temp (&deg;C)</th>
                    <th>Output (MW)</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="r" items="${reactors}">
                    <tr data-id="${r.id}" class="status-${r.status} ${r.overheating ? 'overheating' : ''}">
                        <td>${r.name}</td>
                        <td>${r.location}</td>
                        <td>
                            <span class="badge badge-${r.status}">${r.status}</span>
                        </td>
                        <td class="cell-temp"><fmt:formatNumber value="${r.temperatureC}" maxFractionDigits="1"/></td>
                        <td class="cell-output"><fmt:formatNumber value="${r.outputMW}" maxFractionDigits="1"/></td>
                        <td class="row-actions">
                            <button type="button" class="btn-toggle" data-id="${r.id}">Cycle Status</button>
                            <form method="post" action="${pageContext.request.contextPath}/reactors" class="inline-form js-delete-form">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${r.id}">
                                <button type="submit" class="btn-delete">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty reactors}">
                    <tr><td colspan="6" class="empty-row">No reactors yet &mdash; add one below.</td></tr>
                </c:if>
                </tbody>
            </table>
            <p class="live-note">
                <span id="live-indicator" class="live-dot"></span>
                Readings refresh automatically every 5 seconds.
            </p>
        </section>

        <section class="card">
            <h2>Add Reactor</h2>
            <form method="post" action="${pageContext.request.contextPath}/reactors" id="add-form">
                <input type="hidden" name="action" value="add">
                <div class="form-row">
                    <label for="name">Name</label>
                    <input type="text" id="name" name="name" placeholder="Reactor Unit 4" required>
                </div>
                <div class="form-row">
                    <label for="location">Location</label>
                    <input type="text" id="location" name="location" placeholder="Plant site" required>
                </div>
                <button type="submit" class="button">Add Reactor</button>
                <p id="add-form-error" class="form-error" hidden>Please fill in both fields.</p>
            </form>
        </section>

    </main>

    <footer class="site-footer">
        <p>Reactor Monitor &mdash; demo application, not connected to any real reactor.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/app.js"
            data-api-url="${pageContext.request.contextPath}/reactors"></script>
</body>
</html>
