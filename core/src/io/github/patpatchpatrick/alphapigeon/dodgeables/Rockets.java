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
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Rockets {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

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
    private final float ROCKET_EXPLOSION_WIDTH = 30f;
    private final float ROCKET_EXPLOSION_HEIGHT = 30f;

    public Rockets(World gameWorld, AlphaPigeon game, OrthographicCamera camera){
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        initializeRocketAnimation();
        initializeRocketExplosionAnimation();

    }

    public void render(float stateTime, SpriteBatch batch){
        TextureRegion rocketCurrentFrame = rocketAnimation.getKeyFrame(stateTime, true);
        TextureRegion rocketExplosionCurrentFrame = rocketExplosionAnimation.getKeyFrame(stateTime, true);

        // draw all rocket dodgeables using the current animation frame
        for (Body rocket : rocketArray) {
            if (rocket.isActive()) {
                batch.draw(rocketCurrentFrame, rocket.getPosition().x, rocket.getPosition().y, 0, 0, 10, 20, 1, 1, MathUtils.radiansToDegrees * rocket.getAngle());
            } else {
                rocketArray.removeValue(rocket, false);
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

    public void update(){

        // ROCKETS
        // If rockets are spawned , accelerate them.  The X force is constant and the Y force
        // is stored on the rocket body data.  Y force depends on where the rocket was spawned (see spawnRockets method)
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - lastRocketSpawnTime / GameVariables.MILLION_SCALE > 500) {
            for (Body rocket : rocketArray) {
                if (rocket.isActive()) {
                    float forceX = -1f;
                    BodyData rocketData = (BodyData) rocket.getUserData();
                    if (rocketData  != null){
                        float forceY = rocketData.getRocketYForce();
                        rocket.applyForceToCenter(forceX, forceY, true);
                    }
                } else {
                    rocketArray.removeValue(rocket, false);
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

    }

    public void spawnRocket() {

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

    public long getLastRocketSpawnTime(){
        return lastRocketSpawnTime;
    }

    public void dispose(){
        rocketExplosionSheet.dispose();
        rocketSheet.dispose();
    }

}
