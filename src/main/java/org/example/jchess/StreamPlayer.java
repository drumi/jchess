package org.example.jchess;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.stream.Stream;

public class StreamPlayer implements Player {

    private Board board;
    private InputStream input;
    private OutputStream output;

    public StreamPlayer(Board board, InputStream input, OutputStream output) {
        this.board = Objects.requireNonNull(board);
    }

    @Override
    public Move obtainNextMove() {
        return null;
    }

    @Override
    public void registerMove(Move move) {

    }

    private byte[] toBytes(Move move) {
        return null;
    }

    private Move fromBytes(byte[] buf) {
        return null;
    }
}
