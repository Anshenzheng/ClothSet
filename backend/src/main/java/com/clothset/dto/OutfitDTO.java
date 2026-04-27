package com.clothset.dto;

import lombok.Data;
import java.util.Set;

@Data
public class OutfitDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String occasion;
    private Boolean isFavorite;
    private Set<Long> clothIds;
}
