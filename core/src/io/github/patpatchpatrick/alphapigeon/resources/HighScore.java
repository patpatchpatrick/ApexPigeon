package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.text.DecimalFormat;

import static java.lang.Math.floor;


public class HighScore {

    private float score;
    private String scoreString;
    private BitmapFont scoreBitmapFont;
    private BitmapFont font12;
    FreeTypeFontGenerator generator;
    private Boolean pigeonHasNotCrashed = true;

    public HighScore() {

        // set default score and create and set up the font used for the high score display
        score = 0;
        scoreString = "Distance: 0";
        scoreBitmapFont = new BitmapFont();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("arcadeclassic.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        font12 = generator.generateFont(parameter);
        font12.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font12.getData().setScale(0.1f);
    }

    public void update(float deltaTime) {
        // increase score
        // the score is equal to the distance (meters) that the bird has traveled
        // if the pigeon has not crashed, keep increasing the score
        // after the pigeon crashes, stop increasing score
        DecimalFormat df = new DecimalFormat("#.");
        if (pigeonHasNotCrashed) {
            score = score + GameVariables.pigeonSpeed * deltaTime;
            scoreString = "Distance        " + df.format(score) + "  m";
        }

    }

    public void render(SpriteBatch batch) {
        // display score
        font12.draw(batch, scoreString, 60, 45);

    }

    public void dispose() {
        generator.dispose();
    }

    public void stopCounting() {
        this.pigeonHasNotCrashed = false;
    }


}
