package org.example.service;

import org.example.dto.LogRecord;

public class LogService {

    public void processLog(LogRecord logRecord) {
        // Acessando os campos através dos getters
        String timestamp = logRecord.getTimestamp();
        String level = logRecord.getLevel();
        String message = logRecord.getMessage();
        String source = logRecord.getSource();
        String thread = logRecord.getThread();
        String logger = logRecord.getLogger();

        // Processar o log (Exemplo de uso)
        System.out.println("Processing log: " + logRecord.toString());

        // Aqui você poderia adicionar lógica adicional para manipulação dos logs
    }
}
