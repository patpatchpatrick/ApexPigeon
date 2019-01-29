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
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

public class HighScoreScreen implements Screen, MobileCallbacks {

    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayServices playServices;
    private DatabaseAndPreferenceManager databaseAndPreferenceManager;
    private InputProcessor inputProcessorScreen;
    // -- Input Multiplexer to handle both the scrollpane(stage) and screen input processors
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();

    //Variables
    private float highScoreDeltaTime;
    private float highScoreStateTime;

    //Textures and Buttons
    private Texture highScoreBackground;
    private Texture backButton;
    //--Google Play Leaderboards Button
    private Texture googlePlayLeaderboardsButton;
    private final float GOOGLE_PLAY_LEADERBOARDS_BUTTON_X1 = 71.6f;
    private final float GOOGLE_PLAY_LEADERBOARDS_BUTTON_Y1 = 41.5f;
    private final float GOOGLE_PLAY_LEADERBOARDS_BUTTON_WIDTH = 5.0f;
    private final float GOOGLE_PLAY_LEADERBOARDS_BUTTON_HEIGHT = 4.1f;
    //--Back button
    private final float BACK_BUTTON_WIDTH = 15.8f;
    private final float BACK_BUTTON_HEIGHT = 8.4f;
    private final float BACK_BUTTON_X1 = 1.8f;
    private final float BACK_BUTTON_X2 = 17.7f;
    private final float BACK_BUTTON_Y1 = 3.0f;
    private final float BACK_BUTTON_Y2 = 10.5f;
    //--Global and Local scores buttons
    private Texture globalButtonTexture;
    private Texture localButtonTexture;
    private final float LOCAL_BUTTON_X1 = 24.7f;
    private final float GLOBAL_AND_LOCAL_BUTTON_Y1 = 36.0f;
    private final float LOCAL_BUTTON_ENDPOINT = 39.2f;
    private final float GLOBAL_LOCAL_BUTTON_WIDTH = 30.2f;
    private final float GLOBAL_LOCAL_BUTTON_HEIGHT = 4.2f;
    private final float GLOBAL_BUTTON_X1 = LOCAL_BUTTON_ENDPOINT;
    private final float GLOBAL_BUTTON_ENDPOINT = LOCAL_BUTTON_X1 + GLOBAL_LOCAL_BUTTON_WIDTH;
    //--Rank and Top scores buttons
    private Texture rankButtonTexture;
    private Texture topDayButtonTexture;
    private Texture topWeekButtonTexture;
    private Texture topAllTimeButtonTexture;
    private final float RANK_TOP_BUTTON_WIDTH = 24.5f;
    private final float RANK_TOP_BUTTON_HEIGHT = 6.5f;
    private final float RANK_TOP_BUTTON_X1 = LOCAL_BUTTON_ENDPOINT;
    private final float RANK_TOP_BUTTON_Y1 = GLOBAL_AND_LOCAL_BUTTON_Y1 - RANK_TOP_BUTTON_HEIGHT + 0.2f;
    private final float RANK_BUTTON_ENDPOINT = 47.5f;
    private final float RANK_BUTTON_X1 = LOCAL_BUTTON_ENDPOINT;
    private final float RANK_AND_TOP_BUTTON_Y1 = 33.3f;
    private final float TOP_BUTTON_X1 = RANK_BUTTON_ENDPOINT;
    private final float TOP_BUTTON_ENDPOINT = LOCAL_BUTTON_X1 + GLOBAL_LOCAL_BUTTON_WIDTH;
    private final float DAY_BUTTON_X1 = RANK_BUTTON_X1;
    private final float DAY_BUTTON_ENDPOINT = 44.9f;
    private final float WEEK_BUTTON_X1 = DAY_BUTTON_ENDPOINT;
    private final float WEEK_BUTTON_ENDPOINT = 52.1f;
    private final float ALL_TIME_BUTTON_X1 = WEEK_BUTTON_ENDPOINT;
    private final float ALL_TIME_BUTTON_ENDPOINT = 63.7f;
    private final float DAY_WEEK_ALLTIME_BUTTON_Y1 = 29.7f;
    //--Integers to define which button is selected
    //--Button selected will determine type of score and request to make from network/databases
    public static final int LOCAL_BUTTON = 2;
    public static final int GLOBAL_BUTTON_RANK = 3;
    public static final int GLOBAL_BUTTON_TOP_DAY = 4;
    public static final int GLOBAL_BUTTON_TOP_WEEK = 5;
    public static final int GLOBAL_BUTTON_TOP_ALLTIME = 6;
    private int currentButtonSelected = LOCAL_BUTTON;

