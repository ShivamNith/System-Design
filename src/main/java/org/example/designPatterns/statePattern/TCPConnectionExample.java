package org.example.designPatterns.statePattern;

import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

/**
 * TCP Connection State Pattern Example
 * 
 * This example demonstrates TCP connection states based on the TCP state machine:
 * - ClosedState: No connection exists
 * - ListenState: Waiting for incoming connections (server)
 * - SynSentState: Sent SYN, waiting for SYN-ACK
 * - SynReceivedState: Received SYN, sent SYN-ACK
 * - EstablishedState: Connection established, data transfer
 * - FinWait1State: Sent FIN, waiting for ACK
 * - FinWait2State: Received ACK for FIN
 * - CloseWaitState: Received FIN, waiting for close
 * - LastAckState: Sent FIN after close wait
 * - TimeWaitState: Waiting for final ACK timeout
 */

// TCP Packet types
enum PacketType {
    SYN, ACK, FIN, RST, DATA, SYN_ACK, FIN_ACK
}

// TCP Packet class
class TCPPacket {
    private final PacketType type;
    private final int sequenceNumber;
    private final int acknowledgmentNumber;
    private final String data;
    private final LocalDateTime timestamp;
    private final InetSocketAddress source;
    private final InetSocketAddress destination;

    public TCPPacket(PacketType type, int seqNum, int ackNum, String data, 
                     InetSocketAddress source, InetSocketAddress destination) {
        this.type = type;
        this.sequenceNumber = seqNum;
        this.acknowledgmentNumber = ackNum;
        this.data = data;
        this.source = source;
        this.destination = destination;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public PacketType getType() { return type; }
    public int getSequenceNumber() { return sequenceNumber; }
    public int getAcknowledmentNumber() { return acknowledgmentNumber; }
    public String getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public InetSocketAddress getSource() { return source; }
    public InetSocketAddress getDestination() { return destination; }

    @Override
    public String toString() {
        return String.format("[%s] %s->%s: %s (seq=%d, ack=%d)%s",
            timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
            source.getPort(), destination.getPort(), type,
            sequenceNumber, acknowledgmentNumber,
            data != null && !data.isEmpty() ? " data=\"" + data + "\"" : "");
    }
}

// Connection role enum
enum ConnectionRole {
    CLIENT, SERVER
}

// TCP State interface
interface TCPState {
    void open(TCPConnection connection);
    void close(TCPConnection connection);
    void send(TCPConnection connection, String data);
    void receive(TCPConnection connection, TCPPacket packet);
    void timeout(TCPConnection connection);
    void abort(TCPConnection connection);
    String getStateName();
    boolean canSendData();
    boolean canReceiveData();
}

// TCP Connection context class
class TCPConnection {
    private final String connectionId;
    private TCPState currentState;
    private final InetSocketAddress localAddress;
    private final InetSocketAddress remoteAddress;
    private final ConnectionRole role;
    
    // Connection parameters
    private int localSequenceNumber;
    private int remoteSequenceNumber;
    private final List<TCPPacket> packetHistory;
    private final Map<String, LocalDateTime> stateTransitions;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> timeoutTask;
    
    // Connection statistics
    private long bytesReceived;
    private long bytesSent;
    private int packetsReceived;
    private int packetsSent;
    private LocalDateTime connectionStartTime;
    private LocalDateTime lastActivityTime;

    public TCPConnection(String connectionId, InetSocketAddress localAddress, 
                        InetSocketAddress remoteAddress, ConnectionRole role) {
        this.connectionId = connectionId;
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.role = role;
        this.currentState = new ClosedState();
        this.packetHistory = new ArrayList<>();
        this.stateTransitions = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.localSequenceNumber = new Random().nextInt(1000);
        this.remoteSequenceNumber = 0;
        this.connectionStartTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();
        
        recordStateTransition("Closed");
        log("TCP Connection created: " + localAddress + " <-> " + remoteAddress + " (" + role + ")");
    }

