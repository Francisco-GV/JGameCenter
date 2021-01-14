package com.frank.jgamecenter.game.games;

import com.frank.jgamecenter.game.Game;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryGame extends Game {
    private Card cardSelected;
    private final FlowPane cardsContainer;

    private static final File[] imageFiles;
    private List<Integer> usedIndex;

    static {
        URL url = MemoryGame.class.getResource("/resources/images/memory/retro-wave");
        File directory = new File(url.getPath());

        imageFiles = (directory.exists() && directory.isDirectory())
                ? directory.listFiles((dir, name) -> name.toLowerCase().endsWith("png"))
                : null;
    }

    @Override
    protected void init() {
        BackgroundFill fill = new BackgroundFill(Color.rgb(120, 90, 65), null, null);

        cardsContainer.setBackground(new Background(fill));

        int numberOfPairs = 6;
        usedIndex = new ArrayList<>();
        List<Card> cards = new ArrayList<>();

        try {
            for (int i = 1; i <= numberOfPairs; i++) {
                Card card = loadCard();
                Card pair = loadCard(card.image);
                card.setPair(pair);
                pair.setPair(card);

                cards.add(card);
                cards.add(pair);
            }
            Collections.shuffle(cards);

            for (Card card : cards) {
                cardsContainer.getChildren().add(card.root);
            }
        } catch (IOException ex) {
            ex.getStackTrace();
        }
    }

    private Image loadRandomImage(File directory) {
        return new Image(directory.toURI().toString(), 64, 64, true, true);
    }

    @Override
    public void start() {
        init();
    }

    @Override
    public void restart() {
        start();
    }

    @Override
    public void stop() {

    }

    public MemoryGame() {
        super("Memory");

        this.gameNode = new FlowPane();
        cardsContainer = (FlowPane) this.gameNode;
        cardsContainer.setPrefSize(850, 600);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setHgap(15);
        cardsContainer.setVgap(15);


    }

    public Card loadCard() throws IOException {
        int index;
        do {
            index = (int) (Math.random() * imageFiles.length);
        } while (usedIndex.contains(index));
        usedIndex.add(index);
        Image image = loadRandomImage(imageFiles[index]);
        return loadCard(image);
    }

    public Card loadCard(Image image) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/games/memory/card.fxml"));
        Card cardElement = new Card(image);
        loader.setController(cardElement);
        Parent parent = loader.load();

        parent.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<>() {
            private final Card card = cardElement;
            @Override
            public void handle(MouseEvent event) {
                if (cardSelected == null) {
                    cardSelected = card;
                } else {
                    if (cardSelected != card) {
                        TimerTask task;
                        if (card == cardSelected.pair) {
                            task = new TimerTask() {
                                @Override
                                public void run() {
                                    card.discovered = true;
                                    cardSelected.discovered = true;
                                    cardSelected = null;
                                }
                            };
                        } else {
                            task = new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> {
                                        cardSelected.rotate(cardSelected.getRoot().getRotate(), 0);
                                        card.rotate(card.getRoot().getRotate(), 0);
                                        cardSelected = null;
                                    });
                                }
                            };
                        }
                        new Timer(true).schedule(task, 1000);
                    }
                }
            }
        });

        return cardElement;
    }

    @SuppressWarnings("unused")
    private static class Card {
        @FXML private Pane root;
        @FXML private Label lblCenterImage;

        private Card pair;
        private final Image image;

        private ScaleTransition scaleTransition;
        private RotateTransition rotateTransition;

        private boolean discovered;

        public Card(Image image) {
            this.image = image;
        }

        public void setPair(Card pair) {
            this.pair = pair;
        }

        @FXML private void initialize() {
            root.setRotationAxis(new Point3D(0, 1, 0));
            root.rotateProperty().addListener((observable, oldValue, newValue) -> {
                double angle = newValue.doubleValue();
                if (angle >= 0 && angle <= 90) {
                    root.getStyleClass().removeAll("front");
                    root.getStyleClass().add("back");
                    lblCenterImage.setGraphic(null);
                } else {
                    root.getStyleClass().removeAll("back");
                    root.getStyleClass().add("front");
                    ImageView imageView = new ImageView(image);
                    imageView.setRotationAxis(new Point3D(0, 1, 0));
                    imageView.setRotate(180);
                    lblCenterImage.setGraphic(imageView);
                }
            });

            scaleTransition = new ScaleTransition(Duration.millis(100), root);
            rotateTransition = new RotateTransition(Duration.millis(500), root);

            rotateTransition.setAxis(root.getRotationAxis());

            root.setOnMouseEntered(e -> scale(root.getScaleX(), root.getScaleY(), 1.06f, 1.06f));
            root.setOnMouseExited(e -> scale(root.getScaleX(), root.getScaleY(), 1f, 1f));

            root.setOnMouseClicked(e -> {
                if (!discovered) {
                    rotate(root.getRotate(), 180);
                }
            });
//            root.setOnMouseReleased(e -> rotate(root.getRotate(), 0));
        }

        public void scale(double fromX, double fromY, double toX, double toY) {
            scaleTransition.stop();
            scaleTransition.setFromX(fromX);
            scaleTransition.setFromY(fromY);
            scaleTransition.setToX(toX);
            scaleTransition.setToY(toY);
            scaleTransition.play();
        }

        public void rotate(double from, double to) {
            rotate(from, to, 0);
        }

        public void rotate(double from, double to, long delay) {
            rotateTransition.stop();
            rotateTransition.setDelay(Duration.millis(delay));
            rotateTransition.setFromAngle(from);
            rotateTransition.setToAngle(to);
            rotateTransition.play();
        }

        public Card getPair() {
            return pair;
        }

        public Pane getRoot() {
            return root;
        }
    }
}