package org.example.jchess;

public interface Player {

    Move obtainNextMove();
    void notifyForOtherPlayersMove(Move move);
}
