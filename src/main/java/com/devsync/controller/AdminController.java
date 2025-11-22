package com.devsync.controller;

import com.devsync.model.User;
import com.devsync.model.AdminSettings;
import com.devsync.model.AnalysisHistory;
import com.devsync.repository.UserRepository;
import com.devsync.repository.AnalysisHistoryRepository;
import com.devsync.repository.AdminSettingsRepository;
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
    
    @Autowired
    private AdminSettingsRepository adminSettingsRepository;

    @PostMapping("/fix-counts")
    public ResponseEntity<String> fixAllReportCounts() {
        try {
            List<AnalysisHistory> allReports = analysisHistoryRepository.findAll();
            int fixed = 0;
            
            for (AnalysisHistory report : allReports) {
                try {
                    String content = com.devsync.reports.ReportGenerator.readReportContent(report.getReportPath());
                    
                    // Count issues by parsing the report content
                    int critical = (int) content.lines().filter(line -> line.contains("🚨 🔴")).count();
                    int high = (int) content.lines().filter(line -> line.contains("🚨 🟡")).count();
                    int medium = (int) content.lines().filter(line -> line.contains("🚨 🟠")).count();
                    int total = critical + high + medium;
                    
                    // Update if counts are different
                    if (report.getTotalIssues() != total || report.getCriticalIssues() != critical || 
                        report.getWarnings() != high || report.getSuggestions() != medium) {
                        
                        report.setTotalIssues(total);
                        report.setCriticalIssues(critical);
                        report.setWarnings(high);
                        report.setSuggestions(medium);
                        analysisHistoryRepository.save(report);
                        fixed++;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to fix report: " + report.getReportPath() + " - " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok("Fixed " + fixed + " reports out of " + allReports.size());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fix counts: " + e.getMessage());
        }
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // Overall statistics
        data.put("totalUsers", userRepository.count());
        
        // Get total issues with null safety
        Integer totalIssues = analysisHistoryRepository.sumTotalIssues();
        data.put("totalIssues", totalIssues != null ? totalIssues : 0);
        
        data.put("aiAnalysisCount", analysisHistoryRepository.count());
        
        // Add severity breakdown for dashboard cards
        Integer criticalIssues = analysisHistoryRepository.sumCriticalIssues();
        Integer warningIssues = analysisHistoryRepository.sumWarnings();
        Integer suggestionIssues = analysisHistoryRepository.sumSuggestions();
        
        data.put("criticalIssues", criticalIssues != null ? criticalIssues : 0);
        data.put("warningIssues", warningIssues != null ? warningIssues : 0);
        data.put("suggestionIssues", suggestionIssues != null ? suggestionIssues : 0);
        
        // Calculate clean files
        long totalAnalyses = analysisHistoryRepository.count();
        long filesWithIssues = analysisHistoryRepository.findAll().stream()
            .mapToLong(analysis -> analysis.getTotalIssues() > 0 ? 1 : 0)
            .sum();
        data.put("cleanFiles", Math.max(0, totalAnalyses - filesWithIssues));
        
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
                Long projectCount = analysisHistoryRepository.countByUserId(user.getId().toString());
            userData.put("projectCount", projectCount != null ? projectCount : 0L);
                users.add(userData);
            });
            System.out.println("Found " + (users != null ? users.size() : 0) + " users");
            return ResponseEntity.ok(users != null ? users : new ArrayList<>());
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
            projectData.put("totalIssues", analysis.getTotalIssues() != null ? analysis.getTotalIssues() : 0);
            projectData.put("criticalIssues", analysis.getCriticalIssues() != null ? analysis.getCriticalIssues() : 0);
            projects.add(projectData);
        });
        return ResponseEntity.ok(projects != null ? projects : new ArrayList<>());
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
                reportData.put("totalIssues", analysis.getTotalIssues() != null ? analysis.getTotalIssues() : 0);
                reportData.put("criticalIssues", analysis.getCriticalIssues() != null ? analysis.getCriticalIssues() : 0);
                reportData.put("warnings", analysis.getWarnings() != null ? analysis.getWarnings() : 0);
                reportData.put("suggestions", analysis.getSuggestions() != null ? analysis.getSuggestions() : 0);
                allReports.add(reportData);
            });
            
            System.out.println("Found " + (allReports != null ? allReports.size() : 0) + " reports");
            
            // Issue distribution with null safety
            Map<String, Long> issueTypes = new HashMap<>();
            Integer criticalSum = analysisHistoryRepository.sumCriticalIssues();
            Integer warningsSum = analysisHistoryRepository.sumWarnings();
            Integer suggestionsSum = analysisHistoryRepository.sumSuggestions();
            
            issueTypes.put("critical", criticalSum != null ? criticalSum.longValue() : 0L);
            issueTypes.put("warnings", warningsSum != null ? warningsSum.longValue() : 0L);
            issueTypes.put("suggestions", suggestionsSum != null ? suggestionsSum.longValue() : 0L);
            
            reports.put("allReports", allReports);
            reports.put("issueDistribution", issueTypes);
            reports.put("monthlyAnalysis", getMonthlyAnalysisData());
            
            return ResponseEntity.ok(reports != null ? reports : new HashMap<>());
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
        
        return monthlyData != null ? monthlyData : new ArrayList<>();
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("id", user.getId());
            userDetails.put("username", user.getUsername());
            userDetails.put("email", user.getEmail());
            userDetails.put("createdAt", user.getCreatedAt());
            
            // Get user's analysis history
            List<Map<String, Object>> analyses = new ArrayList<>();
            analysisHistoryRepository.findByUserIdOrderByAnalysisDateDesc(userId.toString())
                .forEach(analysis -> {
                    Map<String, Object> analysisData = new HashMap<>();
                    analysisData.put("id", analysis.getId());
                    analysisData.put("projectName", analysis.getProjectName());
                    analysisData.put("analysisDate", analysis.getAnalysisDate());
                    analysisData.put("totalIssues", analysis.getTotalIssues() != null ? analysis.getTotalIssues() : 0);
                    analysisData.put("criticalIssues", analysis.getCriticalIssues() != null ? analysis.getCriticalIssues() : 0);
                    analysisData.put("warnings", analysis.getWarnings() != null ? analysis.getWarnings() : 0);
                    analysisData.put("suggestions", analysis.getSuggestions() != null ? analysis.getSuggestions() : 0);
                    analysisData.put("reportPath", analysis.getReportPath());
                    analyses.add(analysisData);
                });
            
            userDetails.put("analyses", analyses);
            userDetails.put("totalAnalyses", analyses != null ? analyses.size() : 0);
            userDetails.put("totalIssuesFound", analyses.stream().mapToInt(a -> {
                Object issues = a.get("totalIssues");
                return issues != null ? (Integer) issues : 0;
            }).sum());
            
            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/users/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody Map<String, String> updates) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            if (updates.containsKey("username")) {
                user.setUsername(updates.get("username"));
            }
            if (updates.containsKey("email")) {
                user.setEmail(updates.get("email"));
            }
            
            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update user: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            if (!userRepository.existsById(userId)) {
                return ResponseEntity.notFound().build();
            }
            
            // Delete user's analysis history first
            analysisHistoryRepository.deleteByUserId(userId.toString());
            
            // Delete user
            userRepository.deleteById(userId);
            
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete user: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/users/{userId}/analyses/{analysisId}")
    public ResponseEntity<String> deleteUserAnalysis(@PathVariable Long userId, @PathVariable Long analysisId) {
        try {
            Optional<AnalysisHistory> analysisOpt = analysisHistoryRepository.findById(analysisId);
            if (!analysisOpt.isPresent() || !analysisOpt.get().getUserId().equals(userId.toString())) {
                return ResponseEntity.notFound().build();
            }
            
            analysisHistoryRepository.deleteById(analysisId);
            return ResponseEntity.ok("Analysis deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete analysis: " + e.getMessage());
        }
    }
    
    @GetMapping("/settings")
    public ResponseEntity<Map<String, List<AdminSettings>>> getAdminSettings() {
        List<AdminSettings> allSettings = adminSettingsRepository.findAll();
        Map<String, List<AdminSettings>> groupedSettings = allSettings.stream()
            .collect(Collectors.groupingBy(AdminSettings::getCategory));
        return ResponseEntity.ok(groupedSettings != null ? groupedSettings : new HashMap<>());
    }
    
    @PostMapping("/settings")
    public ResponseEntity<String> saveAdminSettings(@RequestBody List<AdminSettings> settings) {
        try {
            adminSettingsRepository.saveAll(settings);
            return ResponseEntity.ok("Settings saved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save settings: " + e.getMessage());
        }
    }
    
    @PostMapping("/settings/init")
    public ResponseEntity<String> initializeDefaultSettings() {
        try {
            // App Filters
            saveSettingIfNotExists("max_file_size_mb", "50", "Maximum file size for uploads (MB)", "filters");
            saveSettingIfNotExists("allowed_file_types", "zip,jar", "Allowed file types for upload", "filters");
            saveSettingIfNotExists("max_analysis_time_minutes", "10", "Maximum analysis time per project", "filters");
            saveSettingIfNotExists("enable_ai_analysis", "true", "Enable AI analysis globally", "filters");
            
            // Detection Rules
            saveSettingIfNotExists("global_max_method_length", "100", "Global maximum method length", "detection");
            saveSettingIfNotExists("global_max_parameter_count", "10", "Global maximum parameter count", "detection");
            saveSettingIfNotExists("enable_security_scan", "true", "Enable security vulnerability scanning", "detection");
            
            // System Settings
            saveSettingIfNotExists("maintenance_mode", "false", "Enable maintenance mode", "system");
            saveSettingIfNotExists("user_registration_enabled", "true", "Allow new user registration", "system");
            saveSettingIfNotExists("max_users", "1000", "Maximum number of users", "system");
            
            return ResponseEntity.ok("Default settings initialized");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to initialize settings: " + e.getMessage());
        }
    }
    
    private void saveSettingIfNotExists(String key, String value, String description, String category) {
        if (!adminSettingsRepository.findBySettingKey(key).isPresent()) {
            adminSettingsRepository.save(new AdminSettings(key, value, description, category));
        }
    }
}