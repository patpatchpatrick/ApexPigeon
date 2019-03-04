package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.Screens.GameScreen;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Teleport;
import io.github.patpatchpatrick.alphapigeon.dodgeables.PowerUps;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

public class Pigeon {

    private Texture pigeonFlySheet;
    private Animation<TextureRegion> pigeonFlyAnimation;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;
    Body pigeonBody;
    AlphaPigeon game;
    GameScreen gameScreen;
    World world;
    private float stateTime;

    //Power Up Variables
    private int currentPowerUp;
    private float currentPowerUpTime = 0;
    private String powerUpShieldTimeRemaining = "";

    //Power Up Shield Animation Variables
    private Texture powerUpShieldSheet;
    private Animation<TextureRegion> powerUpShieldAnimation;

    //Fonts and text to display on pigeon
            private BitmapFont font;

    //Sound
    private Sound powerUpShieldSound = Sounds.powerUpShieldSound;


    public Pigeon(World world, AlphaPigeon game, GameScreen gameScreen) {

        initializePigeonAnimation();

        this.game = game;
        this.world = world;
        this.gameScreen = gameScreen;

        // create pigeon body, set position in the world
        // create pigeon fixture, attach the fixture created to the body created with the help of
        // Box 2D editor
        BodyDef pigeonBodyDef = new BodyDef();
        pigeonBodyDef.type = BodyDef.BodyType.DynamicBody;
        pigeonBodyDef.position.set(10, 10);
        pigeonBody = world.createBody(pigeonBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlphaPigeonBody.json"));
        FixtureDef pigeonFixtureDef = new FixtureDef();
        pigeonFixtureDef.density = 0.001f;
        pigeonFixtureDef.friction = 0.5f;
        pigeonFixtureDef.restitution = 0.3f;
        // set the pigeon filter category and mask for collisions
        pigeonFixtureDef.filter.categoryBits = GameVariables.CATEGORY_PIGEON;
        pigeonFixtureDef.filter.maskBits = GameVariables.MASK_PIGEON;
        //pigeonFixtureDef.isSensor =  true;
        loader.attachFixture(pigeonBody, "AlphaPigeon", pigeonFixtureDef, 10);
        //Set fixed rotation to body... pigeon should not rotate
        pigeonBody.setFixedRotation(true);

        initializePowerUpShieldAnimation();

        //Initialize FONTS
        font = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"),
                Gdx.files.internal("fonts/arial-15.png"), false);
        font.getData().setScale(0.1f);
        font.setUseIntegerPositions(false);
        font.setColor(Color.RED);


    }

    //Pigeon actions

    public void powerUp(Fixture powerUpFixture) {
        Body powerUpBody = powerUpFixture.getBody();
        BodyData powerUpData = (BodyData) powerUpBody.getUserData();
        int powerUpType = powerUpData.powerUpType;

        switch (powerUpType) {
            case PowerUps.POWER_UP_TYPE_SHIELD:
                //Play powerUp sounds
                this.currentPowerUp = PowerUps.POWER_UP_TYPE_SHIELD;
                powerUpShieldSound = Sounds.powerUpShieldSound;
                powerUpShieldSound.loop(SettingsManager.gameVolume);
                Sounds.activeSounds.add(powerUpShieldSound);
                break;
            case PowerUps.POWER_UP_TYPE_SKULL:
                //Play powerUp sounds
                this.currentPowerUp = PowerUps.POWER_UP_TYPE_SKULL;
                //Kill all active dodgeables when a skull power up is grabbed
                gameScreen.dodgeables.getPowerUps().killAllActiveDodgeables();
                Sounds.powerUpSkullSound.play(SettingsManager.gameVolume);
                break;

        }
        //Set the current power up type and the time that the power up was picked up by the pigeon
        this.currentPowerUpTime = this.stateTime;

        //Destroy the power up after the pigeon power ups
        powerUpBody.setUserData(new BodyData(true));


    }

    private void removePowerUps() {
        //Remove the powerUps from the pigeon
        //Set the powerUp back to its default of PIGEON
        this.currentPowerUp = PowerUps.POWER_UP_TYPE_NONE;
        //Stop the power up sounds
        powerUpShieldSound.stop();
        Sounds.activeSounds.remove(powerUpShieldSound);

    }

    public void zapEnemy() {
        Sounds.powerUpShieldZapSound.play(SettingsManager.gameVolume);
    }

    public int getPowerUpType() {
        //Return the current type of power up applied to the pigeon
        return currentPowerUp;
    }

