package org.example.service;

import org.example.dto.LogRecord;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    public void processLog(LogRecord logRecord) {

        String timestamp = logRecord.getTimestamp();
        LogRecord.LogLevel level = logRecord.getLevel();
        String message = logRecord.getMessage();
        String source = logRecord.getSource();
        String thread = logRecord.getThread();
        String logger = logRecord.getLogger();

        // Example of log processing
        if (level == LogRecord.LogLevel.ERROR) {
            System.err.println("Error log: " + message + " from " + logger + " at " + timestamp);
        } else {
            System.out.println("Processing log: " + message + " from " + logger + " at " + timestamp);
        }

        // Additional log handling logic can be added here
    }
}
