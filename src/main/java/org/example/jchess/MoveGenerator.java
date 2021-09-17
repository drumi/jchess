package org.example.jchess;

import java.util.List;

public interface MoveGenerator {

    List<Move> generateValidMoves(BoardSnapshot boardSnapshot);
}
