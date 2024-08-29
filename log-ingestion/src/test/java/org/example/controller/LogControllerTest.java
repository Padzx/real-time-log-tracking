package org.example.controller;

import org.example.dto.LogRecord;
import org.example.service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LogControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LogService logService;

    @Test
    void testReceiveJsonLog() {
        LogRecord logRecord = new LogRecord(
                "2024-08-28T12:34:56Z",
                "INFO",
                "This is a test log message",
                "TestSource",
                "main",
                "TestLogger"
        );

        String url = "http://localhost:" + port + "/api/logs/json";
        ResponseEntity<String> response = this.restTemplate.postForEntity(url, logRecord, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Log received in JSON format.");
    }

    @Test
    void testReceiveTextLog() {
        String logText = "This is a simple text log message";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(logText, headers);

        String url = "http://localhost:" + port + "/api/logs/text";
        ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Log received in Text format.");
    }

    @Test
    void testReceiveJsonLog_InvalidData() {
        LogRecord invalidLogRecord = new LogRecord(
                "",    // Invalid timestamp
                "INFO",
                "Log with invalid timestamp",
                "TestSource",
                "main",
                "TestLogger"
        );

        String url = "http://localhost:" + port + "/api/logs/json";
        ResponseEntity<String> response = this.restTemplate.postForEntity(url, invalidLogRecord, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Timestamp field is mandatory");
    }

    @Test
    void testReceiveTextLog_Empty() {
        String emptyLogText = "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(emptyLogText, headers);

        String url = "http://localhost:" + port + "/api/logs/text";
        ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