    // State management
    public void setState(TCPState state) {
        String previousState = currentState.getStateName();
        this.currentState = state;
        recordStateTransition(state.getStateName());
        lastActivityTime = LocalDateTime.now();
        log("State transition: " + previousState + " -> " + state.getStateName());
        
        // Cancel any existing timeout
        if (timeoutTask != null && !timeoutTask.isDone()) {
            timeoutTask.cancel(false);
        }
    }

    public TCPState getState() {
        return currentState;
    }

    // Public interface methods
    public void open() {
        currentState.open(this);
    }

    public void close() {
        currentState.close(this);
    }

    public void send(String data) {
        currentState.send(this, data);
    }

    public void receive(TCPPacket packet) {
        currentState.receive(this, packet);
    }

    public void timeout() {
        currentState.timeout(this);
    }

    public void abort() {
        currentState.abort(this);
    }

    // Internal methods used by states
    public void sendPacket(PacketType type, String data) {
        TCPPacket packet = new TCPPacket(type, localSequenceNumber, remoteSequenceNumber,
                                       data, localAddress, remoteAddress);
        packetHistory.add(packet);
        packetsSent++;
        
        if (data != null && !data.isEmpty()) {
            bytesSent += data.length();
            localSequenceNumber += data.length();
        } else {
            localSequenceNumber++;
        }
        
        lastActivityTime = LocalDateTime.now();
        log("SENT: " + packet);
    }

    public void receivePacket(TCPPacket packet) {
        packetHistory.add(packet);
        packetsReceived++;
        
        if (packet.getData() != null && !packet.getData().isEmpty()) {
            bytesReceived += packet.getData().length();
        }
        
        remoteSequenceNumber = packet.getSequenceNumber() + 
                              (packet.getData() != null ? packet.getData().length() : 1);
        
        lastActivityTime = LocalDateTime.now();
        log("RECEIVED: " + packet);
    }

    public void scheduleTimeout(long delaySeconds, Runnable timeoutAction) {
        if (timeoutTask != null && !timeoutTask.isDone()) {
            timeoutTask.cancel(false);
        }
        
        timeoutTask = scheduler.schedule(() -> {
            log("TIMEOUT occurred in " + currentState.getStateName() + " state");
            timeoutAction.run();
        }, delaySeconds, TimeUnit.SECONDS);
    }

    public void cancelTimeout() {
        if (timeoutTask != null && !timeoutTask.isDone()) {
            timeoutTask.cancel(false);
            log("Timeout cancelled");
        }
    }

    private void recordStateTransition(String stateName) {
        stateTransitions.put(stateName, LocalDateTime.now());
    }

    private void log(String message) {
        System.out.println("[CONN-" + connectionId + "] " + message);
    }

    // Cleanup method
    public void cleanup() {
        if (timeoutTask != null && !timeoutTask.isDone()) {
            timeoutTask.cancel(false);
        }
        scheduler.shutdown();
        log("Connection cleaned up");
    }

    // Getters for display and statistics
    public String getConnectionId() { return connectionId; }
    public InetSocketAddress getLocalAddress() { return localAddress; }
    public InetSocketAddress getRemoteAddress() { return remoteAddress; }
    public ConnectionRole getRole() { return role; }
    public int getLocalSequenceNumber() { return localSequenceNumber; }
    public int getRemoteSequenceNumber() { return remoteSequenceNumber; }
    public List<TCPPacket> getPacketHistory() { return new ArrayList<>(packetHistory); }
    
