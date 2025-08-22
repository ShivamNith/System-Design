package org.example.designPatterns.statePattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Document Workflow Example using State Pattern
 * 
 * This example demonstrates a document approval workflow with states:
 * - DraftState: Document being authored
 * - ReviewState: Document under review
 * - ApprovedState: Document approved, ready for publishing
 * - PublishedState: Document published and live
 * - RejectedState: Document rejected, needs revision
 * - ArchivedState: Document archived (final state)
 */

// User roles enum
enum UserRole {
    AUTHOR, REVIEWER, APPROVER, PUBLISHER, ADMIN
}

// User class
class User {
    private final String id;
    private final String name;
    private final UserRole role;

    public User(String id, String name, UserRole role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public UserRole getRole() { return role; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}

// Comment class for feedback
class Comment {
    private final String id;
    private final User author;
    private final String content;
    private final LocalDateTime timestamp;

    public Comment(String id, User author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public User getAuthor() { return author; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", 
            timestamp.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
            author.getName(), content);
    }
}

// Document state interface
interface DocumentState {
    void edit(Document document, User user, String content);
    void submitForReview(Document document, User user);
    void review(Document document, User user, boolean approved, String comments);
    void approve(Document document, User user);
    void publish(Document document, User user);
    void reject(Document document, User user, String reason);
    void archive(Document document, User user);
    void requestRevision(Document document, User user, String reason);
    String getStateName();
    Set<UserRole> getAllowedRoles();
}

// Document class (Context)
class Document {
    private final String id;
    private String title;
    private String content;
    private final User author;
    private DocumentState currentState;
    private final List<Comment> comments;
    private final List<String> revisionHistory;
    private final Map<String, LocalDateTime> stateTransitions;
    private User currentReviewer;
    private User currentApprover;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private int version;

    public Document(String id, String title, String content, User author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.comments = new ArrayList<>();
        this.revisionHistory = new ArrayList<>();
        this.stateTransitions = new HashMap<>();
        this.createdDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.version = 1;
        this.currentState = new DraftState();
        
        addRevision("Document created");
        recordStateTransition("Draft");
    }

    // State management
    public void setState(DocumentState state) {
        String previousState = currentState.getStateName();
        this.currentState = state;
        recordStateTransition(state.getStateName());
        log("State changed from " + previousState + " to " + state.getStateName());
    }

    public DocumentState getState() {
        return currentState;
    }

    // Document operations (delegated to current state)
    public void edit(User user, String newContent) {
        validateUserAction(user);
        currentState.edit(this, user, newContent);
    }

    public void submitForReview(User user) {
        validateUserAction(user);
        currentState.submitForReview(this, user);
    }

    public void review(User user, boolean approved, String comments) {
        validateUserAction(user);
        currentState.review(this, user, approved, comments);
    }

    public void approve(User user) {
        validateUserAction(user);
        currentState.approve(this, user);
    }

    public void publish(User user) {
        validateUserAction(user);
        currentState.publish(this, user);
    }

    public void reject(User user, String reason) {
        validateUserAction(user);
        currentState.reject(this, user, reason);
    }

    public void archive(User user) {
        validateUserAction(user);
        currentState.archive(this, user);
    }

    public void requestRevision(User user, String reason) {
        validateUserAction(user);
        currentState.requestRevision(this, user, reason);
    }

    // Internal methods
    public void updateContent(String newContent) {
        this.content = newContent;
        this.lastModified = LocalDateTime.now();
        this.version++;
        addRevision("Content updated to version " + version);
    }

    public void addComment(User user, String commentText) {
        Comment comment = new Comment(UUID.randomUUID().toString(), user, commentText);
        comments.add(comment);
        log("Comment added by " + user.getName());
    }

    private void addRevision(String description) {
        String revision = String.format("[%s] %s", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            description);
        revisionHistory.add(revision);
    }

    private void recordStateTransition(String stateName) {
        stateTransitions.put(stateName, LocalDateTime.now());
    }

    private void validateUserAction(User user) {
        if (!currentState.getAllowedRoles().contains(user.getRole())) {
            throw new IllegalStateException(
                "User " + user.getName() + " (" + user.getRole() + ") " +
                "is not authorized to perform this action in " + currentState.getStateName() + " state");
        }
    }

    private void log(String message) {
        System.out.println("[DOC-" + id + "] " + message);
    }

    // Getters and display methods
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public User getAuthor() { return author; }
    public List<Comment> getComments() { return new ArrayList<>(comments); }
    public int getVersion() { return version; }
    public User getCurrentReviewer() { return currentReviewer; }
    public User getCurrentApprover() { return currentApprover; }
    
    public void setCurrentReviewer(User reviewer) { this.currentReviewer = reviewer; }
    public void setCurrentApprover(User approver) { this.currentApprover = approver; }

    public void displayStatus() {
        System.out.println("\n=== Document Status ===");
        System.out.println("ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Current State: " + currentState.getStateName());
        System.out.println("Version: " + version);
        System.out.println("Created: " + createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Last Modified: " + lastModified.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        if (currentReviewer != null) {
            System.out.println("Current Reviewer: " + currentReviewer);
        }
        if (currentApprover != null) {
            System.out.println("Current Approver: " + currentApprover);
        }
        
        System.out.println("=======================");
    }

    public void displayComments() {
        System.out.println("\n=== Comments ===");
        if (comments.isEmpty()) {
            System.out.println("No comments yet.");
        } else {
            comments.forEach(System.out::println);
        }
        System.out.println("================");
    }

    public void displayHistory() {
        System.out.println("\n=== Revision History ===");
        revisionHistory.forEach(System.out::println);
        System.out.println("========================");
    }
}

// Concrete State Classes

class DraftState implements DocumentState {
    @Override
    public void edit(Document document, User user, String content) {
        if (user.getRole() != UserRole.AUTHOR && !user.equals(document.getAuthor())) {
            throw new IllegalStateException("Only the author can edit the document in draft state");
        }
        document.updateContent(content);
        System.out.println("Document content updated by " + user.getName());
    }

    @Override
    public void submitForReview(Document document, User user) {
        if (!user.equals(document.getAuthor())) {
            throw new IllegalStateException("Only the author can submit the document for review");
        }
        document.setState(new ReviewState());
        System.out.println("Document submitted for review by " + user.getName());
    }

    @Override
    public void review(Document document, User user, boolean approved, String comments) {
        throw new IllegalStateException("Cannot review document in draft state");
    }

    @Override
    public void approve(Document document, User user) {
        throw new IllegalStateException("Cannot approve document in draft state");
    }

    @Override
    public void publish(Document document, User user) {
        throw new IllegalStateException("Cannot publish document in draft state");
    }

    @Override
    public void reject(Document document, User user, String reason) {
        throw new IllegalStateException("Cannot reject document in draft state");
    }

    @Override
    public void archive(Document document, User user) {
        if (user.getRole() == UserRole.ADMIN || user.equals(document.getAuthor())) {
            document.setState(new ArchivedState());
            System.out.println("Document archived from draft state by " + user.getName());
        } else {
            throw new IllegalStateException("Only admin or author can archive draft document");
        }
    }

    @Override
    public void requestRevision(Document document, User user, String reason) {
        throw new IllegalStateException("Cannot request revision for document in draft state");
    }

    @Override
    public String getStateName() {
        return "Draft";
    }

    @Override
    public Set<UserRole> getAllowedRoles() {
        return Set.of(UserRole.AUTHOR, UserRole.ADMIN);
    }
}

class ReviewState implements DocumentState {
    @Override
    public void edit(Document document, User user, String content) {
        throw new IllegalStateException("Cannot edit document while under review");
    }

    @Override
    public void submitForReview(Document document, User user) {
        throw new IllegalStateException("Document is already under review");
    }

    @Override
    public void review(Document document, User user, boolean approved, String comments) {
        if (user.getRole() != UserRole.REVIEWER) {
            throw new IllegalStateException("Only reviewers can review documents");
        }

        document.setCurrentReviewer(user);
        document.addComment(user, "Review: " + comments);

        if (approved) {
            document.setState(new ApprovedState());
            System.out.println("Document approved by reviewer " + user.getName());
        } else {
            document.setState(new RejectedState());
            System.out.println("Document rejected by reviewer " + user.getName() + ": " + comments);
        }
    }

    @Override
    public void approve(Document document, User user) {
        if (user.getRole() == UserRole.APPROVER) {
            // Fast-track approval
            document.setCurrentApprover(user);
            document.setState(new ApprovedState());
            System.out.println("Document fast-track approved by " + user.getName());
        } else {
            throw new IllegalStateException("Only approvers can directly approve documents under review");
        }
    }

    @Override
    public void publish(Document document, User user) {
        throw new IllegalStateException("Cannot publish document under review");
    }

    @Override
    public void reject(Document document, User user, String reason) {
        if (user.getRole() == UserRole.REVIEWER || user.getRole() == UserRole.APPROVER) {
            document.setState(new RejectedState());
            document.addComment(user, "Rejected: " + reason);
            System.out.println("Document rejected by " + user.getName() + ": " + reason);
        } else {
            throw new IllegalStateException("Only reviewers or approvers can reject documents");
        }
    }

    @Override
    public void archive(Document document, User user) {
        if (user.getRole() == UserRole.ADMIN) {
            document.setState(new ArchivedState());
            System.out.println("Document archived from review state by admin " + user.getName());
        } else {
            throw new IllegalStateException("Only admin can archive document under review");
        }
    }

    @Override
    public void requestRevision(Document document, User user, String reason) {
        if (user.getRole() == UserRole.REVIEWER) {
            document.setState(new RejectedState());
            document.addComment(user, "Revision requested: " + reason);
            System.out.println("Revision requested by " + user.getName() + ": " + reason);
        } else {
            throw new IllegalStateException("Only reviewers can request revisions");
        }
    }

    @Override
    public String getStateName() {
        return "Review";
    }

    @Override
    public Set<UserRole> getAllowedRoles() {
        return Set.of(UserRole.REVIEWER, UserRole.APPROVER, UserRole.ADMIN);
    }
}

class ApprovedState implements DocumentState {
    @Override
    public void edit(Document document, User user, String content) {
        throw new IllegalStateException("Cannot edit approved document");
    }

    @Override
    public void submitForReview(Document document, User user) {
        throw new IllegalStateException("Document is already approved");
    }

    @Override
    public void review(Document document, User user, boolean approved, String comments) {
        throw new IllegalStateException("Document is already approved");
    }

    @Override
    public void approve(Document document, User user) {
        System.out.println("Document is already approved");
    }

    @Override
    public void publish(Document document, User user) {
        if (user.getRole() != UserRole.PUBLISHER && user.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only publishers or admins can publish approved documents");
        }
        document.setState(new PublishedState());
        System.out.println("Document published by " + user.getName());
    }

    @Override
    public void reject(Document document, User user, String reason) {
        if (user.getRole() == UserRole.APPROVER || user.getRole() == UserRole.ADMIN) {
            document.setState(new RejectedState());
            document.addComment(user, "Approval revoked: " + reason);
            System.out.println("Document approval revoked by " + user.getName() + ": " + reason);
        } else {
            throw new IllegalStateException("Only approvers or admins can revoke approval");
        }
    }

    @Override
    public void archive(Document document, User user) {
        if (user.getRole() == UserRole.ADMIN) {
            document.setState(new ArchivedState());
            System.out.println("Approved document archived by admin " + user.getName());
        } else {
            throw new IllegalStateException("Only admin can archive approved document");
        }
    }

    @Override
    public void requestRevision(Document document, User user, String reason) {
        if (user.getRole() == UserRole.APPROVER || user.getRole() == UserRole.ADMIN) {
            document.setState(new RejectedState());
            document.addComment(user, "Revision requested: " + reason);
            System.out.println("Revision requested for approved document by " + user.getName());
        } else {
            throw new IllegalStateException("Only approvers or admins can request revision");
        }
    }

    @Override
    public String getStateName() {
        return "Approved";
    }

    @Override
    public Set<UserRole> getAllowedRoles() {
        return Set.of(UserRole.PUBLISHER, UserRole.APPROVER, UserRole.ADMIN);
    }
}

class PublishedState implements DocumentState {
    @Override
    public void edit(Document document, User user, String content) {
        throw new IllegalStateException("Cannot edit published document");
    }

    @Override
    public void submitForReview(Document document, User user) {
        throw new IllegalStateException("Published document cannot be resubmitted for review");
    }

    @Override
    public void review(Document document, User user, boolean approved, String comments) {
        // Allow post-publication reviews for feedback
        if (user.getRole() == UserRole.REVIEWER || user.getRole() == UserRole.ADMIN) {
            document.addComment(user, "Post-publication review: " + comments);
            System.out.println("Post-publication review added by " + user.getName());
        } else {
            throw new IllegalStateException("Only reviewers or admins can review published documents");
        }
    }

    @Override
    public void approve(Document document, User user) {
        System.out.println("Document is already published");
    }

    @Override
    public void publish(Document document, User user) {
        System.out.println("Document is already published");
    }

    @Override
    public void reject(Document document, User user, String reason) {
        throw new IllegalStateException("Cannot reject published document");
    }

    @Override
    public void archive(Document document, User user) {
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.PUBLISHER) {
            document.setState(new ArchivedState());
            System.out.println("Published document archived by " + user.getName());
        } else {
            throw new IllegalStateException("Only admin or publisher can archive published document");
        }
    }

    @Override
    public void requestRevision(Document document, User user, String reason) {
        throw new IllegalStateException("Cannot request revision for published document");
    }

    @Override
    public String getStateName() {
        return "Published";
    }

    @Override
    public Set<UserRole> getAllowedRoles() {
        return Set.of(UserRole.PUBLISHER, UserRole.REVIEWER, UserRole.ADMIN);
    }
}

class RejectedState implements DocumentState {
    @Override
    public void edit(Document document, User user, String content) {
        if (user.equals(document.getAuthor()) || user.getRole() == UserRole.ADMIN) {
            document.updateContent(content);
            document.setState(new DraftState());
            System.out.println("Document revised and moved back to draft by " + user.getName());
        } else {
            throw new IllegalStateException("Only author or admin can edit rejected document");
        }
    }

