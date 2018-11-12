package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Game;
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

import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class Pigeon {

    private Texture pigeonFlySheet;
    private Animation<TextureRegion> pigeonFlyAnimation;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;
    Body pigeonBody;
    AlphaPigeon game;
    World world;

    public Pigeon(World world, AlphaPigeon game) {

        initializePigeonAnimation();

        this.game =  game;
        this.world  = world;

        // create pigeon body, set position in the world
        // create pigeon fixture, attach the fixture created to the body created with the help of
        // Box 2D editor
        BodyDef pigeonBodyDef = new BodyDef();
        pigeonBodyDef.type = BodyDef.BodyType.KinematicBody;
        pigeonBodyDef.position.set(10, 10);
        pigeonBody = world.createBody(pigeonBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlphaPigeonBody.json"));
        FixtureDef pigeonFixtureDef = new FixtureDef();
        pigeonFixtureDef.density = 0.001f;
        pigeonFixtureDef.friction = 0.5f;
        pigeonFixtureDef.restitution = 0.3f;
        // set the pigeon filter category and mask for collisions
        pigeonFixtureDef.filter.categoryBits = game.CATEGORY_PIGEON;
        pigeonFixtureDef.filter.maskBits = game.MASK_PIGEON;
        //pigeonFixtureDef.isSensor =  true;
        loader.attachFixture(pigeonBody, "AlphaPigeon", pigeonFixtureDef, 10);


    }

    public void powerUp(short powerUpType){

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

    public Body getBody(){
        return pigeonBody;
    }


    public void dispose() {
        pigeonFlySheet.dispose();
    }
}
