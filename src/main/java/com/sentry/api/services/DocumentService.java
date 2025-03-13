package com.sentry.api.services;

import com.sentry.api.models.Document;
import com.sentry.api.repositories.DocumentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentService {

    private static final Logger logger = LogManager.getLogger(DocumentService.class);

    @Autowired
    private final DocumentRepository documentRepository;

    private final Path rootLocation = Paths.get("documents");

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document createDocument(String name, byte[] file) throws IOException {
        try {
            Files.createDirectories(rootLocation);
            logger.info("Directories for storing documents created successfully.");
        } catch (IOException e) {
            logger.error("Failed to create directories for storing documents", e);
            throw new IOException("Failed to create directories for storing documents", e);
        }

        String fileName = UUID.randomUUID() + "_" + name;
        Path filePath = rootLocation.resolve(fileName);

        try {
            Files.write(filePath, file);
            logger.info("File written to disk: {}", fileName);
        } catch (IOException e) {
            logger.error("Failed to write the file to disk", e);
            throw new IOException("Failed to write the file to disk", e);
        }

        Document document = new Document();
        document.setPath(filePath.toString());
        document.setSize((long) file.length);

        Document savedDocument = documentRepository.save(document);
        logger.info("Document created successfully with ID: {}", savedDocument.getId());
        return savedDocument;
    }

    public Document getDocument(Long id) throws FileNotFoundException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new FileNotFoundException("Document not found with ID " + id);
                });

        logger.info("Reading file for document ID: {}", id);
        return document;
    }

    public Document updateDocument(String originalFilename, Long id, byte[] newFile) throws IOException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new FileNotFoundException("Document not found with ID " + id);
                });

        Path oldFile = Paths.get(document.getPath());
        try {
            Files.delete(oldFile);
            logger.info("Old file deleted for document ID: {}", id);
        } catch (IOException e) {
            logger.error("Failed to delete the old file with ID: {}", id, e);
            throw new IOException("Failed to delete the old file", e);
        }

        String newFileName = UUID.randomUUID() + "_updated_" + originalFilename;
        Path newFilePath = rootLocation.resolve(newFileName);
        try {
            Files.write(newFilePath, newFile);
            logger.info("New file written for document ID: {}", id);
        } catch (IOException e) {
            logger.error("Failed to write the updated file for document ID: {}", id, e);
            throw new IOException("Failed to write the updated file", e);
        }

        document.setPath(newFilePath.toString());
        document.setSize((long) newFile.length);

        Document updatedDocument = documentRepository.save(document);
        logger.info("Document updated successfully with ID: {}", updatedDocument.getId());
        return updatedDocument;
    }

    public void deleteDocument(Long id) throws IOException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new FileNotFoundException("Document not found with ID " + id);
                });

        Path filePath = Paths.get(document.getPath());
        try {
            Files.delete(filePath);
            logger.info("Document deleted successfully with ID: {}", id);
        } catch (IOException e) {
            logger.error("Failed to delete the file with ID: {}", id, e);
            throw new IOException("Failed to delete the file", e);
        }

        documentRepository.delete(document);
        logger.info("Document deleted from database with ID: {}", id);
    }
}
