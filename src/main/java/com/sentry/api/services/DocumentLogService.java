package com.sentry.api.services;

import com.sentry.api.repositories.DocumentRepository;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DocumentLogService {

    private final Logger logger = LogManager.getLogger(DocumentLogService.class);

    @Autowired
    private final DocumentRepository documentRepository;

    public DocumentLogService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void logDailyFileStatistics() {
        try {
            Long totalSize = documentRepository.getTotalFileSize();
            Long documentCount = documentRepository.count();
            logger.info("Daily file statistics - Total documents: {}, Total size: {} bytes", documentCount, totalSize);
        } catch (Exception e) {
            logger.error("Error logging daily file statistics", e);
        }
    }
}
