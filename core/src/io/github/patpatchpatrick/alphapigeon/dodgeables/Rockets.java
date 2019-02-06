package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

import java.util.HashMap;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Rocket;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.RocketExplosion;
import io.github.patpatchpatrick.alphapigeon.levels.Gameplay;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

public class Rockets {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Dodgeables dodgeables;

    //Rocket variables
    private final Array<Rocket> activeRockets = new Array<Rocket>();
    private final Pool<Rocket> rocketPool;
    private Animation<TextureRegion> rocketAnimation;
    private Texture rocketSheet;
    private float lastRocketSpawnTime;
    private HashMap<Float, Float> lastSpawnTimeByLevel = new HashMap<Float, Float>();
    private final float ROCKET_ACCELERATION_TIME = 0.5f; //seconds

    //Rocket explosion variables
    private final Array<RocketExplosion> activeRocketExplosions = new Array<RocketExplosion>();
    private final Pool<RocketExplosion> rocketExplosionPool;
    private Animation<TextureRegion> rocketExplosionAnimation;
    private Texture rocketExplosionSheet;
    private final float EXPLOSION_DURATION = 0.5f; //seconds


    public Rockets(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera, Dodgeables dodgeables) {
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;
        this.dodgeables = dodgeables;

        initializeRocketAnimation();
        initializeRocketExplosionAnimation();

        rocketPool = new Pool<Rocket>() {
            @Override
            protected Rocket newObject() {
                return new Rocket(gameWorld, game, camera);
            }
        };

        rocketExplosionPool = new Pool<RocketExplosion>() {
            @Override
            protected RocketExplosion newObject() {
                return new RocketExplosion(gameWorld, game, camera);
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
                dodgeables.activeDodgeables.removeValue(rocket, false);
            }
        }

        // Render all active rocket explosions
        for (RocketExplosion rocketExplosion : activeRocketExplosions) {
            if (rocketExplosion.alive) {
                batch.draw(rocketExplosionCurrentFrame, rocketExplosion.getPosition().x, rocketExplosion.getPosition().y, 0, 0, rocketExplosion.WIDTH, rocketExplosion.HEIGHT, 1, 1, rocketExplosion.getAngle());
            } else {
                activeRocketExplosions.removeValue(rocketExplosion, false);
                dodgeables.activeDodgeables.removeValue(rocketExplosion, false);
            }
        }


    }

    public void update() {

        float currentTimeInMillis = Gameplay.totalGameTime;

        // ROCKETS
        // If rockets are spawned , accelerate them.  The X force is constant and the Y force
        // is stored on the rocket body data.  Y force depends on where the rocket was spawned (see spawnRockets method)
        // If data is null on the rocket, delete the rocket
        if (currentTimeInMillis - lastRocketSpawnTime > ROCKET_ACCELERATION_TIME) {
            for (Rocket rocket : activeRockets) {
                float forceX = -1f;
                BodyData rocketData = (BodyData) rocket.dodgeableBody.getUserData();
                if (rocketData != null) {
                    float forceY = rocketData.getRocketYForce();
                    rocket.dodgeableBody.applyForceToCenter(forceX, forceY, true);
                } else {
                    BodyData setRocketForDeletion = new BodyData(true);
                    rocket.dodgeableBody.setUserData(setRocketForDeletion);
                }
            }
        }

        // ROCKET EXPLOSIONS
        // If rocket explosions are active, check how long they've been active.
        // If they have been active longer than set time,  destroy them.
        for (RocketExplosion rocketExplosion : activeRocketExplosions) {
            BodyData rocketExplosionData = (BodyData) rocketExplosion.dodgeableBody.getUserData();
            if (rocketExplosionData != null) {
                float rocketExplosionSpawnTime = rocketExplosionData.getSpawnTime();
                if (currentTimeInMillis - rocketExplosionSpawnTime > EXPLOSION_DURATION) {
                    rocketExplosionData.setFlaggedForDelete(true);
                }
            } else {
                // If data on the rocket explosion is null, set new data on the explosion to mark it for deletion
                BodyData flagRocketForDelete = new BodyData(true);
                rocketExplosion.dodgeableBody.setUserData(flagRocketForDelete);
            }
        }


        //RECYCLE ROCKETS AND EXPLOSIONS OUT OF PLAY
        // If rockets or explosions are off the screen, free them from the pool and recycle them so they are ready
        // for reuse

        for (Rocket rocket : activeRockets) {
            if (rocket.getPosition().x < 0 - rocket.WIDTH) {
                activeRockets.removeValue(rocket, false);
                dodgeables.activeDodgeables.removeValue(rocket, false);
                rocketPool.free(rocket);
            }
        }

        for (RocketExplosion rocketExplosion : activeRocketExplosions) {
            if (rocketExplosion.getPosition().x < 0 - rocketExplosion.WIDTH) {
                activeRocketExplosions.removeValue(rocketExplosion, false);
                dodgeables.activeDodgeables.removeValue(rocketExplosion, false);
                rocketExplosionPool.free(rocketExplosion);
            }
        }

    }

    public void spawnRocket(float level) {

        // Spawn(obtain) a new rocket from the rocket pool and add to list of active rockets

        Rocket rocket = rocketPool.obtain();
        rocket.init();
        activeRockets.add(rocket);
        dodgeables.activeDodgeables.add(rocket);

        //keep track of time the rocket was spawned
        lastRocketSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastRocketSpawnTime);

        //Play rocket spawn sounds
        Sounds.rocketSpawnSound.play(SettingsManager.gameVolume);

    }

    public void spawnRocketExplosion(float explosionPositionX, float explosionPositionY) {

        // Spawn(obtain) a new rocket explosion from the rocket explosion pool and add to list of active rocket explosions

        RocketExplosion rocketExplosion = rocketExplosionPool.obtain();
        rocketExplosion.init(explosionPositionX, explosionPositionY);
        activeRocketExplosions.add(rocketExplosion);
        dodgeables.activeDodgeables.add(rocketExplosion);

        //Play rocket explosion sound
        Sounds.rocketExplosionSound.play(SettingsManager.gameVolume);


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

    public float getLastRocketSpawnTime(float level) {

        //Return the last spawn time for a given level

        if (lastSpawnTimeByLevel.get(level) == null){
            return 0;
        } else {
            return lastRocketSpawnTime;
        }

    }

    public void sweepDeadBodies() {

        // If the rocket or rocket explosion is flagged for deletion due to a collision, free the object from the pool
        // so that it moves off the screen and can be reused

        for (Rocket rocket : activeRockets) {
            if (!rocket.isActive()) {
                activeRockets.removeValue(rocket, false);
                dodgeables.activeDodgeables.removeValue(rocket, false);
                rocketPool.free(rocket);
            }
        }

        for (RocketExplosion rocketExplosion : activeRocketExplosions) {
            if (!rocketExplosion.isActive()) {
                activeRocketExplosions.removeValue(rocketExplosion, false);
                dodgeables.activeDodgeables.removeValue(rocketExplosion, false);
                rocketExplosionPool.free(rocketExplosion);
            }
        }
    }

    public void resetSpawnTimes(){
        lastRocketSpawnTime = 0;
    }

    public void dispose() {
        rocketExplosionSheet.dispose();
        rocketSheet.dispose();
    }

}
