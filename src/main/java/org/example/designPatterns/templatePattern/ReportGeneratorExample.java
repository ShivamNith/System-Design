package org.example.designPatterns.templatePattern;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Template Method Pattern Example: Report Generation System
 * 
 * This example demonstrates how different report formats (PDF, Excel, HTML)
 * follow the same report generation process while implementing format-specific
 * rendering, styling, and output generation.
 */

// Supporting classes for report data
class ReportData {
    private final String title;
    private final LocalDateTime generatedAt;
    private final Map<String, Object> metadata;
    private final List<ReportSection> sections;
    
    public ReportData(String title) {
        this.title = title;
        this.generatedAt = LocalDateTime.now();
        this.metadata = new HashMap<>();
        this.sections = new ArrayList<>();
    }
    
    public String getTitle() { return title; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    public List<ReportSection> getSections() { return new ArrayList<>(sections); }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public void addSection(ReportSection section) {
        sections.add(section);
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
}

class ReportSection {
    private final String title;
    private final SectionType type;
    private final Object content;
    private final Map<String, Object> properties;
    
    public ReportSection(String title, SectionType type, Object content) {
        this.title = title;
        this.type = type;
        this.content = content;
        this.properties = new HashMap<>();
    }
    
    public String getTitle() { return title; }
    public SectionType getType() { return type; }
    public Object getContent() { return content; }
    public Map<String, Object> getProperties() { return new HashMap<>(properties); }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
}

enum SectionType {
    HEADER, TEXT, TABLE, CHART, IMAGE, FOOTER
}

class TableData {
    private final List<String> headers;
    private final List<List<Object>> rows;
    
    public TableData(List<String> headers) {
        this.headers = new ArrayList<>(headers);
        this.rows = new ArrayList<>();
    }
    
    public void addRow(List<Object> row) {
        if (row.size() != headers.size()) {
            throw new IllegalArgumentException("Row size must match header count");
        }
        rows.add(new ArrayList<>(row));
    }
    
    public List<String> getHeaders() { return new ArrayList<>(headers); }
    public List<List<Object>> getRows() { return new ArrayList<>(rows); }
    public int getRowCount() { return rows.size(); }
    public int getColumnCount() { return headers.size(); }
}

class ChartData {
    private final String chartType;
    private final Map<String, List<Number>> datasets;
    private final List<String> labels;
    
    public ChartData(String chartType) {
        this.chartType = chartType;
        this.datasets = new HashMap<>();
        this.labels = new ArrayList<>();
    }
    
    public void addDataset(String name, List<Number> data) {
        datasets.put(name, new ArrayList<>(data));
    }
    
    public void setLabels(List<String> labels) {
        this.labels.clear();
        this.labels.addAll(labels);
    }
    
    public String getChartType() { return chartType; }
    public Map<String, List<Number>> getDatasets() { return new HashMap<>(datasets); }
    public List<String> getLabels() { return new ArrayList<>(labels); }
}

class GeneratedReport {
    private final String format;
    private final String filePath;
    private final long fileSizeBytes;
    private final LocalDateTime generatedAt;
    private final Map<String, Object> metadata;
    private final boolean successful;
    
    public GeneratedReport(String format, String filePath, long fileSizeBytes, boolean successful) {
        this.format = format;
        this.filePath = filePath;
        this.fileSizeBytes = fileSizeBytes;
        this.generatedAt = LocalDateTime.now();
        this.metadata = new HashMap<>();
        this.successful = successful;
    }
    
    public static GeneratedReport success(String format, String filePath, long fileSize) {
        return new GeneratedReport(format, filePath, fileSize, true);
    }
    
    public static GeneratedReport failure(String format, String error) {
        GeneratedReport report = new GeneratedReport(format, null, 0, false);
        report.addMetadata("error", error);
        return report;
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public String getFormat() { return format; }
    public String getFilePath() { return filePath; }
    public long getFileSizeBytes() { return fileSizeBytes; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    public boolean isSuccessful() { return successful; }
}

// Abstract Report Generator Template
abstract class ReportGenerator {
    private final Map<String, Object> configuration;
    
    public ReportGenerator() {
        this.configuration = new HashMap<>();
        setDefaultConfiguration();
    }
    
    /**
     * Template method defining the report generation process
     */
    public final GeneratedReport generateReport(ReportData data, String outputPath) {
        System.out.println("Starting report generation: " + data.getTitle());
        
        try {
            // Step 1: Validate input data
            validateReportData(data);
            
            // Step 2: Initialize report context
            initializeReportContext(data);
            
            // Step 3: Apply pre-processing transformations
            if (shouldPreprocessData()) {
                data = preprocessData(data);
            }
            
            // Step 4: Create report structure
            createReportStructure(data);
            
            // Step 5: Generate header
            generateHeader(data);
            
            // Step 6: Process each section
            for (ReportSection section : data.getSections()) {
                processSection(section);
            }
            
            // Step 7: Generate footer
            generateFooter(data);
            
            // Step 8: Apply styling and formatting
            applyFormatting();
            
            // Step 9: Optimize output if needed
            if (shouldOptimizeOutput()) {
                optimizeOutput();
            }
            
            // Step 10: Save report to file
            String finalPath = saveReport(outputPath);
            
            // Step 11: Post-process and cleanup
            postProcess();
            
            long fileSize = getOutputFileSize(finalPath);
            GeneratedReport result = GeneratedReport.success(getFormat(), finalPath, fileSize);
            
            // Add generation metadata
            result.addMetadata("generation_time_ms", System.currentTimeMillis());
            result.addMetadata("section_count", data.getSections().size());
            result.addMetadata("format", getFormat());
            
            System.out.println("Report generation completed: " + finalPath);
            return result;
            
        } catch (Exception e) {
            System.err.println("Report generation failed: " + e.getMessage());
            return GeneratedReport.failure(getFormat(), e.getMessage());
        }
    }
    
    // Abstract methods that subclasses must implement
    protected abstract String getFormat();
    protected abstract void createReportStructure(ReportData data);
    protected abstract void generateHeader(ReportData data);
    protected abstract void processSection(ReportSection section);
    protected abstract void generateFooter(ReportData data);
    protected abstract void applyFormatting();
    protected abstract String saveReport(String outputPath) throws IOException;
    
    // Hook methods with default implementations
    protected void setDefaultConfiguration() {
        configuration.put("include_timestamp", true);
        configuration.put("include_page_numbers", true);
        configuration.put("compress_output", false);
    }
    
    protected void validateReportData(ReportData data) {
        if (data == null) {
            throw new IllegalArgumentException("Report data cannot be null");
        }
        if (data.getTitle() == null || data.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Report must have a title");
        }
        if (data.getSections().isEmpty()) {
            throw new IllegalArgumentException("Report must have at least one section");
        }
        System.out.println("Report data validation passed");
    }
    
    protected void initializeReportContext(ReportData data) {
        System.out.println("Initializing report context for format: " + getFormat());
        // Default: no special initialization needed
    }
    
    protected boolean shouldPreprocessData() {
        return false; // Default: no preprocessing needed
    }
    
    protected ReportData preprocessData(ReportData data) {
        System.out.println("Applying default data preprocessing...");
        return data; // Default: return data unchanged
    }
    
    protected boolean shouldOptimizeOutput() {
        return (Boolean) configuration.getOrDefault("compress_output", false);
    }
    
    protected void optimizeOutput() {
        System.out.println("Applying output optimization...");
        // Default: no optimization
    }
    
    protected void postProcess() {
        System.out.println("Performing post-processing cleanup...");
        // Default: no post-processing needed
    }
    
    protected long getOutputFileSize(String filePath) {
        try {
            File file = new File(filePath);
            return file.exists() ? file.length() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    // Configuration methods
    protected void setConfiguration(String key, Object value) {
        configuration.put(key, value);
    }
    
    protected Object getConfiguration(String key) {
        return configuration.get(key);
    }
    
    protected boolean getConfigurationBoolean(String key, boolean defaultValue) {
        Object value = configuration.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }
}

// Concrete implementation for PDF report generation
class PDFReportGenerator extends ReportGenerator {
    private StringBuilder pdfContent;
    private int pageCount;
    
    @Override
    protected String getFormat() {
        return "PDF";
    }
    
    @Override
    protected void setDefaultConfiguration() {
        super.setDefaultConfiguration();
        setConfiguration("page_size", "A4");
        setConfiguration("margin", 72); // points
        setConfiguration("font_size", 12);
        setConfiguration("include_bookmarks", true);
    }
    
    @Override
    protected void initializeReportContext(ReportData data) {
        super.initializeReportContext(data);
        this.pdfContent = new StringBuilder();
        this.pageCount = 1;
        
        // PDF header
        pdfContent.append("%PDF-1.4\n");
        pdfContent.append("% Generated by Template Method Report Generator\n");
    }
    
    @Override
    protected boolean shouldPreprocessData() {
        return true; // PDF needs special text processing
    }
    
    @Override
    protected ReportData preprocessData(ReportData data) {
        System.out.println("Preprocessing data for PDF format...");
        
        // Create a copy of the report data with PDF-specific transformations
        ReportData processedData = new ReportData(data.getTitle());
        
        // Copy metadata
        for (Map.Entry<String, Object> entry : data.getMetadata().entrySet()) {
            processedData.addMetadata(entry.getKey(), entry.getValue());
        }
        
        // Process sections for PDF compatibility
        for (ReportSection section : data.getSections()) {
            if (section.getType() == SectionType.TEXT) {
                // Escape special characters for PDF
                String content = section.getContent().toString();
                content = content.replace("&", "&amp;")
                               .replace("<", "&lt;")
                               .replace(">", "&gt;");
                
                ReportSection processedSection = new ReportSection(
                    section.getTitle(), 
                    section.getType(), 
                    content
                );
                
                // Copy properties
                for (Map.Entry<String, Object> prop : section.getProperties().entrySet()) {
                    processedSection.setProperty(prop.getKey(), prop.getValue());
                }
                
                processedData.addSection(processedSection);
            } else {
                processedData.addSection(section);
            }
        }
        
        return processedData;
    }
    
    @Override
    protected void createReportStructure(ReportData data) {
        System.out.println("Creating PDF report structure...");
        
        pdfContent.append("\n% Document structure\n");
        pdfContent.append("1 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Catalog\n");
        pdfContent.append("/Pages 2 0 R\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n");
    }
    
    @Override
    protected void generateHeader(ReportData data) {
        System.out.println("Generating PDF header...");
        
        pdfContent.append("\n% Header\n");
        pdfContent.append("BT\n");
        pdfContent.append("/F1 18 Tf\n");
        pdfContent.append("100 750 Td\n");
        pdfContent.append("(").append(data.getTitle()).append(") Tj\n");
        pdfContent.append("ET\n");
        
        if (getConfigurationBoolean("include_timestamp", true)) {
            pdfContent.append("BT\n");
            pdfContent.append("/F1 10 Tf\n");
            pdfContent.append("100 730 Td\n");
            pdfContent.append("(Generated: ").append(
                data.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ).append(") Tj\n");
            pdfContent.append("ET\n");
        }
    }
    
    @Override
    protected void processSection(ReportSection section) {
        System.out.println("Processing PDF section: " + section.getTitle());
        
        switch (section.getType()) {
            case HEADER:
                processPDFHeader(section);
                break;
            case TEXT:
                processPDFText(section);
                break;
            case TABLE:
                processPDFTable(section);
                break;
            case CHART:
                processPDFChart(section);
                break;
            case IMAGE:
                processPDFImage(section);
                break;
            default:
                pdfContent.append("% Unsupported section type: ").append(section.getType()).append("\n");
        }
    }
    
    private void processPDFHeader(ReportSection section) {
        pdfContent.append("\n% Section Header\n");
        pdfContent.append("BT\n");
        pdfContent.append("/F1 14 Tf\n");
        pdfContent.append("100 700 Td\n");
        pdfContent.append("(").append(section.getTitle()).append(") Tj\n");
        pdfContent.append("ET\n");
    }
    
    private void processPDFText(ReportSection section) {
        pdfContent.append("\n% Text Section\n");
        pdfContent.append("BT\n");
        pdfContent.append("/F1 12 Tf\n");
        pdfContent.append("100 680 Td\n");
        
        String text = section.getContent().toString();
        // Split long text into multiple lines
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            pdfContent.append("(").append(lines[i]).append(") Tj\n");
            if (i < lines.length - 1) {
                pdfContent.append("0 -15 Td\n"); // Move to next line
            }
        }
        
        pdfContent.append("ET\n");
    }
    
    private void processPDFTable(ReportSection section) {
        if (!(section.getContent() instanceof TableData)) {
            return;
        }
        
        TableData table = (TableData) section.getContent();
        pdfContent.append("\n% Table Section\n");
        
        // Table headers
        pdfContent.append("BT\n");
        pdfContent.append("/F1 10 Tf\n");
        pdfContent.append("100 650 Td\n");
        
        for (int i = 0; i < table.getHeaders().size(); i++) {
            pdfContent.append("(").append(table.getHeaders().get(i)).append(") Tj\n");
            if (i < table.getHeaders().size() - 1) {
                pdfContent.append("100 0 Td\n"); // Move to next column
            }
        }
        
        // Table rows
        for (int row = 0; row < table.getRowCount(); row++) {
            pdfContent.append("0 -15 Td\n"); // Move to next row
            List<Object> rowData = table.getRows().get(row);
            
            for (int col = 0; col < rowData.size(); col++) {
                pdfContent.append("(").append(rowData.get(col).toString()).append(") Tj\n");
                if (col < rowData.size() - 1) {
                    pdfContent.append("100 0 Td\n"); // Move to next column
                }
            }
        }
        
        pdfContent.append("ET\n");
    }
    
    private void processPDFChart(ReportSection section) {
        pdfContent.append("\n% Chart placeholder (PDF chart generation would require specialized library)\n");
        pdfContent.append("BT\n");
        pdfContent.append("/F1 12 Tf\n");
        pdfContent.append("100 600 Td\n");
        pdfContent.append("([Chart: ").append(section.getTitle()).append("]) Tj\n");
        pdfContent.append("ET\n");
    }
    
    private void processPDFImage(ReportSection section) {
        pdfContent.append("\n% Image placeholder\n");
        pdfContent.append("BT\n");
        pdfContent.append("/F1 12 Tf\n");
        pdfContent.append("100 580 Td\n");
        pdfContent.append("([Image: ").append(section.getTitle()).append("]) Tj\n");
        pdfContent.append("ET\n");
    }
    
    @Override
    protected void generateFooter(ReportData data) {
        System.out.println("Generating PDF footer...");
        
        if (getConfigurationBoolean("include_page_numbers", true)) {
            pdfContent.append("\n% Footer\n");
            pdfContent.append("BT\n");
            pdfContent.append("/F1 10 Tf\n");
            pdfContent.append("300 50 Td\n");
            pdfContent.append("(Page ").append(pageCount).append(") Tj\n");
            pdfContent.append("ET\n");
        }
    }
    
    @Override
    protected void applyFormatting() {
        System.out.println("Applying PDF formatting...");
        
        // Add page definition
        pdfContent.append("\n% Page definition\n");
        pdfContent.append("2 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Pages\n");
        pdfContent.append("/Kids [3 0 R]\n");
        pdfContent.append("/Count 1\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n");
        
        // Add font definition
        pdfContent.append("\n% Font definition\n");
        pdfContent.append("4 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Font\n");
        pdfContent.append("/Subtype /Type1\n");
        pdfContent.append("/BaseFont /Helvetica\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n");
    }
    
    @Override
    protected void optimizeOutput() {
        if (shouldOptimizeOutput()) {
            System.out.println("Compressing PDF content...");
            // In a real implementation, this would compress the PDF streams
            pdfContent.append("\n% Content compression applied\n");
        }
    }
    
    @Override
    protected String saveReport(String outputPath) throws IOException {
        String finalPath = outputPath.endsWith(".pdf") ? outputPath : outputPath + ".pdf";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(finalPath))) {
            writer.print(pdfContent.toString());
            
            // Add PDF trailer
            writer.println("\nxref");
            writer.println("0 5");
            writer.println("0000000000 65535 f");
            writer.println("0000000009 00000 n");
            writer.println("0000000074 00000 n");
            writer.println("0000000131 00000 n");
            writer.println("0000000244 00000 n");
            writer.println("trailer");
            writer.println("<<");
            writer.println("/Size 5");
            writer.println("/Root 1 0 R");
            writer.println(">>");
            writer.println("startxref");
            writer.println("344");
            writer.println("%%EOF");
        }
        
        return finalPath;
    }
}

// Concrete implementation for Excel report generation
class ExcelReportGenerator extends ReportGenerator {
    private StringBuilder excelContent;
    private int currentRow;
    
    @Override
    protected String getFormat() {
        return "Excel";
    }
    
    @Override
    protected void setDefaultConfiguration() {
        super.setDefaultConfiguration();
        setConfiguration("sheet_name", "Report");
        setConfiguration("auto_fit_columns", true);
        setConfiguration("freeze_header_row", true);
    }
    
    @Override
    protected void initializeReportContext(ReportData data) {
        super.initializeReportContext(data);
        this.excelContent = new StringBuilder();
        this.currentRow = 1;
        
        // Excel XML header
        excelContent.append("<?xml version=\"1.0\"?>\n");
        excelContent.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
        excelContent.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
        excelContent.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
        excelContent.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
        excelContent.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n");
    }
    
    @Override
    protected void createReportStructure(ReportData data) {
        System.out.println("Creating Excel report structure...");
        
        String sheetName = (String) getConfiguration("sheet_name");
        excelContent.append("<Worksheet ss:Name=\"").append(sheetName).append("\">\n");
        excelContent.append("<Table>\n");
    }
    
    @Override
    protected void generateHeader(ReportData data) {
        System.out.println("Generating Excel header...");
        
        addExcelRow(Arrays.asList(data.getTitle()), "HeaderStyle");
        currentRow++;
        
        if (getConfigurationBoolean("include_timestamp", true)) {
            addExcelRow(Arrays.asList(
                "Generated: " + data.getGeneratedAt().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                )
            ), "TimestampStyle");
            currentRow++;
        }
        
        // Add empty row for spacing
        addExcelRow(Arrays.asList(""), null);
        currentRow++;
    }
    
    @Override
    protected void processSection(ReportSection section) {
        System.out.println("Processing Excel section: " + section.getTitle());
        
        switch (section.getType()) {
            case HEADER:
                processExcelHeader(section);
                break;
            case TEXT:
                processExcelText(section);
                break;
            case TABLE:
                processExcelTable(section);
                break;
            case CHART:
                processExcelChart(section);
                break;
            default:
                addExcelRow(Arrays.asList("Unsupported section: " + section.getType()), null);
                currentRow++;
        }
        
        // Add spacing between sections
        addExcelRow(Arrays.asList(""), null);
        currentRow++;
    }
    
    private void processExcelHeader(ReportSection section) {
        addExcelRow(Arrays.asList(section.getTitle()), "SectionHeaderStyle");
        currentRow++;
    }
    
    private void processExcelText(ReportSection section) {
        String[] lines = section.getContent().toString().split("\n");
        for (String line : lines) {
            addExcelRow(Arrays.asList(line), "TextStyle");
            currentRow++;
        }
    }
    
    private void processExcelTable(ReportSection section) {
        if (!(section.getContent() instanceof TableData)) {
            return;
        }
        
        TableData table = (TableData) section.getContent();
        
        // Add section title
        addExcelRow(Arrays.asList(section.getTitle()), "SectionHeaderStyle");
        currentRow++;
        
        // Add table headers
        List<Object> headers = new ArrayList<>(table.getHeaders());
        addExcelRow(headers, "TableHeaderStyle");
        currentRow++;
        
        // Add table rows
        for (List<Object> row : table.getRows()) {
            addExcelRow(row, "TableDataStyle");
            currentRow++;
        }
    }
    
    private void processExcelChart(ReportSection section) {
        if (!(section.getContent() instanceof ChartData)) {
            return;
        }
        
        ChartData chart = (ChartData) section.getContent();
        
        // Add section title
        addExcelRow(Arrays.asList(section.getTitle() + " (" + chart.getChartType() + ")"), "SectionHeaderStyle");
        currentRow++;
        
        // Add chart data as table
        List<Object> headerRow = new ArrayList<>();
        headerRow.add("Label");
        headerRow.addAll(chart.getDatasets().keySet());
        addExcelRow(headerRow, "TableHeaderStyle");
        currentRow++;
        
        for (int i = 0; i < chart.getLabels().size(); i++) {
            List<Object> dataRow = new ArrayList<>();
            dataRow.add(chart.getLabels().get(i));
            
            for (String dataset : chart.getDatasets().keySet()) {
                List<Number> values = chart.getDatasets().get(dataset);
                if (i < values.size()) {
                    dataRow.add(values.get(i));
                } else {
                    dataRow.add("");
                }
            }
            
            addExcelRow(dataRow, "TableDataStyle");
            currentRow++;
        }
    }
    
    private void addExcelRow(List<Object> values, String styleId) {
        excelContent.append("<Row");
        if (styleId != null) {
            excelContent.append(" ss:StyleID=\"").append(styleId).append("\"");
        }
        excelContent.append(">\n");
        
        for (Object value : values) {
            excelContent.append("<Cell>");
            excelContent.append("<Data ss:Type=\"String\">");
            excelContent.append(escapeXml(value.toString()));
            excelContent.append("</Data>");
            excelContent.append("</Cell>\n");
        }
        
        excelContent.append("</Row>\n");
    }
    
    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
    
    @Override
    protected void generateFooter(ReportData data) {
        System.out.println("Generating Excel footer...");
        
        addExcelRow(Arrays.asList(""), null);
        addExcelRow(Arrays.asList("Report Summary:"), "SectionHeaderStyle");
        addExcelRow(Arrays.asList("Total Sections: " + data.getSections().size()), "TextStyle");
        addExcelRow(Arrays.asList("Generated At: " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )), "TextStyle");
    }
    
    @Override
    protected void applyFormatting() {
        System.out.println("Applying Excel formatting...");
        
        // Close table and worksheet
        excelContent.insert(excelContent.indexOf("<Table>") + 7, "\n" + getExcelStyles());
        excelContent.append("</Table>\n");
        excelContent.append("</Worksheet>\n");
        excelContent.append("</Workbook>\n");
    }
    
    private String getExcelStyles() {
        return "<Styles>\n" +
               "<Style ss:ID=\"HeaderStyle\">\n" +
               "<Font ss:Bold=\"1\" ss:Size=\"16\"/>\n" +
               "</Style>\n" +
               "<Style ss:ID=\"SectionHeaderStyle\">\n" +
               "<Font ss:Bold=\"1\" ss:Size=\"12\"/>\n" +
               "<Interior ss:Color=\"#E0E0E0\" ss:Pattern=\"Solid\"/>\n" +
               "</Style>\n" +
               "<Style ss:ID=\"TableHeaderStyle\">\n" +
               "<Font ss:Bold=\"1\"/>\n" +
               "<Interior ss:Color=\"#D0D0D0\" ss:Pattern=\"Solid\"/>\n" +
               "</Style>\n" +
               "<Style ss:ID=\"TableDataStyle\">\n" +
               "<Borders>\n" +
               "<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
               "</Borders>\n" +
               "</Style>\n" +
               "<Style ss:ID=\"TextStyle\">\n" +
               "<Alignment ss:WrapText=\"1\"/>\n" +
               "</Style>\n" +
               "<Style ss:ID=\"TimestampStyle\">\n" +
               "<Font ss:Italic=\"1\" ss:Size=\"10\"/>\n" +
               "</Style>\n" +
               "</Styles>\n";
    }
    
    @Override
    protected String saveReport(String outputPath) throws IOException {
        String finalPath = outputPath.endsWith(".xml") ? outputPath : outputPath + ".xml";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(finalPath))) {
            writer.print(excelContent.toString());
        }
        
