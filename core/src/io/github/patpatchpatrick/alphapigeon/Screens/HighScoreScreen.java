package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

public class HighScoreScreen implements Screen, MobileCallbacks {

    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayServices playServices;
    private DatabaseManager databaseManager;
    private InputProcessor inputProcessor;

    //Leaderboard
    private boolean leaderBoardShown = false;

    //Variables
    private float highScoreDeltaTime;
    private float highScoreStateTime;

    //Textures
    private Texture highScoreBackground;

    //Button Dimensions
    private final float BACK_BUTTON_X1 = 1.8f;
    private final float BACK_BUTTON_X2 = 17.7f;
    private final float BACK_BUTTON_Y1 = 3.0f;
    private final float BACK_BUTTON_Y2 = 10.5f;

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

        highScoreBackground = new Texture(Gdx.files.internal("textures/HighScoresScreen.png"));

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

        handlePlayServices();

        //Create input processor for user controls
        createInputProcessor();


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

        update();

        game.batch.draw(highScoreBackground, 0, 0, camera.viewportWidth, camera.viewportHeight);

        Gdx.input.setInputProcessor(inputProcessor);

        game.batch.end();

        if (scrollPaneCreated) {
            stage.draw();
            stage.act();
        }


        /**
         if (!leaderBoardShown && playServices != null){
         playServices.showLeaderboard();
         leaderBoardShown = true;
         }*/


    }

    private void update() {

    }

    private void handlePlayServices() {

        //Get player centered high scores

        if (playServices != null) {

            playServices.getPlayerCenteredScores();

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

    }

    @Override
    public void setPlayerCenteredHighScores(final String playerCenteredHighScores) {

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                createScrollPane(playerCenteredHighScores);
            }
        });


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
                camera.unproject(mousePos);

                //If the mouse is in bounds of the back button, go back to the main menu
                if (mousePos.x > BACK_BUTTON_X1 && mousePos.x < BACK_BUTTON_X2 && mousePos.y > BACK_BUTTON_Y1 && mousePos.y < BACK_BUTTON_Y2) {
                    if (button == Input.Buttons.LEFT) {
                        dispose();
                        game.setScreen(new MainMenuScreen(game, playServices, databaseManager));
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

    }

    private void createScrollPane(String scrollPaneScores) {

        scrollPaneViewport = new FitViewport(800, 480);

        stage = new Stage(scrollPaneViewport);
        Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        Table scrollableTable = new Table();
        scrollableTable.setFillParent(true);
        stage.addActor(scrollableTable);

        Table table = new Table();
        table.add(new TextButton(scrollPaneScores, skin)).row();
        table.pack();
        table.setTransform(true);  //clipping enabled

        table.setOrigin(400, table.getHeight() / 2);
        table.setScale(.5f);

        final ScrollPane scroll = new ScrollPane(table, skin);
        scrollableTable.add(scroll).expand().fill();

        //stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);

        scrollPaneCreated = true;
    }

}
