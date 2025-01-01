package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.entity.Brands;
import com.example.Ecommerce_BE.entity.SubCategories;
import com.example.Ecommerce_BE.repository.*;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JasperReportService {
    private static final Logger log = LoggerFactory.getLogger(JasperReportService.class);

    @Autowired
    private BrandsRepository brandRepository;

    @Autowired
    private ProductsRepository productRepository;

    @Autowired
    private CategoriesRepository categoryRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SubCategoriesRepository subCategoriesRepository;

    private List<?> getDataByReportName(String reportName) {
        return switch (reportName) {
            case "brand" -> brandRepository.findAll();
            case "product" -> productRepository.findAll();
            case "category" -> categoryRepository.findAll();
            case "user" -> usersRepository.findAll();
            case "subCategory" -> subCategoriesRepository.findAll();
            default -> throw new IllegalArgumentException("Unknown report name: " + reportName);
        };
    }

    private JasperPrint prepareJasperPrint(String reportName) throws Exception {
        List<?> data = getDataByReportName(reportName);

        log.info("Data size for report '{}': {}", reportName, data.size());

//        for (Object item : data) {
//            if (item instanceof SubCategories) {
//                SubCategories subCategory = (SubCategories) item;
//                log.info("SubCategory - ID: {}, Name: {}, Description: {}, Category Name: {}",
//                        subCategory.getSub_category_id(),
//                        subCategory.getName(),
//                        subCategory.getDescription(),
//                        subCategory.getCategory() != null ? subCategory.getCategory().getName() : "No Category");
//            }
//        }

        if (data.isEmpty()) {
            log.warn("No data found for report '{}'", reportName);
            throw new IllegalArgumentException("No data available for report: " + reportName);
        }

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        String reportPath = "reports/" + reportName + "_report.jasper";
        log.info("Loading Jasper report from path: " + reportPath);

        try (InputStream reportStream = new ClassPathResource(reportPath).getInputStream()) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CreatedBy", "HoangTruong");
            log.info("Filling Jasper report for: " + reportName);
            return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (FileNotFoundException e) {
            log.error("Jasper report file not found at path: " + reportPath, e);
            throw new FileNotFoundException("Jasper file not found for report: " + reportName);
        } catch (Exception e) {
            log.error("Error filling Jasper report for: " + reportName, e);
            throw e;
        }
    }

    public byte[] generateReportAsPDF(String reportName) throws Exception {
        try {
            JasperPrint jasperPrint = prepareJasperPrint(reportName);
            log.info("Exporting Jasper report to PDF for: " + reportName);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            log.error("Error generating PDF report for: " + reportName, e);
            throw e;
        }
    }

    public byte[] generateReportAsXLSX(String reportName) throws Exception {
        try {
            JasperPrint jasperPrint = prepareJasperPrint(reportName);
            ByteArrayOutputStream xlsxOutputStream = new ByteArrayOutputStream();

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsxOutputStream));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setOnePagePerSheet(false);
            configuration.setDetectCellType(true);
            exporter.setConfiguration(configuration);

            exporter.exportReport();
            return xlsxOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating XLSX report for: " + reportName, e);
            throw e;
        }
    }

    public byte[] generateReportAsDOCX(String reportName) throws Exception {
        try {
            JasperPrint jasperPrint = prepareJasperPrint(reportName);
            ByteArrayOutputStream docxOutputStream = new ByteArrayOutputStream();

            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(docxOutputStream));

            exporter.exportReport();
            return docxOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating DOCX report for: " + reportName, e);
            throw e;
        }
    }
}