        return finalPath;
    }
}

// Concrete implementation for HTML report generation
class HTMLReportGenerator extends ReportGenerator {
    private StringBuilder htmlContent;
    
    @Override
    protected String getFormat() {
        return "HTML";
    }
    
    @Override
    protected void setDefaultConfiguration() {
        super.setDefaultConfiguration();
        setConfiguration("include_css", true);
        setConfiguration("responsive_design", true);
        setConfiguration("theme", "default");
    }
    
    @Override
    protected void initializeReportContext(ReportData data) {
        super.initializeReportContext(data);
        this.htmlContent = new StringBuilder();
        
        htmlContent.append("<!DOCTYPE html>\n");
        htmlContent.append("<html lang=\"en\">\n");
        htmlContent.append("<head>\n");
        htmlContent.append("<meta charset=\"UTF-8\">\n");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        htmlContent.append("<title>").append(escapeHtml(data.getTitle())).append("</title>\n");
        
        if (getConfigurationBoolean("include_css", true)) {
            htmlContent.append(getEmbeddedCSS());
        }
        
        htmlContent.append("</head>\n");
        htmlContent.append("<body>\n");
    }
    
    @Override
    protected void createReportStructure(ReportData data) {
        System.out.println("Creating HTML report structure...");
        
        htmlContent.append("<div class=\"report-container\">\n");
    }
    
