package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelTwoBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Meteor;
import io.github.patpatchpatrick.alphapigeon.levels.Gameplay;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Meteors {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Dodgeables dodgeables;

    //Meteor global variables
    private final Array<Meteor> activeMeteors = new Array<Meteor>();
    private final Pool<Meteor> meteorsPool;
    private Texture meteorTextureSpriteSheet;
    private Animation<TextureRegion> meteorAnimation;
    private float lastMeteorSpawnTime;
    private final float METEOR_WIDTH = 80f;
    private final float METEOR_HEIGHT = METEOR_WIDTH / 2;

    //Sounds
    private Sound meteorSound = Gdx.audio.newSound(Gdx.files.internal("sounds/meteor.mp3"));

    public Meteors(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera, Dodgeables dodgeables){

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;
        this.dodgeables = dodgeables;

        // initialize meteor animations
        initializeMeteorAnimation();

        meteorsPool = new Pool<Meteor>() {
            @Override
            protected Meteor newObject() {
                return new Meteor(gameWorld, game, camera);
            }
        };

    }

    public void render(float stateTime, SpriteBatch batch){

        TextureRegion meteorCurrentFrame = meteorAnimation.getKeyFrame(stateTime, true);

        // Render all active meteors
        for (Meteor meteor : activeMeteors) {
            if (meteor.alive) {
                batch.draw(meteorCurrentFrame, meteor.getPosition().x, meteor.getPosition().y, 0, 0, meteor.WIDTH, meteor.HEIGHT, 1, 1, meteor.getAngle());
            } else {
                activeMeteors.removeValue(meteor, false);
                dodgeables.activeDodgeables.removeValue(meteor, false);
            }
        }

    }

    public void update(){

        for (Meteor meteor : activeMeteors){
            if (meteor.getPosition().x < 0 - meteor.WIDTH){
                activeMeteors.removeValue(meteor, false);
                dodgeables.activeDodgeables.removeValue(meteor, false);
                meteorsPool.free(meteor);
            }
        }

    }

    public void spawnMeteor() {

        // Spawn(obtain) a new meteor from the meteors pool and add to list of active meteors

        Meteor meteor = meteorsPool.obtain();
        meteor.init(meteorSound);
        activeMeteors.add(meteor);
        dodgeables.activeDodgeables.add(meteor);

        //keep track of time the meteor was spawned
        lastMeteorSpawnTime = Gameplay.totalGameTime;


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

    public float getLastMeteorSpawnTime(){
        return lastMeteorSpawnTime;
    }

    public void sweepDeadBodies(){

        // If the meteor is flagged for deletion due to a collision, free the meteor from the pool
        // so that it moves off the screen and can be reused

        for (Meteor meteor : activeMeteors){
            if (!meteor.isActive()){
                activeMeteors.removeValue(meteor, false);
                dodgeables.activeDodgeables.removeValue(meteor, false);
                meteorsPool.free(meteor);
            }
        }


    }

    public void dispose(){
        meteorTextureSpriteSheet.dispose();
    }

}
