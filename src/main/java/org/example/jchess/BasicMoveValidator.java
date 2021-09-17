package org.example.jchess;

import java.util.List;
import java.util.Optional;

public final class BasicMoveValidator implements MoveValidator {

    @Override
    public boolean isValid(BoardSnapshot boardSnapshot, Move moveToBeMade) {

        if (hasSameSourceAndDestination(moveToBeMade)) {
            return false;
        }

        if (!isMoveInbounds(moveToBeMade)) {
            return false;
        }

        if (changesColor(boardSnapshot.getBoard(), moveToBeMade)) {
            return false;
        }

        if (!didWhiteStart(boardSnapshot, moveToBeMade)) {
            return false;
        }

        if (isPlayingTwice(boardSnapshot, moveToBeMade)) {
            return false;
        }

        if (capturesPieceWithSameColor(boardSnapshot, moveToBeMade)) {
            return false;
        }

        if (!movesLegally(boardSnapshot, moveToBeMade)) {
            return false;
        }

        return true;
    }

    private boolean hasSameSourceAndDestination(Move move) {
        return move.getFrom().equals(move.getTo());
    }

    private boolean isMoveInbounds(Move move) {
        int srcX = move.getFrom().getX();
        int srcY = move.getFrom().getY();
        int destX = move.getTo().getX();
        int destY = move.getTo().getY();

        return srcX >= 0 && srcY >= 0 && destX >= 0 && destY >= 0 &&
                srcX < 8 && srcY < 8 && destX < 8 && destY < 8;
    }

    private boolean capturesPieceWithSameColor(BoardSnapshot boardSnapshot, Move move) {
        int destX = move.getTo().getX();
        int destY = move.getTo().getY();
        Optional<OccupiedTile> tile = boardSnapshot.getBoard()
                                                   .get(destY)
                                                   .get(destX);

        boolean hasSameColor = tile.map((occupiedTile -> occupiedTile.getPlayerColor() == move.getColor()))
                                   .orElse(false);

        return hasSameColor;
    }

    private boolean changesColor(List<List<Optional<OccupiedTile>>> board, Move move) {
        int srcX = move.getFrom().getX();
        int srcY = move.getFrom().getY();

        Optional<OccupiedTile> tile = board.get(srcY).get(srcX);
        return tile.map(t -> move.getColor() != t.getPlayerColor())
                .orElse(false);
    }

    private boolean didWhiteStart(BoardSnapshot boardSnapshot, Move move) {
        int size = boardSnapshot.getMovesHistory().size();

        if (size == 0) {
            return true;
        }

        Color firstMoveColor = boardSnapshot.getMovesHistory().get(0).getColor();

        return firstMoveColor == Color.WHITE;
    }

    private boolean isPlayingTwice (BoardSnapshot boardSnapshot, Move move) {
        var lastMove = boardSnapshot.getLastMove();

        return lastMove.map(m -> m.getColor() == move.getColor())
                        .orElse(false);
    }

    private boolean movesLegally(BoardSnapshot boardSnapshot, Move move) {
        Position src = move.getFrom();
        Position dest = move.getTo();

        switch (move.getPiece()) {
            case BISHOP:
                return movedDiagonally(src, dest) &&
                        !boardHasPiecesBetween(boardSnapshot.getBoard(), src, dest) &&
                        !isKingIsUnderAttackAfterMove(boardSnapshot, move);

            case KING:
                boolean movedLikeAKing =
                        (movedVertically(src, dest) || movedHorizontally(src, dest)) &&
                        areNeighbouringPositions(src, dest);

                return (movedLikeAKing || isCorrectCastlingMove(boardSnapshot, move)) &&
                        !isKingIsUnderAttackAfterMove(boardSnapshot, move);

            case KNIGHT:
                return movedLikeAKnight(src, dest) &&
                        !isKingIsUnderAttackAfterMove(boardSnapshot, move);

            case PAWN:
                return isCorrectPawnMove(boardSnapshot, move) &&
                        !isKingIsUnderAttackAfterMove(boardSnapshot, move);

            case QUEEN:
                return (movedDiagonally(src, dest) || movedVertically(src, dest) || movedHorizontally(src, dest)) &&
                        !boardHasPiecesBetween(boardSnapshot.getBoard(), src, dest) &&
                        !isKingIsUnderAttackAfterMove(boardSnapshot, move);

            case ROOK:
                return (movedVertically(src, dest) || movedHorizontally(src, dest)) &&
                        !boardHasPiecesBetween(boardSnapshot.getBoard(), src, dest) &&
                        !isKingIsUnderAttackAfterMove(boardSnapshot, move);

            default:
                throw new IllegalStateException("Unexpected value: " + move.getPiece());
        }
    }

