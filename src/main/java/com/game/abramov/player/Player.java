package com.game.abramov.player;

import com.game.abramov.communicator.CommunicatorInterface;
import com.game.abramov.communicator.Mode;

import java.io.IOException;

/**
 * Class for players in the game.
 *
 * @author Dmitrii Abramov
 */
public class Player implements Runnable {
    /**
     * Player name.
     */
    private String name;
    /**
     * Count of reveived messages.
     */
    private int receiveMessageCount = 0;
    /**
     * Count of sent messages.
     */
    private int sendMessageCount = 0;
    /**
     * Player initiator or not.
     */
    private Type type;
    /**
     * Player communicator for sending and receiving messages.
     */
    private CommunicatorInterface communicator;
    /**
     * Condition to stop the game.
     */
    private static final int MAX_REC_MES = 10;
    /**
     * Condition to stop the game.
     */
    private static final int MAX_SEND_MES = 10;
    /**
     * Socket waiting interval.
     */
    private static final int SOCKET_WAITING_TIME = 1000;
    /**
     * Phrase sending by an initiator to non-initiator to stop the game.
     */
    private static final String EXIT_STATEMENT = "%exit%";
    /**
     * Start message which sent by initiator.
     */
    private String initialMessage;

    /**
     * Return a player type
     */
    private Type getType() {
        return type;
    }

    /**
     * Set a player type
     */
    private void setType(Type type) {
        this.type = type;
    }

    /**
     * Set a initial message
     */
    private void setInitialMessage(String initialMessage) {
        this.initialMessage = initialMessage;
    }

    /**
     * Set a communicator for the playe
     */
    private void setCommunicator(CommunicatorInterface communicator) {
        this.communicator = communicator;
    }

    /**
     * @param name           a player name.
     * @param communicator   a communicator for player.
     * @param initialMessage a start message.
     */
    public Player(String name, CommunicatorInterface communicator, String initialMessage) {
        if (communicator != null) {
            setName(name);
            setCommunicator(communicator);
            setInitialMessage(initialMessage);
        } else {
            throw new IllegalArgumentException("Communicator is null");
        }
    }

    /**
     * Write log to console
     */
    private void writeToConsole(String log) {
        System.out.println(name + ": " + log);
    }

    /**
     * Set a player name
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * Return communicator
     */
    private CommunicatorInterface getCommunicator() {
        return communicator;
    }

    /**
     * Return a player name
     */
    public String getName() {
        return name;
    }

    /**
     * Method for playing the game.
     * Player sends and receives messages in loop before the stop game condition is false
     * or EXIT_STATEMENT does not received.
     */
    public void run() {
        try {
            initCommunication();
            String message = receive();
            while (getType().equals(Type.NON_NITIATOR) || sendMessageCount < MAX_SEND_MES || receiveMessageCount < MAX_REC_MES) {
                sendWithCount(message);
                message = receive();
                if (message.equals(EXIT_STATEMENT)) {
                    break;
                }
            }
            prepareDisconnect();
            getCommunicator().disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If player is initiator he sends EXIT_STATEMENT
     */
    private void prepareDisconnect() throws IOException {
        if (getType().equals(Type.INITIATOR)) {
            send(EXIT_STATEMENT);
            try {
                Thread.sleep(SOCKET_WAITING_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Start communication via a player's communicator.
     */
    private void initCommunication() throws IOException {
        getCommunicator().connect();
        setType(communicator.getMode().equals(Mode.SENDER) ? Type.INITIATOR : Type.NON_NITIATOR);
        writeToConsole("I am a(an) - " + getType());
        if (getType().equals(Type.INITIATOR)) {
            send(initialMessage);
        }
    }

    /**
     * Send a message and add counter sendMessageCount.
     */
    public void send(String message) throws IOException {
        getCommunicator().send(message);
        sendMessageCount++;
        writeToConsole("sent " + message);
    }

    /**
     * Receive a message and add counter receiveMessageCount.
     */
    public String receive() throws IOException {
        receiveMessageCount++;
        String message = getCommunicator().receive();
        writeToConsole("received " + message);
        return message;
    }

    /**
     * Return received message count
     */
    public int getReceiveMessageCount() {
        return receiveMessageCount;
    }

    /**
     * Send message concatenated with sendMessageCount.
     */
    public void sendWithCount(String message) throws IOException {
        send(message + sendMessageCount);
    }

    /**
     * Return sent message count
     */
    public int getSendMessageCount() {
        return sendMessageCount;
    }
}
