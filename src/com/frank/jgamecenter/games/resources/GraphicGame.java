package com.frank.jgamecenter.games.resources;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class GraphicGame extends Game {

    protected final Canvas canvas;
    protected final GraphicsContext g;
    private final AnimationTimer animationTimer;

    public GraphicGame(String name, String description, Image thumbnail) {
        super(name, description, thumbnail);

        this.gameNode = new Canvas(640, 640);
        canvas = (Canvas) gameNode;
        g = canvas.getGraphicsContext2D();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused) {
                    refresh();
                    run();
                }
            }
        };

    }
    @Override protected abstract void init();
    protected abstract void run();

    @Override public void start() {
        gameNode.requestFocus();
        gameNode.setFocusTraversable(true);
        setPause(false);
        init();
        animationTimer.start();
    }

    @Override public void stop() {
        animationTimer.stop();
        restart();
    }


    protected void refresh() {
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}