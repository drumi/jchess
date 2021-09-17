package org.example.jchess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class BoardSnapshot {

    private final List<List<Optional<OccupiedTile>>> board;
    private final List<Move> movesHistory;

    public BoardSnapshot(List<List<Optional<OccupiedTile>>> board, List<Move> movesHistory) {
        Objects.requireNonNull(board);
        Objects.requireNonNull(movesHistory);
        board.stream().forEach(Objects::requireNonNull);

        this.board = copy(board);
        this.movesHistory = new ArrayList<>(movesHistory);
    }

    private List<List<Optional<OccupiedTile>>> copy(List<List<Optional<OccupiedTile>>> src) {
        List<List<Optional<OccupiedTile>>> dest = new ArrayList<>();

        for (var list : src) {
            dest.add(new ArrayList<>(list));
        }

        return dest;
    }

    public List<List<Optional<OccupiedTile>>> getBoard() {
        return copy(board);
    }

    public List<Move> getMovesHistory() {
        return new ArrayList<>(movesHistory);
    }

    public Optional<Move> getLastMove() {
        int size = movesHistory.size();
        return size > 0 ? Optional.of(movesHistory.get(size - 1)) : Optional.empty();
    }
}
