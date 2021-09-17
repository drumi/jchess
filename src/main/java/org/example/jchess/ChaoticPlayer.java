package org.example.jchess;

import java.util.Objects;

public class ChaoticPlayer implements Player {

    private Board board;
    private MoveGenerator generator;

    public ChaoticPlayer(Board board, MoveGenerator generator) {
        this.board = Objects.requireNonNull(board);
        this.generator = Objects.requireNonNull(generator);
    }

    @Override
    public Move obtainNextMove() {
        var moves = generator.generateValidMoves(board.getSnapshot());
        int size = moves.size();
        int idx = (int) Math.floor(size * Math.random());

        return moves.get(idx);
    }

    @Override
    public void registerMove(Move move) {
        board.applyMove(move);
    }
}
