package com.vtn.Yame.repository;

import com.vtn.Yame.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

//    @Query("SELECT p FROM ProductEntity p WHERE p.name LIKE %:name%")
//    List<ProductEntity> findByNameLike(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameLike(@Param("name") String name);


    List<Product> findByPriceGreaterThan(BigDecimal price);

    @Query("SELECT COUNT(id) FROM Product")
    Integer countProduct();

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
}

