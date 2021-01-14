package com.frank.jgamecenter;

import com.frank.jgamecenter.game.GameInitializer;
import com.frank.jgamecenter.game.games.*;
import com.frank.jgamecenter.game.Game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JGameCenter extends Application {
    public static final String APP_TITLE = "JGameCenter";
    private List<GameInitializer> gameList;

    @Override
    public void init() {
        Font.loadFont(getClass().getResourceAsStream("/resources/font/arcade_ya/ARCADE_N.TTF"), 30);
        instance = this;
        gameList = new ArrayList<>();
        addNewGame(SnakeGame.class, createData("Snake", """
            The player controls a snake that
            moves around the board. The snake
            must try to eat apples that 
            randomly appear.""",
            GameInitializer.loadThumbnail("snake.png")));

        addNewGame(DodgerGame.class, createData("Dodger", """
            Several asteroids fall from the 
            top of the screen, and the user 
            must avoid them.""",
            GameInitializer.loadThumbnail("dodger.png")));

        addNewGame(MemoryGame.class, createData("Memory","""
            A board full of overturned cards.
            There is a pair for each card.
            The player needs to overturn all
            the cards.""",
            null));

    }

    private Map<String, Object> createData(String title, String description, Image thumbnail) {
        Map<String, Object> map = new HashMap<>();

        map.put("title", title);
        map.put("description", description);
        map.put("thumbnail", thumbnail);

        return map;
    }

    public void addNewGame(Class<? extends Game> gameClass, Map<String, Object> gameData) {
        gameList.add(new GameInitializer(gameClass, gameData));
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/main_gui.fxml"));
        Parent parent = loader.load();
        MainGUI controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        controller.initializeEffects();

        Scene scene = new Scene(parent);

        controller.setPrimaryScene(scene);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/resources/images/game-controller-24x24.png")),
                new Image(getClass().getResourceAsStream("/resources/images/game-controller-32x32.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public List<GameInitializer> getGameList() {
        return gameList;
    }

    private static JGameCenter instance;
    public static JGameCenter getInstance() {
        return instance;
    }
}