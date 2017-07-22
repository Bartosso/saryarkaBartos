package com.turlygazhy.entity;

public class Top10 {
    private String text;
    private int   count;

    public Top10(String text, int count) {
        this.text = text;
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public int getCount() {
        return count;
    }
}
