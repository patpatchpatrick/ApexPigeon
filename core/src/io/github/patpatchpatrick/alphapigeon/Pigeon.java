package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

public class Pigeon {

    private Texture pigeonFlySheet;
    private Animation<TextureRegion> pigeonFlyAnimation;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;
    Body pigeonBody;

    public Pigeon(Body body) {
        pigeonBody = body;
        initializePigeonAnimation();

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

    public void render(float stateTime, SpriteBatch batch) {


        // Get current frame of animation for the current stateTime and render it
        TextureRegion currentFrame = pigeonFlyAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, pigeonBody.getPosition().x, pigeonBody.getPosition().y, 0, 0, 10, 5f, 1, 1, MathUtils.radiansToDegrees * pigeonBody.getAngle());

    }


    public void dispose() {
        pigeonFlySheet.dispose();
    }
}
