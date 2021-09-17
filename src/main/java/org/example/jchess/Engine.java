package org.example.jchess;

import java.util.List;

public interface Engine {

    List<Move> generateMoves(BoardSnapshot boardSnapshot);
    boolean isValidMove(BoardSnapshot boardSnapshot, Move moveToValidate);
    Report analyseBoard(BoardSnapshot boardSnapshot);
}
