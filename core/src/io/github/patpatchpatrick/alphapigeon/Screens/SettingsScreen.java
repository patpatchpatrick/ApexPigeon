package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;

public class SettingsScreen implements Screen, MobileCallbacks {

    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayServices playServices;
    private DatabaseAndPreferenceManager databaseAndPreferenceManager;
    private InputProcessor inputProcessorScreen;
    // -- Input Multiplexer to handle both the stage/sliders and screen input processors
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();

    //Sliders
    private Stage stage;
    private FitViewport sliderViewport;
    private boolean slidersInitialized = false;

    //Variables
    private final float MUSIC_VOLUME_SLIDER_X1 = 400;
    private final float MUSIC_VOLUME_SLIDER_Y1 = 300;
    private final float GAME_VOLUME_SLIDER_X1 = MUSIC_VOLUME_SLIDER_X1;
    private final float GAME_VOLUME_SLIDER_Y1 = 215;
    private final float TOUCH_SENSITIVITY_SLIDER_X1 = MUSIC_VOLUME_SLIDER_X1;
    private final float TOUCH_SENSITIVITY_SLIDER_Y1 = 125;
    private final float ACCEL_SENSITIVITY_SLIDER_X1 = MUSIC_VOLUME_SLIDER_X1;
    private final float ACCEL_SENSITIVITY_SLIDER_Y1 = 39;

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
    private final float MUSIC_BUTTON_Y1 = 32.6f;
    //...Game Sounds Button
    private final float GAME_SOUNDS_BUTTON_X1 = 45.8f;
    private final float GAME_SOUNDS_BUTTON_Y1 = 24.5f;
    //...Touch Button
    private final float TOUCH_BUTTON_X1 = 45.8f;
    private final float TOUCH_BUTTON_Y1 = 15.5f;
    //...Accelerometer Button
    private final float ACCEL_BUTTON_X1 = 45.8f;
    private final float ACCEL_BUTTON_Y1 = 6.5f;

    //...Full Screen Button
    private Texture fullScreenHeader;
    private final float FULL_SCREEN_HEADER_X1 = 61.7f;
    private final float FULL_SCREEN_HEADER_Y1 = 22.7f;
    private final float FULL_SCREEN_HEADER_WIDTH = 14.5f;
    private final float FULL_SCREEN_HEADER_HEIGHT = 2.2f;
    private final float FULL_SCREEN_BUTTON_X1 = 63.1f;
    private final float FULL_SCREEN_BUTTON_Y1 = 17.7f;


    //Button Dimensions
    private final float BACK_BUTTON_X1 = 1.8f;
    private final float BACK_BUTTON_X2 = 17.7f;
    private final float BACK_BUTTON_Y1 = 3.0f;
    private final float BACK_BUTTON_Y2 = 10.5f;

