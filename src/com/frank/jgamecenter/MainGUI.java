package com.frank.jgamecenter;

import com.frank.jgamecenter.game.Game;

import com.frank.jgamecenter.game.GameInitializer;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class MainGUI {
    @FXML private Parent topTitleBox;
    @FXML private AnchorPane mainMenuPane;
    @FXML private FlowPane gamesListContainer;

    private StackPane gameContainer;
    private Stage primaryStage;
    private Scene mainMenuScene;
    private Node inGameMenu;

    @FXML private void initialize() throws IOException {
        for (GameInitializer initializer : JGameCenter.getInstance().getGameList()) {
            gamesListContainer.getChildren().add(createGameElement(initializer));
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.sceneProperty().addListener(
                (obs, old, newScene) -> primaryStage.setResizable(newScene.getRoot() == mainMenuPane)
        );
    }

    public void setPrimaryScene(Scene scene) {
        this.mainMenuScene = scene;
    }

    public void initializeEffects() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1500), topTitleBox);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWING, evt -> fadeTransition.play());
    }

    private Region createGameElement(GameInitializer initializer) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/game_element.fxml"));
        loader.setController(new GameElement(initializer));
        return loader.load();
    }

    private void initInGameMenu(Game game) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/in_game_menu.fxml"));
        loader.setController(new InGameMenu(game));
        inGameMenu = loader.load();
    }

    private void changeToGame(Game game) throws IOException {
        gameContainer = new StackPane();

        gameContainer.setAlignment(Pos.CENTER);

        Node gameNode = game.getGameNode();

        gameNode.setFocusTraversable(true);
        gameNode.requestFocus();

        initInGameMenu(game);
        inGameMenu.setDisable(true);
        inGameMenu.setVisible(false);

        gameContainer.getChildren().add(0, gameNode);
        gameContainer.getChildren().add(1, inGameMenu);

        gameNode.setOnKeyReleased(evt -> {
            if (evt.getCode() == KeyCode.ESCAPE || evt.getCode() == KeyCode.P) {
                game.setPause(true);
                Platform.runLater(() -> {
                    inGameMenu.setDisable(false);
                    inGameMenu.setVisible(true);
                    inGameMenu.requestFocus();
                });
            } else if (evt.getCode() == KeyCode.F12) {
                takeSnapshot(gameNode);
            }
        });

        Platform.runLater(() -> {
            primaryStage.setScene(new Scene(gameContainer));
            primaryStage.centerOnScreen();
        });
    }

    private void changeToMenu() {
        Platform.runLater(() -> {
            primaryStage.setTitle(JGameCenter.APP_TITLE);
            primaryStage.setScene(mainMenuScene);
            primaryStage.centerOnScreen();
            gameContainer = null;
        });
    }

    private void takeSnapshot(Node gameNode) {
        Platform.runLater(() -> {
            WritableImage image = gameNode.snapshot(new SnapshotParameters(), null);
            new Thread(() -> {
                File directory = new File(System.getProperty("user.home").concat("\\Documents").concat("\\JGameCenter screenshots"));
                boolean directoryExists = true;
                if (!directory.exists()) {
                    directoryExists = directory.mkdirs();
                }
                if (directoryExists) {
                    File output = new File(directory.getAbsolutePath() + "\\snapshot" + new Date().getTime() + ".png");
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
                        System.out.println("A screenshot was taken");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Screenshots directory do not exists and could not be created");
                }
            }).start();
        });
    }

    @SuppressWarnings("unused")
    public class GameElement {
        @FXML private Label lblTitle;
        @FXML private Pane root;
        @FXML private Label lblAbout;

        private final GameInitializer initializer;

        public GameElement(GameInitializer initializer) {
            this.initializer = initializer;
        }

        @FXML private void initialize() {
            root.setOnMouseReleased(evt -> {
                try {
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            lblTitle.setText((String) initializer.getData("title", "Unknown title"));
            Tooltip tooltip = new Tooltip((String) initializer.getData("description", "Without description"));
            tooltip.setShowDelay(Duration.millis(10));
            tooltip.setShowDuration(Duration.INDEFINITE);
            lblAbout.setTooltip(tooltip);

            Image thumbnail = (Image) initializer.getData("thumbnail", null);
            if (thumbnail != null) {
                createThumbnail(root, thumbnail);
            } else {
                createThumbnail(root);
            }
        }

        @FXML private void start() throws IOException {
            Game newGame = initializer.createInstance();

            if (newGame != null) {
                changeToGame(newGame);
                primaryStage.setTitle(JGameCenter.APP_TITLE + " - " + initializer.getData("title", "unknown"));
                newGame.start();
            }
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

    @SuppressWarnings("unused")
    public class InGameMenu {
        @FXML private Button btnResume;
        @FXML private Button btnRestart;
        @FXML private Button btnExit;

        private final Game game;
        public InGameMenu(Game game) {
            this.game = game;
        }

        @FXML private void initialize() {
            btnResume.setOnAction(e -> resume());
            btnRestart.setOnAction(e -> game.restart());
            btnExit.setOnAction(e -> backToMenu());
        }

        private void resume() {
            game.setPause(false);
            inGameMenu.setDisable(true);
            inGameMenu.setVisible(false);

            Node gameNode = gameContainer.getChildren().get(0);
            gameNode.requestFocus();
        }

        private void backToMenu() {
            game.stop();
            changeToMenu();
        }
    }
}