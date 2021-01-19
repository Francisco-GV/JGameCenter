package com.frank.jgamecenter.game.games;

import com.frank.jgamecenter.game.Element;
import com.frank.jgamecenter.game.GraphicGame;
import com.frank.jgamecenter.utilities.Util;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlappyBirdGame extends GraphicGame {
    public FlappyBirdGame() {
        super("Flappy Bird");
        canvas.setWidth(400d);
        canvas.setHeight(600d);
    }

    private List<Pipe> pipePairList;

    private double width;
    private double height;

    private double pipeSpeed;

    private Bird bird;
    private boolean dead;

    private int birdImagesIndex;

    private Image[] imgsBird;
    private Image imgPipe;
    private Image imgPipeTop;
    private Image imgBg;
    private Image imgGrass;
    private List<Element> grassElementArray;

    private final double grassWidth = 164;
    private final double grassHeight = 24;

    private final double BOTTOM_LIMIT = 60d;

    @Override
    public void init() {
        pipeSpeed = 2.5;
        dead = false;
        birdImagesIndex = 0;
        imgsBird = new Image[4];
        grassElementArray = new CopyOnWriteArrayList<>();

        imgsBird[0] = loadImage("/resources/images/flappybird/bird_1.png", 46, 32);
        imgsBird[1] = loadImage("/resources/images/flappybird/bird_2.png", 46, 32);
        imgsBird[2] = loadImage("/resources/images/flappybird/bird_3.png", 46, 32);
        imgsBird[3] = imgsBird[1];
        imgPipe     = loadImage("/resources/images/flappybird/Pipe_135x135.png", 135, 135);
        imgPipeTop  = loadImage("/resources/images/flappybird/Pipe_Top_135x50.png", 135, 50);
        imgBg       = loadImage("/resources/images/flappybird/background_bottom.png", 400, 70);
        imgGrass    = loadImage("/resources/images/flappybird/grass.png", 336, 49);

        pipePairList = new CopyOnWriteArrayList<>();

        width = g.getCanvas().getWidth();
        height = g.getCanvas().getHeight();

        bird = new Bird();
        bird.y = height / 2 - bird.height / 2;

        canvas.addEventHandler(KeyEvent.KEY_PRESSED, evt -> {
            if (evt.getCode() == KeyCode.SPACE) {
                if (!dead) {
                    bird.fly();
                }
            }
        });
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
            if (evt.getButton() == MouseButton.PRIMARY) {
                if (!dead) {
                    bird.fly();
                }
            }
        });

        generateObstacle();

        double grassY = height - BOTTOM_LIMIT;

        for (int i = 0; i < 5; i++) {
            Element grassElement = new Element(grassWidth * i, grassY, grassWidth, grassHeight);
            grassElementArray.add(grassElement);
        }

        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!dead) {
                    birdImagesIndex = (birdImagesIndex + 1 > imgsBird.length - 1) ? 0 : birdImagesIndex + 1;
                } else {
                    birdImagesIndex = 1;
                }
            }
        }, 0, 75);
    }

    @Override
    public void run() {
        evaluateCollision();

        evaluateObstacles();

        g.setFill(Color.SKYBLUE);
        g.fillRect(0, 0, width, height);
        g.drawImage(imgBg, 0, height - imgBg.getHeight() - BOTTOM_LIMIT, width, imgBg.getHeight());

        g.drawImage(imgsBird[birdImagesIndex], bird.x, bird.y, bird.width, bird.height);
        //drawRotatedImage(imgsBird[birdImagesIndex], bird.x, bird.y, bird.width, bird.height, -30);

        for (Pipe pipe : pipePairList) {
            Pipe pair = pipe.pair;
            if (!dead) {
                pipe.move();
            }

            g.drawImage(imgPipe, pipe.x, pipe.y, pipe.width, pipe.height);
            double topY = pipe.y + pipe.height - 30;
            drawRotatedImage(imgPipeTop, pipe.x, topY, pipe.width, 30, 180);

            g.drawImage(imgPipe, pair.x, pair.y, pair.width, pair.height);
            double pairTopY = pair.y;
            g.drawImage(imgPipeTop, pair.x, pairTopY, pipe.width, 30);
        }

        g.setFill(Color.rgb(223, 216, 144));
        g.fillRect(0, height - BOTTOM_LIMIT, width, BOTTOM_LIMIT);

        drawGrass();

        bird.fall();
    }

    private void drawGrass() {
        for (Element grass : grassElementArray) {
            Element last = grassElementArray.get(grassElementArray.size() - 1);
            if (grass.x + grass.width < 0) {
                Element newGrass = new Element(last.x + pipeSpeed + last.width, last.y, grassWidth, grassHeight);
                grassElementArray.add(newGrass);

                grassElementArray.remove(grass);
            }
            if (!dead) {
                grass.x -= pipeSpeed;
            }
            /* FIXME Idk why is a little space between the images,
                but can "fix" it temporally drawing every image a bit bigger */
            g.drawImage(imgGrass, grass.x, grass.y, grassWidth + 4.7, grassHeight);
        }
    }

    private void evaluateCollision() {
        for (Pipe pipe : pipePairList) {
            if (bird.collides(pipe) || bird.collides(pipe.pair)
                    || bird.y + bird.height + bird.velocity >= height - BOTTOM_LIMIT) {
                dead = true;
            }
        }
    }

    private void evaluateObstacles() {
        if (pipePairList.size() > 0) {
            Pipe lastPipe = pipePairList.get(pipePairList.size() - 1);
            if (lastPipe.x + lastPipe.width + Pipe.distanceBetweenColumns <= width + 50) {
                generateObstacle();
            }
            if (lastPipe.x + lastPipe.width < 0) {
                pipePairList.remove(lastPipe);
            }
        }
    }

    private void generateObstacle() {
        Pipe pipe = new Pipe(pipeSpeed, this);
        if (pipePairList.size() > 0) {
            Pipe lastPipe = pipePairList.get(pipePairList.size() - 1);
            pipe.x = lastPipe.x + lastPipe.width + Pipe.distanceBetweenColumns;
        } else {
            pipe.x = width + pipe.width;
        }
        pipePairList.add(pipe);
    }

    private class Bird extends Element {
        public double gravity;
        public double velocity;
        private Bird() {
            this.width = 46;
            this.height = 32;
            this.x = 60;

            velocity = 0;
            gravity = 0.5;
        }

        public void fall() {
            double canvasHeight = FlappyBirdGame.this.height;
            if (!dead) {
                if (this.y + this.height + this.velocity <= canvasHeight - BOTTOM_LIMIT) {
                    this.velocity += this.gravity;
                    this.y += this.velocity;
                }
            } else {
                if (this.y + this.height + this.velocity <= canvasHeight - BOTTOM_LIMIT + 20) {
                    this.velocity += this.gravity;
                    this.y += this.velocity;
                }
            }
        }

        public void fly() {
            this.velocity = -10;
        }
    }

    private static class Pipe extends Element {
        private final Pipe pair;
        private final double speed;
        private static final double distanceBetweenPairs;
        private static final double distanceBetweenColumns;

        private final double MIN;
        private final double MAX;

        static {
            distanceBetweenPairs = 150d;
            distanceBetweenColumns = 175d;
        }

        {
            this.width = 90;
        }

        public Pipe(double speed, FlappyBirdGame game) {
            this.speed = speed;
            MIN = (game.height / 3) - distanceBetweenPairs / 2;
            MAX = (game.height / 3 * 2) - distanceBetweenPairs / 2;

            this.y = 0;
            this.height = Util.getRandomNumberBetween(MIN, MAX);
            this.pair = new Pipe(speed, game, this);
        }

        private Pipe(double speed, FlappyBirdGame game, Pipe topPair) {
            this.speed = speed;
            MIN = (game.height / 3) - distanceBetweenPairs / 2;
            MAX = (game.height / 3 * 2) - distanceBetweenPairs / 2;

            this.y = topPair.y + topPair.height + distanceBetweenPairs;
            this.height = Math.abs(game.height - this.y);
            this.pair = topPair;

        }

        private void move() {
            this.x -= speed;
            pair.x = this.x;
        }
    }
}