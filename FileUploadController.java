package com.example.ecommerce.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * File Upload Controller
 * ❌ VULNERABLE: Apache Commons FileUpload 1.3.1 (CVE-2016-1000031)
 */
public class FileUploadController {
    
    private static final Logger logger = LogManager.getLogger(FileUploadController.class);
    
    // ❌ VULNERABLE: Keine Limits auf Dateigröße - DoS möglich!
    public void handleFileUpload(HttpServletRequest request) {
        try {
            // Vulnerable FileUpload Version ohne Sicherheitskontrollen
            DiskFileItemFactory factory = new DiskFileItemFactory();
            
            // ❌ Kein Limit für Speichernutzung
            factory.setSizeThreshold(Integer.MAX_VALUE);
            
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            // ❌ CRITICAL: Keine maximale Dateigröße gesetzt!
            // Ein Angreifer kann beliebig große Dateien hochladen
            // upload.setFileSizeMax() fehlt komplett
            
            List<FileItem> items = upload.parseRequest(request);
            
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    String fileName = item.getName();
                    
                    // ❌ VULNERABLE: User input direkt geloggt (Log4Shell)
                    logger.info("Uploading file: " + fileName);
                    
                    // ❌ Path Traversal möglich - keine Validierung!
                    File uploadedFile = new File("/uploads/" + fileName);
                    item.write(uploadedFile);
                    
                    logger.info("File saved to: " + uploadedFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            // ❌ Sensitive information in logs
            logger.error("Upload failed: " + e.getMessage(), e);
        }
    }
    
    // ❌ Keine Content-Type Validierung
    // ❌ Keine Virus-Scanning
    // ❌ Keine Dateiendungs-Validierung
}
