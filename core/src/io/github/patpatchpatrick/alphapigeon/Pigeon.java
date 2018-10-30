package io.github.patpatchpatrick.alphapigeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public class Pigeon {

    private Rectangle pigeonRectangle;
    private Polygon pigeonPolygon;
    private Texture pigeonFlySheet;
    private Texture pigeonTestImage;
    private Animation<TextureRegion> pigeonFlyAnimation;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;
    private static final float INITIAL_PIGEON_Y = 480 / 2 - 50 / 2;
    private static final float INITIAL_PIGEON_X = 20;
    private float pigeonYCoordinate;
    private float pigeonXCoordinate;

    Pigeon() {

        //Pigeon sprite
        pigeonTestImage = new Texture(Gdx.files.internal("PigeonSprite.png"));

        // create a Rectangle to logically represent the pigeon
        pigeonRectangle = new Rectangle();
        pigeonRectangle.x = 20;
        pigeonRectangle.y = 480 / 2 - 50 / 2;
        pigeonRectangle.width = 100;
        pigeonRectangle.height = 50;

        // create a polygon for pigeon collision detection
        // set the origin at 0,0
        // translate the polygon to its starting position
        pigeonPolygon = new Polygon();
        pigeonPolygon.setOrigin(0, 0);
        float[] vertices = new float[]{
                10, 10,
                75 , 50 ,
                75 , 10
        };
        pigeonPolygon.setVertices(vertices);
        pigeonPolygon.translate(INITIAL_PIGEON_X, INITIAL_PIGEON_Y);





        initializePigeonAnimation();

    }

    private void initializePigeonAnimation() {

        // Load the pigeon sprite sheet as a Texture
        pigeonFlySheet = new Texture(Gdx.files.internal("PigeonSpriteSheet.png"));

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

    public void updateAndRender(float stateTime, SpriteBatch batch) {


        // Get current frame of animation for the current stateTime
        TextureRegion currentFrame = pigeonFlyAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, pigeonPolygon.getX(), pigeonPolygon.getY());

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.polygon(pigeonPolygon.getTransformedVertices());
        batch.draw(pigeonTestImage, 0, 0);
        shapeRenderer.end();



    }

    public void setCoordinates(float xCoordinate, float yCoordinate) {
        pigeonPolygon.setPosition(xCoordinate, yCoordinate);
    }

    public void setXCoordinate(float xCoordinate){
        pigeonPolygon.setPosition(xCoordinate, pigeonPolygon.getY());
    }

    public void setYCoordinate(float yCoordinate){
        pigeonPolygon.setPosition(pigeonPolygon.getX(), yCoordinate);
    }


    public void moveHorizontal(float xCoordinate) {
        pigeonPolygon.translate(xCoordinate, 0);
    }

    public void moveVertical(float yCoordinate) {
        pigeonPolygon.translate(0, yCoordinate);
    }

    public float getX() {
        return pigeonPolygon.getX();
    }

    public float getY() {
        return pigeonPolygon.getY();
    }

    public Rectangle getPigeonRectangle() {
        return pigeonRectangle;
    }

    public void dispose() {
        pigeonFlySheet.dispose();
    }
}
