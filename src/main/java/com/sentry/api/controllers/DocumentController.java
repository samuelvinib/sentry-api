package com.sentry.api.controllers;

import com.sentry.api.config.ErrorResponse;
import com.sentry.api.models.Document;
import com.sentry.api.services.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<Object> createDocument(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (file.isEmpty()) {
            logger.warn("Attempted to upload an empty file.");
            ErrorResponse errorResponse = new ErrorResponse("Bad Request", "File is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            logger.info("Attempting to create document with file: {}", originalFilename);
            Document document = documentService.createDocument(originalFilename, file.getBytes());
            logger.info("Document created successfully with ID: {}", document.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (IOException e) {
            logger.error("Error creating document: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDocument(@PathVariable Long id) {
        try {
            logger.info("Fetching document with ID: {}", id);
            Document file = documentService.getDocument(id);
            logger.info("Document retrieved successfully with ID: {}", id);
            return ResponseEntity.ok().body(file);
        } catch (FileNotFoundException e) {
            logger.error("Document not found with ID: {}", id);
            ErrorResponse errorResponse = new ErrorResponse("Not Found", "Document not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDocument(@PathVariable Long id,
                                                 @RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (file.isEmpty()) {
            logger.warn("Attempted to update document with an empty file.");
            ErrorResponse errorResponse = new ErrorResponse("Bad Request", "File is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            logger.info("Attempting to update document with ID: {}", id);
            Document document = documentService.updateDocument(originalFilename, id, file.getBytes());
            logger.info("Document updated successfully with ID: {}", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (FileNotFoundException e) {
            logger.error("Document not found with ID: {}", id);
            ErrorResponse errorResponse = new ErrorResponse("Not Found", "Document not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IOException e) {
            logger.error("Error updating document with ID: {} - {}", id, e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDocument(@PathVariable Long id) {
        try {
            logger.info("Attempting to delete document with ID: {}", id);
            documentService.deleteDocument(id);
            logger.info("Document deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (FileNotFoundException e) {
            logger.error("Document not found with ID: {}", id);
            ErrorResponse errorResponse = new ErrorResponse("Not Found", "Document not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IOException e) {
            logger.error("Error deleting document with ID: {} - {}", id, e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
