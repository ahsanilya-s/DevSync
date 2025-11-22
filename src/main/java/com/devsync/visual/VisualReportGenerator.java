package com.devsync.visual;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.io.image.ImageDataFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VisualReportGenerator {
    
    public byte[] generateVisualArchitectureReport(Map<String, Object> analysisResults, 
                                                  byte[] diagramPNG, 
                                                  String projectName) throws IOException {
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        try {
            // Title Page
            addTitlePage(document, projectName);
            document.add(new AreaBreak());
            
            // Executive Summary
            addExecutiveSummary(document, analysisResults);
            document.add(new AreaBreak());
            
            // Architecture Diagram
            addArchitectureDiagram(document, diagramPNG);
            document.add(new AreaBreak());
            
            // Detailed Analysis
            addDetailedAnalysis(document, analysisResults);
            document.add(new AreaBreak());
            
            // Manager-Friendly Explanation
            addManagerExplanation(document, analysisResults);
            
        } finally {
            document.close();
        }
        
        return outputStream.toByteArray();
    }
    
    private void addTitlePage(Document document, String projectName) {
        // Title
        Paragraph title = new Paragraph("Visual Architecture Report")
            .setFontSize(28)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(100);
        document.add(title);
        
        // Project name
        Paragraph project = new Paragraph(projectName != null ? projectName : "Java Project Analysis")
            .setFontSize(20)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(20);
        document.add(project);
        
        // Date
        Paragraph date = new Paragraph("Generated on: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm")))
            .setFontSize(14)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(50);
        document.add(date);
        
        // DevSync branding
        Paragraph branding = new Paragraph("Powered by DevSync Code Analysis Tool")
            .setFontSize(12)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(200)
            .setFontColor(ColorConstants.GRAY);
        document.add(branding);
    }
    
    private void addExecutiveSummary(Document document, Map<String, Object> analysisResults) {
        @SuppressWarnings("unchecked")
        Map<String, ClassInfo> classes = (Map<String, ClassInfo>) analysisResults.get("classes");
        @SuppressWarnings("unchecked")
        java.util.List<DependencyInfo> dependencies = (java.util.List<DependencyInfo>) analysisResults.get("dependencies");
        
        // Section title
        Paragraph sectionTitle = new Paragraph("Executive Summary")
            .setFontSize(20)
            .setBold()
            .setMarginBottom(20);
        document.add(sectionTitle);
        
        // Overview paragraph
        int totalClasses = classes.size();
        int totalDependencies = dependencies.size();
        int totalLOC = classes.values().stream().mapToInt(ClassInfo::getLinesOfCode).sum();
        int avgComplexity = classes.values().stream().mapToInt(ClassInfo::getComplexity).sum() / Math.max(1, totalClasses);
        
        Paragraph overview = new Paragraph(String.format(
            "This report provides a comprehensive analysis of your Java codebase architecture. " +
            "The project contains %d classes with a total of %,d lines of code. " +
            "The system has %d inter-class dependencies with an average complexity score of %d per class. " +
            "This analysis helps identify the overall structure, maintainability, and potential areas for improvement.",
            totalClasses, totalLOC, totalDependencies, avgComplexity))
            .setMarginBottom(15);
        document.add(overview);
        
        // Key metrics table
        Table metricsTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
            .setWidth(UnitValue.createPercentValue(100));
        
        metricsTable.addHeaderCell(new Cell().add(new Paragraph("Metric").setBold()));
        metricsTable.addHeaderCell(new Cell().add(new Paragraph("Value").setBold()));
        
        metricsTable.addCell("Total Classes");
        metricsTable.addCell(String.valueOf(totalClasses));
        
        metricsTable.addCell("Total Lines of Code");
        metricsTable.addCell(String.format("%,d", totalLOC));
        
        metricsTable.addCell("Class Dependencies");
        metricsTable.addCell(String.valueOf(totalDependencies));
        
        metricsTable.addCell("Average Complexity");
        metricsTable.addCell(String.valueOf(avgComplexity));
        
        long interfaces = classes.values().stream().filter(ClassInfo::isInterface).count();
        metricsTable.addCell("Interfaces");
        metricsTable.addCell(String.valueOf(interfaces));
        
        long abstractClasses = classes.values().stream().filter(ClassInfo::isAbstract).count();
        metricsTable.addCell("Abstract Classes");
        metricsTable.addCell(String.valueOf(abstractClasses));
        
        document.add(metricsTable);
    }
    
    private void addArchitectureDiagram(Document document, byte[] diagramPNG) throws IOException {
        // Section title
        Paragraph sectionTitle = new Paragraph("System Architecture Diagram")
            .setFontSize(20)
            .setBold()
            .setMarginBottom(20);
        document.add(sectionTitle);
        
        // Description
        Paragraph description = new Paragraph(
            "The following diagram shows the high-level architecture of your system, " +
            "including class relationships, dependencies, and package structure. " +
            "This visual representation helps understand how different components interact.")
            .setMarginBottom(15);
        document.add(description);
        
        // Add diagram
        if (diagramPNG != null && diagramPNG.length > 0) {
            Image diagram = new Image(ImageDataFactory.create(diagramPNG))
                .setWidth(UnitValue.createPercentValue(90))
                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
            document.add(diagram);
        } else {
            Paragraph noDiagram = new Paragraph("Architecture diagram could not be generated.")
                .setFontColor(ColorConstants.RED)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(noDiagram);
        }
    }
    
    private void addDetailedAnalysis(Document document, Map<String, Object> analysisResults) {
        @SuppressWarnings("unchecked")
        Map<String, ClassInfo> classes = (Map<String, ClassInfo>) analysisResults.get("classes");
        @SuppressWarnings("unchecked")
        java.util.List<DependencyInfo> dependencies = (java.util.List<DependencyInfo>) analysisResults.get("dependencies");
        
        // Section title
        Paragraph sectionTitle = new Paragraph("Detailed Class Analysis")
            .setFontSize(20)
            .setBold()
            .setMarginBottom(20);
        document.add(sectionTitle);
        
        // Classes table
        Table classTable = new Table(UnitValue.createPercentArray(new float[]{30, 20, 15, 15, 20}))
            .setWidth(UnitValue.createPercentValue(100));
        
        // Headers
        classTable.addHeaderCell(new Cell().add(new Paragraph("Class Name").setBold()));
        classTable.addHeaderCell(new Cell().add(new Paragraph("Internal Dependencies").setBold()));
        classTable.addHeaderCell(new Cell().add(new Paragraph("External Dependencies").setBold()));
        classTable.addHeaderCell(new Cell().add(new Paragraph("Lines of Code").setBold()));
        classTable.addHeaderCell(new Cell().add(new Paragraph("Complexity").setBold()));
        
        // Sort classes by complexity (highest first)
        java.util.List<ClassInfo> sortedClasses = new ArrayList<>(classes.values());
        sortedClasses.sort((a, b) -> Integer.compare(b.getComplexity(), a.getComplexity()));
        
        // Add top 15 classes to avoid overwhelming the report
        for (int i = 0; i < Math.min(15, sortedClasses.size()); i++) {
            ClassInfo classInfo = sortedClasses.get(i);
            
            // Count dependencies for this class
            long internalDeps = dependencies.stream()
                .filter(dep -> dep.getFromClass().equals(classInfo.getFullName()))
                .filter(dep -> classes.containsKey(dep.getToClass()))
                .count();
            
            long externalDeps = dependencies.stream()
                .filter(dep -> dep.getFromClass().equals(classInfo.getFullName()))
                .filter(dep -> !classes.containsKey(dep.getToClass()))
                .count();
            
            classTable.addCell(classInfo.getClassName());
            classTable.addCell(String.valueOf(internalDeps));
            classTable.addCell(String.valueOf(externalDeps));
            classTable.addCell(String.valueOf(classInfo.getLinesOfCode()));
            
            // Color-code complexity
            Cell complexityCell = new Cell().add(new Paragraph(String.valueOf(classInfo.getComplexity())));
            if (classInfo.getComplexity() > 10) {
                complexityCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            }
            classTable.addCell(complexityCell);
        }
        
        document.add(classTable);
        
        if (sortedClasses.size() > 15) {
            Paragraph note = new Paragraph(String.format(
                "Note: Showing top 15 classes by complexity. Total classes analyzed: %d", 
                sortedClasses.size()))
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY)
                .setMarginTop(10);
            document.add(note);
        }
    }
    
    private void addManagerExplanation(Document document, Map<String, Object> analysisResults) {
        @SuppressWarnings("unchecked")
        Map<String, ClassInfo> classes = (Map<String, ClassInfo>) analysisResults.get("classes");
        @SuppressWarnings("unchecked")
        java.util.List<DependencyInfo> dependencies = (java.util.List<DependencyInfo>) analysisResults.get("dependencies");
        
        // Section title
        Paragraph sectionTitle = new Paragraph("Manager-Friendly Explanation")
            .setFontSize(20)
            .setBold()
            .setMarginBottom(20);
        document.add(sectionTitle);
        
        // What this means section
        Paragraph whatMeansTitle = new Paragraph("What This Analysis Means:")
            .setFontSize(16)
            .setBold()
            .setMarginBottom(10);
        document.add(whatMeansTitle);
        
        com.itextpdf.layout.element.List whatMeansList = new com.itextpdf.layout.element.List()
            .setMarginBottom(15);
        
        whatMeansList.add(new ListItem("Classes: Individual components or modules of your software"));
        whatMeansList.add(new ListItem("Dependencies: How different parts of your code rely on each other"));
        whatMeansList.add(new ListItem("Complexity: How difficult it is to understand and modify each component"));
        whatMeansList.add(new ListItem("Lines of Code: The size of each component (more lines = more functionality but potentially harder to maintain)"));
        
        document.add(whatMeansList);
        
        // Business impact section
        Paragraph impactTitle = new Paragraph("Business Impact:")
            .setFontSize(16)
            .setBold()
            .setMarginBottom(10);
        document.add(impactTitle);
        
        // Calculate some business metrics
        int totalClasses = classes.size();
        int highComplexityClasses = (int) classes.values().stream().filter(c -> c.getComplexity() > 10).count();
        int totalLOC = classes.values().stream().mapToInt(ClassInfo::getLinesOfCode).sum();
        
        com.itextpdf.layout.element.List impactList = new com.itextpdf.layout.element.List()
            .setMarginBottom(15);
        
        if (highComplexityClasses > totalClasses * 0.2) {
            impactList.add(new ListItem("⚠️ High Complexity Alert: " + highComplexityClasses + 
                " classes have high complexity, which may increase maintenance costs"));
        } else {
            impactList.add(new ListItem("✅ Good Complexity: Most classes have manageable complexity levels"));
        }
        
        if (totalLOC > 50000) {
            impactList.add(new ListItem("📈 Large Codebase: " + String.format("%,d", totalLOC) + 
                " lines of code indicates a substantial system requiring structured maintenance"));
        } else {
            impactList.add(new ListItem("📊 Moderate Size: " + String.format("%,d", totalLOC) + 
                " lines of code represents a manageable codebase"));
        }
        
        if (dependencies.size() > totalClasses * 2) {
            impactList.add(new ListItem("🔗 High Coupling: Many dependencies may make changes more risky and time-consuming"));
        } else {
            impactList.add(new ListItem("🔗 Reasonable Coupling: Dependencies are at manageable levels"));
        }
        
        document.add(impactList);
        
        // Recommendations section
        Paragraph recommendationsTitle = new Paragraph("Recommendations for Management:")
            .setFontSize(16)
            .setBold()
            .setMarginBottom(10);
        document.add(recommendationsTitle);
        
        com.itextpdf.layout.element.List recommendationsList = new com.itextpdf.layout.element.List();
        
        recommendationsList.add(new ListItem("Regular code reviews to maintain quality and share knowledge"));
        recommendationsList.add(new ListItem("Invest in developer training for complex components"));
        recommendationsList.add(new ListItem("Consider refactoring high-complexity classes to reduce maintenance costs"));
        recommendationsList.add(new ListItem("Implement automated testing to ensure system reliability"));
        recommendationsList.add(new ListItem("Plan for technical debt reduction in future sprints"));
        
        document.add(recommendationsList);
        
        // Footer note
        Paragraph footer = new Paragraph(
            "This analysis provides insights into your codebase structure and quality. " +
            "Regular monitoring helps maintain system health and development velocity.")
            .setFontSize(10)
            .setFontColor(ColorConstants.GRAY)
            .setMarginTop(30)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(footer);
    }
}