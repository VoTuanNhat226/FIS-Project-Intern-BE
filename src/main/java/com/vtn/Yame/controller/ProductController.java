package com.vtn.Yame.controller;

//import com.vtn.Yame.service.ProductProcessService;
import com.vtn.Yame.entity.Product;
import com.vtn.Yame.service.ProductService;
import com.vtn.Yame.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private  ProductService productService;

    @Autowired
    private ReportService reportService;

    //Xuất báo cáo bảng Product
    @GetMapping("/report/{format}")
    public ResponseEntity<byte[]> getReport(@PathVariable String format) {
        try {
            return reportService.exportReport(format);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    //Lấy toàn bộ sản phẩm
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    //Lấy 1 sản phẩm theo id
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/count-product")
    public Integer getCountProducts() {
        return productService.getCountProducts();
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    //Tìm sản phẩm theo tên
    @GetMapping("/search/{name}")
    public ResponseEntity<List<Product>> searchProduct(@PathVariable String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    //Tạo mới sản phẩm
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    //Update sản phẩm theo id
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }

    //Xóa sản phẩm theo id
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}

