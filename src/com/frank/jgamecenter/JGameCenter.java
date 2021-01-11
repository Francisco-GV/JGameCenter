package com.frank.jgamecenter;

import com.frank.jgamecenter.games.*;
import com.frank.jgamecenter.games.resources.Game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JGameCenter extends Application {
    public static final String APP_TITLE = "JGameCenter";
    private List<Game> gameList;

    @Override
    public void init() {
        Font.loadFont(getClass().getResourceAsStream("/resources/font/arcade_ya/ARCADE_N.TTF"), 30);

        instance = this;
        gameList = new ArrayList<>();
        gameList.add(new DodgerGame());
        //gameList.add(new MemoryGame()); next game
        gameList.add(new SnakeGame());
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/main_gui.fxml"));
        Parent parent = loader.load();
        MainGUI controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

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

    public List<Game> getGameList() {
        return gameList;
    }

    private static JGameCenter instance;
    public static JGameCenter getInstance() {
        return instance;
    }
}