    @Override
    public void submitForReview(Document document, User user) {
        if (user.equals(document.getAuthor())) {
            document.setState(new ReviewState());
            System.out.println("Rejected document resubmitted for review by " + user.getName());
        } else {
            throw new IllegalStateException("Only author can resubmit rejected document");
        }
    }

    @Override
    public void review(Document document, User user, boolean approved, String comments) {
        throw new IllegalStateException("Cannot review rejected document - must be resubmitted first");
    }

    @Override
    public void approve(Document document, User user) {
        throw new IllegalStateException("Cannot approve rejected document - must be resubmitted first");
    }

    @Override
    public void publish(Document document, User user) {
        throw new IllegalStateException("Cannot publish rejected document");
    }

    @Override
    public void reject(Document document, User user, String reason) {
        // Allow additional rejection comments
        document.addComment(user, "Additional rejection note: " + reason);
        System.out.println("Additional rejection comment added by " + user.getName());
    }

    @Override
    public void archive(Document document, User user) {
        if (user.getRole() == UserRole.ADMIN || user.equals(document.getAuthor())) {
            document.setState(new ArchivedState());
            System.out.println("Rejected document archived by " + user.getName());
        } else {
            throw new IllegalStateException("Only admin or author can archive rejected document");
        }
    }

    @Override
    public void requestRevision(Document document, User user, String reason) {
        // Allow additional revision requests
        document.addComment(user, "Additional revision request: " + reason);
        System.out.println("Additional revision request added by " + user.getName());
    }

