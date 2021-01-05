package com.frank.jgamecenter.games;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Game {
    private String name;
    private String description;
    private Image thumbnail;

    private final Canvas canvas;
    protected final GraphicsContext g;
    public Game(String name, String description, Image thumbnail) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;

        canvas = new Canvas();
        g = canvas.getGraphicsContext2D();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void start() {
        init();
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                run();
            }
        };
    }

    public abstract void init();
    public abstract void run();
}
