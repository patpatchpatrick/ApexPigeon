package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;
import sun.rmi.runtime.Log;

public class HighScoreScreen implements Screen, MobileCallbacks {

    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayServices playServices;
    private DatabaseManager databaseManager;
    private InputProcessor inputProcessorScreen;
    // -- Input Multiplexer to handle both the scrollpane(stage) and screen input processors
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();

    //Leaderboard
    private boolean leaderBoardShown = false;
    //--Types of scores to request from network/databases
    private final int GLOBAL_SCORES = 1;
    private final int LOCAL_SCORES = 2;

    //Variables
    private float highScoreDeltaTime;
    private float highScoreStateTime;

    //Textures and Buttons
    private Texture highScoreBackground;
    private Texture backButton;
    private final float BACK_BUTTON_WIDTH = 15.8f;
    private final float BACK_BUTTON_HEIGHT = 8.4f;
    private final float BACK_BUTTON_X1 = 1.8f;
    private final float BACK_BUTTON_X2 = 17.7f;
    private final float BACK_BUTTON_Y1 = 3.0f;
    private final float BACK_BUTTON_Y2 = 10.5f;
    //--Buttons for if global or local high scores are selected
    private Texture globalSelectedButton;
    private Texture localSelectedButton;
    private final float GLOBAL_LOCAL_BUTTON_X1 = 24.7f;
    private final float GLOBAL_LOCAL_BUTTON_Y1 = 36.0f;
    private final float LOCAL_BUTTON_ENDPOINT = 39.2f;
    private final float GLOBAL_LOCAL_BUTTON_WIDTH = 30.2f;
    private final float GLOBAL_LOCAL_BUTTON_HEIGHT = 4.2f;
    //--Boolean to determine if global or local button is selected. One must be always selected
    private boolean globalButtonSelected = false;
    //--Boolean to determine if scores request is needed from the network
    private boolean scoresRequestNeeded = false;


    //Font Generator
    private BitmapFont scoreBitmapFont;
    private BitmapFont scoreFont;
    FreeTypeFontGenerator generator;

    //ScrollPane
    private Stage stage;
    private FitViewport scrollPaneViewport;
    private Boolean scrollPaneCreated = false;


    public HighScoreScreen(AlphaPigeon game, PlayServices playServices, DatabaseManager databaseManager) {

        this.game = game;
        this.playServices = playServices;
        this.databaseManager = databaseManager;
        if (playServices != null) {
            playServices.setMobileCallbacks(this);
        }

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept
        viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);

        highScoreBackground = new Texture(Gdx.files.internal("textures/highscoresscreen/HighScoresScreen.png"));
        backButton = new Texture(Gdx.files.internal("textures/BackArrow.png"));
        globalSelectedButton = new Texture(Gdx.files.internal("textures/highscoresscreen/GlobalLocalButtonsGSelected.png"));
        localSelectedButton = new Texture(Gdx.files.internal("textures/highscoresscreen/GlobalLocalButtonsLSelected.png"));


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

        //Request local scores from database by default
        createInitialScoreRequest();

        //Create input processor for user controls
        createInputProcessor();

        //REMOVE THIS!!!!
        //createTestScroll();


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        highScoreDeltaTime = Gdx.graphics.getDeltaTime();
        highScoreStateTime += highScoreDeltaTime;

        // clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch and draw the game world and objects within it
        game.batch.begin();

        game.batch.draw(highScoreBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);

        game.batch.end();

        // render scrollPane for high scores
        if (scrollPaneCreated) {
            stage.draw();
            stage.act();
        }


        //Render 2nd batch of textures to display above the scrollPane
        game.batch.begin();

        game.batch.draw(backButton, BACK_BUTTON_X1, BACK_BUTTON_Y1, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
        //Render global or local button, depending on which is pushed
        game.batch.draw(globalOrLocalButton(), GLOBAL_LOCAL_BUTTON_X1, GLOBAL_LOCAL_BUTTON_Y1, GLOBAL_LOCAL_BUTTON_WIDTH, GLOBAL_LOCAL_BUTTON_HEIGHT);

        game.batch.end();

        update();


        /**
         if (!leaderBoardShown && playServices != null){
         playServices.showLeaderboard();
         leaderBoardShown = true;
         }*/


    }

