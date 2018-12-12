package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

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
        parameter.size = 12;
        font12 = generator.generateFont(parameter);
        font12.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font12.getData().setScale(0.1f);
    }

    public void update(float deltaTime) {
        // increase score
        // the 9 is the default speed that the pigeon initially flies
        // if the pigeon has not crashed, keep increasing the score
        // after the pigeon crashes, stop increasing score
        if (pigeonHasNotCrashed) {
            score = score + 9 * deltaTime;
            scoreString = "S co r e        " + score;
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
