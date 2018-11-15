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
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class Dodgeables {

    //Class to define objects that the player should dodge

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    private final long MILLION_SCALE = 1000000;

    //Level One Bird variables
    private Array<Body> levelOneBirdsArray = new Array<Body>();
    private Animation<TextureRegion> levelOneBirdAnimation;
    private Texture levelOneBirdFlySheet;
    private long lastLevelOneBirdSpawnTime;

    //Meteor global variables
    private Array<Body> meteorArray = new Array<Body>();
    private Texture meteorTextureSpriteSheet;
    private Animation<TextureRegion> meteorAnimation;
    private long lastMeteorSpawnTime;
    private final int METEOR_SCALE = 15;

    //Level Two Bird variables
    private Array<Body> levelTwoBirdsArray = new Array<Body>();
    private Animation<TextureRegion> levelTwoBirdAnimation;
    private Texture levelTwoBirdFlySheet;
    private long lastLevelTwoBirdSpawnTime;

    //Rocket variables
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
    private final float ROCKET_EXPLOSION_WIDTH = 20f;
    private final float ROCKET_EXPLOSION_HEIGHT = 20f;



    //PowerUp Shield variables
    private Array<Body> powerUpShieldsArray = new Array<Body>();
    private Animation<TextureRegion> powerUpShieldAnimation;
    private Texture powerUpShieldSheet;
    private long lastpowerUpShieldSpawnTime;

    //Teleport variables
    private Array<Body> teleportArray = new Array<Body>();
    private Animation<TextureRegion> teleportAnimation;
    private Texture teleportSheet;
    private long lastTeleportSpawnTime;


    public Dodgeables(Pigeon pigeon, World world, AlphaPigeon game, OrthographicCamera camera) {

        gameWorld = world;
        this.game = game;
        this.camera = camera;

        // initialize enemy animations
        initializeLevelOneBirdAnimation();
        initializeMeteorAnimation();
        initializeLevelTwoBirdAnimation();
        initializeRocketAnimation();
        initializeRocketExplosionAnimation();

        // initialize powerup animations
        initializePowerUpShieldAnimation();

        // initialize other animations
        initializeTeleportAnimation();

    }

    public void spawnDodgeables() {
        //class to determine if we need to spawn new dodgeables depending on how much time has passed
        if (TimeUtils.nanoTime() / MILLION_SCALE - lastLevelOneBirdSpawnTime / MILLION_SCALE > 50000)
            spawnLevelOneBird();
        if (TimeUtils.nanoTime() / MILLION_SCALE - lastMeteorSpawnTime / MILLION_SCALE > 50000)
            spawnMeteor();
        if (TimeUtils.nanoTime() / MILLION_SCALE - lastLevelTwoBirdSpawnTime / MILLION_SCALE > 50000)
            spawnLevelTwoBird();
        if (TimeUtils.nanoTime() / MILLION_SCALE - lastRocketSpawnTime / MILLION_SCALE > 2000)
            spawnRocket();
        if (TimeUtils.nanoTime() / MILLION_SCALE - lastpowerUpShieldSpawnTime / MILLION_SCALE > 50000)
            spawnPowerUpShield();
        if (TimeUtils.nanoTime() / MILLION_SCALE - lastTeleportSpawnTime / MILLION_SCALE > 50000)
            spawnTeleports();
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

        //spawn a new meteor bird
        BodyDef meteorBodyDef = new BodyDef();
        meteorBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn meteor at random width
        meteorBodyDef.position.set(MathUtils.random(0, camera.viewportWidth), camera.viewportHeight);
        Body meteorBody = gameWorld.createBody(meteorBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Meteor.json"));
        FixtureDef meteorFixtureDef = new FixtureDef();
        meteorFixtureDef.density = 0.05f;
        meteorFixtureDef.friction = 0.5f;
        meteorFixtureDef.restitution = 0.3f;
        // set the meteor filter categories and masks for collisions
        meteorFixtureDef.filter.categoryBits = game.CATEGORY_METEOR;
        meteorFixtureDef.filter.maskBits = game.MASK_METEOR;
        loader.attachFixture(meteorBody, "Meteor", meteorFixtureDef, METEOR_SCALE);
        meteorBody.applyForceToCenter(-900.0f, -900.0f, true);

        //add meteor to meteors array
        meteorArray.add(meteorBody);

        //keep track of time the meteor was spawned
        lastMeteorSpawnTime = TimeUtils.nanoTime();

    }

    public void spawnLevelTwoBird() {

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
        loader.attachFixture(levelTwoBirdBody, "LevelTwoBird", levelTwoBirdFixtureDef, 12);
        levelTwoBirdBody.applyForceToCenter(-15.0f, 0, true);

        //add bird to level two birds array
        levelTwoBirdsArray.add(levelTwoBirdBody);

        //keep track of time the bird was spawned
        lastLevelTwoBirdSpawnTime = TimeUtils.nanoTime();

    }

    private void spawnRocket() {

        //spawn a new rocket
        BodyDef rocketBodyDef = new BodyDef();
        rocketBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn rocket at random height and subtract the width of the rocket since the rocket is rotated 90 degrees
        float rocketSpawnHeight = MathUtils.random(ROCKET_WIDTH, camera.viewportHeight);
        rocketBodyDef.position.set(camera.viewportWidth, rocketSpawnHeight);
        Body rocketBody = gameWorld.createBody(rocketBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Rocket.json"));
        FixtureDef rocketFixtureDef = new FixtureDef();
        rocketFixtureDef.density = 0.001f;
        rocketFixtureDef.friction = 0.5f;
        rocketFixtureDef.restitution = 0.3f;
        // set the rocket filter categories and masks for collisions
        rocketFixtureDef.filter.categoryBits = game.CATEGORY_ROCKET;
        rocketFixtureDef.filter.maskBits = game.MASK_ROCKET;
        loader.attachFixture(rocketBody, "Rocket", rocketFixtureDef, 10);
        rocketBody.setTransform(rocketBody.getPosition(), -90 * MathUtils.degreesToRadians);

        //Determine which torque to apply to rocket depending on if it is spawned on bottom half or top half of screen
        //If spawned on bottom half, rotate rocket CW and move it upwards (done in the update method)
        //If spawned on top half, rotate rocket CCW and move it downwards (done in the update method)
        //Randomize the magnitude of the torque
        float rocketTorque;
        boolean rocketSpawnedInBottomHalfScreen = rocketSpawnHeight < camera.viewportHeight / 2;
        if (rocketSpawnedInBottomHalfScreen) {
            rocketTorque = MathUtils.random(-4f, -2f);
        } else {
            rocketTorque = MathUtils.random(-1f, 0f);
        }

        //Set torque and spawn data on the rocket body so it can be used in the update method
        BodyData rocketData = new BodyData(false);
        rocketData.setRocketData(rocketTorque, false);
        rocketBody.setUserData(rocketData);
        rocketBody.applyTorque(rocketTorque, true);
        // apply the force to the rocket at the height it was spawned and at the end of the rocket
        // ROCKET_HEIGHT is used  for x coordinate of force instead of ROCKET_WIDTH because the rocket is rotated 90 degrees
        rocketBody.applyForce(-15.0f, 0, camera.viewportWidth + ROCKET_HEIGHT, rocketSpawnHeight - 5, true);


        //add rocket to rockets array
        rocketArray.add(rocketBody);

        //keep track of time the rocket was spawned
        lastRocketSpawnTime = TimeUtils.nanoTime();


    }

    public void spawnPowerUpShield() {

        //spawn a new PowerUp Shield
        BodyDef powerUpShieldBodyDef = new BodyDef();
        powerUpShieldBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn PowerUp shield at random height
        powerUpShieldBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - 6));
        Body powerUpShieldBody = gameWorld.createBody(powerUpShieldBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/PowerUpShield.json"));
        FixtureDef powerUpShieldFixtureDef = new FixtureDef();
        powerUpShieldFixtureDef.density = 0.001f;
        powerUpShieldFixtureDef.friction = 0.5f;
        powerUpShieldFixtureDef.restitution = 0.3f;
        // set the powerup shield filter categories and masks for collisions
        powerUpShieldFixtureDef.filter.categoryBits = game.CATEGORY_POWERUP_SHIELD;
        powerUpShieldFixtureDef.filter.maskBits = game.MASK_POWERUP;
        //The JSON loader loaders a fixture 1 pixel by 1 pixel... the animation is 80 px x 48 px, so need to scale by a factor of 8 since the width is the limiting factor
        loader.attachFixture(powerUpShieldBody, "PowerUpShield", powerUpShieldFixtureDef, 8);
        powerUpShieldBody.applyForceToCenter(-9.0f, 0, true);

        //add PowerUp shield to shields array
        powerUpShieldsArray.add(powerUpShieldBody);

        //keep track of time the PowerUp shield was spawned
        lastpowerUpShieldSpawnTime = TimeUtils.nanoTime();

    }

    private void spawnTeleports() {

        //spawn two new teleports that start at opposite ends of the screen (x direction) and
        //travel in opposite directions

        //spawn first teleport
        BodyDef teleportBodyDef = new BodyDef();
        teleportBodyDef.type = BodyDef.BodyType.DynamicBody;
        //spawn teleport at random height
        teleportBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - 10));
        Body teleportBody = gameWorld.createBody(teleportBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Teleport.json"));
        FixtureDef teleportFixtureDef = new FixtureDef();
        teleportFixtureDef.density = 0.001f;
        teleportFixtureDef.friction = 0.5f;
        teleportFixtureDef.restitution = 0.3f;
        // set the teleport filter categories and masks for collisions
        teleportFixtureDef.filter.categoryBits = game.CATEGORY_TELEPORT;
        teleportFixtureDef.filter.maskBits = game.MASK_TELEPORT;
        //The JSON loader loaders a fixture 1 pixel by 1 pixel... the animation is 100 px x 100 px, so need to scale by a factor of 10
        loader.attachFixture(teleportBody, "Teleport", teleportFixtureDef, 10);
        teleportBody.applyForceToCenter(-9.0f, 0, true);

        //spawn second teleport which starts at the opposite side of screen as the first and travels in the opposite direction
        BodyDef teleportTwoBodyDef = new BodyDef();
        teleportTwoBodyDef.type = BodyDef.BodyType.DynamicBody;
        teleportTwoBodyDef.position.set(0, MathUtils.random(0, camera.viewportHeight - 10));
        Body teleportTwoBody = gameWorld.createBody(teleportTwoBodyDef);
        loader.attachFixture(teleportTwoBody, "Teleport", teleportFixtureDef, 10);
        teleportTwoBody.applyForceToCenter(7.0f, 0, true);

        //Attach data of the opposite teleport to the teleport, so it can be used to transport the pigeon
        //to the opposite teleport's location
        BodyData teleportOneData = new BodyData(false);
        teleportOneData.setOppositeTeleport(teleportTwoBody);
        BodyData teleportTwoData = new BodyData(false);
        teleportTwoData.setOppositeTeleport(teleportBody);
        teleportBody.setUserData(teleportOneData);
        teleportTwoBody.setUserData(teleportTwoData);


        //add teleport to teleports array
        teleportArray.add(teleportBody);
        teleportArray.add(teleportTwoBody);

        //keep track of time the teleport shield was spawned
        lastTeleportSpawnTime = TimeUtils.nanoTime();

    }

    public void render(float stateTime, SpriteBatch batch) {
        // get current frame of animation for the current stateTime
        TextureRegion backwardsCurrentFrame = levelOneBirdAnimation.getKeyFrame(stateTime, true);
        TextureRegion levelTwoCurrentFrame = levelTwoBirdAnimation.getKeyFrame(stateTime, true);
        TextureRegion rocketCurrentFrame = rocketAnimation.getKeyFrame(stateTime, true);
        TextureRegion rocketExplosionCurrentFrame = rocketExplosionAnimation.getKeyFrame(stateTime, true);
        TextureRegion powerUpShieldCurrentFrame = powerUpShieldAnimation.getKeyFrame(stateTime, true);
        TextureRegion teleportCurrentFrame = teleportAnimation.getKeyFrame(stateTime, true);


        // draw all level one birds dodgeables using the current animation frame
        for (Body backwardsPigeon : levelOneBirdsArray) {
            if (backwardsPigeon.isActive()) {
                batch.draw(backwardsCurrentFrame, backwardsPigeon.getPosition().x, backwardsPigeon.getPosition().y, 0, 0, 6, 6, 1, 1, MathUtils.radiansToDegrees * backwardsPigeon.getAngle());
            } else {
                levelOneBirdsArray.removeValue(backwardsPigeon, false);
            }
        }

        // get current frame of meteor animation for the current stateTime
        TextureRegion meteorCurrentFrame = meteorAnimation.getKeyFrame(stateTime, true);
        for (Body meteor : meteorArray) {
            if (meteor.isActive()) {
                batch.draw(meteorCurrentFrame, meteor.getPosition().x, meteor.getPosition().y, 0, 0, METEOR_SCALE, METEOR_SCALE, 1, 1, MathUtils.radiansToDegrees * meteor.getAngle());
            } else {
                meteorArray.removeValue(meteor, false);
            }
        }

        // draw all level one birds dodgeables using the current animation frame
        for (Body speedBird : levelTwoBirdsArray) {
            if (speedBird.isActive()) {
                batch.draw(levelTwoCurrentFrame, speedBird.getPosition().x, speedBird.getPosition().y - 2f, 0, 2, 12, 12, 1, 1, MathUtils.radiansToDegrees * speedBird.getAngle());
            } else {
                levelTwoBirdsArray.removeValue(speedBird, false);
            }
        }

        // draw all rocket dodgeables using the current animation frame
        for (Body rocket : rocketArray) {
            if (rocket.isActive()) {
                batch.draw(rocketCurrentFrame, rocket.getPosition().x, rocket.getPosition().y, 0, 0, 10, 20, 1, 1, MathUtils.radiansToDegrees * rocket.getAngle());
                batch.draw(rocketExplosionCurrentFrame, rocket.getPosition().x,  rocket.getPosition().y, 0, 0, 20, 20, 1,  1,  MathUtils.radiansToDegrees * rocket.getAngle());
            } else {
                rocketArray.removeValue(rocket, false);
            }
        }

        // draw all PowerUp shield dodgeables using the current animation frame
        for (Body powerUpShield : powerUpShieldsArray) {

            // draw the PowerUp shield if it is active (hasn't been grabbed by the pigeon), otherwise remove it from the array
            if (powerUpShield.isActive()) {
                batch.draw(powerUpShieldCurrentFrame, powerUpShield.getPosition().x, powerUpShield.getPosition().y,
                        0, 0, 8f, 4.8f, 1, 1, MathUtils.radiansToDegrees * powerUpShield.getAngle());
            } else {
                powerUpShieldsArray.removeValue(powerUpShield, false);
            }
        }

        // draw all teleport dodgeables using the current animation frame
        for (Body teleport : teleportArray) {

            // draw the teleport if it is active (hasn't been grabbed by the pigeon), otherwise remove it from the array
            if (teleport.isActive()) {
                batch.draw(teleportCurrentFrame, teleport.getPosition().x, teleport.getPosition().y,
                        0, 0, 10f, 10f, 1, 1, MathUtils.radiansToDegrees * teleport.getAngle());
            } else {
                teleportArray.removeValue(teleport, false);
            }

        }


    }

    public void update(float stateTime) {
        //Check if we need to spawn new dodgeables depending on game time
        spawnDodgeables();

        // ROCKETS
        // If rockets are spawned , accelerate them.  The X force is constant and the Y force
        // is stored on the rocket body data.  Y force depends on where the rocket was spawned (see spawnRockets method)
        if (TimeUtils.nanoTime() / MILLION_SCALE - lastRocketSpawnTime / MILLION_SCALE > 500) {
            for (Body rocket : rocketArray) {
                if (rocket.isActive()) {
                    float forceX = -1f;
                    BodyData rocketData = (BodyData) rocket.getUserData();
                    float forceY = rocketData.getRocketYForce();
                    rocket.applyForceToCenter(forceX, forceY, true);
                } else {
                    rocketArray.removeValue(rocket, false);
                }
            }
        }

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

    private void initializeTeleportAnimation() {

        // Load the teleport sprite sheet as a Texture
        teleportSheet = new Texture(Gdx.files.internal("sprites/TeleportSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(teleportSheet,
                teleportSheet.getWidth() / 10,
                teleportSheet.getHeight() / 9);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] teleportFrames = new TextureRegion[10 * 9];
        int index = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                teleportFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        teleportAnimation = new Animation<TextureRegion>(0.05f, teleportFrames);

    }


    public void dispose() {
        // Dispose of all textures
        levelOneBirdFlySheet.dispose();
        levelTwoBirdFlySheet.dispose();
        meteorTextureSpriteSheet.dispose();
        powerUpShieldSheet.dispose();
        teleportSheet.dispose();
    }

}
