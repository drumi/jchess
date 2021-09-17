package org.example.jchess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class BoardSnapshot {

    private final List<List<Optional<OccupiedTile>>> tiles;
    private final List<Move> movesHistory;

    public BoardSnapshot(List<List<Optional<OccupiedTile>>> tiles, List<Move> movesHistory) {
        Objects.requireNonNull(tiles);
        Objects.requireNonNull(movesHistory);
        tiles.stream().forEach(Objects::requireNonNull);

        this.tiles = copy(tiles);
        this.movesHistory = new ArrayList<>(movesHistory);
    }

    private List<List<Optional<OccupiedTile>>> copy(List<List<Optional<OccupiedTile>>> src) {
        List<List<Optional<OccupiedTile>>> dest = new ArrayList<>();

        for (var list : src) {
            dest.add(new ArrayList<>(list));
        }

        return dest;
    }

    public List<List<Optional<OccupiedTile>>> getTiles() {
        return copy(tiles);
    }

    public List<Move> getMovesHistory() {
        return new ArrayList<>(movesHistory);
    }

    public Optional<Move> getLastMove() {
        int size = movesHistory.size();
        return size > 0 ? Optional.of(movesHistory.get(size - 1)) : Optional.empty();
    }
}
