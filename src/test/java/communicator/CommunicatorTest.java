package communicator;

import com.game.abramov.communicator.Communicator;
import com.game.abramov.communicator.Mode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The class includes tests for communicator functions
 */
class CommunicatorTest {

    @Test
    void connect1() throws ExecutionException, InterruptedException {
        Communicator communicator1 = new Communicator(4004);
        CompletableFuture future1 = CompletableFuture.runAsync(() -> {
            try {
                communicator1.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Communicator communicator2 = new Communicator(4004);
        CompletableFuture future2 = CompletableFuture.runAsync(() -> {
            try {
                communicator2.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        CompletableFuture.allOf(future1, future2).thenAccept(v -> {
            Assertions.assertTrue(communicator1.getMode().equals(Mode.SENDER) && communicator2.getMode().equals(Mode.RECEIVER)
                    || communicator2.getMode().equals(Mode.RECEIVER) && communicator1.getMode().equals(Mode.SENDER));
        }).get();
    }

    @Test
    void sendAndReceive() throws ExecutionException, InterruptedException {
        Communicator communicator1 = new Communicator(4005);
        String testMessage = "testMessage";
        CompletableFuture future1 = CompletableFuture.runAsync(() -> {
            try {
                communicator1.connect();
                communicator1.send(testMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Communicator communicator2 = new Communicator(4005);
        CompletableFuture future2 = CompletableFuture.runAsync(() -> {
            try {
                communicator2.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        CompletableFuture.allOf(future1, future2).thenAccept(v -> {
            try {
                Assertions.assertEquals(testMessage, communicator2.receive());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).get();
    }

    @Test
    void wrongPort1() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            Communicator communicator = new Communicator(-321);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void wrongPort2() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            Communicator communicator = new Communicator(100000);
        });
        assertNotNull(thrown.getMessage());
    }


}
