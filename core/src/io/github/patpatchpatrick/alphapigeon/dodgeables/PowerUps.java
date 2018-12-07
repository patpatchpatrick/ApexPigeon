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
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class PowerUps {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //PowerUp Shield variables
    private Array<Body> powerUpShieldsArray = new Array<Body>();
    private Animation<TextureRegion> powerUpShieldAnimation;
    private Texture powerUpShieldSheet;
    private long lastpowerUpShieldSpawnTime;
    private final float POWER_UP_SHIELD_WIDTH = 8f;
    private final float POWER_UP_SHIELD_HEIGHT = 4.8f;

    //Shield intervals between spawns
    private final float SHIELD_INITIAL_SPAWN_INTERVAL_START_RANGE = 20000;
    private final float SHIELD_INITIAL_SPAWN_INTERVAL_END_RANGE = 60000;
    private float shieldRandomSpawnInterval;

    public PowerUps(World gameWorld, AlphaPigeon game, OrthographicCamera camera){
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        // initialize powerup animations
        initializePowerUpShieldAnimation();

        // set the initial shield spawn interval to a random number between 20 seconds and 60 seconds
        shieldRandomSpawnInterval = MathUtils.random(SHIELD_INITIAL_SPAWN_INTERVAL_START_RANGE, SHIELD_INITIAL_SPAWN_INTERVAL_END_RANGE);


    }

    public void render(float stateTime, SpriteBatch batch){

        TextureRegion powerUpShieldCurrentFrame = powerUpShieldAnimation.getKeyFrame(stateTime, true);

        // draw all PowerUp shield dodgeables using the current animation frame
        for (Body powerUpShield : powerUpShieldsArray) {

            // draw the PowerUp shield if it is active (hasn't been grabbed by the pigeon), otherwise remove it from the array
            if (powerUpShield.isActive()) {
                batch.draw(powerUpShieldCurrentFrame, powerUpShield.getPosition().x, powerUpShield.getPosition().y,
                        0, 0, POWER_UP_SHIELD_WIDTH, POWER_UP_SHIELD_HEIGHT, 1, 1, MathUtils.radiansToDegrees * powerUpShield.getAngle());
            } else {
                powerUpShieldsArray.removeValue(powerUpShield, false);
            }
        }

    }

    public void update(){

        for (Body powerUpShield : powerUpShieldsArray){
            if (powerUpShield.getPosition().x < 0 - POWER_UP_SHIELD_WIDTH ){
                powerUpShieldsArray.removeValue(powerUpShield, false);
                gameWorld.destroyBody(powerUpShield);
            }
        }

    }

    public void spawnPowerUpShield() {

        //spawn a new PowerUp Shield
        BodyDef powerUpShieldBodyDef = new BodyDef();
        powerUpShieldBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn PowerUp shield at random height
        powerUpShieldBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - POWER_UP_SHIELD_HEIGHT));
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
        loader.attachFixture(powerUpShieldBody, "PowerUpShield", powerUpShieldFixtureDef, POWER_UP_SHIELD_WIDTH);
        powerUpShieldBody.applyForceToCenter(-9.0f, 0, true);

        //add PowerUp shield to shields array
        powerUpShieldsArray.add(powerUpShieldBody);

        //keep track of time the PowerUp shield was spawned
        lastpowerUpShieldSpawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;

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

    public float getPowerUpShieldIntervalTime(){
        return this.shieldRandomSpawnInterval;
    }

    public long getLastpowerUpShieldSpawnTime(){
        return lastpowerUpShieldSpawnTime;
    }

    public void dispose(){
        powerUpShieldSheet.dispose();
    }

}
