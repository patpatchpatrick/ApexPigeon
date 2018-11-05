package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
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

import io.github.patpatchpatrick.alphapigeon.Pigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class Dodgeables {

    //Class to define objects that the player should dodge

    private Array<Body> levelOneBirdsArray;
    private Animation<TextureRegion> levelOneBirdAnimation;
    World gameWorld;
    private Texture levelOneBirdFlySheet;
    private long lastLevelOneBirdSpawnTime;

    public Dodgeables(Pigeon pigeon, World world) {

        gameWorld = world;

        // initialize array of level one birds
        levelOneBirdsArray = new Array<Body>();

        // initialize animations
        initializeLevelOneBirdAnimation();

    }

    public void spawnLevelOneBird() {
        //spawn a new level one bird
        BodyDef levelOneBirdBodyDef = new BodyDef();
        levelOneBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelOneBirdBodyDef.position.set(80, MathUtils.random(0, 48 - 6));
        Body levelOneBirdBody = gameWorld.createBody(levelOneBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/LevelOnePigeon.json"));
        FixtureDef levelOneBirdFixtureDef = new FixtureDef();
        levelOneBirdFixtureDef.density = 0.001f;
        levelOneBirdFixtureDef.friction = 0.5f;
        levelOneBirdFixtureDef.restitution = 0.3f;
        loader.attachFixture(levelOneBirdBody, "BackwardsPigeon", levelOneBirdFixtureDef, 6);
        levelOneBirdBody.applyForceToCenter(-9.0f, 0, true);

        //add bird to level one birds array
        levelOneBirdsArray.add(levelOneBirdBody);

        //keep track of time the bird was spawned
        lastLevelOneBirdSpawnTime = TimeUtils.nanoTime();
    }

    public void spawnDodgeables() {
        //class to determine if we need to spawn new dodgeables depending on how much time has passed
        if (TimeUtils.nanoTime() - lastLevelOneBirdSpawnTime > 2000000000) spawnLevelOneBird();
    }

    public void render(float stateTime, SpriteBatch batch) {
        // get current frame of animation for the current stateTime
        TextureRegion backwardsCurrentFrame = levelOneBirdAnimation.getKeyFrame(stateTime, true);

        // draw all level one birds dodgeables using the current animation frame
        for (Body backwardsPigeon : levelOneBirdsArray) {
            batch.draw(backwardsCurrentFrame, backwardsPigeon.getPosition().x, backwardsPigeon.getPosition().y, 0, 0, 6, 6, 1, 1, MathUtils.radiansToDegrees * backwardsPigeon.getAngle());
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

    public void dispose() {
        levelOneBirdFlySheet.dispose();
    }

}
