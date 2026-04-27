package com.clothset.controller;

import com.clothset.dto.CalendarEntryDTO;
import com.clothset.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "http://localhost:4200")
public class CalendarController {
    
    @Autowired
    private CalendarService calendarService;
    
    @GetMapping("/date")
    public ResponseEntity<CalendarEntryDTO> getEntryByDate(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        CalendarEntryDTO entry = calendarService.getEntryByDate(localDate);
        return ResponseEntity.ok(entry);
    }
    
    @GetMapping("/month")
    public ResponseEntity<List<CalendarEntryDTO>> getEntriesByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        List<CalendarEntryDTO> entries = calendarService.getEntriesByMonth(year, month);
        return ResponseEntity.ok(entries);
    }
    
    @PostMapping
    public ResponseEntity<CalendarEntryDTO> saveEntry(@RequestBody CalendarEntryDTO entryDTO) {
        CalendarEntryDTO saved = calendarService.saveEntry(entryDTO);
        return ResponseEntity.ok(saved);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        calendarService.deleteEntry(id);
        return ResponseEntity.ok().build();
    }
}
