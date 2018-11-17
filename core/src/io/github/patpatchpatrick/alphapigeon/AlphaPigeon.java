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
    public final short CATEGORY_METEOR = 0x0004;
    public final short CATEGORY_LEVEL_TWO_BIRD = 0x0008;
    public final short CATEGORY_POWERUP_SHIELD = 0x0016;
    public final short CATEGORY_TELEPORT = 0x0032;
    public final short CATEGORY_ROCKET = 0x0064;
    public final short CATEGORY_ROCKET_EXPLOSION = 0x0128;
    public final short CATEGORY_ALIEN_MISSILE = 0x0256;

    // Filter Masks
    public final short MASK_PIGEON = CATEGORY_LEVEL_ONE_BIRD | CATEGORY_METEOR | CATEGORY_LEVEL_TWO_BIRD
            | CATEGORY_POWERUP_SHIELD | CATEGORY_TELEPORT | CATEGORY_ROCKET | CATEGORY_ROCKET_EXPLOSION
            | CATEGORY_ALIEN_MISSILE;
    public final short MASK_LEVEL_ONE_BIRD = CATEGORY_PIGEON | CATEGORY_METEOR | CATEGORY_ROCKET ;
    public final short MASK_METEOR = CATEGORY_PIGEON | CATEGORY_LEVEL_ONE_BIRD | CATEGORY_LEVEL_TWO_BIRD;
    public final short MASK_LEVEL_TWO_BIRD = CATEGORY_PIGEON | CATEGORY_METEOR | CATEGORY_ROCKET;
    public final short MASK_POWERUP = CATEGORY_PIGEON;
    public final short MASK_TELEPORT = CATEGORY_PIGEON;
    public final short MASK_ROCKET = CATEGORY_PIGEON | CATEGORY_LEVEL_ONE_BIRD | CATEGORY_LEVEL_TWO_BIRD;
    public final short MASK_ROCKET_EXPLOSION = CATEGORY_PIGEON;
    public final short MASK_ALIEN_MISSILE = CATEGORY_PIGEON;


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
