package com.vtn.Yame.service;

import com.vtn.Yame.dto.UpdateWarehouseRequestDTO;
import com.vtn.Yame.entity.Warehouse;
import com.vtn.Yame.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id).orElseThrow(() -> new RuntimeException("Warehouse not found"));
    }

//    public List<WarehouseEntity> searchProductsByName(String name) {
//        return warehouseRepository.findByNameLike(name);
//    }

    public Warehouse saveWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouse(Long id, UpdateWarehouseRequestDTO updatedWarehouse) {
        Warehouse existingWarehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        existingWarehouse.setName(updatedWarehouse.getName());
        existingWarehouse.setAddress(updatedWarehouse.getAddress());
        existingWarehouse.setPhone(updatedWarehouse.getPhone());
        existingWarehouse.setCapacity(updatedWarehouse.getCapacity());

        return warehouseRepository.save(existingWarehouse);
    }

    public void deleteWarehouse(Long id) {
        warehouseRepository.deleteById(id);
    }
}
