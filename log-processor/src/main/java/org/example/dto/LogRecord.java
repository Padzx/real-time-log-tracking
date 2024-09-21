package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogRecord {

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?Z$", message = "Timestamp must be in ISO 8601 format")
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String timestamp;

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR, TRACE
    }

    @NotNull(message = "Level must not be null")
    @JsonProperty("level")
    private LogLevel level;

    @NotBlank(message = "Message must not be blank")
    @JsonProperty("message")
    private String message;

    @JsonProperty("source")
    private String source;

    @JsonProperty("thread")
    private String thread;

    @JsonProperty("logger")
    private String logger;

    @JsonProperty("processedTimestamp")
    private String processedTimestamp;

    @JsonProperty("category")
    private String category;

    @JsonProperty("tags")
    private Map<String, String> tags;

    @JsonProperty("status")
    private String status;


    public LogRecord() {
    }


    private LogRecord(Builder builder) {
        this.timestamp = builder.timestamp;
        this.level = builder.level;
        this.message = builder.message;
        this.source = builder.source;
        this.thread = builder.thread;
        this.logger = builder.logger;
        this.processedTimestamp = builder.processedTimestamp;
        this.category = builder.category;
        this.tags = builder.tags;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getTimestamp() {
        return timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source;
    }

    public String getThread() {
        return thread;
    }

    public String getLogger() {
        return logger;
    }

    public String getProcessedTimestamp() {
        return processedTimestamp;
    }

    public String getCategory() {
        return category;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public String getStatus() {
        return status;
    }

    public static class Builder {
        private String timestamp;
        private LogLevel level;
        private String message;
        private String source;
        private String thread;
        private String logger;
        private String processedTimestamp;
        private String category;
        private Map<String, String> tags;
        private String status;

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder thread(String thread) {
            this.thread = thread;
            return this;
        }

        public Builder logger(String logger) {
            this.logger = logger;
            return this;
        }

        public Builder processedTimestamp(String processedTimestamp) {
            this.processedTimestamp = processedTimestamp;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder tags(Map<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public LogRecord build() {
            return new LogRecord(this);
        }
    }
}
