package org.example.jchess;

public class Main {

    public static void main(String[] args) {
        final int DELAY_MS = 1000;

        MoveValidator validator = new BasicMoveValidator();
        MoveGenerator generator = new BasicMoveGenerator(validator);
        MoveApplier applier = new BasicMoveValidator();

        CheckChecker checker = new BasicMoveValidator();

        Engine engine = new BasicEngine(validator, generator, checker);

        Board whiteBoard = new Board(applier);
        Board blackBoard = new Board(applier);
        Board gameBoard = new Board(applier);

        Player white = new ChaoticPlayer(whiteBoard, generator);
        Player black = new ChaoticPlayer(blackBoard, generator);

        Renderer baseRenderer = new StreamRenderer(System.out);
        Renderer renderer = new DelayRendererDecorator(baseRenderer, DELAY_MS);

        Game game = new ChessGame(white, black, engine, gameBoard, renderer);

        game.run();
    }
}
