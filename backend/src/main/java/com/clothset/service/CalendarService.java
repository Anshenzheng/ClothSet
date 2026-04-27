package com.clothset.service;

import com.clothset.dto.CalendarEntryDTO;
import com.clothset.entity.CalendarEntry;
import com.clothset.entity.Outfit;
import com.clothset.entity.User;
import com.clothset.repository.CalendarEntryRepository;
import com.clothset.repository.OutfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    
    @Autowired
    private CalendarEntryRepository calendarEntryRepository;
    
    @Autowired
    private OutfitRepository outfitRepository;
    
    @Autowired
    private AuthService authService;
    
    public CalendarEntryDTO getEntryByDate(LocalDate date) {
        User user = authService.getCurrentUser();
        return calendarEntryRepository.findByUserAndEntryDate(user, date)
            .map(this::toDTO)
            .orElse(null);
    }
    
    public List<CalendarEntryDTO> getEntriesByMonth(int year, int month) {
        User user = authService.getCurrentUser();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        return calendarEntryRepository.findByUserAndDateRange(user, startDate, endDate).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public CalendarEntryDTO saveEntry(CalendarEntryDTO dto) {
        User user = authService.getCurrentUser();
        
        CalendarEntry entry = calendarEntryRepository.findByUserAndEntryDate(user, dto.getEntryDate())
            .orElseGet(() -> {
                CalendarEntry newEntry = new CalendarEntry();
                newEntry.setUser(user);
                newEntry.setEntryDate(dto.getEntryDate());
                return newEntry;
            });
        
        entry.setNote(dto.getNote());
        entry.setWeather(dto.getWeather());
        
        if (dto.getOutfitId() != null) {
            Outfit outfit = outfitRepository.findById(dto.getOutfitId())
                .orElseThrow(() -> new RuntimeException("Outfit not found"));
            entry.setOutfit(outfit);
        } else {
            entry.setOutfit(null);
        }
        
        CalendarEntry saved = calendarEntryRepository.save(entry);
        return toDTO(saved);
    }
    
    @Transactional
    public void deleteEntry(Long id) {
        User user = authService.getCurrentUser();
        CalendarEntry entry = calendarEntryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entry not found"));
        
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        calendarEntryRepository.delete(entry);
    }
    
    private CalendarEntryDTO toDTO(CalendarEntry entry) {
        CalendarEntryDTO dto = new CalendarEntryDTO();
        dto.setId(entry.getId());
        dto.setEntryDate(entry.getEntryDate());
        dto.setNote(entry.getNote());
        dto.setWeather(entry.getWeather());
        
        if (entry.getOutfit() != null) {
            dto.setOutfitId(entry.getOutfit().getId());
        }
        
        return dto;
    }
}
