package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.Pigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class Dodgeables {

    //Class to define objects that the player should dodge

    private Pigeon pigeon;

    private Array<Body> backwardsPigeonObjects;
    private Animation<TextureRegion> backwardsPigeonFlyAnimation;
    private Animation<TextureRegion> divingPigeonFlyAnimation;
    private Texture backwardsPigeonFlySheet;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;
    World gameWorld;

    private Texture divingPigeonFlySheet;

    private long lastDropTime;


    private int dodgeableSpeed;

    public Dodgeables(Pigeon pigeon, World world){

        // define the pigeon
        this.pigeon = pigeon;
        gameWorld = world;

        // load images for the backwards bird
        backwardsPigeonObjects = new Array<Body>();

        // initialize animations
        initializeBackwardsPigeonAnimation();
        initializeDivingPigeonAnimation();

        // initialize dodgeable speed
        dodgeableSpeed = 200;
    }

    public void spawnBackwardsPigeon(){
        BodyDef bdBack = new BodyDef();
        bdBack.type = BodyDef.BodyType.DynamicBody;
        bdBack.position.set(80, MathUtils.random(0, 48 - 6));
// Create our body in the world using our body definition
        Body bodyBack = gameWorld.createBody(bdBack);

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("BackwardsPigeon.json"));

        // 2. Create a FixtureDef, as usual.`
        FixtureDef fd = new FixtureDef();
        fd.density = 0.001f;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;

        // 3. Create a Body, as usual.`
        loader.attachFixture(bodyBack, "BackwardsPigeon", fd, 6);
        bodyBack.applyForceToCenter(-9.0f, 0, true);

        backwardsPigeonObjects.add(bodyBack);
        lastDropTime = TimeUtils.nanoTime();
    }

    public void spawnDodgeables(){
        //Class to determine if we need to spawn new dodgeables depending on how much time has passed
        if (TimeUtils.nanoTime() - lastDropTime > 2000000000) spawnBackwardsPigeon();
    }

    public void updateAndRender(float stateTime, SpriteBatch batch){
        // Get current frame of animation for the current stateTime
        TextureRegion backwardsCurrentFrame = divingPigeonFlyAnimation.getKeyFrame(stateTime, true);

        // Draw all backwards pigeon dodgeables
        for (Body backwardsPigeon : backwardsPigeonObjects) {
            batch.draw(backwardsCurrentFrame, backwardsPigeon.getPosition().x, backwardsPigeon.getPosition().y,0, 0, 6, 6, 1, 1, MathUtils.radiansToDegrees * backwardsPigeon.getAngle());

        }

        //Check if we need to spawn new dodgeables depending on game time
        spawnDodgeables();

        /**
        for (Iterator<Body> iter = backwardsPigeonObjects.iterator(); iter.hasNext(); ) {
            Body backwardsPigeonRect = iter.next();
            backwardsPigeonRect.x -= dodgeableSpeed * Gdx.graphics.getDeltaTime();
            if (backwardsPigeonRect.x + 64 < 0) iter.remove();
            //Add code to remove pigeon if it collides
        }**/
    }

    public void initializeBackwardsPigeonAnimation(){

        // Load the pigeon sprite sheet as a Texture
        backwardsPigeonFlySheet = new Texture(Gdx.files.internal("BackwardsPigeonSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmpBackwards = TextureRegion.split(backwardsPigeonFlySheet,
                backwardsPigeonFlySheet.getWidth() / FRAME_COLS,
                backwardsPigeonFlySheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] backwardsPigeonFlyFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int backwardsIndex = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                backwardsPigeonFlyFrames[backwardsIndex++] = tmpBackwards[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        backwardsPigeonFlyAnimation = new Animation<TextureRegion>(0.05f, backwardsPigeonFlyFrames);

    }

    public void initializeDivingPigeonAnimation(){

        // Load the pigeon sprite sheet as a Texture
        divingPigeonFlySheet = new Texture(Gdx.files.internal("DivingPigeonSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmpDiving = TextureRegion.split(divingPigeonFlySheet,
                divingPigeonFlySheet.getWidth() / 3,
                divingPigeonFlySheet.getHeight() / 2);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] divingPigeonFlyFrames = new TextureRegion[3 * 2];
        int divingIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                divingPigeonFlyFrames[divingIndex++] = tmpDiving[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        divingPigeonFlyAnimation = new Animation<TextureRegion>(0.05f, divingPigeonFlyFrames);


    }

    public void dispose(){
        backwardsPigeonFlySheet.dispose();
    }

}
