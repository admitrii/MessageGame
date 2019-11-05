package com.game.abramov.communicator;

import java.io.IOException;

/**
 * Interface for communication between 2 players.
 *
 * @author Dmitrii Abramov
 */
public interface CommunicatorInterface {
    void connect() throws IOException;

    void send(String message) throws IOException;

    String receive() throws IOException;

    Mode getMode();

    void setMode(Mode mode);

    void disconnect() throws IOException;
}
