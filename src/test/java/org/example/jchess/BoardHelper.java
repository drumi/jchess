package org.example.jchess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class BoardHelper {
    private BoardHelper() {
    }

    public static List<List<Optional<OccupiedTile>>> getStartingBoard() {
        return Arrays.asList(
                constructEdgeRow(Color.BLACK),
                constructPawnRow(Color.BLACK),
                constructEmptyRow(),
                constructEmptyRow(),
                constructEmptyRow(),
                constructEmptyRow(),
                constructPawnRow(Color.WHITE),
                constructEdgeRow(Color.WHITE)
        );
    }

    public static List<Optional<OccupiedTile>> constructEdgeRow(Color player) {
        var topRow = Arrays.asList(
                Optional.of(new OccupiedTile(Piece.ROOK, player)),
                Optional.of(new OccupiedTile(Piece.KNIGHT, player)),
                Optional.of(new OccupiedTile(Piece.BISHOP, player)),
                Optional.of(new OccupiedTile(Piece.QUEEN, player)),
                Optional.of(new OccupiedTile(Piece.KING, player)),
                Optional.of(new OccupiedTile(Piece.BISHOP, player)),
                Optional.of(new OccupiedTile(Piece.KNIGHT, player)),
                Optional.of(new OccupiedTile(Piece.ROOK, player))
        );

        return topRow;
    }

    public static List<Optional<OccupiedTile>> constructPawnRow(Color player) {
        List<Optional<OccupiedTile>> pawnRow = new ArrayList<>();

        for (int columns = 0; columns < 8; columns++) {
            pawnRow.add(Optional.of(new OccupiedTile(Piece.PAWN, player)));
        }

        return pawnRow;
    }

    public static List<Optional<OccupiedTile>> constructEmptyRow() {
        List<Optional<OccupiedTile>> emptyRow = new ArrayList<>();

        for (int columns = 0; columns < 8; columns++) {
            emptyRow.add(Optional.empty());
        }

        return emptyRow;
    }
}
