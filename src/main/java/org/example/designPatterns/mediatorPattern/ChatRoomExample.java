package org.example.designPatterns.mediatorPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mediator Pattern Example: Chat Room System
 * 
 * This example demonstrates the Mediator Pattern with a comprehensive chat room system
 * that supports multiple rooms, private messages, user management, and message history.
 */

// Message class to represent chat messages
class ChatMessage {
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private MessageType type;
    private String recipient; // For private messages
    
    public enum MessageType {
        PUBLIC, PRIVATE, SYSTEM, BROADCAST
    }
    
    public ChatMessage(String sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatMessage(String sender, String content, MessageType type, String recipient) {
        this(sender, content, type);
        this.recipient = recipient;
    }
    
    // Getters
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRecipient() { return recipient; }
    
    @Override
    public String toString() {
        String timeStr = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        switch (type) {
            case PRIVATE:
                return String.format("[%s] %s -> %s (private): %s", timeStr, sender, recipient, content);
            case SYSTEM:
                return String.format("[%s] SYSTEM: %s", timeStr, content);
            case BROADCAST:
                return String.format("[%s] BROADCAST from %s: %s", timeStr, sender, content);
            default:
                return String.format("[%s] %s: %s", timeStr, sender, content);
        }
    }
}

// User status enum
enum UserStatus {
    ONLINE, AWAY, BUSY, OFFLINE
}

// Mediator interface for chat system
interface ChatMediator {
    void sendMessage(ChatMessage message);
    void sendPrivateMessage(String from, String to, String message);
    void broadcastMessage(String from, String message);
    void addUser(ChatUser user);
    void removeUser(ChatUser user);
    void changeUserStatus(String username, UserStatus status);
    List<String> getOnlineUsers();
    List<ChatMessage> getMessageHistory(int limit);
    void createRoom(String roomName);
    void joinRoom(String username, String roomName);
    void leaveRoom(String username, String roomName);
}

// Concrete mediator - Chat Room
class ChatRoom implements ChatMediator {
    private String roomName;
    private Map<String, ChatUser> users;
    private List<ChatMessage> messageHistory;
    private Map<String, UserStatus> userStatuses;
    private Set<String> moderators;
    private Map<String, Set<String>> userRooms; // User -> Set of rooms
    private Map<String, ChatRoom> subRooms;     // Sub-rooms within this room
    
    public ChatRoom(String roomName) {
        this.roomName = roomName;
        this.users = new ConcurrentHashMap<>();
        this.messageHistory = new CopyOnWriteArrayList<>();
        this.userStatuses = new ConcurrentHashMap<>();
        this.moderators = ConcurrentHashMap.newKeySet();
        this.userRooms = new ConcurrentHashMap<>();
        this.subRooms = new ConcurrentHashMap<>();
    }
    
    @Override
    public void sendMessage(ChatMessage message) {
        // Store message in history
        messageHistory.add(message);
        
        // Broadcast to all users in the room
        for (ChatUser user : users.values()) {
            if (!user.getUsername().equals(message.getSender())) {
                user.receive(message);
            }
        }
        
        System.out.println("[" + roomName + "] " + message);
    }
    
    @Override
    public void sendPrivateMessage(String from, String to, String message) {
        ChatUser sender = users.get(from);
        ChatUser recipient = users.get(to);
        
        if (sender == null) {
            System.out.println("Error: Sender " + from + " not found in room " + roomName);
            return;
        }
        
        if (recipient == null) {
            System.out.println("Error: Recipient " + to + " not found in room " + roomName);
            return;
        }
        
        if (userStatuses.get(to) == UserStatus.BUSY) {
            sender.receive(new ChatMessage("SYSTEM", to + " is currently busy and may not respond", 
                                         ChatMessage.MessageType.SYSTEM));
            return;
        }
        
        ChatMessage privateMessage = new ChatMessage(from, message, ChatMessage.MessageType.PRIVATE, to);
        messageHistory.add(privateMessage);
        
        // Send to recipient
        recipient.receive(privateMessage);
        
        // Confirm to sender
        sender.receive(new ChatMessage("SYSTEM", "Private message sent to " + to, 
                                     ChatMessage.MessageType.SYSTEM));
        
        System.out.println("[" + roomName + "] " + privateMessage);
    }
    
