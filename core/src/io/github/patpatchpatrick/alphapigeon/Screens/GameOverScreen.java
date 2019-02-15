package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.text.DecimalFormat;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.HighScore;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

public class GameOverScreen implements Screen {

    private AlphaPigeon game;
    private PlayServices playServices;
    private DatabaseAndPreferenceManager databaseAndPreferenceManager;
    private OrthographicCamera camera;
    private Viewport viewport;
    private InputProcessor inputProcessor;

    //High Scores
    private float totalNumGames = 0;
    private boolean newHighScoreEarned = false;

    //Textures
    private Texture gameOverBackground;
    private Texture newHighScoreTexture;
    private final float NEW_HIGH_SCORE_TEXTURE_X1 = 13.0f;
    private final float NEW_HIGH_SCORE_TEXTURE_Y1 = 10.7f;
    private final float NEW_HIGH_SCORE_TEXTURE_WIDTH = 55.8f;
    private final float NEW_HIGH_SCORE_TEXTURE_HEIGHT = 8.1f;

    //Button Dimensions
    private final float BACK_BUTTON_X1 = 1.8f;
    private final float BACK_BUTTON_X2 = 17.7f;
    private final float BACK_BUTTON_Y1 = 3.0f;
    private final float BACK_BUTTON_Y2 = 10.5f;

    //Font Generator
    private String gameOverString;
    private BitmapFont scoreFont;
    FreeTypeFontGenerator generator;

    public GameOverScreen(AlphaPigeon game, PlayServices playServices, DatabaseAndPreferenceManager databaseAndPreferenceManager, HighScore highScore) {


        this.game = game;
        this.playServices = playServices;
        this.databaseAndPreferenceManager = databaseAndPreferenceManager;

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept


        //Set viewport to stretch or fit viewport depending on whether user has enabled full screen mode setting
        if (SettingsManager.fullScreenModeIsOn){
            viewport = new StretchViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);
        } else {
            viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);
        }

        gameOverBackground = new Texture(Gdx.files.internal("textures/gameoverscreen/GameOverScreen.png"));
        newHighScoreTexture = new Texture(Gdx.files.internal("textures/gameoverscreen/NewHighScore.png"));

        //Initialize font generator
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/univers.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        scoreFont = generator.generateFont(parameter);
        scoreFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        scoreFont.getData().setScale(0.1f);
        scoreFont.setUseIntegerPositions(false);

        //Handle submitting high currentScore to play services and to local databases
        handleLocalData();
        handlePlayServices();

        //Update the high currentScore string to be displayed
        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormat tgf = new DecimalFormat("#");
        gameOverString = "Distance: " + df.format(highScore.currentScore) + " m"
                + "\nHigh Score: " + df.format(HighScore.currentHighScore) + " m" + "\nTotal Games: " + tgf.format(totalNumGames);

        //Create input processor for user controls
        createInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        //Refresh user settings from shared preferences of mobile device
        SettingsManager.updateSettings();

        if (newHighScoreEarned && SettingsManager.gameSoundsSettingIsOn){
            //If there is a new high score and the game sounds are enabled by user, play the new high score sound
            Sounds.newHighScoreSound.loop(SettingsManager.gameVolume);
        }

        if (playServices != null){
            //Hide ads on game over screen
            playServices.showBannerAds(false);
        }


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch and draw the game world and objects within it
        game.batch.begin();

        game.batch.draw(gameOverBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);
        scoreFont.draw(game.batch, gameOverString, 29, 27);
        //If a new high score was achieved, draw the new high score texture (then when the back button is pushed,
        //reset the newHighScoreEarned boolean to false
        if (newHighScoreEarned) {
            game.batch.draw(newHighScoreTexture, NEW_HIGH_SCORE_TEXTURE_X1, NEW_HIGH_SCORE_TEXTURE_Y1, NEW_HIGH_SCORE_TEXTURE_WIDTH, NEW_HIGH_SCORE_TEXTURE_HEIGHT);
        }

        game.batch.end();

        update();

    }

    private void handlePlayServices() {

        //Check if there is a new high score and if so, submit it to google play services
        //If there is a new high score, newHighScoreEarned is set to true
        newHighScoreEarned = HighScore.submitNewHighScore(playServices);


    }

    private void handleLocalData() {

        if (databaseAndPreferenceManager != null) {

            //Insert game data for the recent game into the local database/shared prefs
            HighScore.updateLocalGameStatisticsData(databaseAndPreferenceManager);

            //Get the total number of games from the local database to display in the game over screen
            totalNumGames = databaseAndPreferenceManager.getTotalNumGames();
        }


    }

    private void update() {
    }

    private void createInputProcessor() {

        inputProcessor = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                //Get the mouse coordinates and unproject to the world coordinates
                Vector3 mousePos = new Vector3(screenX, screenY, 0);
                camera.unproject(mousePos, viewport.getScreenX(), viewport.getScreenY(),  viewport.getScreenWidth(), viewport.getScreenHeight());

                //If the mouse is in bounds of the back button, go back to the main menu
                if (mousePos.x > BACK_BUTTON_X1 && mousePos.x < BACK_BUTTON_X2 && mousePos.y > BACK_BUTTON_Y1 && mousePos.y < BACK_BUTTON_Y2) {
                    if (button == Input.Buttons.LEFT) {
                        if (playServices != null && !SettingsManager.adRemovalPurchased){
                            //Show an interstitial ad when the back button is pushed from the game over screen
                            //Do not show the ad if ad removal was purchased by the user
                            playServices.showOrLoadInterstitialAd();
                        }
                        newHighScoreEarned = false; //Reset the high score
                        Sounds.newHighScoreSound.stop(); //Stop playing the high score sound
                        dispose();
                        game.setScreen(new MainMenuScreen(game, playServices, databaseAndPreferenceManager));
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        };


    }


    @Override
    public void resize(int width, int height) {

        //Update viewport to match screen size
        viewport.update(width, height, true);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        gameOverBackground.dispose();
        newHighScoreTexture.dispose();
        generator.dispose();
        scoreFont.dispose();
    }


}
