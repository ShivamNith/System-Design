package org.example.designPatterns.factoryPattern.problems;

/*
## Problem 1: Document Processor Factory (Basic)
**Difficulty:** ⭐⭐☆☆☆
**Time:** 30-45 minutes
**Focus:** Learning Factory Pattern basics

### Learning Objective
Learn how the Factory Pattern centralizes object creation logic by building a **simplified** document system. You're NOT building a real document processor - just simulating one to understand the pattern.

### Problem Statement
Create a simple system that can create different types of "document" objects. Each document type will just store text in a format-appropriate way (you don't need to generate actual PDF/Word files).

**Document Types to Simulate:**
- **PDF**: Pretend to be a PDF (just add "PDF Header" to content)
- **Word**: Pretend to be Word (just add XML-like tags)
- **Excel**: Pretend to be Excel (organize content as rows/columns)
- **HTML**: Add basic HTML tags to content
- **Markdown**: Add markdown formatting symbols

### Requirements
1. **Create a `Document` interface** with these methods:
   ```java
   void create(String content);        // Initialize with content
   void addSection(String title, String content); // Add a section
   void save(String filename);         // Just print "Saving to filename"
   String getFileExtension();          // Return ".pdf", ".docx", etc.
   ```

2. **Implement concrete classes** (keep them SIMPLE):
   ```java
   public class PDFDocument implements Document {
       private StringBuilder content = new StringBuilder();

       public void addSection(String title, String content) {
           // Just concatenate - don't worry about real PDF format
           this.content.append("==" + title + "==\n" + content + "\n");
       }

       public void save(String filename) {
           System.out.println("Saving PDF to " + filename);
           // Don't actually write files
       }
   }
   ```

3. **Create a Simple Factory**:
   ```java
   public class DocumentFactory {
       public static Document createDocument(String type) {
           switch(type.toUpperCase()) {
               case "PDF": return new PDFDocument();
               case "WORD": return new WordDocument();
               // etc...
           }
       }
   }
   ```

4. **Basic conversion** (just copy content):
   ```java
   public static Document convertDocument(Document source, String targetType) {
       Document target = createDocument(targetType);
       // Just copy the content, don't worry about format translation
       target.create(source.getContent());
       return target;
   }
   ```

5. **Simple templates** (optional bonus):
   ```java
   public static Document createResume(String format) {
       Document doc = createDocument(format);
       doc.addSection("Name", "Your Name");
       doc.addSection("Experience", "Your Experience");
       return doc;
   }
   ```

### What You DON'T Need to Do
❌ Generate real PDF binary format
❌ Create actual Word XML structure
❌ Write actual files to disk
❌ Implement real Excel formulas
❌ Handle complex conversions
❌ Add styling or formatting

### Test Your Implementation
```java
public static void main(String[] args) {
    // Test factory creation
    Document pdf = DocumentFactory.createDocument("PDF");
    pdf.addSection("Intro", "This is my content");
    pdf.save("test.pdf");  // Should just print, not save

    // Test conversion
    Document html = DocumentFactory.convertDocument(pdf, "HTML");

    // Verify it works
    System.out.println("PDF extension: " + pdf.getFileExtension());
    System.out.println("Created documents successfully!");
}
```

### Success Criteria
✅ Factory hides object creation logic
✅ Client code doesn't use 'new' for documents
✅ Easy to add new document types
✅ All documents share common interface
✅ Code is simple and focused on the pattern

### Hints
- Keep it SIMPLE - this is about learning the pattern, not building real documents
- Each document class can be 20-30 lines max
- Focus on how the factory decides which class to instantiate
- Don't spend time on actual file format details

---
* */


class Section{
    private String title;
    private String content;

    public Section(String title, String content){
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString(){
        return "Title:" + title + "\nContent:" + content;
    }
}

interface Document {
    void create(String content);        // Initialize with content
    void addSection(String title, String content); // Add a section
    String getFileExtension();          // Return ".pdf", ".docx", etc.
    public StringBuilder getContent();
    default void save(String fileName) {
        System.out.println("Saving to " + fileName);
    }
}

class PdfDocument implements Document{

    private StringBuilder content;
    private final String fileExtension;

    public PdfDocument(){
        fileExtension = ".pdf";
        this.content = new StringBuilder();
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void create(String content) {
        this.content = new StringBuilder(content);
    }

    @Override
    public void addSection(String title, String content) {
        Section section = new Section(title,content);
        this.content.append(section);
    }

    @Override
    public StringBuilder getContent(){
        return content;
    }
}

class HtmlDocument implements Document{
    private StringBuilder content;
    private final String fileExtension;

    public HtmlDocument(){
        this.fileExtension = ".html";
        this.content = new StringBuilder();
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void create(String content) {
        this.content = new StringBuilder(content);
    }

    @Override
    public void addSection(String title, String content) {
        Section section = new Section(title,content);
        this.content.append(section);
    }

    @Override
    public StringBuilder getContent(){
        return content;
    }
}

class WordDocument implements Document{
    private StringBuilder content;
    private final String fileExtension;

    public WordDocument(){
        fileExtension = ".docx";
        this.content = new StringBuilder();
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void create(String content) {
        this.content = new StringBuilder(content);
    }

    @Override
    public void addSection(String title, String content) {
        Section section = new Section(title,content);
        this.content.append(section);
    }

    @Override
    public StringBuilder getContent(){
        return content;
    }
}

class ExcelDocument implements Document{
    private StringBuilder content;
    private final String fileExtension;

    public ExcelDocument(){
        this.fileExtension = ".xlsx";
        this.content = new StringBuilder();
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void create(String content) {
        this.content = new StringBuilder(content);
    }

    @Override
    public void addSection(String title, String content) {
        Section section = new Section(title,content);
        this.content.append(section);
    }

    @Override
    public StringBuilder getContent(){
        return content;
    }
}

class MarkDownDocument implements Document{
    private StringBuilder content;
    private final String fileExtension;

    public MarkDownDocument(){
        this.fileExtension = ".md";
        this.content = new StringBuilder();
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void create(String content) {
        this.content = new StringBuilder(content);
    }

    @Override
    public void addSection(String title, String content) {
        Section section = new Section(title,content);
        this.content.append(section);
    }

    @Override
    public StringBuilder getContent(){
        return content;
    }
}


public class DocumentFactory {

    private enum DocumentType {
        PDF,WORD,EXCEL,HTML,MARKDOWN
    }

    public static Document createDocument(String type) throws IllegalArgumentException {
        DocumentType docType = DocumentType.valueOf(type);
        return switch (docType) {
            case DocumentType.PDF -> new PdfDocument();
            case DocumentType.HTML -> new HtmlDocument();
            case DocumentType.WORD -> new WordDocument();
            case DocumentType.EXCEL -> new ExcelDocument();
            case DocumentType.MARKDOWN -> new MarkDownDocument();
            case null -> throw new IllegalArgumentException("invalid document type: " + type);
        };
    }

    public static Document convertDocument(Document source, String targetType){
        Document document = createDocument(targetType);
        document.create(source.getContent().toString());
        return document;
    }

    public static Document createResume(String format) {
        Document doc = createDocument(format);
        doc.addSection("Name", "Your Name");
        doc.addSection("Experience", "Your Experience");
        return doc;
    }

}
