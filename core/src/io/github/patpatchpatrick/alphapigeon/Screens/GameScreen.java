package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.Pigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.HighScore;
import io.github.patpatchpatrick.alphapigeon.resources.ScrollingBackground;

public class GameScreen implements Screen {
    AlphaPigeon game;

    private OrthographicCamera camera;
    public static Sound dropSound;
    private Music rainMusic;
    private Pigeon pigeon;
    private Dodgeables dodgeables;
    public ScrollingBackground scrollingBackground;
    public HighScore highScore;
    private float stateTime;
    private float deltaTime;
    private Body pigeonBody;
    Box2DDebugRenderer debugRenderer;
    World world;

    //Variables
    final float PIGEON_WIDTH = 10.0f;
    final float PIGEON_HEIGHT = 5.0f;

    public GameScreen(AlphaPigeon game) {
        this.game = game;

        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();

        // set initial time to 0
        stateTime = 0f;

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 80, 48);

        // initialize game resources
        this.scrollingBackground = new ScrollingBackground();
        this.highScore = new HighScore();
        this.pigeon = new Pigeon(world, game);
        this.dodgeables = new Dodgeables(this.pigeon, world, game, camera);
        pigeonBody = this.pigeon.getBody();

        // load the game sound effects and background music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/rain.mp3"));

        // start the playback of the background music immediately
        //rainMusic.setLooping(true);
        //rainMusic.play();

        // create contact listener to listen for if the Pigeon collides with another object
        // if the pigeon collides with another object, the game is over
        createContactListener();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update the state time
        deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        // tell the camera to update its matrices
        camera.update();

        debugRenderer.render(world, camera.combined);
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch and draw the game world and objects within it
        game.batch.begin();
        scrollingBackground.render(game.batch);
        highScore.render(game.batch);
        dodgeables.render(stateTime, game.batch);
        pigeon.render(stateTime, game.batch);
        game.batch.end();

        //Update method called after rendering
        update();

    }

    @Override
    public void resize(int width, int height) {

        this.scrollingBackground.resize(width, height);

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

        // dispose of all the native resources... CALL THIS METHOD MANUALLY WHEN YOU EXIT A SCREEN
        dropSound.dispose();
        rainMusic.dispose();
        game.batch.dispose();
        pigeon.dispose();
        dodgeables.dispose();
        highScore.dispose();
        scrollingBackground.dispose();


    }

    public void update() {

        // step the world
        world.step(1 / 60f, 6, 2);

        sweepDeadBodies();

        // update all the game resources
        scrollingBackground.update(deltaTime);
        highScore.update(deltaTime);
        dodgeables.update(stateTime);

        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            // the camera unproject method converts touchPos coordinates to the
            // camera coordinate system
            camera.unproject(touchPos);
            pigeonBody.applyForceToCenter(0.3f * (touchPos.x - pigeonBody.getPosition().x), 0.3f * (touchPos.y - pigeonBody.getPosition().y), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            pigeonBody.setLinearVelocity(-30.0f, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            pigeonBody.setLinearVelocity(30.0f, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            pigeonBody.setLinearVelocity(0, 30.0f);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            pigeonBody.setLinearVelocity(0, -30.0f);
           


        // make sure the pigeon stays within the screen bounds
        if (pigeonBody.getPosition().x < 0) {
            Vector2 vel = pigeonBody.getLinearVelocity();
            vel.x = 0f;
            pigeonBody.setLinearVelocity(vel);
            pigeonBody.setTransform(new Vector2(0, pigeonBody.getPosition().y), pigeonBody.getAngle());
        }
        if (pigeonBody.getPosition().x > camera.viewportWidth - PIGEON_WIDTH) {
            Vector2 vel = pigeonBody.getLinearVelocity();
            vel.x = 0f;
            pigeonBody.setLinearVelocity(vel);
            pigeonBody.setTransform(new Vector2(camera.viewportWidth - PIGEON_WIDTH, pigeonBody.getPosition().y), pigeonBody.getAngle());
        }
        if (pigeonBody.getPosition().y < 0) {
            Vector2 vel = pigeonBody.getLinearVelocity();
            vel.y = 0f;
            pigeonBody.setLinearVelocity(vel);
            pigeonBody.setTransform(new Vector2(pigeonBody.getPosition().x, 0), pigeonBody.getAngle());
        }
        if (pigeonBody.getPosition().y > camera.viewportHeight - PIGEON_HEIGHT) {
            Vector2 vel = pigeonBody.getLinearVelocity();
            vel.y = 0f;
            pigeonBody.setLinearVelocity(vel);
            pigeonBody.setTransform(new Vector2(pigeonBody.getPosition().x, camera.viewportHeight - PIGEON_HEIGHT), pigeonBody.getAngle());
        }

    }

    private void createContactListener() {

        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {

                //If the pigeon is involved in any of the collisions, the pigeon crashed and the game is over.
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                //Boolean collision checks used to determine action to take
                Boolean pigeonInvolvedInCollision = fixtureA.getBody().equals(pigeonBody) || fixtureB.getBody().equals(pigeonBody);
                Boolean powerUpShieldInvolvedInCollision = fixtureA.getFilterData().categoryBits == game.CATEGORY_POWERUP_SHIELD || fixtureB.getFilterData().categoryBits == game.CATEGORY_POWERUP_SHIELD;
                Boolean powerUpInvolvedInCollision = powerUpShieldInvolvedInCollision;


                short powerUpType;
                if (powerUpShieldInvolvedInCollision) {
                    powerUpType = game.CATEGORY_POWERUP_SHIELD;
                    // destroy the power up body
                    if (fixtureA.getFilterData().categoryBits == game.CATEGORY_POWERUP_SHIELD) {
                        fixtureA.getBody().setUserData(new BodyData(true));
                    } else if (fixtureB.getFilterData().categoryBits == game.CATEGORY_POWERUP_SHIELD) {
                        fixtureB.getBody().setUserData(new BodyData(true));
                    }
                } else {
                    //TODO figure out else
                    powerUpType = game.CATEGORY_PIGEON;
                }


                //If pigeon contacts a power up, power up the pigeon, otherwise if pigeon contacts a different object, the game is over
                if (pigeonInvolvedInCollision && powerUpInvolvedInCollision) {
                    pigeon.powerUp(powerUpType);

                } else if (pigeonInvolvedInCollision) {
                    gameOver();
                }

            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                //Gdx.app.log("endContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });

    }

    private void gameOver() {

        // bird has crashed, game is over
        // stop counting the high score
        highScore.stopCounting();
    }

    public void sweepDeadBodies() {

        //Go through and remove all bodies flagged for deletion

        Array bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Iterator<Body> iter = bodies.iterator(); iter.hasNext(); ) {
            Body body = iter.next();
            if (body != null) {
                BodyData data = (BodyData) body.getUserData();
                if (data != null){
                    if (data.isFlaggedForDelete()) {
                        world.destroyBody(body);
                        //body.setUserData(null);
                        body = null;
                    }
                }
            }
        }
    }

}
