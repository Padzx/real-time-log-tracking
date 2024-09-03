package org.example.unit;

import org.example.controller.LogController;
import org.example.service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LogIngestionTest {

    @Autowired
    private LogController logController;

    @Autowired
    private LogService logService;

    @Test
    void contextLoads() {

        assertThat(logController).isNotNull();
        assertThat(logService).isNotNull();
    }
}
