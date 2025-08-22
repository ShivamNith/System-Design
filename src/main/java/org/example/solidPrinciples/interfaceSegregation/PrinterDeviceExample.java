package org.example.solidPrinciples.interfaceSegregation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Printer Device System - Interface Segregation Principle Example
 * 
 * This example demonstrates how to properly segregate interfaces so that
 * clients don't depend on methods they don't use. It shows the transformation
 * from a "fat" interface to properly segregated, focused interfaces.
 * 
 * Key Concepts:
 * - No client should be forced to depend on methods it doesn't use
 * - Prefer many client-specific interfaces over one general-purpose interface
 * - Interface pollution leads to unnecessary dependencies
 * - Composition of interfaces for complex devices
 * 
 * Real-world scenario: Office equipment with varying capabilities
 */
public class PrinterDeviceExample {
    
    /**
     * BEFORE: Fat interface violating ISP
     * This forces all devices to implement methods they may not support
     */
    public static class ViolatingISP {
        
        // Fat interface with too many responsibilities
        public interface MultiFunctionDevice {
            // Printing operations
            void print(Document document);
            void printColor(Document document);
            void printDuplex(Document document);
            void print3D(Object model);
            
            // Scanning operations
            void scan(Document document);
            void scanToEmail(String email);
            void scanToPDF();
            void ocrScan(Document document);
            
            // Fax operations
            void sendFax(Document document, String phoneNumber);
            void receiveFax();
            FaxStatus getFaxStatus();
            void setFaxNumber(String number);
            
            // Copy operations
            void copy(Document document);
            void copyWithEnlargement(Document document, int percentage);
            void copyWithReduction(Document document, int percentage);
            
            // Network operations
            void connectToWifi(String ssid, String password);
            void connectToEthernet();
            void enableCloudPrint();
            void setupEmailServer(String smtp, int port);
            
            // Maintenance operations
            void cleanPrintHead();
            void alignPrintHead();
            void checkInkLevels();
            void orderSupplies();
            void runDiagnostics();
            void updateFirmware();
        }
        
        // Simple printer forced to implement everything
        public static class SimplePrinter implements MultiFunctionDevice {
            private String model = "Basic Printer";
            
            @Override
            public void print(Document document) {
                System.out.println(model + ": Printing " + document.getName());
            }
            
            @Override
            public void printColor(Document document) {
                throw new UnsupportedOperationException(model + " doesn't support color printing");
            }
            
            @Override
            public void printDuplex(Document document) {
                throw new UnsupportedOperationException(model + " doesn't support duplex printing");
            }
            
            @Override
            public void print3D(Object model) {
                throw new UnsupportedOperationException(this.model + " doesn't support 3D printing");
            }
            
            @Override
            public void scan(Document document) {
                throw new UnsupportedOperationException(model + " doesn't have a scanner");
            }
            
            @Override
            public void scanToEmail(String email) {
                throw new UnsupportedOperationException(model + " doesn't have a scanner");
            }
            
            @Override
            public void scanToPDF() {
                throw new UnsupportedOperationException(model + " doesn't have a scanner");
            }
            
            @Override
            public void ocrScan(Document document) {
                throw new UnsupportedOperationException(model + " doesn't have OCR capability");
            }
            
            @Override
            public void sendFax(Document document, String phoneNumber) {
                throw new UnsupportedOperationException(model + " doesn't have fax capability");
            }
            
            @Override
            public void receiveFax() {
                throw new UnsupportedOperationException(model + " doesn't have fax capability");
            }
            
            @Override
            public FaxStatus getFaxStatus() {
                throw new UnsupportedOperationException(model + " doesn't have fax capability");
            }
            
            @Override
            public void setFaxNumber(String number) {
                throw new UnsupportedOperationException(model + " doesn't have fax capability");
            }
            
            @Override
            public void copy(Document document) {
                throw new UnsupportedOperationException(model + " doesn't have copy capability");
            }
            
            @Override
            public void copyWithEnlargement(Document document, int percentage) {
                throw new UnsupportedOperationException(model + " doesn't have copy capability");
            }
            