    private boolean isCorrectCastlingMove(BoardSnapshot boardSnapshot, Move move) {
        if (move.getPiece() != Piece.KING) {
            return false;
        }

        int leftRooksExpectedX = 0;
        int rightRooksExpectedX = 7;
        int castlingY = move.getFrom().getY();
        int kingsStartingX = 4;

        OccupiedTile kingsTile = new OccupiedTile(Piece.KING, move.getColor());
        OccupiedTile rooksTile = new OccupiedTile(Piece.ROOK, move.getColor());

        int deltaX = move.getTo().getX() - move.getFrom().getX();
        int direction = getDirection(deltaX);
        int rooksExpectedX = direction > 0 ? rightRooksExpectedX: leftRooksExpectedX;

        if (wasPieceEverMoved(boardSnapshot, move.getFrom(), kingsTile) &&
                wasPieceEverMoved(boardSnapshot, new Position(rooksExpectedX, castlingY), rooksTile)) {
            return false;
        }

        // Same as 'for x in [kingsStartingX, rooksExpectedX)' when kingsStartingX < rooksExpectedX
        // Same as 'for x in [rooksExpectedX, kingsStartingX)' when rooksExpectedX < kingsStartingX
        for (int x = kingsStartingX; Math.abs(x - rooksExpectedX) != 1 ; x += direction) {
            var board = boardSnapshot.getBoard();
            var playerColor = move.getColor();
            Position positionUnderCheck = new Position(x, castlingY);

            if(isPositionUnderAttack(board, playerColor, positionUnderCheck)) {
                return false;
            }
        }

        return true;
    }

    private boolean isCorrectPawnMove(BoardSnapshot boardSnapshot, Move move) {
        if (movedHorizontally(move.getFrom(), move.getTo())) {
            if (isOccupied(boardSnapshot.getBoard(), move.getTo())) {
                return false;
            }

            if (areNeighbouringPositions(move.getFrom(), move.getTo())) {
                int destY = move.getTo().getY();

                if ( destY == 0 || destY == 7) {
                    return isValidPawnPromotionData(move);
                }

                return true;
            }

            return isCorrectPawnDoubleSquarePush(boardSnapshot.getBoard(), move);
        }

        if (movedVertically(move.getFrom(), move.getTo())) {
            if (!isOccupied(boardSnapshot.getBoard(), move.getTo())) {
                return isValidEnPassant(boardSnapshot, move);
            }

            if (!areNeighbouringPositions(move.getFrom(), move.getTo())) {
                return false;
            }

            int destY = move.getTo().getY();

            if ( destY == 0 || destY == 7) {
                return isValidPawnPromotionData(move);
            }

            return true;
        }

        return false;
    }

    private boolean isCorrectPawnDoubleSquarePush(List<List<Optional<OccupiedTile>>> board, Move move) {
        if (move.getPiece() != Piece.PAWN) {
            return false;
        }

        Color playerColor = move.getColor();
        int srcX = move.getFrom().getX();
        int srcY = move.getFrom().getY();
        int destX = move.getTo().getX();
        int destY = move.getTo().getY();

        int deltaY = destY - srcY;
        int deltaX = destX - srcX;

        int whitePawnStartingY = 6;
        int blackPawnStartingY = 1;

        if (deltaX != 0 || Math.abs(deltaY) != 2) {
            return false;
        }

        if (playerColor == Color.WHITE && srcY != whitePawnStartingY) {
            return false;
        }

        if (playerColor == Color.BLACK && srcY != blackPawnStartingY) {
            return false;
        }

        int directionY = getDirection(deltaY);
        boolean isThereABlockingPiece = boardHasPiecesBetween(board, move.getFrom(), new Position(destX, destY + directionY));

        return isThereABlockingPiece;
    }

    boolean isValidEnPassant(BoardSnapshot boardSnapshot, Move move) {
        if (move.getPiece() != Piece.PAWN) {
            return false;
        }

        if (!movedVertically(move.getFrom(), move.getTo())) {
            return false;
        }

        if (!areNeighbouringPositions(move.getFrom(), move.getTo())) {
            return false;
        }

        Move lastMove = boardSnapshot.getLastMove().get();

        int deltaYofLastMove = lastMove.getTo().getY() - lastMove.getFrom().getY();

        if (deltaYofLastMove != 2 && deltaYofLastMove != -2) {
            return false;
        }

        return lastMove.getTo().getX() == move.getTo().getX();
    }

