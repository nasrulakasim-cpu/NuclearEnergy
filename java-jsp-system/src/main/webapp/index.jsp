<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuclear Reactor Monitoring System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header class="site-header">
        <h1>☢️ Nuclear Reactor Monitoring System</h1>
        <p class="tagline">A simple Java &middot; JSP &middot; JavaScript demo application</p>
    </header>

    <main class="landing">
        <section class="card">
            <h2>Welcome</h2>
            <p>
                This is a small full-stack example built with:
            </p>
            <ul>
                <li><strong>Java</strong> &mdash; a servlet (<code>ReactorServlet</code>) holding the
                    application logic and an in-memory DAO acting as the data store.</li>
                <li><strong>JSP</strong> &mdash; server-rendered pages (this page and the dashboard)
                    using JSTL instead of scriptlets.</li>
                <li><strong>JavaScript</strong> &mdash; the dashboard polls a JSON endpoint to show
                    live-updating readings and handles status toggling without a page reload.</li>
            </ul>
            <p>
                <a class="button" href="${pageContext.request.contextPath}/reactors">Open the Dashboard &rarr;</a>
            </p>
        </section>
    </main>

    <footer class="site-footer">
        <p>Reactor Monitor &mdash; demo application, not connected to any real reactor.</p>
    </footer>
</body>
</html>
