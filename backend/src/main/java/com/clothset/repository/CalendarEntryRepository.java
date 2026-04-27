package com.clothset.repository;

import com.clothset.entity.CalendarEntry;
import com.clothset.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarEntryRepository extends JpaRepository<CalendarEntry, Long> {
    Optional<CalendarEntry> findByUserAndEntryDate(User user, LocalDate entryDate);
    
    @Query("SELECT c FROM CalendarEntry c WHERE c.user = :user AND c.entryDate BETWEEN :startDate AND :endDate")
    List<CalendarEntry> findByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT MONTH(c.entryDate), COUNT(c) FROM CalendarEntry c WHERE c.user = :user AND YEAR(c.entryDate) = :year GROUP BY MONTH(c.entryDate)")
    List<Object[]> countByUserAndYearGroupByMonth(@Param("user") User user, @Param("year") int year);
}
