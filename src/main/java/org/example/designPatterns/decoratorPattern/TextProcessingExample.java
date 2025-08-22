package org.example.designPatterns.decoratorPattern;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Text Processing Pipeline Decorator Pattern Example
 * 
 * This example demonstrates the Decorator Pattern using a text processing system.
 * Base processors handle different text formats, and decorators add various
 * processing capabilities like formatting, validation, caching, etc.
 * 
 * Features:
 * - Multiple base text processors
 * - Chain of processing decorators
 * - Statistics and performance monitoring
 * - Flexible pipeline configuration
 * - Error handling and validation
 */

// Component interface
interface TextProcessor {
    String process(String text);
    String getDescription();
    Map<String, Object> getStatistics();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    double getProcessingTime(); // in milliseconds
}

// Base implementation
abstract class BaseTextProcessor implements TextProcessor {
    protected boolean enabled;
    protected Map<String, Object> statistics;
    protected String name;
    protected long lastProcessingTime;
    
    public BaseTextProcessor(String name) {
        this.name = name;
        this.enabled = true;
        this.statistics = new HashMap<>();
        this.lastProcessingTime = 0;
        initializeStatistics();
    }
    
    protected void initializeStatistics() {
        updateStatistics("characters_processed", 0);
        updateStatistics("texts_processed", 0);
        updateStatistics("total_processing_time", 0L);
        updateStatistics("average_processing_time", 0.0);
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        System.out.println((enabled ? "✅ Enabled" : "❌ Disabled") + " processor: " + name);
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        return new HashMap<>(statistics);
    }
    
    @Override
    public double getProcessingTime() {
        return lastProcessingTime;
    }
    
    protected void updateStatistics(String key, Object value) {
        statistics.put(key, value);
    }
    
    protected void recordProcessingTime(long startTime, String text) {
        lastProcessingTime = System.currentTimeMillis() - startTime;
        
        // Update cumulative statistics
        int textsProcessed = (Integer) statistics.get("texts_processed") + 1;
        int charsProcessed = (Integer) statistics.get("characters_processed") + text.length();
        long totalTime = (Long) statistics.get("total_processing_time") + lastProcessingTime;
        double avgTime = (double) totalTime / textsProcessed;
        
        updateStatistics("texts_processed", textsProcessed);
        updateStatistics("characters_processed", charsProcessed);
        updateStatistics("total_processing_time", totalTime);
        updateStatistics("average_processing_time", avgTime);
        updateStatistics("last_processing_time", lastProcessingTime);
    }
}

// Concrete components
class PlainTextProcessor extends BaseTextProcessor {
    private String encoding;
    private int maxLength;
    
    public PlainTextProcessor() {
        super("Plain Text Processor");
        this.encoding = "UTF-8";
        this.maxLength = Integer.MAX_VALUE;
        updateStatistics("encoding", encoding);
    }
    
    @Override
    public String process(String text) {
        if (!enabled) return text;
        
        long startTime = System.currentTimeMillis();
        
        if (text == null) {
            text = "";
        }
        
        // Apply length limit
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength) + "...";
            updateStatistics("texts_truncated", 
                (Integer) statistics.getOrDefault("texts_truncated", 0) + 1);
        }
        
        // Basic text validation and normalization
        text = text.trim();
        
        recordProcessingTime(startTime, text);
        return text;
    }
    
    @Override
    public String getDescription() {
        return name + " (encoding: " + encoding + ", max length: " + 
               (maxLength == Integer.MAX_VALUE ? "unlimited" : maxLength + " chars") + ")";
    }
    
    public void setMaxLength(int maxLength) {
        this.maxLength = Math.max(0, maxLength);
        updateStatistics("max_length", maxLength);
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        updateStatistics("encoding", encoding);
    }
}

class MarkdownProcessor extends BaseTextProcessor {
    private boolean enableExtensions;
    private Set<String> enabledExtensions;
    
    public MarkdownProcessor() {
        super("Markdown Processor");
        this.enableExtensions = true;
        this.enabledExtensions = new HashSet<>(Arrays.asList(
            "tables", "strikethrough", "autolinks", "task-lists", "code-blocks"
        ));
        updateStatistics("extensions_enabled", enabledExtensions.size());
    }
    
    @Override
    public String process(String text) {
        if (!enabled) return text;
        
        long startTime = System.currentTimeMillis();
        
        if (text == null) return "";
        
        String processed = text;
        int elementsProcessed = 0;
        
        // Process headers (# ## ### etc.)
        processed = processHeaders(processed);
        elementsProcessed += countMatches(text, "^#{1,6}\\s+.+$");
        
        // Process bold text (**text** or __text__)
        processed = processed.replaceAll("\\*\\*([^*]+)\\*\\*", "<strong>$1</strong>");
        processed = processed.replaceAll("__([^_]+)__", "<strong>$1</strong>");
        elementsProcessed += countMatches(text, "\\*\\*[^*]+\\*\\*") + countMatches(text, "__[^_]+__");
        
        // Process italic text (*text* or _text_)
        processed = processed.replaceAll("(?<!\\*)\\*([^*]+)\\*(?!\\*)", "<em>$1</em>");
        processed = processed.replaceAll("(?<!_)_([^_]+)_(?!_)", "<em>$1</em>");
        elementsProcessed += countMatches(text, "(?<!\\*)\\*[^*]+\\*(?!\\*)") + 
                            countMatches(text, "(?<!_)_[^_]+_(?!_)");
        
        // Process links [text](url)
        processed = processed.replaceAll("\\[([^\\]]+)\\]\\(([^)]+)\\)", "<a href=\"$2\">$1</a>");
        elementsProcessed += countMatches(text, "\\[([^\\]]+)\\]\\(([^)]+)\\)");
        
        // Process inline code `code`
        processed = processed.replaceAll("`([^`]+)`", "<code>$1</code>");
        elementsProcessed += countMatches(text, "`[^`]+`");
        
        if (enableExtensions) {
            elementsProcessed += processExtensions(processed, text);
        }
        
        updateStatistics("markdown_elements_processed", 
            (Integer) statistics.getOrDefault("markdown_elements_processed", 0) + elementsProcessed);
        
        recordProcessingTime(startTime, text);
        return processed;
    }
    
