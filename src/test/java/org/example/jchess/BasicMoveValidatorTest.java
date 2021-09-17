package org.example.jchess;

import org.junit.*;

import java.util.ArrayList;
import java.util.Optional;

public class BasicMoveValidatorTest {

    MoveValidator validator;
    @Before
    public void setUp() {
        validator = new BasicMoveValidator();
    }

    @Test
    public void shouldBeAbleToMoveDoublePawnsOnStartingBoard() {
        var startingBoard = BoardHelper.getStartingBoard();
        for (int x = 0; x < 8; x++) {
            var move = new Move(new Position(x, 6), new Position(x, 4), Piece.PAWN, Color.WHITE, Optional.empty());
            var snapshot = new BoardSnapshot(startingBoard, new ArrayList<>());
            Assert.assertTrue(validator.isValid(snapshot, move));
        }
    }


    @Test
    public void shouldBeAbleToMoveSinglePawnsOnStartingBoard() {
        var startingBoard = BoardHelper.getStartingBoard();
        for (int x = 0; x < 8; x++) {
            var move = new Move(new Position(x, 6), new Position(x, 5), Piece.PAWN, Color.WHITE, Optional.empty());
            var snapshot = new BoardSnapshot(startingBoard, new ArrayList<>());
            Assert.assertTrue(validator.isValid(snapshot, move));
        }
    }

    @Test
    public void shouldBeAbleToMoveKnightsOnStartingBoard() {
        var startingBoard = BoardHelper.getStartingBoard();
        for (int x = 1; x < 8 ; x += 5) {
            for (int i = -1; i < 2; i += 2) {
                var move = new Move(new Position(x, 7), new Position(x + i, 5), Piece.KNIGHT, Color.WHITE, Optional.empty());
                var snapshot = new BoardSnapshot(startingBoard, new ArrayList<>());
                Assert.assertTrue(validator.isValid(snapshot, move));
            }
        }
    }
}