            @Override
            public void copyWithReduction(Document document, int percentage) {
                throw new UnsupportedOperationException(model + " doesn't have copy capability");
            }
            
            @Override
            public void connectToWifi(String ssid, String password) {
                throw new UnsupportedOperationException(model + " doesn't have WiFi");
            }
            
            @Override
            public void connectToEthernet() {
                throw new UnsupportedOperationException(model + " doesn't have Ethernet");
            }
            
            @Override
            public void enableCloudPrint() {
                throw new UnsupportedOperationException(model + " doesn't support cloud printing");
            }
            
            @Override
            public void setupEmailServer(String smtp, int port) {
                throw new UnsupportedOperationException(model + " doesn't have email capability");
            }
            
            @Override
            public void cleanPrintHead() {
                System.out.println(model + ": Cleaning print head");
            }
            
            @Override
            public void alignPrintHead() {
                System.out.println(model + ": Aligning print head");
            }
            
            @Override
            public void checkInkLevels() {
                System.out.println(model + ": Checking ink levels");
            }
            
            @Override
            public void orderSupplies() {
                throw new UnsupportedOperationException(model + " can't order supplies automatically");
            }
            
            @Override
            public void runDiagnostics() {
                System.out.println(model + ": Running basic diagnostics");
            }
            
            @Override
            public void updateFirmware() {
                throw new UnsupportedOperationException(model + " doesn't support firmware updates");
            }
        }
        
        // Problems with this approach:
        // 1. SimplePrinter has 16 methods that throw UnsupportedOperationException
        // 2. Violates Liskov Substitution Principle
        // 3. Client code can't tell what's actually supported
        // 4. Maintenance nightmare as interface grows
        // 5. Testing requires mocking unnecessary methods
    }
    
    /**
     * AFTER: Properly segregated interfaces following ISP
     */
    
    // Core document class
    public static class Document {
        private String name;
        private String content;
        private int pages;
        private DocumentType type;
        
        public enum DocumentType {
            TEXT, PDF, IMAGE, SPREADSHEET
        }
        
        public Document(String name, String content, int pages, DocumentType type) {
            this.name = name;
            this.content = content;
            this.pages = pages;
            this.type = type;
        }
        
        public String getName() { return name; }
        public String getContent() { return content; }
        public int getPages() { return pages; }
        public DocumentType getType() { return type; }
    }
    
    // Segregated interfaces - each with a specific responsibility
    
    public interface Printable {
        void print(Document document);
        PrintStatus getPrintStatus();
        void cancelPrint();
    }
    
    public interface ColorPrintable extends Printable {
        void printColor(Document document);
        void setColorMode(ColorMode mode);
        
        enum ColorMode {
            FULL_COLOR, GRAYSCALE, BLACK_WHITE
        }
    }
    
    public interface DuplexPrintable extends Printable {
        void printDuplex(Document document);
        void setDuplexMode(DuplexMode mode);
        
        enum DuplexMode {
            SINGLE_SIDED, DOUBLE_SIDED_LONG_EDGE, DOUBLE_SIDED_SHORT_EDGE
        }
    }
    
    public interface Scannable {
        ScannedDocument scan();
        void setScanResolution(int dpi);
        ScanStatus getScanStatus();
    }
    
    public interface AdvancedScannable extends Scannable {
        void scanToEmail(String email);
        ScannedDocument scanToPDF();
        String ocrScan(Document document);
    }
    
    public interface Faxable {
        void sendFax(Document document, String phoneNumber);
        FaxDocument receiveFax();
        FaxStatus getFaxStatus();
        void setFaxNumber(String number);
    }
    
    public interface Copyable {
        void copy(Document document, int copies);
        CopyStatus getCopyStatus();
    }
    
    public interface AdvancedCopyable extends Copyable {
        void copyWithEnlargement(Document document, int percentage);
        void copyWithReduction(Document document, int percentage);
        void setQuality(CopyQuality quality);
        
