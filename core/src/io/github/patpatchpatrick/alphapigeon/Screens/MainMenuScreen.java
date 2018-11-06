package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;

public class MainMenuScreen implements Screen {
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Texture mainMenuBackground;
    private Texture startButton;

    public MainMenuScreen(AlphaPigeon game){
        this.game = game;

        //Initialize World

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 80, 48);

        mainMenuBackground = new Texture(Gdx.files.internal("textures/MainMenuScreen.png"));
        startButton = new Texture(Gdx.files.internal("textures/Start.png"));





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
        game.batch.draw(startButton, 22, 15, 32, 8);


        game.batch.end();

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
        startButton.dispose();
        mainMenuBackground.dispose();

    }
}
