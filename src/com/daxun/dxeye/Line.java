package com.daxun.dxeye;

/**
 * Created by luhuiguo on 13-7-7.
 */
public class Line {

    private int x;

    private int y;

    private String content;

    public Line() {

    }

    public Line(int x, int y, String content) {
        this.x = x;
        this.y = y;
        this.content = content;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Line{" +
                "x=" + x +
                ", y=" + y +
                ", content='" + content + '\'' +
                '}';
    }

}
