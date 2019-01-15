package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.patpatchpatrick.alphapigeon.Screens.MainMenuScreen;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

public class AlphaPigeon extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    private PlayServices playServices;

    public AlphaPigeon(PlayServices playServices){
        //ANDROID CONSTRUCTOR FOR GAME
        this.playServices =  playServices;
    }

    public AlphaPigeon(){
        //DESKTOP CONSTRUCTOR FOR GAME
        this.playServices = null;
    }

    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this, playServices));

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