    public void displayStatus() {
        System.out.println("\n=== TCP Connection Status ===");
        System.out.println("Connection ID: " + connectionId);
        System.out.println("Local Address: " + localAddress);
        System.out.println("Remote Address: " + remoteAddress);
        System.out.println("Role: " + role);
        System.out.println("Current State: " + currentState.getStateName());
        System.out.println("Local Seq #: " + localSequenceNumber);
        System.out.println("Remote Seq #: " + remoteSequenceNumber);
        System.out.println("Bytes Sent: " + bytesSent);
        System.out.println("Bytes Received: " + bytesReceived);
        System.out.println("Packets Sent: " + packetsSent);
        System.out.println("Packets Received: " + packetsReceived);
        System.out.println("Connection Started: " + connectionStartTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("Last Activity: " + lastActivityTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("Can Send Data: " + currentState.canSendData());
        System.out.println("Can Receive Data: " + currentState.canReceiveData());
        System.out.println("==============================");
    }

    public void displayPacketHistory() {
        System.out.println("\n=== Packet History ===");
        packetHistory.forEach(System.out::println);
        System.out.println("======================");
    }
}

// Concrete State Classes

class ClosedState implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        if (connection.getRole() == ConnectionRole.CLIENT) {
            // Client initiates connection with SYN
            connection.sendPacket(PacketType.SYN, null);
            connection.setState(new SynSentState());
        } else {
            // Server goes to listening state
            connection.setState(new ListenState());
            System.out.println("Server now listening for connections...");
        }
    }

    @Override
    public void close(TCPConnection connection) {
        System.out.println("Connection already closed");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        throw new IllegalStateException("Cannot send data on closed connection");
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        if (packet.getType() == PacketType.SYN && connection.getRole() == ConnectionRole.SERVER) {
            // Passive open - server receiving SYN
            connection.receivePacket(packet);
            connection.sendPacket(PacketType.SYN_ACK, null);
            connection.setState(new SynReceivedState());
        } else {
            System.out.println("Ignoring packet on closed connection: " + packet.getType());
        }
    }

    @Override
    public void timeout(TCPConnection connection) {
        // No timeouts in closed state
    }

    @Override
    public void abort(TCPConnection connection) {
        System.out.println("Connection already closed");
    }

    @Override
    public String getStateName() { return "CLOSED"; }

    @Override
    public boolean canSendData() { return false; }

    @Override
    public boolean canReceiveData() { return false; }
}

class ListenState implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        System.out.println("Already listening for connections");
    }

    @Override
    public void close(TCPConnection connection) {
        connection.setState(new ClosedState());
        System.out.println("Stopped listening, connection closed");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        throw new IllegalStateException("Cannot send data while listening");
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        if (packet.getType() == PacketType.SYN) {
            connection.receivePacket(packet);
            connection.sendPacket(PacketType.SYN_ACK, null);
            connection.setState(new SynReceivedState());
        } else {
            System.out.println("Ignoring non-SYN packet while listening: " + packet.getType());
        }
    }

    @Override
    public void timeout(TCPConnection connection) {
        // No timeouts while listening
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.setState(new ClosedState());
        System.out.println("Listen aborted, connection closed");
    }

    @Override
    public String getStateName() { return "LISTEN"; }

    @Override
    public boolean canSendData() { return false; }

    @Override
    public boolean canReceiveData() { return false; }
}

class SynSentState implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        System.out.println("Connection opening already in progress");
    }

    @Override
    public void close(TCPConnection connection) {
        connection.setState(new ClosedState());
        System.out.println("Connection opening cancelled, closed");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        throw new IllegalStateException("Cannot send data while connection is opening");
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        if (packet.getType() == PacketType.SYN_ACK) {
            connection.receivePacket(packet);
            connection.sendPacket(PacketType.ACK, null);
            connection.setState(new EstablishedState());
            System.out.println("Connection established (3-way handshake complete)");
        } else if (packet.getType() == PacketType.SYN) {
            // Simultaneous open
            connection.receivePacket(packet);
            connection.sendPacket(PacketType.SYN_ACK, null);
            connection.setState(new SynReceivedState());
        } else if (packet.getType() == PacketType.RST) {
            connection.receivePacket(packet);
            connection.setState(new ClosedState());
            System.out.println("Connection reset by remote peer");
        }
    }

    @Override
    public void timeout(TCPConnection connection) {
        System.out.println("Connection attempt timed out, retrying...");
        connection.sendPacket(PacketType.SYN, null);
        connection.scheduleTimeout(5, () -> connection.timeout());
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.sendPacket(PacketType.RST, null);
        connection.setState(new ClosedState());
        System.out.println("Connection attempt aborted");
    }

    @Override
    public String getStateName() { return "SYN-SENT"; }

    @Override
    public boolean canSendData() { return false; }

    @Override
    public boolean canReceiveData() { return false; }
}

