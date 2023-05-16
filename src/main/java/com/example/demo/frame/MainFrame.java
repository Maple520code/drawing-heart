package com.example.demo.frame;

import javax.swing.*;

import static com.example.demo.utils.Constants.WINDOW_HEIGHT;
import static com.example.demo.utils.Constants.WINDOW_WIDTH;

public class MainFrame extends JFrame {
    private final ModeSelection modeSelection;

    public MainFrame() {
        setTitle("Mode Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);

        modeSelection = new ModeSelection(this);
        setContentPane(modeSelection);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void hideMainFrame() {
        setVisible(false);
    }

    public void showMainFrame() {
        setContentPane(modeSelection);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
