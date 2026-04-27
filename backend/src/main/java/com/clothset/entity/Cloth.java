package com.clothset.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "clothes")
public class Cloth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    private String brand;
    
    private String color;
    
    private String material;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    private BigDecimal price;
    
    @Column(name = "wear_count")
    private Integer wearCount = 0;
    
    @Enumerated(EnumType.STRING)
    private ClothStatus status = ClothStatus.ACTIVE;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "cloth_seasons",
        joinColumns = @JoinColumn(name = "cloth_id"),
        inverseJoinColumns = @JoinColumn(name = "season_id")
    )
    private Set<Season> seasons = new HashSet<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ClothStatus {
        ACTIVE, ARCHIVED, DISCARDED
    }
}