class SynReceivedState implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        System.out.println("Connection handshake in progress");
    }

    @Override
    public void close(TCPConnection connection) {
        connection.sendPacket(PacketType.FIN, null);
        connection.setState(new FinWait1State());
    }

    @Override
    public void send(TCPConnection connection, String data) {
        throw new IllegalStateException("Cannot send data during handshake");
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        if (packet.getType() == PacketType.ACK) {
            connection.receivePacket(packet);
            connection.setState(new EstablishedState());
            System.out.println("Connection established (handshake complete)");
        } else if (packet.getType() == PacketType.RST) {
            connection.receivePacket(packet);
            connection.setState(new ClosedState());
            System.out.println("Connection reset during handshake");
        }
    }

    @Override
    public void timeout(TCPConnection connection) {
        System.out.println("Handshake timeout, resending SYN-ACK");
        connection.sendPacket(PacketType.SYN_ACK, null);
        connection.scheduleTimeout(5, () -> connection.timeout());
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.sendPacket(PacketType.RST, null);
        connection.setState(new ClosedState());
        System.out.println("Handshake aborted");
    }

    @Override
    public String getStateName() { return "SYN-RECEIVED"; }

    @Override
    public boolean canSendData() { return false; }

    @Override
    public boolean canReceiveData() { return false; }
}

class EstablishedState implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        System.out.println("Connection already established");
    }

    @Override
    public void close(TCPConnection connection) {
        connection.sendPacket(PacketType.FIN, null);
        connection.setState(new FinWait1State());
        System.out.println("Initiating connection close");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        if (data != null && !data.isEmpty()) {
            connection.sendPacket(PacketType.DATA, data);
            System.out.println("Data sent: \"" + data + "\"");
        }
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        connection.receivePacket(packet);
        
        if (packet.getType() == PacketType.DATA) {
            // Send ACK for data
            connection.sendPacket(PacketType.ACK, null);
            System.out.println("Data received and acknowledged: \"" + packet.getData() + "\"");
        } else if (packet.getType() == PacketType.FIN) {
            // Remote peer wants to close
            connection.sendPacket(PacketType.ACK, null);
            connection.setState(new CloseWaitState());
            System.out.println("Remote peer initiated close, entering close-wait");
        } else if (packet.getType() == PacketType.RST) {
            connection.setState(new ClosedState());
            System.out.println("Connection reset by remote peer");
        }
    }

    @Override
    public void timeout(TCPConnection connection) {
        System.out.println("Keepalive timeout - connection may be dead");
        // Could implement keepalive logic here
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.sendPacket(PacketType.RST, null);
        connection.setState(new ClosedState());
        System.out.println("Connection aborted");
    }

    @Override
    public String getStateName() { return "ESTABLISHED"; }

    @Override
    public boolean canSendData() { return true; }

    @Override
    public boolean canReceiveData() { return true; }
}

class FinWait1State implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        throw new IllegalStateException("Cannot open connection while closing");
    }

    @Override
    public void close(TCPConnection connection) {
        System.out.println("Close already initiated");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        // Can still send data in FIN-WAIT-1
        if (data != null && !data.isEmpty()) {
            connection.sendPacket(PacketType.DATA, data);
            System.out.println("Data sent during close: \"" + data + "\"");
        }
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        connection.receivePacket(packet);
        
        if (packet.getType() == PacketType.ACK) {
            // ACK for our FIN
            connection.setState(new FinWait2State());
            System.out.println("FIN acknowledged, waiting for remote FIN");
        } else if (packet.getType() == PacketType.FIN) {
            // Simultaneous close
            connection.sendPacket(PacketType.ACK, null);
            connection.setState(new TimeWaitState());
            System.out.println("Simultaneous close detected, entering time-wait");
        } else if (packet.getType() == PacketType.FIN_ACK) {
            // Combined FIN+ACK
            connection.sendPacket(PacketType.ACK, null);
            connection.setState(new TimeWaitState());
        }
    }

    @Override
    public void timeout(TCPConnection connection) {
        System.out.println("FIN timeout, retransmitting FIN");
        connection.sendPacket(PacketType.FIN, null);
        connection.scheduleTimeout(5, () -> connection.timeout());
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.sendPacket(PacketType.RST, null);
        connection.setState(new ClosedState());
        System.out.println("Connection aborted during close");
    }

    @Override
    public String getStateName() { return "FIN-WAIT-1"; }

    @Override
    public boolean canSendData() { return true; }

    @Override
    public boolean canReceiveData() { return true; }
}

