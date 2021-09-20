package org.example.jchess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class StreamPlayerProxy implements Player {

    private Board board;
    private InputStream input;
    private OutputStream output;
    private MoveSerializer serializer;

    public StreamPlayerProxy(Board board, InputStream input, OutputStream output, MoveSerializer serializer) {
        this.board = Objects.requireNonNull(board);
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
        this.serializer = Objects.requireNonNull(serializer);
    }

    @Override
    public Move obtainNextMove() {
        try {
            byte[] bytes = input.readAllBytes();
            Move move = serializer.deserialize(bytes);
            board.applyMove(move);
            return move;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerMove(Move move) {
        try {
            byte[] bytes = serializer.serialize(move);
            output.write(bytes);
            output.flush();
            board.applyMove(move);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
