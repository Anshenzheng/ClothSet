package com.clothset.controller;

import com.clothset.dto.OutfitDTO;
import com.clothset.service.OutfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/outfits")
@CrossOrigin(origins = "http://localhost:4200")
public class OutfitController {
    
    @Autowired
    private OutfitService outfitService;
    
    @GetMapping
    public ResponseEntity<List<OutfitDTO>> getAllOutfits() {
        List<OutfitDTO> outfits = outfitService.getAllOutfits();
        return ResponseEntity.ok(outfits);
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<List<OutfitDTO>> getFavoriteOutfits() {
        List<OutfitDTO> outfits = outfitService.getFavoriteOutfits();
        return ResponseEntity.ok(outfits);
    }
    
    @PostMapping("/random")
    public ResponseEntity<OutfitDTO> generateRandomOutfit() {
        OutfitDTO outfit = outfitService.generateRandomOutfit();
        return ResponseEntity.ok(outfit);
    }
    
    @PostMapping
    public ResponseEntity<OutfitDTO> saveOutfit(@RequestBody OutfitDTO outfitDTO) {
        OutfitDTO saved = outfitService.saveOutfit(outfitDTO);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OutfitDTO> updateOutfit(@PathVariable Long id, @RequestBody OutfitDTO outfitDTO) {
        OutfitDTO updated = outfitService.updateOutfit(id, outfitDTO);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOutfit(@PathVariable Long id) {
        outfitService.deleteOutfit(id);
        return ResponseEntity.ok().build();
    }
}
