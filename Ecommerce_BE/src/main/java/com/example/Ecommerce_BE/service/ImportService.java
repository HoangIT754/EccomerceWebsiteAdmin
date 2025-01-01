package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.entity.Brands;
import com.example.Ecommerce_BE.entity.SubCategories;
import com.example.Ecommerce_BE.repository.BrandsRepository;
import com.example.Ecommerce_BE.repository.SubCategoriesRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    @Autowired
    private BrandsRepository brandsRepository;

    @Autowired
    private SubCategoriesRepository subCategoriesRepository;

    private final List<DateTimeFormatter> formatters = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );

    @Transactional
    public void importExcel(MultipartFile file, String tableName) throws Exception {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Object> entities = new ArrayList<>();

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                switch (tableName) {
                    case "brand":
                        String brandName = getCellValueAsString(row.getCell(0));
                        if (brandsRepository.existsByName(brandName)) {
                            throw new IllegalArgumentException("Duplicate name found in row " + (i + 1) + ": " + brandName);
                        }

                        Brands brand = new Brands();
                        brand.setName(brandName);
                        brand.setDescription(getCellValueAsString(row.getCell(1)));
                        brand.setCreated_at(getCellValueAsLocalDateTime(row.getCell(2)));
                        brand.setUpdated_at(getCellValueAsLocalDateTime(row.getCell(3)));
                        entities.add(brand);
                        break;

                    case "subCategory":
                        SubCategories subCategory = new SubCategories();
                        subCategory.setName(getCellValueAsString(row.getCell(0)));
                        subCategory.setDescription(getCellValueAsString(row.getCell(1)));
                        entities.add(subCategory);
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported table: " + tableName);
                }
            }

            saveEntities(entities, tableName);
        } catch (Exception e) {
            throw new Exception("Failed to import data: " + e.getMessage(), e);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private LocalDateTime getCellValueAsLocalDateTime(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.STRING) {
            String cellValue = cell.getStringCellValue();
            for (DateTimeFormatter fmt : formatters) {
                try {
                    return LocalDateTime.parse(cellValue, fmt);
                } catch (Exception ignored) {
                    // Thử với formatter tiếp theo
                }
            }
            throw new IllegalArgumentException("Invalid date format in cell: " + cellValue);
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getLocalDateTimeCellValue();
        }
        return null;
    }

    private void saveEntities(List<Object> entities, String tableName) {
        switch (tableName) {
            case "brand":
                List<Brands> brands = entities.stream()
                        .filter(Brands.class::isInstance)
                        .map(Brands.class::cast)
                        .toList();
                brandsRepository.saveAll(brands);
                break;

            case "subCategory":
                List<SubCategories> subCategories = entities.stream()
                        .filter(SubCategories.class::isInstance)
                        .map(SubCategories.class::cast)
                        .toList();
                subCategoriesRepository.saveAll(subCategories);
                break;

            default:
                throw new IllegalArgumentException("Unsupported table: " + tableName);
        }
    }
}