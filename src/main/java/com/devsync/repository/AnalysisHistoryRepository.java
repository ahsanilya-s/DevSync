package com.devsync.repository;

import com.devsync.model.AnalysisHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Long> {
    List<AnalysisHistory> findByUserIdOrderByAnalysisDateDesc(String userId);
    
    Long countByUserId(String userId);
    
    void deleteByUserId(String userId);
    
    @Query("SELECT COALESCE(SUM(a.totalIssues), 0) FROM AnalysisHistory a")
    Integer sumTotalIssues();
    
    @Query("SELECT COALESCE(SUM(a.criticalIssues), 0) FROM AnalysisHistory a")
    Integer sumCriticalIssues();
    
    @Query("SELECT COALESCE(SUM(a.warnings), 0) FROM AnalysisHistory a")
    Integer sumWarnings();
    
    @Query("SELECT COALESCE(SUM(a.suggestions), 0) FROM AnalysisHistory a")
    Integer sumSuggestions();
    
    @Query("SELECT MONTH(a.analysisDate) as month, COUNT(a) as count FROM AnalysisHistory a WHERE YEAR(a.analysisDate) = YEAR(CURRENT_DATE) GROUP BY MONTH(a.analysisDate) ORDER BY month ")
    List<Object[]> getMonthlyAnalysisCount();
}