    public SettingsScreen(AlphaPigeon game, PlayServices playServices, DatabaseAndPreferenceManager databaseAndPreferenceManager) {
        this.game = game;
        this.playServices = playServices;
        this.databaseAndPreferenceManager = databaseAndPreferenceManager;

        if (playServices != null) {
            //Set the current device mobile callbacks to include this screen specific interface so
            // it can receive callbacks from the mobile device
            playServices.setMobileCallbacks(this);
        }

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept

        //Set viewport to stretch or fit viewport depending on whether user has enabled full screen mode setting
        if (SettingsManager.fullScreenModeIsOn) {
            viewport = new StretchViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);
        } else {
            viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);
        }

        settingsBackground = new Texture(Gdx.files.internal("textures/settingsscreen/SettingsScreen.png"));
        onOffButtonOnSelected = new Texture(Gdx.files.internal("textures/settingsscreen/OnOffButtonOnSelected.png"));
        onOffButtonOffSelected = new Texture(Gdx.files.internal("textures/settingsscreen/OnOffButtonOffSelected.png"));
        fullScreenHeader = new Texture(Gdx.files.internal("textures/settingsscreen/FullScreenHeader.png"));


        //Create input processor for user controls
        createInputProcessor();

        //Get the local settings that the user has set from the mobile device database/preferences
        SettingsManager.updateSettings();

        initializeSliders();

        if (playServices != null) {
            //Hide ads on settings screen
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


        game.batch.draw(settingsBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);

        //Draw music button
        game.batch.draw(drawOnOffButton(SettingsManager.musicSettingIsOn), MUSIC_BUTTON_X1, MUSIC_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);
        //Draw game sounds button
        game.batch.draw(drawOnOffButton(SettingsManager.gameSoundsSettingIsOn), GAME_SOUNDS_BUTTON_X1, GAME_SOUNDS_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);
        //Draw touch button
        game.batch.draw(drawOnOffButton(SettingsManager.touchSettingIsOn), TOUCH_BUTTON_X1, TOUCH_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);
        //Draw accel button
        game.batch.draw(drawOnOffButton(SettingsManager.accelerometerSettingIsOn), ACCEL_BUTTON_X1, ACCEL_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);
        //Draw full screen button
        game.batch.draw(fullScreenHeader, FULL_SCREEN_HEADER_X1, FULL_SCREEN_HEADER_Y1, FULL_SCREEN_HEADER_WIDTH, FULL_SCREEN_HEADER_HEIGHT);
        game.batch.draw(drawOnOffButton(SettingsManager.fullScreenModeIsOn), FULL_SCREEN_BUTTON_X1, FULL_SCREEN_BUTTON_Y1, ON_OFF_BUTTON_WIDTH, ON_OFF_BUTTON_HEIGHT);


        game.batch.end();

        // render scrollPane for high scores
        if (slidersInitialized) {
            stage.draw();
            stage.act();
        }

        update();

    }

    private void update() {


    }

    private Texture drawOnOffButton(Boolean isOn) {

        //Return on button texture if button is on
        //Return off button texture if button is off

        if (isOn) {
            return onOffButtonOnSelected;
        } else {
            return onOffButtonOffSelected;
        }

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

        settingsBackground.dispose();
        fullScreenHeader.dispose();
        onOffButtonOffSelected.dispose();
        onOffButtonOnSelected.dispose();

    }

    private void createInputProcessor() {

        inputProcessorScreen = new InputProcessor() {
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
                camera.unproject(mousePos, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

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
                        if (mousePos.x < MUSIC_BUTTON_X1 + ON_BUTTON_WIDTH) {
                            //ON pushed
                            SettingsManager.toggleMusicSetting(true);
                            return true;
                        } else {
                            //OFF pushed
                            SettingsManager.toggleMusicSetting(false);
                            return true;
                        }
                    }
                } else if (mousePos.x > GAME_SOUNDS_BUTTON_X1 && mousePos.x < GAME_SOUNDS_BUTTON_X1 + ON_OFF_BUTTON_WIDTH && mousePos.y > GAME_SOUNDS_BUTTON_Y1 && mousePos.y < GAME_SOUNDS_BUTTON_Y1 + ON_OFF_BUTTON_HEIGHT) {
                    // Game sounds button pushed
                    if (button == Input.Buttons.LEFT) {
                        if (mousePos.x < GAME_SOUNDS_BUTTON_X1 + ON_BUTTON_WIDTH) {
                            //ON pushed
                            SettingsManager.toggleGameSoundsSetting(true);
                            return true;
                        } else {
                            //OFF pushed
                            SettingsManager.toggleGameSoundsSetting(false);
                            return true;
                        }
                    }
                } else if (mousePos.x > TOUCH_BUTTON_X1 && mousePos.x < TOUCH_BUTTON_X1 + ON_OFF_BUTTON_WIDTH && mousePos.y > TOUCH_BUTTON_Y1 && mousePos.y < TOUCH_BUTTON_Y1 + ON_OFF_BUTTON_HEIGHT) {
                    // Touch button pushed
                    if (button == Input.Buttons.LEFT) {
                        if (mousePos.x < TOUCH_BUTTON_X1 + ON_BUTTON_WIDTH) {
                            //ON pushed
                            SettingsManager.toggleTouchSetting(true);
                            return true;
                        } else {
                            //OFF pushed
                            SettingsManager.toggleTouchSetting(false);
                            return true;
                        }
                    }
                } else if (mousePos.x > ACCEL_BUTTON_X1 && mousePos.x < ACCEL_BUTTON_X1 + ON_OFF_BUTTON_WIDTH && mousePos.y > ACCEL_BUTTON_Y1 && mousePos.y < ACCEL_BUTTON_Y1 + ON_OFF_BUTTON_HEIGHT) {
                    // Accel button pushed
                    if (button == Input.Buttons.LEFT) {
                        if (mousePos.x < ACCEL_BUTTON_X1 + ON_BUTTON_WIDTH) {
                            //ON pushed
                            SettingsManager.toggleAccelerometerSetting(true);
                            return true;
                        } else {
                            //OFF pushed
                            SettingsManager.toggleAccelerometerSetting(false);
                            return true;
                        }
                    }
                } else if (mousePos.x > FULL_SCREEN_BUTTON_X1 && mousePos.x < FULL_SCREEN_BUTTON_X1 + ON_OFF_BUTTON_WIDTH && mousePos.y > FULL_SCREEN_BUTTON_Y1 && mousePos.y < FULL_SCREEN_BUTTON_Y1 + ON_OFF_BUTTON_HEIGHT) {
                    // Full screen button pushed
                    if (button == Input.Buttons.LEFT) {
                        if (mousePos.x < FULL_SCREEN_BUTTON_X1 + ON_BUTTON_WIDTH) {
                            //ON pushed
                            SettingsManager.toggleFullScreenSetting(true);
                            return true;
                        } else {
                            //OFF pushed
                            SettingsManager.toggleFullScreenSetting(false);
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

    private void initializeSliders() {

        //Initialize all sliders used in settings (musicVolume, gameVolume, touchSensitivity, accelSensitivity)

        sliderViewport = new FitViewport(800, 480);
        stage = new Stage(sliderViewport);

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        createMusicVolumeSlider(skin);
        createGameVolumeSlider(skin);
        createTouchSensitivitySlider(skin);
        createAccelSensitivitySlider(skin);

        slidersInitialized = true;
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(inputProcessorScreen);

        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    private void createMusicVolumeSlider(Skin skin) {

        //Create slider to control music volume

        final Slider volumeSlider = new Slider(SettingsManager.MINIMUM_SLIDER_VALUE, SettingsManager.MAXIMUM_SLIDER_VALUE, 0.01f, false, skin);
        //Set default value to be current value set by user (Settings Manager will get value from mobile device)
        volumeSlider.setValue(SettingsManager.musicVolume);

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //If changed, provide new value to the SettingsManager to update mobile device db/prefs
                SettingsManager.toggleMusicVolumeSetting(volumeSlider.getValue());
            }
        });

        Container<Slider> container = new Container<Slider>(volumeSlider);
        container.setTransform(true);   // for enabling scaling and rotation
        container.setScale(1);  //scale according to your requirement

        Table volTable = new Table();
        volTable.add(container).width(100).height(50);
        volTable.setPosition(MUSIC_VOLUME_SLIDER_X1, MUSIC_VOLUME_SLIDER_Y1);
        stage.addActor(volTable);

    }

    private void createGameVolumeSlider(Skin skin) {

        //Create slider to control game (sound effects) volume

        final Slider volumeSlider = new Slider(SettingsManager.MINIMUM_SLIDER_VALUE, SettingsManager.MAXIMUM_SLIDER_VALUE, 0.01f, false, skin);
        //Set default value to be current value set by user (Settings Manager will get value from mobile device)
        volumeSlider.setValue(SettingsManager.gameVolume);

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //If changed, provide new value to the SettingsManager to update mobile device db/prefs
                SettingsManager.toggleGameVolumeSetting(volumeSlider.getValue());
            }
        });

        Container<Slider> container = new Container<Slider>(volumeSlider);
        container.setTransform(true);   // for enabling scaling and rotation
        container.setScale(1);  //scale according to your requirement

        Table volTable = new Table();
        volTable.add(container).width(100).height(50);
        volTable.setPosition(GAME_VOLUME_SLIDER_X1, GAME_VOLUME_SLIDER_Y1);
        stage.addActor(volTable);

    }

    private void createTouchSensitivitySlider(Skin skin) {

        //Create slider to control touch sensitivity

        final Slider touchSlider = new Slider(SettingsManager.MINIMUM_SLIDER_VALUE, SettingsManager.MAXIMUM_SLIDER_VALUE, 0.01f, false, skin);
        //Set default value to be current value set by user (Settings Manager will get value from mobile device)
        touchSlider.setValue(SettingsManager.touchSensitivity);

        touchSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //If changed, provide new value to the SettingsManager to update mobile device db/prefs
                SettingsManager.toggleTouchSensitivitySetting(touchSlider.getValue());
            }
        });

        Container<Slider> container = new Container<Slider>(touchSlider);
        container.setTransform(true);   // for enabling scaling and rotation
        container.setScale(1);  //scale according to your requirement

        Table table = new Table();
        table.add(container).width(100).height(50);
        table.setPosition(TOUCH_SENSITIVITY_SLIDER_X1, TOUCH_SENSITIVITY_SLIDER_Y1);
        stage.addActor(table);

    }

    private void createAccelSensitivitySlider(Skin skin) {

        //Create slider to control touch sensitivity

        final Slider accelSlider = new Slider(SettingsManager.MINIMUM_SLIDER_VALUE, SettingsManager.MAXIMUM_SLIDER_VALUE, 0.01f, false, skin);
        //Set default value to be current value set by user (Settings Manager will get value from mobile device)
        accelSlider.setValue(SettingsManager.accelSensitivity);

        accelSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //If changed, provide new value to the SettingsManager to update mobile device db/prefs
                SettingsManager.toggleAccelSensitivitySetting(accelSlider.getValue());
            }
        });

        Container<Slider> container = new Container<Slider>(accelSlider);
        container.setTransform(true);   // for enabling scaling and rotation
        container.setScale(1);  //scale according to your requirement

        Table table = new Table();
        table.add(container).width(100).height(50);
        table.setPosition(ACCEL_SENSITIVITY_SLIDER_X1, ACCEL_SENSITIVITY_SLIDER_Y1);
        stage.addActor(table);

    }

    @Override
    public void requestedHighScoresReceived(ArrayList<String> playerCenteredHighScores) {

    }

    @Override
    public void requestedLocalScoresReceived(ArrayList<String> localScores) {

    }

    @Override
    public void appResumed() {
        //Reload the screen when app is resumed to prevent scaling issues
        resetScreen();
    }

    private void resetScreen() {
        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run() {
                dispose();
                game.setScreen(new SettingsScreen(game, playServices, databaseAndPreferenceManager));
            }
        });
    }
}
