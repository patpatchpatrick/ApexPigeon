package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

public class SettingsScreen implements Screen {

    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayServices playServices;
    private DatabaseManager databaseManager;

    //Variables
    private float settingsDeltaTime;
    private float settingsStateTime;

    //Textures
    private Texture settingsBackground;

    //Button Dimensions
    private final float BACK_BUTTON_X1 = 1.8f;
    private final float BACK_BUTTON_X2 = 17.7f;
    private final float BACK_BUTTON_Y1 = 3.0f;
    private final float BACK_BUTTON_Y2 = 10.5f;

    public SettingsScreen(AlphaPigeon game, PlayServices playServices, DatabaseManager databaseManager){
        this.game = game;
        this.playServices = playServices;
        this.databaseManager = databaseManager;

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept
        viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);

        settingsBackground = new Texture(Gdx.files.internal("textures/SettingsScreen.png"));

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        settingsDeltaTime = Gdx.graphics.getDeltaTime();
        settingsStateTime += settingsDeltaTime;

        // clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch and draw the game world and objects within it
        game.batch.begin();

        update();

        game.batch.draw(settingsBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);

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

    private void update(){


    }

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

        settingsBackground.dispose();

    }
}
