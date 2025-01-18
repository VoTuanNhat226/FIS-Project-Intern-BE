package com.vtn.Yame.controller;

import com.vtn.Yame.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping("/import-excel")
    public String importData(@RequestParam("file") MultipartFile file, @RequestParam("entityName") String entityName) {
        try {
            importService.importFromExcel(file, entityName);
            return "Import thành công!";
        } catch (Exception e) {
            return "Lỗi khi import dữ liệu: " + e.getMessage();
        }
    }
}
