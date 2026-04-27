package com.clothset.controller;

import com.clothset.entity.Season;
import com.clothset.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seasons")
@CrossOrigin(origins = "http://localhost:4200")
public class SeasonController {
    
    @Autowired
    private SeasonRepository seasonRepository;
    
    @GetMapping
    public ResponseEntity<List<Season>> getAllSeasons() {
        List<Season> seasons = seasonRepository.findAll();
        return ResponseEntity.ok(seasons);
    }
}
