package com.clothset.controller;

import com.clothset.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:4200")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> stats = statisticsService.getOverviewStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategoryStats() {
        Map<String, Object> stats = statisticsService.getCategoryStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/colors")
    public ResponseEntity<Map<String, Object>> getColorStats() {
        Map<String, Object> stats = statisticsService.getColorStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStats(@RequestParam int year) {
        Map<String, Object> stats = statisticsService.getMonthlyStatistics(year);
        return ResponseEntity.ok(stats);
    }
}
