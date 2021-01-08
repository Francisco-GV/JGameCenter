package com.frank.jgamecenter.games;

import com.frank.jgamecenter.games.resources.Element;
import com.frank.jgamecenter.games.resources.Game;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SnakeGame extends Game {
    private Snake snake;
    private Direction currentDirection;
    private Direction latestDirection;

    private double canvasWidth;
    private double canvasHeight;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public boolean isXAxis() {
            return this == LEFT || this == RIGHT;
        }

        public boolean isYAxis() {
            return this == UP || this == DOWN;
        }
    }

    @Override
    protected void init() {
        snake = new Snake();

        canvasWidth = getCanvas().getWidth();
        canvasHeight = getCanvas().getHeight();

        snake.getHead().x = snake.size * 10;
        snake.getHead().y = snake.size * 10;
        currentDirection = Direction.RIGHT;
        snake.getHead().direction = Direction.RIGHT;
        latestDirection = currentDirection;

        for (int i = 1; i <= 150; i++) {
            snake.add();
        }

        getCanvas().addEventHandler(KeyEvent.KEY_PRESSED, evt -> currentDirection = switch (evt.getCode()) {
            case UP,W     -> Direction.UP;
            case DOWN,S   -> Direction.DOWN;
            case LEFT,A   -> Direction.LEFT;
            case RIGHT,D  -> Direction.RIGHT;
            default -> currentDirection;
        });
    }

    @Override
    protected void run() {
        for (Snake.Segment segment : snake.body) {
            g.setFill(segment.color);
            g.fillRect(segment.x, segment.y, segment.width, segment.height);
        }
        drawGrid();
        snake.move(latestDirection);
        changeDirection();
    }

    private void changeDirection() {
        if (latestDirection != currentDirection) {
            if (latestDirection.isXAxis() && currentDirection.isYAxis()) {
                if (snake.getHead().x % snake.size == 0) {
                    latestDirection = currentDirection;
                }
            } else if (latestDirection.isYAxis() && currentDirection.isXAxis()) {
                if (snake.getHead().y % snake.size == 0) {
                    latestDirection = currentDirection;
                }
            }
            if (snake.body.size() == 1 && latestDirection.isXAxis() && currentDirection.isXAxis()) {
                latestDirection = currentDirection;
            }

            if (snake.body.size() == 1 && latestDirection.isYAxis() && currentDirection.isYAxis()) {
                latestDirection = currentDirection;
            }
        }
    }

    private void drawGrid() {
        g.setStroke(Color.BLACK);
        for (int i = 0; i < canvasWidth; i += snake.size) {
            g.strokeLine(i, 0, i, canvasHeight);
        }

        for (int i = 0; i < canvasHeight; i += snake.size) {
            g.strokeLine(0, i, canvasWidth, i);
        }
    }

    private class Snake {
        public List<Segment> body;
        private final double size = 25;
        public double speed;
        public Snake() {
            speed = 2.5;
            body = new CopyOnWriteArrayList<>();
            Segment head = new Segment();
            head.width = size;
            head.height = size;
            body.add(0, head);
        }

        public void move(Direction direction) {
            for (int i = 0; i < body.size(); i++) {
                Segment segment = body.get(i);
                segment.lastX = segment.x;
                segment.lastY = segment.y;
                if (i == 0) {
                    segment.direction = direction;
                    switch (direction) {
                        case UP     -> segment.y -= speed;
                        case DOWN   -> segment.y += speed;
                        case LEFT   -> segment.x -= speed;
                        case RIGHT  -> segment.x += speed;
                    }
                } else {
                    segment.x = body.get(i - 1).lastX;
                    segment.y = body.get(i - 1).lastY;
                }
            }
        }

        public void add() {
            Segment segment = new Segment();
            segment.width = size;
            segment.height = size;

            Segment lastSegment = body.get(body.size() - 1);
            segment.direction = latestDirection;
            switch (latestDirection) {
                case UP -> {
                    segment.x = lastSegment.x;
                    segment.y = lastSegment.y + lastSegment.height;
                }
                case DOWN -> {
                    segment.x = lastSegment.x;
                    segment.y = lastSegment.y - lastSegment.height;
                }
                case LEFT -> {
                    segment.y = lastSegment.y;
                    segment.x = lastSegment.x + lastSegment.width;
                }
                case RIGHT -> {
                    segment.y = lastSegment.y;
                    segment.x = lastSegment.x - lastSegment.width;
                }
            }

            body.add(segment);
        }

        public Segment getHead() {
            return body.get(0);
        }

        private class Segment extends Element {
            public double lastX;
            public double lastY;
            public Color color;
            public Direction direction;
            public Segment() {
                color = Color.color(Math.random(), Math.random(), Math.random());
            }
        }
    }

    private class Food extends Element {
        public void generate() {
            this.x = (int) (Math.random() * canvasWidth);
            this.y = (int) (Math.random() * canvasHeight);
        }
    }

    public SnakeGame() {
        super("Snake", """
                A worm or snake constantly moves
                around the board. The worm must 
                try to eat apples that randomly 
                appear.
                """, null);
    }
}
