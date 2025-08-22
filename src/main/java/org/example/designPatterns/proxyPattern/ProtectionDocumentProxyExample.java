package org.example.designPatterns.proxyPattern;

import java.util.*;

/**
 * Protection Proxy Example demonstrating access control
 * 
 * This example shows how a proxy can control access to sensitive
 * documents based on user permissions and roles.
 */

// User class for authentication
class User {
    private String username;
    private String role;
    private Set<String> permissions;
    
    public User(String username, String role) {
        this.username = username;
        this.role = role;
        this.permissions = new HashSet<>();
        assignDefaultPermissions();
    }
    
    private void assignDefaultPermissions() {
        switch (role.toLowerCase()) {
            case "admin":
                permissions.addAll(Arrays.asList("READ", "WRITE", "DELETE", "SHARE", "ADMIN"));
                break;
            case "editor":
                permissions.addAll(Arrays.asList("READ", "WRITE", "SHARE"));
                break;
            case "viewer":
                permissions.add("READ");
                break;
            case "guest":
                // No default permissions
                break;
            default:
                permissions.add("READ");
        }
    }
    
    public void addPermission(String permission) {
        permissions.add(permission);
    }
    
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    // Getters
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public Set<String> getPermissions() { return new HashSet<>(permissions); }
}

// Subject interface
interface Document {
    String getContent();
    void editContent(String newContent, User user);
    void deleteDocument(User user);
    void shareDocument(String recipient, User user);
    String getMetadata();
    void addComment(String comment, User user);
    List<String> getComments();
}

// RealSubject - the actual document
class RealDocument implements Document {
    private String title;
    private String content;
    private String owner;
    private Date createdDate;
    private Date lastModified;
    private List<String> comments;
    private List<String> collaborators;
    
    public RealDocument(String title, String content, String owner) {
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.createdDate = new Date();
        this.lastModified = new Date();
        this.comments = new ArrayList<>();
        this.collaborators = new ArrayList<>();
        this.collaborators.add(owner);
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public void editContent(String newContent, User user) {
        this.content = newContent;
        this.lastModified = new Date();
        System.out.println("Document '" + title + "' content updated by " + user.getUsername());
    }
    
    @Override
    public void deleteDocument(User user) {
        System.out.println("Document '" + title + "' deleted by " + user.getUsername());
        // In real implementation, this would remove the document
    }
    
    @Override
    public void shareDocument(String recipient, User user) {
        if (!collaborators.contains(recipient)) {
            collaborators.add(recipient);
        }
        System.out.println("Document '" + title + "' shared with " + recipient + " by " + user.getUsername());
    }
    
    @Override
    public String getMetadata() {
        return String.format("Title: %s, Owner: %s, Created: %s, Last Modified: %s, Collaborators: %s",
                           title, owner, createdDate, lastModified, collaborators);
    }
    
    @Override
    public void addComment(String comment, User user) {
        String timestampedComment = String.format("[%s] %s: %s", 
                                                 new Date(), user.getUsername(), comment);
        comments.add(timestampedComment);
        System.out.println("Comment added to '" + title + "' by " + user.getUsername());
    }
    
    @Override
    public List<String> getComments() {
        return new ArrayList<>(comments);
    }
    
    // Additional getters
    public String getTitle() { return title; }
    public String getOwner() { return owner; }
    public List<String> getCollaborators() { return new ArrayList<>(collaborators); }
}

// Protection Proxy
class DocumentProxy implements Document {
    private RealDocument realDocument;
    private String documentId;
    private Map<String, Object> documentInfo;
    private List<String> accessLog;
    
    public DocumentProxy(String documentId, String title, String content, String owner) {
        this.documentId = documentId;
        this.documentInfo = new HashMap<>();
        this.documentInfo.put("title", title);
        this.documentInfo.put("owner", owner);
        this.documentInfo.put("classification", "CONFIDENTIAL");
        this.accessLog = new ArrayList<>();
        
        // Lazy initialization - don't create real document until needed
        this.realDocument = null;
    }
    
    @Override
    public String getContent() {
        User currentUser = getCurrentUser();
        if (!hasReadPermission(currentUser)) {
            logAccess(currentUser, "READ", false, "Insufficient permissions");
            throw new SecurityException("Access denied: READ permission required");
        }
        
        logAccess(currentUser, "READ", true, "Content accessed");
        ensureDocumentLoaded();
        return realDocument.getContent();
    }
    
