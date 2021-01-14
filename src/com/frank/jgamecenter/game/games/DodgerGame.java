package com.frank.jgamecenter.game.games;

import com.frank.jgamecenter.game.Element;
import com.frank.jgamecenter.game.GraphicGame;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DodgerGame extends GraphicGame {
    private double width;
    private double height;

    private List<Obstacle> obstacleList;
    private Spacecraft spacecraft;

    private boolean toLeft = false;
    private boolean toRight = false;

    private boolean stop = false;

    private int totalLives;
    private int lives;

    private Image imgBackground;
    private Image imgSpacecraft;
    private Image imgHeart;
    private Image imgHeartEmpty;
    private Image imgObstacle;

    @Override
    public void init() {
        totalLives = 5;
        lives = totalLives;
        getGameNode().addEventHandler(KeyEvent.KEY_PRESSED, evt -> {
            KeyCode code = evt.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.A) {
                if (spacecraft.x - spacecraft.verticalSpeed > 0) {
                    toLeft = true;
                    toRight = false;
                }
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                if (spacecraft.x + spacecraft.width + spacecraft.verticalSpeed < width) {
                    toRight = true;
                    toLeft = false;
                }
            }
        });

        getGameNode().addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            switch (evt.getCode()) {
                case LEFT, A    -> toLeft = false;
                case RIGHT, D   -> toRight = false;
            }
        });

        width = g.getCanvas().getWidth();
        height = g.getCanvas().getHeight();

        obstacleList = new CopyOnWriteArrayList<>();

        spacecraft = new Spacecraft();

        imgBackground   = loadImage("/resources/images/dodger/space_pixel_background.png");
        imgHeart        = loadImage("/resources/images/dodger/heart.png", 32);
        imgHeartEmpty   = loadImage("/resources/images/dodger/heart_empty.png", 32);
        imgSpacecraft   = loadImage("/resources/images/dodger/spacecraft.png", 64);
        imgObstacle     = loadImage("/resources/images/dodger/asteroid.png", 64);
    }

    @Override
    public void restart() {
        init();
    }

    private void generateObstacle() {
        obstacleList.add(new Obstacle());
    }

    @Override
    public void run() {
        if (!stop) {
            if (toLeft) {
                spacecraft.x -= spacecraft.verticalSpeed;
            }
            if (toRight) {
                spacecraft.x += spacecraft.verticalSpeed;
            }
            evaluateCollision();
        }

        drawBackground();

        for (Obstacle obstacle : obstacleList) {
            drawObstacle(obstacle);
        }

        g.drawImage(this.imgSpacecraft, spacecraft.x, spacecraft.y, spacecraft.width, spacecraft.height);
        drawHearts();

        int probability = (int) (Math.random() * 100);
        if (probability >= 50 && probability <= 53) {
            generateObstacle();
        }
    }

    private void drawBackground() {
        g.drawImage(imgBackground, 0, 0, width, height);
    }

    private void drawObstacle(Obstacle obstacle) {
        double angle = obstacle.angle += obstacle.speed;
        drawRotatedImage(imgObstacle, angle, obstacle.x, obstacle.y, obstacle.width, obstacle.height);
    }

    private void drawRotatedImage(Image image, double deg, double x, double y, double width, double height) {
        g.save();
        Rotate r = new Rotate(deg, x + width / 2, y + height / 2);
        g.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        g.drawImage(image, x, y, width, height);
        g.restore();
    }

    private void drawHearts() {
        int heartX = 5;
        int heartY = 5;
        int size = 32;
        for (int i = 0; i < lives; i++) {
            g.drawImage(imgHeart, heartX, heartY, size, size);
            heartX += size + 5;
        }
        for (int i = 0; i < totalLives - lives; i++) {
            g.drawImage(imgHeartEmpty, heartX, heartY, size, size);
            heartX += size + 5;
        }
    }

    private void evaluateCollision() {
        for (Obstacle obstacle : obstacleList) {
            obstacle.y += obstacle.speed;
            evaluatePosition(obstacle);
            if (spacecraft.collides(obstacle) && !obstacle.alreadyCollided) {
                obstacle.alreadyCollided = true;
                lives--;
                if (lives == 0) {
                    stop = true;
                }
            }
        }
    }

    private void evaluatePosition(Obstacle obstacle) {
        if (obstacle.y > height) {
            obstacleList.remove(obstacle);
        }
    }

    private class Obstacle extends Element {
        public double speed;
        public boolean alreadyCollided;
        public double rotationSpeed;
        public double angle;
        public Obstacle() {
            double size = Math.random() * 25 + 25;
            this.x = Math.random() * DodgerGame.this.width;
            this.y = (Math.random() * -100) - size;
            this.width = size;
            this.height = size;
            this.speed = (Math.random() * 2) + 1;
            this.rotationSpeed = (Math.random() * 3);
            this.angle = 0;
        }
    }

    private class Spacecraft extends Element {
        public double verticalSpeed;
        public Spacecraft() {
            this.width = 64;
            this.height = 64;
            this.x = (int) (DodgerGame.this.width / 2 - this.width / 2);
            this.y = (int) (DodgerGame.this.height - this.height - 25);

            this.verticalSpeed = 6;
        }
    }

    public DodgerGame() {
        super("Dodger");

        g.getCanvas().setWidth(900);
        g.getCanvas().setHeight(640);
    }
}