    @Override
    public void broadcastMessage(String from, String message) {
        if (!moderators.contains(from)) {
            ChatUser sender = users.get(from);
            if (sender != null) {
                sender.receive(new ChatMessage("SYSTEM", "Only moderators can broadcast messages", 
                                             ChatMessage.MessageType.SYSTEM));
            }
            return;
        }
        
        ChatMessage broadcast = new ChatMessage(from, message, ChatMessage.MessageType.BROADCAST);
        messageHistory.add(broadcast);
        
        // Send to all users
        for (ChatUser user : users.values()) {
            user.receive(broadcast);
        }
        
        System.out.println("[" + roomName + "] " + broadcast);
    }
    
    @Override
    public void addUser(ChatUser user) {
        users.put(user.getUsername(), user);
        userStatuses.put(user.getUsername(), UserStatus.ONLINE);
        
        // Notify other users
        ChatMessage joinMessage = new ChatMessage("SYSTEM", 
            user.getUsername() + " has joined the room", ChatMessage.MessageType.SYSTEM);
        sendMessage(joinMessage);
        
        // Send welcome message to new user
        user.receive(new ChatMessage("SYSTEM", 
            "Welcome to " + roomName + "! There are " + users.size() + " users online.", 
            ChatMessage.MessageType.SYSTEM));
        
        // Send recent message history
        List<ChatMessage> recentMessages = getMessageHistory(5);
        if (!recentMessages.isEmpty()) {
            user.receive(new ChatMessage("SYSTEM", "--- Recent Messages ---", ChatMessage.MessageType.SYSTEM));
            for (ChatMessage msg : recentMessages) {
                user.receive(msg);
            }
        }
    }
    
    @Override
    public void removeUser(ChatUser user) {
        if (users.remove(user.getUsername()) != null) {
            userStatuses.remove(user.getUsername());
            moderators.remove(user.getUsername());
            
            // Notify other users
            ChatMessage leaveMessage = new ChatMessage("SYSTEM", 
                user.getUsername() + " has left the room", ChatMessage.MessageType.SYSTEM);
            sendMessage(leaveMessage);
        }
    }
    
    @Override
    public void changeUserStatus(String username, UserStatus status) {
        if (users.containsKey(username)) {
            UserStatus oldStatus = userStatuses.put(username, status);
            if (oldStatus != status) {
                ChatMessage statusMessage = new ChatMessage("SYSTEM", 
                    username + " is now " + status.toString().toLowerCase(), 
                    ChatMessage.MessageType.SYSTEM);
                sendMessage(statusMessage);
            }
        }
    }
    
    @Override
    public List<String> getOnlineUsers() {
        List<String> onlineUsers = new ArrayList<>();
        for (Map.Entry<String, UserStatus> entry : userStatuses.entrySet()) {
            if (entry.getValue() == UserStatus.ONLINE) {
                onlineUsers.add(entry.getKey());
            }
        }
        return onlineUsers;
    }
    
    @Override
    public List<ChatMessage> getMessageHistory(int limit) {
        int start = Math.max(0, messageHistory.size() - limit);
        return new ArrayList<>(messageHistory.subList(start, messageHistory.size()));
    }
    
    @Override
    public void createRoom(String roomName) {
        if (!subRooms.containsKey(roomName)) {
            ChatRoom subRoom = new ChatRoom(this.roomName + "/" + roomName);
            subRooms.put(roomName, subRoom);
            
            ChatMessage roomCreated = new ChatMessage("SYSTEM", 
                "Sub-room '" + roomName + "' has been created", ChatMessage.MessageType.SYSTEM);
            sendMessage(roomCreated);
        }
    }
    
    @Override
    public void joinRoom(String username, String roomName) {
        ChatRoom targetRoom = subRooms.get(roomName);
        if (targetRoom != null && users.containsKey(username)) {
            ChatUser user = users.get(username);
            targetRoom.addUser(user);
            
            userRooms.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet()).add(roomName);
        }
    }
    
