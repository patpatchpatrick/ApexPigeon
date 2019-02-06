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
import io.github.patpatchpatrick.alphapigeon.Screens.GameScreen;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.AlienMissile;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.AlienMissileCorner;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.AlienMissileCornerExplosion;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.AlienMissileExplosion;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelTwoBird;
import io.github.patpatchpatrick.alphapigeon.levels.Gameplay;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

public class AlienMissiles {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private  Dodgeables dodgeables;

    //Alien Missile variables
    private final Array<AlienMissile> activeAlienMissiles = new Array<AlienMissile>();
    private final Pool<AlienMissile> alienMissilePool;
    private Animation<TextureRegion> alienMissileAnimation;
    private Texture alienMissileSheet;
    private float lastAlienMissileSpawnTime;
    private HashMap<Float, Float> lastSpawnTimeByLevel = new HashMap<Float, Float>();
    public final float SPAWN_DIRECTION_LEFTWARD = 0f;
    public final float SPAWN_DIRECTION_UPWARD = 1f;
    public final float SPAWN_DIRECTION_RIGHTWARD = 2f;
    public final float SPAWN_DIRECTION_DOWNWARD = 3f;

    //Alien Missile Explosion variables
    private final Array<AlienMissileExplosion> activeAlienMissileExplosions = new Array<AlienMissileExplosion>();
    private final Pool<AlienMissileExplosion> alienMissileExplosionsPool;
    private Array<Body> alienMissileExplosionArray = new Array<Body>();
    private Animation<TextureRegion> alienMissileExplosionAnimation;
    private Texture alienMissileExplosionSheet;
    private float lastAlienMissileExplosionSpawnTime;

    //Alien Missile Corner variables
    private final Array<AlienMissileCorner> activeAlienMissileCorners = new Array<AlienMissileCorner>();
    private final Pool<AlienMissileCorner> alienMissileCornersPool;
    private Animation<TextureRegion> alienMissileCornerAnimation;
    private Texture alienMissileCornerSheet;
    private float lastAlienMissileCornerSpawnTime;
    private final float ALIEN_MISSILE_CORNER_EXPLOSION_FUSE_TIME = 1f;  //seconds

    //Alien Missile Corner Explosion variables
    private final Array<AlienMissileCornerExplosion> activeAlienMissileCornerExplosions = new Array<AlienMissileCornerExplosion>();
    private final Pool<AlienMissileCornerExplosion> alienMissileCornerExplosionsPool;
    private Animation<TextureRegion> alienMissileCornerExplosionAnimation;


    public AlienMissiles(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera, Dodgeables dodgeables){
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;
        this.dodgeables = dodgeables;

        initializeAlienMissileAnimation();
        initializeAlienMissileExplosionAnimation();
        initializeAlienMissileCornerAnimation();

        alienMissilePool = new Pool<AlienMissile>() {
            @Override
            protected AlienMissile newObject() {
                return new AlienMissile(gameWorld, game, camera);
            }
        };

        alienMissileExplosionsPool = new Pool<AlienMissileExplosion>() {
            @Override
            protected AlienMissileExplosion newObject() {
                return new AlienMissileExplosion(gameWorld, game, camera);
            }
        };

        alienMissileCornersPool = new Pool<AlienMissileCorner>() {
            @Override
            protected AlienMissileCorner newObject() {
                return new AlienMissileCorner(gameWorld, game, camera);
            }
        };

        alienMissileCornerExplosionsPool = new Pool<AlienMissileCornerExplosion>() {
            @Override
            protected AlienMissileCornerExplosion newObject() {
                return new AlienMissileCornerExplosion(gameWorld, game, camera);
            }
        };

    }


