package com.github.mustard.metrics.munin;

class MuninMetricsUtil {

    static String sanitiseCheckName(String checkName) {
        return checkName.replaceAll(" ", "_");
    }

    static String sanitiseCheckKey(String checkKey) {
        return checkKey.replaceAll(" ", "_").toLowerCase();
    }

}
