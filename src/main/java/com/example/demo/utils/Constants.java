package com.example.demo.utils;

import java.awt.*;
import java.util.Random;

import static java.awt.Color.*;

public class Constants {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    public static final Color[] colors = {RED, PINK, GREEN, YELLOW, BLUE, MAGENTA, ORANGE, CYAN};

    public static Color colorGenerator() {

        Random random = new Random();
        int randomNumber = random.nextInt(100); // 生成0到99之间的随机数

        if (randomNumber < 50) {
            return RED;
        } else if (randomNumber < 60) {
            return PINK;
        } else {
            int index = random.nextInt(colors.length - 2) + 2;
            return colors[index];
        }
    }

}
