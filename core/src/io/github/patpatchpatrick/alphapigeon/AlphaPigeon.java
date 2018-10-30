package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input.Keys;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.resources.HighScore;
import io.github.patpatchpatrick.alphapigeon.resources.ScrollingBackground;

public class AlphaPigeon extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    public static Sound dropSound;
    private Music rainMusic;
    private Pigeon pigeon;
    private Dodgeables dodgeables;
    public ScrollingBackground scrollingBackground;
    public HighScore highScore;
    private float stateTime;


    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;

    //Variables
    private int particleAcceleration = 0;


    @Override
    public void create() {

        stateTime = 0f;

        this.scrollingBackground = new ScrollingBackground();
        this.highScore = new HighScore();
        this.pigeon = new Pigeon();
        this.dodgeables = new Dodgeables(this.pigeon);

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // start the playback of the background music immediately
        //rainMusic.setLooping(true);
        //rainMusic.play();

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        // spawn the first dodgeable
        dodgeables.spawnBackwardsPigeon();
    }

    @Override
    public void render() {
        // clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += Gdx.graphics.getDeltaTime();

        // tell the camera to update its matrices
        camera.update();

        float deltaTime = Gdx.graphics.getDeltaTime();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and all drops
        batch.begin();
        scrollingBackground.updateAndRender(deltaTime, batch);
        highScore.updateAndRender(deltaTime, batch);
        dodgeables.updateAndRender(stateTime, batch);
        pigeon.updateAndRender(stateTime, batch);

        batch.end();

        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            // the camera unproject method converts touchPos coordinates to the
            // camera coordinate system
            camera.unproject(touchPos);
            pigeon.setCoordinates(touchPos.x - 100 / 2, touchPos.y - 50 / 2);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) pigeon.moveHorizontal(-200 * deltaTime);
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) pigeon.moveHorizontal(200 * deltaTime);
        if (Gdx.input.isKeyPressed(Keys.UP)) pigeon.moveVertical(200 * deltaTime);
        if (Gdx.input.isKeyPressed(Keys.DOWN)) pigeon.moveVertical(-200 * deltaTime);


        // make sure the pigeon stays within the screen bounds
        if (pigeon.getX() < 0) pigeon.setXCoordinate(0);
        if (pigeon.getX() > 800 - 100) pigeon.setXCoordinate(800 - 100);
        if (pigeon.getY() < 0) pigeon.setYCoordinate(0);
        if (pigeon.getY() > 480 - 50) pigeon.setYCoordinate(480 - 50);


    }

    @Override
    public void resize(int width, int height) {
        this.scrollingBackground.resize(width, height);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        dropSound.dispose();
        rainMusic.dispose();
        batch.dispose();
        pigeon.dispose();
        dodgeables.dispose();
    }


}
