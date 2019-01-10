package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class MainMenuScreen implements Screen {
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture mainMenuBackground;
    private Texture startButtonSelected;
    private Texture startButtonNotSelected;

    //Variables
    private final float imageScale = 10;
    private final float START_BUTTON_WIDTH = 18f;
    private final float START_BUTTON_HEIGHT = 7.2f;
    private final float START_BUTTON_X = 30;
    private final float START_BUTTON_Y = 12;

    public MainMenuScreen(AlphaPigeon game){

        this.game = game;

        //Initialize World

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept
        viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);

        mainMenuBackground = new Texture(Gdx.files.internal("textures/MainMenuScreen.png"));
        startButtonSelected = new Texture(Gdx.files.internal("textures/StartYellowSelected.png"));
        startButtonNotSelected = new Texture(Gdx.files.internal("textures/StartYellowNotSelected.png"));




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

        game.batch.draw(mainMenuBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);

        //Get the mouse coordinates and unproject to the world coordinates
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        //If the mouse is in bounds of the start button, show the selected start button, otherwise show the unselected start button
        //If the mouse is clicked while in the start button bounds, dispose, then start the game
        if (mousePos.x > START_BUTTON_X && mousePos.x < START_BUTTON_X + START_BUTTON_WIDTH && mousePos.y > START_BUTTON_Y && mousePos.y < START_BUTTON_Y +  START_BUTTON_HEIGHT ){
            game.batch.draw(startButtonSelected, START_BUTTON_X, START_BUTTON_Y, START_BUTTON_WIDTH, START_BUTTON_HEIGHT);
            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
                dispose();
                game.setScreen(new GameScreen(game, camera, viewport));
            }
        } else {
            game.batch.draw(startButtonNotSelected, START_BUTTON_X, START_BUTTON_Y, START_BUTTON_WIDTH, START_BUTTON_HEIGHT);
        }



        game.batch.end();



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
        startButtonSelected.dispose();
        mainMenuBackground.dispose();
        startButtonNotSelected.dispose();
    }

}
