package com.vtn.Yame.service;

import com.vtn.Yame.entity.Category;
import com.vtn.Yame.repository.CategoryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ImportService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CategoryRepository categoryRepository;

    public void importFromExcel(MultipartFile file, String entityName) throws Exception {

        Class<?> entityClass = Class.forName("com.vtn.Yame.entity." + entityName);
        List<Object> entityList = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new Exception("Header row is missing");
        }

        Map<String, Integer> columnIndexMap = new HashMap<>();

        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            String columnName = headerRow.getCell(i).getStringCellValue();
            columnIndexMap.put(columnName, i);
        }

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);

            if (row == null) {
                continue;
            }

            Object entityInstance = entityClass.getDeclaredConstructor().newInstance();

            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (columnIndexMap.containsKey(fieldName)) {
                    int columnIndex = columnIndexMap.get(fieldName);
                    Cell cell = row.getCell(columnIndex);
                    setFieldValue(entityInstance, field, cell);
                }
            }

            entityList.add(entityInstance);
        }

        workbook.close();

        String repositoryName = entityName.toLowerCase() + "Repository";
        Object repository = applicationContext.getBean(repositoryName);

        if (repository != null) {
            if (repository instanceof org.springframework.data.jpa.repository.JpaRepository) {
                org.springframework.data.jpa.repository.JpaRepository<Object, Long> jpaRepository =
                        (org.springframework.data.jpa.repository.JpaRepository<Object, Long>) repository;
                jpaRepository.saveAll(entityList);
            } else {
                throw new IllegalArgumentException("Repository không hợp lệ cho entity: " + entityName);
            }
        } else {
            throw new IllegalArgumentException("Không tìm thấy repository cho entity: " + entityName);
        }
    }

    private void setFieldValue(Object entityInstance, Field field, Cell cell) throws IllegalAccessException {
        if (cell == null) return;

        switch (cell.getCellType()) {
            case STRING:
                field.set(entityInstance, cell.getStringCellValue());
                break;
            case NUMERIC:
                if (field.getType().equals(Category.class)) {
                    // Giả sử bạn có một phương thức để tìm Category theo ID
                    Long categoryId = Math.round(cell.getNumericCellValue());
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found"));
                    field.set(entityInstance, category);
                } else if (field.getType().equals(BigDecimal.class)) {
                    field.set(entityInstance, BigDecimal.valueOf(cell.getNumericCellValue()));
                } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                    field.set(entityInstance, Math.round(cell.getNumericCellValue()));
                } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                    field.set(entityInstance, (int) cell.getNumericCellValue());
                } else {
                    field.set(entityInstance, cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                field.set(entityInstance, cell.getBooleanCellValue());
                break;
            default:
                break;
        }
    }
}