    @Override
    public void leaveRoom(String username, String roomName) {
        ChatRoom targetRoom = subRooms.get(roomName);
        if (targetRoom != null && users.containsKey(username)) {
            ChatUser user = users.get(username);
            targetRoom.removeUser(user);
            
            Set<String> userRoomSet = userRooms.get(username);
            if (userRoomSet != null) {
                userRoomSet.remove(roomName);
            }
        }
    }
    
    public void addModerator(String username) {
        if (users.containsKey(username)) {
            moderators.add(username);
            ChatMessage modMessage = new ChatMessage("SYSTEM", 
                username + " has been granted moderator privileges", ChatMessage.MessageType.SYSTEM);
            sendMessage(modMessage);
        }
    }
    
    public String getRoomName() {
        return roomName;
    }
    
    public int getUserCount() {
        return users.size();
    }
    
    public void printRoomStatus() {
        System.out.println("\n=== " + roomName + " Status ===");
        System.out.println("Users: " + users.size());
        System.out.println("Messages: " + messageHistory.size());
        System.out.println("Moderators: " + moderators.size());
        System.out.println("Sub-rooms: " + subRooms.size());
        System.out.println("Online users: " + getOnlineUsers());
        System.out.println("===========================\n");
    }
}

// Abstract colleague class
abstract class ChatUser {
    protected String username;
    protected ChatMediator chatRoom;
    protected UserStatus status;
    protected List<ChatMessage> privateMessages;
    
    public ChatUser(String username, ChatMediator chatRoom) {
        this.username = username;
        this.chatRoom = chatRoom;
        this.status = UserStatus.ONLINE;
        this.privateMessages = new ArrayList<>();
        chatRoom.addUser(this);
    }
    
    public String getUsername() { return username; }
    public UserStatus getStatus() { return status; }
    
    public void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(username, message, ChatMessage.MessageType.PUBLIC);
        chatRoom.sendMessage(chatMessage);
    }
    
    public void sendPrivateMessage(String to, String message) {
        chatRoom.sendPrivateMessage(username, to, message);
    }
    
    public void broadcastMessage(String message) {
        chatRoom.broadcastMessage(username, message);
    }
    
    public void changeStatus(UserStatus newStatus) {
        this.status = newStatus;
        chatRoom.changeUserStatus(username, newStatus);
    }
    
    public void leaveRoom() {
        chatRoom.removeUser(this);
    }
    
    public abstract void receive(ChatMessage message);
}

// Concrete colleague - Regular User
class RegularUser extends ChatUser {
    
    public RegularUser(String username, ChatMediator chatRoom) {
        super(username, chatRoom);
    }
    
    @Override
    public void receive(ChatMessage message) {
        if (message.getType() == ChatMessage.MessageType.PRIVATE && 
            message.getRecipient().equals(username)) {
            privateMessages.add(message);
        }
        
        System.out.println("[User: " + username + "] " + message);
        
        // Auto-response for private messages when busy
        if (message.getType() == ChatMessage.MessageType.PRIVATE && 
            status == UserStatus.BUSY && !message.getSender().equals("SYSTEM")) {
            chatRoom.sendPrivateMessage(username, message.getSender(), 
                "I'm currently busy, I'll get back to you later.");
        }
    }
}

// Concrete colleague - Bot User
class BotUser extends ChatUser {
    private Map<String, String> responses;
    
    public BotUser(String username, ChatMediator chatRoom) {
        super(username, chatRoom);
        initializeResponses();
    }
    
