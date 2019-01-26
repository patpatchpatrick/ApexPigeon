package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

public class SettingsScreen implements Screen {

    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayServices playServices;
    private DatabaseAndPreferenceManager databaseAndPreferenceManager;
    private InputProcessor inputProcessor;

    //Variables
    private float settingsDeltaTime;
    private float settingsStateTime;

    //Textures
    private Texture settingsBackground;

    //Settings Buttons
    private Texture onOffButtonOnSelected;
    private Texture onOffButtonOffSelected;
    private final float ON_OFF_BUTTON_WIDTH = 10.9f;
    private final float ON_OFF_BUTTON_HEIGHT = 3.6f;
    private final float ON_BUTTON_WIDTH = 5.2f;
    //...Music Button
    private final float MUSIC_BUTTON_X1 = 38.3f;
    private final float MUSIC_BUTTON_Y1 = 32.5f;
    private boolean musicButtonIsOn = true;
    //...Game Sounds Button
    private final float GAME_SOUNDS_BUTTON_X1 = 45.8f;
    private final float GAME_SOUNDS_BUTTON_Y1 = 25.7f;
    private boolean gameSoundsButtonIsOn = true;
    //...Touch Button
    private final float TOUCH_BUTTON_X1 = 45.8f;
    private final float TOUCH_BUTTON_Y1 = 17.2f;
    private boolean touchButtonIsOn = true;
    //...Accelerometer Button
    private final float ACCEL_BUTTON_X1 = 45.8f;
    private final float ACCEL_BUTTON_Y1 = 7.5f;
    private boolean accelButtonIsOn = true;



    //Button Dimensions
    private final float BACK_BUTTON_X1 = 1.8f;
    private final float BACK_BUTTON_X2 = 17.7f;
    private final float BACK_BUTTON_Y1 = 3.0f;
    private final float BACK_BUTTON_Y2 = 10.5f;

    public SettingsScreen(AlphaPigeon game, PlayServices playServices, DatabaseAndPreferenceManager databaseAndPreferenceManager){
        this.game = game;
        this.playServices = playServices;
        this.databaseAndPreferenceManager = databaseAndPreferenceManager;

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept
        viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);

        settingsBackground = new Texture(Gdx.files.internal("textures/settingsscreen/SettingsScreen.png"));
        onOffButtonOnSelected = new Texture(Gdx.files.internal("textures/settingsscreen/OnOffButtonOnSelected.png"));
        onOffButtonOffSelected = new Texture(Gdx.files.internal("textures/settingsscreen/OnOffButtonOffSelected.png"));

        //Create input processor for user controls
        createInputProcessor();

        //Check mobile device for the current settings selected by the user
        getCurrentSettings();

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


        game.batch.draw(settingsBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);

        //Draw music button
        game.batch.draw(drawOnOffButton(musicButtonIsOn), MUSIC_BUTTON_X1, MUSIC_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);
        //Draw game sounds button
        game.batch.draw(drawOnOffButton(gameSoundsButtonIsOn), GAME_SOUNDS_BUTTON_X1, GAME_SOUNDS_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);
        //Draw touch button
        game.batch.draw(drawOnOffButton(touchButtonIsOn), TOUCH_BUTTON_X1, TOUCH_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);
        //Draw accel button
        game.batch.draw(drawOnOffButton(accelButtonIsOn), ACCEL_BUTTON_X1, ACCEL_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);

        Gdx.input.setInputProcessor(inputProcessor);

        game.batch.end();

