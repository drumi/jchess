package org.example.jchess;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BasicEngine implements Engine {

    private final MoveValidator validator;
    private final MoveGenerator generator;
    private final CheckChecker checker;

    public BasicEngine(MoveValidator validator, MoveGenerator generator, CheckChecker checker) {
        this.validator = Objects.requireNonNull(validator);
        this.generator = Objects.requireNonNull(generator);
        this.checker = Objects.requireNonNull(checker);
    }

    @Override
    public List<Move> generateMoves(BoardSnapshot boardSnapshot) {
        return generator.generateValidMoves(boardSnapshot);
    }

    @Override
    public boolean isValidMove(BoardSnapshot boardSnapshot, Move moveToValidate) {
        return validator.isValid(boardSnapshot, moveToValidate);
    }

    @Override
    public Report analyseBoard(BoardSnapshot boardSnapshot) {
        var moves = generator.generateValidMoves(boardSnapshot);
        Color player = getCurrentPlayersColor(boardSnapshot);
        Color opponent = getOpponentsColor(player);

        if (moves.size() == 0) {
            if (checker.isUnderCheck(boardSnapshot.getBoard(), player)) {
                return new Report(player, GameState.CHECKMATE, Optional.of(opponent));
            } else {
                return new Report(player, GameState.STALEMATE, Optional.empty());
            }
        } else {
            if (checker.isUnderCheck(boardSnapshot.getBoard(), player)) {
                return new Report(player, GameState.CHECK, Optional.of(opponent));
            } else {
                return new Report(player, GameState.NORMAL, Optional.empty());
            }
        }
    }

    private Color getCurrentPlayersColor(BoardSnapshot boardSnapshot) {
        return boardSnapshot.getLastMove()
                     .map(c -> getOpponentsColor(c.getColor()))
                     .orElse(Color.WHITE);
    }

    private Color getOpponentsColor(Color player) {
        return player == Color.BLACK ? Color.WHITE : Color.BLACK;
    }
}
