package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.annotations.UnitTest;
import org.example.service.LogProcessingService;
import org.example.repository.LogProcessingRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@UnitTest
@Tag("unit")
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class LogProcessorApplicationTest {

    @Autowired
    private LogProcessingService logProcessingService;

    @MockBean
    private LogProcessingRepository logProcessingRepository;

    @Test
    void contextLoads() {
        assertThat(logProcessingService).isNotNull();
    }
}
