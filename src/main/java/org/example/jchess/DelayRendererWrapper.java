package org.example.jchess;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DelayRendererWrapper implements Renderer {

    private final Renderer renderer;
    private final int delayMilliseconds;

    public DelayRendererWrapper(Renderer renderer, int delayMilliseconds) {
        this.renderer = Objects.requireNonNull(renderer);
        this.delayMilliseconds = delayMilliseconds;
    }

    @Override
    public void draw(BoardSnapshot snapshot) {
        try {
            TimeUnit.MILLISECONDS.sleep(delayMilliseconds);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        renderer.draw(snapshot);
    }
}
