package com.frank.jgamecenter;

import com.frank.jgamecenter.games.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;

public class MainGUI {
    @FXML private AnchorPane menuPane;
    @FXML private Canvas currentCanvas;
    @FXML private FlowPane gamesContainer;

    private Stage primaryStage;

    @FXML private void initialize() throws IOException {
        for (Game game : JGameCenter.getInstance().getGameList()) {
            gamesContainer.getChildren().add(createGameElement(game));
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private Region createGameElement(Game game) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/game_element.fxml"));
        loader.setController(new GameElement(game));
        return loader.load();
    }

    private void changeToCanvas(Canvas canvas) {
        primaryStage.setScene(new Scene(new AnchorPane(canvas)));
    }

    private void changeToMenu() {
        primaryStage.setScene(new Scene(menuPane));
    }

    public class GameElement {
        @FXML private Label lblTitle;
        @FXML private Pane root;
        @FXML private Label lblAbout;

        private final Game game;

        public GameElement(Game game) {
            this.game = game;
        }

        @FXML private void initialize() {
            root.setOnMouseReleased(evt -> start());

            lblTitle.setText(game.getName());
            Tooltip tooltip = new Tooltip(game.getDescription());
            tooltip.setShowDelay(Duration.millis(10));
            lblAbout.setTooltip(tooltip);

            if (game.getThumbnail() != null) {
                createThumbnail(root, game.getThumbnail());
            } else {
                createThumbnail(root);
            }
        }

        @FXML private void start() {
            changeToCanvas(game.getCanvas());
            game.start();
        }

        private void createThumbnail(Region root, Image image) {
            BackgroundFill color        = new BackgroundFill(Color.rgb(56, 56, 56), null, null);
            BackgroundFill colorHover   = new BackgroundFill(Color.rgb(28, 28, 28), null, null);
            BackgroundFill colorPressed = new BackgroundFill(Color.rgb(14, 14, 14), null, null);

            BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

            root.setBackground(new Background(new BackgroundFill[] {color},
                    new BackgroundImage[] {backgroundImage}));

            root.pressedProperty().addListener((obs, old, newValue) -> {
                BackgroundFill fillPressed;
                if      (newValue)          fillPressed = colorPressed;
                else if (root.isHover())    fillPressed = colorHover;
                else                        fillPressed = color;
                Background background = new Background(
                        new BackgroundFill[] {fillPressed},
                        new BackgroundImage[] {backgroundImage});
                root.setBackground(background);
            });
            root.hoverProperty().addListener((obs, old, newValue) -> {
                BackgroundFill fillHover;
                if      (newValue)  fillHover = colorHover;
                else                fillHover = color;
                Background background = new Background(
                        new BackgroundFill[] {fillHover},
                        new BackgroundImage[] {backgroundImage});
                root.setBackground(background);
            });
        }

        private void createThumbnail(Region root) {
            InputStream inputStream = MainGUI.GameElement.class.getResourceAsStream("/resources/images/game-controller-128x128.png");
            Image image = new Image(inputStream, 64, 64, true, true);
            createThumbnail(root, image);
        }
    }
}
