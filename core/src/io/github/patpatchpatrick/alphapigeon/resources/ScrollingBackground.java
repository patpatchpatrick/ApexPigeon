package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScrollingBackground {

    public static final int DEFAULT_SPEED = 10;

    Texture backgroundImage;
    // Background image positions
    float x1, x2;
    int speed;
    float imageScale;
    boolean speedFixed;

    public ScrollingBackground() {

        backgroundImage = new Texture("textures/CloudBackgroundDark.png");

        speed = DEFAULT_SPEED;
        speedFixed = true;
        imageScale = 10;

        //Set the first background image position to be 0, and the second background image position to be at the end of the first image (scaled down by 10)
        x1 = 0;
        x2 = backgroundImage.getWidth()/imageScale;


    }

    public void update(float deltaTime) {
        //Speed adjustment for both background images
        x1 -= speed * deltaTime;
        x2 -= speed * deltaTime;

        //if image reaches the bottom  of screen and is not visible, put it back behind the other image
        if (x1 + backgroundImage.getWidth()/imageScale <= 0)
            x1 = x2 + backgroundImage.getWidth()/imageScale;

        if (x2 + backgroundImage.getWidth()/imageScale <= 0)
            x2 = x1 + backgroundImage.getWidth()/imageScale;

    }

    public void render(SpriteBatch batch) {
        //Render
        batch.draw(backgroundImage, x1, 0, backgroundImage.getWidth()/imageScale, Gdx.graphics.getHeight()/10);
        batch.draw(backgroundImage, x2, 0, backgroundImage.getWidth()/imageScale, Gdx.graphics.getHeight()/10 );
    }

    public void resize (int width, int height){
        //imageScale = width / backgroundImage.getWidth();
    }

    public void dispose(){
        backgroundImage.dispose();
    }

}
