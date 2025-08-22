package org.example.solidPrinciples.interfaceSegregation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User Management System - Interface Segregation Principle Example
 * 
 * This example demonstrates how to design role-based interfaces that provide
 * only the operations relevant to specific user types. It shows the problems
 * with monolithic interfaces and how to properly segregate them.
 * 
 * Key Concepts:
 * - Role-based interface design
 * - Client-specific interfaces
 * - Avoiding interface pollution
 * - Composition of capabilities
 * 
 * Real-world scenario: Multi-tenant SaaS application with different user roles
 */
public class UserManagementExample {
    
    /**
     * BEFORE: Monolithic interface violating ISP
     * Forces all clients to know about operations they don't need
     */
    public static class ViolatingISP {
        
        // Fat interface with all possible user operations
        public interface UserService {
            // Basic user operations
            User login(String username, String password);
            void logout(String sessionId);
            User getProfile(String userId);
            void updateProfile(String userId, UserProfile profile);
            void changePassword(String userId, String oldPassword, String newPassword);
            
            // Admin operations
            List<User> getAllUsers();
            User createUser(UserData userData);
            void deleteUser(String userId);
            void suspendUser(String userId);
            void activateUser(String userId);
            void resetUserPassword(String userId);
            void assignRole(String userId, String role);
            void removeRole(String userId, String role);
            
            // System admin operations
            void createTenant(TenantData tenantData);
            void deleteTenant(String tenantId);
            void migrateTenant(String tenantId, String targetServer);
            SystemStats getSystemStats();
            void performSystemBackup();
            void restoreSystemBackup(String backupId);
            void updateSystemConfiguration(Map<String, String> config);
            List<AuditLog> getAuditLogs(Date from, Date to);
            
            // Moderator operations
            List<Content> getFlaggedContent();
            void approveContent(String contentId);
            void rejectContent(String contentId);
            void banUser(String userId, String reason);
            void unbanUser(String userId);
            List<Report> getUserReports();
            
            // Analytics operations
            UserAnalytics getUserAnalytics(String userId);
            SystemAnalytics getSystemAnalytics();
            List<UsageReport> generateUsageReports(Date from, Date to);
            Map<String, Object> getMetrics();
            void exportAnalytics(String format, String destination);
            
            // Billing operations
            Invoice generateInvoice(String userId);
            void processPayment(String userId, PaymentData payment);
            List<Transaction> getTransactionHistory(String userId);
            void issueRefund(String transactionId);
            void updateSubscription(String userId, String planId);
            
            // Support operations
            List<Ticket> getSupportTickets(String userId);
            Ticket createSupportTicket(String userId, TicketData data);
            void respondToTicket(String ticketId, String response);
            void escalateTicket(String ticketId);
            void closeTicket(String ticketId);
        }
        
        // Regular user forced to see admin operations
        public static class RegularUserClient {
            private UserService userService;
            
            public RegularUserClient(UserService userService) {
                this.userService = userService;
            }
            
            public void doUserStuff(String userId) {
                // User only needs these
                User profile = userService.getProfile(userId);
                userService.changePassword(userId, "old", "new");
                
                // But has access to dangerous operations!
                // userService.deleteUser(someOtherId); // Shouldn't be possible!
                // userService.performSystemBackup();   // Definitely not!
            }
        }
        
        // Problems:
        // 1. Regular users see admin operations
        // 2. Impossible to restrict access at compile time
        // 3. Interface has 40+ methods but most clients use <5
        // 4. Changes to admin features affect user clients
        // 5. Testing requires mocking dozens of unused methods
    }
    
    /**
     * AFTER: Properly segregated interfaces following ISP
     */
    
    // Core domain models
    public static class User {
        private String id;
        private String username;
        private String email;
        private UserProfile profile;
        private Set<String> roles;
        private AccountStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;
        
        public enum AccountStatus {
            ACTIVE, SUSPENDED, BANNED, DELETED
        }
        