    public void render(float stateTime, SpriteBatch batch){

        TextureRegion alienMissileCurrentFrame = alienMissileAnimation.getKeyFrame(stateTime, true);
        TextureRegion alienMissileExplosionCurrentFrame = alienMissileExplosionAnimation.getKeyFrame(stateTime, true);
        TextureRegion alienCornerCurrentFrame = alienMissileCornerAnimation.getKeyFrame(stateTime, true);

        // Render all active alien missiles
        for (AlienMissile alienMissile : activeAlienMissiles) {
            if (alienMissile.alive) {
                batch.draw(alienMissileCurrentFrame, alienMissile.getPosition().x, alienMissile.getPosition().y, alienMissile.WIDTH / 2, alienMissile.HEIGHT / 2, alienMissile.WIDTH, alienMissile.HEIGHT, 1, 1, alienMissile.getAngle());
            } else {
                activeAlienMissiles.removeValue(alienMissile, false);
                dodgeables.activeDodgeables.removeValue(alienMissile, false);
            }
        }

        // Render all active alien missile explosions
        for (AlienMissileExplosion alienMissileExplosion : activeAlienMissileExplosions) {
            if (alienMissileExplosion.alive) {
                batch.draw(alienMissileExplosionCurrentFrame, alienMissileExplosion.getPosition().x, alienMissileExplosion.getPosition().y, 0, 0, alienMissileExplosion.WIDTH, alienMissileExplosion.HEIGHT, 1, 1, 0);
            } else {
                activeAlienMissileExplosions.removeValue(alienMissileExplosion, false);
                dodgeables.activeDodgeables.removeValue(alienMissileExplosion, false);
            }
        }

        // Render all active alien missile corners
        for (AlienMissileCorner alienMissileCorner : activeAlienMissileCorners) {
            if (alienMissileCorner.alive) {
                batch.draw(alienCornerCurrentFrame, alienMissileCorner.getPosition().x, alienMissileCorner.getPosition().y, 0, 0, alienMissileCorner.WIDTH, alienMissileCorner.HEIGHT, 1, 1, 0);
            } else {
                activeAlienMissileCorners.removeValue(alienMissileCorner, false);
                dodgeables.activeDodgeables.removeValue(alienMissileCorner, false);
            }
        }

        // Render all active alien missile corner explosions
        for (AlienMissileCornerExplosion alienMissileCornerExplosion : activeAlienMissileCornerExplosions) {
            if (alienMissileCornerExplosion.alive) {
                batch.draw(alienMissileExplosionCurrentFrame, alienMissileCornerExplosion.getPosition().x, alienMissileCornerExplosion.getPosition().y, 0, 0, alienMissileCornerExplosion.WIDTH, alienMissileCornerExplosion.HEIGHT, 1, 1, 0);
            } else {
                activeAlienMissileCornerExplosions.removeValue(alienMissileCornerExplosion, false);
                dodgeables.activeDodgeables.removeValue(alienMissileCornerExplosion, false);
            }
        }

    }

    public void update(){

        float currentTime = Gameplay.totalGameTime;

        // Alien Missile
        // If missiles are spawned , explode them after a set amount of time.
        // Exploding the missiles shoots the 4 missile corners in opposing directions away from the center of missile

        for (AlienMissile alienMissile : activeAlienMissiles){
            BodyData missileData = (BodyData) alienMissile.dodgeableBody.getUserData();
            if (missileData != null) {
                float missileSpawnTime = missileData.getSpawnTime();
                if (currentTime - missileSpawnTime > 2) {
                    missileData.setFlaggedForDelete(true);
                    spawnAlienMissileExplosion(alienMissile.getPosition().x + alienMissile.WIDTH/2, alienMissile.getPosition().y + alienMissile.HEIGHT/2);
                    spawnAlienMissileCorners(alienMissile.getPosition().x, alienMissile.getPosition().y);
                }
            } else {
                BodyData setFlagForDelete = new BodyData(true);
                alienMissile.dodgeableBody.setUserData(setFlagForDelete);
            }
        }

        // Alien Missile Explosions
        // If missiles explosions are spawned , destroy them after a set amount of time.

        for (AlienMissileExplosion alienMissileExplosion : activeAlienMissileExplosions){
            BodyData missileExplosionData = (BodyData) alienMissileExplosion.dodgeableBody.getUserData();
            if (missileExplosionData != null) {
                float missileExplosionSpawnTime = missileExplosionData.getSpawnTime();
                if (currentTime - missileExplosionSpawnTime > 0.5f) {
                    missileExplosionData.setFlaggedForDelete(true);
                }
            } else {
                BodyData setFlagForDelete = new BodyData(true);
                alienMissileExplosion.dodgeableBody.setUserData(setFlagForDelete);
            }
        }

        // Alien Corner Missile
        // If missiles are spawned , explode them after a set amount of time.

        for (AlienMissileCorner alienMissileCorner : activeAlienMissileCorners){
            BodyData missileData = (BodyData) alienMissileCorner.dodgeableBody.getUserData();
            if (missileData != null) {
                float missileSpawnTime = missileData.getSpawnTime();
                if (currentTime - missileSpawnTime > ALIEN_MISSILE_CORNER_EXPLOSION_FUSE_TIME) {
                    missileData.setFlaggedForDelete(true);
                    spawnAlienMissileCornerExplosions(alienMissileCorner.getPosition().x, alienMissileCorner.getPosition().y);
                }
            } else {
                BodyData setFlagForDelete = new BodyData(true);
                alienMissileCorner.dodgeableBody.setUserData(setFlagForDelete);
            }
        }

        // Alien Corner Missile Explosions
        // If missiles explosions are spawned , destroy them after a set amount of time.

        for (AlienMissileCornerExplosion alienMissileCornerExplosion : activeAlienMissileCornerExplosions){
            BodyData missileExplosionData = (BodyData) alienMissileCornerExplosion.dodgeableBody.getUserData();
            if (missileExplosionData != null) {
                float missileExplosionSpawnTime = missileExplosionData.getSpawnTime();
                if (currentTime - missileExplosionSpawnTime > 0.5f) {
                    missileExplosionData.setFlaggedForDelete(true);
                }
            } else {
                BodyData setFlagForDelete = new BodyData(true);
                alienMissileCornerExplosion.dodgeableBody.setUserData(setFlagForDelete);
            }
        }


    }