    private boolean isValidPawnPromotionData(Move move) {
        if (move.getPiece() != Piece.PAWN) {
            return false;
        }

        Optional<Piece> promotedPiece = move.getPromotedTo();
        if (!promotedPiece.isPresent() ||
            promotedPiece.get() == Piece.PAWN ||
            promotedPiece.get() == Piece.KING) {
            return false;
        }

        Color playerColor = move.getColor();
        int destY = move.getTo().getY();

        return  (destY == 0 && playerColor == Color.WHITE) ||
                (destY == 7 && playerColor == Color.BLACK);
    }

    private boolean wasPieceEverMoved(BoardSnapshot boardSnapshot, Position position, OccupiedTile tile) {
        List<List<Optional<OccupiedTile>>> board = boardSnapshot.getBoard();
        List<Move> moveHistory = boardSnapshot.getMovesHistory();
        Optional<OccupiedTile> tileUnderCheck = board.get(position.getY())
                                                     .get(position.getX());

        // During castling, there will be no recorded move for the Rook as it is a Kings' move.
        // In order to check for such move this is necessary
        boolean isThePieceStillOnTheTile = tileUnderCheck.map(t -> t.getPiece() == tile.getPiece())
                                                         .orElse(false);

        boolean wasThePieceMoved = moveHistory.stream().anyMatch(move -> move.getFrom().equals(position));

        return wasThePieceMoved || !isThePieceStillOnTheTile;
    }

    private boolean movedHorizontally(Position src, Position dest) {
        return src.getY() == dest.getY();
    }

    private boolean movedVertically(Position src, Position dest) {
        return src.getX() == dest.getX();
    }

    private boolean movedDiagonally(Position src, Position dest) {
        return src.getX() - src.getY() == dest.getX() - dest.getY() ||
                src.getX() + src.getY() == dest.getX() + dest.getY();
    }

    private boolean movedLikeAKnight(Position src, Position dest) {
        int deltaX = dest.getX() - src.getX();
        int deltaY = dest.getX() - dest.getY();

        return deltaX * deltaX + deltaY * deltaY == 5;
    }

    private boolean areNeighbouringPositions(Position src, Position dest) {
        int deltaX = dest.getX() - src.getX();
        int deltaY = dest.getX() - dest.getY();

        return deltaX * deltaX + deltaY * deltaY <= 2;
    }

    private boolean boardHasPiecesBetween(List<List<Optional<OccupiedTile>>> board, Position src, Position dest) {
        return obtainFirstOccupiedTilePositionBetween(board, src, dest).isPresent();
    }

    private Optional<Position> obtainFirstOccupiedTilePositionBetween(List<List<Optional<OccupiedTile>>> board, Position src, Position dest) {
        int deltaX = dest.getX() - src.getX();
        int deltaY = dest.getX() - dest.getY();
        int directionOfX = getDirection(deltaX);
        int directionOfY = getDirection(deltaY);

        int x = src.getX();
        int y = src.getY();

        while (x != dest.getX() && y != dest.getY()) {
            if (board.get(x).get(y).isPresent()){
                return Optional.of(new Position(x, y));
            }

            x += directionOfX;
            y += directionOfY;
        }

        return Optional.empty();
    }

    private boolean isOccupied (List<List<Optional<OccupiedTile>>> board, Position position) {
        return board.get(position.getY()).get(position.getX()).isPresent();
    }

    private int getDirection(int delta) {
        return  delta > 0 ? 1 :
                delta < 0 ? -1 :
                        0;
    }

    private boolean isKingIsUnderAttackAfterMove(BoardSnapshot boardSnapshot, Move move) {
        //TODO
        return false;
    }

    private boolean isKingUnderAttack(List<List<Optional<OccupiedTile>>> board, Color defender) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean foundDefendersKing = board.get(row).get(col)
                     .map(tile -> tile.getPiece() == Piece.KING && tile.getPlayerColor() == defender)
                     .orElse(false);

                if (foundDefendersKing) {
                    return isPositionUnderAttack(board, defender, new Position(col, row));
                }
            }
        }

        throw new RuntimeException("This code should not be reachable!");
    }

    private boolean isPositionUnderAttack(List<List<Optional<OccupiedTile>>> board, Color defender, Position position) {
        // TODO
        return false;
    }
}