class FinWait2State implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        throw new IllegalStateException("Cannot open connection while closing");
    }

    @Override
    public void close(TCPConnection connection) {
        System.out.println("Close already initiated, waiting for remote FIN");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        // Generally can't send new data in FIN-WAIT-2
        System.out.println("Cannot send new data in FIN-WAIT-2 state");
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        connection.receivePacket(packet);
        
        if (packet.getType() == PacketType.FIN) {
            connection.sendPacket(PacketType.ACK, null);
            connection.setState(new TimeWaitState());
            System.out.println("Received remote FIN, entering time-wait");
        } else if (packet.getType() == PacketType.DATA) {
            // Still receiving data
            connection.sendPacket(PacketType.ACK, null);
            System.out.println("Received final data: \"" + packet.getData() + "\"");
        }
    }

    @Override
    public void timeout(TCPConnection connection) {
        System.out.println("FIN-WAIT-2 timeout - assuming connection closed");
        connection.setState(new ClosedState());
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.sendPacket(PacketType.RST, null);
        connection.setState(new ClosedState());
        System.out.println("Connection aborted in FIN-WAIT-2");
    }

    @Override
    public String getStateName() { return "FIN-WAIT-2"; }

    @Override
    public boolean canSendData() { return false; }

    @Override
    public boolean canReceiveData() { return true; }
}

class CloseWaitState implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        throw new IllegalStateException("Cannot open connection while closing");
    }

    @Override
    public void close(TCPConnection connection) {
        connection.sendPacket(PacketType.FIN, null);
        connection.setState(new LastAckState());
        System.out.println("Sending FIN to complete close");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        // Can still send data in CLOSE-WAIT
        if (data != null && !data.isEmpty()) {
            connection.sendPacket(PacketType.DATA, data);
            System.out.println("Final data sent: \"" + data + "\"");
        }
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        connection.receivePacket(packet);
        
        if (packet.getType() == PacketType.DATA) {
            connection.sendPacket(PacketType.ACK, null);
            System.out.println("Received data in close-wait: \"" + packet.getData() + "\"");
        }
        // Ignore other packet types - we're waiting for application to close
    }

    @Override
    public void timeout(TCPConnection connection) {
        System.out.println("Close-wait timeout - application should close connection");
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.sendPacket(PacketType.RST, null);
        connection.setState(new ClosedState());
        System.out.println("Connection aborted in close-wait");
    }

    @Override
    public String getStateName() { return "CLOSE-WAIT"; }

    @Override
    public boolean canSendData() { return true; }

    @Override
    public boolean canReceiveData() { return false; }
}

class LastAckState implements TCPState {
    @Override
    public void open(TCPConnection connection) {
        throw new IllegalStateException("Cannot open connection while closing");
    }

    @Override
    public void close(TCPConnection connection) {
        System.out.println("Final close already sent, waiting for ACK");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        throw new IllegalStateException("Cannot send data in last-ack state");
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        connection.receivePacket(packet);
        
        if (packet.getType() == PacketType.ACK) {
            connection.setState(new ClosedState());
            System.out.println("Final ACK received, connection closed");
        }
    }

