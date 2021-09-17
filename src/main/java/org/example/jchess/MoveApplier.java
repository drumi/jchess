package org.example.jchess;

import java.util.Optional;

public interface MoveApplier {

    BoardSnapshot applyMove(BoardSnapshot boardSnapshot, Move move);
}
