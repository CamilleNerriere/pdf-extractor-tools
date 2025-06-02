package com.noesis.pdf_extractor_tools.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.noesis.pdf_extractor_tools.core.common.ExportFormats;
import com.noesis.pdf_extractor_tools.core.common.ExportedFile;
import com.noesis.pdf_extractor_tools.model.ExtractionDataRequest;
import com.noesis.pdf_extractor_tools.service.functional.ExtractorFunction;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class GenericExtractionService {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationsService.class);

    public void extractAndSendFiles(ExtractionDataRequest request, HttpServletResponse response,
            ExtractorFunction extractor) throws Exception {
        String title = request.getTitle();
        List<ExportFormats> formats = normalizeFormats(request.getFormats());

        try (InputStream pdf = convertToInputStream(request.getPdf())) {

            if (pdf == null) {
                logger.error("Unable to read PDF file.");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File not found");
                return;
            }

            if (formats.isEmpty()) {
                logger.warn("No valid formats provided.");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid formats requested");
                return;
            }

            List<ExportedFile> files = extractor.extract(pdf, formats, title);

            if (files.isEmpty()) {
                logger.warn("No export files created.");
                response.sendError(HttpServletResponse.SC_NO_CONTENT, "No data found");
                return;
            }

            String filename = (title != null && !title.trim().isEmpty())
                    ? sanitizeFilename(title) + "_export.zip"
                    : "export.zip";

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setCharacterEncoding("UTF-8");

            try (OutputStream out = response.getOutputStream()) {
                zipFiles(files, out);
                out.flush();
            }

        } catch (Exception e) {
            logger.error("Error during extraction", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during file treatment");
            }
            throw e;
        }
    }

    private void zipFiles(List<ExportedFile> files, OutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            byte[] buffer = new byte[8192];

            for (ExportedFile file : files) {
                try (InputStream inputStream = file.openStream()) {
                    String entryName = sanitizeFilename(file.fileName());
                    ZipEntry entry = new ZipEntry(entryName);

                    zipOutputStream.putNextEntry(entry);

                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }

                    zipOutputStream.closeEntry();

                } catch (IOException e) {
                    logger.error("Unable to add file {} to ZIP", file.fileName(), e);
                }
            }
        } finally {
            cleanupTempFiles(files);
        }
    }

    private void cleanupTempFiles(List<ExportedFile> files) {
        for (ExportedFile file : files) {
            try {
                file.deleteTempFile();
            } catch (IOException e) {
                logger.warn("Unable to remove tempFile {}", file.fileName(), e);
            }
        }
    }

    private List<ExportFormats> normalizeFormats(String[] formats) {

        List<ExportFormats> normalizedFormats = new ArrayList<>();

        if (formats == null || formats.length == 0) {
            logger.warn("No format specification");
            return normalizedFormats;
        }

        for (String format : formats) {
            Optional<ExportFormats> op = ExportFormats.fromString(format);

            if (op.isPresent()) {
                normalizedFormats.add(op.get());
            } else {
                logger.warn("Unknown format : {}", format);
            }

        }

        return normalizedFormats;
    }

    private InputStream convertToInputStream(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            logger.error("Null or Empty file");
            return null;
        }

        try {
            return file.getInputStream();
        } catch (IOException e) {
            logger.error("Error during file to InputStream Conversion");
            return null;
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "fichier";
        }

        return filename.replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_{2,}", "_")
                .trim();
    }

}