    @Override
    public void editContent(String newContent, User user) {
        if (!hasWritePermission(user)) {
            logAccess(user, "WRITE", false, "Insufficient permissions");
            throw new SecurityException("Access denied: WRITE permission required");
        }
        
        logAccess(user, "WRITE", true, "Content modified");
        ensureDocumentLoaded();
        realDocument.editContent(newContent, user);
    }
    
    @Override
    public void deleteDocument(User user) {
        if (!hasDeletePermission(user)) {
            logAccess(user, "DELETE", false, "Insufficient permissions");
            throw new SecurityException("Access denied: DELETE permission required or not document owner");
        }
        
        logAccess(user, "DELETE", true, "Document deleted");
        ensureDocumentLoaded();
        realDocument.deleteDocument(user);
    }
    
    @Override
    public void shareDocument(String recipient, User user) {
        if (!hasSharePermission(user)) {
            logAccess(user, "SHARE", false, "Insufficient permissions");
            throw new SecurityException("Access denied: SHARE permission required");
        }
        
        logAccess(user, "SHARE", true, "Document shared with " + recipient);
        ensureDocumentLoaded();
        realDocument.shareDocument(recipient, user);
    }
    
    @Override
    public String getMetadata() {
        User currentUser = getCurrentUser();
        if (!hasReadPermission(currentUser)) {
            // Return limited metadata for unauthorized users
            return String.format("Title: %s, Classification: %s", 
                               documentInfo.get("title"), documentInfo.get("classification"));
        }
        
        logAccess(currentUser, "READ_METADATA", true, "Metadata accessed");
        ensureDocumentLoaded();
        return realDocument.getMetadata();
    }
    
    @Override
    public void addComment(String comment, User user) {
        if (!hasReadPermission(user)) {
            logAccess(user, "COMMENT", false, "Insufficient permissions");
            throw new SecurityException("Access denied: READ permission required to comment");
        }
        
        logAccess(user, "COMMENT", true, "Comment added");
        ensureDocumentLoaded();
        realDocument.addComment(comment, user);
    }
    
    @Override
    public List<String> getComments() {
        User currentUser = getCurrentUser();
        if (!hasReadPermission(currentUser)) {
            logAccess(currentUser, "READ_COMMENTS", false, "Insufficient permissions");
            return Arrays.asList("Access denied: READ permission required");
        }
        
        logAccess(currentUser, "READ_COMMENTS", true, "Comments accessed");
        ensureDocumentLoaded();
        return realDocument.getComments();
    }
    
    // Permission checking methods
    private boolean hasReadPermission(User user) {
        return user.hasPermission("READ") && (isOwner(user) || isCollaborator(user) || user.hasPermission("ADMIN"));
    }
    
    private boolean hasWritePermission(User user) {
        return user.hasPermission("WRITE") && (isOwner(user) || isCollaborator(user) || user.hasPermission("ADMIN"));
    }
    
    private boolean hasDeletePermission(User user) {
        return user.hasPermission("DELETE") && (isOwner(user) || user.hasPermission("ADMIN"));
    }
    
    private boolean hasSharePermission(User user) {
        return user.hasPermission("SHARE") && (isOwner(user) || user.hasPermission("ADMIN"));
    }
    
    private boolean isOwner(User user) {
        return user.getUsername().equals(documentInfo.get("owner"));
    }
    
    private boolean isCollaborator(User user) {
        ensureDocumentLoaded();
        return realDocument.getCollaborators().contains(user.getUsername());
    }
    
    private void ensureDocumentLoaded() {
        if (realDocument == null) {
            // In real implementation, this would load from database/file system
            realDocument = new RealDocument(
                (String) documentInfo.get("title"),
                "This is confidential document content...",
                (String) documentInfo.get("owner")
            );
        }
    }
    
    private User getCurrentUser() {
        // In real implementation, this would get current user from security context
        return UserSession.getCurrentUser();
    }
    
    private void logAccess(User user, String operation, boolean success, String details) {
        String logEntry = String.format("[%s] User: %s, Operation: %s, Success: %s, Details: %s",
                                      new Date(), user.getUsername(), operation, success, details);
        accessLog.add(logEntry);
        System.out.println("Security Log: " + logEntry);
    }
    