        enum CopyQuality {
            DRAFT, NORMAL, HIGH, PHOTO
        }
    }
    
    public interface NetworkEnabled {
        void connectToNetwork(NetworkConfig config);
        boolean isConnected();
        NetworkInfo getNetworkInfo();
    }
    
    public interface CloudEnabled extends NetworkEnabled {
        void enableCloudPrint(CloudConfig config);
        void syncWithCloud();
        CloudStatus getCloudStatus();
    }
    
    public interface Maintainable {
        void performMaintenance();
        MaintenanceStatus getMaintenanceStatus();
        void scheduleMainten ance(LocalDateTime time);
    }
    
    // Supporting classes
    public static class PrintStatus {
        private boolean printing;
        private int pagesCompleted;
        private int totalPages;
        
        public PrintStatus(boolean printing, int pagesCompleted, int totalPages) {
            this.printing = printing;
            this.pagesCompleted = pagesCompleted;
            this.totalPages = totalPages;
        }
        
        public boolean isPrinting() { return printing; }
        public int getPagesCompleted() { return pagesCompleted; }
        public int getTotalPages() { return totalPages; }
    }
    
    public static class ScannedDocument extends Document {
        private int resolution;
        
        public ScannedDocument(String name, String content, int resolution) {
            super(name, content, 1, DocumentType.IMAGE);
            this.resolution = resolution;
        }
        
        public int getResolution() { return resolution; }
    }
    
    public static class FaxDocument extends Document {
        private String senderNumber;
        private LocalDateTime receivedTime;
        
        public FaxDocument(String name, String content, String senderNumber) {
            super(name, content, 1, DocumentType.IMAGE);
            this.senderNumber = senderNumber;
            this.receivedTime = LocalDateTime.now();
        }
        
        public String getSenderNumber() { return senderNumber; }
        public LocalDateTime getReceivedTime() { return receivedTime; }
    }
    
    public enum ScanStatus {
        IDLE, SCANNING, COMPLETE, ERROR
    }
    
    public enum FaxStatus {
        IDLE, SENDING, RECEIVING, COMPLETE, ERROR
    }
    
    public enum CopyStatus {
        IDLE, COPYING, COMPLETE, ERROR
    }
    
    public enum CloudStatus {
        CONNECTED, DISCONNECTED, SYNCING, ERROR
    }
    
    public static class NetworkConfig {
        private String ssid;
        private String password;
        private NetworkType type;
        
        public enum NetworkType {
            WIFI, ETHERNET, BLUETOOTH
        }
        
        public NetworkConfig(String ssid, String password, NetworkType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }
        
        public String getSsid() { return ssid; }
        public String getPassword() { return password; }
        public NetworkType getType() { return type; }
    }
    
    public static class NetworkInfo {
        private String ipAddress;
        private String macAddress;
        private int signalStrength;
        
        public NetworkInfo(String ipAddress, String macAddress, int signalStrength) {
            this.ipAddress = ipAddress;
            this.macAddress = macAddress;
            this.signalStrength = signalStrength;
        }
        
        public String getIpAddress() { return ipAddress; }
        public String getMacAddress() { return macAddress; }
        public int getSignalStrength() { return signalStrength; }
    }
    
    public static class CloudConfig {
        private String serviceName;
        private String apiKey;
        private String accountId;
        
        public CloudConfig(String serviceName, String apiKey, String accountId) {
            this.serviceName = serviceName;
            this.apiKey = apiKey;
            this.accountId = accountId;
        }
        
        public String getServiceName() { return serviceName; }
        public String getApiKey() { return apiKey; }
        public String getAccountId() { return accountId; }
    }
    
    public static class MaintenanceStatus {
        private boolean needsCleaning;
        private boolean needsInkReplacement;
        private LocalDateTime lastMaintenance;
        private LocalDateTime nextScheduledMaintenance;
        
        public MaintenanceStatus(boolean needsCleaning, boolean needsInkReplacement,
                                LocalDateTime lastMaintenance) {
            this.needsCleaning = needsCleaning;
            this.needsInkReplacement = needsInkReplacement;
            this.lastMaintenance = lastMaintenance;
        }
        
