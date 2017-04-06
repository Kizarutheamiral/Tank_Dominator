package com.dominator.game.Quadtree;

/**
 * Created by Choujaa Wassil on 24/03/2017.
 */
public class Point {
    public float x;
    public float y;
    public Point(float p0, float p1) {
        x = p0;
        y = p1;
    }

    public Point() {
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Point set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
