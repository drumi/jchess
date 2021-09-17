package org.example.jchess;

import java.util.*;

public final class ChessGame implements Game {

    private final Player white;
    private final Player black;
    private final Engine engine;

    private List<List<Optional<OccupiedTile>>> board;
    private List<Move> movesHistory;

    public ChessGame(Player white, Player black, Engine engine) {
        this.white = Objects.requireNonNull(white);
        this.black = Objects.requireNonNull(black);
        this.engine = Objects.requireNonNull(engine);

        init();
    }

    private void init() {
        initMovesHistory();
        initBoard();
    }

    private void initMovesHistory() {
        movesHistory = new ArrayList<>();
    }

    private void initBoard() {
        board = Arrays.asList(
                constructEdgeRow(Color.BLACK),
                constructPawnRow(Color.BLACK),
                constructEmptyRow(),
                constructEmptyRow(),
                constructEmptyRow(),
                constructEmptyRow(),
                constructPawnRow(Color.WHITE),
                constructEdgeRow(Color.WHITE)
        );
    }

    private List<Optional<OccupiedTile>> constructEdgeRow(Color player) {
        var topRow = Arrays.asList(
                Optional.of(new OccupiedTile(Piece.ROOK, player)),
                Optional.of(new OccupiedTile(Piece.KNIGHT, player)),
                Optional.of(new OccupiedTile(Piece.BISHOP, player)),
                Optional.of(new OccupiedTile(Piece.QUEEN, player)),
                Optional.of(new OccupiedTile(Piece.KING, player)),
                Optional.of(new OccupiedTile(Piece.BISHOP, player)),
                Optional.of(new OccupiedTile(Piece.KNIGHT, player)),
                Optional.of(new OccupiedTile(Piece.ROOK, player))
        );

        return topRow;
    }

    private List<Optional<OccupiedTile>> constructPawnRow(Color player) {
        List<Optional<OccupiedTile>> pawnRow = new ArrayList<>();

        for (int columns = 0; columns < 8; columns++) {
            pawnRow.add(Optional.of(new OccupiedTile(Piece.PAWN, player)));
        }

        return pawnRow;
    }

    private List<Optional<OccupiedTile>> constructEmptyRow() {
        List<Optional<OccupiedTile>> emptyRow = new ArrayList<>();

        for (int columns = 0; columns < 8; columns++) {
            emptyRow.add(Optional.empty());
        }

        return emptyRow;
    }

    public void run() {
        while (true) {
            Move whiteMove = white.obtainNextMove();
            black.notifyForOtherPlayersMove(whiteMove);

            if (!isGameInProgress()) {
                break;
            }

            Move blackMove = black.obtainNextMove();
            white.notifyForOtherPlayersMove(blackMove);

            if (!isGameInProgress()) {
                break;
            }
        }
    }

    private boolean isGameInProgress() {
        Report report = engine.analyseBoard(new BoardSnapshot(board, movesHistory));
        GameState gameState = report.getGameState();

        return gameState != GameState.CHECKMATE &&
                gameState != GameState.STALEMATE &&
                gameState != GameState.DRAW;
    }

}
