package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.Input.Keys;

import java.util.Iterator;

public class AlphaPigeon extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	Texture img;
	private Texture squareObjectImage;
	private Texture pigeonImage;
	private Sound dropSound;
	private Music rainMusic;
	private Rectangle pigeon;
	private Array<Rectangle> squareObjects;
	private long lastDropTime;
	public ScrollingBackground scrollingBackground;


	@Override
	public void create () {

		this.scrollingBackground = new ScrollingBackground();

		// load images for the droplet and bucket
		squareObjectImage = new Texture(Gdx.files.internal("SquareObjectShape.png"));
		pigeonImage = new  Texture(Gdx.files.internal("PigeonShape.png"));

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the bucket
		pigeon = new Rectangle();
		pigeon.x = 20;
		pigeon.y = 480/2 - 50/2;
		pigeon.width = 100;
		pigeon.height = 50;

		// create the squareObjects array and spawn the first raindrop
		squareObjects = new Array<Rectangle>();
		spawnRaindrop();
	}

	private void spawnRaindrop() {
		Rectangle squareObject = new Rectangle();
		squareObject.x = 800;
		squareObject.y = MathUtils.random(0, 480-64);
		squareObject.width = 64;
		squareObject.height = 64;
		squareObjects.add(squareObject);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		// clear the screen with a dark blue color
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices
		camera.update();

		float deltaTime = Gdx.graphics.getDeltaTime();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and all drops
		batch.begin();
		scrollingBackground.updateAndRender(deltaTime, batch);
		batch.draw(pigeonImage, pigeon.x, pigeon.y);
		for(Rectangle raindrop: squareObjects) {
			batch.draw(squareObjectImage, raindrop.x, raindrop.y);
		}
		batch.end();

		// process user input
		if (Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			// the camera unproject method converts touchPos coordinates to the
			// camera coordinate system
			camera.unproject(touchPos);
			pigeon.x = touchPos.x - 100 / 2;
			pigeon.y = touchPos.y - 50 / 2;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)) pigeon.x -= 200 * deltaTime;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) pigeon.x += 200 * deltaTime;
		if(Gdx.input.isKeyPressed(Keys.UP)) pigeon.y += 200 * deltaTime;
		if(Gdx.input.isKeyPressed(Keys.DOWN)) pigeon.y -= 200 * deltaTime;


		// make sure the bucket stays within  the screen bounds
		if(pigeon.x < 0) pigeon.x = 0;
		if(pigeon.x > 800 - 100) pigeon.x = 800 - 100;
		if(pigeon.y < 0) pigeon.y = 0;
		if(pigeon.y > 480 - 50) pigeon.y = 480 - 50;


		// check if we need to create a new raindrop
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		// move the squareObjects, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the latter case we play back
		// a sound effect as well.
		for (Iterator<Rectangle> iter = squareObjects.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.x -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.x + 64 < 0) iter.remove();
			if(raindrop.overlaps(pigeon)) {
				dropSound.play();
				iter.remove();
			}
		}


	}

	@Override
	public void resize(int width, int height) {
		this.scrollingBackground.resize(width, height);
		super.resize(width, height);
	}

	@Override
	public void dispose () {
		// dispose of all the native resources
		squareObjectImage.dispose();
		pigeonImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}
