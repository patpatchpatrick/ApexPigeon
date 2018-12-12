package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Game;
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
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Bird;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Birds {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //Level One Bird variables
    private final Array<Bird> activeBirds = new Array<Bird>();
    private final Pool<Bird> birdPool;
    //private Array<Body> levelOneBirdsArray = new Array<Body>();
    private Animation<TextureRegion> levelOneBirdAnimation;
    private Texture levelOneBirdFlySheet;
    private long lastLevelOneBirdSpawnTime;
    private final float LEVEL_ONE_BIRD_WIDTH = 6f;
    private final float LEVEL_ONE_BIRD_HEIGHT = 6f;
    private final float LEVEL_ONE_FORCE_X = -9.0f;

    //Level Two Bird variables
    private Array<Body> levelTwoBirdsArray = new Array<Body>();
    private Animation<TextureRegion> levelTwoBirdAnimation;
    private Texture levelTwoBirdFlySheet;
    private long lastLevelTwoBirdSpawnTime;
    private final float LEVEL_TWO_BIRD_WIDTH = 12f;
    private final float LEVEL_TWO_BIRD_HEIGHT = 12f;
    private final float LEVEL_TWO_FORCE_X = -25.0f;

    public Birds(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera){

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        initializeLevelOneBirdAnimation();
        initializeLevelTwoBirdAnimation();

        birdPool = new Pool<Bird>() {
            @Override
            protected Bird newObject() {
                return new Bird(gameWorld, game, camera);
            }
        };

    }

    public void render(float stateTime, SpriteBatch batch){

        TextureRegion backwardsCurrentFrame = levelOneBirdAnimation.getKeyFrame(stateTime, true);
        TextureRegion levelTwoCurrentFrame = levelTwoBirdAnimation.getKeyFrame(stateTime, true);

        // draw all level one birds dodgeables using the current animation frame
        /**
        for (Body backwardsPigeon : levelOneBirdsArray) {
            if (backwardsPigeon.isActive()) {
                batch.draw(backwardsCurrentFrame, backwardsPigeon.getPosition().x, backwardsPigeon.getPosition().y, 0, 0, LEVEL_ONE_BIRD_WIDTH, LEVEL_ONE_BIRD_HEIGHT, 1, 1, MathUtils.radiansToDegrees * backwardsPigeon.getAngle());
            } else {
                levelOneBirdsArray.removeValue(backwardsPigeon, false);
            }
        }
         */

        for (Bird backwardsPigeon : activeBirds) {
            if (backwardsPigeon.alive) {
                batch.draw(backwardsCurrentFrame, backwardsPigeon.getPosition().x, backwardsPigeon.getPosition().y, 0, 0, LEVEL_ONE_BIRD_WIDTH, LEVEL_ONE_BIRD_HEIGHT, 1, 1, backwardsPigeon.getAngle());
            } else {
                activeBirds.removeValue(backwardsPigeon, false);
            }
        }

        // draw all level one birds dodgeables using the current animation frame
        for (Body speedBird : levelTwoBirdsArray) {
            if (speedBird.isActive()) {
                batch.draw(levelTwoCurrentFrame, speedBird.getPosition().x, speedBird.getPosition().y - 2f, 0, 2, LEVEL_TWO_BIRD_WIDTH, LEVEL_TWO_BIRD_HEIGHT, 1, 1, MathUtils.radiansToDegrees * speedBird.getAngle());
            } else {
                levelTwoBirdsArray.removeValue(speedBird, false);
            }
        }

    }

    public void update(){

        // Destroy all bodies that are off the screen
        /**
        for (Body levelOneBird : levelOneBirdsArray){
            if (levelOneBird.getPosition().x < 0 - LEVEL_ONE_BIRD_WIDTH ){
                levelOneBirdsArray.removeValue(levelOneBird, false);
                gameWorld.destroyBody(levelOneBird);
            }
        }
         */

        for (Bird levelOneBird : activeBirds){
            if (levelOneBird.getPosition().x < 0 - LEVEL_ONE_BIRD_WIDTH ){
                activeBirds.removeValue(levelOneBird, false);
                birdPool.free(levelOneBird);
            }
        }

        for (Body levelTwoBird : levelTwoBirdsArray){
            if (levelTwoBird.getPosition().x < 0 - LEVEL_TWO_BIRD_WIDTH){
                levelTwoBirdsArray.removeValue(levelTwoBird, false);
                gameWorld.destroyBody(levelTwoBird);
            }
        }

    }

    public void spawnLevelOneBird(){

        /**

        //spawn a new level one bird
        BodyDef levelOneBirdBodyDef = new BodyDef();
        levelOneBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelOneBirdBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - LEVEL_ONE_BIRD_HEIGHT));
        Body levelOneBirdBody = gameWorld.createBody(levelOneBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/LevelOnePigeon.json"));
        FixtureDef levelOneBirdFixtureDef = new FixtureDef();
        levelOneBirdFixtureDef.density = 0.001f;
        levelOneBirdFixtureDef.friction = 0.5f;
        levelOneBirdFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        levelOneBirdFixtureDef.filter.categoryBits = game.CATEGORY_LEVEL_ONE_BIRD;
        levelOneBirdFixtureDef.filter.maskBits = game.MASK_LEVEL_ONE_BIRD;
        loader.attachFixture(levelOneBirdBody, "BackwardsPigeon", levelOneBirdFixtureDef, LEVEL_ONE_BIRD_HEIGHT);
        levelOneBirdBody.applyForceToCenter(LEVEL_ONE_FORCE_X, 0, true);

         */

        Bird bird = birdPool.obtain();
        bird.init();
        activeBirds.add(bird);
        //add bird to level one birds array
        //levelOneBirdsArray.add(levelOneBirdBody);

        //keep track of time the bird was spawned
        lastLevelOneBirdSpawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;

    }

    public void spawnLevelTwoBird(){
        //spawn a new level two bird
        BodyDef levelTwoBirdBodyDef = new BodyDef();
        levelTwoBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelTwoBirdBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - 6));
        Body levelTwoBirdBody = gameWorld.createBody(levelTwoBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/LevelTwoBird.json"));
        FixtureDef levelTwoBirdFixtureDef = new FixtureDef();
        levelTwoBirdFixtureDef.density = 0.001f;
        levelTwoBirdFixtureDef.friction = 0.5f;
        levelTwoBirdFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        levelTwoBirdFixtureDef.filter.categoryBits = game.CATEGORY_LEVEL_TWO_BIRD;
        levelTwoBirdFixtureDef.filter.maskBits = game.MASK_LEVEL_TWO_BIRD;
        loader.attachFixture(levelTwoBirdBody, "LevelTwoBird", levelTwoBirdFixtureDef, LEVEL_TWO_BIRD_WIDTH);
        levelTwoBirdBody.applyForceToCenter(LEVEL_TWO_FORCE_X, 0, true);

        //add bird to level two birds array
        levelTwoBirdsArray.add(levelTwoBirdBody);

        //keep track of time the bird was spawned
        lastLevelTwoBirdSpawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
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

    public float getLastLevelOneBirdSpawnTime(){
        return lastLevelOneBirdSpawnTime;
    }

    public float getLastLevelTwoBirdSpawnTime(){
        return lastLevelTwoBirdSpawnTime;
    }

    public void sweepDeadBodies(){
        for (Bird bird : activeBirds){
            if (!bird.isActive()){
                birdPool.free(bird);
            }
        }
    }

    public void dispose(){
        levelOneBirdFlySheet.dispose();
        levelTwoBirdFlySheet.dispose();
    }

}
