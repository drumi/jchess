package org.example.jchess;

import org.junit.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BasicMoveSerializerTest {

    private MoveSerializer serializer;

    @Before
    public void setUp() {
        serializer = new BasicMoveSerializer();
    }

    @Test
    public void shouldDeserializeAndSerialize() {
        Position p1 = new Position(3, 4);
        Position p2 = new Position(5, 6);
        Piece piece = Piece.BISHOP;
        Color color = Color.WHITE;
        Optional<Piece> promotedTo = Optional.empty();

        Move move = new Move(p1, p2, piece, color, promotedTo);
        byte[] bytes = serializer.serialize(move);
        Move deserializedMove = serializer.deserialize(bytes);

        Assert.assertEquals(p1, deserializedMove.getFrom());
        Assert.assertEquals(p2, deserializedMove.getTo());
        Assert.assertEquals(piece, deserializedMove.getPiece());
        Assert.assertEquals(color, deserializedMove.getColor());
        Assert.assertEquals(promotedTo, deserializedMove.getPromotedTo());
    }
}