    private void initializeResponses() {
        responses = new HashMap<>();
        responses.put("hello", "Hello! How can I help you today?");
        responses.put("help", "Available commands: /users, /time, /status, /joke");
        responses.put("time", "Current time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        responses.put("joke", "Why don't scientists trust atoms? Because they make up everything!");
        responses.put("users", "Use /users to see online users");
    }
    
    @Override
    public void receive(ChatMessage message) {
        System.out.println("[Bot: " + username + "] " + message);
        
        // Respond to mentions or private messages
        if ((message.getContent().toLowerCase().contains("@" + username.toLowerCase()) || 
             message.getType() == ChatMessage.MessageType.PRIVATE) &&
            !message.getSender().equals("SYSTEM") && !message.getSender().equals(username)) {
            
            processCommand(message);
        }
    }
    
    private void processCommand(ChatMessage message) {
        String content = message.getContent().toLowerCase();
        String response = null;
        
        for (String keyword : responses.keySet()) {
            if (content.contains(keyword)) {
                response = responses.get(keyword);
                break;
            }
        }
        
        if (content.contains("/users")) {
            List<String> onlineUsers = chatRoom.getOnlineUsers();
            response = "Online users: " + String.join(", ", onlineUsers);
        }
        
        if (response != null) {
            if (message.getType() == ChatMessage.MessageType.PRIVATE) {
                chatRoom.sendPrivateMessage(username, message.getSender(), response);
            } else {
                chatRoom.sendMessage(new ChatMessage(username, response, ChatMessage.MessageType.PUBLIC));
            }
        }
    }
}

// Concrete colleague - Admin User
class AdminUser extends ChatUser {
    
    public AdminUser(String username, ChatMediator chatRoom) {
        super(username, chatRoom);
        // Admins are automatically moderators
        if (chatRoom instanceof ChatRoom) {
            ((ChatRoom) chatRoom).addModerator(username);
        }
    }
    
    @Override
    public void receive(ChatMessage message) {
        System.out.println("[Admin: " + username + "] " + message);
        
        // Log all messages for moderation
        if (message.getType() != ChatMessage.MessageType.SYSTEM) {
            // In real implementation, this would log to a file or database
        }
    }
    
    public void createRoom(String roomName) {
        chatRoom.createRoom(roomName);
    }
    
    public void kickUser(String targetUser) {
        // In a real implementation, this would remove the user
        chatRoom.sendMessage(new ChatMessage("SYSTEM", 
            targetUser + " has been kicked by " + username, ChatMessage.MessageType.SYSTEM));
    }
}

// Client code demonstrating the Mediator Pattern
public class ChatRoomExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Chat Room Mediator Pattern Demo ===\n");
        
        // Create main chat room (mediator)
        ChatRoom mainRoom = new ChatRoom("General Chat");
        
        // Create users (colleagues)
        RegularUser alice = new RegularUser("Alice", mainRoom);
        RegularUser bob = new RegularUser("Bob", mainRoom);
        AdminUser admin = new AdminUser("Admin", mainRoom);
        BotUser chatBot = new BotUser("ChatBot", mainRoom);
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Public Messages ===");
        alice.sendMessage("Hello everyone!");
        bob.sendMessage("Hi Alice! How are you?");
        alice.sendMessage("I'm doing great, thanks!");
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Bot Interactions ===");
        alice.sendMessage("@ChatBot hello");
        bob.sendMessage("@ChatBot help");
        alice.sendMessage("@ChatBot time");
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Private Messages ===");
        alice.sendPrivateMessage("Bob", "Hey Bob, want to grab lunch later?");
        bob.sendPrivateMessage("Alice", "Sure! How about 12 PM?");
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Status Changes ===");
        bob.changeStatus(UserStatus.BUSY);
        alice.sendPrivateMessage("Bob", "Are you still free for lunch?");
        
        Thread.sleep(1000);
        
        bob.changeStatus(UserStatus.ONLINE);
        
        System.out.println("\n=== Admin Functions ===");
        admin.broadcastMessage("Welcome to our chat room! Please be respectful.");
        admin.createRoom("Tech Discussion");
        
        Thread.sleep(1000);
        
        System.out.println("\n=== More Users Join ===");
        RegularUser charlie = new RegularUser("Charlie", mainRoom);
        RegularUser diana = new RegularUser("Diana", mainRoom);
        
        Thread.sleep(1000);
        
        charlie.sendMessage("Hello everyone! I'm new here.");
        diana.sendMessage("Welcome Charlie!");
        
