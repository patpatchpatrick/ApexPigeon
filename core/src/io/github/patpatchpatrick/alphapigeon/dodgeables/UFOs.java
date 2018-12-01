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

public class UFOs {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //UFO vehicle variables
    private Array<Body> ufoArray = new Array<Body>();
    private Animation<TextureRegion> ufoAnimation;
    private Texture ufoSheet;
    private final float UFO_WIDTH = 15f;
    private final float UFO_HEIGHT = UFO_WIDTH;
    private float lastUfoSpawnTime;

    //UFO tracking variables
    private boolean ufoIsSpawned = false;


    //UFO Energy Ball variables
    private Array<Body> energyBallArray = new Array<Body>();
    private Animation<TextureRegion> energyBallAnimation;
    private Texture energyBallSheet;
    private float energyBallWidth = 5f;
    private float energyBallHeight = energyBallWidth/2;
    private float energyBallFrameNumber = 0;
    private boolean energyBallSizeEqualsBeamSize = false;
    private boolean energyBeamAnimationComplete = false;

    //UFO Energy Beam variables
    private Array<Body> energyBeamArray = new Array<Body>();
    private Animation<TextureRegion> energyBeamAnimation;
    private Texture energyBeamSheet;
    private final float ENERGY_BEAM_WIDTH = 80f;
    private final float ENERGY_BEAM_HEIGHT = 40f;

    //UFO Static Energy Beam variables
    private Animation<TextureRegion> energyBeamStaticAnimation;
    private Texture energyBeamStaticSheet;
    private float ENERGY_BEAM_STATIC_WIDTH = 80f;
    private float ENERGY_BEAM_STATIC_HEIGHT = 40f;

    public UFOs(World gameWorld, AlphaPigeon game, OrthographicCamera camera){

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        //Initialize ufo animations
        initializeUfoAnimation();

        //Initialize laser beam animations
        initializeEnergyBallAnimation();
        initializeEnergyBeamAnimation();
        initializeEnergyBeamStaticAnimation();

    }

    public void render(float stateTime, SpriteBatch batch){

        TextureRegion ufoCurrentFrame = ufoAnimation.getKeyFrame(stateTime, true);
        TextureRegion energyBallCurrentFrame = energyBallAnimation.getKeyFrame(stateTime, true);
        TextureRegion energyBeamStaticCurrentFrame = energyBeamStaticAnimation.getKeyFrame(stateTime, true);
        TextureRegion energyBeamCurrentFrame = energyBeamAnimation.getKeyFrame(stateTime, true);

        if (!energyBallSizeEqualsBeamSize){
            batch.draw(energyBallCurrentFrame, -2 + ENERGY_BEAM_WIDTH - energyBallWidth, 5 + ENERGY_BEAM_HEIGHT/2 - energyBallHeight/2, 0, 0, energyBallWidth, energyBallHeight, 1, 1, 0);
        } else if (!energyBeamAnimationComplete) {
            batch.draw(energyBeamCurrentFrame, -2, 5, 0, 0, ENERGY_BEAM_WIDTH, ENERGY_BEAM_HEIGHT, 1, 1, 0);
        } else {
            batch.draw(energyBallCurrentFrame, -2 , 5, 0, 0, ENERGY_BEAM_WIDTH, ENERGY_BEAM_HEIGHT, 1, 1, 0);
            batch.draw(energyBeamStaticCurrentFrame, -2, 5, 0, 0, ENERGY_BEAM_STATIC_WIDTH, ENERGY_BEAM_STATIC_HEIGHT, 1, 1,0);
            batch.draw(energyBeamCurrentFrame, -2, 5, 0, 0, ENERGY_BEAM_WIDTH, ENERGY_BEAM_HEIGHT, 1, 1, 0);

        }

        // draw all ufos using the current animation frame
        for (Body ufo : ufoArray) {
            if (ufo.isActive()) {
                batch.draw(ufoCurrentFrame, ufo.getPosition().x, ufo.getPosition().y, 0, 0, UFO_WIDTH, UFO_HEIGHT, 1, 1, MathUtils.radiansToDegrees * ufo.getAngle());
            } else {
                ufoArray.removeValue(ufo, false);
            }
        }


    }

