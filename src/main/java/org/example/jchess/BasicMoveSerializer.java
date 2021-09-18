package org.example.jchess;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BasicMoveSerializer implements MoveSerializer {

    private static final String delimiter = ";";
    @Override
    public byte[] serialize(Move move) {
        String s = move.getFrom().getX() + delimiter +
                    move.getFrom().getY() + delimiter +
                    move.getTo().getX() + delimiter +
                    move.getTo().getY() + delimiter +
                    colorToString(move.getColor())+ delimiter +
                    pieceToString(move.getPiece()) + delimiter +
                    promotedToString(move.getPromotedTo());

        return s.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Move deserialize(byte[] bytes) {
        String s = new String(bytes, StandardCharsets.UTF_8);
        String[] segments = s.split(delimiter);

        int srcX = Integer.parseInt(segments[0]);
        int srcY = Integer.parseInt(segments[1]);
        int destX = Integer.parseInt(segments[2]);
        int destY = Integer.parseInt(segments[3]);
        Color color = stringToColor(segments[4]);
        Piece piece = stringToPiece(segments[5]);
        Optional<Piece> promotedTo = stringToPromoted(segments[6]);

        return new Move(new Position(srcX, srcY),
                        new Position(destX, destY),
                        piece,
                        color,
                        promotedTo);
    }

    private String colorToString(Color color) {
        return color == Color.WHITE ? "white" : "black" ;
    }

    private Color stringToColor(String s) {
        if ("white".equals(s)) {
            return Color.WHITE;
        }

        if ("black".equals(s)) {
            return Color.BLACK;
        }

        throw new RuntimeException("This code should not be reachable!");
    }

    private String pieceToString(Piece piece) {
        switch (piece) {
            case BISHOP:
                return "bishop";
            case KING:
                return "king";
            case KNIGHT:
                return "knight";
            case PAWN:
                return "pawn";
            case QUEEN:
                return "queen";
            case ROOK:
                return "rook";
            default:
                throw new IllegalStateException("Unexpected value: " + piece);
        }
    }

    private Piece stringToPiece(String s) {
        if ("bishop".equals(s)) {
            return Piece.BISHOP;
        }

        if ("king".equals(s)) {
            return Piece.BISHOP;
        }

        if ("knight".equals(s)) {
            return Piece.BISHOP;
        }

        if ("pawn".equals(s)) {
            return Piece.BISHOP;
        }

        if ("queen".equals(s)) {
            return Piece.BISHOP;
        }

        if ("rook".equals(s)) {
            return Piece.BISHOP;
        }

        throw new RuntimeException("This code should not be reachable!");
    }

    private String promotedToString(Optional<Piece> p) {
        return p.map(this::pieceToString)
                .orElse("empty");
    }

    private Optional<Piece> stringToPromoted(String s) {
        if ("empty".equals(s)) {
            return Optional.empty();
        }

        return Optional.of(stringToPiece(s));
    }
}
