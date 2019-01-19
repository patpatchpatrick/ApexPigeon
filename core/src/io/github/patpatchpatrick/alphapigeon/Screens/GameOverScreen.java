package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.text.DecimalFormat;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.HighScore;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;
import sun.rmi.runtime.Log;

public class GameOverScreen implements Screen {

    private AlphaPigeon game;
    private PlayServices playServices;
    private DatabaseManager databaseManager;
    private OrthographicCamera camera;
    private Viewport viewport;

    //Variables
    private float gameOverDeltaTime;
    private float gameOverStateTime;

    //Textures
    private Texture gameOverBackground;

    //Button Dimensions
    private final float BACK_BUTTON_X1 = 1.8f;
    private final float BACK_BUTTON_X2 = 17.7f;
    private final float BACK_BUTTON_Y1 = 3.0f;
    private final float BACK_BUTTON_Y2 = 10.5f;

    //Font Generator
    private String scoreString;
    private BitmapFont scoreBitmapFont;
    private BitmapFont scoreFont;
    FreeTypeFontGenerator generator;

    public GameOverScreen(AlphaPigeon game, PlayServices playServices, DatabaseManager databaseManager, HighScore highScore){


        this.game = game;
        this.playServices = playServices;
        this.databaseManager = databaseManager;

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept
        viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);

        gameOverBackground = new Texture(Gdx.files.internal("textures/GameOverScreen.png"));

        //Initialize font generator
        scoreBitmapFont = new BitmapFont();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/univers.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        scoreFont = generator.generateFont(parameter);
        scoreFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        scoreFont.getData().setScale(0.1f);
        scoreFont.setUseIntegerPositions(false);
        DecimalFormat df = new DecimalFormat("#.##");
        scoreString = "Distance: " + df.format(highScore.score) + " m" + "\n Long: " + df.format((long) (highScore.score * 100));

        //Handle submitting high score to play services and to local databases
        handlePlayServices(highScore);
        handleLocalData(highScore);


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        gameOverDeltaTime = Gdx.graphics.getDeltaTime();
        gameOverStateTime += gameOverDeltaTime;

        // clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch and draw the game world and objects within it
        game.batch.begin();

        update();

        game.batch.draw(gameOverBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);
        scoreFont.draw(game.batch, scoreString, 29, 30);

        //Get the mouse coordinates and unproject to the world coordinates
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        //If the mouse is in bounds of the start button, show the selected start button, otherwise show the unselected start button
        //If the mouse is clicked while in the start button bounds, dispose, then start the game
        if (mousePos.x > BACK_BUTTON_X1 && mousePos.x < BACK_BUTTON_X2 && mousePos.y > BACK_BUTTON_Y1 && mousePos.y < BACK_BUTTON_Y2) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                dispose();
                game.setScreen(new MainMenuScreen(game, playServices, databaseManager));
            }
        } else {

        }


        game.batch.end();

    }

    private void handlePlayServices(HighScore highScore){

        if (playServices != null){
            //Format the score for Google Play Services and submit the score
            long highScoreFormatted = (long)(highScore.score * 100);
            playServices.submitScore(highScoreFormatted);
        }

    }

    private void handleLocalData(HighScore highScore){

        if (databaseManager != null){

            float currentScore = highScore.score;
            float currentHighScore = highScore.score;
            float numGamesPlayed = 1;

            databaseManager.insert(currentHighScore, currentScore, numGamesPlayed);

        }

    }

    private void update(){}

    @Override
    public void resize(int width, int height) {

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

    }
}
