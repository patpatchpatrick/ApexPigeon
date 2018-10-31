package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HighScore {

    private float score;
    private String scoreString;
    private BitmapFont scoreBitmapFont;

    public HighScore(){
        score = 0;
        scoreString = "Distance: 0";
        scoreBitmapFont = new BitmapFont();
    }

    public void updateAndRender(float deltaTime, SpriteBatch batch){
        //Increase score
        score = score + 9 * deltaTime;
        scoreString = "S co r e   " + score;

        scoreBitmapFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        scoreBitmapFont.getData().setScale(0.1f);
        scoreBitmapFont.draw(batch, scoreString, 60, 45);
    }





}
