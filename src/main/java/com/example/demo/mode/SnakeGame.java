package com.example.demo.mode;

import com.example.demo.frame.MainFrame;
import com.example.demo.entity.SnakeMode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.GeneralPath;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

import static com.example.demo.utils.Constants.WINDOW_HEIGHT;
import static com.example.demo.utils.Constants.WINDOW_WIDTH;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (WINDOW_WIDTH * WINDOW_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    private AtomicInteger snakeLength = new AtomicInteger(3);
    private int[] snakeX = new int[GAME_UNITS];
    private int[] snakeY = new int[GAME_UNITS];
    private int foodX;
    private int foodY;
    private char direction = 'R';
    private boolean running = false;

    private Timer timer;
    private Graphics2D g2d;

    private final MainFrame mainFrame;


    public SnakeGame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        setLayout(null);
        addCustomButton();

        startGame();
    }

    public void startGame() {
        initSnakePosition();
        generateFood();
        resetTimer(SnakeMode.EASY);
        running = true;
        snakeLength = new AtomicInteger(3);
        direction = 'R';
    }

    private void initSnakePosition() {
        snakeX = new int[GAME_UNITS];
        snakeY = new int[GAME_UNITS];
        snakeX[0] = 0;
        snakeY[0] = UNIT_SIZE * 2;
        snakeX[1] = 0;
        snakeY[1] = UNIT_SIZE * 2;
    }

    public void resetTimer(SnakeMode level) {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        timer = new Timer(level.getDelay(), this);
        timer.setDelay(level.getDelay());
        timer.setInitialDelay(0);
        timer.start();
    }

    public void generateFood() {
        //前两行不产生food
        foodX = ThreadLocalRandom.current().nextInt(1, (WINDOW_WIDTH / UNIT_SIZE) - 1) * UNIT_SIZE;
        foodY = ThreadLocalRandom.current().nextInt(2, (WINDOW_HEIGHT / UNIT_SIZE) - 2) * UNIT_SIZE;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g2d = (Graphics2D) g;

        // 绘制灰色实线
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, UNIT_SIZE * 2 - 2, WINDOW_WIDTH, UNIT_SIZE * 2 - 2);

        description();
        draw(g2d);
    }

    public void draw(Graphics2D g) {
        int heartSize = UNIT_SIZE / 2;
        if (running) {
            g2d.setColor(Color.RED);
            draw(g2d, foodX, foodY, heartSize);

            for (int i = 0; i < snakeLength.get(); i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                draw(g2d, snakeX[i], snakeY[i], heartSize);
            }

            Toolkit.getDefaultToolkit().sync();
            showCongratulation();
        } else {
            showMsg("Game Over!");
        }
    }

    public void draw(Graphics2D g2d, int px, int py, int heartSize) {
        int x = px + heartSize;
        int y = py + heartSize;

        double start = Math.sqrt(3) / 2.0;
        double xRate = 2;
        double yRate = 1.5;

        // 绘制心形
        GeneralPath heartShape = new GeneralPath();
        heartShape.moveTo(x, y - heartSize * start);
        heartShape.curveTo(x + heartSize * yRate, y - heartSize * yRate,
                x + heartSize * xRate, y, x, y + heartSize);
        heartShape.curveTo(x - heartSize * xRate, y,
                x - heartSize * yRate, y - heartSize * yRate, x, y - heartSize * start);

        g2d.fill(heartShape);
    }


    public void move() {
        for (int i = snakeLength.get(); i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (direction) {
            case 'U':
                snakeY[0] -= UNIT_SIZE;
                break;
            case 'D':
                snakeY[0] += UNIT_SIZE;
                break;
            case 'L':
                snakeX[0] -= UNIT_SIZE;
                break;
            case 'R':
                snakeX[0] += UNIT_SIZE;
                break;
        }
    }

    public void checkFoodCollision() {
        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            snakeLength.incrementAndGet();
            generateFood();
            resetLevel(snakeLength.get());
        }
    }

    public void resetLevel(int snakeLength) {
        if (snakeLength == SnakeMode.MEDIUM.getLength()) {
            resetTimer(SnakeMode.MEDIUM);
        } else if (snakeLength == SnakeMode.HARD.getLength()) {
            resetTimer(SnakeMode.HARD);
        } else if (snakeLength == SnakeMode.DEAD.getLength()) {
            resetTimer(SnakeMode.DEAD);
        }
    }

    public void showCongratulation() {
        if (snakeLength.get() == SnakeMode.MEDIUM.getLength()) {
            showMsg("Happy 520! Speed up~");
        } else if (snakeLength.get() == SnakeMode.HARD.getLength()) {
            showMsg("Love You! Hard mode~");
        } else if (snakeLength.get() == SnakeMode.DEAD.getLength()) {
            showMsg("Amazing! Over mode~");
        } else if (snakeLength.get() == SnakeMode.SUCCESS.getLength()) {
            running = false;
            showMsg("Congratulations! You are so great~");
        }
    }

    public void checkCollision() {
        // Check if snake collides with itself
        for (int i = snakeLength.get(); i > 0; i--) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                running = false;
                break;
            }
        }

        // Check if snake hits the borders
        if (snakeX[0] < 0 || snakeX[0] >= WINDOW_WIDTH - UNIT_SIZE ||
                snakeY[0] < UNIT_SIZE * 2 || snakeY[0] >= WINDOW_HEIGHT - UNIT_SIZE * 2) {
            running = false;
        }
    }

    private void showMsg(String msg) {
        g2d.setColor(Color.MAGENTA);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int gameOverWidth = fontMetrics.stringWidth(msg);
        int gameOverHeight = fontMetrics.getHeight();
        int x = (WINDOW_WIDTH - gameOverWidth) / 2;
        int y = (WINDOW_HEIGHT - gameOverHeight) / 2;
        g2d.drawString(msg, x, y);
    }

    private void description() {
        String msg = snakeLength.get() <= 3 ? "please use the arrow keys to play the game" :
                "snake length is: " + snakeLength.get();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int gameOverWidth = fontMetrics.stringWidth(msg);
        int gameOverHeight = fontMetrics.getHeight();
        int x = (WINDOW_WIDTH - gameOverWidth) / 2;
        int y = gameOverHeight;
        g2d.drawString(msg, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFoodCollision();
            checkCollision();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }


    private void addCustomButton() {
        addCustomButton("back", 10, 7, this::pressedBackButton);
        addCustomButton("reset", WINDOW_WIDTH - 120, 7, this::pressedResetButton);
    }

    private void addCustomButton(String text, int buttonX, int buttonY, Runnable callback) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.addActionListener(e -> callback.run());
        add(button);
    }

    private void pressedBackButton() {
        mainFrame.showMainFrame();
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            JFrame jFrame = (JFrame) window;
            jFrame.dispose();
        }
    }

    private void pressedResetButton() {
        startGame();
        requestFocusInWindow();
    }

}

