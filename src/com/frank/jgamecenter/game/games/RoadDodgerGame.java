package com.frank.jgamecenter.game.games;

import com.frank.jgamecenter.game.Element;
import com.frank.jgamecenter.game.GraphicGame;
import com.frank.jgamecenter.utilities.Util;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoadDodgerGame extends GraphicGame {
    private UserCar userCar;
    private boolean toLeft = false;
    private boolean toRight = false;

    private List<ObstacleCar> obstacleCarList;
    private final double roadWidth = 140.0;
    private final double roadHeight = 132.0;
    private final int roadColumns = 4;
    private final int roadRows = 6;

    private Element[][] roadElements;
    private Image imgRoad;

    private Image imgCar;
    private Image imgUserCar;

    private final double borderWidth;
    private final double highwayStartX;
    private final double highwayEndX;

    public RoadDodgerGame() {
        super("Road Dodger");

        borderWidth = 60;
        canvas.setWidth(borderWidth + (roadWidth * roadColumns) + borderWidth);
        canvas.setHeight(roadHeight * (roadRows - 1));

        highwayStartX = borderWidth;
        highwayEndX = canvas.getWidth() - borderWidth;
    }

    @Override
    protected void init() {
        imgRoad = loadImage("/resources/images/roaddodger/road.png", roadWidth, roadHeight);
        imgCar = loadImage("/resources/images/roaddodger/car.png", 60, 112);
        imgUserCar = loadImage("/resources/images/roaddodger/car_user.png", 60, 112);

        obstacleCarList = new CopyOnWriteArrayList<>();
        double firstY = -roadHeight;

        roadElements = new Element[roadColumns][roadRows];
        for (int i = 0; i < roadColumns; i++) {
            for (int n = 0; n < roadElements[i].length; n++) {
                roadElements[i][n] = new Element(highwayStartX + roadWidth * i, firstY + roadHeight * n, roadWidth, roadHeight);
            }
        }

        userCar = new UserCar();
        int x = (int) (canvas.getWidth() / 2 - userCar.width / 2);
        int y = (int) (canvas.getHeight() - userCar.height - 50);

        userCar.x = x;
        userCar.y = y;

        canvas.addEventHandler(KeyEvent.KEY_PRESSED, evt -> {
            KeyCode code = evt.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.A) {
                if (userCar.x - userCar.verticalSpeed > highwayStartX) {
                    toLeft = true;
                    toRight = false;
                } else {
                    toLeft = false;
                }
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                if (userCar.x + userCar.width + userCar.verticalSpeed < highwayEndX) {
                    toRight = true;
                    toLeft = false;
                } else {
                    toRight = false;
                }
            }
        });

        canvas.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            switch (evt.getCode()) {
                case LEFT, A    -> toLeft = false;
                case RIGHT, D   -> toRight = false;
            }
        });

    }

    @Override
    protected void run() {
        if (toLeft) {
            userCar.moveToLeft();
        }
        if (toRight) {
            userCar.moveToRight();
        }

        moveBackground();
        drawBackground();

        for (ObstacleCar obstacleCar : obstacleCarList) {
            obstacleCar.move();
            if (obstacleCar.y >= canvas.getHeight()) {
                obstacleCarList.remove(obstacleCar);
                continue;
            }
            g.setFill(obstacleCar.color);
            g.fillPolygon(obstacleCar.getCurrentShapeX(), obstacleCar.getCurrentShapeY(), obstacleCar.getShapeX().length);
            g.drawImage(imgCar, obstacleCar.x, obstacleCar.y, obstacleCar.width, obstacleCar.height);
        }

        g.setFill(userCar.color);
        g.fillPolygon(userCar.getCurrentShapeX(), userCar.getCurrentShapeY(), userCar.getShapeX().length);
        g.drawImage(imgUserCar, userCar.x, userCar.y, userCar.width, userCar.height);

        if (Util.probableEventOcurred(5.0)) {
            generateCar();
        }
    }

    private void moveBackground() {
        for (Element[] roadElement : roadElements) {
            for (Element e : roadElement) {
                double roadSpeed = 8.0;
                e.y += roadSpeed;

                if (e.y >= canvas.getHeight()) {
                    e.y = -roadHeight;
                }
            }
        }
    }

    private void generateCar() {
        int column = Util.getRandomIntBetween(0, roadColumns * 2);
        double columnWidth = roadWidth / 2;
        double centerX = columnWidth / 2 - imgCar.getWidth() / 2;
        double posX = highwayStartX + centerX + (column * columnWidth);
        double posY = 0 - imgCar.getHeight() * 2;

        ObstacleCar newObstacleCar = new ObstacleCar(posX, posY);

        boolean wait = false;
        for (ObstacleCar obstacleCar : obstacleCarList) {
            if (newObstacleCar.collides(obstacleCar)) {
                wait = true;
                break;
            }
        }

        if (!wait) obstacleCarList.add(newObstacleCar);
    }

    private void drawBackground() {
        g.setFill(Color.rgb(78, 78, 78));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Element[] roadElement : roadElements) {
            for (Element e : roadElement) {
                g.drawImage(imgRoad, e.x, e.y, e.width, e.height);
            }
        }

        g.setFill(Color.rgb(132, 192, 17));
        g.fillRect(0, 0, borderWidth, canvas.getHeight());
        g.fillRect(highwayEndX, 0, borderWidth, canvas.getHeight());
    }

    private static class Car extends Element {
        protected double[][] shapePosition;

        public double[] getShapeX() {
            return shapePosition[0];
        }

        public double[] getCurrentShapeX() {
            double[] copy = Arrays.copyOf(shapePosition[0], shapePosition[0].length);

            for (int i = 0; i < copy.length; i++) {
                copy[i] = copy[i] += this.x;
            }

            return copy;
        }

        public double[] getCurrentShapeY() {
            double[] copy = Arrays.copyOf(shapePosition[1], shapePosition[1].length);

            for (int i = 0; i < copy.length; i++) {
                copy[i] = copy[i] += this.y;
            }

            return copy;
        }
    }

    private static class ObstacleCar extends Car {
        private double speed;
        private final Color color;

        public ObstacleCar(double x, double y) {
            shapePosition = new double[][] {
                    {30, 42, 48, 54, 54, 58, 58, 54, 54, 44, 30, 16, 6, 6, 2, 2, 6, 6, 12, 18, 30},
                    {2, 2, 4, 10, 38, 40, 44, 46, 106, 108, 108, 108, 106, 46, 44, 40, 40, 10, 4, 2, 2}
            };

            color = new Color(Math.random(), Math.random(), Math.random(), 1);

            speed = (Math.random() + 1) * 5;
            speed = 5.0;

            this.x = x;
            this.y = y;
            this.width = 60;
            this.height = 112;
        }

        public void move() {
            y += speed;
        }
    }

    private class UserCar extends Car {
        private final Color color;
        public double verticalSpeed;

        public UserCar() {
            shapePosition = new double[][] {
                    {30, 40, 44, 52, 54, 54, 52, 52, 54, 54, 44, 30, 16, 6, 6, 8, 8, 6, 6, 12, 16, 20, 30},
                    {2, 2, 4, 12, 16, 34, 34, 86, 86, 106, 108, 108, 108, 106, 86, 86, 34, 34, 16, 12, 4, 2, 2}
            };

            color = Color.rgb(125, 30, 30);
            this.width = 60;
            this.height = 112;

            verticalSpeed = 6;
        }

        public void moveToLeft() {
            if (this.x - this.verticalSpeed > highwayStartX) {
                this.x -= this.verticalSpeed;
            }
        }
        public void moveToRight() {
            if (this.x + this.width + this.verticalSpeed < highwayEndX) {
                this.x += this.verticalSpeed;
            }
        }
    }
}