    public void spawnAlienMissile(float direction, float level) {

        // Spawn(obtain) a new alien missile from the alien missile pool and add to list of active alien missiles
        // Spawn the the missile in the inputted direction
        // Associate the level in which the alien missile was spawned with a spawn time

        AlienMissile alienMissile = alienMissilePool.obtain();
        if (direction == SPAWN_DIRECTION_UPWARD){
            alienMissile.initUpward();
        } else if (direction == SPAWN_DIRECTION_RIGHTWARD){
            alienMissile.initRightward();
        } else if (direction == SPAWN_DIRECTION_DOWNWARD){
            alienMissile.initDownward();
        } else {
            alienMissile.initLeftward();
        }
        activeAlienMissiles.add(alienMissile);
        dodgeables.activeDodgeables.add(alienMissile);

        //keep track of time the bird was spawned
        lastAlienMissileSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastAlienMissileSpawnTime);

    }

    public void spawnAlienMissileExplosion(float explosionPositionX, float explosionPositionY) {

        // Spawn(obtain) a new alien missile explosion from the alien missile explosion pool and add to list of active explosions

        AlienMissileExplosion alienMissileExplosion = alienMissileExplosionsPool.obtain();
        alienMissileExplosion.init(explosionPositionX, explosionPositionY);
        activeAlienMissileExplosions.add(alienMissileExplosion);
        dodgeables.activeDodgeables.add(alienMissileExplosion);

        //keep track of time the missile was spawned
        lastAlienMissileExplosionSpawnTime = Gameplay.totalGameTime;

        //play explosion sound
        Sounds.alienMissileExplosionSound.play(SettingsManager.gameVolume);

    }

    public void spawnAlienMissileCorners(float explosionPositionX, float explosionPositionY){

        //spawn new alien missile corners
        //the corners are the circular missiles that rotates the main missile.  when they are spawned,
        //they travel in 4 different opposite directions away from the main missile
        //generate a random angle theta and send the missiles in 4 opposite directions depending on random angle theta

        float theta = MathUtils.random(0, 90);

        // Spawn(obtain) a four alien missile corners from the alien corners pool and add to list of active corners

        AlienMissileCorner alienMissileCorner = alienMissileCornersPool.obtain();
        alienMissileCorner.initFirstCorner(explosionPositionX, explosionPositionY, theta);
        activeAlienMissileCorners.add(alienMissileCorner);
        dodgeables.activeDodgeables.add(alienMissileCorner);

        AlienMissileCorner alienMissileCornerTwo = alienMissileCornersPool.obtain();
        alienMissileCornerTwo.initSecondCorner(explosionPositionX, explosionPositionY, theta);
        activeAlienMissileCorners.add(alienMissileCornerTwo);
        dodgeables.activeDodgeables.add(alienMissileCornerTwo);

        AlienMissileCorner alienMissileCornerThree = alienMissileCornersPool.obtain();
        alienMissileCornerThree.initThirdCorner(explosionPositionX, explosionPositionY, theta);
        activeAlienMissileCorners.add(alienMissileCornerThree);
        dodgeables.activeDodgeables.add(alienMissileCornerThree);

        AlienMissileCorner alienMissileCornerFour = alienMissileCornersPool.obtain();
        alienMissileCornerFour.initFourthCorner(explosionPositionX, explosionPositionY, theta);
        activeAlienMissileCorners.add(alienMissileCornerFour);
        dodgeables.activeDodgeables.add(alienMissileCornerFour);
    }

    public void spawnAlienMissileCornerExplosions(float explosionPositionX, float explosionPositionY){

        // Spawn(obtain) a new alien missile corner explosion from the alien missile corner explosion pool and add to list of active alien missile explosions

        AlienMissileCornerExplosion alienMissileCornerExplosion = alienMissileCornerExplosionsPool.obtain();
        alienMissileCornerExplosion.init(explosionPositionX, explosionPositionY);
        activeAlienMissileCornerExplosions.add(alienMissileCornerExplosion);
        dodgeables.activeDodgeables.add(alienMissileCornerExplosion);

        //play explosion sound
        Sounds.alienMissileExplosionSound.play(SettingsManager.gameVolume);


    }


    private void initializeAlienMissileAnimation() {

        // Load the alien missile sprite sheet as a Texture
        alienMissileSheet = new Texture(Gdx.files.internal("sprites/AlienGrenadeSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(alienMissileSheet,
                alienMissileSheet.getWidth() / 4,
                alienMissileSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] alienFrames = new TextureRegion[4 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 4; j++) {
                alienFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        alienMissileAnimation = new Animation<TextureRegion>(0.06f, alienFrames);
    }

    private void initializeAlienMissileExplosionAnimation() {

        // Load the alien missile explosion sprite sheet as a Texture
        alienMissileExplosionSheet = new Texture(Gdx.files.internal("sprites/AlienMissileExplosionSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(alienMissileExplosionSheet,
                alienMissileExplosionSheet.getWidth() / 7,
                alienMissileExplosionSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] alienFrames = new TextureRegion[7 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 7; j++) {
                alienFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        alienMissileExplosionAnimation = new Animation<TextureRegion>(0.06f, alienFrames);

    }

    private void initializeAlienMissileCornerAnimation() {

        // Load the alien missile sprite sheet as a Texture
        alienMissileCornerSheet = new Texture(Gdx.files.internal("sprites/AlienMissileCorner.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(alienMissileCornerSheet,
                alienMissileCornerSheet.getWidth() / 1,
                alienMissileCornerSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] alienFrames = new TextureRegion[1 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                alienFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        alienMissileCornerAnimation = new Animation<TextureRegion>(1f, alienFrames);
    }

    public float getLastAlienMissileSpawnTime(float level){
        //Return the last spawn time for the given level
        if (lastSpawnTimeByLevel.get(level) == null){
            return 0;
        } else {
        return lastSpawnTimeByLevel.get(level);}
    }

    public void sweepDeadBodies(){

        // If the alien missile is flagged for deletion due to a collision, free the alien missile from the pool
        // so that it moves off the screen and can be reused

        for (AlienMissile alienMissile : activeAlienMissiles){
            if (!alienMissile.isActive()){
                activeAlienMissiles.removeValue(alienMissile, false);
                dodgeables.activeDodgeables.removeValue(alienMissile, false);
                alienMissilePool.free(alienMissile);
            }
        }

        for (AlienMissileExplosion alienMissileExplosion : activeAlienMissileExplosions){
            if (!alienMissileExplosion.isActive()){
                activeAlienMissileExplosions.removeValue(alienMissileExplosion, false);
                dodgeables.activeDodgeables.removeValue(alienMissileExplosion, false);
                alienMissileExplosionsPool.free(alienMissileExplosion);
            }
        }

        for (AlienMissileCorner alienMissileCorner : activeAlienMissileCorners){
            if (!alienMissileCorner.isActive()){
                activeAlienMissileCorners.removeValue(alienMissileCorner, false);
                dodgeables.activeDodgeables.removeValue(alienMissileCorner, false);
                alienMissileCornersPool.free(alienMissileCorner);
            }
        }

        for (AlienMissileCornerExplosion alienMissileCornerExplosion : activeAlienMissileCornerExplosions){
            if (!alienMissileCornerExplosion.isActive()){
                activeAlienMissileCornerExplosions.removeValue(alienMissileCornerExplosion, false);
                dodgeables.activeDodgeables.removeValue(alienMissileCornerExplosion, false);
                alienMissileCornerExplosionsPool.free(alienMissileCornerExplosion);
            }
        }

    }

    public void dispose(){

        alienMissileSheet.dispose();
        alienMissileExplosionSheet.dispose();
        alienMissileCornerSheet.dispose();
    }

}