    public void update(float stateTime){

        energyBallWidth = energyBallWidth + 0.2f;
        energyBallHeight = energyBallWidth /2;
        if (energyBallWidth >= ENERGY_BEAM_WIDTH){
            energyBallSizeEqualsBeamSize = true;
            energyBallFrameNumber++;
            if (energyBallFrameNumber > 12){
                energyBeamAnimationComplete = true;
            }
        }

        if (ufoIsSpawned){
        }

    }

    public void spawnUfo(){

        //spawn a new ufo
        BodyDef ufoBodyDef = new BodyDef();
        ufoBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn ufo at random height
        ufoBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - UFO_HEIGHT));
        Body ufoBody = gameWorld.createBody(ufoBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Ufo.json"));
        FixtureDef ufoFixtureDef = new FixtureDef();
        ufoFixtureDef.density = 0.001f;
        ufoFixtureDef.friction = 0.5f;
        ufoFixtureDef.restitution = 0.3f;
        // set the ufo filter categories and masks for collisions
        ufoFixtureDef.filter.categoryBits = game.CATEGORY_UFO;
        ufoFixtureDef.filter.maskBits = game.MASK_UFO;
        loader.attachFixture(ufoBody, "Ufo", ufoFixtureDef, UFO_HEIGHT);
        ufoBody.applyForceToCenter(-9.0f, 0, true);

        //add ufo to ufos array
        ufoArray.add(ufoBody);

        //keep track of time the ufo was spawned
        lastUfoSpawnTime = TimeUtils.nanoTime();

        ufoIsSpawned = true;
    }

    private void initializeUfoAnimation() {

        // Load the ufo sprite sheet as a Texture
        ufoSheet = new Texture(Gdx.files.internal("sprites/UfoSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(ufoSheet,
                ufoSheet.getWidth() / 2,
                ufoSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[2 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 2; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        ufoAnimation = new Animation<TextureRegion>(0.05f, frames);

    }

    private void initializeEnergyBallAnimation() {

        // Load the energy ball sprite sheet as a Texture
        energyBallSheet = new Texture(Gdx.files.internal("sprites/EnergyBallLongSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(energyBallSheet,
                energyBallSheet.getWidth() / 3,
                energyBallSheet.getHeight() / 2);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[3 * 2];
        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        energyBallAnimation = new Animation<TextureRegion>(0.05f, frames);


    }

    private void initializeEnergyBeamAnimation() {

        // Load the energy beam sprite sheet as a Texture
        energyBeamSheet = new Texture(Gdx.files.internal("sprites/EnergyBeamSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(energyBeamSheet,
                energyBeamSheet.getWidth() / 3,
                energyBeamSheet.getHeight() / 4);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[3 * 4];
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        energyBeamAnimation = new Animation<TextureRegion>(0.07f, frames);


    }

    private void initializeEnergyBeamStaticAnimation() {

        // Load the energy beam static sprite sheet as a Texture
        // The static energy beam is the energy beam animation after the energy beam has been shot
        // out of the laser and remains in a straight line
        energyBeamStaticSheet = new Texture(Gdx.files.internal("sprites/EnergyBeamStaticSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(energyBeamStaticSheet,
                energyBeamStaticSheet.getWidth() / 3,
                energyBeamStaticSheet.getHeight() / 3);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[3 * 3];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        energyBeamStaticAnimation = new Animation<TextureRegion>(0.05f, frames);


    }

    public float getLastUfoSpawnTime(){
        return lastUfoSpawnTime;
    }

    public void dispose(){
        energyBeamSheet.dispose();
        energyBallSheet.dispose();
        energyBeamStaticSheet.dispose();
    }



}
