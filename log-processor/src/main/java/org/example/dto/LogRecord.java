package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class LogRecord {

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?Z$", message = "Timestamp must be in ISO 8601 format")
    @JsonProperty("timestamp")
    private String timestamp;

    @NotNull(message = "Level must not be null")
    @JsonProperty("level")
    private String level;

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

    // Default constructor
    public LogRecord() {
    }

    // All-arguments constructor
    public LogRecord(String timestamp, String level, String message, String source, String thread, String logger,
                     String processedTimestamp, String category, Map<String, String> tags, String status) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.source = source;
        this.thread = thread;
        this.logger = logger;
        this.processedTimestamp = processedTimestamp;
        this.category = category;
        this.tags = tags;
        this.status = status;
    }

    // Getters and Setters
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getProcessedTimestamp() {
        return processedTimestamp;
    }

    public void setProcessedTimestamp(String processedTimestamp) {
        this.processedTimestamp = processedTimestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString for easy logging and debugging
    @Override
    public String toString() {
        return "LogRecord{" +
                "timestamp='" + timestamp + '\'' +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                ", source='" + source + '\'' +
                ", thread='" + thread + '\'' +
                ", logger='" + logger + '\'' +
                ", processedTimestamp='" + processedTimestamp + '\'' +
                ", category='" + category + '\'' +
                ", tags=" + tags +
                ", status='" + status + '\'' +
                '}';
    }
}
