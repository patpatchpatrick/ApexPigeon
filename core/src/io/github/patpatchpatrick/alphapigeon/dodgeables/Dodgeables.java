package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.Pigeon;

public class Dodgeables {

    //Class to define objects that the player should dodge

    private Pigeon pigeon;

    private Texture backwardsPigeonTexture;
    private Array<Rectangle> backwardsPigeonObjects;
    private long lastDropTime;

    private Animation<TextureRegion> backwardsPigeonFlyAnimation;
    private Animation<TextureRegion> divingPigeonFlyAnimation;
    private Texture backwardsPigeonFlySheet;
    private Texture divingPigeonFlySheet;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;

    private int dodgeableSpeed;

    public Dodgeables(Pigeon pigeon){

        // define the pigeon
        this.pigeon = pigeon;

        // load images for the backwards bird
        backwardsPigeonTexture = new Texture(Gdx.files.internal("SquareObjectShape.png"));
        backwardsPigeonObjects = new Array<Rectangle>();


        // initialize animations
        initializeBackwardsPigeonAnimation();
        initializeDivingPigeonAnimation();

        // initialize dodgeable speed
        dodgeableSpeed = 200;
    }

    public void spawnBackwardsPigeon(){
        Rectangle backwardsPigeonRectangle = new Rectangle();
        backwardsPigeonRectangle.x = 800;
        backwardsPigeonRectangle.y = MathUtils.random(0, 480 - 64);
        backwardsPigeonRectangle.width = 64;
        backwardsPigeonRectangle.height = 64;
        backwardsPigeonObjects.add(backwardsPigeonRectangle);
        lastDropTime = TimeUtils.nanoTime();
    }

    public void spawnDodgeables(){
        //Class to determine if we need to spawn new dodgeables depending on how much time has passed
        if (TimeUtils.nanoTime() - lastDropTime > 2000000000) spawnBackwardsPigeon();
    }

    public void updateAndRender(float stateTime, SpriteBatch batch){
        // Get current frame of animation for the current stateTime
        TextureRegion backwardsCurrentFrame = backwardsPigeonFlyAnimation.getKeyFrame(stateTime, true);

        // Draw all backwards pigeon dodgeables
        for (Rectangle backwardsPigeon : backwardsPigeonObjects) {
            batch.draw(backwardsCurrentFrame, backwardsPigeon.x, backwardsPigeon.y);
        }

        //Check if we need to spawn new dodgeables depending on game time
        spawnDodgeables();

        for (Iterator<Rectangle> iter = backwardsPigeonObjects.iterator(); iter.hasNext(); ) {
            Rectangle backwardsPigeonRect = iter.next();
            backwardsPigeonRect.x -= dodgeableSpeed * Gdx.graphics.getDeltaTime();
            if (backwardsPigeonRect.x + 64 < 0) iter.remove();
            if (backwardsPigeonRect.overlaps(pigeon.getPigeonRectangle())) {
                AlphaPigeon.dropSound.play();
                iter.remove();
            }
        }
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
        backwardsPigeonTexture.dispose();
    }

}
