package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelTwoBird;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

public class MainMenuScreen implements Screen, MobileCallbacks {
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture mainMenuBackground;
    private Texture mainMenuLogoAndText;
    private PlayServices playServices;
    private DatabaseAndPreferenceManager databaseAndPreferenceManager;

    //Input Processor
    private InputProcessor inputProcessor;

    //Variables
    private float mainMenuStateTime;
    private float mainMenuDeltaTime;

    //Icons
    private Texture soundOnIcon;
    private Texture soundOffIcon;
    private final float SOUND_ICON_WIDTH = 8;
    private final float SOUND_ICON_HEIGHT = SOUND_ICON_WIDTH;
    private Texture adRemoval;
    private final float AD_REMOVAL_WIDTH = 9.5f;
    private final float AD_REMOVAL_HEIGHT = 4.5f;
    private final float AD_REMOVAL_X2 = 80 - 1.8f;
    private final float AD_REMOVAL_X1 = AD_REMOVAL_X2 - AD_REMOVAL_WIDTH;
    private final float AD_REMOVAL_Y1 = 48f - AD_REMOVAL_HEIGHT - 3f;

    //Button Dimensions
    //--Play Button
    private final float PLAY_BUTTON_X1 = 34.5f;
    private final float PLAY_BUTTON_X2 = 45.5f;
    private final float PLAY_BUTTON_Y1 = 14.0f;
    private final float PLAY_BUTTON_Y2 = 18.0f;
    //--HighScores Button
    private final float HIGH_SCORES_BUTTON_X1 = 30.8f;
    private final float HIGH_SCORES_BUTTON_X2 = 49.5f;
    private final float HIGH_SCORES_BUTTON_Y1 = 9.5f;
    private final float HIGH_SCORES_BUTTON_Y2 = 12.0f;
    //--Settings Button
    private final float SETTINGS_BUTTON_X1 = 33.3f;
    private final float SETTINGS_BUTTON_X2 = 46.9f;
    private final float SETTINGS_BUTTON_Y1 = 4.7f;
    private final float SETTINGS_BUTTON_Y2 = 7.5f;

    //--Sound Button
    private final float SOUND_BUTTON_X1 = 80 - 9f;
    private final float SOUND_BUTTON_X2 = 80 - 2.8f;
    private final float SOUND_BUTTON_Y1 = 3f;
    private final float SOUND_BUTTON_Y2 = 8.5f;

    //Animations
    //---LevelOneBird
    private Animation<TextureRegion> levelOneBirdAnimation;
    private Texture levelOneBirdFlySheet;
    private boolean levelOneBirdPositionSet = false;
    private float levelOneBirdXPosition = 0;
    private float levelOneBirdYPosition = 0;
    private boolean levelOneBirdTwoPositionSet = false;
    private float levelOneBirdTwoXPosition = 0;
    private float levelOneBirdTwoYPosition = 0;

    //---LevelTwoBird
    private Animation<TextureRegion> levelTwoBirdAnimation;
    private Texture levelTwoBirdFlySheet;
    private boolean levelTwoBirdPositionSet = false;
    private float levelTwoBirdXPosition = 0;
    private float levelTwoBirdYPosition = 0;
    private boolean levelTwoBirdTwoPositionSet = false;
    private float levelTwoBirdTwoXPosition = 0;
    private float levelTwoBirdTwoYPosition = 0;

    //TextField
    //Variables needed for text field input
    // -- Input Multiplexer to handle both the textfield(stage) and screen input processors
    private TextField userNameTextField;
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();
    private Stage stage;
    private Viewport textFieldViewport;
    private Boolean textFieldCreated = false;
    private Skin textFieldSkin;
    //Enter Name Texture that displays when textField is empty
    private Texture enterNameTexture;
    private final float ENTER_NAME_WIDTH = 15.9f;
    private final float ENTER_NAME_HEIGHT = 8.7f;
    private final float ENTER_NAME_X1 = 1f;
    private final float ENTER_NAME_Y1 = 3f;
    private boolean userNameIsEmpty = SettingsManager.userName == "" || SettingsManager.userName.isEmpty();