    private String processHeaders(String text) {
        String[] lines = text.split("\\n");
        StringBuilder result = new StringBuilder();
        
        for (String line : lines) {
            if (line.matches("^#{1,6}\\s+.+$")) {
                int level = 0;
                while (level < line.length() && line.charAt(level) == '#') {
                    level++;
                }
                String content = line.substring(level).trim();
                result.append("<h").append(level).append(">").append(content)
                      .append("</h").append(level).append(">");
            } else {
                result.append(line);
            }
            result.append("\\n");
        }
        return result.toString();
    }
    
    private int processExtensions(String processed, String original) {
        int extensionsProcessed = 0;
        
        if (enabledExtensions.contains("strikethrough")) {
            extensionsProcessed += countMatches(original, "~~[^~]+~~");
        }
        
        if (enabledExtensions.contains("task-lists")) {
            extensionsProcessed += countMatches(original, "^\\s*- \\[[x ]\\] .+$");
        }
        
        if (enabledExtensions.contains("tables")) {
            extensionsProcessed += countMatches(original, "\\|.*\\|");
        }
        
        if (enabledExtensions.contains("autolinks")) {
            extensionsProcessed += countMatches(original, "https?://[^\\s]+");
        }
        
        return extensionsProcessed;
    }
    
    @Override
    public String getDescription() {
        return name + " (extensions: " + (enableExtensions ? 
            enabledExtensions.size() + " enabled" : "disabled") + ")";
    }
    
    private int countMatches(String text, String regex) {
        return (int) Pattern.compile(regex, Pattern.MULTILINE).matcher(text).results().count();
    }
    
    public void setEnableExtensions(boolean enableExtensions) {
        this.enableExtensions = enableExtensions;
        updateStatistics("extensions_enabled", enableExtensions ? enabledExtensions.size() : 0);
    }
    
    public void addExtension(String extension) {
        enabledExtensions.add(extension);
        updateStatistics("extensions_enabled", enabledExtensions.size());
    }
    
    public void removeExtension(String extension) {
        enabledExtensions.remove(extension);
        updateStatistics("extensions_enabled", enabledExtensions.size());
    }
}

class HTMLProcessor extends BaseTextProcessor {
    private boolean preserveFormatting;
    private boolean allowUnsafeTags;
    private Set<String> allowedTags;
    
    public HTMLProcessor() {
        super("HTML Processor");
        this.preserveFormatting = true;
        this.allowUnsafeTags = false;
        this.allowedTags = new HashSet<>(Arrays.asList(
            "p", "div", "span", "strong", "em", "h1", "h2", "h3", "h4", "h5", "h6",
            "ul", "ol", "li", "a", "img", "br", "hr"
        ));
        updateStatistics("allowed_tags", allowedTags.size());
    }
    
    @Override
    public String process(String text) {
        if (!enabled) return text;
        
        long startTime = System.currentTimeMillis();
        
        if (text == null) return "";
        
        String processed = text;
        int tagsProcessed = 0;
        
        if (!allowUnsafeTags) {
            // Remove or escape unsafe tags
            processed = sanitizeHTML(processed);
            tagsProcessed += countMatches(text, "<[^>]+>");
        }
        
        if (!preserveFormatting) {
            // Normalize whitespace
            processed = processed.replaceAll("\\s+", " ").trim();
        }
        
        // Count and validate HTML structure
        int openTags = countMatches(processed, "<(?!/)[^>]*>");
        int closeTags = countMatches(processed, "</[^>]*>");
        
        updateStatistics("html_tags_processed", 
            (Integer) statistics.getOrDefault("html_tags_processed", 0) + tagsProcessed);
        updateStatistics("open_tags", openTags);
        updateStatistics("close_tags", closeTags);
        updateStatistics("balanced_tags", openTags == closeTags);
        
        recordProcessingTime(startTime, text);
        return processed;
    }
    
    private String sanitizeHTML(String html) {
        // Simple HTML sanitization (in reality, would use a proper library)
        String sanitized = html;
        
        // Remove script tags completely
        sanitized = sanitized.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        sanitized = sanitized.replaceAll("(?i)<style[^>]*>.*?</style>", "");
        
        // Remove unsafe attributes
        sanitized = sanitized.replaceAll("(?i)\\s*(javascript|vbscript|onload|onclick|onerror)\\s*=\\s*[\"'][^\"']*[\"']", "");
        
        return sanitized;
    }
    
    @Override
    public String getDescription() {
        return name + " (safe: " + !allowUnsafeTags + ", formatting: " + 
               (preserveFormatting ? "preserved" : "normalized") + ")";
    }
    
    private int countMatches(String text, String regex) {
        return (int) Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).results().count();
    }
    
    public void setPreserveFormatting(boolean preserveFormatting) {
        this.preserveFormatting = preserveFormatting;
    }
    
    public void setAllowUnsafeTags(boolean allowUnsafeTags) {
        this.allowUnsafeTags = allowUnsafeTags;
    }
}

// Abstract decorator
abstract class TextProcessorDecorator implements TextProcessor {
    protected TextProcessor processor;
    
