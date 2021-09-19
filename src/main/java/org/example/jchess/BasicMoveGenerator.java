package org.example.jchess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BasicMoveGenerator implements MoveGenerator {

    private final MoveValidator validator;

    public BasicMoveGenerator(MoveValidator validator) {
        this.validator = Objects.requireNonNull(validator);
    }

    @Override
    public List<Move> generateValidMoves(BoardSnapshot boardSnapshot) {
        List<Move> dummyMoves = generateDummyMoves(boardSnapshot);
        List<Move> validMoves = new ArrayList<>();

        for (Move move : dummyMoves) {
            if (validator.isValid(boardSnapshot, move)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    private List<Move> generateDummyMoves(BoardSnapshot boardSnapshot) {
        Color player = boardSnapshot.getLastMove()
                                    .map(m -> m.getColor() == Color.BLACK ? Color.WHITE : Color.BLACK)
                                    .orElse(Color.WHITE);

        List<List<Optional<OccupiedTile>>> tiles = boardSnapshot.getTiles();
        List<Move> result = new ArrayList<>();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Position position = new Position(x, y);
                Optional<OccupiedTile> tile = tiles.get(y).get(x);
                List<Move> moves = tile.map(r -> generateDummyMovesForPiece(r.getPiece(), position, player))
                                        .orElse(new ArrayList<Move>());
                result.addAll(moves);
            }
        }

        return result;
    }

    private List<Move> generateDummyMovesForPiece(Piece piece, Position position, Color player) {
        List<Move> result = new ArrayList<>();
        int srcX = position.getX();
        int srcY = position.getY();

        // Knight moves
        if (piece == Piece.KNIGHT) {
            for (int x = srcX - 2; x <= srcX + 2; x++) {
                for (int y = srcY - 2; y <= srcY + 2; y++) {
                    int deltaX = srcX - x;
                    int deltaY = srcY - y;
                    if (deltaX * deltaX + deltaY * deltaY == 5 && x >= 0 && y >= 0) {
                        result.add(new Move(position, new Position(x, y), piece, player, Optional.empty()));
                    }
                }
            }

            return result;
        }


        // Horizontal moves
        for (int x = 0; x < 8; x++) {
            result.add(new Move(position, new Position(x, srcY), piece, player, Optional.empty()));
        }

        // Vertical moves
        for (int y = 0; y < 8; y++) {
            result.add(new Move(position, new Position(srcX, y), piece, player, Optional.empty()));
            if (piece == Piece.PAWN) {
                result.add(new Move(position, new Position(srcX, y), piece, player, Optional.of(Piece.QUEEN)));
            }
        }

        // Diagonal moves
        for (int x = srcX, y = srcY; x < 8 && y < 8; x++, y++) {
            result.add(new Move(position, new Position(x, y), piece, player, Optional.empty()));
            if (piece == Piece.PAWN) {
                result.add(new Move(position, new Position(x, y), piece, player, Optional.of(Piece.QUEEN)));
            }
        }
        for (int x = srcX, y = srcY; x >= 0 && y < 8; x--, y++) {
            result.add(new Move(position, new Position(x, y), piece, player, Optional.empty()));
            if (piece == Piece.PAWN) {
                result.add(new Move(position, new Position(x, y), piece, player, Optional.of(Piece.QUEEN)));
            }
        }
        for (int x = srcX, y = srcY; x < 8 && y >= 0; x++, y--) {
            result.add(new Move(position, new Position(x, y), piece, player, Optional.empty()));
            if (piece == Piece.PAWN) {
                result.add(new Move(position, new Position(x, y), piece, player, Optional.of(Piece.QUEEN)));
            }
        }
        for (int x = srcX, y = srcY; x >= 0 && y >= 0; x--, y--) {
            result.add(new Move(position, new Position(x, y), piece, player, Optional.empty()));
            if (piece == Piece.PAWN) {
                result.add(new Move(position, new Position(x, y), piece, player, Optional.of(Piece.QUEEN)));
            }
        }

        return result;
    }
}