    private void createTestScroll(){

        ArrayList<String> testStrings = new ArrayList<String>();
        testStrings.add(new String("NAME: Joe RANK: 1 SCORE: 95.22"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Shamrock Farms RANK: 2123123 SCORE: 95.11"));
        testStrings.add(new String("NAME: Joe RANK: 1 SCORE: 95.22"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Shamrock Farms RANK: 2123123 SCORE: 95.11"));
        testStrings.add(new String("NAME: Joe RANK: 1 SCORE: 95.22"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Shamrock Farms RANK: 2123123 SCORE: 95.11"));
        testStrings.add(new String("NAME: Joe RANK: 1 SCORE: 95.22"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Shamrock Farms RANK: 2123123 SCORE: 95.11"));
        testStrings.add(new String("NAME: Joe RANK: 1 SCORE: 95.22"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Sham RANK: 2 SCORE: 95.11"));
        testStrings.add(new String("NAME: Shamrock Farms RANK: 2123123 SCORE: 95.11"));
        createScrollPane(testStrings);



    }

    private void checkIfScoreRequestNeeded(){

        //If a score request is needed, request type of score needed
        //A callback will be received after score is queried

        if (scoresRequestNeeded){
            if (globalButtonSelected){
                requestScores(GLOBAL_SCORES);
            } else {
                requestScores(LOCAL_SCORES);
            }
            scoresRequestNeeded = false;
        }

    }

    private void update() {

        checkIfScoreRequestNeeded();

    }

    private void createInitialScoreRequest() {

        //Get player centered high scores to initiate the high scores menu

        if (databaseManager != null) {

            requestScores(LOCAL_SCORES);

        }

    }

    private void requestScores(int scoreType){

        //Request scores from network or database

            switch(scoreType) {
                case GLOBAL_SCORES:
                    //Request player centered scores from play services
                    if (playServices != null){
                        playServices.getPlayerCenteredScores();
                        Gdx.app.log("GLOBAL SCORES REQUESTED", "TEST");
                    }
                    break;
                case LOCAL_SCORES:
                    //Request player local scores from the mobile device database
                    if (databaseManager != null){
                        databaseManager.queryHighScores();
                        Gdx.app.log("LOCAL SCORES REQUESTED", "TEST");
                    }
                    break;
                default:
                    // code block
            }





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

        highScoreBackground.dispose();
        backButton.dispose();
        globalSelectedButton.dispose();
        localSelectedButton.dispose();

    }

    @Override
    public void setPlayerCenteredHighScores(final ArrayList<String> playerCenteredHighScores) {

        //Callback received from mobile device for global high scores (player centered)
        //If received, set the scrollPane to show global scores (player centered)

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                createScrollPane(playerCenteredHighScores);
            }
        });


    }

    @Override
    public void playerLocalScoresReceived(final ArrayList<String> localScores) {

        //Callback received from mobile device for local high scores (top 15)
        //If received, set the scrollPane to show local scores (top 15)

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                createScrollPane(localScores);
            }
        });



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
                camera.unproject(mousePos);

                //If the mouse is in bounds of the back button, go back to the main menu
                if (mousePos.x > BACK_BUTTON_X1 && mousePos.x < BACK_BUTTON_X2 && mousePos.y > BACK_BUTTON_Y1 && mousePos.y < BACK_BUTTON_Y2) {
                    if (button == Input.Buttons.LEFT) {
                        dispose();
                        game.setScreen(new MainMenuScreen(game, playServices, databaseManager));
                        return true;
                    }
                } else if (mousePos.x > GLOBAL_LOCAL_BUTTON_X1 && mousePos.x < LOCAL_BUTTON_ENDPOINT && mousePos.y > GLOBAL_LOCAL_BUTTON_Y1 && mousePos.y < GLOBAL_LOCAL_BUTTON_Y1 + GLOBAL_LOCAL_BUTTON_HEIGHT) {
                    if (button == Input.Buttons.LEFT) {
                        //Local button pushed
                        if (globalButtonSelected){
                            //If the button changes, a new score request is needed
                            scoresRequestNeeded = true;
                        }
                        globalButtonSelected = false;
                        return true;
                    }
                } else if (mousePos.x > LOCAL_BUTTON_ENDPOINT && mousePos.x < GLOBAL_LOCAL_BUTTON_X1 + GLOBAL_LOCAL_BUTTON_WIDTH && mousePos.y > GLOBAL_LOCAL_BUTTON_Y1 && mousePos.y < GLOBAL_LOCAL_BUTTON_Y1 + GLOBAL_LOCAL_BUTTON_HEIGHT) {
                    if (button == Input.Buttons.LEFT) {
                        //Global button pushed
                        if (!globalButtonSelected){
                            //If the button changes, a new score request is needed
                            scoresRequestNeeded = true;
                        }
                        globalButtonSelected = true;
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

        Gdx.input.setInputProcessor(inputProcessorScreen);

    }

    private Texture globalOrLocalButton() {

        if (globalButtonSelected){
            return globalSelectedButton;
        } else {
            return localSelectedButton;
        }


    }

    private void createScrollPane(ArrayList<String> scrollPaneScores) {

        scrollPaneViewport = new FitViewport(800, 480);

        stage = new Stage(scrollPaneViewport);
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        Table scrollableTable = new Table();
        scrollableTable.setFillParent(true);
        stage.addActor(scrollableTable);

        Table table = new Table();
        for (String score : scrollPaneScores) {
            table.add(new TextButton(score, skin)).row();
        }
        table.pack();
        table.setTransform(true);  //clipping enabled

        table.setOrigin(400, 0);
        table.setScale(0.75f);

        final ScrollPane scroll = new ScrollPane(table, skin);
        scrollableTable.add(scroll).expand().fill();


        stage.setDebugAll(true);


        //When stage is created, set input processor to be a multiplexer to look at both screen and stage controls
        inputMultiplexer.addProcessor(inputProcessorScreen);
        inputMultiplexer.addProcessor(stage);

        Gdx.input.setInputProcessor(inputMultiplexer);

        scrollPaneCreated = true;
    }

}
