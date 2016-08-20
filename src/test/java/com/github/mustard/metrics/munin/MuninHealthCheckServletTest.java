package com.github.mustard.metrics.munin;

import com.github.mustard.metrics.munin.example.ExampleApp;
import com.github.mustard.metrics.munin.example.ExampleConfig;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;

public class MuninHealthCheckServletTest {

    @ClassRule
    public static final DropwizardAppRule<ExampleConfig> RULE =
            new DropwizardAppRule<>(ExampleApp.class, resourceFilePath("config.yml"));

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = new JerseyClientBuilder(RULE.getEnvironment()).build("test-client");
    }

    private String targetAdmin(String path) {
        return "http://localhost:" + RULE.getAdminPort() + path;
    }

    @Test
    public void canGetHealthChecksConfig() {
        String actual = client.target(targetAdmin("/muninHealthCheck"))
                .queryParam("config", true)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get(String.class);

        assertThat(actual).isNotEmpty();
        List<String> actualLines = Arrays.asList(StringUtils.split(actual, '\n'));

        assertThat(actualLines.get(0)).isEqualTo("graph_title Health Checks");
        assertThat(actualLines.get(1)).isEqualTo("graph_vlabel Success");
        assertThat(actualLines.get(2)).isEqualTo("graph_category health");
        assertThat(actualLines.get(3)).isEqualTo("graph_info Application Health Checks");

        assertThat(actualLines.subList(4, actualLines.size()))
                .containsOnly(
                        "deadlocks.label deadlocks",
                        "deadlocks.draw AREASTACK",
                        "deadlocks.critical 0",
                        "db.label DB",
                        "db.draw AREASTACK",
                        "db.critical 0",
                        "flux_capacitor.label Flux_Capacitor",
                        "flux_capacitor.draw AREASTACK",
                        "flux_capacitor.critical 0"
                );
    }


    @Test
    public void canGetHealthCheckResults() {
        String actual = client.target(targetAdmin("/muninHealthCheck"))
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get(String.class);

        assertThat(actual).isNotEmpty();
        List<String> actualLines = Arrays.asList(StringUtils.split(actual, '\n'));

        assertThat(actualLines)
                .containsOnly(
                        "db.value 1",
                        "flux_capacitor.value 1",
                        "deadlocks.value 1"
                );
    }

}