    @Override
    public void timeout(TCPConnection connection) {
        System.out.println("Last ACK timeout, retransmitting FIN");
        connection.sendPacket(PacketType.FIN, null);
        connection.scheduleTimeout(5, () -> connection.timeout());
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.setState(new ClosedState());
        System.out.println("Connection aborted in last-ack");
    }

    @Override
    public String getStateName() { return "LAST-ACK"; }

    @Override
    public boolean canSendData() { return false; }

    @Override
    public boolean canReceiveData() { return false; }
}

class TimeWaitState implements TCPState {
    private static final int TIME_WAIT_DURATION = 10; // seconds (normally 2*MSL)

    public TimeWaitState() {
        // In real implementation, this would be 2 * Maximum Segment Lifetime
    }

    @Override
    public void open(TCPConnection connection) {
        throw new IllegalStateException("Cannot open connection in time-wait");
    }

    @Override
    public void close(TCPConnection connection) {
        System.out.println("Connection close in progress, time-wait active");
    }

    @Override
    public void send(TCPConnection connection, String data) {
        throw new IllegalStateException("Cannot send data in time-wait");
    }

    @Override
    public void receive(TCPConnection connection, TCPPacket packet) {
        connection.receivePacket(packet);
        
        if (packet.getType() == PacketType.FIN) {
            // Retransmitted FIN, re-ACK it
            connection.sendPacket(PacketType.ACK, null);
            System.out.println("Retransmitted FIN received, re-sent ACK");
        }
        // Ignore other packets in time-wait
    }

    @Override
    public void timeout(TCPConnection connection) {
        connection.setState(new ClosedState());
        System.out.println("Time-wait timeout, connection fully closed");
    }

    @Override
    public void abort(TCPConnection connection) {
        connection.setState(new ClosedState());
        System.out.println("Time-wait aborted, connection closed");
    }

    @Override
    public String getStateName() { return "TIME-WAIT"; }

    @Override
    public boolean canSendData() { return false; }

    @Override
    public boolean canReceiveData() { return false; }
}

// TCP Connection Manager for demonstration
class TCPConnectionManager {
    private final Map<String, TCPConnection> connections = new HashMap<>();
    private int connectionCounter = 0;

    public TCPConnection createConnection(InetSocketAddress local, InetSocketAddress remote, ConnectionRole role) {
        String connectionId = "CONN-" + (++connectionCounter);
        TCPConnection connection = new TCPConnection(connectionId, local, remote, role);
        connections.put(connectionId, connection);
        return connection;
    }

    public TCPConnection getConnection(String connectionId) {
        return connections.get(connectionId);
    }

    public List<TCPConnection> getAllConnections() {
        return new ArrayList<>(connections.values());
    }

    public void closeAllConnections() {
        connections.values().forEach(TCPConnection::cleanup);
        connections.clear();
    }

    public void displayAllConnections() {
        System.out.println("\n=== All TCP Connections ===");
        connections.values().forEach(conn -> 
            System.out.println(conn.getConnectionId() + ": " + conn.getState().getStateName() + 
                             " (" + conn.getLocalAddress() + " <-> " + conn.getRemoteAddress() + ")"));
        System.out.println("============================");
    }
}

