package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;
import io.github.patpatchpatrick.alphapigeon.Screens.MainMenuScreen;
import io.github.patpatchpatrick.alphapigeon.resources.AppleGameCenterManager;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseManager;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;
import de.golfgl.gdxgamesvcs.IGameServiceClient;

public class AlphaPigeon extends Game{
    public SpriteBatch batch;
    public BitmapFont font;
    protected IGameServiceClient gsClient;
    public AppleGameCenterManager appleGameCenter;
    private PlayServices playServices;
    private DatabaseManager databaseManager;

    public AlphaPigeon(PlayServices playServices, DatabaseManager databaseManager){
        //ANDROID CONSTRUCTOR FOR GAME
        this.playServices =  playServices;
        this.databaseManager = databaseManager;
    }

    public AlphaPigeon(){
        this.playServices = null;
        this.databaseManager = null;
    }


    @Override
    public void create() {

        initializeAppleGameServices();

        batch = new SpriteBatch();
        font = new BitmapFont();
        //Set the mobile device database and pref manager on the SettingsManager class
        SettingsManager.databaseManager = databaseManager;
        this.setScreen(new MainMenuScreen(this, playServices, databaseManager));

    }

    private void initializeAppleGameServices(){
        //Initialize game service client for Apple Game Center
        if (gsClient == null){
            gsClient = new NoGameServiceClient();
        }

       appleGameCenter = new AppleGameCenterManager(gsClient);


    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        super.pause();
        appleGameCenter.gsClient.pauseSession();
    }

    @Override
    public void resume() {
        super.resume();
        appleGameCenter.gsClient.resumeSession();
    }

    @Override
    public void dispose() {

        batch.dispose();
        font.dispose();
        Sounds.dispose();
    }

}
