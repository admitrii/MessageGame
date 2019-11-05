package communicator;

import com.game.abramov.communicator.CommunicatorInterface;
import com.game.abramov.communicator.Mode;
import com.game.abramov.player.Type;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Mock realisation of communicator.
 * It uses for in test cases.
 * The messages transfer via internal deque.
 */
public class CommunicatorMock implements CommunicatorInterface {
    private CommunicatorMock communicatorOtherSide;
    private Mode mode = Mode.RECEIVER;
    private Deque<String> messageQueue;

    public CommunicatorMock() {
        messageQueue = new LinkedList<>();
    }

    public Deque<String> getMessageQueue() {
        return messageQueue;
    }

    public void setReceiver(Mode mode) {
        this.mode = mode;
    }

    private CommunicatorMock getCommunicatorOtherSide() {
        return communicatorOtherSide;
    }

    public void setCommunicatorOtherSide(CommunicatorMock communicatorOtherSide) {
        this.communicatorOtherSide = communicatorOtherSide;
    }

    @Override
    public void connect() {
    }

    @Override
    public void send(String message) {
        getCommunicatorOtherSide().getMessageQueue().addFirst(message);
    }

    @Override
    public String receive() {
        String message = getMessageQueue().getLast();
        getMessageQueue().removeLast();
        return message;

    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void disconnect() {

    }
}