    //--Boolean to determine if scores request is needed from the network
    //--By default is false, unless a button is changed, then a score request for new type of button is made
    private boolean scoresRequestNeeded = false;

    //Font Generator
    private BitmapFont scoreBitmapFont;
    private BitmapFont scoreFont;
    FreeTypeFontGenerator generator;

    //ScrollPane
    private Stage stage;
    private FitViewport scrollPaneViewport;
    private Boolean scrollPaneCreated = false;
    private Skin scrollableSkin;


    public HighScoreScreen(AlphaPigeon game, PlayServices playServices, DatabaseAndPreferenceManager databaseAndPreferenceManager) {

        this.game = game;
        this.playServices = playServices;
        this.databaseAndPreferenceManager = databaseAndPreferenceManager;
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
        globalButtonTexture = new Texture(Gdx.files.internal("textures/highscoresscreen/GlobalLocalButtonsGSelected.png"));
        localButtonTexture = new Texture(Gdx.files.internal("textures/highscoresscreen/GlobalLocalButtonsLSelected.png"));
        rankButtonTexture = new Texture(Gdx.files.internal("textures/highscoresscreen/RankTopButtonRank.png"));
        topDayButtonTexture = new Texture(Gdx.files.internal("textures/highscoresscreen/RankTopButtonTopDay.png"));
        topWeekButtonTexture = new Texture(Gdx.files.internal("textures/highscoresscreen/RankTopButtonTopWeek.png"));
        topAllTimeButtonTexture = new Texture(Gdx.files.internal("textures/highscoresscreen/RankTopButtonTopAllTime.png"));
        googlePlayLeaderboardsButton = new Texture(Gdx.files.internal("textures/highscoresscreen/GooglePlayLeaderboardsButton.png"));


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
        game.batch.draw(googlePlayLeaderboardsButton, GOOGLE_PLAY_LEADERBOARDS_BUTTON_X1, GOOGLE_PLAY_LEADERBOARDS_BUTTON_Y1,
                GOOGLE_PLAY_LEADERBOARDS_BUTTON_WIDTH, GOOGLE_PLAY_LEADERBOARDS_BUTTON_HEIGHT);
        //Render global or local button, depending on which is pushed
        if (currentButtonSelected != LOCAL_BUTTON) {
            //If local button is not selected, draw the global buttons
            game.batch.draw(globalButtonTexture, LOCAL_BUTTON_X1, GLOBAL_AND_LOCAL_BUTTON_Y1, GLOBAL_LOCAL_BUTTON_WIDTH, GLOBAL_LOCAL_BUTTON_HEIGHT);
            game.batch.draw(globalRankTopButtonSelected(), RANK_TOP_BUTTON_X1, RANK_TOP_BUTTON_Y1, RANK_TOP_BUTTON_WIDTH, RANK_TOP_BUTTON_HEIGHT);
        } else {
            //If local button is selected, draw the local button
            game.batch.draw(localButtonTexture, LOCAL_BUTTON_X1, GLOBAL_AND_LOCAL_BUTTON_Y1, GLOBAL_LOCAL_BUTTON_WIDTH, GLOBAL_LOCAL_BUTTON_HEIGHT);
        }

        game.batch.end();

        update();


    }

    private void createTestScroll() {

        ArrayList<String> testStrings = new ArrayList<String>();
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        testStrings.add(new String("NAME: Joe" + "\nRANK: 1 " + "\nSCORE: 95.22"));
        testStrings.add(new String("NAME: Test blah balh !123*&^%" + "\nRANK: 1898237 " + "\nSCORE: 95.2222"));
        createScrollPane(testStrings);


    }

    private void startPlayServicesLeaderboardIntent() {

        if (playServices != null) {
            playServices.showLeaderboard();
        }

    }

    private void checkIfScoreRequestNeeded() {

        //If a score request is needed, request type of score needed
        //A callback will be received after score is queried

        if (scoresRequestNeeded) {
            requestScores();
            scoresRequestNeeded = false;
        }

    }

    private void update() {

        checkIfScoreRequestNeeded();

    }

    private void createInitialScoreRequest() {

        //Get player centered high scores to initiate the high scores menu

        if (databaseAndPreferenceManager != null) {

            requestScores();

        }

    }

