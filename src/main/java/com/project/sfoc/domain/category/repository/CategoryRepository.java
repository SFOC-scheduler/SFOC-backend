package com.project.sfoc.domain.category.repository;

import com.project.sfoc.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
