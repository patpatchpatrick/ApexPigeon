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

    public HighScore(){
        score = 0;
        scoreString = "Distance: 0";
        scoreBitmapFont = new BitmapFont();
    }

    public void updateAndRender(float deltaTime, SpriteBatch batch){
        //Increase score
        score = score + 9 * deltaTime;
        scoreString = "S co r e        " + score;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ARCADECLASSIC.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font12 = generator.generateFont(parameter); // font size 12 pixels



        font12.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font12.getData().setScale(0.1f);
        font12.draw(batch, scoreString, 60, 45);
    }

    public void dispose(){
        generator.dispose();
    }





}