    @Override
    public String getStateName() {
        return "Rejected";
    }

    @Override
    public Set<UserRole> getAllowedRoles() {
        return Set.of(UserRole.AUTHOR, UserRole.REVIEWER, UserRole.APPROVER, UserRole.ADMIN);
    }
}

class ArchivedState implements DocumentState {
    @Override
    public void edit(Document document, User user, String content) {
        throw new IllegalStateException("Cannot edit archived document");
    }

    @Override
    public void submitForReview(Document document, User user) {
        throw new IllegalStateException("Cannot submit archived document for review");
    }

    @Override
    public void review(Document document, User user, boolean approved, String comments) {
        // Allow archival reviews for audit purposes
        if (user.getRole() == UserRole.ADMIN) {
            document.addComment(user, "Archival review: " + comments);
            System.out.println("Archival review added by admin " + user.getName());
        } else {
            throw new IllegalStateException("Only admin can review archived documents");
        }
    }

    @Override
    public void approve(Document document, User user) {
        throw new IllegalStateException("Cannot approve archived document");
    }

    @Override
    public void publish(Document document, User user) {
        throw new IllegalStateException("Cannot publish archived document");
    }

    @Override
    public void reject(Document document, User user, String reason) {
        throw new IllegalStateException("Cannot reject archived document");
    }

