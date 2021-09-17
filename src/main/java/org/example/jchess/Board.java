package org.example.jchess;

import java.util.*;

public class Board {

    private BoardSnapshot snapshot;
    private MoveApplier applier;

    public Board(MoveApplier applier) {
        this.applier = Objects.requireNonNull(applier);

        snapshot = new BoardSnapshot(initBoard(), new ArrayList<>());
    }


    private List<List<Optional<OccupiedTile>>> initBoard() {
        return  Arrays.asList(
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

    private List<Optional<OccupiedTile>> constructEdgeRow(Color player) {
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

    private List<Optional<OccupiedTile>> constructPawnRow(Color player) {
        List<Optional<OccupiedTile>> pawnRow = new ArrayList<>();

        for (int columns = 0; columns < 8; columns++) {
            pawnRow.add(Optional.of(new OccupiedTile(Piece.PAWN, player)));
        }

        return pawnRow;
    }

    private List<Optional<OccupiedTile>> constructEmptyRow() {
        List<Optional<OccupiedTile>> emptyRow = new ArrayList<>();

        for (int columns = 0; columns < 8; columns++) {
            emptyRow.add(Optional.empty());
        }

        return emptyRow;
    }

    public void applyMove(Move move) {
        snapshot = applier.applyMove(snapshot, move);
    }

    public BoardSnapshot getSnapshot() {
        return snapshot;
    }
}
