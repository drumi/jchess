package org.example.jchess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class DebugConsolePlayer implements Player {

    BufferedReader reader;
    Color color;
    Board board;
    MoveValidator validator;

    public DebugConsolePlayer(Color color) {
        this.color = Objects.requireNonNull(color);
        reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        board = new Board(new BasicMoveValidator());
        validator = new BasicMoveValidator();
    }

    @Override
    public Move obtainNextMove() {
        try {
            System.out.print("sourceX: ");
            int srcX = Integer.parseInt(reader.readLine());
            System.out.print("sourceY: ");
            int srcY = Integer.parseInt(reader.readLine());
            System.out.print("destX: ");
            int destX = Integer.parseInt(reader.readLine());
            System.out.print("destY: ");
            int destY = Integer.parseInt(reader.readLine());

            System.out.print("piece type: ");
            String pieceString = reader.readLine();

            System.out.print("pawn promoted to (or null): ");
            String promotedToString = reader.readLine();

            Optional<Piece> promotedTo;
            if ("null".equals(promotedToString)) {
                promotedTo = Optional.empty();
            } else {
                promotedTo = Optional.of(stringToPiece(promotedToString));
            }


            Move move = new Move(new Position(srcX, srcY),
                                 new Position(destX, destY),
                                 stringToPiece(pieceString),
                                 color,
                                 promotedTo);

            if (!validator.isValid(board.getSnapshot(), move)) {
                System.out.println("\nWarning: supplied move is invalid\n");
            }

            board.applyMove(move);
            return move;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerMove(Move move) {
        board.applyMove(move);
    }

    private Piece stringToPiece(String s) {
        if ("bishop".equals(s)) {
            return Piece.BISHOP;
        }

        if ("king".equals(s)) {
            return Piece.KING;
        }

        if ("knight".equals(s)) {
            return Piece.KNIGHT;
        }

        if ("pawn".equals(s)) {
            return Piece.PAWN;
        }

        if ("queen".equals(s)) {
            return Piece.QUEEN;
        }

        if ("rook".equals(s)) {
            return Piece.ROOK;
        }

        throw new RuntimeException("This code should not be reachable!");
    }
}
