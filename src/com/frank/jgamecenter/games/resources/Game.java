package com.frank.jgamecenter.games.resources;

import com.frank.jgamecenter.games.SnakeGame;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public abstract class Game {
    private final String name;
    private final String description;
    private final Image thumbnail;

    private final Canvas canvas;
    protected final GraphicsContext g;
    public Game(String name, String description, Image thumbnail) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;

        canvas = new Canvas(775, 450);
        g = canvas.getGraphicsContext2D();

        getCanvas().setOnKeyReleased(evt -> {
            if (evt.getCode() == KeyCode.R) {
                init();
            } else if (evt.getCode() == KeyCode.F5) {
                WritableImage image = getCanvas().snapshot(new SnapshotParameters(), null);

                File output = new File("snapshot" + new Date().getTime() + ".png");
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Image loadImage(String path) {
        InputStream inputStream = SnakeGame.class.getResourceAsStream(path);
        return new Image(inputStream);
    }

    public static Image loadImage(String path, int size) {
        InputStream inputStream = SnakeGame.class.getResourceAsStream(path);
        return new Image(inputStream, size, size, true, true);
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
        canvas.requestFocus();
        canvas.setFocusTraversable(true);
        init();
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                refresh();
                run();
            }
        };

        animationTimer.start();
    }

    protected void refresh() {
        g.clearRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());
    }

    protected static Image loadThumbnail(String name) {
        return new Image(Game.class.getResourceAsStream("/resources/images/thumbnail/" + name), 160, 160, true, true);
    }

    protected abstract void init();
    protected abstract void run();
}