    public TextProcessorDecorator(TextProcessor processor) {
        this.processor = processor;
    }
    
    @Override
    public String process(String text) {
        return processor.process(text);
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription();
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        return processor.getStatistics();
    }
    
    @Override
    public boolean isEnabled() {
        return processor.isEnabled();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        processor.setEnabled(enabled);
    }
    
    @Override
    public double getProcessingTime() {
        return processor.getProcessingTime();
    }
}

// Concrete decorators
class UpperCaseDecorator extends TextProcessorDecorator {
    private boolean preserveWhitespace;
    private boolean preserveHtml;
    
    public UpperCaseDecorator(TextProcessor processor, boolean preserveWhitespace, boolean preserveHtml) {
        super(processor);
        this.preserveWhitespace = preserveWhitespace;
        this.preserveHtml = preserveHtml;
    }
    
    public UpperCaseDecorator(TextProcessor processor) {
        this(processor, true, true);
    }
    
    @Override
    public String process(String text) {
        String processed = super.process(text);
        
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            
            if (preserveHtml && processed.contains("<")) {
                // Only uppercase text outside HTML tags
                processed = uppercasePreservingHtml(processed);
            } else {
                processed = processed.toUpperCase();
                
                if (!preserveWhitespace) {
                    processed = processed.replaceAll("\\s+", " ");
                }
            }
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("uppercase_applied", true);
            stats.put("uppercase_processing_time", System.currentTimeMillis() - startTime);
        }
        
        return processed;
    }
    
    private String uppercasePreservingHtml(String text) {
        StringBuilder result = new StringBuilder();
        boolean insideTag = false;
        
        for (char c : text.toCharArray()) {
            if (c == '<') {
                insideTag = true;
                result.append(c);
            } else if (c == '>') {
                insideTag = false;
                result.append(c);
            } else if (insideTag) {
                result.append(c); // Keep original case inside tags
            } else {
                result.append(Character.toUpperCase(c));
            }
        }
        
        return result.toString();
    }
    
    @Override
    public String getDescription() {
        String options = "preserve HTML: " + preserveHtml + ", preserve whitespace: " + preserveWhitespace;
        return processor.getDescription() + " + UpperCase (" + options + ")";
    }
}

class TrimDecorator extends TextProcessorDecorator {
    private TrimMode trimMode;
    
    public enum TrimMode {
        LEADING("Leading whitespace"),
        TRAILING("Trailing whitespace"),
        BOTH("Leading and trailing whitespace"),
        ALL_EXTRA("All extra whitespace"),
        NORMALIZE_LINES("Normalize line breaks");
        
        private final String description;
        
        TrimMode(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    public TrimDecorator(TextProcessor processor, TrimMode trimMode) {
        super(processor);
        this.trimMode = trimMode;
    }
    
    public TrimDecorator(TextProcessor processor) {
        this(processor, TrimMode.BOTH);
    }
    
    @Override
    public String process(String text) {
        String processed = super.process(text);
        
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            String original = processed;
            
            switch (trimMode) {
                case LEADING:
                    processed = processed.replaceAll("^\\s+", "");
                    break;
                case TRAILING:
                    processed = processed.replaceAll("\\s+$", "");
                    break;
                case BOTH:
                    processed = processed.trim();
                    break;
                case ALL_EXTRA:
                    processed = processed.trim().replaceAll("\\s+", " ");
                    break;
                case NORMALIZE_LINES:
                    processed = processed.replaceAll("\\r\\n|\\r", "\\n")
                                       .replaceAll("\\n{3,}", "\\n\\n")
                                       .trim();
                    break;
            }
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("trim_mode", trimMode.name());
            stats.put("characters_trimmed", original.length() - processed.length());
            stats.put("trim_processing_time", System.currentTimeMillis() - startTime);
        }
        
        return processed;
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + Trim (" + trimMode.getDescription() + ")";
    }
}

class FilterDecorator extends TextProcessorDecorator {
    private Set<String> badWords;
    private String replacement;
    private boolean caseSensitive;
    private int wordsFiltered;
    private FilterMode filterMode;
    
    public enum FilterMode {
        REPLACE("Replace with placeholder"),
        REMOVE("Remove completely"),
        ASTERISK("Replace with asterisks"),
        PARTIAL("Partial masking (first and last char)");
        
        private final String description;
        
