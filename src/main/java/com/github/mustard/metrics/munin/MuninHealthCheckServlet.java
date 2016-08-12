package com.github.mustard.metrics.munin;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ExecutorService;

public class MuninHealthCheckServlet extends HttpServlet {

    private final transient HealthCheckRegistry registry;
    private final transient ExecutorService executorService;

    public MuninHealthCheckServlet(HealthCheckRegistry registry) {
        this.registry = registry;
        this.executorService = null;
    }

    public MuninHealthCheckServlet(HealthCheckRegistry registry, ExecutorService executorService) {
        this.registry = registry;
        this.executorService = executorService;
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/plain");
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        if (registry.getNames().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        } else {
            if (req.getParameter("config") != null) {
                writeConfigBody(resp.getWriter(), registry);
            } else {
                writeFetchBody(resp.getWriter(), runHealthChecks());
            }
        }

    }

    private void writeFetchBody(PrintWriter writer, SortedMap<String, HealthCheck.Result> results) {
        for (Map.Entry<String, HealthCheck.Result> check : results.entrySet()) {
            String sanitisedCheckName = sanitiseCheckName(check.getKey());
            writer.println(sanitisedCheckName + ".value " + check.getValue().isHealthy());
        }
    }

    private void writeConfigBody(PrintWriter writer, HealthCheckRegistry registry) {
        writer.println("graph_title Health Checks");
        writer.println("graph_vlabel Success");
        for (String checkName : registry.getNames()) {
            String sanitisedCheckName = sanitiseCheckName(checkName);
            writer.println(sanitisedCheckName + ".label " + sanitisedCheckName);
        }
    }

    private SortedMap<String, HealthCheck.Result> runHealthChecks() {
        if (executorService == null) {
            return registry.runHealthChecks();
        }
        return registry.runHealthChecks(executorService);
    }

    private String sanitiseCheckName(String checkName) {
        return checkName.replaceAll(" ", "_");
    }

}