        public User(String id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.profile = new UserProfile();
            this.roles = new HashSet<>();
            this.status = AccountStatus.ACTIVE;
            this.createdAt = LocalDateTime.now();
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public UserProfile getProfile() { return profile; }
        public Set<String> getRoles() { return roles; }
        public AccountStatus getStatus() { return status; }
        public void setStatus(AccountStatus status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getLastLoginAt() { return lastLoginAt; }
        public void setLastLoginAt(LocalDateTime time) { this.lastLoginAt = time; }
    }
    
    public static class UserProfile {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;
        private String bio;
        private Map<String, String> preferences;
        
        public UserProfile() {
            this.preferences = new HashMap<>();
        }
        
        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getFullName() { 
            return firstName + " " + lastName; 
        }
    }
    
    public static class Session {
        private String sessionId;
        private String userId;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean active;
        
        public Session(String sessionId, String userId) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.createdAt = LocalDateTime.now();
            this.expiresAt = createdAt.plusHours(24);
            this.active = true;
        }
        
        public String getSessionId() { return sessionId; }
        public String getUserId() { return userId; }
        public boolean isActive() { return active && LocalDateTime.now().isBefore(expiresAt); }
        public void invalidate() { this.active = false; }
    }
    
    // Segregated interfaces based on user roles and responsibilities
    
    // Basic authentication - everyone needs this
    public interface Authenticatable {
        Session login(String username, String password);
        void logout(String sessionId);
        boolean validateSession(String sessionId);
        void refreshSession(String sessionId);
    }
    
    // User profile operations - for regular users
    public interface UserProfileOperations {
        User getProfile(String userId);
        void updateProfile(String userId, UserProfile profile);
        void changePassword(String userId, String oldPassword, String newPassword);
        void updatePreferences(String userId, Map<String, String> preferences);
        void deleteAccount(String userId, String password);
    }
    
    // User admin operations - for user administrators
    public interface UserAdminOperations {
        List<User> getAllUsers();
        List<User> searchUsers(String query);
        User createUser(String username, String email, String password);
        void suspendUser(String userId, String reason);
        void activateUser(String userId);
        void resetUserPassword(String userId);
        void assignRole(String userId, String role);
        void removeRole(String userId, String role);
        List<User> getUsersByRole(String role);
    }
    
    // System admin operations - for system administrators
    public interface SystemAdminOperations {
        SystemStats getSystemStats();
        void performMaintenance(MaintenanceType type);
        void updateSystemConfiguration(String key, String value);
        Map<String, String> getSystemConfiguration();
        void clearCache();
        void reindexSearch();
        
        enum MaintenanceType {
            BACKUP, CLEANUP, OPTIMIZATION, FULL
        }
    }
    
    // Audit operations - for compliance and security
    public interface AuditOperations {
        List<AuditLog> getAuditLogs(LocalDateTime from, LocalDateTime to);
        List<AuditLog> getUserAuditLogs(String userId, LocalDateTime from, LocalDateTime to);
        void exportAuditLogs(String format, String destination);
        AuditReport generateAuditReport(LocalDateTime from, LocalDateTime to);
    }
    
    // Content moderation - for moderators
    public interface ContentModerationOperations {
        List<Content> getFlaggedContent();
        List<Content> getContentByUser(String userId);
        void approveContent(String contentId);
        void rejectContent(String contentId, String reason);
        void flagContent(String contentId, String reason);
        List<Report> getContentReports();
        void resolveReport(String reportId, String resolution);
    }
    
    // User moderation - for user moderators
    public interface UserModerationOperations {
        void warnUser(String userId, String reason);
        void banUser(String userId, String reason, int durationHours);
        void unbanUser(String userId);
        List<User> getBannedUsers();
        List<Report> getUserReports();
        UserHistory getUserHistory(String userId);
    }
    
    // Analytics operations - for analysts
    public interface AnalyticsOperations {
        UserAnalytics getUserAnalytics(String userId);
        SystemAnalytics getSystemAnalytics(LocalDateTime from, LocalDateTime to);
        List<TrendReport> getTrends(String metric, int days);
        Map<String, Object> getMetrics();
        void scheduleReport(ReportConfig config);
    }
    
    // Support operations - for support staff
    public interface SupportOperations {
        List<Ticket> getOpenTickets();
        List<Ticket> getUserTickets(String userId);
        Ticket getTicket(String ticketId);
        void respondToTicket(String ticketId, String response);
        void escalateTicket(String ticketId, String escalationLevel);
        void closeTicket(String ticketId, String resolution);
        void assignTicket(String ticketId, String supportAgentId);
    }
    