        FilterMode(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    public FilterDecorator(TextProcessor processor, Set<String> badWords, String replacement, FilterMode mode) {
        super(processor);
        this.badWords = new HashSet<>(badWords);
        this.replacement = replacement;
        this.caseSensitive = false;
        this.wordsFiltered = 0;
        this.filterMode = mode;
    }
    
    public FilterDecorator(TextProcessor processor) {
        this(processor, 
             new HashSet<>(Arrays.asList("spam", "offensive", "inappropriate", "badword")), 
             "***", FilterMode.REPLACE);
    }
    
    @Override
    public String process(String text) {
        String processed = super.process(text);
        
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            int initialWordsFiltered = wordsFiltered;
            
            for (String badWord : badWords) {
                String pattern = caseSensitive ? "\\b" + Pattern.quote(badWord) + "\\b" : 
                                               "(?i)\\b" + Pattern.quote(badWord) + "\\b";
                
                String before = processed;
                
                switch (filterMode) {
                    case REPLACE:
                        processed = processed.replaceAll(pattern, replacement);
                        break;
                    case REMOVE:
                        processed = processed.replaceAll(pattern, "");
                        break;
                    case ASTERISK:
                        processed = processed.replaceAll(pattern, "*".repeat(badWord.length()));
                        break;
                    case PARTIAL:
                        processed = processed.replaceAll(pattern, 
                            badWord.length() > 2 ? 
                            badWord.charAt(0) + "*".repeat(badWord.length() - 2) + badWord.charAt(badWord.length() - 1) :
                            "*".repeat(badWord.length()));
                        break;
                }
                
                // Count replacements
                int beforeLength = before.length();
                int afterLength = processed.length();
                if (beforeLength != afterLength || !before.equals(processed)) {
                    wordsFiltered++;
                }
            }
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("words_filtered_total", wordsFiltered);
            stats.put("words_filtered_this_pass", wordsFiltered - initialWordsFiltered);
            stats.put("filter_mode", filterMode.name());
            stats.put("filter_processing_time", System.currentTimeMillis() - startTime);
        }
        
        return processed;
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + Filter (" + badWords.size() + " words, " + 
               filterMode.getDescription() + ")";
    }
    
    public void addBadWord(String word) {
        badWords.add(word);
    }
    
    public void removeBadWord(String word) {
        badWords.remove(word);
    }
    
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    public int getWordsFiltered() { return wordsFiltered; }
    public FilterMode getFilterMode() { return filterMode; }
}

class ValidationDecorator extends TextProcessorDecorator {
    private int minLength;
    private int maxLength;
    private Pattern allowedPattern;
    private Pattern forbiddenPattern;
    private boolean throwOnFailure;
    private ValidationMode validationMode;
    
    public enum ValidationMode {
        STRICT("Fail on any violation"),
        LENIENT("Warn on violations"),
        CORRECTIVE("Attempt to fix violations");
        
        private final String description;
        
        ValidationMode(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    public ValidationDecorator(TextProcessor processor, int minLength, int maxLength, ValidationMode mode) {
        super(processor);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.allowedPattern = null;
        this.forbiddenPattern = null;
        this.throwOnFailure = (mode == ValidationMode.STRICT);
        this.validationMode = mode;
    }
    
    public ValidationDecorator(TextProcessor processor) {
        this(processor, 0, Integer.MAX_VALUE, ValidationMode.LENIENT);
    }
    
    @Override
    public String process(String text) {
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            
            // Validate before processing
            ValidationResult validation = validate(text);
            
            if (!validation.isValid()) {
                Map<String, Object> stats = new HashMap<>(processor.getStatistics());
                stats.put("validation_failed", true);
                stats.put("validation_errors", validation.getErrors());
                stats.put("validation_mode", validationMode.name());
                
                switch (validationMode) {
                    case STRICT:
                        throw new IllegalArgumentException("Validation failed: " + validation.getErrors());
                    case LENIENT:
                        System.out.println("⚠️ Validation warnings: " + validation.getErrors());
                        break;
                    case CORRECTIVE:
                        System.out.println("🔧 Attempting to fix validation issues...");
                        text = attemptFix(text, validation);
                        break;
                }
            }
            
            String processed = super.process(text);
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("validation_passed", validation.isValid());
            stats.put("validation_processing_time", System.currentTimeMillis() - startTime);
            
            return processed;
        }
        
        return super.process(text);
    }
    
    private ValidationResult validate(String text) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (text == null) {
            errors.add("Text cannot be null");
            return new ValidationResult(false, errors, warnings);
        }
        
        if (text.length() < minLength) {
            errors.add("Text too short (minimum: " + minLength + " characters, actual: " + text.length() + ")");
        }
        
        if (text.length() > maxLength) {
            errors.add("Text too long (maximum: " + maxLength + " characters, actual: " + text.length() + ")");
        }
        
        if (allowedPattern != null && !allowedPattern.matcher(text).matches()) {
            errors.add("Text does not match required pattern");
        }
        
        if (forbiddenPattern != null && forbiddenPattern.matcher(text).find()) {
            warnings.add("Text contains forbidden pattern");
        }
        
        // Check for potential encoding issues
        if (text.contains("�")) {
            warnings.add("Text may contain encoding issues");
        }
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    private String attemptFix(String text, ValidationResult validation) {
        String fixed = text;
        
        // Attempt to fix length issues
        if (text.length() > maxLength) {
            fixed = text.substring(0, maxLength - 3) + "...";
            System.out.println("✂️ Truncated text to maximum length");
        }
        
        if (text.length() < minLength) {
            int padding = minLength - text.length();
            fixed = text + " ".repeat(padding);
            System.out.println("➕ Padded text to minimum length");
        }
        
        // Remove forbidden patterns
        if (forbiddenPattern != null) {
            String beforeFix = fixed;
            fixed = forbiddenPattern.matcher(fixed).replaceAll("[FILTERED]");
            if (!beforeFix.equals(fixed)) {
                System.out.println("🚫 Removed forbidden patterns");
            }
        }
        
        return fixed;
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + Validation (" + minLength + "-" + maxLength + " chars, " + 
               validationMode.getDescription() + ")";
    }
    
    public void setAllowedPattern(String regex) {
        this.allowedPattern = Pattern.compile(regex);
    }
    
    public void setForbiddenPattern(String regex) {
        this.forbiddenPattern = Pattern.compile(regex);
    }
    
    public void setValidationMode(ValidationMode mode) {
        this.validationMode = mode;
        this.throwOnFailure = (mode == ValidationMode.STRICT);
    }
    
    private static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        
        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
    }
}

class CacheDecorator extends TextProcessorDecorator {
    private Map<String, CacheEntry> cache;
    private int maxCacheSize;
    private long cacheExpiryMs;
    private int cacheHits;
    private int cacheMisses;
    private CacheStrategy strategy;
    
    public enum CacheStrategy {
        LRU("Least Recently Used"),
        LFU("Least Frequently Used"),
        FIFO("First In, First Out");
        
