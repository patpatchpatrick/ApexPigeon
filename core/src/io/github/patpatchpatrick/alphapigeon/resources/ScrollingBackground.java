package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScrollingBackground {

    public static final int DEFAULT_SPEED = 10;
    public static final int ACCELERATION = 50;
    public static final int GOAL_REACH_ACCELERATION = 200;

    Texture backgroundImage;
    float x1, x2;
    int speed;  // in pixels/second
    int goalSpeed; // max speed scrolling background will achieve via acceleration
    float imageScale;
    boolean speedFixed;

    public ScrollingBackground() {
        backgroundImage = new Texture("CloudBackground.png");

        x1 = 0;
        x2 = backgroundImage.getWidth()/10;
        speed = DEFAULT_SPEED;
        speedFixed = true;
        imageScale = 0;

    }

    public void updateAndRender(float deltaTime, SpriteBatch batch) {
        //Speed adjustment to reach goal

        x1 -= speed * deltaTime;
        x2 -= speed * deltaTime;

        //if image reaches the bottom  of screen and is not visible, put it back behind the other image
        if (x1 + backgroundImage.getWidth()/10 <= 0)
            x1 = x2 + backgroundImage.getWidth()/10;

        if (x2 + backgroundImage.getWidth()/10 <= 0)
            x2 = x1 + backgroundImage.getWidth()/10;

        //Render
        batch.draw(backgroundImage, x1, 0, backgroundImage.getWidth()/10, Gdx.graphics.getHeight()/10);
        batch.draw(backgroundImage, x2, 0, backgroundImage.getWidth()/10, Gdx.graphics.getHeight()/10 );



    }

    public void resize (int width, int height){
        imageScale = width / backgroundImage.getWidth();
    }

    public void setSpeed(int goalSpeed){
        this.goalSpeed = goalSpeed;
    }

}