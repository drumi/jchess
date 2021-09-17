package org.example.jchess;

import java.util.Objects;

public final class OccupiedTile {

    private final Piece piece;
    private final Color playerColor;

    public OccupiedTile(Piece piece, Color playerColor) {
        this.piece = Objects.requireNonNull(piece);
        this.playerColor = Objects.requireNonNull(playerColor);
    }

    public Piece getPiece() {
        return piece;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OccupiedTile that = (OccupiedTile) o;

        if (piece != that.piece) return false;
        return playerColor == that.playerColor;
    }

    @Override
    public int hashCode() {
        int result = piece.hashCode();
        result = 31 * result + playerColor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "OccupiedTile{" +
                "piece=" + piece +
                ", playerColor=" + playerColor +
                '}';
    }
}
