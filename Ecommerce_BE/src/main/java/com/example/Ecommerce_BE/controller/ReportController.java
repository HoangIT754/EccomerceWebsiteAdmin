package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.ImportService;
import com.example.Ecommerce_BE.service.JasperReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final JasperReportService reportService;
    private final ImportService importService;
    private final AuthService authService;

    @Autowired
    public ReportController(JasperReportService reportService, ImportService importService, AuthService authService) {
        this.reportService = reportService;
        this.importService = importService;
        this.authService = authService;
    }

    private ResponseEntity<?> checkAdminAuthorization(HttpServletRequest request) {
        String token = authService.extractTokenFromCookie(request);
        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token.");
        }
        List<String> roles = authService.getUserRoles(token);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(403).body("Access denied: Admin privileges required.");
        }
        return null;
    }

    @GetMapping("export/{reportName}/pdf")
    public ResponseEntity<?> exportReportAsPDF(@PathVariable String reportName, HttpServletRequest request) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(request);
        if (authResponse != null) return authResponse;

        try {
            byte[] data = reportService.generateReportAsPDF(reportName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", reportName + "_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("Error generating PDF report for: " + reportName, e);
            return ResponseEntity.internalServerError().body("Failed to generate PDF report.");
        }
    }

    @GetMapping("export/{reportName}/xlsx")
    public ResponseEntity<?> exportReportAsXLSX(@PathVariable String reportName, HttpServletRequest request) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(request);
        if (authResponse != null) return authResponse;

        try {
            byte[] data = reportService.generateReportAsXLSX(reportName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", reportName + "_report.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("Error generating XLSX report for: " + reportName, e);
            return ResponseEntity.internalServerError().body("Failed to generate XLSX report.");
        }
    }

    @GetMapping("export/{reportName}/docx")
    public ResponseEntity<?> exportReportAsDOCX(@PathVariable String reportName, HttpServletRequest request) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(request);
        if (authResponse != null) return authResponse;

        try {
            byte[] data = reportService.generateReportAsDOCX(reportName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", reportName + "_report.docx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("Error generating DOCX report for: " + reportName, e);
            return ResponseEntity.internalServerError().body("Failed to generate DOCX report.");
        }
    }

    @PostMapping("/import/{tableName}")
    public ResponseEntity<?> importData(@RequestParam("file") MultipartFile file, @PathVariable String tableName, HttpServletRequest request) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(request);
        if (authResponse != null) return authResponse;

        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body("Invalid file. Please upload a valid Excel file.");
        }
        try {
            importService.importExcel(file, tableName);
            return ResponseEntity.ok("Data imported successfully into table: " + tableName);
        } catch (IllegalArgumentException e) {
            log.error("Error during import process: Invalid arguments", e);
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error importing data into table: " + tableName, e);
            return ResponseEntity.internalServerError().body("Error importing data: " + e.getMessage());
        }
    }
}