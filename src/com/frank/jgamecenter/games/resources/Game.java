package com.frank.jgamecenter.games.resources;

import com.frank.jgamecenter.games.SnakeGame;
import javafx.scene.Node;
import javafx.scene.image.Image;
import java.io.InputStream;

public abstract class Game {
    private final String name;
    private final String description;
    private final Image thumbnail;

    protected Node gameNode;

    protected boolean isPaused;

    public Game(String name, String description, Image thumbnail) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.isPaused = false;
    }

    public static Image loadImage(String path) {
        InputStream inputStream = SnakeGame.class.getResourceAsStream(path);
        return new Image(inputStream, 0, 0, true, true);
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

    public Node getGameNode() {
        return gameNode;
    }

    protected static Image loadThumbnail(String name) {
        return new Image(Game.class.getResourceAsStream("/resources/images/thumbnail/" + name), 160, 160, true, true);
    }

    protected abstract void init();
    public abstract void start();
    public abstract void restart();
    public abstract void stop();

    public void setPause(boolean pause) {
        this.isPaused = pause;
    }
}