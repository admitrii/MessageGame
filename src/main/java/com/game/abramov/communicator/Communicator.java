package com.game.abramov.communicator;

import java.io.*;
import java.net.*;

/**
 * Class for communication between 2 players.
 *
 * @author Dmitrii Abramov
 */

public class Communicator implements CommunicatorInterface {
    /**
     * input message flow
     */
    private BufferedReader in;
    /**
     * output message flow
     */
    private BufferedWriter out;
    /**
     * random soket timeout as a protection from full synchronization
     */
    private static final int SOCKET_TIMEOUT = (int) Math.round(1000 * Math.random() + 1);
    /**
     * communicator supports only 1 connection
     */
    private static final int MAX_SOCKET_CONNECTION = 1;
    private static final String LOCALHOST = "127.0.0.1";
    /**
     * Communicator mode
     */
    private Mode mode = Mode.RECEIVER;
    /**
     * communicator uses socket for transferring messages
     */
    private Socket socket;
    /**
     * port number for socket
     */
    private int port;

    /**
     * Constructor get socket porn number as paremetr.
     */
    public Communicator(int port) {
        if (port >= 0 && port <= Math.pow(2, 16)) {
            this.port = port;
        } else {
            throw new IllegalArgumentException("Port " + port + " is out of range 0-65535");
        }
    }

    /**
     * Return communicator mode.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Set communicator Mode.
     */
    public void setMode(Mode Mode) {
        this.mode = Mode;
    }

    /**
     * Communicator starts finding other communicator. It switches between sending and receiving.
     * When other communicator connected which one send a message starts to be sender
     * and another one starts to be non-sender(receiver).
     */
    @Override
    public void connect() throws IOException {
        Socket socket;
        while (true) {
            try (ServerSocket server = new ServerSocket(port, MAX_SOCKET_CONNECTION)) {
                server.setSoTimeout(SOCKET_TIMEOUT);
                socket = server.accept();
                setMode(Mode.SENDER);
                break;
            } catch (SocketTimeoutException e) {
                writeToConsole("Communicator switched to receiver mode");
            } catch (SocketException ex) {
                ex.printStackTrace();
            }
            try {
                socket = new Socket(LOCALHOST, port);
                if (!socket.isConnected()) {
                    continue;
                }
                setMode(Mode.RECEIVER);
                break;
            } catch (IOException e) {
                writeToConsole("Communicator switched to sender mode");
            }
        }
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Communicator close resources before disconnection.
     */
    public void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    /**
     * Write log to console.
     */
    private static void writeToConsole(String log) {
        System.out.println(log);
    }

    /**
     * Send a message to the connected communicator.
     */
    @Override
    public void send(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }

    /**
     * Receive a message from the connected communicator.
     */
    @Override
    public String receive() throws IOException {
        return in.readLine();
    }


}
