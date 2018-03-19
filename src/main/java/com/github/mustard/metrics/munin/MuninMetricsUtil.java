package com.github.mustard.metrics.munin;

class MuninMetricsUtil {

    static String sanitiseName(String name) {
        return name.replaceAll(" ", "_");
    }

    static String sanitiseKey(String key) {
        return key.replaceAll(" ", "_").toLowerCase();
    }

}