        private final String description;
        
        CacheStrategy(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    private static class CacheEntry {
        private final String result;
        private final long timestamp;
        private final String inputHash;
        private int accessCount;
        private long lastAccessed;
        
        public CacheEntry(String result, String inputHash) {
            this.result = result;
            this.inputHash = inputHash;
            this.timestamp = System.currentTimeMillis();
            this.lastAccessed = timestamp;
            this.accessCount = 1;
        }
        
        public String getResult() { 
            lastAccessed = System.currentTimeMillis();
            accessCount++;
            return result; 
        }
        
        public boolean isExpired(long expiryMs) {
            return System.currentTimeMillis() - timestamp > expiryMs;
        }
        
        public long getLastAccessed() { return lastAccessed; }
        public int getAccessCount() { return accessCount; }
        public String getInputHash() { return inputHash; }
    }
    
    public CacheDecorator(TextProcessor processor, int maxCacheSize, long cacheExpiryMs, CacheStrategy strategy) {
        super(processor);
        this.cache = new LinkedHashMap<String, CacheEntry>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                return size() > maxCacheSize;
            }
        };
        this.maxCacheSize = maxCacheSize;
        this.cacheExpiryMs = cacheExpiryMs;
        this.cacheHits = 0;
        this.cacheMisses = 0;
        this.strategy = strategy;
    }
    
    public CacheDecorator(TextProcessor processor) {
        this(processor, 100, 5 * 60 * 1000, CacheStrategy.LRU); // 100 entries, 5 minutes expiry
    }
    
    @Override
    public String process(String text) {
        if (!processor.isEnabled()) {
            return super.process(text);
        }
        
        String cacheKey = generateCacheKey(text);
        CacheEntry entry = cache.get(cacheKey);
        
        if (entry != null && !entry.isExpired(cacheExpiryMs)) {
            cacheHits++;
            System.out.println("💾 Cache hit! (hits: " + cacheHits + ", misses: " + cacheMisses + ")");
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("cache_hits", cacheHits);
            stats.put("cache_misses", cacheMisses);
            stats.put("cache_hit_ratio", (double) cacheHits / (cacheHits + cacheMisses));
            stats.put("cache_size", cache.size());
            
            return entry.getResult();
        }
        
        // Cache miss - process and cache result
        cacheMisses++;
        long startTime = System.currentTimeMillis();
        
        String result = super.process(text);
        cache.put(cacheKey, new CacheEntry(result, Integer.toString(text.hashCode())));
        
        System.out.println("🔄 Cache miss - processed and cached (hits: " + cacheHits + ", misses: " + cacheMisses + ")");
        
        // Apply cache strategy if needed
        if (cache.size() > maxCacheSize) {
            applyEvictionStrategy();
        }
        
        // Update statistics
        Map<String, Object> stats = new HashMap<>(processor.getStatistics());
        stats.put("cache_hits", cacheHits);
        stats.put("cache_misses", cacheMisses);
        stats.put("cache_hit_ratio", (double) cacheHits / (cacheHits + cacheMisses));
        stats.put("cache_size", cache.size());
        stats.put("cache_processing_time", System.currentTimeMillis() - startTime);
        
        return result;
    }
    
    private void applyEvictionStrategy() {
        if (strategy == CacheStrategy.LFU) {
            // Remove least frequently used
            String leastUsedKey = cache.entrySet().stream()
                .min(Map.Entry.comparingByValue((e1, e2) -> 
                    Integer.compare(e1.getAccessCount(), e2.getAccessCount())))
                .map(Map.Entry::getKey)
                .orElse(null);
            
            if (leastUsedKey != null) {
                cache.remove(leastUsedKey);
            }
        }
        // LRU and FIFO are handled by LinkedHashMap automatically
    }
    
    private String generateCacheKey(String text) {
        return processor.getClass().getSimpleName() + "_" + 
               Integer.toHexString(text.hashCode()) + "_" + 
               processor.getDescription().hashCode();
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + Cache (" + cache.size() + "/" + maxCacheSize + 
               ", " + strategy.getDescription() + ", hit ratio: " + 
               String.format("%.1f%%", getCacheHitRatio() * 100) + ")";
    }
    
    public void clearCache() {
        cache.clear();
        System.out.println("🧹 Cache cleared");
    }
    
    public double getCacheHitRatio() {
        if (cacheHits + cacheMisses == 0) return 0.0;
        return (double) cacheHits / (cacheHits + cacheMisses);
    }
    
    public int getCacheSize() { return cache.size(); }
    public CacheStrategy getStrategy() { return strategy; }
    
    public void showCacheStats() {
        System.out.println("📊 Cache Statistics:");
        System.out.println("  Strategy: " + strategy.getDescription());
        System.out.println("  Size: " + cache.size() + "/" + maxCacheSize);
        System.out.println("  Hits: " + cacheHits);
        System.out.println("  Misses: " + cacheMisses);
        System.out.printf("  Hit Ratio: %.1f%%%n", getCacheHitRatio() * 100);
        System.out.println("  Expiry: " + (cacheExpiryMs / 1000) + " seconds");
    }
}

// Text processing service
class TextProcessingService {
    private String serviceName;
    private List<TextProcessor> processors;
    private Map<String, Object> globalStatistics;
    private List<String> processedTexts;
    
    public TextProcessingService(String serviceName) {
        this.serviceName = serviceName;
        this.processors = new ArrayList<>();
        this.globalStatistics = new HashMap<>();
        this.processedTexts = new ArrayList<>();
        initializeGlobalStats();
    }
    
