package org.example.jchess;

public interface MoveSerializer {

    byte[] serialize(Move move);
    Move deserialize(byte[] bytes);
}
