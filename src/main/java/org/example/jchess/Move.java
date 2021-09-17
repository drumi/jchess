package org.example.jchess;

import java.util.Objects;
import java.util.Optional;

public final class Move {

    private final Position from;
    private final Position to;
    private final Piece piece;
    private final Color color;
    private final Optional<Piece> promotedTo;

    public Move(Position from, Position to, Piece piece, Color color, Optional<Piece> promotedTo) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.piece = Objects.requireNonNull(piece);
        this.color = Objects.requireNonNull(color);
        this.promotedTo = Objects.requireNonNull(promotedTo);
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getPiece() {
        return piece;
    }

    public Color getColor() {
        return color;
    }

    public Optional<Piece> getPromotedTo() {
        return promotedTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (!from.equals(move.from)) return false;
        if (!to.equals(move.to)) return false;
        if (piece != move.piece) return false;
        if (color != move.color) return false;
        return promotedTo.equals(move.promotedTo);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + piece.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + promotedTo.hashCode();
        return result;
    }
}