    private void initializeGlobalStats() {
        globalStatistics.put("texts_processed", 0);
        globalStatistics.put("total_processing_time", 0L);
        globalStatistics.put("total_input_chars", 0);
        globalStatistics.put("total_output_chars", 0);
        globalStatistics.put("successful_processing", 0);
        globalStatistics.put("failed_processing", 0);
    }
    
    public void addProcessor(TextProcessor processor) {
        processors.add(processor);
        System.out.println("➕ Added processor: " + processor.getDescription());
    }
    
    public void removeProcessor(TextProcessor processor) {
        if (processors.remove(processor)) {
            System.out.println("➖ Removed processor: " + processor.getDescription());
        }
    }
    
    public String processText(String text) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📝 " + serviceName + " - Processing Text");
        System.out.println("=".repeat(80));
        System.out.println("📥 Input text: \"" + truncateForDisplay(text) + "\"");
        System.out.println("📏 Input length: " + text.length() + " characters");
        
        String result = text;
        long totalProcessingTime = 0;
        boolean processingSuccessful = true;
        
        try {
            for (int i = 0; i < processors.size(); i++) {
                TextProcessor processor = processors.get(i);
                
                if (processor.isEnabled()) {
                    System.out.println("\n--- 🔄 Step " + (i + 1) + ": " + processor.getDescription() + " ---");
                    
                    long stepStart = System.currentTimeMillis();
                    String before = result;
                    result = processor.process(result);
                    long stepTime = System.currentTimeMillis() - stepStart;
                    
                    totalProcessingTime += stepTime;
                    
                    System.out.println("📤 Output: \"" + truncateForDisplay(result) + "\"");
                    System.out.println("⏱️ Step time: " + stepTime + "ms");
                    
                    if (!before.equals(result)) {
                        System.out.println("✅ Text modified");
                    } else {
                        System.out.println("➡️ Text unchanged");
                    }
                    
                    // Show processor-specific stats if available
                    Map<String, Object> stats = processor.getStatistics();
                    if (stats.containsKey("cache_hits")) {
                        System.out.printf("💾 Cache: hits=%s, misses=%s%n", 
                            stats.get("cache_hits"), stats.get("cache_misses"));
                    }
                    
                } else {
                    System.out.println("\n--- ❌ Step " + (i + 1) + ": " + processor.getDescription() + " (DISABLED) ---");
                }
            }
            
        } catch (Exception e) {
            processingSuccessful = false;
            System.out.println("🚫 Processing failed: " + e.getMessage());
            throw e;
            
        } finally {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("📤 Final result: \"" + truncateForDisplay(result) + "\"");
            System.out.println("📏 Final length: " + result.length() + " characters");
            System.out.println("⏱️ Total processing time: " + totalProcessingTime + "ms");
            System.out.println("🔧 Active processors: " + processors.stream().mapToLong(p -> p.isEnabled() ? 1 : 0).sum());
            System.out.println("📊 Status: " + (processingSuccessful ? "✅ SUCCESS" : "❌ FAILED"));
            System.out.println("=".repeat(80));
            
            updateGlobalStatistics(text, result, totalProcessingTime, processingSuccessful);
            processedTexts.add(result);
        }
        
        return result;
    }
    
    private void updateGlobalStatistics(String original, String result, long processingTime, boolean successful) {
        globalStatistics.put("texts_processed", (Integer) globalStatistics.get("texts_processed") + 1);
        globalStatistics.put("total_processing_time", (Long) globalStatistics.get("total_processing_time") + processingTime);
        globalStatistics.put("total_input_chars", (Integer) globalStatistics.get("total_input_chars") + original.length());
        globalStatistics.put("total_output_chars", (Integer) globalStatistics.get("total_output_chars") + result.length());
        
        if (successful) {
            globalStatistics.put("successful_processing", (Integer) globalStatistics.get("successful_processing") + 1);
        } else {
            globalStatistics.put("failed_processing", (Integer) globalStatistics.get("failed_processing") + 1);
        }
        
        // Calculate derived statistics
        int totalTexts = (Integer) globalStatistics.get("texts_processed");
        long totalTime = (Long) globalStatistics.get("total_processing_time");
        globalStatistics.put("average_processing_time", totalTexts > 0 ? (double) totalTime / totalTexts : 0.0);
        globalStatistics.put("success_rate", totalTexts > 0 ? 
            (double) (Integer) globalStatistics.get("successful_processing") / totalTexts * 100 : 0.0);
    }
    
    private String truncateForDisplay(String text) {
        if (text.length() <= 100) return text;
        return text.substring(0, 97) + "...";
    }
    
    public void showProcessorChain() {
        System.out.println("\n📋 " + serviceName + " - Processor Chain");
        System.out.println("=".repeat(60));
        
        for (int i = 0; i < processors.size(); i++) {
            TextProcessor processor = processors.get(i);
            String status = processor.isEnabled() ? "✅ Enabled" : "❌ Disabled";
            System.out.println((i + 1) + ". " + processor.getDescription() + " [" + status + "]");
            
            // Show key statistics
            Map<String, Object> stats = processor.getStatistics();
            if (stats.containsKey("texts_processed") && (Integer) stats.get("texts_processed") > 0) {
                System.out.printf("   📊 Processed: %d texts, %.2fms avg%n", 
                    stats.get("texts_processed"), stats.get("average_processing_time"));
            }
        }
        
        System.out.println("=".repeat(60));
    }
    
