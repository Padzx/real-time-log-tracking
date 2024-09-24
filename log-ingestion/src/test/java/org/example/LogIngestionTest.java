package org.example;

import org.example.annotations.UnitTest;
import org.example.controller.LogController;
import org.example.service.LogService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@Tag("unit")
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
