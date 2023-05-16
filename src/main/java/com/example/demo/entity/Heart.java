package com.example.demo.entity;

import com.example.demo.utils.Constants;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.utils.Constants.WINDOW_HEIGHT;

public class Heart {

    private int x;
    private int y;
    private double scale;
    private int dx;
    private int dy;

    private GeneralPath heartShape;
    private final Color color;
    private static final int radius = 100;
    private double delta = 0.02;
    private double initStep = 2000;
    private int stepOffset = -10;


    public Heart(int x, int y, double scale, int dx, int dy, Color color) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return "Heart{" +
                "x=" + x +
                ", y=" + y +
                ", scale=" + scale +
                '}';
    }

    public void draw(Graphics2D g2d) {
        int heartSize = (int) (radius * scale);

        double start = Math.sqrt(3) / 2.0;
        double xRate = 2;
        double yRate = 1.5;

        // 绘制心形
        this.heartShape = new GeneralPath();
        heartShape.moveTo(x, y - heartSize * start);
        heartShape.curveTo(x + heartSize * yRate, y - heartSize * yRate,
                x + heartSize * xRate, y, x, y + heartSize);
        heartShape.curveTo(x - heartSize * xRate, y,
                x - heartSize * yRate, y - heartSize * yRate, x, y - heartSize * start);

        g2d.setColor(this.color);
        g2d.fill(heartShape);
        if (scale > 1.0) {
            description(g2d);
        }
    }


    public void move(int screenWidth, int screenHeight) {
        x += dx;
        y += dy;

        // 随机改变移动方向
        if (Math.random() < 0.05) {
            dx = -dx;
        }
        if (Math.random() < 0.05) {
            dy = -dy;
        }

        // 控制移动范围
        if (x < 0 || x > screenWidth) {
            dx = -dx;
        }
        if (y < 0 || y > screenHeight) {
            dy = -dy;
        }
    }

    public List<Heart> split() {
        double splitScale = 0.5;
        int dx = 4;
        int dy = 4;

        ArrayList<Heart> hearts = new ArrayList<>();
        hearts.add(new Heart(x - dx, y - dy, splitScale, -dx, -dy, Constants.colorGenerator()));
        hearts.add(new Heart(x + dx, y - dy, splitScale, dx, -dy, Constants.colorGenerator()));

        return hearts;
    }

    public void beating() {
        //缩放
        scaling();
        //跳动
        dancing();
    }

    private void scaling() {
        double minScale = 0;
        double maxScale = 2.0;

        //缩放
        scale += delta;
        if (scale <= minScale || scale >= maxScale) {
            delta = -delta;
        }
    }

    private void dancing() {
        int minStep = 0;
        int maxStep = 2000;

        initStep += stepOffset;
        if (initStep <= minStep || initStep >= maxStep) {
            stepOffset = -stepOffset;
        }
        double theta = initStep / maxStep;

        // 控制跳动幅度和速度
        double amplitude = 50;
        double speed = 4;

        // 更新爱心的位置
        double yOffset = amplitude * Math.sin(theta * speed * 2 * Math.PI);
        y = WINDOW_HEIGHT / 2 + (int) yOffset;

    }

    private void description(Graphics2D g2d) {
        String msg = "I Love You";
        g2d.setColor(Color.MAGENTA);
        g2d.setFont(new Font("Arial", Font.BOLD, (int) (30 * scale)));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int gameOverWidth = fontMetrics.stringWidth(msg);

        g2d.drawString(msg, x - gameOverWidth / 2, y);
    }

    public boolean contains(int mouseX, int mouseY) {
        return heartShape.contains(mouseX, mouseY);
    }
}