    @Override
    public void archive(Document document, User user) {
        System.out.println("Document is already archived");
    }

    @Override
    public void requestRevision(Document document, User user, String reason) {
        throw new IllegalStateException("Cannot request revision for archived document");
    }

    @Override
    public String getStateName() {
        return "Archived";
    }

    @Override
    public Set<UserRole> getAllowedRoles() {
        return Set.of(UserRole.ADMIN);
    }
}

// Document workflow manager
class DocumentWorkflowManager {
    private final Map<String, Document> documents = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();

    public DocumentWorkflowManager() {
        initializeUsers();
    }

    private void initializeUsers() {
        users.put("alice", new User("alice", "Alice Johnson", UserRole.AUTHOR));
        users.put("bob", new User("bob", "Bob Smith", UserRole.REVIEWER));
        users.put("carol", new User("carol", "Carol Brown", UserRole.APPROVER));
        users.put("dave", new User("dave", "Dave Wilson", UserRole.PUBLISHER));
        users.put("admin", new User("admin", "System Admin", UserRole.ADMIN));
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public Document createDocument(String id, String title, String content, User author) {
        Document document = new Document(id, title, content, author);
        documents.put(id, document);
        return document;
    }

    public Document getDocument(String id) {
        return documents.get(id);
    }

    public List<Document> getDocumentsByState(String stateName) {
        return documents.values().stream()
            .filter(doc -> doc.getState().getStateName().equals(stateName))
            .toList();
    }

    public void displayAllDocuments() {
        System.out.println("\n=== All Documents ===");
        documents.values().forEach(doc -> 
            System.out.println(doc.getId() + ": " + doc.getTitle() + " [" + doc.getState().getStateName() + "]"));
        System.out.println("=====================");
    }
}

// Main demonstration class
public class DocumentWorkflowExample {
    public static void main(String[] args) {
        System.out.println("=== Document Workflow State Pattern Demo ===\n");

        DocumentWorkflowManager manager = new DocumentWorkflowManager();

        // Get users
        User alice = manager.getUser("alice");    // Author
        User bob = manager.getUser("bob");        // Reviewer
        User carol = manager.getUser("carol");    // Approver
        User dave = manager.getUser("dave");      // Publisher
        User admin = manager.getUser("admin");    // Admin

        // Demo 1: Normal workflow
        System.out.println("Demo 1: Normal Document Workflow");
        System.out.println("----------------------------------");

        Document doc1 = manager.createDocument("DOC001", "API Documentation", 
            "Initial API documentation content", alice);
        doc1.displayStatus();

        // Author edits document
        doc1.edit(alice, "Updated API documentation with examples");
        
        // Submit for review
        doc1.submitForReview(alice);
        doc1.displayStatus();

        // Reviewer reviews and approves
        doc1.review(bob, true, "Looks good, well documented");
        doc1.displayStatus();

        // Publisher publishes
        doc1.publish(dave);
        doc1.displayStatus();
        doc1.displayComments();

        // Demo 2: Rejection and revision workflow
        System.out.println("\nDemo 2: Rejection and Revision Workflow");
        System.out.println("---------------------------------------");

        Document doc2 = manager.createDocument("DOC002", "User Guide", 
            "Basic user guide content", alice);
        
        doc2.submitForReview(alice);
        doc2.review(bob, false, "Needs more detailed examples and screenshots");
        doc2.displayStatus();

        // Author revises rejected document
        doc2.edit(alice, "Revised user guide with detailed examples and screenshots");
        doc2.displayStatus();

        // Resubmit for review
        doc2.submitForReview(alice);
        doc2.review(bob, true, "Much better! Good examples added");
        doc2.publish(dave);

        // Demo 3: Fast-track approval
        System.out.println("\nDemo 3: Fast-track Approval");
        System.out.println("----------------------------");

        Document doc3 = manager.createDocument("DOC003", "Security Policy", 
            "Company security policy document", alice);
        
        doc3.submitForReview(alice);
        // Approver can fast-track approve during review
        doc3.approve(carol);
        doc3.displayStatus();

        // Demo 4: Approval revocation
        System.out.println("\nDemo 4: Approval Revocation");
        System.out.println("----------------------------");

        Document doc4 = manager.createDocument("DOC004", "Process Manual", 
            "Company process manual", alice);
        
        doc4.submitForReview(alice);
        doc4.review(bob, true, "Approved");
        doc4.displayStatus();

        // Approver revokes approval
        doc4.reject(carol, "Found compliance issues, needs legal review");
        doc4.displayStatus();

        // Demo 5: Administrative actions
        System.out.println("\nDemo 5: Administrative Actions");
        System.out.println("------------------------------");

        Document doc5 = manager.createDocument("DOC005", "Draft Policy", 
            "Draft policy document", alice);
        
        // Admin can archive draft directly
        doc5.archive(admin);
        doc5.displayStatus();

        // Demo 6: Post-publication review
        System.out.println("\nDemo 6: Post-publication Review");
        System.out.println("--------------------------------");

        // Add post-publication review to already published document
        doc1.review(bob, true, "Still accurate after 6 months review");
        doc1.displayComments();

        // Archive published document
        doc1.archive(dave);
        doc1.displayStatus();

        // Demo 7: Error handling
        System.out.println("\nDemo 7: Error Handling");
        System.out.println("----------------------");

        Document doc6 = manager.createDocument("DOC006", "Test Document", 
            "Test content", alice);

        try {
            // Try invalid operations
            doc6.approve(alice); // Author can't approve
        } catch (IllegalStateException e) {
            System.out.println("Error caught: " + e.getMessage());
        }

        try {
            doc6.publish(alice); // Can't publish from draft
        } catch (IllegalStateException e) {
            System.out.println("Error caught: " + e.getMessage());
        }

        try {
            doc6.edit(bob, "Unauthorized edit"); // Reviewer can't edit draft
        } catch (IllegalStateException e) {
            System.out.println("Error caught: " + e.getMessage());
        }

        // Final status
        System.out.println("\n=== Final Status ===");
        manager.displayAllDocuments();

        // Show documents by state
        System.out.println("\nPublished Documents:");
        manager.getDocumentsByState("Published").forEach(doc -> 
            System.out.println("- " + doc.getTitle()));

        System.out.println("\nArchived Documents:");
        manager.getDocumentsByState("Archived").forEach(doc -> 
            System.out.println("- " + doc.getTitle()));

        // Display revision history for a document
        System.out.println("\n=== Document History Example ===");
        doc2.displayHistory();
    }
}

/**
 * Key Features Demonstrated:
 * 
 * 1. State Management:
 *    - DraftState: Document being authored
 *    - ReviewState: Document under review
 *    - ApprovedState: Document approved
 *    - PublishedState: Document published and live
 *    - RejectedState: Document rejected, needs revision
 *    - ArchivedState: Document archived (final state)
 * 
 * 2. Role-based Access Control:
 *    - Each state defines allowed user roles
 *    - Actions validated against user permissions
 *    - Different workflows for different roles
 * 
 * 3. Business Logic:
 *    - Version control and revision tracking
 *    - Comment system for feedback
 *    - State transition logging
 *    - Audit trail maintenance
 * 
 * 4. Workflow Features:
 *    - Normal approval workflow
 *    - Rejection and revision cycle
 *    - Fast-track approval
 *    - Administrative overrides
 *    - Post-publication activities
 * 
 * 5. Error Handling:
 *    - Invalid state transitions
 *    - Unauthorized operations
 *    - Role-based restrictions
 *    - Clear error messages
 * 
 * Benefits of State Pattern in this example:
 * - Clear workflow definition
 * - Role-based security enforcement
 * - Easy to add new states or modify workflows
 * - Eliminates complex conditional logic
 * - Audit trail and compliance support
 * - Extensible for new document types or workflows
 */