package com.clothset.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class ClothDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String color;
    private String material;
    private LocalDate purchaseDate;
    private BigDecimal price;
    private Integer wearCount;
    private String status;
    private Set<Long> seasonIds;
}