    private void requestScores() {

        //Request scores from network or database depending on the button selected and type of scores needed

        switch (currentButtonSelected) {
            case GLOBAL_BUTTON_TOP_DAY:
                //Request top scores from play services leaderboard
                if (playServices != null) {
                    playServices.getTopScores(currentButtonSelected);
                }
                break;
            case GLOBAL_BUTTON_TOP_WEEK:
                //Request top scores from play services leaderboard
                if (playServices != null) {
                    playServices.getTopScores(currentButtonSelected);
                }
                break;
            case GLOBAL_BUTTON_TOP_ALLTIME:
                //Request top scores from play services leaderboard
                if (playServices != null) {
                    playServices.getTopScores(currentButtonSelected);
                }
                break;
            case GLOBAL_BUTTON_RANK:
                //Request player centered scores from play services
                if (playServices != null) {
                    playServices.getPlayerCenteredScores();
                }
                break;
            case LOCAL_BUTTON:
                //Request player local scores from the mobile device database
                if (databaseAndPreferenceManager != null) {
                    databaseAndPreferenceManager.queryHighScores();
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
        globalButtonTexture.dispose();
        localButtonTexture.dispose();
        rankButtonTexture.dispose();
        topDayButtonTexture.dispose();
        topWeekButtonTexture.dispose();
        topAllTimeButtonTexture.dispose();

    }

    @Override
    public void requestedHighScoresReceived(final ArrayList<String> playerCenteredHighScores) {

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
    public void requestedLocalScoresReceived(final ArrayList<String> localScores) {

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
                        game.setScreen(new MainMenuScreen(game, playServices, databaseAndPreferenceManager));
                        return true;
                    }
                } else if (mousePos.x > GOOGLE_PLAY_LEADERBOARDS_BUTTON_X1 && mousePos.x < GOOGLE_PLAY_LEADERBOARDS_BUTTON_X1 + GOOGLE_PLAY_LEADERBOARDS_BUTTON_WIDTH && mousePos.y > GOOGLE_PLAY_LEADERBOARDS_BUTTON_Y1 && mousePos.y < GOOGLE_PLAY_LEADERBOARDS_BUTTON_Y1 + GOOGLE_PLAY_LEADERBOARDS_BUTTON_HEIGHT) {
                    if (button == Input.Buttons.LEFT) {
                        //Google play leaderboards button pushed, open leaderboards intent if play services is not null
                        startPlayServicesLeaderboardIntent();
                        return true;
                    }
                } else if (mousePos.x > LOCAL_BUTTON_X1 && mousePos.x < LOCAL_BUTTON_ENDPOINT && mousePos.y > GLOBAL_AND_LOCAL_BUTTON_Y1 && mousePos.y < GLOBAL_AND_LOCAL_BUTTON_Y1 + GLOBAL_LOCAL_BUTTON_HEIGHT) {
                    if (button == Input.Buttons.LEFT) {
                        //Local button pushed
                        if (currentButtonSelected != LOCAL_BUTTON) {
                            //If the button changes, a new score request is needed and will be made in the update method
                            scoresRequestNeeded = true;
                        }
                        currentButtonSelected = LOCAL_BUTTON;
                        return true;
                    }
                } else if (mousePos.x > GLOBAL_BUTTON_X1 && mousePos.x < GLOBAL_BUTTON_ENDPOINT && mousePos.y > GLOBAL_AND_LOCAL_BUTTON_Y1 && mousePos.y < GLOBAL_AND_LOCAL_BUTTON_Y1 + GLOBAL_LOCAL_BUTTON_HEIGHT) {
                    if (button == Input.Buttons.LEFT) {
                        //Global button pushed... if local button is currently selected, request a
                        // global rank score request by default.  Otherwise, do nothing and leave the current
                        // global button selected
                        if (currentButtonSelected == LOCAL_BUTTON) {
                            //If the button changes, a new score request is needed and will be made in the update method
                            scoresRequestNeeded = true;
                            currentButtonSelected = GLOBAL_BUTTON_RANK;
                        }
                        return true;
                    }
                } else if (mousePos.x > RANK_BUTTON_X1 && mousePos.x < RANK_BUTTON_ENDPOINT && mousePos.y > RANK_AND_TOP_BUTTON_Y1 && mousePos.y < GLOBAL_AND_LOCAL_BUTTON_Y1) {
                    if (button == Input.Buttons.LEFT) {
                        //Rank button pushed
                        if (currentButtonSelected != GLOBAL_BUTTON_RANK) {
                            //If the button changes, a new score request is needed and will be made in the update method
                            scoresRequestNeeded = true;
                        }
                        currentButtonSelected = GLOBAL_BUTTON_RANK;
                        return true;
                    }
                } else if (mousePos.x > TOP_BUTTON_X1 && mousePos.x < TOP_BUTTON_ENDPOINT && mousePos.y > RANK_AND_TOP_BUTTON_Y1 && mousePos.y < GLOBAL_AND_LOCAL_BUTTON_Y1) {
                    if (button == Input.Buttons.LEFT) {
                        //Top button pushed
                        //If the current button pushed does not equal any of the top score buttons, push the TOP_DAY button by default
                        if (currentButtonSelected != GLOBAL_BUTTON_TOP_DAY && currentButtonSelected != GLOBAL_BUTTON_TOP_WEEK && currentButtonSelected != GLOBAL_BUTTON_TOP_ALLTIME) {
                            //If the button changes, a new score request is needed and will be made in the update method
                            scoresRequestNeeded = true;
                            currentButtonSelected = GLOBAL_BUTTON_TOP_DAY;
                        }
                        return true;
                    }
                } else if (mousePos.x > DAY_BUTTON_X1 && mousePos.x < DAY_BUTTON_ENDPOINT && mousePos.y > DAY_WEEK_ALLTIME_BUTTON_Y1 && mousePos.y < RANK_AND_TOP_BUTTON_Y1) {
                    if (button == Input.Buttons.LEFT) {
                        //Top day button pushed
                        if (currentButtonSelected != GLOBAL_BUTTON_TOP_DAY) {
                            //If the button changes, a new score request is needed and will be made in the update method
                            scoresRequestNeeded = true;
                        }
                        currentButtonSelected = GLOBAL_BUTTON_TOP_DAY;
                        return true;
                    }
                } else if (mousePos.x > WEEK_BUTTON_X1 && mousePos.x < WEEK_BUTTON_ENDPOINT && mousePos.y > DAY_WEEK_ALLTIME_BUTTON_Y1 && mousePos.y < RANK_AND_TOP_BUTTON_Y1) {
                    if (button == Input.Buttons.LEFT) {
                        //Top week button pushed
                        if (currentButtonSelected != GLOBAL_BUTTON_TOP_WEEK) {
                            //If the button changes, a new score request is needed and will be made in the update method
                            scoresRequestNeeded = true;
                        }
                        currentButtonSelected = GLOBAL_BUTTON_TOP_WEEK;
                        return true;
                    }
                } else if (mousePos.x > ALL_TIME_BUTTON_X1 && mousePos.x < ALL_TIME_BUTTON_ENDPOINT && mousePos.y > DAY_WEEK_ALLTIME_BUTTON_Y1 && mousePos.y < RANK_AND_TOP_BUTTON_Y1) {
                    if (button == Input.Buttons.LEFT) {
                        //Top all time button pushed
                        if (currentButtonSelected != GLOBAL_BUTTON_TOP_ALLTIME) {
                            //If the button changes, a new score request is needed and will be made in the update method
                            scoresRequestNeeded = true;
                        }
                        currentButtonSelected = GLOBAL_BUTTON_TOP_ALLTIME;
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

    private Texture globalRankTopButtonSelected() {

        //Depending on the current button selected, return corresponding button texture

        switch (currentButtonSelected) {
            case GLOBAL_BUTTON_TOP_DAY:
                return topDayButtonTexture;
            case GLOBAL_BUTTON_TOP_WEEK:
                return topWeekButtonTexture;
            case GLOBAL_BUTTON_TOP_ALLTIME:
                return topAllTimeButtonTexture;
            case GLOBAL_BUTTON_RANK:
                return rankButtonTexture;
        }

        return topDayButtonTexture;

    }

    private void createScrollPane(ArrayList<String> scrollPaneScores) {

        if (!scrollPaneCreated) {

            scrollPaneViewport = new FitViewport(800, 480);

            stage = new Stage(scrollPaneViewport);
            scrollableSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        } else {
            stage.clear();
        }


        Table scrollableTable = new Table();
        scrollableTable.setFillParent(true);

        stage.addActor(scrollableTable);

        Table table = new Table();
        for (String score : scrollPaneScores) {
            table.add(new TextButton(score, scrollableSkin)).row();
        }
        table.pack();
        table.setTransform(true);
        //table.setTransform(true);  //clipping enabled ... this setting makes scrolling not occur in uniform fashion

        table.setOrigin(400, 0);
        table.setScale(0.75f);


        final ScrollPane scroll = new ScrollPane(table, scrollableSkin);
        scrollableTable.add(scroll).expand().fill();


        //stage.setDebugAll(true);


        //When stage is created, set input processor to be a multiplexer to look at both screen and stage controls
        if (!scrollPaneCreated) {
            inputMultiplexer.addProcessor(inputProcessorScreen);
            inputMultiplexer.addProcessor(stage);

            Gdx.input.setInputProcessor(inputMultiplexer);
        }

        scrollPaneCreated = true;
    }

}
