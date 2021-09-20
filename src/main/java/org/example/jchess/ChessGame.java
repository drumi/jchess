package org.example.jchess;

import java.util.*;

public final class ChessGame implements Game {

    private final Player white;
    private final Player black;
    private final Engine engine;
    private final Board board;
    private final Renderer renderer;

    public ChessGame(Player white, Player black, Engine engine, Board board, Renderer renderer) {
        this.white = Objects.requireNonNull(white);
        this.black = Objects.requireNonNull(black);
        this.engine = Objects.requireNonNull(engine);
        this.board = Objects.requireNonNull(board);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public void run() {
        renderer.draw(board.getSnapshot());

        while (true) {
            Move whiteMove = white.obtainNextMove();
            black.registerMove(whiteMove);
            board.applyMove(whiteMove);

            renderer.draw(board.getSnapshot());

            if (!isGameInProgress()) {
                break;
            }

            Move blackMove = black.obtainNextMove();
            white.registerMove(blackMove);
            board.applyMove(blackMove);

            renderer.draw(board.getSnapshot());

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
