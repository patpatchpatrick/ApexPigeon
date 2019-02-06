package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.PowerUp;
import io.github.patpatchpatrick.alphapigeon.levels.Gameplay;

public class PowerUps {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Dodgeables dodgeables;

    //PowerUps
    private final Array<PowerUp> activePowerUps = new Array<PowerUp>();
    private final Pool<PowerUp> powerUpsPool;
    public static final int POWER_UP_TYPE_NONE = 0;
    //Shields make the bird invincible and kill any enemy it touches
    public static final int POWER_UP_TYPE_SHIELD = 1;
    //Skull power ups kill all active dodgeables
    public static final int POWER_UP_TYPE_SKULL = 2;
    //power up shield duration in seconds
    public static final float POWER_UP_SHIELD_DURATION = 8;

    //PowerUp Shield variables
    private Animation<TextureRegion> powerUpShieldAnimation;
    private Texture powerUpShieldSheet;
    public static float lastpowerUpShieldSpawnTime;

    //PowerUp Skull variables
    private Animation<TextureRegion> powerUpSkullAnimation;
    private Texture powerUpSkullSheet;
    public static float lastpowerUpSkullSpawnTime;


    //Shield intervals between spawns
    private final float SHIELD_INITIAL_SPAWN_INTERVAL_START_RANGE = 20; //seconds
    private final float SHIELD_INITIAL_SPAWN_INTERVAL_END_RANGE = 60; //seconds
    public static float randomSpawnIntervalTime; //The random spawn interval used to determine whether or not to spawn new powerUp

    public PowerUps(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera, Dodgeables dodgeables) {
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;
        this.dodgeables = dodgeables;

        // initialize powerup animations
        initializePowerUpShieldAnimation();
        initializePowerUpSkullAnimation();

        powerUpsPool = new Pool<PowerUp>() {
            @Override
            protected PowerUp newObject() {
                return new PowerUp(gameWorld, game, camera);
            }
        };

        // set the initial shield spawn interval to a random number between 20 seconds and 60 seconds
        randomSpawnIntervalTime = MathUtils.random(SHIELD_INITIAL_SPAWN_INTERVAL_START_RANGE, SHIELD_INITIAL_SPAWN_INTERVAL_END_RANGE);


    }

    public void render(float stateTime, SpriteBatch batch) {

        TextureRegion powerUpShieldCurrentFrame = powerUpShieldAnimation.getKeyFrame(stateTime, true);
        TextureRegion powerUpSkullCurrentFrame = powerUpSkullAnimation.getKeyFrame(stateTime, true);

        // Render all active powerups
        for (PowerUp powerUp : activePowerUps) {
            if (powerUp.alive) {
                switch (powerUp.powerUpType) {
                    case PowerUps.POWER_UP_TYPE_SHIELD:
                        batch.draw(powerUpShieldCurrentFrame, powerUp.getPosition().x, powerUp.getPosition().y,
                                0, 0, powerUp.WIDTH, powerUp.HEIGHT, 1, 1, powerUp.getAngle());
                        break;
                    case PowerUps.POWER_UP_TYPE_SKULL:
                        batch.draw(powerUpSkullCurrentFrame, powerUp.getPosition().x, powerUp.getPosition().y,
                                0, 0, powerUp.WIDTH, powerUp.HEIGHT, 1, 1, powerUp.getAngle());
                        break;
                }

            } else {
                activePowerUps.removeValue(powerUp, false);
                dodgeables.activeDodgeables.removeValue(powerUp, false);
            }
        }


    }

    public void update() {

        //Remove all powerups that are off the screen

        for (PowerUp powerUp : activePowerUps) {
            if (powerUp.getPosition().x < 0 - powerUp.WIDTH) {
                activePowerUps.removeValue(powerUp, false);
                dodgeables.activeDodgeables.removeValue(powerUp, false);
                powerUpsPool.free(powerUp);
            }
        }

    }

    public void spawnPowerUp(int powerUpType) {

        // Spawn(obtain) a new powerup from the powerups pool and add to list of active powerups

        PowerUp powerUp = powerUpsPool.obtain();
        powerUp.init(powerUpType);
        activePowerUps.add(powerUp);
        dodgeables.activeDodgeables.add(powerUp);

        //keep track of time the PowerUp was spawned
        switch (powerUpType) {
            case POWER_UP_TYPE_SHIELD:
                lastpowerUpShieldSpawnTime = Gameplay.totalGameTime;
                break;
            case POWER_UP_TYPE_SKULL:
                lastpowerUpSkullSpawnTime = Gameplay.totalGameTime;
                break;
        }


    }

    public void killAllActiveDodgeables() {

        for (Dodgeable dodgeable : dodgeables.activeDodgeables) {
            dodgeable.flagForDeletion = true;
        }

    }

    private void initializePowerUpShieldAnimation() {

        // Load the power up shield sprite sheet as a Texture
        powerUpShieldSheet = new Texture(Gdx.files.internal("sprites/PowerUpShieldSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(powerUpShieldSheet,
                powerUpShieldSheet.getWidth() / 4,
                powerUpShieldSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] powerUpShieldFrames = new TextureRegion[4 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 4; j++) {
                powerUpShieldFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        powerUpShieldAnimation = new Animation<TextureRegion>(0.08f, powerUpShieldFrames);


    }

    private void initializePowerUpSkullAnimation() {

        // Load the power up shield sprite sheet as a Texture
        powerUpSkullSheet = new Texture(Gdx.files.internal("sprites/PowerUpSkullSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(powerUpSkullSheet,
                powerUpSkullSheet.getWidth() / 4,
                powerUpSkullSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] powerUpSkullFrames = new TextureRegion[4 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 4; j++) {
                powerUpSkullFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        powerUpSkullAnimation = new Animation<TextureRegion>(0.08f, powerUpSkullFrames);


    }

    public void sweepDeadBodies() {

        // If the powerup is flagged for deletion due to a collision, free the powerup from the pool
        // so that it moves off the screen and can be reused

        for (PowerUp powerUp : activePowerUps) {
            if (!powerUp.isActive()) {
                activePowerUps.removeValue(powerUp, false);
                dodgeables.activeDodgeables.removeValue(powerUp, false);
                powerUpsPool.free(powerUp);
            }
        }

    }

    public void resetSpawnTimes(){
        lastpowerUpShieldSpawnTime = 0;
        lastpowerUpSkullSpawnTime = 0;
    }

    public void dispose() {
        powerUpShieldSheet.dispose();
        powerUpSkullSheet.dispose();
    }

}
