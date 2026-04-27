package com.clothset.controller;

import com.clothset.entity.Category;
import com.clothset.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/top")
    public ResponseEntity<List<Category>> getTopCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<Category>> getSubCategories(@PathVariable Long parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        return ResponseEntity.ok(categories);
    }
}
