package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScrollingBackground {

    public static final int DEFAULT_SPEED = 80;
    public static final int ACCELERATION = 50;
    public static final int GOAL_REACH_ACCELERATION = 200;

    Texture backgroundImage;
    float y1, y2;
    int speed;  // in pixels/second
    int goalSpeed; // max speed scrolling background will achieve via acceleration
    float imageScale;
    boolean speedFixed;

    public ScrollingBackground() {
        backgroundImage = new Texture("CloudBackground.png");

        y1 = 0;
        y2 = backgroundImage.getHeight();
        speed = 0;
        goalSpeed = DEFAULT_SPEED;
        speedFixed = true;
        imageScale = 0;

    }

    public void updateAndRender(float deltaTime, SpriteBatch batch) {
        //Speed adjustment to reach goal
        if (speed < goalSpeed) {
            speed += GOAL_REACH_ACCELERATION * deltaTime;
            if (speed > goalSpeed) {
                speed = goalSpeed;
            }
        } else if (speed > goalSpeed) {
            speed -= GOAL_REACH_ACCELERATION * deltaTime;
            if (speed < goalSpeed) {
                speed = goalSpeed;
            }
        }

        if (!speedFixed)
            speed += ACCELERATION * deltaTime;

        y1 -= speed * deltaTime;
        y2 -= speed * deltaTime;

        //if image reaches the bottom  of screen and is not visible, put it back behind the other image
        if (y1 + backgroundImage.getHeight() <= 0)
            y1 = y2 + backgroundImage.getHeight();

        if (y2 + backgroundImage.getHeight() <= 0)
            y2 = y1 + backgroundImage.getHeight();

        //Render
        batch.draw(backgroundImage, 0, y1, Gdx.graphics.getWidth(), backgroundImage.getHeight());
        batch.draw(backgroundImage, 0, y2, Gdx.graphics.getWidth(), backgroundImage.getHeight() );



    }

    public void resize (int width, int height){
        imageScale = width / backgroundImage.getWidth();
    }

    public void setSpeed(int goalSpeed){
        this.goalSpeed = goalSpeed;
    }

    public void setSpeedFixed(boolean speedFixed){
        this.speedFixed = speedFixed;
    }

}
