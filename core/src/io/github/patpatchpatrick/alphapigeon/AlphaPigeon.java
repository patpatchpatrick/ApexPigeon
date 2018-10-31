package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
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
    private Body body;
    private Body bodyAlpha;
    private BodyDef bd;

    private CircleShape circle;
    Box2DDebugRenderer debugRenderer;
    World world;


    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;

    //Variables
    private int particleAcceleration = 0;


    @Override
    public void create() {

        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();
        // First we create a body definition
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
// Set our body's starting position in the world
        bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(10, 10);
// Create our body in the world using our body definition
        bodyAlpha = world.createBody(bd);

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("AlphaPigeon.json"));

        // 2. Create a FixtureDef, as usual.`
        FixtureDef fd = new FixtureDef();
        fd.density = 0.001f;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;

        // 3. Create a Body, as usual.`
        loader.attachFixture(bodyAlpha, "AlphaPigeon", fd, 10);


// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.




        stateTime = 0f;

        this.scrollingBackground = new ScrollingBackground();
        this.highScore = new HighScore();
        this.pigeon = new Pigeon(bodyAlpha);
        this.dodgeables = new Dodgeables(this.pigeon);

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // start the playback of the background music immediately
        //rainMusic.setLooping(true);
        //rainMusic.play();

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 80, 48);
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

        debugRenderer.render(world, camera.combined);
        world.step(1/60f, 6, 2);
        this.pigeon.setCoordinates(bodyAlpha.getPosition().x, bodyAlpha.getPosition().y);



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
            bodyAlpha.applyForceToCenter(1.0f, 0.0f, true);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) bodyAlpha.applyForceToCenter(-9.0f, 0.0f, true);
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) bodyAlpha.applyForceToCenter(9.0f, 0.0f, true);
        if (Gdx.input.isKeyPressed(Keys.UP)) bodyAlpha.applyForceToCenter(0.0f, 9.0f, true);
        if (Gdx.input.isKeyPressed(Keys.DOWN)) bodyAlpha.applyForceToCenter(0.0f, -9.0f, true);


        // make sure the pigeon stays within the screen bounds

        if (bodyAlpha.getPosition().x < 0) {
            Vector2 vel = bodyAlpha.getLinearVelocity();
            vel.x = 0f;
            bodyAlpha.setLinearVelocity(vel);}
        if (bodyAlpha.getPosition().x > 80 - 10) {
            Vector2 vel = bodyAlpha.getLinearVelocity();
            vel.x = 0f;
            bodyAlpha.setLinearVelocity(vel);}
        if (bodyAlpha.getPosition().y < 0) {
            Vector2 vel = bodyAlpha.getLinearVelocity();
            vel.y = 0f;
            bodyAlpha.setLinearVelocity(vel);}
        if (bodyAlpha.getPosition().y > 48 - 5) {
            Vector2 vel = bodyAlpha.getLinearVelocity();
            vel.y = 0f;
            bodyAlpha.setLinearVelocity(vel);}








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
        circle.dispose();
    }


}
