package org.example.jchess;

import java.util.Objects;

public class ChaoticPlayer implements Player {

    private final Board board;
    private final MoveGenerator generator;

    public ChaoticPlayer(Board board, MoveGenerator generator) {
        this.board = Objects.requireNonNull(board);
        this.generator = Objects.requireNonNull(generator);
    }

    @Override
    public Move obtainNextMove() {
        var moves = generator.generateValidMoves(board.getSnapshot());
        int size = moves.size();
        int idx = (int) Math.floor(size * Math.random());

        Move move = moves.get(idx);
        board.applyMove(move);

        return move;
    }

    @Override
    public void registerMove(Move move) {
        board.applyMove(move);
    }
}
