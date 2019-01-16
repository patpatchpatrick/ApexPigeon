package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelTwoBird;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

public class MainMenuScreen implements Screen {
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture mainMenuBackground;
    private Texture mainMenuLogoAndText;
    private PlayServices playServices;

    //Variables
    private final float imageScale = 10;
    private float mainMenuStateTime;
    private float mainMenuDeltaTime;

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


    public MainMenuScreen(AlphaPigeon game, PlayServices playServices) {

        this.game = game;
        this.playServices = playServices;

        //Initialize World

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept
        viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);

        mainMenuBackground = new Texture(Gdx.files.internal("textures/MainMenuScreen.png"));
        mainMenuLogoAndText = new Texture(Gdx.files.internal("textures/MainMenuScreenTransparent.png"));


        initializeLevelOneBirdAnimation();
        initializeLevelTwoBirdAnimation();

        playServices.signIn();

        /**
        if(playServices.isSignedIn()) {
            System.out.println("C:MenuState : F:MenuState Constructor : Already SignedIn Google PlayServices");

        }
        else {
            playServices.onStartMethod();
            playServices.signIn();
            System.out.println("C:MenuState : F:MenuState Constructor : SignedIn Google PlayServices");
        }
*/

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

        update();

        game.batch.draw(mainMenuBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);

        game.batch.draw(levelOneCurrentFrame, levelOneBirdXPosition, levelOneBirdYPosition, 0, 0, LevelOneBird.WIDTH, LevelOneBird.HEIGHT, 1, 1, 0);
        game.batch.draw(levelOneCurrentFrame, levelOneBirdTwoXPosition, levelOneBirdTwoYPosition, 0, 0, LevelOneBird.WIDTH, LevelOneBird.HEIGHT, 1, 1, 0);
        game.batch.draw(levelTwoCurrentFrame, levelTwoBirdXPosition, levelTwoBirdYPosition, 0, 0, LevelTwoBird.WIDTH, LevelTwoBird.HEIGHT, 1, 1, 0);
        game.batch.draw(levelTwoCurrentFrame, levelTwoBirdTwoXPosition, levelTwoBirdTwoYPosition, 0, 0, LevelTwoBird.WIDTH, LevelTwoBird.HEIGHT, 1, 1, 0);

        game.batch.draw(mainMenuLogoAndText, 0, 0, camera.viewportWidth, camera.viewportHeight);

        //Get the mouse coordinates and unproject to the world coordinates
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        //If the mouse is in bounds of any of the buttons on the screen and the buttons are clicked, open corresponding screen
        if (mousePos.x > PLAY_BUTTON_X1 && mousePos.x < PLAY_BUTTON_X2 && mousePos.y > PLAY_BUTTON_Y1 && mousePos.y < PLAY_BUTTON_Y2) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                dispose();
                game.setScreen(new GameScreen(game, playServices));
            }
        } else if (mousePos.x > HIGH_SCORES_BUTTON_X1 && mousePos.x < HIGH_SCORES_BUTTON_X2 && mousePos.y > HIGH_SCORES_BUTTON_Y1 && mousePos.y < HIGH_SCORES_BUTTON_Y2) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                dispose();
                game.setScreen(new HighScoreScreen(game, playServices));
            }
        } else if (mousePos.x > SETTINGS_BUTTON_X1 && mousePos.x < SETTINGS_BUTTON_X2 && mousePos.y > SETTINGS_BUTTON_Y1 && mousePos.y < SETTINGS_BUTTON_Y2) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                dispose();
                game.setScreen(new SettingsScreen(game,  playServices));
            }
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
        mainMenuBackground.dispose();
        levelOneBirdFlySheet.dispose();
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
            levelOneBirdTwoXPosition = 80 + 5*LevelOneBird.WIDTH;
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

}
