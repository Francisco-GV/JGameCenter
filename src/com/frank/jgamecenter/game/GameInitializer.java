package com.frank.jgamecenter.game;

import javafx.scene.image.Image;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class GameInitializer {
    private final Class<? extends Game> gameClass;
    private final Map<String, Object> gameData;

    public GameInitializer(Class<? extends Game> game, Map<String, Object> gameData) {
        this.gameClass = game;
        this.gameData = gameData;
    }

    public Game createInstance() {
        try {
            return gameClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getData(String key, Object defaultValue) {
        return gameData.getOrDefault(key, defaultValue);
    }

    public static Image loadThumbnail(String name) {
        return new Image(Game.class.getResourceAsStream("/resources/images/thumbnail/" + name), 160, 160, true, true);
    }
}