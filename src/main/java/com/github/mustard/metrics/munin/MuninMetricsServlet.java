package com.github.mustard.metrics.munin;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;

public class MuninMetricsServlet extends HttpServlet {

    private final transient MetricRegistry registry;
    private final transient ExecutorService executorService;

    public MuninMetricsServlet(MetricRegistry registry) {
        this.registry = registry;
        this.executorService = null;
    }

    public MuninMetricsServlet(MetricRegistry registry, ExecutorService executorService) {
        this.registry = registry;
        this.executorService = executorService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/plain");
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        if (registry.getNames().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        } else {
            if (req.getParameter("config") != null) {
                writeConfigBody(resp.getWriter(), registry);
            } else {
                writeFetchBody(resp.getWriter(), run());
            }
        }

    }

    private void writeFetchBody(PrintWriter writer, SortedMap<String, HealthCheck.Result> results) {
        for (Map.Entry<String, HealthCheck.Result> check : results.entrySet()) {
            String sanitisedCheckName = MuninMetricsUtil.sanitiseName(check.getKey());
            writer.println(sanitisedCheckName + ".value " + check.getValue().isHealthy());
        }
    }

    private void writeConfigBody(PrintWriter writer, MetricRegistry registry) {
        writer.println("graph_title Metrics");
        writer.println("graph_category metrics");
        writer.println("graph_info Application Metrics");

        SortedMap<String, Gauge> gauges = registry.getGauges();
        for (Map.Entry<String, Gauge> stringGaugeEntry : gauges.entrySet()) {
            writer.println(MuninMetricsUtil.sanitiseKey(stringGaugeEntry.getKey()));
        }
//        gauges.
//        for (String checkName : registry.getNames()) {
//            String sanitisedCheckName = MuninMetricsUtil.sanitiseCheckName(checkName);
//            writer.println(sanitisedCheckName + ".label " + sanitisedCheckName);
//        }
    }

    private SortedMap<String, HealthCheck.Result> run() {
        return new TreeMap<>();
//        if (executorService == null) {
//            return registry.runHealthChecks();
//        }
//        return registry.runHealthChecks(executorService);
    }

}
