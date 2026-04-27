package com.clothset.repository;

import com.clothset.entity.Outfit;
import com.clothset.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    List<Outfit> findByUserOrderByCreatedAtDesc(User user);
    List<Outfit> findByUserAndIsFavoriteTrueOrderByCreatedAtDesc(User user);
}
