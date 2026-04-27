package com.clothset.service;

import com.clothset.entity.Category;
import com.clothset.entity.User;
import com.clothset.repository.CategoryRepository;
import com.clothset.repository.ClothRepository;
import com.clothset.repository.CalendarEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsService {
    
    @Autowired
    private ClothRepository clothRepository;
    
    @Autowired
    private CalendarEntryRepository calendarEntryRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private AuthService authService;
    
    public Map<String, Object> getCategoryStatistics() {
        User user = authService.getCurrentUser();
        List<Category> categories = categoryRepository.findByParentIsNull();
        List<Object[]> rawStats = clothRepository.countByUserGroupByCategory(user);
        
        Map<Long, Long> categoryCountMap = new HashMap<>();
        for (Object[] stat : rawStats) {
            Long categoryId = ((Number) stat[0]).longValue();
            Long count = ((Number) stat[1]).longValue();
            categoryCountMap.put(categoryId, count);
        }
        
        List<Map<String, Object>> categoryStats = new ArrayList<>();
        for (Category category : categories) {
            Long count = categoryCountMap.getOrDefault(category.getId(), 0L);
            List<Category> subCategories = categoryRepository.findByParentId(category.getId());
            
            long subCount = 0;
            for (Category subCat : subCategories) {
                subCount += categoryCountMap.getOrDefault(subCat.getId(), 0L);
            }
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("categoryId", category.getId());
            stat.put("categoryName", category.getName());
            stat.put("count", count + subCount);
            categoryStats.add(stat);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("categoryStats", categoryStats);
        return result;
    }
    
    public Map<String, Object> getColorStatistics() {
        User user = authService.getCurrentUser();
        List<Object[]> rawStats = clothRepository.countByUserGroupByColor(user);
        
        List<Map<String, Object>> colorStats = new ArrayList<>();
        for (Object[] stat : rawStats) {
            Map<String, Object> colorMap = new HashMap<>();
            colorMap.put("color", stat[0]);
            colorMap.put("count", ((Number) stat[1]).longValue());
            colorStats.add(colorMap);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("colorStats", colorStats);
        return result;
    }
    
    public Map<String, Object> getMonthlyStatistics(int year) {
        User user = authService.getCurrentUser();
        
        List<Object[]> purchaseStats = clothRepository.countByUserGroupByMonth(user);
        List<Object[]> calendarStats = calendarEntryRepository.countByUserAndYearGroupByMonth(user, year);
        
        Map<Integer, Long> purchaseMap = new HashMap<>();
        for (Object[] stat : purchaseStats) {
            int month = ((Number) stat[0]).intValue();
            Long count = ((Number) stat[1]).longValue();
            purchaseMap.put(month, count);
        }
        
        Map<Integer, Long> calendarMap = new HashMap<>();
        for (Object[] stat : calendarStats) {
            int month = ((Number) stat[0]).intValue();
            Long count = ((Number) stat[1]).longValue();
            calendarMap.put(month, count);
        }
        
        List<Map<String, Object>> monthlyStats = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> monthMap = new HashMap<>();
            monthMap.put("month", i);
            monthMap.put("purchases", purchaseMap.getOrDefault(i, 0L));
            monthMap.put("wearCount", calendarMap.getOrDefault(i, 0L));
            monthlyStats.add(monthMap);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("monthlyStats", monthlyStats);
        return result;
    }
    
    public Map<String, Object> getOverviewStatistics() {
        User user = authService.getCurrentUser();
        LocalDate today = LocalDate.now();
        
        Long totalClothes = clothRepository.countByUserAndCategory(user, null);
        
        List<CalendarEntryDTO> currentMonthEntries = new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalClothes", totalClothes);
        result.put("month", today.getMonthValue());
        result.put("year", today.getYear());
        return result;
    }
}
