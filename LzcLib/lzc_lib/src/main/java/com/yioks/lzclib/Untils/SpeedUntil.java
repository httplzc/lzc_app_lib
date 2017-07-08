package com.yioks.lzclib.Untils;

/**
 * Created by ${User} on 2017/6/14 0014.
 */

public class SpeedUntil {
    int lastX;
    int lastY;
    long lastTime;
    float speedX;
    float speedY;

    public SpeedUntil() {

    }

    public void bindData(int currentX, int currentY) {
        speedX = (float) (currentX - lastX) / (System.currentTimeMillis() - lastTime);
        speedY = (float) (currentY - lastY) / (System.currentTimeMillis() - lastTime);
        this.lastX = currentX;
        this.lastY = currentY;
        lastTime = System.currentTimeMillis();
    }


    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }
}
