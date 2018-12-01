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

public class Meteors {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //Meteor global variables
    private Array<Body> meteorArray = new Array<Body>();
    private Texture meteorTextureSpriteSheet;
    private Animation<TextureRegion> meteorAnimation;
    private long lastMeteorSpawnTime;
    private final float METEOR_WIDTH = 80f;
    private final float METEOR_HEIGHT = METEOR_WIDTH / 2;

    public Meteors(World gameWorld, AlphaPigeon game, OrthographicCamera camera){

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        // initialize meteor animations
        initializeMeteorAnimation();

    }

    public void render(float stateTime, SpriteBatch batch){

        TextureRegion meteorCurrentFrame = meteorAnimation.getKeyFrame(stateTime, true);

        // draw all meteors using the current animation frame
        for (Body meteor : meteorArray) {
            if (meteor.isActive()) {
                batch.draw(meteorCurrentFrame, meteor.getPosition().x, meteor.getPosition().y, 0, 0, METEOR_WIDTH, METEOR_HEIGHT, 1, 1, MathUtils.radiansToDegrees * meteor.getAngle());
            } else {
                meteorArray.removeValue(meteor, false);
            }
        }

    }

    public void update(){

    }

    public void spawnMeteor() {

        //spawn a new meteor bird
        BodyDef meteorBodyDef = new BodyDef();
        meteorBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn meteor at random width
        meteorBodyDef.position.set(MathUtils.random(0 - METEOR_WIDTH/2, camera.viewportWidth), camera.viewportHeight + METEOR_HEIGHT/2);
        Body meteorBody = gameWorld.createBody(meteorBodyDef);
        meteorBody.setTransform(meteorBody.getPosition().x, meteorBody.getPosition().y, MathUtils.degreesToRadians*-15);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Meteor.json"));
        FixtureDef meteorFixtureDef = new FixtureDef();
        meteorFixtureDef.density = 0.05f;
        meteorFixtureDef.friction = 0.5f;
        meteorFixtureDef.restitution = 0.3f;
        // set the meteor filter categories and masks for collisions
        meteorFixtureDef.filter.categoryBits = game.CATEGORY_METEOR;
        meteorFixtureDef.filter.maskBits = game.MASK_METEOR;
        loader.attachFixture(meteorBody, "Meteor", meteorFixtureDef, METEOR_WIDTH);
        meteorBody.applyForceToCenter(-3000.0f, -3000.0f, true);

        //add meteor to meteors array
        meteorArray.add(meteorBody);

        //keep track of time the meteor was spawned
        lastMeteorSpawnTime = TimeUtils.nanoTime();

    }

    private void initializeMeteorAnimation() {

        meteorTextureSpriteSheet = new Texture(Gdx.files.internal("sprites/MeteorSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmpMeteor = TextureRegion.split(meteorTextureSpriteSheet,
                meteorTextureSpriteSheet.getWidth() / 8,
                meteorTextureSpriteSheet.getHeight() / 8);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] meteorFrames = new TextureRegion[61 * 1];
        int meteorIndex = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 8; j++) {
                meteorFrames[meteorIndex++] = tmpMeteor[i][j];
            }
        }

        //The meteor sprite region only has 61 frames, so for the last row of the 8x8 sprite grid
        // , only add 5 sprite frames
        for (int j = 0; j < 5; j++) {
            meteorFrames[meteorIndex++] = tmpMeteor[7][j];
        }

        // Initialize the Animation with the frame interval and array of frames
        meteorAnimation = new Animation<TextureRegion>(0.05f, meteorFrames);


    }

    public long getLastMeteorSpawnTime(){
        return lastMeteorSpawnTime;
    }

    public void dispose(){
        meteorTextureSpriteSheet.dispose();
    }

}
