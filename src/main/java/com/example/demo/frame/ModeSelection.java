package com.example.demo.frame;

import com.example.demo.mode.HeartAnimation;
import com.example.demo.mode.SnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.example.demo.utils.Constants.WINDOW_HEIGHT;
import static com.example.demo.utils.Constants.WINDOW_WIDTH;

public class ModeSelection extends JPanel implements ActionListener {
    private final MainFrame mainFrame;

    private final JButton heartModeButton;
    private final JButton gameModeButton;


    public ModeSelection(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());

        // 创建按钮
        heartModeButton = new JButton("Heart Mode");
        gameModeButton = new JButton("Game Mode");
        heartModeButton.setFocusPainted(false);
        gameModeButton.setFocusPainted(false);

        // 设置按钮的大小
        Dimension buttonSize = new Dimension(200, 50);
        heartModeButton.setPreferredSize(buttonSize);
        gameModeButton.setPreferredSize(buttonSize);

        // 设置按钮监听器
        heartModeButton.addActionListener(this);
        gameModeButton.addActionListener(this);

        // 添加按钮到面板
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(heartModeButton, gbc);

        gbc.gridy = 1;
        add(gameModeButton, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 判断点击的按钮，并根据按钮选择进入对应的模式
        if (e.getSource() == heartModeButton) {
            showHeartMode();
        } else if (e.getSource() == gameModeButton) {
            showGameMode();
        }
        mainFrame.hideMainFrame();
    }

    private void showHeartMode() {
        String title = "please click the heart!";
        createFrame(title, new HeartAnimation(mainFrame));
    }

    private void showGameMode() {
        String title = "Heart Snake Game";
        createFrame(title, new SnakeGame(mainFrame));
    }

    private void createFrame(String title, Component component) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(component);
        frame.setVisible(true);
    }
}

