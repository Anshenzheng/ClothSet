package com.clothset.service;

import com.clothset.dto.OutfitDTO;
import com.clothset.entity.Cloth;
import com.clothset.entity.Outfit;
import com.clothset.entity.User;
import com.clothset.repository.ClothRepository;
import com.clothset.repository.OutfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OutfitService {
    
    @Autowired
    private OutfitRepository outfitRepository;
    
    @Autowired
    private ClothRepository clothRepository;
    
    @Autowired
    private AuthService authService;
    
    public List<OutfitDTO> getAllOutfits() {
        User user = authService.getCurrentUser();
        return outfitRepository.findByUserOrderByCreatedAtDesc(user).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<OutfitDTO> getFavoriteOutfits() {
        User user = authService.getCurrentUser();
        return outfitRepository.findByUserAndIsFavoriteTrueOrderByCreatedAtDesc(user).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public OutfitDTO generateRandomOutfit() {
        User user = authService.getCurrentUser();
        
        List<Long> topCategoryIds = Arrays.asList(7L, 8L, 9L, 10L);
        List<Long> bottomCategoryIds = Arrays.asList(11L, 12L, 13L, 14L, 15L, 16L);
        List<Long> outerCategoryIds = Arrays.asList(17L, 18L, 19L, 20L, 21L);
        List<Long> shoeCategoryIds = Arrays.asList(22L, 23L, 24L, 25L);
        
        Set<Cloth> selectedClothes = new HashSet<>();
        
        pickRandomFromCategories(user, topCategoryIds).ifPresent(selectedClothes::add);
        pickRandomFromCategories(user, bottomCategoryIds).ifPresent(selectedClothes::add);
        pickRandomFromCategories(user, outerCategoryIds).ifPresent(selectedClothes::add);
        pickRandomFromCategories(user, shoeCategoryIds).ifPresent(selectedClothes::add);
        
        if (selectedClothes.isEmpty()) {
            throw new RuntimeException("No clothes available to generate outfit");
        }
        
        Outfit outfit = new Outfit();
        outfit.setUser(user);
        outfit.setName("随机穿搭 " + new Date());
        outfit.setClothes(selectedClothes);
        outfit.setIsFavorite(false);
        
        Outfit saved = outfitRepository.save(outfit);
        return toDTO(saved);
    }
    
    private Optional<Cloth> pickRandomFromCategories(User user, List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            Optional<Cloth> cloth = clothRepository.findRandomByUserAndCategory(user, categoryId);
            if (cloth.isPresent()) {
                return cloth;
            }
        }
        return Optional.empty();
    }
    
    @Transactional
    public OutfitDTO saveOutfit(OutfitDTO dto) {
        User user = authService.getCurrentUser();
        
        Outfit outfit = new Outfit();
        outfit.setUser(user);
        outfit.setName(dto.getName());
        outfit.setDescription(dto.getDescription());
        outfit.setImageUrl(dto.getImageUrl());
        outfit.setOccasion(dto.getOccasion());
        outfit.setIsFavorite(dto.getIsFavorite() != null ? dto.getIsFavorite() : false);
        
        if (dto.getClothIds() != null && !dto.getClothIds().isEmpty()) {
            Set<Cloth> clothes = new HashSet<>(clothRepository.findAllById(dto.getClothIds()));
            outfit.setClothes(clothes);
        }
        
        Outfit saved = outfitRepository.save(outfit);
        return toDTO(saved);
    }
    
    @Transactional
    public OutfitDTO updateOutfit(Long id, OutfitDTO dto) {
        User user = authService.getCurrentUser();
        Outfit outfit = outfitRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Outfit not found"));
        
        if (!outfit.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        outfit.setName(dto.getName());
        outfit.setDescription(dto.getDescription());
        outfit.setImageUrl(dto.getImageUrl());
        outfit.setOccasion(dto.getOccasion());
        if (dto.getIsFavorite() != null) {
            outfit.setIsFavorite(dto.getIsFavorite());
        }
        
        if (dto.getClothIds() != null && !dto.getClothIds().isEmpty()) {
            Set<Cloth> clothes = new HashSet<>(clothRepository.findAllById(dto.getClothIds()));
            outfit.setClothes(clothes);
        }
        
        Outfit saved = outfitRepository.save(outfit);
        return toDTO(saved);
    }
    
    @Transactional
    public void deleteOutfit(Long id) {
        User user = authService.getCurrentUser();
        Outfit outfit = outfitRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Outfit not found"));
        
        if (!outfit.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        outfitRepository.delete(outfit);
    }
    
    private OutfitDTO toDTO(Outfit outfit) {
        OutfitDTO dto = new OutfitDTO();
        dto.setId(outfit.getId());
        dto.setName(outfit.getName());
        dto.setDescription(outfit.getDescription());
        dto.setImageUrl(outfit.getImageUrl());
        dto.setOccasion(outfit.getOccasion());
        dto.setIsFavorite(outfit.getIsFavorite());
        
        if (outfit.getClothes() != null) {
            dto.setClothIds(outfit.getClothes().stream()
                .map(Cloth::getId)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}
