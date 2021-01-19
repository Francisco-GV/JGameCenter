package com.frank.jgamecenter.game;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

public abstract class GraphicGame extends Game {

    protected final Canvas canvas;
    protected final GraphicsContext g;
    private final AnimationTimer animationTimer;

    public GraphicGame(String name) {
        super(name);

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

    @Override
    public void stop() {
        animationTimer.stop();
    }

    protected void refresh() {
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    protected void drawRotatedImage(Image image, double x, double y, double width, double height, int deg) {
        g.save();
        Rotate r = new Rotate(deg, x + width / 2, y + height / 2);
        g.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        g.drawImage(image, x, y, width, height);
        g.restore();
    }
}