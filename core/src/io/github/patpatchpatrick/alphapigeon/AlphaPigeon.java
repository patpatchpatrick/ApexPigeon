package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.patpatchpatrick.alphapigeon.Screens.MainMenuScreen;

public class AlphaPigeon extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    // Filter Categories
    public final short CATEGORY_PIGEON = 0x0001;
    public final short CATEGORY_LEVEL_ONE_BIRD = 0x0002;
    public final short CATEGORY_METEOR = 0x0003;

    // Filter Masks
    public final short MASK_PIGEON = CATEGORY_LEVEL_ONE_BIRD | CATEGORY_METEOR;
    public final short MASK_LEVEL_ONE_BIRD = CATEGORY_PIGEON | CATEGORY_METEOR ;
    public final short MASK_METEOR = CATEGORY_PIGEON | CATEGORY_METEOR;


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
