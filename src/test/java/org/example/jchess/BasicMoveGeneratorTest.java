package org.example.jchess;

import org.junit.*;

import java.util.ArrayList;

public class BasicMoveGeneratorTest {

    private BasicMoveGenerator generator;

    @Before
    public void setUp() {
        generator = new BasicMoveGenerator(new BasicMoveValidator());
    }
    @Test
    public void newGameShouldGenerate20Moves() {
        var board = BoardHelper.getStartingBoard();
        var result = generator.generateValidMoves(new BoardSnapshot(board, new ArrayList<>()));
        Assert.assertEquals(20, result.size());
    }
}
