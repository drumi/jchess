package org.example.jchess;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class BasicMoveValidator implements MoveValidator, MoveApplier, CheckChecker {

    @Override
    public boolean isValid(BoardSnapshot boardSnapshot, Move moveToBeMade) {

        return !hasSameSourceAndDestination(moveToBeMade) &&
                isMoveInbounds(moveToBeMade) &&
                !changesColor(boardSnapshot.getTiles(), moveToBeMade) &&
                didWhiteStart(boardSnapshot, moveToBeMade) &&
                !isPlayingTwice(boardSnapshot, moveToBeMade) &&
                !capturesPieceWithSameColor(boardSnapshot, moveToBeMade) &&
                movesLegally(boardSnapshot, moveToBeMade);
    }

    @Override
    public boolean isUnderCheck(List<List<Optional<OccupiedTile>>> board, Color defender) {
        return isKingUnderAttack(board, defender);
    }

    @Override
    public BoardSnapshot applyMove(BoardSnapshot boardSnapshot, Move move) {
        var tiles = boardSnapshot.getTiles();
        var moveHistory = boardSnapshot.getMovesHistory();

        Optional<OccupiedTile> tile = tiles.get(move.getFrom().getY())
                                           .get(move.getFrom().getX());

        if (move.getPiece() == Piece.KING) {
            int deltaX = move.getTo().getX() - move.getFrom().getX();
            int direction = getDirection(deltaX);

            if (Math.abs(deltaX) == 2) {
                int rooksX = direction > 0 ? 7 : 0;

                Optional<OccupiedTile> rooksTile = tiles.get(move.getFrom().getY())
                                                        .get(rooksX);

                tiles.get(move.getTo().getY())
                     .set(rooksX, Optional.empty());

                tiles.get(move.getFrom().getY())
                     .set(move.getFrom().getX() + direction, rooksTile);
            }
        }

        if (isValidEnPassant(boardSnapshot, move)) {
            tiles.get(move.getFrom().getY())
                 .set(move.getTo().getX(), Optional.empty());
        }

        tiles.get(move.getTo().getY())
             .set(move.getTo().getX(), tile);

        tiles.get(move.getFrom().getY())
             .set(move.getFrom().getX(), Optional.empty());

        if (isValidPawnPromotionData(move)) {
            OccupiedTile promotedTile = new OccupiedTile(move.getPromotedTo().get(), move.getColor());
            tiles.get(move.getTo().getY())
                 .set(move.getTo().getX(), Optional.of(promotedTile));
        }

        moveHistory.add(move);

        return new BoardSnapshot(tiles, moveHistory);
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
        Optional<OccupiedTile> tile = boardSnapshot.getTiles()
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
                        !boardHasPiecesBetween(boardSnapshot.getTiles(), src, dest) &&
                        !isKingIsUnderAttackAfterMove(boardSnapshot, move);

            case KING:
                boolean movedLikeAKing =
                        (movedVertically(src, dest) || movedHorizontally(src, dest) || movedDiagonally(src, dest)) &&
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
                        !boardHasPiecesBetween(boardSnapshot.getTiles(), src, dest) &&
                        !isKingIsUnderAttackAfterMove(boardSnapshot, move);

            case ROOK:
                return (movedVertically(src, dest) || movedHorizontally(src, dest)) &&
                        !boardHasPiecesBetween(boardSnapshot.getTiles(), src, dest) &&
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
        int deltaY = move.getTo().getY() - move.getFrom().getY();
        int direction = getDirection(deltaX);
        int rooksExpectedX = direction > 0 ? rightRooksExpectedX: leftRooksExpectedX;

        if (deltaY != 0) {
            return false;
        }

        var positionDestToCheckForPiecesInBetween = new Position(rooksExpectedX, castlingY);
        if (boardHasPiecesBetween(boardSnapshot.getTiles(), move.getFrom(), positionDestToCheckForPiecesInBetween)) {
            return false;
        }

        if (wasPieceEverMovedOrCaptured(boardSnapshot, move.getFrom(), kingsTile) ||
                wasPieceEverMovedOrCaptured(boardSnapshot, new Position(rooksExpectedX, castlingY), rooksTile)) {
            return false;
        }

        // Same as 'for x in [kingsStartingX, rooksExpectedX)' when kingsStartingX < rooksExpectedX
        // Same as 'for x in [rooksExpectedX, kingsStartingX)' when rooksExpectedX < kingsStartingX
        for (int x = kingsStartingX; Math.abs(x - rooksExpectedX) != 1 ; x += direction) {
            var tiles = boardSnapshot.getTiles();
            var playerColor = move.getColor();
            Position positionUnderCheck = new Position(x, castlingY);

            if(isPositionUnderAttack(tiles, playerColor, positionUnderCheck)) {
                return false;
            }
        }

        return true;
    }

    private boolean isCorrectPawnMove(BoardSnapshot boardSnapshot, Move move) {
        if (!movedForward(move.getFrom(), move.getTo(), move.getColor())) {
            return false;
        }

        if (movedVertically(move.getFrom(), move.getTo())) {
            if (isOccupied(boardSnapshot.getTiles(), move.getTo())) {
                return false;
            }

            if (areNeighbouringPositions(move.getFrom(), move.getTo())) {
                int destY = move.getTo().getY();

                if ( destY == 0 || destY == 7) {
                    return isValidPawnPromotionData(move);
                }

                return !move.getPromotedTo().isPresent();
            }

            return isCorrectPawnDoubleSquarePush(boardSnapshot.getTiles(), move);
        }

        if (movedDiagonally(move.getFrom(), move.getTo())) {
            if (!isOccupied(boardSnapshot.getTiles(), move.getTo())) {
                return isValidEnPassant(boardSnapshot, move);
            }

            if (!areNeighbouringPositions(move.getFrom(), move.getTo())) {
                return false;
            }

            int destY = move.getTo().getY();

            if ( destY == 0 || destY == 7) {
                return isValidPawnPromotionData(move);
            }

            return !move.getPromotedTo().isPresent();
        }

        return false;
    }

    private boolean isCorrectPawnDoubleSquarePush(List<List<Optional<OccupiedTile>>> board, Move move) {
        if (move.getPiece() != Piece.PAWN || move.getPromotedTo().isPresent()) {
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

        boolean isThereABlockingPiece = obtainFirstOccupiedTilePositionIncludingDest(board, move.getFrom(), new Position(destX, destY)).isPresent();
        return !isThereABlockingPiece;
    }

    private boolean isValidEnPassant(BoardSnapshot boardSnapshot, Move move) {
        if (move.getPiece() != Piece.PAWN) {
            return false;
        }

        if (!boardSnapshot.getLastMove().isPresent()) {
            return false;
        }

        if (boardSnapshot.getLastMove().get().getPiece() != Piece.PAWN) {
            return false;
        }

        if (!movedDiagonally(move.getFrom(), move.getTo())) {
            return false;
        }

        if (!areNeighbouringPositions(move.getFrom(), move.getTo())) {
            return false;
        }

        Move lastMove = boardSnapshot.getLastMove().get();

        int deltaYofLastMove = lastMove.getTo().getY() - lastMove.getFrom().getY();

        if (Math.abs(deltaYofLastMove) != 2) {
            return false;
        }

        if (!areNeighbouringPositions(lastMove.getTo(), move.getFrom()) ||
            !movedHorizontally(lastMove.getTo(), move.getFrom())) {
            return false;
        }

        return true;
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

    private boolean wasPieceEverMovedOrCaptured(BoardSnapshot boardSnapshot, Position position, OccupiedTile tile) {
        List<List<Optional<OccupiedTile>>> tiles = boardSnapshot.getTiles();
        List<Move> moveHistory = boardSnapshot.getMovesHistory();
        Optional<OccupiedTile> tileUnderCheck = tiles.get(position.getY())
                                                     .get(position.getX());

        // During castling, there will be no recorded move for the Rook as it is a Kings' move.
        // In order to check for such move this is necessary
        boolean isThePieceStillOnTheTile = tileUnderCheck.map(t -> t.getPiece() == tile.getPiece())
                                                         .orElse(false);

        boolean wasThePieceMovedOrCaptured = moveHistory.stream().anyMatch(move -> move.getTo().equals(position));

        return wasThePieceMovedOrCaptured || !isThePieceStillOnTheTile;
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
        int deltaY = dest.getY() - src.getY();

        return deltaX * deltaX + deltaY * deltaY == 5;
    }

    private boolean areNeighbouringPositions(Position src, Position dest) {
        int deltaX = dest.getX() - src.getX();
        int deltaY = dest.getY() - src.getY();

        return deltaX * deltaX + deltaY * deltaY <= 2;
    }

    private boolean boardHasPiecesBetween(List<List<Optional<OccupiedTile>>> board, Position src, Position dest) {
        return obtainFirstOccupiedTilePositionBetween(board, src, dest).isPresent();
    }

    private Optional<Position> obtainFirstOccupiedTilePositionBetween(List<List<Optional<OccupiedTile>>> board, Position src, Position dest) {
        var tilePosition = obtainFirstOccupiedTilePositionIncludingDest(board, src, dest);
        boolean isDestinationTileFound = tilePosition.map(t -> t.equals(dest))
                                                     .orElse(false);

        return isDestinationTileFound ? Optional.empty() : tilePosition;
    }

    private Optional<Position> obtainFirstOccupiedTilePositionIncludingDest(List<List<Optional<OccupiedTile>>> board, Position src, Position dest) {
        int deltaX = dest.getX() - src.getX();
        int deltaY = dest.getY() - src.getY();
        int directionOfX = getDirection(deltaX);
        int directionOfY = getDirection(deltaY);

        int x = src.getX();
        int y = src.getY();

        while (x != dest.getX() || y != dest.getY()) {
            x += directionOfX;
            y += directionOfY;

            if (board.get(y).get(x).isPresent()) {
                return Optional.of(new Position(x, y));
            }
        }

        return Optional.empty();
    }

    private boolean isOccupied (List<List<Optional<OccupiedTile>>> board, Position position) {
        return board.get(position.getY())
                    .get(position.getX())
                    .isPresent();
    }

    private int getDirection(int delta) {
        return  delta > 0 ? 1 :
                delta < 0 ? -1 :
                        0;
    }

    private boolean isKingIsUnderAttackAfterMove(BoardSnapshot boardSnapshot, Move move) {
        BoardSnapshot b = applyMove(boardSnapshot, move);
        return isKingUnderAttack(b.getTiles(), move.getColor());
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
        int x = position.getX();
        int y = position.getY();
        Color attacker = (defender == Color.WHITE) ? Color.BLACK : Color.WHITE;

        List<Position> edges = Arrays.asList(
                new Position(x, 0),
                new Position(x, 7),
                new Position(0, y),
                new Position(7, y),
                new Position(Math.max(x - y, 0), Math.max(y - x, 0)),
                new Position(Math.min(x - y + 7, 7), Math.min(y - x + 7, 7)),
                new Position(Math.min(x + y, 7), Math.max(x + y - 7, 0)),
                new Position(Math.max(x + y - 7, 0),Math.min(x + y, 7))
        );

        for(Position edge : edges) {
            Optional<Position> pos = obtainFirstOccupiedTilePositionIncludingDest(board, position, edge);
            Optional<OccupiedTile> optionalTile = pos.map(p -> board.get(p.getY()).get(p.getX()))
                                             .orElse(Optional.empty());

            if (optionalTile.isPresent()) {
                OccupiedTile tile = optionalTile.get();
                Position positionOfFoundTile = pos.get();

                if (tile.getPlayerColor() == attacker) {
                    switch (tile.getPiece()) {
                        case BISHOP:
                            if(movedDiagonally(positionOfFoundTile, position)) {
                                return true;
                            }
                            break;
                        case KING:
                            if (areNeighbouringPositions(positionOfFoundTile, position)) {
                                return true;
                            }
                            break;
                        case KNIGHT:
                                continue;
                        case PAWN:
                            if (movedDiagonally(positionOfFoundTile, position) &&
                                areNeighbouringPositions(positionOfFoundTile, position) &&
                                movedForward(positionOfFoundTile, position, attacker)) {
                                return true;
                            }
                            break;

                        case QUEEN:
                            return true;
                        case ROOK:
                            if (movedHorizontally(positionOfFoundTile, position) ||
                                movedVertically(positionOfFoundTile, position)) {
                                return true;
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + tile.getPiece());
                    }
                }
            }
        }

        return isAttackedFromAKnight(board, defender, position);
    }

    private boolean isAttackedFromAKnight(List<List<Optional<OccupiedTile>>> board, Color defender, Position position) {
        int x = position.getX();
        int y = position.getY();

        for (int i = x - 2; i <= x + 2; i++) {
            for (int j = y - 2; j <= y + 2; j++) {
                int deltaX = i - x;
                int deltaY = j - y;
                if (deltaX * deltaX + deltaY * deltaY == 5 && i >= 0 && j >= 0 && i < 8 && j < 8) {
                    Optional<OccupiedTile> tile = board.get(j).get(i);
                    boolean isThereAnEnemyKnight =tile.map(t -> t.getPiece() == Piece.KNIGHT && t.getPlayerColor() != defender)
                                                      .orElse(false);
                    if (isThereAnEnemyKnight) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean movedForward(Position src, Position dest, Color player) {
        int deltaY = dest.getY() - src.getY();

        if (player == Color.WHITE) {
            return deltaY < 0;
        }

        return deltaY > 0;
    }
}
