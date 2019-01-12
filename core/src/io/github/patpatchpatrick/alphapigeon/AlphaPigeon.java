package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.patpatchpatrick.alphapigeon.Screens.MainMenuScreen;

public class AlphaPigeon extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this));

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

        batch.dispose();
        font.dispose();
    }



}
