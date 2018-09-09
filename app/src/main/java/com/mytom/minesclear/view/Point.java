package com.mytom.minesclear.view;

/**
 * Created by yufeiyang on 2018/9/8.
 */

public class Point {

    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 2 * x + y;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return this.hashCode() == ( (obj)).hashCode();

    }
}