// Main demonstration class
public class TCPConnectionExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== TCP Connection State Pattern Demo ===\n");

        TCPConnectionManager manager = new TCPConnectionManager();

        // Create addresses
        InetSocketAddress clientAddr = new InetSocketAddress("127.0.0.1", 8080);
        InetSocketAddress serverAddr = new InetSocketAddress("127.0.0.1", 80);

        // Demo 1: Normal TCP connection establishment and data transfer
        System.out.println("Demo 1: Normal TCP Connection Lifecycle");
        System.out.println("---------------------------------------");

        TCPConnection clientConn = manager.createConnection(clientAddr, serverAddr, ConnectionRole.CLIENT);
        TCPConnection serverConn = manager.createConnection(serverAddr, clientAddr, ConnectionRole.SERVER);

        // Server starts listening
        serverConn.open();
        
        // Client initiates connection
        clientConn.open();
        clientConn.displayStatus();

        // Simulate 3-way handshake
        // 1. Server receives SYN from client
        TCPPacket synPacket = new TCPPacket(PacketType.SYN, 100, 0, null, clientAddr, serverAddr);
        serverConn.receive(synPacket);

        // 2. Client receives SYN-ACK from server
        TCPPacket synAckPacket = new TCPPacket(PacketType.SYN_ACK, 200, 101, null, serverAddr, clientAddr);
        clientConn.receive(synAckPacket);

        // 3. Server receives ACK from client
        TCPPacket ackPacket = new TCPPacket(PacketType.ACK, 101, 201, null, clientAddr, serverAddr);
        serverConn.receive(ackPacket);

        System.out.println("\n--- Connection Established ---");
        clientConn.displayStatus();

        // Data transfer
        System.out.println("\n--- Data Transfer ---");
        clientConn.send("Hello, Server!");
        
        // Server receives data
        TCPPacket dataPacket = new TCPPacket(PacketType.DATA, 101, 201, "Hello, Server!", clientAddr, serverAddr);
        serverConn.receive(dataPacket);

        // Server sends response
        serverConn.send("Hello, Client!");
        TCPPacket responsePacket = new TCPPacket(PacketType.DATA, 201, 115, "Hello, Client!", serverAddr, clientAddr);
        clientConn.receive(responsePacket);

        // Demo 2: Normal connection termination
        System.out.println("\nDemo 2: Normal Connection Termination");
        System.out.println("-------------------------------------");

        // Client initiates close
        clientConn.close();

        // Server receives FIN
        TCPPacket finPacket = new TCPPacket(PacketType.FIN, 115, 215, null, clientAddr, serverAddr);
        serverConn.receive(finPacket);

        // Server sends final data and closes
        serverConn.send("Final data");
        serverConn.close();

        // Client receives final data and FIN
        TCPPacket finalDataPacket = new TCPPacket(PacketType.DATA, 215, 116, "Final data", serverAddr, clientAddr);
        clientConn.receive(finalDataPacket);

        TCPPacket serverFinPacket = new TCPPacket(PacketType.FIN, 225, 116, null, serverAddr, clientAddr);
        clientConn.receive(serverFinPacket);

        // Client sends final ACK
        TCPPacket finalAckPacket = new TCPPacket(PacketType.ACK, 116, 226, null, clientAddr, serverAddr);
        serverConn.receive(finalAckPacket);

        clientConn.displayStatus();
        serverConn.displayStatus();

        // Demo 3: Connection with timeout and retry
        System.out.println("\nDemo 3: Connection Timeout and Retry");
        System.out.println("------------------------------------");

        TCPConnection timeoutConn = manager.createConnection(
            new InetSocketAddress("127.0.0.1", 9090),
            new InetSocketAddress("127.0.0.1", 90), 
            ConnectionRole.CLIENT);

        timeoutConn.open();
        System.out.println("Simulating timeout...");
        Thread.sleep(1000);
        timeoutConn.timeout();

        // Demo 4: Connection reset
        System.out.println("\nDemo 4: Connection Reset");
        System.out.println("------------------------");

        TCPConnection resetConn = manager.createConnection(
            new InetSocketAddress("127.0.0.1", 9091),
            new InetSocketAddress("127.0.0.1", 91), 
            ConnectionRole.CLIENT);

        resetConn.open();
        
        // Simulate receiving RST
        TCPPacket rstPacket = new TCPPacket(PacketType.RST, 0, 0, null, 
            new InetSocketAddress("127.0.0.1", 91),
            new InetSocketAddress("127.0.0.1", 9091));
        resetConn.receive(rstPacket);

        // Demo 5: Simultaneous open
        System.out.println("\nDemo 5: Simultaneous Open");
        System.out.println("-------------------------");

        TCPConnection conn1 = manager.createConnection(
            new InetSocketAddress("127.0.0.1", 9092),
            new InetSocketAddress("127.0.0.1", 9093), 
            ConnectionRole.CLIENT);

        TCPConnection conn2 = manager.createConnection(
            new InetSocketAddress("127.0.0.1", 9093),
            new InetSocketAddress("127.0.0.1", 9092), 
            ConnectionRole.CLIENT);

        // Both initiate connection
        conn1.open();
        conn2.open();

        // Each receives SYN from the other
        TCPPacket syn1 = new TCPPacket(PacketType.SYN, 300, 0, null,
            new InetSocketAddress("127.0.0.1", 9093),
            new InetSocketAddress("127.0.0.1", 9092));
        conn1.receive(syn1);

        TCPPacket syn2 = new TCPPacket(PacketType.SYN, 400, 0, null,
            new InetSocketAddress("127.0.0.1", 9092),
            new InetSocketAddress("127.0.0.1", 9093));
        conn2.receive(syn2);

        // Complete the handshake
        TCPPacket ack1 = new TCPPacket(PacketType.ACK, 301, 401, null,
            new InetSocketAddress("127.0.0.1", 9092),
            new InetSocketAddress("127.0.0.1", 9093));
        conn2.receive(ack1);

        TCPPacket ack2 = new TCPPacket(PacketType.ACK, 401, 301, null,
            new InetSocketAddress("127.0.0.1", 9093),
            new InetSocketAddress("127.0.0.1", 9092));
        conn1.receive(ack2);

        // Demo 6: Error handling
        System.out.println("\nDemo 6: Error Handling");
        System.out.println("----------------------");

        TCPConnection errorConn = manager.createConnection(
            new InetSocketAddress("127.0.0.1", 9094),
            new InetSocketAddress("127.0.0.1", 94), 
            ConnectionRole.CLIENT);

        try {
            errorConn.send("Data without connection");
        } catch (IllegalStateException e) {
            System.out.println("Error caught: " + e.getMessage());
        }

        try {
            errorConn.open();
            errorConn.open(); // Try to open again
        } catch (Exception e) {
            System.out.println("Second open attempt handled gracefully");
        }

        // Final status
        System.out.println("\n=== Final Connection States ===");
        manager.displayAllConnections();

        // Show packet history for one connection
        System.out.println("\n=== Sample Packet History ===");
        clientConn.displayPacketHistory();

        // Cleanup
        manager.closeAllConnections();
        System.out.println("\nAll connections cleaned up.");
    }
}

