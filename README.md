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

We can create a simple munin-node plugin for *nix systems with the following script

```bash
#!/bin/bash

URL='http://localhost:8081/munin-healthcheck'

case "$1" in
    suggest)
        // TODO auto configuring with suggest
        ;;
    config)
        curl "$URL?config"
        ;;
    *)
        curl "$URL"
        ;;
esac
```

Copy this script to your munin plugin directory something like `/opt/munin/plugins/api-health` or `/usr/share/munin/plugins/api-health`

Make it executable and enable it and restart the munin-node

```bash
chmod +x /opt/munin/plugins/api-health
ln -s '/usr/share/munin/plugins/api-health' '/etc/munin/plugins/api-health'
/etc/init.d/munin-node restart
```

