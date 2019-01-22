package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.patpatchpatrick.alphapigeon.Screens.MainMenuScreen;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseManager;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

public class AlphaPigeon extends Game{
    public SpriteBatch batch;
    public BitmapFont font;
    private PlayServices playServices;
    private DatabaseManager databaseManager;

    public AlphaPigeon(PlayServices playServices, DatabaseManager databaseManager){
        //ANDROID CONSTRUCTOR FOR GAME
        this.playServices =  playServices;
        this.databaseManager = databaseManager;
    }

    public AlphaPigeon(){
        //DESKTOP CONSTRUCTOR FOR GAME
        this.playServices = null;
        this.databaseManager = null;
    }

    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this, playServices, databaseManager));

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