    public void showStatistics() {
        System.out.println("\n📊 " + serviceName + " - Comprehensive Statistics");
        System.out.println("=".repeat(70));
        
        // Global statistics
        System.out.println("🌐 Global Statistics:");
        System.out.printf("  📈 Total texts processed: %d%n", globalStatistics.get("texts_processed"));
        System.out.printf("  ✅ Successful: %d (%.1f%%)%n", 
            globalStatistics.get("successful_processing"), globalStatistics.get("success_rate"));
        System.out.printf("  ❌ Failed: %d%n", globalStatistics.get("failed_processing"));
        System.out.printf("  ⏱️ Total time: %dms%n", globalStatistics.get("total_processing_time"));
        System.out.printf("  📊 Average time: %.2fms%n", globalStatistics.get("average_processing_time"));
        System.out.printf("  📝 Input chars: %d%n", globalStatistics.get("total_input_chars"));
        System.out.printf("  📤 Output chars: %d%n", globalStatistics.get("total_output_chars"));
        
        int inputChars = (Integer) globalStatistics.get("total_input_chars");
        int outputChars = (Integer) globalStatistics.get("total_output_chars");
        if (inputChars > 0) {
            double expansionRatio = (double) outputChars / inputChars;
            System.out.printf("  📈 Expansion ratio: %.2fx%n", expansionRatio);
        }
        
        // Individual processor statistics
        System.out.println("\n🔧 Processor Statistics:");
        for (int i = 0; i < processors.size(); i++) {
            TextProcessor processor = processors.get(i);
            System.out.println((i + 1) + ". " + processor.getDescription());
            
            Map<String, Object> stats = processor.getStatistics();
            for (Map.Entry<String, Object> entry : stats.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    System.out.printf("    %s: %s%n", entry.getKey(), entry.getValue());
                }
            }
            
            System.out.println();
        }
        
        System.out.println("=".repeat(70));
    }
    
    public void enableAllProcessors() {
        processors.forEach(p -> p.setEnabled(true));
        System.out.println("✅ All processors enabled");
    }
    
    public void disableAllProcessors() {
        processors.forEach(p -> p.setEnabled(false));
        System.out.println("❌ All processors disabled");
    }
    
    public void clearAllCaches() {
        for (TextProcessor processor : processors) {
            if (processor instanceof CacheDecorator) {
                ((CacheDecorator) processor).clearCache();
            }
        }
        System.out.println("🧹 All caches cleared");
    }
    
    public void resetStatistics() {
        initializeGlobalStats();
        processedTexts.clear();
        
        // Reset processor statistics
        for (TextProcessor processor : processors) {
            if (processor instanceof BaseTextProcessor) {
                ((BaseTextProcessor) processor).initializeStatistics();
            }
        }
        
        System.out.println("🔄 All statistics reset");
    }
    
    public List<String> getProcessedTexts() {
        return new ArrayList<>(processedTexts);
    }
    