        update();

    }

    private void getCurrentSettings(){

        //Fetch current settings from the mobile device database/preferences

        if (databaseAndPreferenceManager != null){

            musicButtonIsOn = databaseAndPreferenceManager.isMusicOn();
            gameSoundsButtonIsOn = databaseAndPreferenceManager.isGameSoundsOn();
            touchButtonIsOn = databaseAndPreferenceManager.isTouchControlsOn();
            accelButtonIsOn = databaseAndPreferenceManager.isAccelButtonOn();

        }

    }

    private void update(){


    }

    private Texture drawOnOffButton(Boolean isOn){

        //Return on button texture if button is on
        //Return off button texture if button is off

        if (isOn){
            return onOffButtonOnSelected;
        } else {
            return onOffButtonOffSelected;
        }

    }

    private void setMusicButtonOn(Boolean setOn){

        //If the button changed, update the mobile device preferences

        if (musicButtonIsOn != setOn && databaseAndPreferenceManager != null){
            databaseAndPreferenceManager.toggleMusicOnOff(setOn);
        }
        musicButtonIsOn = setOn;


    }

    private void setGameSoundsButtonOn(Boolean setOn){

        //If the button changed, update the mobile device preferences

        if (gameSoundsButtonIsOn != setOn && databaseAndPreferenceManager != null){
            databaseAndPreferenceManager.toggleGameSoundsOnOff(setOn);
        }
        gameSoundsButtonIsOn = setOn;

    }

    private void setTouchButtonOn(Boolean setOn){

        //If the button changed, update the mobile device preferences

        if (touchButtonIsOn != setOn && databaseAndPreferenceManager != null){
            databaseAndPreferenceManager.toggleTouchControlsOnOff(setOn);
        }
        touchButtonIsOn = setOn;

    }

    private void setAccelButtonOn(Boolean setOn){

        //If the button changed, update the mobile device preferences

        if (accelButtonIsOn != setOn && databaseAndPreferenceManager != null){
            databaseAndPreferenceManager.toggleAccelButtonOnOff(setOn);
        }
        accelButtonIsOn = setOn;

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

    private void createInputProcessor(){

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
                camera.unproject(mousePos);

                //If the mouse is in bounds of the back button, go back to the main menu
                if (mousePos.x > BACK_BUTTON_X1 && mousePos.x < BACK_BUTTON_X2 && mousePos.y > BACK_BUTTON_Y1 && mousePos.y < BACK_BUTTON_Y2) {
                    if (button == Input.Buttons.LEFT) {
                        dispose();
                        game.setScreen(new MainMenuScreen(game, playServices, databaseAndPreferenceManager));
                        return true;
                    }
                } else if (mousePos.x > MUSIC_BUTTON_X1 && mousePos.x < MUSIC_BUTTON_X1 + ON_OFF_BUTTON_WIDTH && mousePos.y > MUSIC_BUTTON_Y1 && mousePos.y < MUSIC_BUTTON_Y1 + ON_OFF_BUTTON_HEIGHT) {
                    // Music button pushed
                    if (button == Input.Buttons.LEFT) {
                        if (mousePos.x < MUSIC_BUTTON_X1 + ON_BUTTON_WIDTH){
                            //ON pushed
                            setMusicButtonOn(true);
                            return true;
                        } else  {
                            //OFF pushed
                            setMusicButtonOn(false);
                            return true;
                        }
                    }
                } else if (mousePos.x > GAME_SOUNDS_BUTTON_X1 && mousePos.x < GAME_SOUNDS_BUTTON_X1 + ON_OFF_BUTTON_WIDTH && mousePos.y > GAME_SOUNDS_BUTTON_Y1 && mousePos.y < GAME_SOUNDS_BUTTON_Y1 + ON_OFF_BUTTON_HEIGHT) {
                    // Game sounds button pushed
                    if (button == Input.Buttons.LEFT) {
                        if (mousePos.x < GAME_SOUNDS_BUTTON_X1 + ON_BUTTON_WIDTH){
                            //ON pushed
                            setGameSoundsButtonOn(true);
                            return true;
                        } else  {
                            //OFF pushed
                            setGameSoundsButtonOn(false);
                            return true;
                        }
                    }
                } else if (mousePos.x > TOUCH_BUTTON_X1 && mousePos.x < TOUCH_BUTTON_X1 + ON_OFF_BUTTON_WIDTH && mousePos.y > TOUCH_BUTTON_Y1 && mousePos.y < TOUCH_BUTTON_Y1 + ON_OFF_BUTTON_HEIGHT) {
                    // Touch button pushed
                    if (button == Input.Buttons.LEFT) {
                        if (mousePos.x < TOUCH_BUTTON_X1 + ON_BUTTON_WIDTH){
                            //ON pushed
                            setTouchButtonOn(true);
                            return true;
                        } else  {
                            //OFF pushed
                            setTouchButtonOn(false);
                            return true;
                        }
                    }
                } else if (mousePos.x > ACCEL_BUTTON_X1 && mousePos.x < ACCEL_BUTTON_X1 + ON_OFF_BUTTON_WIDTH && mousePos.y > ACCEL_BUTTON_Y1 && mousePos.y < ACCEL_BUTTON_Y1 + ON_OFF_BUTTON_HEIGHT) {
                    // Accel button pushed
                    if (button == Input.Buttons.LEFT) {
                        if (mousePos.x < ACCEL_BUTTON_X1 + ON_BUTTON_WIDTH){
                            //ON pushed
                            setAccelButtonOn(true);
                            return true;
                        } else  {
                            //OFF pushed
                            setAccelButtonOn(false);
                            return true;
                        }
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
}
