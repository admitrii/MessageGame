package com.game.abramov;

import com.game.abramov.communicator.Communicator;
import com.game.abramov.player.Player;

public class Main {
    private static final int DEFAULT_PORT = 4004;
    private static final String DEFAULT_PLAYER1_NAME = "Player1";
    private static final String DEFAULT_PLAYER2_NAME = "Player2";
    private static final String DEFAULT_INIT_MESSAGE = "Message";
    private static final int DEFAULT_START_TYPE = 2;

    /**
     * The application could start with default setting or with custom setting which can be insert as argumentrs.
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String player1Name = DEFAULT_PLAYER1_NAME;
        String player2Name = DEFAULT_PLAYER2_NAME;
        String initMessage = DEFAULT_INIT_MESSAGE;
        int startType = DEFAULT_START_TYPE;
        if (args.length == 5) {
            startType = Integer.parseInt(args[0]);
            if (startType != 1 && startType != 2) {
                throw new IllegalArgumentException("Support only 1 or 2 type (1 - one player in app, 2 - two players in app), received " + args[0]);
            }
            port = Integer.parseInt(args[1]);
            player1Name = args[2];
            player2Name = args[3];
            initMessage = args[4];
        } else if (args.length != 0) {
            throw new IllegalArgumentException("Support only 4 or 0 arguments. 1 - start type " +
                    "(1 - one player in app, 2 - two players in app) 2 - port (0-65535), 3 - 1 player name, 4 - 2 player name (ignore in 1 start type), 5 - init message.");
        }
        Player player1 = new Player(player1Name, new Communicator(port), initMessage);
        Thread pl1Thread = new Thread(player1);
        pl1Thread.start();
        if (startType == 2) {
            Player player2 = new Player(player2Name, new Communicator(port), initMessage);
            Thread pl2Thread = new Thread(player2);
            pl2Thread.start();
        }
    }
}