    // Additional proxy methods
    public List<String> getAccessLog() {
        return new ArrayList<>(accessLog);
    }
    
    public void printSecurityReport() {
        System.out.println("\n=== Security Report for Document: " + documentInfo.get("title") + " ===");
        for (String logEntry : accessLog) {
            System.out.println(logEntry);
        }
    }
}

// Simple user session simulation
class UserSession {
    private static User currentUser;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("User session started: " + user.getUsername() + " (" + user.getRole() + ")");
    }
    
    public static User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in");
        }
        return currentUser;
    }
    
    public static void logout() {
        if (currentUser != null) {
            System.out.println("User session ended: " + currentUser.getUsername());
            currentUser = null;
        }
    }
}

// Client code
public class ProtectionDocumentProxyExample {
    public static void main(String[] args) {
        System.out.println("=== Protection Proxy Pattern: Document Access Control ===\n");
        
        // Create users with different roles
        User admin = new User("alice", "admin");
        User editor = new User("bob", "editor");
        User viewer = new User("charlie", "viewer");
        User guest = new User("diana", "guest");
        
        System.out.println("=== Users Created ===");
        System.out.println("Admin: " + admin.getUsername() + " - " + admin.getPermissions());
        System.out.println("Editor: " + editor.getUsername() + " - " + editor.getPermissions());
        System.out.println("Viewer: " + viewer.getUsername() + " - " + viewer.getPermissions());
        System.out.println("Guest: " + guest.getUsername() + " - " + guest.getPermissions());
        
        // Create document proxy
        DocumentProxy document = new DocumentProxy("DOC001", "Company Strategy 2024", 
                                                  "Strategic planning document...", "alice");
        
        // Test different user access scenarios
        System.out.println("\n=== Access Control Testing ===");
        
        // Admin access (should work for everything)
        System.out.println("\n--- Admin Access ---");
        UserSession.setCurrentUser(admin);
        try {
            System.out.println("Content: " + document.getContent().substring(0, 30) + "...");
            document.editContent("Updated strategy document content...", admin);
            document.addComment("Initial review completed", admin);
            document.shareDocument("bob", admin);
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
        
        // Editor access (read, write, comment, share)
        System.out.println("\n--- Editor Access ---");
        UserSession.setCurrentUser(editor);
        try {
            System.out.println("Content: " + document.getContent().substring(0, 30) + "...");
            document.editContent("Editor's modifications...", editor);
            document.addComment("Reviewed and updated", editor);
            document.shareDocument("charlie", editor);
            // This should fail
            document.deleteDocument(editor);
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
        
        // Viewer access (read only)
        System.out.println("\n--- Viewer Access ---");
        UserSession.setCurrentUser(viewer);
        try {
            System.out.println("Content: " + document.getContent().substring(0, 30) + "...");
            document.addComment("Looks good to me", viewer);
            // These should fail
            document.editContent("Viewer trying to edit...", viewer);
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
        
        try {
            document.shareDocument("diana", viewer);
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
        
        // Guest access (no permissions)
        System.out.println("\n--- Guest Access ---");
        UserSession.setCurrentUser(guest);
        try {
            System.out.println("Metadata: " + document.getMetadata());
            document.getContent();
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
        
        // Show security report
        document.printSecurityReport();
        
        // Demonstrate metadata access for unauthorized users
        System.out.println("\n=== Limited Metadata Access ===");
        System.out.println("Guest can see: " + document.getMetadata());
        
        // Show comments
        UserSession.setCurrentUser(admin);
        System.out.println("\n=== Comments (Admin View) ===");
        List<String> comments = document.getComments();
        for (String comment : comments) {
            System.out.println(comment);
        }
        
        UserSession.logout();
        
        System.out.println("\n=== Protection Proxy Benefits ===");
        System.out.println("1. Centralized access control - all permissions checked in proxy");
        System.out.println("2. Security logging - all access attempts logged");
        System.out.println("3. Lazy loading - real document loaded only when authorized access occurs");
        System.out.println("4. Transparent security - client code doesn't handle security logic");
        System.out.println("5. Role-based access - different permissions for different user roles");
        System.out.println("6. Audit trail - complete log of who accessed what and when");
    }
}