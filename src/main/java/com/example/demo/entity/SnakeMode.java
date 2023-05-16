package com.example.demo.entity;

public enum SnakeMode {
    EASY(0, 125),
    MEDIUM(12, 90),
    HARD(25, 50),
    DEAD(30, 30),
    SUCCESS(99, 150);


    private final int length;
    private final int delay;

    SnakeMode(int length, int delay) {
        this.length = length;
        this.delay = delay;
    }

    public int getLength() {
        return length;
    }

    public int getDelay() {
        return delay;
    }
}
