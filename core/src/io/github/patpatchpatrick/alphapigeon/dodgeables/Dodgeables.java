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
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.Pigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class Dodgeables {

    //Class to define objects that the player should dodge

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    private final long MILLION_SCALE = 1000000;

    //Level One Bird variables
    private Array<Body> levelOneBirdsArray;
    private Animation<TextureRegion> levelOneBirdAnimation;
    private Texture levelOneBirdFlySheet;
    private long lastLevelOneBirdSpawnTime;

    //Meteor global variables
    private Array<Body> meteorArray;
    private Texture meteorTextureSpriteSheet;
    private Animation<TextureRegion> meteorAnimation;
    private long lastMeteorSpawnTime;
    private final int METEOR_SCALE = 15;

    //Level Two Bird variables
    private Array<Body> levelTwoBirdsArray;
    private Animation<TextureRegion> levelTwoBirdAnimation;
    private Texture levelTwoBirdFlySheet;
    private long lastLevelTwoBirdSpawnTime;

    public Dodgeables(Pigeon pigeon, World world, AlphaPigeon game, OrthographicCamera camera) {

        gameWorld = world;
        this.game = game;
        this.camera = camera;

        // initialize array of level one birds
        levelOneBirdsArray = new Array<Body>();
        meteorArray = new Array<Body>();
        levelTwoBirdsArray = new Array<Body>();

        // initialize animations
        initializeLevelOneBirdAnimation();
        initializeMeteorAnimation();
        initializeLevelTwoBirdAnimation();

    }

    public void spawnLevelOneBird() {
        //spawn a new level one bird
        BodyDef levelOneBirdBodyDef = new BodyDef();
        levelOneBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelOneBirdBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - 6));
        Body levelOneBirdBody = gameWorld.createBody(levelOneBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/LevelOnePigeon.json"));
        FixtureDef levelOneBirdFixtureDef = new FixtureDef();
        levelOneBirdFixtureDef.density = 0.001f;
        levelOneBirdFixtureDef.friction = 0.5f;
        levelOneBirdFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        levelOneBirdFixtureDef.filter.categoryBits = game.CATEGORY_LEVEL_ONE_BIRD;
        levelOneBirdFixtureDef.filter.maskBits = game.MASK_LEVEL_ONE_BIRD;
        loader.attachFixture(levelOneBirdBody, "BackwardsPigeon", levelOneBirdFixtureDef, 6);
        levelOneBirdBody.applyForceToCenter(-9.0f, 0, true);

        //add bird to level one birds array
        levelOneBirdsArray.add(levelOneBirdBody);

        //keep track of time the bird was spawned
        lastLevelOneBirdSpawnTime = TimeUtils.nanoTime();
    }

    public void spawnMeteor() {

        //spawn a new level one bird
        BodyDef meteorBodyDef = new BodyDef();
        meteorBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        meteorBodyDef.position.set(MathUtils.random(0, camera.viewportWidth), camera.viewportHeight);
        Body meteorBody = gameWorld.createBody(meteorBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Meteor.json"));
        FixtureDef meteorFixtureDef = new FixtureDef();
        meteorFixtureDef.density = 0.05f;
        meteorFixtureDef.friction = 0.5f;
        meteorFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        meteorFixtureDef.filter.categoryBits = game.CATEGORY_METEOR;
        meteorFixtureDef.filter.maskBits = game.MASK_METEOR;
        loader.attachFixture(meteorBody, "Meteor", meteorFixtureDef, METEOR_SCALE);
        meteorBody.applyForceToCenter(-900.0f, -900.0f, true);

        //add bird to level one birds array
        meteorArray.add(meteorBody);

        //keep track of time the bird was spawned
        lastMeteorSpawnTime = TimeUtils.nanoTime();

    }

    public void spawnLevelTwoBird(){

        //spawn a new level one bird
        BodyDef levelTwoBirdBodyDef = new BodyDef();
        levelTwoBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelTwoBirdBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - 6));
        Body levelTwoBirdBody = gameWorld.createBody(levelTwoBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/SpeedPigeon.json"));
        FixtureDef levelTwoBirdFixtureDef = new FixtureDef();
        levelTwoBirdFixtureDef.density = 0.001f;
        levelTwoBirdFixtureDef.friction = 0.5f;
        levelTwoBirdFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        levelTwoBirdFixtureDef.filter.categoryBits = game.CATEGORY_LEVEL_ONE_BIRD;
        levelTwoBirdFixtureDef.filter.maskBits = game.MASK_LEVEL_ONE_BIRD;
        loader.attachFixture(levelTwoBirdBody, "Name", levelTwoBirdFixtureDef, 6);
        levelTwoBirdBody.applyForceToCenter(-9.0f, 0, true);

        //add bird to level one birds array
        levelTwoBirdsArray.add(levelTwoBirdBody);

        //keep track of time the bird was spawned
        lastLevelTwoBirdSpawnTime = TimeUtils.nanoTime();



    }

    public void spawnDodgeables() {
        //class to determine if we need to spawn new dodgeables depending on how much time has passed
        if (TimeUtils.nanoTime()/MILLION_SCALE - lastLevelOneBirdSpawnTime/MILLION_SCALE > 2000) spawnLevelOneBird();
        if (TimeUtils.nanoTime()/MILLION_SCALE - lastMeteorSpawnTime/MILLION_SCALE > 4000) spawnMeteor();
        if (TimeUtils.nanoTime()/MILLION_SCALE - lastLevelTwoBirdSpawnTime/MILLION_SCALE > 2000) spawnLevelTwoBird();
    }

    public void render(float stateTime, SpriteBatch batch) {
        // get current frame of animation for the current stateTime
        TextureRegion backwardsCurrentFrame = levelOneBirdAnimation.getKeyFrame(stateTime, true);
        TextureRegion levelTwoCurrentFrame = levelTwoBirdAnimation.getKeyFrame(stateTime, true);

        // draw all level one birds dodgeables using the current animation frame
        for (Body backwardsPigeon : levelOneBirdsArray) {
            batch.draw(backwardsCurrentFrame, backwardsPigeon.getPosition().x, backwardsPigeon.getPosition().y, 0, 0, 6, 6, 1, 1, MathUtils.radiansToDegrees * backwardsPigeon.getAngle());
        }

        // get current frame of meteor animation for the current stateTime
        TextureRegion meteorCurrentFrame = meteorAnimation.getKeyFrame(stateTime, true);
        for(Body meteor : meteorArray) {
            game.batch.draw(meteorCurrentFrame, meteor.getPosition().x, meteor.getPosition().y, 0, 0, METEOR_SCALE, METEOR_SCALE, 1, 1, MathUtils.radiansToDegrees * meteor.getAngle());
        }

        // draw all level one birds dodgeables using the current animation frame
        for (Body speedBird : levelTwoBirdsArray) {
            batch.draw(levelTwoCurrentFrame, speedBird.getPosition().x, speedBird.getPosition().y - 1, 0, 0, 6, 6, 1, 1, MathUtils.radiansToDegrees * speedBird.getAngle());
        }

    }

    public void update(float stateTime) {
        //Check if we need to spawn new dodgeables depending on game time
        spawnDodgeables();

        /**
         for (Iterator<Body> iter = levelOneBirdsArray.iterator(); iter.hasNext(); ) {
         Body backwardsPigeonRect = iter.next();
         backwardsPigeonRect.x -= dodgeableSpeed * Gdx.graphics.getDeltaTime();
         if (backwardsPigeonRect.x + 64 < 0) iter.remove();
         //Add code to remove pigeon if it collides
         }**/
    }

    public void initializeLevelOneBirdAnimation() {

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

    private void initializeMeteorAnimation() {

        meteorTextureSpriteSheet = new Texture(Gdx.files.internal("textures/Meteor.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmpMeteor = TextureRegion.split(meteorTextureSpriteSheet,
                meteorTextureSpriteSheet.getWidth() / 1,
                meteorTextureSpriteSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] meteorFrames = new TextureRegion[1 * 1];
        int meteorIndex = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                meteorFrames[meteorIndex++] = tmpMeteor[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        meteorAnimation = new Animation<TextureRegion>(0.05f, meteorFrames);



    }

    private void initializeLevelTwoBirdAnimation() {

        // Load the level one bird sprite sheet as a Texture
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
        levelTwoBirdAnimation = new Animation<TextureRegion>(0.05f, levelTwoBirdFlyFrames);


    }

    public void dispose() {
        levelOneBirdFlySheet.dispose();
        levelTwoBirdFlySheet.dispose();
    }

}
