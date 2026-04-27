package com.clothset.service;

import com.clothset.dto.ClothDTO;
import com.clothset.entity.Category;
import com.clothset.entity.Cloth;
import com.clothset.entity.Season;
import com.clothset.entity.User;
import com.clothset.repository.CategoryRepository;
import com.clothset.repository.ClothRepository;
import com.clothset.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClothService {
    
    @Autowired
    private ClothRepository clothRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private SeasonRepository seasonRepository;
    
    @Autowired
    private AuthService authService;
    
    public List<ClothDTO> getAllClothes() {
        User user = authService.getCurrentUser();
        return clothRepository.findByUserOrderByCreatedAtDesc(user).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<ClothDTO> getClothesByCategory(Long categoryId) {
        User user = authService.getCurrentUser();
        return clothRepository.findByUserAndCategory(user, categoryId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<ClothDTO> getClothesBySeason(Long seasonId) {
        User user = authService.getCurrentUser();
        return clothRepository.findByUserAndSeason(user, seasonId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public ClothDTO getClothById(Long id) {
        User user = authService.getCurrentUser();
        Cloth cloth = clothRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cloth not found"));
        
        if (!cloth.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        return toDTO(cloth);
    }
    
    @Transactional
    public ClothDTO createCloth(ClothDTO dto) {
        User user = authService.getCurrentUser();
        
        Cloth cloth = new Cloth();
        cloth.setUser(user);
        cloth.setName(dto.getName());
        cloth.setDescription(dto.getDescription());
        cloth.setImageUrl(dto.getImageUrl());
        cloth.setBrand(dto.getBrand());
        cloth.setColor(dto.getColor());
        cloth.setMaterial(dto.getMaterial());
        cloth.setPurchaseDate(dto.getPurchaseDate());
        cloth.setPrice(dto.getPrice());
        cloth.setWearCount(0);
        cloth.setStatus(Cloth.ClothStatus.ACTIVE);
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            cloth.setCategory(category);
        }
        
        if (dto.getSeasonIds() != null && !dto.getSeasonIds().isEmpty()) {
            Set<Season> seasons = new HashSet<>(seasonRepository.findAllById(dto.getSeasonIds()));
            cloth.setSeasons(seasons);
        }
        
        Cloth saved = clothRepository.save(cloth);
        return toDTO(saved);
    }
    
    @Transactional
    public ClothDTO updateCloth(Long id, ClothDTO dto) {
        User user = authService.getCurrentUser();
        Cloth cloth = clothRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cloth not found"));
        
        if (!cloth.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        cloth.setName(dto.getName());
        cloth.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) {
            cloth.setImageUrl(dto.getImageUrl());
        }
        cloth.setBrand(dto.getBrand());
        cloth.setColor(dto.getColor());
        cloth.setMaterial(dto.getMaterial());
        cloth.setPurchaseDate(dto.getPurchaseDate());
        cloth.setPrice(dto.getPrice());
        if (dto.getStatus() != null) {
            cloth.setStatus(Cloth.ClothStatus.valueOf(dto.getStatus()));
        }
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            cloth.setCategory(category);
        }
        
        if (dto.getSeasonIds() != null && !dto.getSeasonIds().isEmpty()) {
            Set<Season> seasons = new HashSet<>(seasonRepository.findAllById(dto.getSeasonIds()));
            cloth.setSeasons(seasons);
        }
        
        Cloth saved = clothRepository.save(cloth);
        return toDTO(saved);
    }
    
    @Transactional
    public void deleteCloth(Long id) {
        User user = authService.getCurrentUser();
        Cloth cloth = clothRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cloth not found"));
        
        if (!cloth.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        clothRepository.delete(cloth);
    }
    
    private ClothDTO toDTO(Cloth cloth) {
        ClothDTO dto = new ClothDTO();
        dto.setId(cloth.getId());
        dto.setName(cloth.getName());
        dto.setDescription(cloth.getDescription());
        dto.setImageUrl(cloth.getImageUrl());
        dto.setBrand(cloth.getBrand());
        dto.setColor(cloth.getColor());
        dto.setMaterial(cloth.getMaterial());
        dto.setPurchaseDate(cloth.getPurchaseDate());
        dto.setPrice(cloth.getPrice());
        dto.setWearCount(cloth.getWearCount());
        dto.setStatus(cloth.getStatus() != null ? cloth.getStatus().name() : null);
        
        if (cloth.getCategory() != null) {
            dto.setCategoryId(cloth.getCategory().getId());
            dto.setCategoryName(cloth.getCategory().getName());
        }
        
        if (cloth.getSeasons() != null) {
            dto.setSeasonIds(cloth.getSeasons().stream()
                .map(Season::getId)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}