    @Override
    protected void generateHeader(ReportData data) {
        System.out.println("Generating HTML header...");
        
        htmlContent.append("<header class=\"report-header\">\n");
        htmlContent.append("<h1>").append(escapeHtml(data.getTitle())).append("</h1>\n");
        
        if (getConfigurationBoolean("include_timestamp", true)) {
            htmlContent.append("<p class=\"timestamp\">Generated: ");
            htmlContent.append(data.getGeneratedAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
            htmlContent.append("</p>\n");
        }
        
        htmlContent.append("</header>\n");
        htmlContent.append("<main class=\"report-content\">\n");
    }
    
    @Override
    protected void processSection(ReportSection section) {
        System.out.println("Processing HTML section: " + section.getTitle());
        
        htmlContent.append("<section class=\"report-section\">\n");
        
        switch (section.getType()) {
            case HEADER:
                processHTMLHeader(section);
                break;
            case TEXT:
                processHTMLText(section);
                break;
            case TABLE:
                processHTMLTable(section);
                break;
            case CHART:
                processHTMLChart(section);
                break;
            case IMAGE:
                processHTMLImage(section);
                break;
            default:
                htmlContent.append("<p>Unsupported section type: ").append(section.getType()).append("</p>\n");
        }
        
        htmlContent.append("</section>\n");
    }
    
    private void processHTMLHeader(ReportSection section) {
        htmlContent.append("<h2>").append(escapeHtml(section.getTitle())).append("</h2>\n");
    }
    
    private void processHTMLText(ReportSection section) {
        htmlContent.append("<h3>").append(escapeHtml(section.getTitle())).append("</h3>\n");
        
        String text = section.getContent().toString();
        String[] paragraphs = text.split("\n\n");
        
        for (String paragraph : paragraphs) {
            htmlContent.append("<p>").append(escapeHtml(paragraph.trim())).append("</p>\n");
        }
    }
    
    private void processHTMLTable(ReportSection section) {
        if (!(section.getContent() instanceof TableData)) {
            return;
        }
        
        TableData table = (TableData) section.getContent();
        
        htmlContent.append("<h3>").append(escapeHtml(section.getTitle())).append("</h3>\n");
        htmlContent.append("<table class=\"data-table\">\n");
        
        // Table headers
        htmlContent.append("<thead>\n<tr>\n");
        for (String header : table.getHeaders()) {
            htmlContent.append("<th>").append(escapeHtml(header)).append("</th>\n");
        }
        htmlContent.append("</tr>\n</thead>\n");
        
        // Table body
        htmlContent.append("<tbody>\n");
        for (List<Object> row : table.getRows()) {
            htmlContent.append("<tr>\n");
            for (Object cell : row) {
                htmlContent.append("<td>").append(escapeHtml(cell.toString())).append("</td>\n");
            }
            htmlContent.append("</tr>\n");
        }
        htmlContent.append("</tbody>\n");
        
        htmlContent.append("</table>\n");
    }
    
    private void processHTMLChart(ReportSection section) {
        if (!(section.getContent() instanceof ChartData)) {
            return;
        }
        
        ChartData chart = (ChartData) section.getContent();
        
        htmlContent.append("<h3>").append(escapeHtml(section.getTitle())).append("</h3>\n");
        htmlContent.append("<div class=\"chart-container\">\n");
        htmlContent.append("<p class=\"chart-type\">Chart Type: ").append(chart.getChartType()).append("</p>\n");
        
        // Simple chart representation as a table
        htmlContent.append("<table class=\"chart-table\">\n");
        htmlContent.append("<thead>\n<tr>\n");
        htmlContent.append("<th>Label</th>\n");
        for (String dataset : chart.getDatasets().keySet()) {
            htmlContent.append("<th>").append(escapeHtml(dataset)).append("</th>\n");
        }
        htmlContent.append("</tr>\n</thead>\n");
        
        htmlContent.append("<tbody>\n");
        for (int i = 0; i < chart.getLabels().size(); i++) {
            htmlContent.append("<tr>\n");
            htmlContent.append("<td>").append(escapeHtml(chart.getLabels().get(i))).append("</td>\n");
            
            for (String dataset : chart.getDatasets().keySet()) {
                List<Number> values = chart.getDatasets().get(dataset);
                if (i < values.size()) {
                    htmlContent.append("<td>").append(values.get(i)).append("</td>\n");
                } else {
                    htmlContent.append("<td>-</td>\n");
                }
            }
            htmlContent.append("</tr>\n");
        }
        htmlContent.append("</tbody>\n");
        htmlContent.append("</table>\n");
        htmlContent.append("</div>\n");
    }
    
    private void processHTMLImage(ReportSection section) {
        htmlContent.append("<h3>").append(escapeHtml(section.getTitle())).append("</h3>\n");
        htmlContent.append("<div class=\"image-placeholder\">\n");
        htmlContent.append("<p>[Image placeholder: ").append(escapeHtml(section.getTitle())).append("]</p>\n");
        htmlContent.append("</div>\n");
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
    
    @Override
    protected void generateFooter(ReportData data) {
        System.out.println("Generating HTML footer...");
        
        htmlContent.append("</main>\n");
        htmlContent.append("<footer class=\"report-footer\">\n");
        htmlContent.append("<div class=\"footer-content\">\n");
        htmlContent.append("<p>Report generated on ");
        htmlContent.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        htmlContent.append("</p>\n");
        htmlContent.append("<p>Total sections: ").append(data.getSections().size()).append("</p>\n");
        htmlContent.append("</div>\n");
        htmlContent.append("</footer>\n");
    }
    
    @Override
    protected void applyFormatting() {
        System.out.println("Applying HTML formatting...");
        
        htmlContent.append("</div>\n"); // Close report-container
        htmlContent.append("</body>\n");
        htmlContent.append("</html>\n");
    }
    
    private String getEmbeddedCSS() {
        return "<style>\n" +
               "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }\n" +
               ".report-container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n" +
               ".report-header { border-bottom: 2px solid #333; padding-bottom: 20px; margin-bottom: 30px; }\n" +
               ".report-header h1 { color: #333; margin: 0 0 10px 0; }\n" +
               ".timestamp { color: #666; font-style: italic; margin: 0; }\n" +
               ".report-section { margin-bottom: 30px; }\n" +
               ".report-section h2 { color: #2c3e50; border-left: 4px solid #3498db; padding-left: 10px; }\n" +
               ".report-section h3 { color: #34495e; margin-bottom: 15px; }\n" +
               ".data-table, .chart-table { width: 100%; border-collapse: collapse; margin: 15px 0; }\n" +
               ".data-table th, .data-table td, .chart-table th, .chart-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n" +
               ".data-table th, .chart-table th { background-color: #f2f2f2; font-weight: bold; }\n" +
               ".data-table tr:nth-child(even), .chart-table tr:nth-child(even) { background-color: #f9f9f9; }\n" +
               ".chart-container { border: 1px solid #ddd; padding: 15px; border-radius: 5px; background-color: #fafafa; }\n" +
               ".chart-type { font-weight: bold; color: #555; margin-bottom: 10px; }\n" +
               ".image-placeholder { border: 2px dashed #ccc; padding: 20px; text-align: center; color: #999; }\n" +
               ".report-footer { border-top: 1px solid #ddd; padding-top: 20px; margin-top: 30px; color: #666; text-align: center; }\n" +
               "@media (max-width: 768px) { .report-container { padding: 10px; } .data-table, .chart-table { font-size: 12px; } }\n" +
               "</style>\n";
    }
    
    @Override
    protected String saveReport(String outputPath) throws IOException {
        String finalPath = outputPath.endsWith(".html") ? outputPath : outputPath + ".html";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(finalPath))) {
            writer.print(htmlContent.toString());
        }
        
        return finalPath;
    }
}