    public void teleport(Fixture teleportFixture) {
        //Get the teleport data from the teleport fixture that contacted the pigeon
        final Body teleport = teleportFixture.getBody();
        BodyData teleportData = (BodyData) teleport.getUserData();
        if (teleportData != null) {
            final Teleport oppositeTeleport = teleportData.getOppositeTeleport();
            if (oppositeTeleport != null) {

                final float positionX = oppositeTeleport.getPosition().x;
                final float positionY = oppositeTeleport.getPosition().y;
                final float angle = oppositeTeleport.getAngle();

                final BodyData deleteObjectOne = new BodyData(true);
                final BodyData deleteObjectTwo = new BodyData(true);

                //Play the teleport sound
                Sounds.teleportSound.play(SettingsManager.gameVolume);

                //Move the pigeon to the opposite teleport's location and then destroy both teleports
                //This must be done using Runnable app.postRunnable so it occurs in the rendering thread which is currently locked
                //while the world is still stepping
                //Everything in the postRunnable Runnable is called on the render thread before the game render method is called

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {
                        pigeonBody.setTransform(positionX, positionY, angle);
                        oppositeTeleport.dodgeableBody.setUserData(deleteObjectOne);
                        teleport.setUserData(deleteObjectTwo);
                    }
                });
            }


        }


    }

    private void initializePigeonAnimation() {

        // load the pigeon sprite sheet as a Texture
        pigeonFlySheet = new Texture(Gdx.files.internal("sprites/AlphaPigeon_SpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(pigeonFlySheet,
                pigeonFlySheet.getWidth() / FRAME_COLS,
                pigeonFlySheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] pigeonFlyFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                pigeonFlyFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        pigeonFlyAnimation = new Animation<TextureRegion>(0.05f, pigeonFlyFrames);

    }

    private void initializePowerUpShieldAnimation() {

        // load the PowerUp shield sprite sheet as a Texture
        powerUpShieldSheet = new Texture(Gdx.files.internal("sprites/PUShieldSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(powerUpShieldSheet,
                powerUpShieldSheet.getWidth() / 3,
                powerUpShieldSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] shieldTextureRegion = new TextureRegion[3 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 3; j++) {
                shieldTextureRegion[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        powerUpShieldAnimation = new Animation<TextureRegion>(0.05f, shieldTextureRegion);

    }

    public void render(float stateTime, SpriteBatch batch) {


        // Get current frame of animation for the current stateTime and render it
        TextureRegion pigeonCurrentFrame = pigeonFlyAnimation.getKeyFrame(stateTime, true);
        batch.draw(pigeonCurrentFrame, pigeonBody.getPosition().x, pigeonBody.getPosition().y, 0, 0, 10, 5f, 1, 1, MathUtils.radiansToDegrees * pigeonBody.getAngle());

        if (this.currentPowerUp == PowerUps.POWER_UP_TYPE_SHIELD) {
            // If the pigeon is powered up with a shield, draw the shield around it and also draw the shield time remaining
            // Get current frame of animation for the current stateTime and render it
            TextureRegion powUpShieldCurrentFrame = powerUpShieldAnimation.getKeyFrame(stateTime, true);
            batch.draw(powUpShieldCurrentFrame, pigeonBody.getPosition().x - 2.5f, pigeonBody.getPosition().y - 2.5f, 0, 0, 15f, 10f, 1, 1, MathUtils.radiansToDegrees * pigeonBody.getAngle());
            // display power up remaining time

            //Font
            font.draw(batch, powerUpShieldTimeRemaining, pigeonBody.getPosition().x, pigeonBody.getPosition().y);

        }

    }

    public void update(float stateTime) {
        this.stateTime = stateTime;

        //Check if power up shield has expired
        //If so, set currentPowerUp back to CATEGORY_PIGEON, which is the default currentPowerUp setting
        //If not, display time remaining on screen
        if (this.stateTime - this.currentPowerUpTime > PowerUps.POWER_UP_SHIELD_DURATION) {
            removePowerUps();
        } else if (this.currentPowerUp == PowerUps.POWER_UP_TYPE_SHIELD) {
            powerUpShieldTimeRemaining = "" + MathUtils.floor(PowerUps.POWER_UP_SHIELD_DURATION - (this.stateTime - this.currentPowerUpTime) + 1);
        }

    }

    public Body getBody() {
        return pigeonBody;
    }


    public void dispose() {
        pigeonFlySheet.dispose();
        powerUpShieldSheet.dispose();
        font.dispose();
    }
}
