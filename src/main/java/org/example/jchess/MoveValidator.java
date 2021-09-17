package org.example.jchess;

public interface MoveValidator {

    boolean isValid(BoardSnapshot boardSnapshot, Move moveToBeMade);
}