// Example usage and demonstration
public class ReportGeneratorExample {
    
    public static void main(String[] args) {
        System.out.println("Template Method Pattern - Report Generation Example");
        System.out.println("==================================================\n");
        
        // Create sample report data
        ReportData sampleData = createSampleReportData();
        
        // Example 1: Generate PDF Report
        demonstratePDFGeneration(sampleData);
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Example 2: Generate Excel Report
        demonstrateExcelGeneration(sampleData);
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Example 3: Generate HTML Report
        demonstrateHTMLGeneration(sampleData);
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Example 4: Batch Report Generation
        demonstrateBatchGeneration(sampleData);
    }
    
    private static ReportData createSampleReportData() {
        ReportData report = new ReportData("Quarterly Sales Report - Q4 2024");
        
        // Add metadata
        report.addMetadata("author", "Sales Analytics Team");
        report.addMetadata("department", "Sales");
        report.addMetadata("quarter", "Q4 2024");
        
        // Add executive summary section
        ReportSection summary = new ReportSection(
            "Executive Summary", 
            SectionType.TEXT,
            "This report presents the sales performance for Q4 2024. Our team achieved remarkable results with a 15% increase in revenue compared to Q3 2024.\n\nKey highlights include strong performance in the technology sector and successful expansion into new markets."
        );
        report.addSection(summary);
        
        // Add sales data table
        TableData salesTable = new TableData(Arrays.asList("Region", "Q3 Sales", "Q4 Sales", "Growth %"));
        salesTable.addRow(Arrays.asList("North America", "$1,200,000", "$1,380,000", "15%"));
        salesTable.addRow(Arrays.asList("Europe", "$950,000", "$1,045,000", "10%"));
        salesTable.addRow(Arrays.asList("Asia Pacific", "$800,000", "$960,000", "20%"));
        salesTable.addRow(Arrays.asList("Latin America", "$450,000", "$495,000", "10%"));
        
        ReportSection salesSection = new ReportSection("Regional Sales Performance", SectionType.TABLE, salesTable);
        report.addSection(salesSection);
        
        // Add chart data
        ChartData chartData = new ChartData("Bar Chart");
        chartData.setLabels(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun"));
        chartData.addDataset("Revenue", Arrays.asList(100000, 120000, 110000, 140000, 160000, 180000));
        chartData.addDataset("Profit", Arrays.asList(20000, 25000, 22000, 30000, 35000, 40000));
        
        ReportSection chartSection = new ReportSection("Monthly Trend Analysis", SectionType.CHART, chartData);
        report.addSection(chartSection);
        
        // Add recommendations section
        ReportSection recommendations = new ReportSection(
            "Recommendations",
            SectionType.TEXT,
            "Based on the analysis, we recommend:\n\n1. Continue investment in Asia Pacific market\n2. Develop new product lines for European market\n3. Strengthen partnerships in North America\n4. Explore opportunities in emerging markets"
        );
        report.addSection(recommendations);
        
        return report;
    }
    
    private static void demonstratePDFGeneration(ReportData data) {
        System.out.println("1. Generating PDF Report");
        System.out.println("========================");
        
        ReportGenerator pdfGenerator = new PDFReportGenerator();
        GeneratedReport result = pdfGenerator.generateReport(data, "quarterly_sales_report");
        
        printGenerationResult("PDF", result);
    }
    
    private static void demonstrateExcelGeneration(ReportData data) {
        System.out.println("2. Generating Excel Report");
        System.out.println("==========================");
        
        ReportGenerator excelGenerator = new ExcelReportGenerator();
        GeneratedReport result = excelGenerator.generateReport(data, "quarterly_sales_report");
        
        printGenerationResult("Excel", result);
    }
    
    private static void demonstrateHTMLGeneration(ReportData data) {
        System.out.println("3. Generating HTML Report");
        System.out.println("=========================");
        
        ReportGenerator htmlGenerator = new HTMLReportGenerator();
        GeneratedReport result = htmlGenerator.generateReport(data, "quarterly_sales_report");
        
        printGenerationResult("HTML", result);
    }
    
    private static void demonstrateBatchGeneration(ReportData data) {
        System.out.println("4. Batch Report Generation");
        System.out.println("==========================");
        
        List<ReportGenerator> generators = Arrays.asList(
            new PDFReportGenerator(),
            new ExcelReportGenerator(),
            new HTMLReportGenerator()
        );
        
        List<String> outputNames = Arrays.asList(
            "batch_pdf_report",
            "batch_excel_report", 
            "batch_html_report"
        );
        
        List<GeneratedReport> results = new ArrayList<>();
        
        for (int i = 0; i < generators.size(); i++) {
            System.out.println("\nGenerating " + generators.get(i).getFormat() + " report...");
            GeneratedReport result = generators.get(i).generateReport(data, outputNames.get(i));
            results.add(result);
        }
        
        System.out.println("\n=== Batch Generation Summary ===");
        for (GeneratedReport result : results) {
            System.out.println("• " + result.getFormat() + " Report: " + 
                             (result.isSuccessful() ? "SUCCESS" : "FAILED"));
            if (result.isSuccessful()) {
                System.out.println("  File: " + result.getFilePath());
                System.out.println("  Size: " + result.getFileSizeBytes() + " bytes");
            } else {
                System.out.println("  Error: " + result.getMetadata().get("error"));
            }
        }
        
        // Calculate summary statistics
        long totalSize = results.stream()
            .filter(GeneratedReport::isSuccessful)
            .mapToLong(GeneratedReport::getFileSizeBytes)
            .sum();
        long successCount = results.stream()
            .mapToLong(r -> r.isSuccessful() ? 1 : 0)
            .sum();
            
        System.out.println("\nSummary:");
        System.out.println("Successful generations: " + successCount + "/" + results.size());
        System.out.println("Total file size: " + totalSize + " bytes");
    }
    
    private static void printGenerationResult(String format, GeneratedReport result) {
        System.out.println("\n" + format + " Generation Result:");
        System.out.println("Success: " + result.isSuccessful());
        
        if (result.isSuccessful()) {
            System.out.println("File path: " + result.getFilePath());
            System.out.println("File size: " + result.getFileSizeBytes() + " bytes");
            System.out.println("Generated at: " + result.getGeneratedAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
            
            if (!result.getMetadata().isEmpty()) {
                System.out.println("Metadata:");
                result.getMetadata().entrySet().stream()
                    .filter(entry -> !"error".equals(entry.getKey()))
                    .forEach(entry -> 
                        System.out.println("  • " + entry.getKey() + ": " + entry.getValue()));
            }
        } else {
            System.out.println("Error: " + result.getMetadata().get("error"));
        }
    }
}