    // Supporting classes
    public static class SystemStats {
        private int totalUsers;
        private int activeUsers;
        private long totalStorage;
        private long usedStorage;
        private double cpuUsage;
        private double memoryUsage;
        
        public SystemStats(int totalUsers, int activeUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
        }
        
        public int getTotalUsers() { return totalUsers; }
        public int getActiveUsers() { return activeUsers; }
    }
    
    public static class AuditLog {
        private String id;
        private String userId;
        private String action;
        private String details;
        private LocalDateTime timestamp;
        private String ipAddress;
        
        public AuditLog(String userId, String action, String details) {
            this.id = UUID.randomUUID().toString();
            this.userId = userId;
            this.action = action;
            this.details = details;
            this.timestamp = LocalDateTime.now();
        }
        
        public String getAction() { return action; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public static class AuditReport {
        private List<AuditLog> logs;
        private Map<String, Integer> actionCounts;
        private List<String> anomalies;
        
        public AuditReport(List<AuditLog> logs) {
            this.logs = logs;
            this.actionCounts = new HashMap<>();
            this.anomalies = new ArrayList<>();
            analyzeLogs();
        }
        
        private void analyzeLogs() {
            for (AuditLog log : logs) {
                actionCounts.merge(log.getAction(), 1, Integer::sum);
            }
        }
    }
    
    public static class Content {
        private String id;
        private String userId;
        private String content;
        private ContentStatus status;
        private List<String> flags;
        
        public enum ContentStatus {
            PENDING, APPROVED, REJECTED, FLAGGED
        }
        
        public Content(String id, String userId, String content) {
            this.id = id;
            this.userId = userId;
            this.content = content;
            this.status = ContentStatus.PENDING;
            this.flags = new ArrayList<>();
        }
        
        public String getId() { return id; }
        public ContentStatus getStatus() { return status; }
        public void setStatus(ContentStatus status) { this.status = status; }
    }
    
    public static class Report {
        private String id;
        private String reporterId;
        private String targetId;
        private String reason;
        private ReportStatus status;
        
        public enum ReportStatus {
            OPEN, INVESTIGATING, RESOLVED, DISMISSED
        }
        
        public Report(String reporterId, String targetId, String reason) {
            this.id = UUID.randomUUID().toString();
            this.reporterId = reporterId;
            this.targetId = targetId;
            this.reason = reason;
            this.status = ReportStatus.OPEN;
        }
    }
    
    public static class UserHistory {
        private String userId;
        private List<String> warnings;
        private List<String> bans;
        private List<String> actions;
        
        public UserHistory(String userId) {
            this.userId = userId;
            this.warnings = new ArrayList<>();
            this.bans = new ArrayList<>();
            this.actions = new ArrayList<>();
        }
        
        public void addWarning(String warning) { warnings.add(warning); }
        public void addBan(String ban) { bans.add(ban); }
        public List<String> getWarnings() { return warnings; }
        public List<String> getBans() { return bans; }
    }
    
    public static class UserAnalytics {
        private String userId;
        private int loginCount;
        private LocalDateTime lastActive;
        private Map<String, Integer> actionCounts;
        
        public UserAnalytics(String userId) {
            this.userId = userId;
            this.actionCounts = new HashMap<>();
        }
    }
    
    public static class SystemAnalytics {
        private int totalUsers;
        private int activeUsers;
        private Map<String, Double> metrics;
        
        public SystemAnalytics() {
            this.metrics = new HashMap<>();
        }
    }
    
    public static class TrendReport {
        private String metric;
        private List<DataPoint> dataPoints;
        
        public static class DataPoint {
            LocalDateTime timestamp;
            double value;
            
            public DataPoint(LocalDateTime timestamp, double value) {
                this.timestamp = timestamp;
                this.value = value;
            }
        }
    }
    
    public static class ReportConfig {
        private String name;
        private String schedule;
        private List<String> metrics;
        private List<String> recipients;
    }
    
    public static class Ticket {
        private String id;
        private String userId;
        private String subject;
        private String description;
        private TicketStatus status;
        private String assignedTo;
        
        public enum TicketStatus {
            OPEN, IN_PROGRESS, ESCALATED, RESOLVED, CLOSED
        }
        
        public Ticket(String userId, String subject, String description) {
            this.id = UUID.randomUUID().toString();
            this.userId = userId;
            this.subject = subject;
            this.description = description;
            this.status = TicketStatus.OPEN;
        }
        
        public String getId() { return id; }
        public TicketStatus getStatus() { return status; }
        public void setStatus(TicketStatus status) { this.status = status; }
    }
    
    // Concrete implementations for different user types
    
    // Regular user service - implements only what users need
    public static class RegularUserService implements Authenticatable, UserProfileOperations {
        private Map<String, User> users;
        private Map<String, Session> sessions;
        
        public RegularUserService() {
            this.users = new HashMap<>();
            this.sessions = new HashMap<>();
            initializeSampleUsers();
        }
        
        private void initializeSampleUsers() {
            User user1 = new User("1", "john_doe", "john@example.com");
            user1.getRoles().add("USER");
            users.put(user1.getId(), user1);
            
            User user2 = new User("2", "jane_smith", "jane@example.com");
            user2.getRoles().add("USER");
            users.put(user2.getId(), user2);
        }
        
        @Override
        public Session login(String username, String password) {
            System.out.println("User login: " + username);
            User user = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
                
            if (user != null) {
                Session session = new Session(UUID.randomUUID().toString(), user.getId());
                sessions.put(session.getSessionId(), session);
                user.setLastLoginAt(LocalDateTime.now());
                return session;
            }
            return null;
        }
        
        @Override
        public void logout(String sessionId) {
            Session session = sessions.get(sessionId);
            if (session != null) {
                session.invalidate();
                System.out.println("User logged out: " + sessionId);
            }
        }
        
        @Override
        public boolean validateSession(String sessionId) {
            Session session = sessions.get(sessionId);
            return session != null && session.isActive();
        }
        
        @Override
        public void refreshSession(String sessionId) {
            Session session = sessions.get(sessionId);
            if (session != null && session.isActive()) {
                System.out.println("Session refreshed: " + sessionId);
            }
        }
        
        @Override
        public User getProfile(String userId) {
            return users.get(userId);
        }
        
        @Override
        public void updateProfile(String userId, UserProfile profile) {
            User user = users.get(userId);
            if (user != null) {
                user.profile = profile;
                System.out.println("Profile updated for user: " + userId);
            }
        }
        
        @Override
        public void changePassword(String userId, String oldPassword, String newPassword) {
            System.out.println("Password changed for user: " + userId);
        }
        
        @Override
        public void updatePreferences(String userId, Map<String, String> preferences) {
            User user = users.get(userId);
            if (user != null) {
                user.getProfile().preferences = preferences;
                System.out.println("Preferences updated for user: " + userId);
            }
        }
        
        @Override
        public void deleteAccount(String userId, String password) {
            User user = users.get(userId);
            if (user != null) {
                user.setStatus(User.AccountStatus.DELETED);
                System.out.println("Account deleted: " + userId);
            }
        }
    }
    
    // Admin service - implements admin-specific operations
    public static class AdminService implements UserAdminOperations, SystemAdminOperations {
        private Map<String, User> users;
        private Map<String, String> systemConfig;
        
        public AdminService() {
            this.users = new HashMap<>();
            this.systemConfig = new HashMap<>();
            initializeConfig();
        }
        
        private void initializeConfig() {
            systemConfig.put("max_users", "10000");
            systemConfig.put("session_timeout", "3600");
            systemConfig.put("maintenance_mode", "false");
        }
        
        @Override
        public List<User> getAllUsers() {
            System.out.println("Admin: Getting all users");
            return new ArrayList<>(users.values());
        }
        
        @Override
        public List<User> searchUsers(String query) {
            System.out.println("Admin: Searching users with query: " + query);
            return users.values().stream()
                .filter(u -> u.getUsername().contains(query) || u.getEmail().contains(query))
                .collect(Collectors.toList());
        }
        
        @Override
        public User createUser(String username, String email, String password) {
            String userId = UUID.randomUUID().toString();
            User user = new User(userId, username, email);
            users.put(userId, user);
            System.out.println("Admin: Created user " + username);
            return user;
        }
        
        @Override
        public void suspendUser(String userId, String reason) {
            User user = users.get(userId);
            if (user != null) {
                user.setStatus(User.AccountStatus.SUSPENDED);
                System.out.println("Admin: Suspended user " + userId + " - Reason: " + reason);
            }
        }
        
        @Override
        public void activateUser(String userId) {
            User user = users.get(userId);
            if (user != null) {
                user.setStatus(User.AccountStatus.ACTIVE);
                System.out.println("Admin: Activated user " + userId);
            }
        }
        
        @Override
        public void resetUserPassword(String userId) {
            System.out.println("Admin: Reset password for user " + userId);
        }
        
        @Override
        public void assignRole(String userId, String role) {
            User user = users.get(userId);
            if (user != null) {
                user.getRoles().add(role);
                System.out.println("Admin: Assigned role " + role + " to user " + userId);
            }
        }
        
        @Override
        public void removeRole(String userId, String role) {
            User user = users.get(userId);
            if (user != null) {
                user.getRoles().remove(role);
                System.out.println("Admin: Removed role " + role + " from user " + userId);
            }
        }
        
        @Override
        public List<User> getUsersByRole(String role) {
            return users.values().stream()
                .filter(u -> u.getRoles().contains(role))
                .collect(Collectors.toList());
        }
        
        @Override
        public SystemStats getSystemStats() {
            int total = users.size();
            int active = (int) users.values().stream()
                .filter(u -> u.getStatus() == User.AccountStatus.ACTIVE)
                .count();
            return new SystemStats(total, active);
        }
        
        @Override
        public void performMaintenance(MaintenanceType type) {
            System.out.println("System Admin: Performing " + type + " maintenance");
            systemConfig.put("maintenance_mode", "true");
            // Perform maintenance
            systemConfig.put("maintenance_mode", "false");
        }
        
        @Override
        public void updateSystemConfiguration(String key, String value) {
            systemConfig.put(key, value);
            System.out.println("System Admin: Updated config " + key + " = " + value);
        }
        
        @Override
        public Map<String, String> getSystemConfiguration() {
            return new HashMap<>(systemConfig);
        }
        
        @Override
        public void clearCache() {
            System.out.println("System Admin: Cache cleared");
        }
        
        @Override
        public void reindexSearch() {
            System.out.println("System Admin: Search index rebuilt");
        }
    }
    
    // Moderator service - implements moderation operations
    public static class ModeratorService implements ContentModerationOperations, UserModerationOperations {
        private Map<String, Content> contents;
        private Map<String, Report> reports;
        private Map<String, UserHistory> userHistories;
        
        public ModeratorService() {
            this.contents = new HashMap<>();
            this.reports = new HashMap<>();
            this.userHistories = new HashMap<>();
        }
        
        @Override
        public List<Content> getFlaggedContent() {
            System.out.println("Moderator: Getting flagged content");
            return contents.values().stream()
                .filter(c -> c.getStatus() == Content.ContentStatus.FLAGGED)
                .collect(Collectors.toList());
        }
        
        @Override
        public List<Content> getContentByUser(String userId) {
            return contents.values().stream()
                .filter(c -> c.userId.equals(userId))
                .collect(Collectors.toList());
        }
        
        @Override
        public void approveContent(String contentId) {
            Content content = contents.get(contentId);
            if (content != null) {
                content.setStatus(Content.ContentStatus.APPROVED);
                System.out.println("Moderator: Approved content " + contentId);
            }
        }
        
        @Override
        public void rejectContent(String contentId, String reason) {
            Content content = contents.get(contentId);
            if (content != null) {
                content.setStatus(Content.ContentStatus.REJECTED);
                System.out.println("Moderator: Rejected content " + contentId + " - " + reason);
            }
        }
        
        @Override
        public void flagContent(String contentId, String reason) {
            Content content = contents.get(contentId);
            if (content != null) {
                content.setStatus(Content.ContentStatus.FLAGGED);
                content.flags.add(reason);
                System.out.println("Moderator: Flagged content " + contentId);
            }
        }
        
        @Override
        public List<Report> getContentReports() {
            return new ArrayList<>(reports.values());
        }
        
        @Override
        public void resolveReport(String reportId, String resolution) {
            Report report = reports.get(reportId);
            if (report != null) {
                report.status = Report.ReportStatus.RESOLVED;
                System.out.println("Moderator: Resolved report " + reportId);
            }
        }
        
        @Override
        public void warnUser(String userId, String reason) {
            UserHistory history = userHistories.computeIfAbsent(userId, UserHistory::new);
            history.addWarning(reason);
            System.out.println("Moderator: Warned user " + userId + " - " + reason);
        }
        
        @Override
        public void banUser(String userId, String reason, int durationHours) {
            UserHistory history = userHistories.computeIfAbsent(userId, UserHistory::new);
            history.addBan(reason + " (Duration: " + durationHours + " hours)");
            System.out.println("Moderator: Banned user " + userId + " for " + durationHours + " hours");
        }
        
        @Override
        public void unbanUser(String userId) {
            System.out.println("Moderator: Unbanned user " + userId);
        }
        
        @Override
        public List<User> getBannedUsers() {
            // Would return list of banned users
            return new ArrayList<>();
        }
        
        @Override
        public List<Report> getUserReports() {
            return new ArrayList<>(reports.values());
        }
        
        @Override
        public UserHistory getUserHistory(String userId) {
            return userHistories.getOrDefault(userId, new UserHistory(userId));
        }
    }
    
    // Support service - implements support operations
    public static class SupportService implements SupportOperations {
        private Map<String, Ticket> tickets;
        
        public SupportService() {
            this.tickets = new HashMap<>();
        }
        
        @Override
        public List<Ticket> getOpenTickets() {
            return tickets.values().stream()
                .filter(t -> t.getStatus() == Ticket.TicketStatus.OPEN)
                .collect(Collectors.toList());
        }
        
        @Override
        public List<Ticket> getUserTickets(String userId) {
            return tickets.values().stream()
                .filter(t -> t.userId.equals(userId))
                .collect(Collectors.toList());
        }
        
        @Override
        public Ticket getTicket(String ticketId) {
            return tickets.get(ticketId);
        }
        
        @Override
        public void respondToTicket(String ticketId, String response) {
            Ticket ticket = tickets.get(ticketId);
            if (ticket != null) {
                ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
                System.out.println("Support: Responded to ticket " + ticketId);
            }
        }
        
        @Override
        public void escalateTicket(String ticketId, String escalationLevel) {
            Ticket ticket = tickets.get(ticketId);
            if (ticket != null) {
                ticket.setStatus(Ticket.TicketStatus.ESCALATED);
                System.out.println("Support: Escalated ticket " + ticketId + " to " + escalationLevel);
            }
        }
        
        @Override
        public void closeTicket(String ticketId, String resolution) {
            Ticket ticket = tickets.get(ticketId);
            if (ticket != null) {
                ticket.setStatus(Ticket.TicketStatus.CLOSED);
                System.out.println("Support: Closed ticket " + ticketId);
            }
        }
        
        @Override
        public void assignTicket(String ticketId, String supportAgentId) {
            Ticket ticket = tickets.get(ticketId);
            if (ticket != null) {
                ticket.assignedTo = supportAgentId;
                System.out.println("Support: Assigned ticket " + ticketId + " to agent " + supportAgentId);
            }
        }
    }
    
    // Client applications that use only the interfaces they need
    
    public static class UserDashboard {
        private final Authenticatable auth;
        private final UserProfileOperations profileOps;
        
        public UserDashboard(Authenticatable auth, UserProfileOperations profileOps) {
            this.auth = auth;
            this.profileOps = profileOps;
        }
        
        public void userWorkflow(String username, String password) {
            System.out.println("\n=== User Dashboard ===");
            Session session = auth.login(username, password);
            if (session != null) {
                User profile = profileOps.getProfile(session.getUserId());
                System.out.println("Welcome, " + profile.getUsername());
                
                // Update preferences
                Map<String, String> prefs = new HashMap<>();
                prefs.put("theme", "dark");
                prefs.put("notifications", "enabled");
                profileOps.updatePreferences(session.getUserId(), prefs);
                
                auth.logout(session.getSessionId());
            }
        }
    }
    
    public static class AdminPanel {
        private final UserAdminOperations userAdmin;
        private final SystemAdminOperations sysAdmin;
        
        public AdminPanel(UserAdminOperations userAdmin, SystemAdminOperations sysAdmin) {
            this.userAdmin = userAdmin;
            this.sysAdmin = sysAdmin;
        }
        
        public void adminWorkflow() {
            System.out.println("\n=== Admin Panel ===");
            
            // User management
            User newUser = userAdmin.createUser("new_user", "new@example.com", "password");
            userAdmin.assignRole(newUser.getId(), "PREMIUM");
            
            // System management
            SystemStats stats = sysAdmin.getSystemStats();
            System.out.println("System stats: " + stats.getTotalUsers() + " total users, " + 
                             stats.getActiveUsers() + " active");
            
            sysAdmin.updateSystemConfiguration("max_upload_size", "50MB");
        }
    }
    
    public static class ModeratorConsole {
        private final ContentModerationOperations contentMod;
        private final UserModerationOperations userMod;
        
        public ModeratorConsole(ContentModerationOperations contentMod, 
                               UserModerationOperations userMod) {
            this.contentMod = contentMod;
            this.userMod = userMod;
        }
        
        public void moderatorWorkflow() {
            System.out.println("\n=== Moderator Console ===");
            
            // Content moderation
            List<Content> flagged = contentMod.getFlaggedContent();
            System.out.println("Flagged content items: " + flagged.size());
            
            // User moderation
            userMod.warnUser("user123", "Inappropriate language");
            UserHistory history = userMod.getUserHistory("user123");
            System.out.println("User warnings: " + history.getWarnings().size());
        }
    }
    
    public static class SupportDesk {
        private final SupportOperations support;
        
        public SupportDesk(SupportOperations support) {
            this.support = support;
        }
        
        public void supportWorkflow() {
            System.out.println("\n=== Support Desk ===");
            
            List<Ticket> openTickets = support.getOpenTickets();
            System.out.println("Open tickets: " + openTickets.size());
            
            // Handle tickets
            for (Ticket ticket : openTickets) {
                support.assignTicket(ticket.getId(), "agent001");
                support.respondToTicket(ticket.getId(), "We're looking into this issue.");
            }
        }
    }
    
    /**
     * Demonstration of Interface Segregation Principle
     */
    public static void main(String[] args) {
        System.out.println("=== User Management System - ISP Demo ===\n");
        
        // Create services
        RegularUserService userService = new RegularUserService();
        AdminService adminService = new AdminService();
        ModeratorService moderatorService = new ModeratorService();
        SupportService supportService = new SupportService();
        
        System.out.println("1. REGULAR USER - Limited Interface Access");
        System.out.println("─".repeat(50));
        UserDashboard userDashboard = new UserDashboard(userService, userService);
        userDashboard.userWorkflow("john_doe", "password");
        
        System.out.println("\n2. ADMINISTRATOR - Admin Interface Access");
        System.out.println("─".repeat(50));
        AdminPanel adminPanel = new AdminPanel(adminService, adminService);
        adminPanel.adminWorkflow();
        
        System.out.println("\n3. MODERATOR - Moderation Interface Access");
        System.out.println("─".repeat(50));
        ModeratorConsole modConsole = new ModeratorConsole(moderatorService, moderatorService);
        modConsole.moderatorWorkflow();
        
        System.out.println("\n4. SUPPORT STAFF - Support Interface Access");
        System.out.println("─".repeat(50));
        SupportDesk supportDesk = new SupportDesk(supportService);
        supportDesk.supportWorkflow();
        
        System.out.println("\n5. BENEFITS OF ISP IN THIS DESIGN");
        System.out.println("─".repeat(50));
        System.out.println("✅ Each client sees only the operations it needs");
        System.out.println("✅ Regular users can't accidentally access admin operations");
        System.out.println("✅ Changes to admin features don't affect user clients");
        System.out.println("✅ Easy to test - mock only the interfaces you use");
        System.out.println("✅ Clear role boundaries enforced at compile time");
        System.out.println("✅ New roles can be added without affecting existing ones");
        System.out.println("✅ Security is improved through interface segregation");
        
        System.out.println("\n=== Demo Complete ===");
    }
}