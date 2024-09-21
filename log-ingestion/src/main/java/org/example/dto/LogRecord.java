package org.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LogRecord(
        @NotNull @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?Z$", message = "Timestamp must be in ISO 8601 format")
        @JsonProperty("timestamp") String timestamp,

        @NotNull
        @JsonProperty("level") String level,

        @NotNull
        @JsonProperty("message") String message,

        @JsonProperty("source") String source,
        @JsonProperty("thread") String thread,
        @JsonProperty("logger") String logger
) {}
