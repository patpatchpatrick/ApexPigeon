package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScrollingBackground {

    //Scrolling background that scrolls over time to give the illusion of movement
    //The speed of the scrolling background matches the pigeon's speed
    //The background moves backwards relative to pigeon and visually indicates that pigeon is flying a certain speed

    Texture backgroundImage;
    // Background image positions
    float x1, x2;
    float imageScale;

    public ScrollingBackground() {

        backgroundImage = new Texture("textures/CloudPixelArtDark.png");

        //Initiate the background speed as the minimum speed
        imageScale = 10;

        //Set the first background image position to be 0, and the second background image position to be at the end of the first image (scaled down by 10)
        x1 = 0;
        x2 = backgroundImage.getWidth() / imageScale;


    }

    public void update(float deltaTime) {
        //Speed adjustment for both background images
        x1 -= GameVariables.pigeonSpeed * deltaTime;
        x2 -= GameVariables.pigeonSpeed * deltaTime;

        //if image reaches the bottom  of screen and is not visible, put it back behind the other image
        if (x1 + backgroundImage.getWidth() / imageScale <= 0)
            x1 = x2 + backgroundImage.getWidth() / imageScale;

        if (x2 + backgroundImage.getWidth() / imageScale <= 0)
            x2 = x1 + backgroundImage.getWidth() / imageScale;

    }

    public void render(SpriteBatch batch) {
        //Render
        batch.draw(backgroundImage, x1, 0, backgroundImage.getWidth() / imageScale, Gdx.graphics.getHeight() / 10);
        batch.draw(backgroundImage, x2, 0, backgroundImage.getWidth() / imageScale, Gdx.graphics.getHeight() / 10);
    }

    public void resize(int width, int height) {
    }

    public void dispose() {
        backgroundImage.dispose();
    }

}
