package org.example.jchess;

public final class ConsoleRenderer implements Renderer {

    @Override
    public void draw(BoardSnapshot snapshot) {

        var tiles = snapshot.getTiles();

        for (int row = 0; row < 8; row++) {
            System.out.print("|");

            for (int col = 0; col < 8; col++) {
                tiles.get(row).get(col)
                     .ifPresentOrElse(t -> drawTile(t),
                                      ()-> drawEmptyTile());

                System.out.print("|");
            }

            System.out.print("\n");
        }

        System.out.print("\n\n\n");
    }

    private void drawTile(OccupiedTile tile) {
        String c = colorToString(tile.getPlayerColor());
        String p = pieceToString(tile.getPiece());
        System.out.print(c + p);
    }

    private void drawEmptyTile() {
        System.out.print("   ");
    }

    private String colorToString(Color color) {
        return color == Color.WHITE ? " w" : " b";
    }

    private String pieceToString(Piece piece) {
        switch (piece) {
            case BISHOP:
                return "B";
            case KING:
                return "K";
            case KNIGHT:
                return "N";
            case PAWN:
                return "P";
            case QUEEN:
                return "Q";
            case ROOK:
                return "R";
            default:
                throw new IllegalStateException("Unexpected value: " + piece);
        }
    }
}
