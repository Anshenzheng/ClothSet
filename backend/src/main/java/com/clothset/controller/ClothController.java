package com.clothset.controller;

import com.clothset.dto.ClothDTO;
import com.clothset.service.ClothService;
import com.clothset.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/clothes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClothController {
    
    @Autowired
    private ClothService clothService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping
    public ResponseEntity<List<ClothDTO>> getAllClothes(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long seasonId) {
        List<ClothDTO> clothes;
        if (categoryId != null) {
            clothes = clothService.getClothesByCategory(categoryId);
        } else if (seasonId != null) {
            clothes = clothService.getClothesBySeason(seasonId);
        } else {
            clothes = clothService.getAllClothes();
        }
        return ResponseEntity.ok(clothes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClothDTO> getClothById(@PathVariable Long id) {
        ClothDTO cloth = clothService.getClothById(id);
        return ResponseEntity.ok(cloth);
    }
    
    @PostMapping
    public ResponseEntity<ClothDTO> createCloth(@RequestBody ClothDTO clothDTO) {
        ClothDTO created = clothService.createCloth(clothDTO);
        return ResponseEntity.ok(created);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = fileStorageService.storeFile(file);
        return ResponseEntity.ok(imageUrl);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClothDTO> updateCloth(@PathVariable Long id, @RequestBody ClothDTO clothDTO) {
        ClothDTO updated = clothService.updateCloth(id, clothDTO);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCloth(@PathVariable Long id) {
        clothService.deleteCloth(id);
        return ResponseEntity.ok().build();
    }
}
