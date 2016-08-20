package com.github.mustard.metrics.munin.example;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.github.mustard.metrics.munin.MuninHealthCheckServlet;
import com.github.mustard.metrics.munin.MuninMetricsServlet;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class ExampleApp extends Application<ExampleConfig> {

    public static void main(String[] args) throws Exception {
        new ExampleApp().run(args);
    }

    @Override
    public void run(ExampleConfig conf, Environment env) throws Exception {

        env.admin().addServlet("munin-health", new MuninHealthCheckServlet(env.healthChecks(), env.getHealthCheckExecutorService()))
                .addMapping("/muninHealthCheck");

        env.admin().addServlet("munin-metrics", new MuninMetricsServlet(env.metrics()))
                .addMapping("/muninMetrics");

        addHealthChecks(env.healthChecks());
    }

    private void addHealthChecks(HealthCheckRegistry registry) {
        registry.register("DB", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });

        registry.register("Flux Capacitor", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy("It works! It works");
            }
        });
    }

}
