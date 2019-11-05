package player;

import com.game.abramov.communicator.Communicator;
import com.game.abramov.communicator.Mode;
import com.game.abramov.player.Player;
import communicator.CommunicatorMock;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The class includes tests for player functions
 */
class PlayerTest {
    private static final String TEST_MESSAGE = "Message";
    private static final String EXIT_STATEMENT = "%exit%";

    @Test
    void send() throws IOException {
        CommunicatorMock communicator1 = new CommunicatorMock();
        CommunicatorMock communicator2 = new CommunicatorMock();
        communicator1.setCommunicatorOtherSide(communicator2);
        Player player = new Player("", communicator1, TEST_MESSAGE);
        player.send(TEST_MESSAGE);
        Assert.assertEquals(TEST_MESSAGE, communicator2.receive());
    }

    @Test
    void receive() throws IOException {
        CommunicatorMock communicator1 = new CommunicatorMock();
        CommunicatorMock communicator2 = new CommunicatorMock();
        communicator1.setCommunicatorOtherSide(communicator2);
        communicator2.setCommunicatorOtherSide(communicator1);
        Player player = new Player("Player1", communicator1, TEST_MESSAGE);
        communicator2.send(TEST_MESSAGE);
        Assert.assertEquals(TEST_MESSAGE, player.receive());
    }

    @Test
    void sendWithCount() throws IOException {
        CommunicatorMock communicator1 = new CommunicatorMock();
        CommunicatorMock communicator2 = new CommunicatorMock();
        communicator1.setCommunicatorOtherSide(communicator2);
        Player player = new Player("Player1", communicator1, TEST_MESSAGE);
        player.send(TEST_MESSAGE);
        communicator2.receive();
        player.send(TEST_MESSAGE);
        communicator2.receive();
        player.sendWithCount(TEST_MESSAGE);
        Assert.assertEquals(TEST_MESSAGE + (player.getSendMessageCount() - 1), communicator2.receive());
    }

    @Test
    void initiatorRun() throws ExecutionException, InterruptedException {
        CommunicatorMock communicator1 = new CommunicatorMock();
        CommunicatorMock communicator2 = new CommunicatorMock();
        communicator1.setCommunicatorOtherSide(communicator2);
        communicator2.setCommunicatorOtherSide(communicator1);
        communicator1.setMode(Mode.SENDER);
        String message = TEST_MESSAGE;
        Player player = new Player("Player1", communicator1, TEST_MESSAGE);
        message = message + 0;
        for (int i = 1; i < 11; i++) {
            communicator2.send(message);
            message = message + i + i;
        }
        CompletableFuture future1 = CompletableFuture.runAsync(() -> {
            player.run();
        });
        CompletableFuture.allOf(future1).thenAccept(v -> {
            Assertions.assertEquals(11, player.getSendMessageCount());
            Assertions.assertEquals(10, player.getReceiveMessageCount());
        }).get();
        Assert.assertEquals(EXIT_STATEMENT, communicator2.getMessageQueue().getFirst());
        communicator2.getMessageQueue().removeFirst();
        Assert.assertEquals(TEST_MESSAGE + "011223344556677889", communicator2.getMessageQueue().getFirst());
    }

    @Test
    void notInitiatorRun() throws ExecutionException, InterruptedException {
        CommunicatorMock communicator1 = new CommunicatorMock();
        CommunicatorMock communicator2 = new CommunicatorMock();
        communicator1.setCommunicatorOtherSide(communicator2);
        communicator2.setCommunicatorOtherSide(communicator1);
        communicator2.setMode(Mode.SENDER);
        String message = TEST_MESSAGE;
        Player player = new Player("Player1", communicator1, TEST_MESSAGE);
        communicator2.send(message);
        message = message + 0;
        for (int i = 1; i < 10; i++) {
            communicator2.send(message);
            message = message + i + i;
        }
        communicator2.send(EXIT_STATEMENT);
        CompletableFuture future1 = CompletableFuture.runAsync(() -> {
            player.run();
        });
        CompletableFuture.allOf(future1).thenAccept(v -> {
            Assertions.assertEquals(10, player.getSendMessageCount());
            Assertions.assertEquals(11, player.getReceiveMessageCount());
        }).get();
        Assert.assertEquals(TEST_MESSAGE + "011223344556677889", communicator2.getMessageQueue().getFirst());
    }


    @Test
    void integrationTest() throws ExecutionException, InterruptedException {
        int port = 4006;
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Player player1 = new Player("player1", new Communicator(port), TEST_MESSAGE);
        CompletableFuture future1 = CompletableFuture.runAsync(() -> {
            player1.run();
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Player player2 = new Player("player2", new Communicator(port), TEST_MESSAGE);
        CompletableFuture future2 = CompletableFuture.runAsync(() -> {
            player2.run();
        });
        CompletableFuture.allOf(future1, future2).thenAccept(v -> {
            Assertions.assertEquals(11, player1.getSendMessageCount());
            Assertions.assertEquals(10, player1.getReceiveMessageCount());
            Assertions.assertEquals(11, player2.getReceiveMessageCount());
            Assertions.assertEquals(10, player2.getSendMessageCount());
        }).get();
    }

}
