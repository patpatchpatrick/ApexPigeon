package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Rocket;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Rockets {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //Rocket variables
    private final Array<Rocket> activeRockets = new Array<Rocket>();
    private final Pool<Rocket> rocketPool;
    private Array<Body> rocketArray = new Array<Body>();
    private Animation<TextureRegion> rocketAnimation;
    private Texture rocketSheet;
    private long lastRocketSpawnTime;
    private final float ROCKET_WIDTH = 10f;
    private final float ROCKET_HEIGHT = 20f;

    //Rocket explosion variables
    private Array<Body> rocketExplosionArray = new Array<Body>();
    private Animation<TextureRegion> rocketExplosionAnimation;
    private Texture rocketExplosionSheet;
    private long lastRocketExplosionSpawnTime;
    private final float ROCKET_EXPLOSION_WIDTH = 30f;
    private final float ROCKET_EXPLOSION_HEIGHT = 30f;

    public Rockets(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera) {
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        initializeRocketAnimation();
        initializeRocketExplosionAnimation();

        rocketPool = new Pool<Rocket>() {
            @Override
            protected Rocket newObject() {
                return new Rocket(gameWorld, game, camera);
            }
        };

    }

    public void render(float stateTime, SpriteBatch batch) {
        TextureRegion rocketCurrentFrame = rocketAnimation.getKeyFrame(stateTime, true);
        TextureRegion rocketExplosionCurrentFrame = rocketExplosionAnimation.getKeyFrame(stateTime, true);

        // Render all active rockets
        for (Rocket rocket : activeRockets) {
            if (rocket.alive) {
                batch.draw(rocketCurrentFrame, rocket.getPosition().x, rocket.getPosition().y, 0, 0, rocket.WIDTH, rocket.HEIGHT, 1, 1, rocket.getAngle());
            } else {
                activeRockets.removeValue(rocket, false);
            }
        }

        // draw all rocket explosion dodgeables using the current animation frame
        for (Body rocketExplosion : rocketExplosionArray) {
            if (rocketExplosion.isActive()) {
                batch.draw(rocketExplosionCurrentFrame, rocketExplosion.getPosition().x, rocketExplosion.getPosition().y, 0, 0, ROCKET_EXPLOSION_WIDTH, ROCKET_EXPLOSION_HEIGHT, 1, 1, MathUtils.radiansToDegrees * rocketExplosion.getAngle());
            } else {
                rocketExplosionArray.removeValue(rocketExplosion, false);
            }
        }

    }

    public void update() {

        long currentTimeInMillis = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;

        // ROCKETS
        // If rockets are spawned , accelerate them.  The X force is constant and the Y force
        // is stored on the rocket body data.  Y force depends on where the rocket was spawned (see spawnRockets method)
        if (currentTimeInMillis - lastRocketSpawnTime > 500) {
            for (Rocket rocket : activeRockets) {
                float forceX = -1f;
                BodyData rocketData = (BodyData) rocket.dodgeableBody.getUserData();
                if (rocketData != null) {
                    float forceY = rocketData.getRocketYForce();
                    rocket.dodgeableBody.applyForceToCenter(forceX, forceY, true);
                }
            }
        }

        // ROCKET EXPLOSIONS
        // If rocket explosions are active, check how long they've been active.
        // If they have been active longer than set time,  destroy them.
        for (Body rocketExplosion : rocketExplosionArray) {
            if (rocketExplosion.isActive()) {
                BodyData rocketExplosionData = (BodyData) rocketExplosion.getUserData();
                if (rocketExplosionData != null) {
                    long rocketExplosionTime = rocketExplosionData.getExplosionTime();
                    if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - rocketExplosionTime / GameVariables.MILLION_SCALE > 500) {
                        rocketExplosionData.setFlaggedForDelete(true);
                    }
                } else {
                    if (rocketExplosionData != null) {
                        rocketExplosionData.setFlaggedForDelete(true);
                    }
                }

            } else {
                rocketExplosionArray.removeValue(rocketExplosion, false);
            }
        }


        //RECYCLE ROCKETS OUT OF PLAY
        // If rockets are off the screen, free them from the pool and recycle them so they are ready
        // for reuse

        for (Rocket rocket : activeRockets) {
            if (rocket.getPosition().x < 0 - rocket.WIDTH) {
                activeRockets.removeValue(rocket, false);
                rocketPool.free(rocket);
            }
        }

    }

    public void spawnRocket() {

        // Spawn(obtain) a new rocket from the rocket pool and add to list of active rockets

        Rocket rocket = rocketPool.obtain();
        rocket.init();
        activeRockets.add(rocket);

        //keep track of time the rocket was spawned
        lastRocketSpawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;

    }

    public void spawnRocketExplosion(float explosionPositionX, float explosionPositionY) {

        //spawn a new rocket explosion
        BodyDef rocketExplosionBodyDef = new BodyDef();
        rocketExplosionBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn rocket explosion at the input position (this will be the position of the enemy that was hit
        // with the rocket.   Move the rocket to the left and downwards so it is centered on the enemy's body
        rocketExplosionBodyDef.position.set(explosionPositionX - ROCKET_WIDTH * 1.5f, explosionPositionY - ROCKET_HEIGHT / 1.5f);
        Body rocketExplosionBody = gameWorld.createBody(rocketExplosionBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/RocketExplosion.json"));
        FixtureDef rocketExplosionFixtureDef = new FixtureDef();
        rocketExplosionFixtureDef.density = 0.001f;
        rocketExplosionFixtureDef.friction = 0.5f;
        rocketExplosionFixtureDef.restitution = 0.3f;
        // set the rocket explosion filter categories and masks for collisions
        rocketExplosionFixtureDef.filter.categoryBits = game.CATEGORY_ROCKET_EXPLOSION;
        rocketExplosionFixtureDef.filter.maskBits = game.MASK_ROCKET_EXPLOSION;
        loader.attachFixture(rocketExplosionBody, "RocketExplosion", rocketExplosionFixtureDef, 30);
        rocketExplosionBody.applyForceToCenter(0, 0, true);

        //Set the time the rocket was exploded on the rocket.  This is used in the update method
        //to destroy the rocket explosion after a set amount of time
        BodyData rocketExplosionData = new BodyData(false);
        rocketExplosionData.setExplosionData(TimeUtils.nanoTime());
        rocketExplosionBody.setUserData(rocketExplosionData);

        //add rocket explosion to rocket explosions array
        rocketExplosionArray.add(rocketExplosionBody);

        //keep track of time the bird was spawned
        lastRocketExplosionSpawnTime = TimeUtils.nanoTime();


    }

    private void initializeRocketAnimation() {

        // Load the rocket sprite sheet as a Texture
        rocketSheet = new Texture(Gdx.files.internal("sprites/RocketSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(rocketSheet,
                rocketSheet.getWidth() / 8,
                rocketSheet.getHeight() / 8);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] rocketFireFrames = new TextureRegion[61 * 1];
        int index = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 8; j++) {
                rocketFireFrames[index++] = tmp[i][j];
            }
        }
        //The rocket prite region only has 61 frames, so for the last row of the 8x8 sprite grid
        // , only add 5 sprite frames
        for (int j = 0; j < 5; j++) {
            rocketFireFrames[index++] = tmp[7][j];
        }


        // Initialize the Animation with the frame interval and array of frames
        rocketAnimation = new Animation<TextureRegion>(0.02f, rocketFireFrames);

    }

    private void initializeRocketExplosionAnimation() {

        // Load the rocket explosion sprite sheet as a Texture
        rocketExplosionSheet = new Texture(Gdx.files.internal("sprites/RocketExplosionSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(rocketExplosionSheet,
                rocketExplosionSheet.getWidth() / 6,
                rocketExplosionSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] rocketExplosionFrames = new TextureRegion[6 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 6; j++) {
                rocketExplosionFrames[index++] = tmp[i][j];
            }
        }


        // Initialize the Animation with the frame interval and array of frames
        rocketExplosionAnimation = new Animation<TextureRegion>(0.15f, rocketExplosionFrames);

    }

    public long getLastRocketSpawnTime() {
        return lastRocketSpawnTime;
    }

    public void sweepDeadBodies(){

        // If the rocket is flagged for deletion due to a collision, free the rocket from the pool
        // so that it moves off the screen and can be reused

        for (Rocket rocket : activeRockets){
            if (!rocket.isActive()){
                activeRockets.removeValue(rocket, false);
                rocketPool.free(rocket);
            }
        }
    }

    public void dispose() {
        rocketExplosionSheet.dispose();
        rocketSheet.dispose();
    }

}
