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

import java.util.HashMap;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBirdReverse;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelTwoBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelTwoBirdReverse;
import io.github.patpatchpatrick.alphapigeon.levels.Gameplay;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Birds {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Dodgeables dodgeables;

    //Spawn variables
    public final static float VERT_POSITION_RANDOM = -1;

    //Level One Bird variables
    private final Array<LevelOneBird> activeLevelOneBirds = new Array<LevelOneBird>();
    private final Pool<LevelOneBird> levelOneBirdPool;
    private Animation<TextureRegion> levelOneBirdAnimation;
    private Texture levelOneBirdFlySheet;
    private float lastLevelOneBirdSpawnTime;


    //Level One Bird (Reverse) variables
    private final Array<LevelOneBirdReverse> activeLevelOneBirdReverses = new Array<LevelOneBirdReverse>();
    private final Pool<LevelOneBirdReverse> levelOneBirdReversePool;
    private float lastLevelOneBirdReverseSpawnTime;


    //Level Two Bird variables
    private Array<LevelTwoBird> activeLevelTwoBirds = new Array<LevelTwoBird>();
    private final Pool<LevelTwoBird> levelTwoBirdPool;
    private Animation<TextureRegion> levelTwoBirdAnimation;
    private Texture levelTwoBirdFlySheet;
    private float lastLevelTwoBirdSpawnTime;


    //Level Two Bird (Reverse) variables
    private final Array<LevelTwoBirdReverse> activeLevelTwoBirdReverses = new Array<LevelTwoBirdReverse>();
    private final Pool<LevelTwoBirdReverse> levelTwoBirdReversePool;
    private float lastLevelTwoBirdReverseSpawnTime;



    public Birds(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera, Dodgeables dodgeables) {

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;
        this.dodgeables = dodgeables;

        initializeLevelOneBirdAnimation();
        initializeLevelTwoBirdAnimation();

        levelOneBirdPool = new Pool<LevelOneBird>() {
            @Override
            protected LevelOneBird newObject() {
                return new LevelOneBird(gameWorld, game, camera);
            }
        };

        levelOneBirdReversePool = new Pool<LevelOneBirdReverse>() {
            @Override
            protected LevelOneBirdReverse newObject() {
                return new LevelOneBirdReverse(gameWorld, game, camera);
            }
        };

        levelTwoBirdPool = new Pool<LevelTwoBird>() {
            @Override
            protected LevelTwoBird newObject() {
                return new LevelTwoBird(gameWorld, game, camera);
            }
        };

        levelTwoBirdReversePool = new Pool<LevelTwoBirdReverse>() {
            @Override
            protected LevelTwoBirdReverse newObject() {
                return new LevelTwoBirdReverse(gameWorld, game, camera);
            }
        };



    }

    public void render(float stateTime, SpriteBatch batch) {

        // Get the animation frames for the level one and level two birds
        TextureRegion levelOneCurrentFrame = levelOneBirdAnimation.getKeyFrame(stateTime, true);
        TextureRegion levelTwoCurrentFrame = levelTwoBirdAnimation.getKeyFrame(stateTime, true);

        // Render all active level one birds
        for (LevelOneBird levelOneBird : activeLevelOneBirds) {
            if (levelOneBird.alive) {
                batch.draw(levelOneCurrentFrame, levelOneBird.getPosition().x, levelOneBird.getPosition().y, 0, 0, levelOneBird.WIDTH, levelOneBird.HEIGHT, 1, 1, levelOneBird.getAngle());
            } else {
                activeLevelOneBirds.removeValue(levelOneBird, false);
                dodgeables.activeDodgeables.removeValue(levelOneBird, false);
            }
        }

        // Render all active level one birds(reversed)
        for (LevelOneBirdReverse levelOneBirdReverse : activeLevelOneBirdReverses) {
            if (levelOneBirdReverse.alive) {
                //X scale is -1 because bird is reversed
                batch.draw(levelOneCurrentFrame, levelOneBirdReverse.getPosition().x + levelOneBirdReverse.WIDTH, levelOneBirdReverse.getPosition().y, 0, 0, levelOneBirdReverse.WIDTH, levelOneBirdReverse.HEIGHT, -1, 1, levelOneBirdReverse.getAngle());
            } else {
                activeLevelOneBirdReverses.removeValue(levelOneBirdReverse, false);
                dodgeables.activeDodgeables.removeValue(levelOneBirdReverse, false);
            }
        }

        // Render all active level two birds
        for (LevelTwoBird levelTwoBird : activeLevelTwoBirds) {
            if (levelTwoBird.alive) {
                batch.draw(levelTwoCurrentFrame, levelTwoBird.getPosition().x, levelTwoBird.getPosition().y - 2f, 0, 2, levelTwoBird.WIDTH, levelTwoBird.HEIGHT, 1, 1, levelTwoBird.getAngle());
            } else {
                activeLevelTwoBirds.removeValue(levelTwoBird, false);
                dodgeables.activeDodgeables.removeValue(levelTwoBird, false);
            }
        }

        // Render all active level two birds (reversed)
        for (LevelTwoBirdReverse levelTwoBirdReverse : activeLevelTwoBirdReverses) {
            if (levelTwoBirdReverse.alive) {
                //X scale is -1 because bird if reversed
                batch.draw(levelTwoCurrentFrame, levelTwoBirdReverse.getPosition().x + levelTwoBirdReverse.WIDTH, levelTwoBirdReverse.getPosition().y - 2f, 0, 2, levelTwoBirdReverse.WIDTH, levelTwoBirdReverse.HEIGHT, -1, 1, levelTwoBirdReverse.getAngle());
            } else {
                activeLevelTwoBirdReverses.removeValue(levelTwoBirdReverse, false);
                dodgeables.activeDodgeables.removeValue(levelTwoBirdReverse, false);
            }
        }

    }

    public void update() {

        // For all level one and level two birds that are off the game screen, free the birds in the pool
        // so that they can be reused 

        for (LevelOneBird levelOneBird : activeLevelOneBirds) {
            if (levelOneBird.getPosition().x < 0 - levelOneBird.WIDTH || levelOneBird.getPosition().x > camera.viewportWidth + levelOneBird.WIDTH || levelOneBird.getPosition().y < 0 - 2 * levelOneBird.HEIGHT) {
                activeLevelOneBirds.removeValue(levelOneBird, false);
                dodgeables.activeDodgeables.removeValue(levelOneBird, false);
                levelOneBirdPool.free(levelOneBird);
            }
        }

        for (LevelOneBirdReverse levelOneBirdReverse : activeLevelOneBirdReverses) {
            if (levelOneBirdReverse.getPosition().x < 0 - levelOneBirdReverse.WIDTH || levelOneBirdReverse.getPosition().x > camera.viewportWidth + levelOneBirdReverse.WIDTH || levelOneBirdReverse.getPosition().y < 0 - 2 * levelOneBirdReverse.HEIGHT) {
                activeLevelOneBirdReverses.removeValue(levelOneBirdReverse, false);
                dodgeables.activeDodgeables.removeValue(levelOneBirdReverse, false);
                levelOneBirdReversePool.free(levelOneBirdReverse);
            }
        }

        for (LevelTwoBird levelTwoBird : activeLevelTwoBirds) {
            if (levelTwoBird.getPosition().x < 0 - levelTwoBird.WIDTH || levelTwoBird.getPosition().x > camera.viewportWidth + levelTwoBird.WIDTH || levelTwoBird.getPosition().y < 0 - 2 * levelTwoBird.HEIGHT) {
                activeLevelTwoBirds.removeValue(levelTwoBird, false);
                dodgeables.activeDodgeables.removeValue(levelTwoBird, false);
                levelTwoBirdPool.free(levelTwoBird);
            }
        }

        for (LevelTwoBirdReverse levelTwoBirdReverse : activeLevelTwoBirdReverses) {
            if (levelTwoBirdReverse.getPosition().x < 0 - levelTwoBirdReverse.WIDTH || levelTwoBirdReverse.getPosition().x > camera.viewportWidth + levelTwoBirdReverse.WIDTH || levelTwoBirdReverse.getPosition().y < 0 - 2 * levelTwoBirdReverse.HEIGHT) {
                activeLevelTwoBirdReverses.removeValue(levelTwoBirdReverse, false);
                dodgeables.activeDodgeables.removeValue(levelTwoBirdReverse, false);
                levelTwoBirdReversePool.free(levelTwoBirdReverse);
            }
        }


    }

    public void spawnLevelOneBird(float totalGameTime) {

        // Spawn(obtain) a new bird from the level one bird pool and add to list of active birds

        LevelOneBird levelOneBird = levelOneBirdPool.obtain();
        levelOneBird.init(totalGameTime, VERT_POSITION_RANDOM);
        activeLevelOneBirds.add(levelOneBird);
        dodgeables.activeDodgeables.add(levelOneBird);

        //keep track of time the bird was spawned
        lastLevelOneBirdSpawnTime = Gameplay.totalGameTime;
        Gdx.app.log("BIRDSPAWNTIME", "" + lastLevelOneBirdSpawnTime);

    }

    public void spawnLevelOneBirdReverse(float totalGameTime) {

        // Spawn(obtain) a new bird from the level one bird pool and add to list of active birds

        LevelOneBirdReverse levelOneBirdReverse = levelOneBirdReversePool.obtain();
        levelOneBirdReverse.init(totalGameTime);
        activeLevelOneBirdReverses.add(levelOneBirdReverse);
        dodgeables.activeDodgeables.add(levelOneBirdReverse);

        //keep track of time the bird was spawned
        lastLevelOneBirdReverseSpawnTime = Gameplay.totalGameTime;

    }

    public void spawnLevelTwoBird(float totalGameTime) {

        // Spawn(obtain) a new bird from the level two bird pool and add to list of active birds

        LevelTwoBird levelTwoBird = levelTwoBirdPool.obtain();
        levelTwoBird.init(totalGameTime);
        activeLevelTwoBirds.add(levelTwoBird);
        dodgeables.activeDodgeables.add(levelTwoBird);

        //keep track of time the bird was spawned
        lastLevelTwoBirdSpawnTime = Gameplay.totalGameTime;
    }

    public void spawnLevelTwoBirdReverse(float totalGameTime) {

        // Spawn(obtain) a new bird from the level two reverse bird pool and add to list of active birds

        LevelTwoBirdReverse levelTwoBirdReverse = levelTwoBirdReversePool.obtain();
        levelTwoBirdReverse.init(totalGameTime);
        activeLevelTwoBirdReverses.add(levelTwoBirdReverse);
        dodgeables.activeDodgeables.add(levelTwoBirdReverse);

        //keep track of time the bird was spawned
        lastLevelTwoBirdReverseSpawnTime = Gameplay.totalGameTime;


    }

    public void spawnVerticalLineOfBirds(float totalGameTime){

        // Spawn(obtain) a vertical line of birds that move horizontally across the screen (leftwards)

        float spawnHeight = camera.viewportHeight;

        while (spawnHeight >= 0){

            LevelOneBird levelOneBird = levelOneBirdPool.obtain();
            levelOneBird.init(totalGameTime, spawnHeight);
            activeLevelOneBirds.add(levelOneBird);
            dodgeables.activeDodgeables.add(levelOneBird);

            spawnHeight -= levelOneBird.HEIGHT;

        }


        //keep track of time the bird was spawned
        lastLevelOneBirdSpawnTime = Gameplay.totalGameTime;


    }

    private void initializeLevelOneBirdAnimation() {

        // Load the level one bird sprite sheet as a Texture
        levelOneBirdFlySheet = new Texture(Gdx.files.internal("sprites/DivingPigeonSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmpDiving = TextureRegion.split(levelOneBirdFlySheet,
                levelOneBirdFlySheet.getWidth() / 3,
                levelOneBirdFlySheet.getHeight() / 2);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] levelOneBirdFlyFrames = new TextureRegion[3 * 2];
        int divingIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                levelOneBirdFlyFrames[divingIndex++] = tmpDiving[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        levelOneBirdAnimation = new Animation<TextureRegion>(0.05f, levelOneBirdFlyFrames);


    }

    private void initializeLevelTwoBirdAnimation() {

        // Load the level two bird sprite sheet as a Texture
        levelTwoBirdFlySheet = new Texture(Gdx.files.internal("sprites/SpeedBirdSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmpLevelTwo = TextureRegion.split(levelTwoBirdFlySheet,
                levelTwoBirdFlySheet.getWidth() / 4,
                levelTwoBirdFlySheet.getHeight() / 2);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] levelTwoBirdFlyFrames = new TextureRegion[4 * 2];
        int levelTwoIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                levelTwoBirdFlyFrames[levelTwoIndex++] = tmpLevelTwo[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        levelTwoBirdAnimation = new Animation<TextureRegion>(0.04f, levelTwoBirdFlyFrames);

    }

    public float getLastLevelOneBirdSpawnTime() {

        return lastLevelOneBirdSpawnTime;
    }

    public float getLastLevelTwoBirdSpawnTime() {

        return lastLevelTwoBirdSpawnTime;
    }

    public float getLastLevelOneReverseBirdSpawnTime() {

        return lastLevelOneBirdReverseSpawnTime;
    }

    public float getLastLevelTwoReverseBirdSpawnTime() {

        return lastLevelTwoBirdReverseSpawnTime;
    }

    public void sweepDeadBodies() {

        // If the bird is flagged for deletion due to a collision, free the bird from the pool
        // so that it moves off the screen and can be reused

        for (LevelOneBird levelOneBird : activeLevelOneBirds) {
            if (!levelOneBird.isActive()) {
                activeLevelOneBirds.removeValue(levelOneBird, false);
                dodgeables.activeDodgeables.removeValue(levelOneBird, false);
                levelOneBirdPool.free(levelOneBird);
            }
        }

        for (LevelOneBirdReverse levelOneBirdReverse : activeLevelOneBirdReverses) {
            if (!levelOneBirdReverse.isActive()) {
                activeLevelOneBirdReverses.removeValue(levelOneBirdReverse, false);
                dodgeables.activeDodgeables.removeValue(levelOneBirdReverse, false);
                levelOneBirdReversePool.free(levelOneBirdReverse);
            }
        }

        for (LevelTwoBird levelTwoBird : activeLevelTwoBirds) {
            if (!levelTwoBird.isActive()) {
                activeLevelTwoBirds.removeValue(levelTwoBird, false);
                dodgeables.activeDodgeables.removeValue(levelTwoBird, false);
                levelTwoBirdPool.free(levelTwoBird);
            }
        }

        for (LevelTwoBirdReverse levelTwoBirdReverse : activeLevelTwoBirdReverses) {
            if (!levelTwoBirdReverse.isActive()) {
                activeLevelTwoBirdReverses.removeValue(levelTwoBirdReverse, false);
                dodgeables.activeDodgeables.removeValue(levelTwoBirdReverse, false);
                levelTwoBirdReversePool.free(levelTwoBirdReverse);
            }
        }


    }

    public void dispose() {
        levelOneBirdFlySheet.dispose();
        levelTwoBirdFlySheet.dispose();
    }

}
