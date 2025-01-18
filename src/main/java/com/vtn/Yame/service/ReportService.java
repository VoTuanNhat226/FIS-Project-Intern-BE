package com.vtn.Yame.service;

import com.vtn.Yame.entity.Product;
import com.vtn.Yame.repository.ProductRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<byte[]> exportReport(String reportFormat) throws FileNotFoundException, JRException, IOException {

        List<Product> products = productRepository.findAll();

        File file = ResourceUtils.getFile("classpath:ProductReport.jrxml");
        System.out.println(file.getAbsolutePath());

        if (!file.exists()) {
            throw new FileNotFoundException("Không tìm thấy template báo cáo Jasper: " + file.getAbsolutePath());
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(products);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "TuanNhat");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

//        if (reportFormat.equalsIgnoreCase("html")) {
//            JasperExportManager.exportReportToHtmlStream(jasperPrint, byteArrayOutputStream); }
        if (reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        } else if (reportFormat.equalsIgnoreCase("xlsx")) {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setOnePagePerSheet(false);
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(false);

            exporter.setConfiguration(configuration);
            exporter.exportReport();
        }

        HttpHeaders headers = new HttpHeaders();
        String contentDisposition = "attachment; filename=ProductReport." + reportFormat;
        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/" + reportFormat);

        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }
}

