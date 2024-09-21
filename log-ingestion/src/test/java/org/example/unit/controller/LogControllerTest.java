package org.example.unit.controller;

import org.example.annotations.UnitTest;
import org.example.controller.LogController;
import org.example.dto.LogRecord;
import org.example.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnitTest
@Tag("unit")
@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogService logService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doNothing().when(logService).processLog(any(LogRecord.class));
    }

    @Test
    void testReceiveJsonLog() throws Exception {
        String jsonLog = """
                {
                    "timestamp": "2024-08-28T12:34:56Z",
                    "level": "INFO",
                    "message": "This is a test log message",
                    "source": "TestSource",
                    "thread": "main",
                    "logger": "TestLogger"
                }
                """;

        mockMvc.perform(post("/api/logs/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLog))
                .andExpect(status().isOk())
                .andExpect(content().string("Log received in JSON format."));
    }

    @Test
    void testReceiveTextLog() throws Exception {
        String logText = "This is a simple text log message";

        mockMvc.perform(post("/api/logs/text")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(logText))
                .andExpect(status().isOk())
                .andExpect(content().string("Log received in Text format."));
    }

    @Test
    void testReceiveTextLog_Empty() throws Exception {
        String emptyLogText = "";

        mockMvc.perform(post("/api/logs/text")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(emptyLogText))
                .andExpect(status().isBadRequest());
    }
}
