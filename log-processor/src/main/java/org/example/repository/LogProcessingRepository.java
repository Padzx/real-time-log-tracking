package org.example.repository;

import org.example.entity.LogProcessing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface LogProcessingRepository extends JpaRepository<LogProcessing, Long> {

    List<LogProcessing> findByLevel(String level);

    List<LogProcessing> findByStatus(String status);

    List<LogProcessing> findByProcessedTimestampAfter(Instant timestamp);

    List<LogProcessing> findByCategory(String category);

    List<LogProcessing> findByMessageContainingIgnoreCase(String keyword);

    @Query("SELECT l FROM LogProcessing l JOIN l.tags t WHERE KEY(t) = :tagKey AND VALUE(t) = :tagValue")
    List<LogProcessing> findByTagKeyAndValue(@Param("tagKey") String tagKey, @Param("tagValue") String tagValue);
}
