package com.clothset.repository;

import com.clothset.entity.Cloth;
import com.clothset.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClothRepository extends JpaRepository<Cloth, Long> {
    List<Cloth> findByUserOrderByCreatedAtDesc(User user);
    List<Cloth> findByUserAndStatusOrderByCreatedAtDesc(User user, Cloth.ClothStatus status);
    
    @Query("SELECT c FROM Cloth c WHERE c.user = :user AND c.category.id = :categoryId AND c.status = 'ACTIVE'")
    List<Cloth> findByUserAndCategory(@Param("user") User user, @Param("categoryId") Long categoryId);
    
    @Query("SELECT c FROM Cloth c JOIN c.seasons s WHERE c.user = :user AND s.id = :seasonId AND c.status = 'ACTIVE'")
    List<Cloth> findByUserAndSeason(@Param("user") User user, @Param("seasonId") Long seasonId);
    
    @Query("SELECT c FROM Cloth c WHERE c.user = :user AND c.category.id = :categoryId AND c.status = 'ACTIVE' ORDER BY RAND() LIMIT 1")
    Optional<Cloth> findRandomByUserAndCategory(@Param("user") User user, @Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(c) FROM Cloth c WHERE c.user = :user AND c.category.id = :categoryId AND c.status = 'ACTIVE'")
    Long countByUserAndCategory(@Param("user") User user, @Param("categoryId") Long categoryId);
    
    @Query("SELECT c.category.id, COUNT(c) FROM Cloth c WHERE c.user = :user AND c.status = 'ACTIVE' GROUP BY c.category.id")
    List<Object[]> countByUserGroupByCategory(@Param("user") User user);
    
    @Query("SELECT c.color, COUNT(c) FROM Cloth c WHERE c.user = :user AND c.status = 'ACTIVE' AND c.color IS NOT NULL GROUP BY c.color")
    List<Object[]> countByUserGroupByColor(@Param("user") User user);
    
    @Query("SELECT MONTH(c.purchaseDate), COUNT(c) FROM Cloth c WHERE c.user = :user AND c.status = 'ACTIVE' AND c.purchaseDate IS NOT NULL GROUP BY MONTH(c.purchaseDate)")
    List<Object[]> countByUserGroupByMonth(@Param("user") User user);
}
