package org.example.jchess;

import java.util.*;

public final class ChessGame implements Game {

    private final Player white;
    private final Player black;
    private final Engine engine;
    private final Board board;

    public ChessGame(Player white, Player black, Engine engine, Board board) {
        this.white = Objects.requireNonNull(white);
        this.black = Objects.requireNonNull(black);
        this.engine = Objects.requireNonNull(engine);
        this.board = Objects.requireNonNull(board);
    }

    public void run() {
        while (true) {
            Move whiteMove = white.obtainNextMove();
            black.registerMove(whiteMove);

            if (!isGameInProgress()) {
                break;
            }

            Move blackMove = black.obtainNextMove();
            white.registerMove(blackMove);

            if (!isGameInProgress()) {
                break;
            }
        }
    }

    private boolean isGameInProgress() {
        Report report = engine.analyseBoard(board.getSnapshot());
        GameState gameState = report.getGameState();

        return gameState != GameState.CHECKMATE &&
                gameState != GameState.STALEMATE &&
                gameState != GameState.DRAW;
    }

}
