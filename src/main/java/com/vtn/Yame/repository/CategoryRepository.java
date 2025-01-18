package com.vtn.Yame.repository;

import com.vtn.Yame.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByName(String name);

    List<Category> findByDescriptionContaining(String keyword);

    @Query("SELECT COUNT(id) FROM Category ")
    Integer countCategory();
}
