package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.service.LogProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class LogProcessorApplicationTest {

    @Autowired
    private LogProcessingService logProcessingService;

    @Test
    void contextLoads() {
        assertThat(logProcessingService).isNotNull();
    }
}
