package com.vtn.Yame.service;

import com.vtn.Yame.entity.*;
import com.vtn.Yame.repository.InventoryRepository;
import com.vtn.Yame.repository.ProductRepository;
import com.vtn.Yame.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryByProductId(Long id) {
        return inventoryRepository.findByProductId(id);
    }

    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(Long id, Inventory inventory) {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hàng tồn kho"));

        existingInventory.setProduct(inventory.getProduct());
        existingInventory.setWarehouse(inventory.getWarehouse());
        existingInventory.setQuantity(inventory.getQuantity());

        return inventoryRepository.save(existingInventory);
    }

    @Transactional
    public void updateInventoryQuantity(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in inventory"));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}