        System.out.println("\n=== Bot Commands ===");
        charlie.sendPrivateMessage("ChatBot", "/users");
        diana.sendMessage("@ChatBot joke");
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Complex Interactions ===");
        alice.sendMessage("Has anyone tried the new restaurant downtown?");
        bob.sendMessage("Which one? The Italian place?");
        charlie.sendMessage("I heard it's really good!");
        diana.sendPrivateMessage("Alice", "I went there last week, it was amazing!");
        
        Thread.sleep(1000);
        
        System.out.println("\n=== User Leaves ===");
        charlie.changeStatus(UserStatus.AWAY);
        charlie.leaveRoom();
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Final Interactions ===");
        alice.sendMessage("Charlie left, hope he comes back soon");
        admin.sendMessage("Thanks everyone for the great conversation!");
        
        Thread.sleep(1000);
        
        mainRoom.printRoomStatus();
        
        System.out.println("=== Message History ===");
        List<ChatMessage> history = mainRoom.getMessageHistory(5);
        for (ChatMessage msg : history) {
            System.out.println("History: " + msg);
        }
    }
}

/*
Expected Output:
=== Chat Room Mediator Pattern Demo ===

[General Chat] [timestamp] SYSTEM: Alice has joined the room
[User: Alice] [timestamp] SYSTEM: Welcome to General Chat! There are 1 users online.
[General Chat] [timestamp] SYSTEM: Bob has joined the room
[User: Alice] [timestamp] SYSTEM: Bob has joined the room
[User: Bob] [timestamp] SYSTEM: Welcome to General Chat! There are 2 users online.
[General Chat] [timestamp] SYSTEM: Admin has joined the room
[User: Alice] [timestamp] SYSTEM: Admin has joined the room
[User: Bob] [timestamp] SYSTEM: Admin has joined the room
[Admin: Admin] [timestamp] SYSTEM: Welcome to General Chat! There are 3 users online.
[General Chat] [timestamp] SYSTEM: Admin has been granted moderator privileges
[User: Alice] [timestamp] SYSTEM: Admin has been granted moderator privileges
[User: Bob] [timestamp] SYSTEM: Admin has been granted moderator privileges
[Admin: Admin] [timestamp] SYSTEM: Admin has been granted moderator privileges
[General Chat] [timestamp] SYSTEM: ChatBot has joined the room
[User: Alice] [timestamp] SYSTEM: ChatBot has joined the room
[User: Bob] [timestamp] SYSTEM: ChatBot has joined the room
[Admin: Admin] [timestamp] SYSTEM: ChatBot has joined the room
[Bot: ChatBot] [timestamp] SYSTEM: Welcome to General Chat! There are 4 users online.

=== Public Messages ===
[General Chat] [timestamp] Alice: Hello everyone!
[User: Bob] [timestamp] Alice: Hello everyone!
[Admin: Admin] [timestamp] Alice: Hello everyone!
[Bot: ChatBot] [timestamp] Alice: Hello everyone!
[General Chat] [timestamp] Bob: Hi Alice! How are you?
[User: Alice] [timestamp] Bob: Hi Alice! How are you?
[Admin: Admin] [timestamp] Bob: Hi Alice! How are you?
[Bot: ChatBot] [timestamp] Bob: Hi Alice! How are you?
[General Chat] [timestamp] Alice: I'm doing great, thanks!
[User: Bob] [timestamp] Alice: I'm doing great, thanks!
[Admin: Admin] [timestamp] Alice: I'm doing great, thanks!
[Bot: ChatBot] [timestamp] Alice: I'm doing great, thanks!

=== Bot Interactions ===
[General Chat] [timestamp] Alice: @ChatBot hello
[User: Bob] [timestamp] Alice: @ChatBot hello
[Admin: Admin] [timestamp] Alice: @ChatBot hello
[Bot: ChatBot] [timestamp] Alice: @ChatBot hello
[General Chat] [timestamp] ChatBot: Hello! How can I help you today?
[User: Alice] [timestamp] ChatBot: Hello! How can I help you today?
[User: Bob] [timestamp] ChatBot: Hello! How can I help you today?
[Admin: Admin] [timestamp] ChatBot: Hello! How can I help you today?
[General Chat] [timestamp] Bob: @ChatBot help
[User: Alice] [timestamp] Bob: @ChatBot help
[Admin: Admin] [timestamp] Bob: @ChatBot help
[Bot: ChatBot] [timestamp] Bob: @ChatBot help
[General Chat] [timestamp] ChatBot: Available commands: /users, /time, /status, /joke
[User: Alice] [timestamp] ChatBot: Available commands: /users, /time, /status, /joke
[User: Bob] [timestamp] ChatBot: Available commands: /users, /time, /status, /joke
[Admin: Admin] [timestamp] ChatBot: Available commands: /users, /time, /status, /joke

=== Private Messages ===
[General Chat] [timestamp] Alice -> Bob (private): Hey Bob, want to grab lunch later?
[User: Alice] [timestamp] SYSTEM: Private message sent to Bob
[User: Bob] [timestamp] Alice -> Bob (private): Hey Bob, want to grab lunch later?
[General Chat] [timestamp] Bob -> Alice (private): Sure! How about 12 PM?
[User: Bob] [timestamp] SYSTEM: Private message sent to Alice
[User: Alice] [timestamp] Bob -> Alice (private): Sure! How about 12 PM?

=== Status Changes ===
[General Chat] [timestamp] SYSTEM: Bob is now busy
[User: Alice] [timestamp] SYSTEM: Bob is now busy
[Admin: Admin] [timestamp] SYSTEM: Bob is now busy
[Bot: ChatBot] [timestamp] SYSTEM: Bob is now busy
[User: Alice] [timestamp] SYSTEM: Bob is currently busy and may not respond
[General Chat] [timestamp] SYSTEM: Bob is now online
[User: Alice] [timestamp] SYSTEM: Bob is now online
[Admin: Admin] [timestamp] SYSTEM: Bob is now online
[Bot: ChatBot] [timestamp] SYSTEM: Bob is now online

=== Admin Functions ===
[General Chat] [timestamp] BROADCAST from Admin: Welcome to our chat room! Please be respectful.
[User: Alice] [timestamp] BROADCAST from Admin: Welcome to our chat room! Please be respectful.
[User: Bob] [timestamp] BROADCAST from Admin: Welcome to our chat room! Please be respectful.
[Bot: ChatBot] [timestamp] BROADCAST from Admin: Welcome to our chat room! Please be respectful.
[General Chat] [timestamp] SYSTEM: Sub-room 'Tech Discussion' has been created
[User: Alice] [timestamp] SYSTEM: Sub-room 'Tech Discussion' has been created
[User: Bob] [timestamp] SYSTEM: Sub-room 'Tech Discussion' has been created
[Admin: Admin] [timestamp] SYSTEM: Sub-room 'Tech Discussion' has been created
[Bot: ChatBot] [timestamp] SYSTEM: Sub-room 'Tech Discussion' has been created

=== More Users Join ===
[General Chat] [timestamp] SYSTEM: Charlie has joined the room
[User: Alice] [timestamp] SYSTEM: Charlie has joined the room
[User: Bob] [timestamp] SYSTEM: Charlie has joined the room
[Admin: Admin] [timestamp] SYSTEM: Charlie has joined the room
[Bot: ChatBot] [timestamp] SYSTEM: Charlie has joined the room
[User: Charlie] [timestamp] SYSTEM: Welcome to General Chat! There are 5 users online.
[User: Charlie] [timestamp] SYSTEM: --- Recent Messages ---
[General Chat] [timestamp] SYSTEM: Diana has joined the room
[User: Alice] [timestamp] SYSTEM: Diana has joined the room
[User: Bob] [timestamp] SYSTEM: Diana has joined the room
[Admin: Admin] [timestamp] SYSTEM: Diana has joined the room
[Bot: ChatBot] [timestamp] SYSTEM: Diana has joined the room
[User: Charlie] [timestamp] SYSTEM: Diana has joined the room
[User: Diana] [timestamp] SYSTEM: Welcome to General Chat! There are 6 users online.

=== General Chat Status ===
Users: 6
Messages: 25
Moderators: 1
Sub-rooms: 1
Online users: [Alice, Bob, Admin, ChatBot, Charlie, Diana]
===========================
*/