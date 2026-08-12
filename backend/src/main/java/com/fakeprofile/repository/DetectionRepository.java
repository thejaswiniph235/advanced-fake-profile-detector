package com.fakeprofile.repository;
import com.fakeprofile.model.Detection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DetectionRepository extends JpaRepository<Detection,Long> {
    List<Detection> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndPrediction(Long userId, String prediction);
}
