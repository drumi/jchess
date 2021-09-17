package org.example.jchess;

import java.util.List;
import java.util.Optional;

public interface CheckChecker {

    boolean isUnderCheck(List<List<Optional<OccupiedTile>>> board, Color defender);
}
