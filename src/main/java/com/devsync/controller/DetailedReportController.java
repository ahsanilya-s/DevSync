package com.devsync.controller;

import com.devsync.services.DetailedReportService;
import com.devsync.services.ChartDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/detailed-report")
@CrossOrigin(origins = "*")
public class DetailedReportController {

    @Autowired
    private DetailedReportService detailedReportService;
    
    @Autowired
    private ChartDataService chartDataService;

    @GetMapping("/generate")
    public ResponseEntity<String> generateDetailedReport(
            @RequestParam String reportPath,
            @RequestParam String projectPath) {
        
        try {
            String htmlReport = detailedReportService.generateDetailedHTMLReport(reportPath, projectPath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"detailed-report.html\"");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(htmlReport);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("<html><body><h1>Error generating report</h1><p>" + e.getMessage() + "</p></body></html>");
        }
    }
    
    @PostMapping("/chart-data")
    public ResponseEntity<Map<String, Object>> getChartData(
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> analysisResults = (Map<String, Object>) request.get("analysisResults");
            @SuppressWarnings("unchecked")
            java.util.List<String> issues = (java.util.List<String>) request.get("issues");
            
            Map<String, Object> chartData = chartDataService.generateChartData(analysisResults, issues);
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
