package com.frank.jgamecenter.games;

import com.frank.jgamecenter.games.resources.GraphicGame;
import com.frank.jgamecenter.games.resources.Element;

import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SnakeGame extends GraphicGame {
    private Snake snake;
    private Food food;
    private Direction currentDirection;
    private Direction latestDirection;

    private double canvasWidth;
    private double canvasHeight;

    private Image snakeZero;
    private Image snakeHead;
    private Image snakeTail;
    private Image snakeBody;
    private Image snakeTurn;
    private Image snakeDead;
    private Image apple;

    private boolean stop = false;

    private final double snakeSize;

    private final int HORIZONTAL_CELLS;
    private final int VERTICAL_CELLS;

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
        snakeZero = loadImage("/resources/images/snake/snake_zero.png", 32);
        snakeHead = loadImage("/resources/images/snake/snake_head.png", 32);
        snakeTail = loadImage("/resources/images/snake/snake_tail.png", 32);
        snakeBody = loadImage("/resources/images/snake/snake_body.png", 32);
        snakeTurn = loadImage("/resources/images/snake/snake_turn.png", 32);
        snakeDead = loadImage("/resources/images/snake/snake_head_dead.png", 32);
        apple     = loadImage("/resources/images/snake/apple.png", 32);

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        getGameNode().addEventHandler(KeyEvent.KEY_PRESSED,
            evt -> currentDirection = switch (evt.getCode()) {
                case UP,W     -> Direction.UP;
                case DOWN,S   -> Direction.DOWN;
                case LEFT,A   -> Direction.LEFT;
                case RIGHT,D  -> Direction.RIGHT;
                default -> currentDirection;
        });

        stop = false;

        snake = new Snake(snakeSize);
        snake.getHead().x = snake.size * 0;
        snake.getHead().y = snake.size * 0;

        currentDirection = Direction.RIGHT;
        snake.getHead().direction = Direction.RIGHT;
        latestDirection = currentDirection;

        food = new Food(snake.size);
        food.generate();

        snake.grow();
    }

    private int counter;
    @Override
    protected void run() {
        if (!stop) {
            changeDirection();
            int FRAMES = 5;
            if (counter++ == FRAMES) {
                counter = 0;
                snake.move(latestDirection);
            }
            evaluateCollision();
        }

        drawFilledGrid();
        List<Snake.Segment> listCopy = new ArrayList<>(snake.body);
        Collections.reverse(listCopy);
        for (Snake.Segment segment : listCopy) {
            drawSegment(segment);
        }

        // draw apple
        g.drawImage(apple, food.x, food.y, food.size, food.size);
    }

    @Override
    public void restart() {
        init();
    }

    private void drawFilledGrid() {
        Color color1 = Color.rgb(154, 203, 143);
        Color color2 = Color.rgb(163, 191, 115);

        for (int i = 0; i < HORIZONTAL_CELLS; i++) {
            for (int n = 0; n < VERTICAL_CELLS; n++) {
                g.setFill((i + n) % 2 == 0 ? color1 : color2);
                g.fillRect(i * snake.size, n * snake.size, snake.size, snake.size);
            }
        }
    }

    private void evaluateCollision() {
        if (snake.getHead().collides(food)) {
            snake.grow();
            food.generate();
        }

        for (int i = 2; i < snake.body.size(); i++) {
            if (snake.getHead().collides(snake.body.get(i))) {
                stop = true;
            }
        }
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

    private void drawSegment(Snake.Segment currentSegment) {
        Image image;
        Direction direction = currentSegment.direction;

        int index = snake.body.indexOf(currentSegment);
        int deg = -1;


        if (index == 0) {
            image = (stop) ? snakeDead : (snake.body.size() == 1) ? snakeZero : snakeHead;
            switch (direction) {
                case UP     -> rotateImage(image, currentSegment.x, currentSegment.y, 0);
                case DOWN   -> rotateImage(image, currentSegment.x + snake.size, currentSegment.y + snake.size, 180);
                case LEFT   -> rotateImage(image, currentSegment.x, currentSegment.y + snake.size, 270);
                case RIGHT  -> rotateImage(image, currentSegment.x + snake.size, currentSegment.y, 90);
            }

        } else {
            if (index == snake.body.size() - 1) {
                Snake.Segment previous = snake.body.get(index - 1);
                image = snakeTail;

                if (previous.x == currentSegment.x) {
                    if (previous.y < currentSegment.y) {
                        deg = 0;
                    } else if (previous.y > currentSegment.y) {
                        deg = 180;
                    }

                }
                if (previous.y == currentSegment.y) {
                    if (previous.x < currentSegment.x) {
                        deg = 270;
                    } else if (previous.x > currentSegment.x) {
                        deg = 90;
                    }
                }
            } else {
                Object[] values = calculateRotation(index);

                image = (Image) values[0];
                deg = (Integer) values[1];
            }

            switch (deg) {
                case 0      -> rotateImage(image, currentSegment.x, currentSegment.y, 0);
                case 180    -> rotateImage(image, currentSegment.x + snake.size, currentSegment.y + snake.size, 180);
                case 270    -> rotateImage(image, currentSegment.x, currentSegment.y + snake.size, 270);
                case 90     -> rotateImage(image, currentSegment.x + snake.size, currentSegment.y, 90);
            }
        }
    }

    private void rotateImage(Image image, double x, double y, int deg) {
        g.save();
        Rotate r = new Rotate(deg, x, y);
        g.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        g.drawImage(image, x, y, snake.size, snake.size);
        g.restore();
    }

    private Object[] calculateRotation(int index) {
        Snake.Segment current = snake.body.get(index);
        Snake.Segment previous = null;
        Snake.Segment next = null;

        int deg = -1;
        Image image = null;

        if (index - 1 >= 0) {
            previous = snake.body.get(index - 1);
        }

        if (index + 1 < snake.body.size()) {
            next = snake.body.get(index + 1);
        }

        if (previous != null && next != null) {
            if (next.y == current.y && current.y != previous.y) {
                image = snakeTurn;
                if (next.x < current.x && next.x < previous.x) {
                    if (next.y < previous.y) {
                        deg = 90;
                    } else if (next.y > previous.y) {
                        deg = 180;
                    }
                } else if (next.x > current.x && next.x > previous.x) {
                    if (next.y < previous.y) {
                        deg = 0;
                    } else if (next.y > previous.y) {
                        deg = 270;
                    }
                }
            } else if (next.x == current.x && current.x != previous.x) {
                image = snakeTurn;
                if (next.y < current.y && next.y < previous.y) {
                    if (next.x < previous.x) {
                        deg = 270;
                    } else if (next.x > previous.x) {
                        deg = 180;
                    }
                } else if (next.y > current.y && next.y > previous.y) {
                    if (next.x < previous.x) {
                        deg = 0;
                    } else if (next.x > previous.x) {
                        deg = 90;
                    }
                }
            } else if (next.x == current.x) {
                image = snakeBody;
                deg = 0;
            } else if (next.y == current.y) {
                image = snakeBody;
                deg = 90;
            }
        }

        return new Object[] {image, deg};
    }

    private class Snake {
        public List<Segment> body;
        private final double size;
        public double speed;
        public Snake(double size) {
            this.size = size;
            speed = size / 10;
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
                        case UP     -> segment.y -= size;
                        case DOWN   -> segment.y += size;
                        case LEFT   -> segment.x -= size;
                        case RIGHT  -> segment.x += size;
                    }
                } else {
                    segment.x = body.get(i - 1).lastX;
                    segment.y = body.get(i - 1).lastY;
                }
            }
        }

        public void grow() {
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

            @Override
            public boolean collides(Element element) {
                return this.x == element.x && this.y == element.y;
            }
        }
    }

    private class Food extends Element {
        public double size;

        public Food(double size) {
            this.size = size;
        }

        public void generate() {
            this.x = snake.size * (int) (Math.random() * canvasWidth / snake.size);
            this.y = snake.size * (int) (Math.random() * canvasHeight / snake.size);
        }
    }

    public SnakeGame() {
        super("Snake", """
                The player controls a snake that
                moves around the board. The snake 
                must try to eat apples that 
                randomly appear.""",
                loadThumbnail("snake.png"));

        snakeSize = 32.0;

        HORIZONTAL_CELLS = 25;
        VERTICAL_CELLS = 15;

        g.getCanvas().setWidth(snakeSize * HORIZONTAL_CELLS);
        g.getCanvas().setHeight(snakeSize * VERTICAL_CELLS);
    }
}