        public boolean needsCleaning() { return needsCleaning; }
        public boolean needsInkReplacement() { return needsInkReplacement; }
        public LocalDateTime getLastMaintenance() { return lastMaintenance; }
        public LocalDateTime getNextScheduledMaintenance() { return nextScheduledMaintenance; }
        public void setNextScheduledMaintenance(LocalDateTime time) { 
            this.nextScheduledMaintenance = time; 
        }
    }
    
    // Concrete implementations - each implementing only what they need
    
    public static class BasicPrinter implements Printable, Maintainable {
        private String model;
        private boolean printing;
        private Queue<Document> printQueue;
        
        public BasicPrinter(String model) {
            this.model = model;
            this.printing = false;
            this.printQueue = new LinkedList<>();
        }
        
        @Override
        public void print(Document document) {
            printQueue.offer(document);
            printing = true;
            System.out.println(model + ": Printing " + document.getName());
            System.out.println("  Pages: " + document.getPages());
            System.out.println("  Type: " + document.getType());
            // Simulate printing
            printing = false;
        }
        
        @Override
        public PrintStatus getPrintStatus() {
            return new PrintStatus(printing, 0, printQueue.size());
        }
        
        @Override
        public void cancelPrint() {
            printQueue.clear();
            printing = false;
            System.out.println(model + ": Print job cancelled");
        }
        
        @Override
        public void performMaintenance() {
            System.out.println(model + ": Performing maintenance");
            System.out.println("  - Cleaning print head");
            System.out.println("  - Checking ink levels");
            System.out.println("  - Aligning print head");
        }
        
        @Override
        public MaintenanceStatus getMaintenanceStatus() {
            return new MaintenanceStatus(false, false, LocalDateTime.now());
        }
        
