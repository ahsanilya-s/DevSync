package com.devsync.scheduler;

import com.devsync.model.AnalysisHistory;
import com.devsync.repository.AnalysisHistoryRepository;
import com.devsync.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CleanupScheduler {
    
    @Autowired
    private AnalysisHistoryRepository analysisHistoryRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    // Run every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldReports() {
        System.out.println("🧹 Starting automatic cleanup of old reports...");
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        List<AnalysisHistory> oldReports = analysisHistoryRepository.findByAnalysisDateBefore(cutoffDate);
        
        int deletedCount = 0;
        
        for (AnalysisHistory report : oldReports) {
            try {
                if (report.getProjectPath() != null) {
                    File projectFolder = new File(report.getProjectPath());
                    if (projectFolder.exists()) {
                        fileStorageService.deleteDirectory(projectFolder);
                    }
                }
                
                analysisHistoryRepository.delete(report);
                deletedCount++;
            } catch (Exception e) {
                System.err.println("❌ Failed to cleanup " + report.getProjectName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("🧹 Cleanup complete: " + deletedCount + " reports deleted");
    }
}
