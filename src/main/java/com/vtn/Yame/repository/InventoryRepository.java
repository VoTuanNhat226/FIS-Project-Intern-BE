package com.vtn.Yame.repository;

import com.vtn.Yame.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Tìm Inventory theo productId
    Optional<Inventory> findByProductId(Long productId);

//    @Query(value = "SELECT * FROM InventoryEntity WHERE product.id = :productId", nativeQuery = true)
//    Optional<InventoryEntity> findInventoryByProductId(@Param("productId") Long productId);
}
