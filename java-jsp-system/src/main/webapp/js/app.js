// Client-side behavior for the reactor dashboard (reactors.jsp).
// Responsibilities:
//   1. Poll the servlet's JSON endpoint and update readings live.
//   2. Toggle a reactor's status via AJAX (no full page reload).
//   3. Validate the "add reactor" form before it submits.
//   4. Confirm before deleting a reactor.
(function () {
    "use strict";

    var scriptTag = document.currentScript;
    var apiUrl = scriptTag.getAttribute("data-api-url");
    var POLL_INTERVAL_MS = 5000;
    var OVERHEAT_THRESHOLD = 400.0;

    function apiUrlWithParams(params) {
        return apiUrl + "?" + params;
    }

    function refreshReadings() {
        fetch(apiUrlWithParams("format=json"))
            .then(function (response) {
                if (!response.ok) {
                    throw new Error("Request failed: " + response.status);
                }
                return response.json();
            })
            .then(applyReadings)
            .catch(function (err) {
                console.error("Could not refresh reactor readings:", err);
            });
    }

    function applyReadings(reactors) {
        var totalOutput = 0;

        reactors.forEach(function (reactor) {
            totalOutput += reactor.outputMW;

            var row = document.querySelector('tr[data-id="' + reactor.id + '"]');
            if (!row) {
                return;
            }

            row.querySelector(".cell-temp").textContent = reactor.temperatureC.toFixed(1);
            row.querySelector(".cell-output").textContent = reactor.outputMW.toFixed(1);

            var badge = row.querySelector(".badge");
            badge.textContent = reactor.status;
            badge.className = "badge badge-" + reactor.status;

            row.classList.toggle("overheating", reactor.temperatureC >= OVERHEAT_THRESHOLD);
        });

        var totalOutputEl = document.getElementById("total-output");
        if (totalOutputEl) {
            totalOutputEl.textContent = totalOutput.toFixed(1);
        }
    }

    function toggleStatus(id) {
        var body = "action=toggleStatus&ajax=true&id=" + encodeURIComponent(id);
        fetch(apiUrl, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: body
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error("Toggle failed: " + response.status);
                }
                return response.json();
            })
            .then(applyReadings)
            .catch(function (err) {
                console.error("Could not toggle reactor status:", err);
                alert("Sorry, that status change failed. Please try again.");
            });
    }

    function wireToggleButtons() {
        document.querySelectorAll(".btn-toggle").forEach(function (btn) {
            btn.addEventListener("click", function () {
                toggleStatus(btn.getAttribute("data-id"));
            });
        });
    }

    function wireDeleteConfirmation() {
        document.querySelectorAll(".js-delete-form").forEach(function (form) {
            form.addEventListener("submit", function (event) {
                if (!confirm("Delete this reactor? This cannot be undone.")) {
                    event.preventDefault();
                }
            });
        });
    }

    function wireAddFormValidation() {
        var form = document.getElementById("add-form");
        if (!form) {
            return;
        }
        var errorEl = document.getElementById("add-form-error");

        form.addEventListener("submit", function (event) {
            var name = form.elements["name"].value.trim();
            var location = form.elements["location"].value.trim();

            if (!name || !location) {
                event.preventDefault();
                errorEl.hidden = false;
            } else {
                errorEl.hidden = true;
            }
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        wireToggleButtons();
        wireDeleteConfirmation();
        wireAddFormValidation();
        setInterval(refreshReadings, POLL_INTERVAL_MS);
    });
})();