    public List<TextProcessor> getProcessors() {
        return new ArrayList<>(processors);
    }
}

// Demo class
public class TextProcessingExample {
    public static void main(String[] args) {
        System.out.println("=== 📝 Decorator Pattern: Text Processing Pipeline Example ===\n");
        
        TextProcessingService service = new TextProcessingService("Advanced Text Processor");
        
        // Create base processors
        PlainTextProcessor plainProcessor = new PlainTextProcessor();
        plainProcessor.setMaxLength(500);
        
        MarkdownProcessor markdownProcessor = new MarkdownProcessor();
        markdownProcessor.addExtension("emoji");
        
        HTMLProcessor htmlProcessor = new HTMLProcessor();
        
        // Example 1: Simple plain text processing
        System.out.println("--- 📄 Example 1: Plain Text with Basic Decorators ---");
        
        TextProcessor pipeline1 = plainProcessor;
        pipeline1 = new TrimDecorator(pipeline1, TrimDecorator.TrimMode.ALL_EXTRA);
        pipeline1 = new UpperCaseDecorator(pipeline1, true, false);
        
        service.addProcessor(pipeline1);
        
        String text1 = "  hello    world  \n\n  this is   a   test  ";
        String result1 = service.processText(text1);
        
        service.getProcessors().clear();
        
        // Example 2: Markdown processing with validation
        System.out.println("\n--- 📝 Example 2: Markdown with Validation ---");
        
        TextProcessor pipeline2 = markdownProcessor;
        pipeline2 = new ValidationDecorator(pipeline2, 10, 200, ValidationDecorator.ValidationMode.CORRECTIVE);
        pipeline2 = new TrimDecorator(pipeline2);
        
        service.addProcessor(pipeline2);
        
        String text2 = "# Hello World\n\n**Bold text** and *italic text*\n\n`code snippet`\n\n[link](http://example.com)";
        String result2 = service.processText(text2);
        
        service.getProcessors().clear();
        
        // Example 3: HTML processing with filtering
        System.out.println("\n--- 🌐 Example 3: HTML with Content Filtering ---");
        
        TextProcessor pipeline3 = htmlProcessor;
        pipeline3 = new FilterDecorator(pipeline3, 
            new HashSet<>(Arrays.asList("spam", "advertisement", "click here")), 
            "[FILTERED]", FilterDecorator.FilterMode.PARTIAL);
        pipeline3 = new ValidationDecorator(pipeline3, 0, 1000, ValidationDecorator.ValidationMode.LENIENT);
        
        service.addProcessor(pipeline3);
        
        String text3 = "<div>This is a <strong>spam</strong> message. <script>alert('bad');</script> Click here now! <p>Valid content</p></div>";
        String result3 = service.processText(text3);
        
        service.getProcessors().clear();
        
        // Example 4: Complex pipeline with caching
        System.out.println("\n--- 🏗️ Example 4: Complex Pipeline with Caching ---");
        
        TextProcessor pipeline4 = new PlainTextProcessor();
        pipeline4 = new TrimDecorator(pipeline4, TrimDecorator.TrimMode.NORMALIZE_LINES);
        pipeline4 = new FilterDecorator(pipeline4, 
            new HashSet<>(Arrays.asList("badword", "offensive", "inappropriate")), 
            "***", FilterDecorator.FilterMode.ASTERISK);
        pipeline4 = new ValidationDecorator(pipeline4, 5, 300, ValidationDecorator.ValidationMode.STRICT);
        pipeline4 = new CacheDecorator(pipeline4, 50, 2 * 60 * 1000, CacheDecorator.CacheStrategy.LFU);
        pipeline4 = new UpperCaseDecorator(pipeline4, true, false);
        
        service.addProcessor(pipeline4);
        
        String text4 = "This is a sample text with some badword content\n\n\n\nthat needs to be processed.";
        
        // Process same text multiple times to demonstrate caching
        System.out.println("First processing (cache miss expected):");
        String result4a = service.processText(text4);
        
        System.out.println("\nSecond processing (cache hit expected):");
        String result4b = service.processText(text4);
        
        System.out.println("\nThird processing (cache hit expected):");
        String result4c = service.processText(text4);
        
        // Show cache statistics
        for (TextProcessor processor : service.getProcessors()) {
            if (processor instanceof CacheDecorator) {
                ((CacheDecorator) processor).showCacheStats();
            }
        }
        
        service.getProcessors().clear();
        
        // Example 5: Multi-stage processing pipeline
        System.out.println("\n--- 🔄 Example 5: Multi-Stage Processing Pipeline ---");
        
        // Stage 1: Input sanitization
        TextProcessor stage1 = new PlainTextProcessor();
        stage1 = new TrimDecorator(stage1, TrimDecorator.TrimMode.ALL_EXTRA);
        stage1 = new ValidationDecorator(stage1, 1, 500, ValidationDecorator.ValidationMode.CORRECTIVE);
        
        // Stage 2: Content filtering
        TextProcessor stage2 = new FilterDecorator(stage1, 
            new HashSet<>(Arrays.asList("spam", "scam", "virus", "malware")), 
            "[SECURITY_FILTERED]", FilterDecorator.FilterMode.REPLACE);
        
        // Stage 3: Format processing
        TextProcessor stage3 = new MarkdownProcessor();
        stage3 = new TrimDecorator(stage3);
        
        // Stage 4: Optimization
        TextProcessor stage4 = new CacheDecorator(stage3, 25, 60000, CacheDecorator.CacheStrategy.LRU);
        
        service.addProcessor(stage1);
        service.addProcessor(stage2);
        service.addProcessor(stage4); // Note: stage3 is wrapped in stage4
        
        String text5 = "   # Important Document  \n\n" +
                      "This document contains **important** information.\n\n" +
                      "Please be aware that some spam content might be present.\n\n" +
                      "- Item 1\n- Item 2\n- Item 3   ";
        
        String result5 = service.processText(text5);
        
        // Show processor chain and statistics
        service.showProcessorChain();
        service.showStatistics();
        
        // Example 6: Error handling demonstration
        System.out.println("\n--- ⚠️ Example 6: Error Handling ---");
        
        service.getProcessors().clear();
        
        TextProcessor errorPipeline = new PlainTextProcessor();
        errorPipeline = new ValidationDecorator(errorPipeline, 20, 50, ValidationDecorator.ValidationMode.STRICT);
        service.addProcessor(errorPipeline);
        
        try {
            String invalidText = "Short"; // Too short, will fail validation
            service.processText(invalidText);
        } catch (Exception e) {
            System.out.println("Caught expected validation error: " + e.getMessage());
        }
        
        // Example 7: Dynamic processor configuration
        System.out.println("\n--- 🔧 Example 7: Dynamic Configuration ---");
        
        service.getProcessors().clear();
        
        TextProcessor flexiblePipeline = new PlainTextProcessor();
        flexiblePipeline = new TrimDecorator(flexiblePipeline);
        CacheDecorator cacheDecorator = new CacheDecorator(flexiblePipeline, 10, 30000, CacheDecorator.CacheStrategy.LRU);
        FilterDecorator filterDecorator = new FilterDecorator(cacheDecorator);
        UpperCaseDecorator upperDecorator = new UpperCaseDecorator(filterDecorator);
        
        service.addProcessor(upperDecorator);
        
        String dynamicText = "This is a test message with some content.";
        
        // Process with all decorators enabled
        System.out.println("Processing with all decorators enabled:");
        service.processText(dynamicText);
        
        // Disable some decorators
        System.out.println("\nDisabling filter and uppercase decorators:");
        filterDecorator.setEnabled(false);
        upperDecorator.setEnabled(false);
        service.processText(dynamicText);
        
        // Re-enable decorators
        System.out.println("\nRe-enabling all decorators:");
        service.enableAllProcessors();
        service.processText(dynamicText);
        
        // Final statistics
        service.showStatistics();
        
        System.out.println("\n=== ✅ Demo Complete ===");
        
        // Key benefits demonstrated
        System.out.println("\n🎯 Key Benefits Demonstrated:");
        System.out.println("✅ Flexible composition - decorators can be combined in any order");
        System.out.println("✅ Runtime configuration - processors can be enabled/disabled dynamically");
        System.out.println("✅ Layered functionality - each decorator adds specific capability");
        System.out.println("✅ Transparent interface - all processors work the same way");
        System.out.println("✅ Performance optimization - caching reduces processing time");
        System.out.println("✅ Error handling - validation and recovery mechanisms");
        System.out.println("✅ Statistics tracking - comprehensive monitoring and reporting");
        System.out.println("✅ Easy extensibility - new decorators can be added without changing existing code");
    }
}