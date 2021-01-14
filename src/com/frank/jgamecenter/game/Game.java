package com.frank.jgamecenter.game;

import com.frank.jgamecenter.game.games.SnakeGame;
import javafx.scene.Node;
import javafx.scene.image.Image;
import java.io.InputStream;

public abstract class Game {
    private final String name;

    protected Node gameNode;

    protected boolean isPaused;

    public Game(String name) {
        this.name = name;
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

    public Node getGameNode() {
        return gameNode;
    }

    protected abstract void init();
    public abstract void start();
    public abstract void restart();
    public abstract void stop();

    public void setPause(boolean pause) {
        this.isPaused = pause;
    }
}