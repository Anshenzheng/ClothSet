package com.clothset.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CalendarEntryDTO {
    private Long id;
    private LocalDate entryDate;
    private Long outfitId;
    private String note;
    private String weather;
}
