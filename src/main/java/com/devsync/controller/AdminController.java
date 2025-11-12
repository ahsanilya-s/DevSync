package com.devsync.controller;

import com.devsync.model.User;
import com.devsync.repository.UserRepository;
import com.devsync.repository.AnalysisHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnalysisHistoryRepository analysisHistoryRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // Overall statistics
        data.put("totalUsers", userRepository.count());
        data.put("totalIssues", analysisHistoryRepository.sumTotalIssues());
        data.put("aiAnalysisCount", analysisHistoryRepository.count());
        
        return ResponseEntity.ok(data);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        try {
            List<Map<String, Object>> users = new ArrayList<>();
            userRepository.findAll().forEach(user -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
                userData.put("projectCount", analysisHistoryRepository.countByUserId(user.getId().toString()));
                users.add(userData);
            });
            System.out.println("Found " + users.size() + " users");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error in getUsers: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> getProjects() {
        List<Map<String, Object>> projects = new ArrayList<>();
        Map<String, String> userEmails = userRepository.findAll().stream()
            .collect(Collectors.toMap(u -> u.getId().toString(), User::getEmail));
        
        analysisHistoryRepository.findAll().forEach(analysis -> {
            Map<String, Object> projectData = new HashMap<>();
            projectData.put("id", analysis.getId());
            projectData.put("projectName", analysis.getProjectName());
            projectData.put("userId", analysis.getUserId());
            projectData.put("userEmail", userEmails.get(analysis.getUserId()));
            projectData.put("analysisDate", analysis.getAnalysisDate());
            projectData.put("totalIssues", analysis.getTotalIssues());
            projectData.put("criticalIssues", analysis.getCriticalIssues());
            projects.add(projectData);
        });
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports() {
        try {
            Map<String, Object> reports = new HashMap<>();
            Map<String, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(u -> u.getId().toString(), User::getUsername));
            
            // All reports with user names
            List<Map<String, Object>> allReports = new ArrayList<>();
            analysisHistoryRepository.findAll().forEach(analysis -> {
                Map<String, Object> reportData = new HashMap<>();
                reportData.put("id", analysis.getId());
                reportData.put("projectName", analysis.getProjectName());
                reportData.put("userName", userNames.getOrDefault(analysis.getUserId(), "Unknown User"));
                reportData.put("analysisDate", analysis.getAnalysisDate());
                reportData.put("totalIssues", analysis.getTotalIssues());
                reportData.put("criticalIssues", analysis.getCriticalIssues());
                reportData.put("warnings", analysis.getWarnings());
                reportData.put("suggestions", analysis.getSuggestions());
                allReports.add(reportData);
            });
            
            System.out.println("Found " + allReports.size() + " reports");
            
            // Issue distribution
            Map<String, Long> issueTypes = new HashMap<>();
            issueTypes.put("critical", analysisHistoryRepository.sumCriticalIssues().longValue());
            issueTypes.put("warnings", analysisHistoryRepository.sumWarnings().longValue());
            issueTypes.put("suggestions", analysisHistoryRepository.sumSuggestions().longValue());
            
            reports.put("allReports", allReports);
            reports.put("issueDistribution", issueTypes);
            reports.put("monthlyAnalysis", getMonthlyAnalysisData());
            
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            System.err.println("Error in getReports: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new HashMap<>());
        }
    }

    private List<Map<String, Object>> getMonthlyAnalysisData() {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        List<Object[]> results = analysisHistoryRepository.getMonthlyAnalysisCount();
        
        for (Object[] result : results) {
            Map<String, Object> month = new HashMap<>();
            month.put("month", result[0]);
            month.put("analyses", result[1]);
            monthlyData.add(month);
        }
        
        return monthlyData;
    }
}