    public MainMenuScreen(AlphaPigeon game, PlayServices playServices, DatabaseAndPreferenceManager databaseAndPreferenceManager) {

        this.game = game;
        this.playServices = playServices;
        this.databaseAndPreferenceManager = databaseAndPreferenceManager;
        //Set mobile callbacks on this screen to receive any callbacks from mobile device
        if (playServices != null) {
            playServices.setMobileCallbacks(this);
        }

        //Initialize World

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept

        //Update/refresh the user settings from mobile device shared preferences
        SettingsManager.updateSettings();

        //Set viewport to stretch or fit viewport depending on whether user has enabled full screen mode setting
        if (SettingsManager.fullScreenModeIsOn) {
            viewport = new StretchViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);
        } else {
            viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);
        }


        // Load textures
        mainMenuBackground = new Texture(Gdx.files.internal("textures/mainmenuscreen/MainMenuScreen.png"));
        mainMenuLogoAndText = new Texture(Gdx.files.internal("textures/mainmenuscreen/MainMenuScreenTransparent.png"));
        soundOnIcon = new Texture(Gdx.files.internal("textures/icons/SoundOnIcon.png"));
        soundOffIcon = new Texture(Gdx.files.internal("textures/icons/SoundOffIcon.png"));
        adRemoval = new Texture(Gdx.files.internal("textures/icons/AdRemoval.png"));
        enterNameTexture = new Texture(Gdx.files.internal("textures/mainmenuscreen/EnterName.png"));


        initializeLevelOneBirdAnimation();
        initializeLevelTwoBirdAnimation();

        if (playServices != null) {
            playServices.signIn();
        }

        createInputProcessor();

        //Initialize background music after updating user settings (retrieving settings from mobile device db/prefs)
        Sounds.initializeBackgroundMusic();

        if (playServices != null && !SettingsManager.adRemovalPurchased) {
            //Show ads on main menu screen
            playServices.showBannerAds(true);
        } else if (SettingsManager.adRemovalPurchased) {
            //If ad removal was purchased, do not show banner ads on the main menu screen
            if (playServices != null) {
                playServices.showBannerAds(false);
            }
        }

        createTextField();

        //TODO when releasinng on Android or mobile, re-enable this setting
        //Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        mainMenuDeltaTime = Gdx.graphics.getDeltaTime();
        mainMenuStateTime += mainMenuDeltaTime;

        // Get the animation frames for the level one and level two birds
        TextureRegion levelOneCurrentFrame = levelOneBirdAnimation.getKeyFrame(mainMenuStateTime, true);
        TextureRegion levelTwoCurrentFrame = levelTwoBirdAnimation.getKeyFrame(mainMenuStateTime, true);

        // clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the game world and objects within it
        game.batch.begin();

        game.batch.draw(mainMenuBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);

        game.batch.draw(levelOneCurrentFrame, levelOneBirdXPosition, levelOneBirdYPosition, 0, 0, LevelOneBird.WIDTH, LevelOneBird.HEIGHT, 1, 1, 0);
        game.batch.draw(levelOneCurrentFrame, levelOneBirdTwoXPosition, levelOneBirdTwoYPosition, 0, 0, LevelOneBird.WIDTH, LevelOneBird.HEIGHT, 1, 1, 0);
        game.batch.draw(levelTwoCurrentFrame, levelTwoBirdXPosition, levelTwoBirdYPosition, 0, 0, LevelTwoBird.WIDTH, LevelTwoBird.HEIGHT, 1, 1, 0);
        game.batch.draw(levelTwoCurrentFrame, levelTwoBirdTwoXPosition, levelTwoBirdTwoYPosition, 0, 0, LevelTwoBird.WIDTH, LevelTwoBird.HEIGHT, 1, 1, 0);
        game.batch.draw(mainMenuLogoAndText, 0, 0, camera.viewportWidth, camera.viewportHeight);

        //If the user has not entered a username, display the "Enter Name" image on the screen
        if (userNameIsEmpty){
            game.batch.draw(enterNameTexture, ENTER_NAME_X1, ENTER_NAME_Y1, ENTER_NAME_WIDTH, ENTER_NAME_HEIGHT);
        }

        game.batch.end();


        //Render user name text entry field
        if (textFieldCreated) {
            stage.draw();
            stage.act();
        }

        game.batch.begin();

        //If sound is on, draw the sound on icon, otherwise draw sound off icon
        if (SettingsManager.musicSettingIsOn) {
            game.batch.draw(soundOnIcon, SOUND_BUTTON_X1, SOUND_BUTTON_Y1, SOUND_ICON_WIDTH, SOUND_ICON_HEIGHT);
        } else {
            game.batch.draw(soundOffIcon, SOUND_BUTTON_X1, SOUND_BUTTON_Y1, SOUND_ICON_WIDTH, SOUND_ICON_HEIGHT);
        }

        //If ad removal is not purchased, draw the "Remove Ads" button
        if (!SettingsManager.adRemovalPurchased) {
            game.batch.draw(adRemoval, AD_REMOVAL_X1, AD_REMOVAL_Y1, AD_REMOVAL_WIDTH, AD_REMOVAL_HEIGHT);
        }


        game.batch.end();

        update();


    }

    @Override
    public void resize(int width, int height) {

        Gdx.app.log("RESIZE", "W" + width + "H" + height);

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
        mainMenuBackground.dispose();
        levelOneBirdFlySheet.dispose();
        soundOnIcon.dispose();
        soundOffIcon.dispose();
        adRemoval.dispose();
        enterNameTexture.dispose();
    }

    private void update() {

        if (!levelOneBirdPositionSet) {
            levelOneBirdXPosition = 80 + LevelOneBird.WIDTH;
            levelOneBirdYPosition = MathUtils.random(0, camera.viewportHeight - LevelOneBird.HEIGHT);
            levelOneBirdPositionSet = true;
        } else {
            levelOneBirdXPosition -= 0.25f;
        }

        if (levelOneBirdXPosition < 0 - LevelOneBird.WIDTH) {
            levelOneBirdPositionSet = false;
        }

        if (!levelOneBirdTwoPositionSet) {
            levelOneBirdTwoXPosition = 80 + 5 * LevelOneBird.WIDTH;
            levelOneBirdTwoYPosition = MathUtils.random(0, camera.viewportHeight - LevelOneBird.HEIGHT);
            levelOneBirdTwoPositionSet = true;
        } else {
            levelOneBirdTwoXPosition -= 0.25f;
        }

        if (levelOneBirdTwoXPosition < 0 - LevelOneBird.WIDTH) {
            levelOneBirdTwoPositionSet = false;
        }

        if (levelOneBirdXPosition < 0 - LevelOneBird.WIDTH) {
            levelOneBirdPositionSet = false;
        }

        if (!levelTwoBirdPositionSet) {
            levelTwoBirdXPosition = 80 + 3 * LevelTwoBird.WIDTH;
            levelTwoBirdYPosition = MathUtils.random(0, camera.viewportHeight - LevelTwoBird.HEIGHT);
            levelTwoBirdPositionSet = true;
        } else {
            levelTwoBirdXPosition -= 0.5f;
        }

        if (levelTwoBirdXPosition < 0 - LevelTwoBird.WIDTH) {
            levelTwoBirdPositionSet = false;
        }

        if (!levelTwoBirdTwoPositionSet) {
            levelTwoBirdTwoXPosition = 80 + 6 * LevelTwoBird.WIDTH;
            levelTwoBirdTwoYPosition = MathUtils.random(0, camera.viewportHeight - LevelTwoBird.HEIGHT);
            levelTwoBirdTwoPositionSet = true;
        } else {
            levelTwoBirdTwoXPosition -= 0.5f;
        }

        if (levelTwoBirdTwoXPosition < 0 - LevelTwoBird.WIDTH) {
            levelTwoBirdTwoPositionSet = false;
        }

    }

    private void initializeLevelOneBirdAnimation() {

        // Load the level one bird sprite sheet as a Texture
        levelOneBirdFlySheet = new Texture(Gdx.files.internal("sprites/LevelOneBirdBlueSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmpDiving = TextureRegion.split(levelOneBirdFlySheet,
                levelOneBirdFlySheet.getWidth() / 3,
                levelOneBirdFlySheet.getHeight() / 2);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] levelOneBirdFlyFrames = new TextureRegion[3 * 2];
        int divingIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                levelOneBirdFlyFrames[divingIndex++] = tmpDiving[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        levelOneBirdAnimation = new Animation<TextureRegion>(0.05f, levelOneBirdFlyFrames);
    }

    private void initializeLevelTwoBirdAnimation() {

        // Load the level two bird sprite sheet as a Texture
        levelTwoBirdFlySheet = new Texture(Gdx.files.internal("sprites/LevelTwoBirdBlueSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmpLevelTwo = TextureRegion.split(levelTwoBirdFlySheet,
                levelTwoBirdFlySheet.getWidth() / 4,
                levelTwoBirdFlySheet.getHeight() / 2);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] levelTwoBirdFlyFrames = new TextureRegion[4 * 2];
        int levelTwoIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                levelTwoBirdFlyFrames[levelTwoIndex++] = tmpLevelTwo[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        levelTwoBirdAnimation = new Animation<TextureRegion>(0.04f, levelTwoBirdFlyFrames);

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
                camera.unproject(mousePos, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

                //If the mouse is in bounds of any of the buttons on the screen and the buttons are clicked, open corresponding screen
                if (mousePos.x > PLAY_BUTTON_X1 && mousePos.x < PLAY_BUTTON_X2 && mousePos.y > PLAY_BUTTON_Y1 && mousePos.y < PLAY_BUTTON_Y2) {
                    if (button == Input.Buttons.LEFT) {
                        dispose();
                        game.setScreen(new GameScreen(game, playServices, databaseAndPreferenceManager));
                        return true;
                    }
                } else if (mousePos.x > HIGH_SCORES_BUTTON_X1 && mousePos.x < HIGH_SCORES_BUTTON_X2 && mousePos.y > HIGH_SCORES_BUTTON_Y1 && mousePos.y < HIGH_SCORES_BUTTON_Y2) {
                    if (button == Input.Buttons.LEFT) {
                        dispose();
                        game.setScreen(new HighScoreScreen(game, playServices, databaseAndPreferenceManager));
                        return true;
                    }
                } else if (mousePos.x > SETTINGS_BUTTON_X1 && mousePos.x < SETTINGS_BUTTON_X2 && mousePos.y > SETTINGS_BUTTON_Y1 && mousePos.y < SETTINGS_BUTTON_Y2) {
                    if (button == Input.Buttons.LEFT) {
                        dispose();
                        game.setScreen(new SettingsScreen(game, playServices, databaseAndPreferenceManager));
                        return true;
                    }
                } else if (mousePos.x > SOUND_BUTTON_X1 && mousePos.x < SOUND_BUTTON_X1 + SOUND_ICON_WIDTH &&
                        mousePos.y > SOUND_BUTTON_Y1 && mousePos.y < SOUND_BUTTON_Y1 + SOUND_ICON_HEIGHT) {
                    if (button == Input.Buttons.LEFT) {
                        //Turn background music on or off
                        if (SettingsManager.musicSettingIsOn) {
                            SettingsManager.toggleMusicSetting(false);
                            return true;
                        } else {
                            SettingsManager.toggleMusicSetting(true);
                            return true;
                        }
                    }
                } else if (mousePos.x > AD_REMOVAL_X1 && mousePos.x < AD_REMOVAL_X1 + AD_REMOVAL_WIDTH &&
                        mousePos.y > AD_REMOVAL_Y1 && mousePos.y < AD_REMOVAL_Y1 + AD_REMOVAL_HEIGHT) {
                    if (button == Input.Buttons.LEFT && !SettingsManager.adRemovalPurchased) {
                        //AD Removal Button Pushed
                        //If ads are already removed by user, nothing will happen
                        removeAds();

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
    public void requestedHighScoresReceived(ArrayList<String> playerCenteredHighScores) {

    }

    @Override
    public void requestedLocalScoresReceived(ArrayList<String> localScores) {

    }

    @Override
    public void appResumed() {
        //When the app is resumed, reload the entire screen so that it is scaled appropriately
        //If this isn't done, there is a bug in android where the screen is not scaled properly temporarily
        resetScreen();
    }

    private void resetScreen() {
        dispose();
        game.setScreen(new MainMenuScreen(game, playServices, databaseAndPreferenceManager));
    }

    private void removeAds() {

        if (playServices != null) {
            playServices.purchaseAdRemoval();
        }

    }

    private void createTextField() {

        if (!textFieldCreated) {

            //Set the textfield viewport to be stretch or fit depending no whether user has enabled full screen mode
            if (SettingsManager.fullScreenModeIsOn) {
                textFieldViewport = new StretchViewport(800, 480);
            } else {
                textFieldViewport = new FitViewport(800, 480);
            }


            stage = new Stage(textFieldViewport);
            textFieldSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        } else {
            stage.clear();
        }


        //Create a new text field and update the textfield to contain the user's name from the shared preferences by default
        userNameTextField = new TextField(SettingsManager.userName, textFieldSkin);
        userNameTextField.setFillParent(true);
        userNameTextField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {

                //Trim the spaces off the end of the user name
                String userNameString = userNameTextField.getText().trim();

                //Update the user's username in the shared prefs when it is changed
                SettingsManager.setUserName(userNameString);

                userNameIsEmpty = userNameString == "" || userNameString.isEmpty();

            }
        });

        stage.addActor(userNameTextField);

        //stage.setDebugAll(true);


        //When stage is created, set input processor to be a multiplexer to look at both screen and stage controls
        if (!textFieldCreated) {
            inputMultiplexer.addProcessor(inputProcessor);
            inputMultiplexer.addProcessor(stage);

            Gdx.input.setInputProcessor(inputMultiplexer);
        }

        textFieldCreated = true;

    }
}