/**
 * Key Features Demonstrated:
 * 
 * 1. TCP State Machine:
 *    - CLOSED: No connection
 *    - LISTEN: Server waiting for connections
 *    - SYN-SENT: Client sent SYN
 *    - SYN-RECEIVED: Server received SYN, sent SYN-ACK
 *    - ESTABLISHED: Connection active, data transfer
 *    - FIN-WAIT-1: Sent FIN, waiting for ACK
 *    - FIN-WAIT-2: Received ACK for FIN, waiting for remote FIN
 *    - CLOSE-WAIT: Received FIN, application must close
 *    - LAST-ACK: Sent final FIN, waiting for ACK
 *    - TIME-WAIT: Waiting for duplicate packets to expire
 * 
 * 2. Protocol Features:
 *    - Three-way handshake (SYN, SYN-ACK, ACK)
 *    - Four-way connection termination
 *    - Simultaneous open and close scenarios
 *    - Timeout handling and retransmission
 *    - Connection reset (RST) handling
 * 
 * 3. State Management:
 *    - State-specific packet handling
 *    - Sequence number management
 *    - Timeout scheduling and cancellation
 *    - Connection statistics tracking
 * 
 * 4. Error Handling:
 *    - Invalid operations in inappropriate states
 *    - Timeout and retry mechanisms
 *    - Connection reset scenarios
 *    - Graceful error recovery
 * 
 * Benefits of State Pattern in TCP implementation:
 * - Clear protocol state management
 * - Easy to understand TCP state machine
 * - Robust error handling per state
 * - Extensible for new TCP features
 * - Eliminates complex conditional logic
 * - Facilitates protocol debugging and testing
 */