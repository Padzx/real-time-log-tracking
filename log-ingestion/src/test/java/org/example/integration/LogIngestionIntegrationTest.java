package org.example.integration;

import org.example.annotations.IntegrationTest;
import org.example.dto.LogRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.time.Duration;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogIngestionIntegrationTest {

    private static final Logger logger = Logger.getLogger(LogIngestionIntegrationTest.class.getName());
    private static final Network network = Network.newNetwork();

    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
            .withExposedPorts(9092)
            .waitingFor(new LogMessageWaitStrategy()
                    .withRegEx(".*\\[KafkaServer id=\\d+] started.*\\n")
                    .withStartupTimeout(Duration.ofMinutes(5)));

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void setup() {
        String useDockerNetwork = System.getenv("USE_DOCKER_NETWORK");
        if ("true".equals(useDockerNetwork)) {
            System.setProperty("testcontainers.useDockerForNetwork", "true");
            System.setProperty("testcontainers.reuse.enable", "true");
            System.setProperty("testcontainers.network.name", "ci-network");
        }

        try {
            kafkaContainer.start();
            String bootstrapServers = kafkaContainer.getBootstrapServers();
            System.setProperty("spring.kafka.bootstrap-servers", bootstrapServers);
            logger.info("Kafka started with bootstrap servers: " + bootstrapServers);
        } catch (Exception e) {
            logger.severe("Failed to start Kafka container: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void teardown() {
        kafkaContainer.stop();
        logger.info("Kafka container stopped.");
    }

    @Test
    void testMicroserviceWithKafkaReceiveJsonLog() {
        LogRecord logRecord = new LogRecord(
                "2024-08-28T12:34:56Z",
                "INFO",
                "This is a test log message",
                "TestSource",
                "main",
                "TestLogger"
        );

        String url = String.format("https://%s:%d/api/logs/json", getHost(), port);
        ResponseEntity<String> response = this.restTemplate.postForEntity(url, logRecord, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Log received in JSON format.");
        logger.info("Successfully tested JSON log ingestion through the microservice.");
    }

    @Test
    void testMicroserviceWithKafkaReceiveTextLog() {
        String logText = "This is a simple text log message";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(logText, headers);

        String url = String.format("https://%s:%d/api/logs/text", getHost(), port);
        ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Log received in Text format.");
        logger.info("Successfully tested Text log ingestion through the microservice.");
    }

    private String getHost() {
        // Use "host.docker.internal" when running in a CI Docker environment, "localhost" otherwise
        String useDockerNetwork = System.getenv("USE_DOCKER_NETWORK");
        return "true".equals(useDockerNetwork) ? "host.docker.internal" : "localhost";
    }
}