        @Override
        public void scheduleMaintenance(LocalDateTime time) {
            System.out.println(model + ": Maintenance scheduled for " + 
                             time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
    
    public static class ColorLaserPrinter implements ColorPrintable, DuplexPrintable, 
                                                      NetworkEnabled, Maintainable {
        private String model;
        private ColorMode colorMode;
        private DuplexMode duplexMode;
        private boolean connected;
        private NetworkInfo networkInfo;
        
        public ColorLaserPrinter(String model) {
            this.model = model;
            this.colorMode = ColorMode.FULL_COLOR;
            this.duplexMode = DuplexMode.SINGLE_SIDED;
            this.connected = false;
        }
        
        @Override
        public void print(Document document) {
            System.out.println(model + ": Printing " + document.getName());
        }
        
        @Override
        public void printColor(Document document) {
            System.out.println(model + ": Color printing " + document.getName());
            System.out.println("  Color mode: " + colorMode);
        }
        
        @Override
        public void printDuplex(Document document) {
            System.out.println(model + ": Duplex printing " + document.getName());
            System.out.println("  Duplex mode: " + duplexMode);
        }
        
        @Override
        public void setColorMode(ColorMode mode) {
            this.colorMode = mode;
            System.out.println(model + ": Color mode set to " + mode);
        }
        
        @Override
        public void setDuplexMode(DuplexMode mode) {
            this.duplexMode = mode;
            System.out.println(model + ": Duplex mode set to " + mode);
        }
        
        @Override
        public PrintStatus getPrintStatus() {
            return new PrintStatus(false, 0, 0);
        }
        
        @Override
        public void cancelPrint() {
            System.out.println(model + ": Print cancelled");
        }
        
        @Override
        public void connectToNetwork(NetworkConfig config) {
            System.out.println(model + ": Connecting to " + config.getSsid());
            this.connected = true;
            this.networkInfo = new NetworkInfo("192.168.1.100", "AA:BB:CC:DD:EE:FF", 85);
        }
        
        @Override
        public boolean isConnected() {
            return connected;
        }
        
        @Override
        public NetworkInfo getNetworkInfo() {
            return networkInfo;
        }
        
        @Override
        public void performMaintenance() {
            System.out.println(model + ": Performing laser printer maintenance");
            System.out.println("  - Calibrating colors");
            System.out.println("  - Cleaning laser assembly");
            System.out.println("  - Checking toner levels");
        }
        
        @Override
        public MaintenanceStatus getMaintenanceStatus() {
            return new MaintenanceStatus(false, false, LocalDateTime.now());
        }
        
        @Override
        public void scheduleMaintenance(LocalDateTime time) {
            System.out.println(model + ": Maintenance scheduled");
        }
    }
    
    public static class MultiFunctionPrinter implements ColorPrintable, DuplexPrintable,
                                                        AdvancedScannable, Faxable,
                                                        AdvancedCopyable, CloudEnabled,
                                                        Maintainable {
        private String model;
        private boolean cloudEnabled;
        private CloudStatus cloudStatus;
        
        public MultiFunctionPrinter(String model) {
            this.model = model;
            this.cloudEnabled = false;
            this.cloudStatus = CloudStatus.DISCONNECTED;
        }
        
        // Printing capabilities
        @Override
        public void print(Document document) {
            System.out.println(model + ": Printing " + document.getName());
        }
        
        @Override
        public void printColor(Document document) {
            System.out.println(model + ": Color printing " + document.getName());
        }
        
        @Override
        public void printDuplex(Document document) {
            System.out.println(model + ": Duplex printing " + document.getName());
        }
        
        @Override
        public void setColorMode(ColorMode mode) {
            System.out.println(model + ": Color mode set to " + mode);
        }
        
        @Override
        public void setDuplexMode(DuplexMode mode) {
            System.out.println(model + ": Duplex mode set to " + mode);
        }
        
        @Override
        public PrintStatus getPrintStatus() {
            return new PrintStatus(false, 0, 0);
        }
        
        @Override
        public void cancelPrint() {
            System.out.println(model + ": Print cancelled");
        }
        
        // Scanning capabilities
        @Override
        public ScannedDocument scan() {
            System.out.println(model + ": Scanning document");
            return new ScannedDocument("Scanned_Doc", "Scanned content", 300);
        }
        
        @Override
        public void setScanResolution(int dpi) {
            System.out.println(model + ": Scan resolution set to " + dpi + " DPI");
        }
        
        @Override
        public ScanStatus getScanStatus() {
            return ScanStatus.IDLE;
        }
        
        @Override
        public void scanToEmail(String email) {
            System.out.println(model + ": Scanning and emailing to " + email);
        }
        
        @Override
        public ScannedDocument scanToPDF() {
            System.out.println(model + ": Scanning to PDF");
            return new ScannedDocument("Scanned_PDF", "PDF content", 300);
        }
        
        @Override
        public String ocrScan(Document document) {
            System.out.println(model + ": Performing OCR scan");
            return "Extracted text from document";
        }
        
        // Fax capabilities
        @Override
        public void sendFax(Document document, String phoneNumber) {
            System.out.println(model + ": Sending fax to " + phoneNumber);
        }
        
        @Override
        public FaxDocument receiveFax() {
            System.out.println(model + ": Receiving fax");
            return new FaxDocument("Received_Fax", "Fax content", "+1234567890");
        }
        
        @Override
        public FaxStatus getFaxStatus() {
            return FaxStatus.IDLE;
        }
        
        @Override
        public void setFaxNumber(String number) {
            System.out.println(model + ": Fax number set to " + number);
        }
        
        // Copy capabilities
        @Override
        public void copy(Document document, int copies) {
            System.out.println(model + ": Making " + copies + " copies of " + document.getName());
        }
        
        @Override
        public void copyWithEnlargement(Document document, int percentage) {
            System.out.println(model + ": Copying with " + percentage + "% enlargement");
        }
        
        @Override
        public void copyWithReduction(Document document, int percentage) {
            System.out.println(model + ": Copying with " + percentage + "% reduction");
        }
        
        @Override
        public void setQuality(CopyQuality quality) {
            System.out.println(model + ": Copy quality set to " + quality);
        }
        
        @Override
        public CopyStatus getCopyStatus() {
            return CopyStatus.IDLE;
        }
        
        // Network and Cloud capabilities
        @Override
        public void connectToNetwork(NetworkConfig config) {
            System.out.println(model + ": Connected to network " + config.getSsid());
        }
        
        @Override
        public boolean isConnected() {
            return true;
        }
        
        @Override
        public NetworkInfo getNetworkInfo() {
            return new NetworkInfo("192.168.1.200", "11:22:33:44:55:66", 90);
        }
        
        @Override
        public void enableCloudPrint(CloudConfig config) {
            System.out.println(model + ": Cloud print enabled with " + config.getServiceName());
            this.cloudEnabled = true;
            this.cloudStatus = CloudStatus.CONNECTED;
        }
        
        @Override
        public void syncWithCloud() {
            System.out.println(model + ": Syncing with cloud");
            this.cloudStatus = CloudStatus.SYNCING;
        }
        
        @Override
        public CloudStatus getCloudStatus() {
            return cloudStatus;
        }
        
        // Maintenance
        @Override
        public void performMaintenance() {
            System.out.println(model + ": Performing comprehensive maintenance");
            System.out.println("  - Cleaning all components");
            System.out.println("  - Checking all consumables");
            System.out.println("  - Running full diagnostics");
        }
        
        @Override
        public MaintenanceStatus getMaintenanceStatus() {
            return new MaintenanceStatus(false, false, LocalDateTime.now());
        }
        
        @Override
        public void scheduleMaintenance(LocalDateTime time) {
            System.out.println(model + ": Comprehensive maintenance scheduled");
        }
    }
    
    public static class Scanner implements AdvancedScannable {
        private String model;
        private int resolution;
        
        public Scanner(String model) {
            this.model = model;
            this.resolution = 300;
        }
        
        @Override
        public ScannedDocument scan() {
            System.out.println(model + ": Scanning at " + resolution + " DPI");
            return new ScannedDocument("Scanned_Document", "Content", resolution);
        }
        
        @Override
        public void setScanResolution(int dpi) {
            this.resolution = dpi;
            System.out.println(model + ": Resolution set to " + dpi + " DPI");
        }
        
        @Override
        public ScanStatus getScanStatus() {
            return ScanStatus.IDLE;
        }
        
        @Override
        public void scanToEmail(String email) {
            System.out.println(model + ": Scanning and sending to " + email);
        }
        
        @Override
        public ScannedDocument scanToPDF() {
            System.out.println(model + ": Creating PDF from scan");
            return new ScannedDocument("Scanned_PDF", "PDF content", resolution);
        }
        
        @Override
        public String ocrScan(Document document) {
            System.out.println(model + ": Performing OCR");
            return "Extracted text";
        }
    }
    
    // Client code that uses only the interfaces it needs
    public static class PrintService {
        private List<Printable> printers;
        
        public PrintService() {
            this.printers = new ArrayList<>();
        }
        
        public void addPrinter(Printable printer) {
            printers.add(printer);
            System.out.println("✓ Printer added to print service");
        }
        
        public void printDocument(Document document) {
            if (printers.isEmpty()) {
                System.out.println("No printers available");
                return;
            }
            
            Printable printer = printers.get(0);
            printer.print(document);
        }
    }
    
    public static class ScanService {
        private List<Scannable> scanners;
        
        public ScanService() {
            this.scanners = new ArrayList<>();
        }
        
        public void addScanner(Scannable scanner) {
            scanners.add(scanner);
            System.out.println("✓ Scanner added to scan service");
        }
        
        public ScannedDocument scanDocument() {
            if (scanners.isEmpty()) {
                System.out.println("No scanners available");
                return null;
            }
            
            Scannable scanner = scanners.get(0);
            return scanner.scan();
        }
    }
    
    public static class CloudPrintService {
        private List<CloudEnabled> cloudDevices;
        
        public CloudPrintService() {
            this.cloudDevices = new ArrayList<>();
        }
        
        public void addCloudDevice(CloudEnabled device) {
            cloudDevices.add(device);
            System.out.println("✓ Cloud device added to service");
        }
        
        public void enableCloudPrinting(CloudConfig config) {
            for (CloudEnabled device : cloudDevices) {
                device.enableCloudPrint(config);
                device.syncWithCloud();
            }
        }
    }
    
    /**
     * Demonstration of Interface Segregation Principle
     */
    public static void main(String[] args) {
        System.out.println("=== Printer Device System - ISP Demo ===\n");
        
        // Create different devices with varying capabilities
        BasicPrinter basicPrinter = new BasicPrinter("HP DeskJet");
        ColorLaserPrinter laserPrinter = new ColorLaserPrinter("Canon LaserJet Pro");
        MultiFunctionPrinter mfp = new MultiFunctionPrinter("Epson WorkForce");
        Scanner scanner = new Scanner("Fujitsu ScanSnap");
        
        // Create a test document
        Document testDoc = new Document("Report.pdf", "Annual Report Content", 10, 
                                       Document.DocumentType.PDF);
        
        System.out.println("1. PRINT SERVICE - Uses only Printable interface");
        System.out.println("─".repeat(50));
        PrintService printService = new PrintService();
        printService.addPrinter(basicPrinter);
        printService.addPrinter(laserPrinter);
        printService.addPrinter(mfp);
        printService.printDocument(testDoc);
        
        System.out.println("\n2. SCAN SERVICE - Uses only Scannable interface");
        System.out.println("─".repeat(50));
        ScanService scanService = new ScanService();
        scanService.addScanner(scanner);
        scanService.addScanner(mfp);
        ScannedDocument scanned = scanService.scanDocument();
        if (scanned != null) {
            System.out.println("Scanned: " + scanned.getName());
        }
        
        System.out.println("\n3. CLOUD SERVICE - Uses only CloudEnabled interface");
        System.out.println("─".repeat(50));
        CloudPrintService cloudService = new CloudPrintService();
        cloudService.addCloudDevice(mfp);
        CloudConfig cloudConfig = new CloudConfig("Google Cloud Print", "API_KEY_123", "ACC_456");
        cloudService.enableCloudPrinting(cloudConfig);
        
        System.out.println("\n4. DEVICE-SPECIFIC OPERATIONS");
        System.out.println("─".repeat(50));
        
        // Basic printer - limited capabilities
        System.out.println("\nBasic Printer capabilities:");
        basicPrinter.print(testDoc);
        basicPrinter.performMaintenance();
        
        // Laser printer - more capabilities
        System.out.println("\nLaser Printer capabilities:");
        laserPrinter.setColorMode(ColorPrintable.ColorMode.FULL_COLOR);
        laserPrinter.printColor(testDoc);
        laserPrinter.setDuplexMode(DuplexPrintable.DuplexMode.DOUBLE_SIDED_LONG_EDGE);
        laserPrinter.printDuplex(testDoc);
        
        // MFP - full capabilities
        System.out.println("\nMulti-Function Printer capabilities:");
        mfp.copy(testDoc, 5);
        mfp.sendFax(testDoc, "+1-555-0123");
        String ocrText = mfp.ocrScan(testDoc);
        System.out.println("OCR Result: " + ocrText);
        
        System.out.println("\n5. BENEFITS OF ISP IN THIS DESIGN");
        System.out.println("─".repeat(50));
        System.out.println("✅ BasicPrinter only implements what it can actually do");
        System.out.println("✅ No UnsupportedOperationException throwing");
        System.out.println("✅ Client services depend only on interfaces they need");
        System.out.println("✅ Easy to add new device types with specific capabilities");
        System.out.println("✅ Clear contracts for what each device can do");
        System.out.println("✅ Testing is simpler - mock only needed methods");
        System.out.println("✅ Maintenance is easier - changes don't ripple unnecessarily");
        
        System.out.println("\n=== Demo Complete ===");
    }
}