package org.example.jchess;

public interface Player {

    Move obtainNextMove();
    void registerMove(Move move);
}
