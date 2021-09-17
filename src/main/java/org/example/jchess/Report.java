package org.example.jchess;

import java.util.Objects;
import java.util.Optional;

public class Report {

    private Color playersTurn;
    private GameState gameState;
    private Optional<Color> winner;

    public Report(Color playersTurn, GameState gameState, Optional<Color> winner) {
        this.playersTurn = Objects.requireNonNull(playersTurn);
        this.gameState = Objects.requireNonNull(gameState);
        this.winner = Objects.requireNonNull(winner);
    }

    public Color getPlayersTurn() {
        return playersTurn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Optional<Color> getWinner() {
        return winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        if (playersTurn != report.playersTurn) return false;
        if (gameState != report.gameState) return false;
        return winner.equals(report.winner);
    }

    @Override
    public int hashCode() {
        int result = playersTurn.hashCode();
        result = 31 * result + gameState.hashCode();
        result = 31 * result + winner.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Report{" +
                "playersTurn=" + playersTurn +
                ", gameState=" + gameState +
                ", winner=" + winner +
                '}';
    }
}
