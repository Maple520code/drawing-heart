package com.example.demo.mode;

import com.example.demo.frame.MainFrame;
import com.example.demo.entity.Heart;
import com.example.demo.utils.Constants;
import com.example.demo.entity.HeartMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.demo.utils.Constants.*;

public class HeartAnimation extends JPanel implements ActionListener, MouseListener {
    private static final long serialVersionUID = 1L;

    private final MainFrame mainFrame;

    private List<Heart> hearts;
    private Graphics2D g2d;
    private HeartMode heartMode = HeartMode.DEFAULT;

    private static final double INIT_HEART_SCALE = 1.0; //初始缩放大小
    private static final double SPLIT_HEART_SCALE = 0.5;
    private static final int DEFAULT_DX = 4;
    private static final int DEFAULT_DY = 4;

    private static final int BUTTON_PX = WINDOW_WIDTH - 120;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    private static int MAX_HEART_SIZE = 30; // 目标大小
    private boolean showTextAnimation = false; // 是否显示文字动画
    private double textScale = 1; // 文字初始缩放比例
    private boolean isTextGrowing = true; // 文字是否正在放大


    public HeartAnimation(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        hearts = new ArrayList<>();
        Timer timer = new Timer(30, this);
        timer.start();

        addMouseListener(this);

        setLayout(null);
        addCustomButton();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g2d = (Graphics2D) g;

        //防锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制爱心
        if (hearts.isEmpty()) {
            initHeart(INIT_HEART_SCALE);
        } else {
            hearts.forEach(heart -> heart.draw(g2d));
        }

        // 爱心数量达到指定数量则显示文字动画
        if (hearts.size() >= MAX_HEART_SIZE) {
            showTextAnimation = true;
            drawTextAnimation();
        }
    }

    private void initHeart(double heartScale) {
        hearts.add(new Heart(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, heartScale, 0, 0, Color.RED));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (heartMode != HeartMode.BEATING) {
            // 移动爱心
            hearts.forEach(heart -> heart.move(getWidth(), getHeight()));
        } else {
            hearts.forEach(Heart::beating);
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (heartMode) {
            case HIT:
                processHitMode(e);
                break;
            case CRAZY:
                processCrazyMode(e);
                break;
            case BEATING:
                processBeatingMode(e);
                break;
            case AUTO:
            default:
                processDefaultMode(e);
                break;
        }
    }

    private void processDefaultMode(MouseEvent e) {
        if (hearts.size() == 1) {
            hearts.clear();
        }
        addSplitHearts(e.getX(), DEFAULT_DX, e.getY(), DEFAULT_DY);
    }

    private void processHitMode(MouseEvent e) {
        for (int i = hearts.size() - 1; i >= 0; i--) {
            Heart heart = hearts.get(i);
            if (heart.contains(e.getX(), e.getY())) {
                hearts.remove(heart);
                hearts.addAll(heart.split());
                break;
            }
        }
    }

    private void processCrazyMode(MouseEvent e) {
        int dx = 15;
        int dy = 15;
        if (hearts.size() == 1) {
            hearts.clear();
        }
        addSplitHearts(e.getX(), dx, e.getY(), dy);
    }

    private void processBeatingMode(MouseEvent e) {
        //do nothing
    }

    private void addSplitHearts(int mouseX, int dx, int mouseY, int dy) {
        hearts.add(new Heart(mouseX - dx, mouseY - dy, SPLIT_HEART_SCALE, -dx, -dy, Constants.colorGenerator()));
        hearts.add(new Heart(mouseX + dx, mouseY - dy, SPLIT_HEART_SCALE, dx, -dy, Constants.colorGenerator()));
    }


    private void addCustomButton() {
        addCustomButton("back", 10, 5, this::pressedBackButton);
        addCustomButton("一路生花", BUTTON_PX, 5, this::pressedDefaultButton);
        addCustomButton("一击即中", BUTTON_PX, 30, this::pressedHitButton);
        addCustomButton("一劳永逸", BUTTON_PX, 55, this::pressedAutoButton);
        addCustomButton("心花怒放", BUTTON_PX, 80, this::pressedCrazyButton);
        addCustomButton("怦然心动", BUTTON_PX, 105, this::pressedBeatButton);
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

    private void pressedDefaultButton() {
        this.heartMode = HeartMode.DEFAULT;
        MAX_HEART_SIZE = 30;
        this.hearts = new ArrayList<>();
        initHeart(INIT_HEART_SCALE);
    }

    private void pressedHitButton() {
        this.heartMode = HeartMode.HIT;
        MAX_HEART_SIZE = 12;
        this.hearts = new ArrayList<>();
        initHeart(INIT_HEART_SCALE);
    }


    private void pressedCrazyButton() {
        this.heartMode = HeartMode.CRAZY;
        MAX_HEART_SIZE = 30;
        this.hearts = new ArrayList<>();
        initHeart(INIT_HEART_SCALE);
    }

    private void pressedAutoButton() {
        this.heartMode = HeartMode.AUTO;
        MAX_HEART_SIZE = 50;
        this.hearts = new ArrayList<>();
        initHeart(INIT_HEART_SCALE);
        new Thread(this::startAutoMode).start();
    }

    private void pressedBeatButton() {
        this.heartMode = HeartMode.BEATING;
        this.hearts = new ArrayList<>();
        initHeart(0);
    }

    private void startAutoMode() {
        try {
            while (heartMode == HeartMode.AUTO && hearts.size() <= 80) {
                int mouseX = new Random().nextInt(WINDOW_WIDTH);
                int mouseY = new Random().nextInt(WINDOW_HEIGHT);
                if (hearts.size() == 1) {
                    hearts.clear();
                }
                addSplitHearts(mouseX, DEFAULT_DX, mouseY, DEFAULT_DY);
                Thread.sleep(500);
            }
        } catch (Exception ignored) {
        }
    }


    private void drawTextAnimation() {
        if (showTextAnimation) {
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.setColor(Color.MAGENTA);

            // 缩放文字
            int textWidth = g2d.getFontMetrics().stringWidth("Happy 520! Love You!");
            int textHeight = g2d.getFontMetrics().getHeight();
            double scaledWidth = textWidth * textScale;
            double scaledHeight = textHeight * textScale;
            int x = (getWidth() - (int) scaledWidth) / 2;
            int y = (getHeight() - (int) scaledHeight) / 2;

            // 创建缩放变换矩阵
            AffineTransform transform = new AffineTransform();
            transform.translate(x, y);
            transform.scale(textScale, textScale);


            // 应用变换矩阵，并绘制文字
            Graphics2D g2dText = (Graphics2D) g2d.create();
            g2dText.transform(transform);

            g2dText.drawString("Happy 520! Love You!", 0, 0);
            g2dText.dispose();

            // 控制文字的缩放效果和闪烁效果
            if (isTextGrowing) {
                textScale += 1;
                // 文字初始缩放比例
                double maxTextScale = 5;
                if (textScale >= maxTextScale) {
                    isTextGrowing = false;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
