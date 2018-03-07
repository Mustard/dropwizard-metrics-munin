# Dropwizard Metrics Munin

[![Build Status](https://travis-ci.org/Mustard/dropwizard-metrics-munin.svg?branch=master)](https://travis-ci.org/Mustard/dropwizard-metrics-munin)

[Munin](http://munin-monitoring.org/) Adapter for [Dropwizard Metrics](TODO)

This repository consists of two servlets one to expose Metrics and another To expose Health checks in a plain text format that can be collected by a munin-node plugin

# Setup

Add one or both of the `MuninHealthCheckServlet` or `MuninMetricsServlet` to your projects admin environment

```java
@Override
public void run(AppConfig config, Environment env) throws Exception {
    env.admin()
        .addServlet("MuninHealthCheck", new MuninHealthCheckServlet(env.healthChecks()))
        .addMapping("/munin-healthcheck");
}
```

Now making a request to `/munin-healthcheck?config` will return the munin node configuration and `/munin-healthcheck` will return the health check data

We can create a simple munin-node plugin for *nix systems with 

```bash

```


## Health

`MuninHealthCheck`

## Metrics

The `MuninMetricsServlet` 

