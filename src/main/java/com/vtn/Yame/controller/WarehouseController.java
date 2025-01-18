package com.vtn.Yame.controller;

import com.vtn.Yame.dto.UpdateWarehouseRequestDTO;
import com.vtn.Yame.entity.Warehouse;
import com.vtn.Yame.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    @Autowired
    private WarehouseService warehouseService;

    //Lấy toàn bộ nhà kho
    @GetMapping
    public List<Warehouse> getAllWarehouse() {
        return warehouseService.getAllWarehouses();
    }

    //Lấy 1 nhà kho theo id
    @GetMapping("/{id}")
    public Warehouse getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id);
    }

    //Tạo mới nhà kho
    @PostMapping
    public Warehouse createWarehouse(@RequestBody Warehouse warehouse) {
        return warehouseService.saveWarehouse(warehouse);
    }

    //Update nhà kho theo id
    @PutMapping("/{id}")
    public Warehouse updateWarehouse(@PathVariable Long id, @RequestBody UpdateWarehouseRequestDTO updatedWarehouse) {
        return warehouseService.updateWarehouse(id, updatedWarehouse);
    }

    //Xóa nhà kho theo id
    @DeleteMapping("/{id}")
    public void deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
    }
}
