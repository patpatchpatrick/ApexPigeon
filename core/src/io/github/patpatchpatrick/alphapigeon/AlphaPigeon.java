package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.patpatchpatrick.alphapigeon.Screens.MainMenuScreen;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

public class AlphaPigeon extends Game{
    public SpriteBatch batch;
    public BitmapFont font;
    private PlayServices playServices;
    private DatabaseAndPreferenceManager databaseAndPreferenceManager;

    public AlphaPigeon(PlayServices playServices, DatabaseAndPreferenceManager databaseAndPreferenceManager){
        //ANDROID CONSTRUCTOR FOR GAME
        this.playServices =  playServices;
        this.databaseAndPreferenceManager = databaseAndPreferenceManager;
    }

    public AlphaPigeon(){
        //DESKTOP CONSTRUCTOR FOR GAME
        this.playServices = null;
        this.databaseAndPreferenceManager = null;
    }

    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        //Set the mobile device database and pref manager on the SettingsManager class
        SettingsManager.databaseAndPreferenceManager = databaseAndPreferenceManager;
        this.setScreen(new MainMenuScreen(this, playServices, databaseAndPreferenceManager));

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
        Sounds.dispose();
    }

}
