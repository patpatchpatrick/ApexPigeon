package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.EnergyBall;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.UFO;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam.UfoEnergyBeamDown;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam.UfoEnergyBeamLeft;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam.UfoEnergyBeamRight;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam.UfoEnergyBeamUp;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class UFOs {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //UFO vehicle variables
    private final Array<UFO> activeUFOs = new Array<UFO>();
    private final Pool<UFO> ufoPool;
    private Animation<TextureRegion> ufoAnimation;
    private Texture ufoSheet;
    private final float UFO_WIDTH = 15f;
    private final float UFO_HEIGHT = UFO_WIDTH;
    private long lastUfoSpawnTime;

    //UFO tracking variables

    //UFO Energy Ball variables
    private Array<Body> energyBallArray = new Array<Body>();
    private Animation<TextureRegion> energyBallAnimation;
    private Texture energyBallSheet;
    private final float ENERGY_BALL_INITIAL_WIDTH = 5f;
    private final float ENERGY_BALL_INITIAL_HEIGHT = ENERGY_BALL_INITIAL_WIDTH / 2;

    //UFO Energy Beam variables
    private final Array<UfoEnergyBeamLeft> activeEnergyBeamLefts = new Array<UfoEnergyBeamLeft>();
    private final Pool<UfoEnergyBeamLeft> ufoEnergyBeamLeftPool;
    private final Array<UfoEnergyBeamRight> activeEnergyBeamRights = new Array<UfoEnergyBeamRight>();
    private final Pool<UfoEnergyBeamRight> ufoEnergyBeamRightPool;
    private final Array<UfoEnergyBeamDown> activeEnergyBeamDowns = new Array<UfoEnergyBeamDown>();
    private final Pool<UfoEnergyBeamDown> ufoEnergyBeamDownPool;
    private final Array<UfoEnergyBeamUp> activeEnergyBeamUps = new Array<UfoEnergyBeamUp>();
    private final Pool<UfoEnergyBeamUp> ufoEnergyBeamUpPool;
    private Animation<TextureRegion> energyBeamAnimation;
    private Texture energyBeamSheet;
    private final float ENERGY_BEAM_WIDTH = 80f;
    private final float ENERGY_BEAM_HEIGHT = 40f;
    private final float ENERGY_BEAM_VERTICAL_WIDTH = 40f;
    private final float ENERGY_BEAM_VERTICAL_HEIGHT = 80f;
    //UFO Energy Beam Directions
    public final float ENERGY_BEAM_LEFT = 0f;
    public final float ENERGY_BEAM_RIGHT = 1f;
    public final float ENERGY_BEAM_UP = 2f;
    public final float ENERGY_BEAM_DOWN = 3f;
    public final float ENERGY_BEAM_RANDOM = 4f;
    public final float ENERGY_BEAM_ALL_DIRECTIONS = 5f;
    public final float ENERGY_BEAM_HORIZONAL_DIRECTIONS = 6f;
    public final float ENERGY_BEAM_VERTICAL_DIRECTIONS = 7f;

    //UFO Static Energy Beam variables
    private Animation<TextureRegion> energyBeamStaticAnimation;
    private Texture energyBeamStaticSheet;

    public UFOs(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera) {

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        //Initialize ufo animations
        initializeUfoAnimation();

        //Initialize laser beam animations
        initializeEnergyBallAnimation();
        initializeEnergyBeamAnimation();
        initializeEnergyBeamStaticAnimation();

        ufoPool = new Pool<UFO>() {
            @Override
            protected UFO newObject() {
                return new UFO(gameWorld, game, camera);
            }
        };

        ufoEnergyBeamLeftPool = new Pool<UfoEnergyBeamLeft>() {
            @Override
            protected UfoEnergyBeamLeft newObject() {
                return new UfoEnergyBeamLeft(gameWorld, game, camera);
            }
        };

        ufoEnergyBeamRightPool = new Pool<UfoEnergyBeamRight>() {
            @Override
            protected UfoEnergyBeamRight newObject() {
                return new UfoEnergyBeamRight(gameWorld, game, camera);
            }
        };

        ufoEnergyBeamDownPool = new Pool<UfoEnergyBeamDown>() {
            @Override
            protected UfoEnergyBeamDown newObject() {
                return new UfoEnergyBeamDown(gameWorld, game, camera);
            }
        };

        ufoEnergyBeamUpPool = new Pool<UfoEnergyBeamUp>() {
            @Override
            protected UfoEnergyBeamUp newObject() {
                return new UfoEnergyBeamUp(gameWorld, game, camera);
            }
        };

    }

    public void render(float stateTime, SpriteBatch batch) {

        TextureRegion ufoCurrentFrame = ufoAnimation.getKeyFrame(stateTime, true);
        TextureRegion energyBallCurrentFrame = energyBallAnimation.getKeyFrame(stateTime, true);
        TextureRegion energyBeamStaticCurrentFrame = energyBeamStaticAnimation.getKeyFrame(stateTime, true);
        TextureRegion energyBeamCurrentFrame = energyBeamAnimation.getKeyFrame(stateTime, true);

        // Render all active ufos
        for (UFO ufo : activeUFOs) {
            if (ufo.alive) {
                BodyData ufoData = (BodyData) ufo.dodgeableBody.getUserData();
                Boolean energyBallIsSpawned = false;
                if (ufoData != null) {
                    energyBallIsSpawned = ufoData.ufoEnergyBallIsSpawned();
                }
                // draw the UFO vehicle at the UFO body position
                batch.draw(ufoCurrentFrame, ufo.getPosition().x, ufo.getPosition().y, 0, 0, ufo.WIDTH, ufo.HEIGHT, 1, 1, ufo.getAngle());

                // Energy ball/beam render methods
                if (energyBallIsSpawned) {

                    //Render all energy balls associated with the UFO
                    Array<EnergyBall> energyBalls = ufoData.getEnergyBalls();
                    for (EnergyBall energyBall : energyBalls){

                        float energyBallWidth = energyBall.getWidth();
                        float energyBallHeight = energyBall.getHeight();
                        float energyBallDirection = energyBall.getDirection();
                        Boolean energyBallIsCharged = energyBall.isCharged();
                        Boolean energyBallAnimationIsComplete = energyBall.animationIsComplete();


                        //Get positions of UFOs and EnergyBeams relative to UFOs
                        float ufoXPosition = ufo.getPosition().x;
                        float ufoYPosition = ufo.getPosition().y + UFO_HEIGHT / 2;
                        float energyBeamXPosition = 0;
                        float energyBeamYPosition = ufoYPosition - ENERGY_BEAM_HEIGHT / 2;
                        float energyBeamXScale = 1;
                        float energyBallXPosition = 0;
                        float energyBallYPosition = ufoYPosition - energyBallHeight / 2;
                        float energyBeamRotation = 0;


                        if (energyBallDirection == ENERGY_BEAM_LEFT) {
                            energyBallXPosition = ufoXPosition - energyBallWidth;
                            energyBeamXPosition = ufoXPosition - ENERGY_BEAM_WIDTH;
                        } else if (energyBallDirection == ENERGY_BEAM_RIGHT) {
                            energyBallXPosition = ufoXPosition + UFO_WIDTH + 1f - (0.88f) * energyBallWidth;
                            energyBeamXPosition = ufoXPosition + UFO_WIDTH;
                            energyBeamXScale = -1;
                        } else if (energyBallDirection == ENERGY_BEAM_DOWN) {
                            energyBallXPosition = ufoXPosition + 8f - (0.94f) * energyBallWidth;
                            energyBallYPosition = ufoYPosition - UFO_WIDTH / 4 - (0.6f) * energyBallHeight;
                            energyBeamXPosition = ufoXPosition - 32f;
                            energyBeamYPosition = ufoYPosition - 62f;
                            energyBeamRotation = 90;
                        } else if (energyBallDirection == ENERGY_BEAM_UP) {
                            energyBallXPosition = ufoXPosition + 8f - (0.94f) * energyBallWidth;
                            energyBallYPosition = ufoYPosition - UFO_WIDTH / 4 - (0.4f) * energyBallHeight + UFO_HEIGHT - 6f;
                            energyBeamXPosition = ufoXPosition - 33f;
                            energyBeamYPosition = ufoYPosition - 63f + ENERGY_BEAM_VERTICAL_HEIGHT + UFO_HEIGHT/2;
                            energyBeamRotation = -90;
                        }

                        // If the energy ball is not fully charged, render it using its current width and height
                        // If the ball is fully charged, render the energy ball to beam transition animation
                        // If the transition animation is complete, render the energy beam
                        if (!energyBallIsCharged) {
                            batch.draw(energyBallCurrentFrame, energyBallXPosition, energyBallYPosition, 0, 0, energyBallWidth, energyBallHeight, 1, 1, 0);
                        } else if (!energyBallAnimationIsComplete) {
                            batch.draw(energyBeamCurrentFrame, energyBeamXPosition, energyBeamYPosition, ENERGY_BEAM_WIDTH / 2, ENERGY_BEAM_HEIGHT / 2, ENERGY_BEAM_WIDTH, ENERGY_BEAM_HEIGHT, energyBeamXScale, 1, energyBeamRotation);
                        }

                    }
                }
            } else {
                activeUFOs.removeValue(ufo, false);
            }
        }

        // Render all active energy beams
        for (UfoEnergyBeamLeft ufoEnergyBeamLeft : activeEnergyBeamLefts) {
            if (ufoEnergyBeamLeft.alive) {
               renderEnergyBeam(ufoEnergyBeamLeft, batch, energyBallCurrentFrame, energyBeamStaticCurrentFrame, energyBeamCurrentFrame);
            } else {
                activeEnergyBeamLefts.removeValue(ufoEnergyBeamLeft, false);
            }
        }

        // Render all active energy beams
        for (UfoEnergyBeamRight ufoEnergyBeamRight : activeEnergyBeamRights) {
            if (ufoEnergyBeamRight.alive) {
                renderEnergyBeam(ufoEnergyBeamRight, batch, energyBallCurrentFrame, energyBeamStaticCurrentFrame, energyBeamCurrentFrame);
            } else {
                activeEnergyBeamRights.removeValue(ufoEnergyBeamRight, false);
            }
        }

        // Render all active energy beams
        for (UfoEnergyBeamDown ufoEnergyBeamDown : activeEnergyBeamDowns) {
            if (ufoEnergyBeamDown.alive) {
                renderEnergyBeam(ufoEnergyBeamDown, batch, energyBallCurrentFrame, energyBeamStaticCurrentFrame, energyBeamCurrentFrame);
            } else {
                activeEnergyBeamDowns.removeValue(ufoEnergyBeamDown, false);
            }
        }

        // Render all active energy beams
        for (UfoEnergyBeamUp ufoEnergyBeamUp : activeEnergyBeamUps) {
            if (ufoEnergyBeamUp.alive) {
                renderEnergyBeam(ufoEnergyBeamUp, batch, energyBallCurrentFrame, energyBeamStaticCurrentFrame, energyBeamCurrentFrame);
            } else {
                activeEnergyBeamUps.removeValue(ufoEnergyBeamUp, false);
            }
        }

        
    }
    
    private void renderEnergyBeam(Dodgeable energyBeam, SpriteBatch batch, TextureRegion energyBallCurrentFrame, TextureRegion energyBeamStaticCurrentFrame, TextureRegion energyBeamCurrentFrame){

        BodyData energyBeamData = (BodyData) energyBeam.dodgeableBody.getUserData();
        float energyBeamDirection = 0;
        float energyBeamXScale;
        float energyBeamRotation = 0;

        float energyBeamXPosition = energyBeam.getPosition().x;
        float energyBeamYPosition = energyBeam.getPosition().y;

        // Based on the energy beams direction, determine what the image scale should be
        // determine if the image should be flipped or not depending on the direction of the beam
        if (energyBeamData != null) {
            energyBeamDirection = energyBeamData.getEnergyBeamDirection();
        }
        if (energyBeamDirection == ENERGY_BEAM_RIGHT) {
            energyBeamXScale = -1;
        } else if (energyBeamDirection == ENERGY_BEAM_DOWN) {
            energyBeamXScale = 1;
            energyBeamRotation = 90;
            energyBeamXPosition = energyBeamXPosition - ENERGY_BEAM_VERTICAL_WIDTH / 2 + 0.5f;
            energyBeamYPosition = energyBeamYPosition + ENERGY_BEAM_VERTICAL_WIDTH / 2 - 2f;

        } else if (energyBeamDirection == ENERGY_BEAM_UP) {
            energyBeamXScale = 1;
            energyBeamRotation = -90;
            energyBeamXPosition = energyBeamXPosition - ENERGY_BEAM_VERTICAL_WIDTH / 2 + -0.5f;
            energyBeamYPosition = energyBeamYPosition + ENERGY_BEAM_VERTICAL_WIDTH / 2 + 4f;

        } else {
            energyBeamXScale = 1;
        }


        // overlay the energy ball, the energy beam animation and the static energy beam to
        // complete the energy beam animation
        batch.draw(energyBallCurrentFrame, energyBeamXPosition, energyBeamYPosition, ENERGY_BEAM_WIDTH / 2, ENERGY_BEAM_HEIGHT / 2, ENERGY_BEAM_WIDTH, ENERGY_BEAM_HEIGHT, energyBeamXScale, 1, energyBeamRotation);
        batch.draw(energyBeamStaticCurrentFrame, energyBeamXPosition, energyBeamYPosition, ENERGY_BEAM_WIDTH / 2, ENERGY_BEAM_HEIGHT / 2, ENERGY_BEAM_WIDTH, ENERGY_BEAM_HEIGHT, energyBeamXScale, 1, energyBeamRotation);
        batch.draw(energyBeamCurrentFrame, energyBeamXPosition, energyBeamYPosition, ENERGY_BEAM_WIDTH / 2, ENERGY_BEAM_HEIGHT / 2, ENERGY_BEAM_WIDTH, ENERGY_BEAM_HEIGHT, energyBeamXScale, 1, energyBeamRotation);
        
        
        
    }

    public void update(float stateTime) {

        long currentTimeInMillis = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;

        //Update all ufos
        //If the ufo is spawned long enough... create an energy ball that will ultimately turn
        //into an energy beam when it is fully charged
        //Slowly increment the energy ball size until it is fully charged
        //After the energy ball width matches the beam width, it is fully charged and ready to be a beam
        for (UFO ufo : activeUFOs){
            BodyData ufoData = (BodyData) ufo.dodgeableBody.getUserData();
            if (ufoData != null) {
                if (!ufoData.ufoEnergyBallIsSpawned()) {
                    long ufoSpawnTime = ufoData.getSpawnTime();
                    if (currentTimeInMillis - ufoSpawnTime > 5000) {
                            //Spawn an energy ball after a set amount of time
                            spawnEnergyBall(ufoData, ufo.direction);
                    }
                } else {
                    Array<EnergyBall> energyBalls = ufoData.getEnergyBalls();
                    for (int i = 0, n = energyBalls.size; i < n ; i ++){
                        EnergyBall energyBall = energyBalls.get(i);
                        Boolean energyBeamIsSpawned = energyBall.getEnergyBeamIsSpawned();
                        if (energyBall != null) {
                            if (energyBall.getWidth() >= ENERGY_BEAM_WIDTH) {
                                energyBall.setCharged(true);
                                energyBall.incrementFrameNumber();
                                if (energyBall.getFrameNumber() > 12f && !energyBeamIsSpawned) {
                                    energyBall.setAnimationIsComplete(true);
                                    energyBall.setEnergyBeamIsSpawned(true);
                                    spawnEnergyBeam(ufo, ufoData);
                                }
                            } else {
                                energyBall.increaseWidth(0.2f);
                                energyBall.increaseHeight(0.1f);
                            }
                        }
                    }
                }
            }
        }

        //REMOVE OFF SCREEN UFOs
        for (UFO ufo : activeUFOs){
            if (ufo.getPosition().x < 0 - ufo.WIDTH){
                activeUFOs.removeValue(ufo, false);
                ufoPool.free(ufo);
            }
        }

        //REMOVE ENERGY BEAMS ATTACHED TO UFOs that aren't alive
        for (UfoEnergyBeamLeft ufoEnergyBeamLeft : activeEnergyBeamLefts){
            Boolean removeBeamFromPool = false;
            BodyData ufoData = (BodyData) ufoEnergyBeamLeft.dodgeableBody.getUserData();
            if (ufoData != null){
                UFO ufo = ufoData.getUfo();
                if (ufo == null || !ufo.alive){
                    removeBeamFromPool = true;
                }
            } else {
                removeBeamFromPool = true;
            }
           
            if (removeBeamFromPool){
                activeEnergyBeamLefts.removeValue(ufoEnergyBeamLeft, false);
                ufoEnergyBeamLeftPool.free(ufoEnergyBeamLeft);
            }
        }

        //REMOVE ENERGY BEAMS ATTACHED TO UFOs that aren't alive
        for (UfoEnergyBeamRight ufoEnergyBeamRight : activeEnergyBeamRights){
            Boolean removeBeamFromPool = false;
            BodyData ufoData = (BodyData) ufoEnergyBeamRight.dodgeableBody.getUserData();
            if (ufoData != null){
                UFO ufo = ufoData.getUfo();
                if (ufo == null || !ufo.alive){
                    removeBeamFromPool = true;
                }
            } else {
                removeBeamFromPool = true;
            }

            if (removeBeamFromPool){
                activeEnergyBeamRights.removeValue(ufoEnergyBeamRight, false);
                ufoEnergyBeamRightPool.free(ufoEnergyBeamRight);
            }
        }

        //REMOVE ENERGY BEAMS ATTACHED TO UFOs that aren't alive
        for (UfoEnergyBeamDown ufoEnergyBeamDown : activeEnergyBeamDowns){
            Boolean removeBeamFromPool = false;
            BodyData ufoData = (BodyData) ufoEnergyBeamDown.dodgeableBody.getUserData();
            if (ufoData != null){
                UFO ufo = ufoData.getUfo();
                if (ufo == null || !ufo.alive){
                    removeBeamFromPool = true;
                }
            } else {
                removeBeamFromPool = true;
            }

            if (removeBeamFromPool){
                activeEnergyBeamDowns.removeValue(ufoEnergyBeamDown, false);
                ufoEnergyBeamDownPool.free(ufoEnergyBeamDown);
            }
        }

        //REMOVE ENERGY BEAMS ATTACHED TO UFOs that aren't alive
        for (UfoEnergyBeamUp ufoEnergyBeamUp : activeEnergyBeamUps){
            Boolean removeBeamFromPool = false;
            BodyData ufoData = (BodyData) ufoEnergyBeamUp.dodgeableBody.getUserData();
            if (ufoData != null){
                UFO ufo = ufoData.getUfo();
                if (ufo == null || !ufo.alive){
                    removeBeamFromPool = true;
                }
            } else {
                removeBeamFromPool = true;
            }

            if (removeBeamFromPool){
                activeEnergyBeamUps.removeValue(ufoEnergyBeamUp, false);
                ufoEnergyBeamUpPool.free(ufoEnergyBeamUp);
            }
        }

    }

    public void spawnUfo(float direction) {

        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs

        UFO ufo = ufoPool.obtain();
        ufo.init(direction);
        activeUFOs.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;

    }

    public void spawnHorizontalUfo(float direction){

        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs
        // Horizontal Ufos start in the vertical middle of the right of the screen and move slowly
        // towards the left of the screen horizontally

        UFO ufo = ufoPool.obtain();
        ufo.initHorizontal(direction);
        activeUFOs.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;



    }

    public void spawnEnergyBall(BodyData ufoData, float direction) {

        //Spawn a UFO energy ball
        //The energy ball is what spawns next to the UFO before the energy beam is launched
        //The energy ball slowly grows in size over time until it has enough energy to shoot the beam
        //The direction input is the side of the UFO that the energy ball/beam will shoot from

        //If the direction is RANDOM, spawn the ball in a random direction
        //If the direction is ALL, spawn four balls in all directions
        //Otherwise, spawn the ball in chosen direction

        if (direction == ENERGY_BEAM_RANDOM){
            float randomEnergyBeamDirection = MathUtils.random(0, 3);
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, randomEnergyBeamDirection));
            ufoData.setUfoEnergyBallIsSpawned(true);
        } else if (direction == ENERGY_BEAM_ALL_DIRECTIONS){
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_LEFT));
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_UP));
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_RIGHT));
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_DOWN));
            ufoData.setUfoEnergyBallIsSpawned(true);
        } else if (direction == ENERGY_BEAM_HORIZONAL_DIRECTIONS){
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_LEFT));
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_RIGHT));
            ufoData.setUfoEnergyBallIsSpawned(true);
        } else if (direction == ENERGY_BEAM_VERTICAL_DIRECTIONS){
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_UP));
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_DOWN));
            ufoData.setUfoEnergyBallIsSpawned(true);
        } else {
            ufoData.setEnergyBall(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, direction));
            ufoData.setUfoEnergyBallIsSpawned(true);
        }


    }

    private void spawnEnergyBeam(UFO ufo, BodyData ufoData) {

        // For all  energy balls associated with the UFO, spawn energy beams

        Array<EnergyBall> energyBalls = ufoData.getEnergyBalls();
        for (EnergyBall energyBall : energyBalls){

            //get the direction of the beam from the direction of the energy ball attached to the UFO
            float energyBeamDirection = energyBall.getDirection();

            if (energyBeamDirection == ENERGY_BEAM_LEFT) {

                // Spawn(obtain) a new energy beam left from the energy beam left pool and add to list of active energy beam lefts

                UfoEnergyBeamLeft ufoEnergyBeamLeft = ufoEnergyBeamLeftPool.obtain();
                ufoEnergyBeamLeft.init(ufo, energyBeamDirection);
                activeEnergyBeamLefts.add(ufoEnergyBeamLeft);


            } else if (energyBeamDirection == ENERGY_BEAM_RIGHT) {

                // Spawn(obtain) a new energy beam Right from the energy beam Right pool and add to list of active energy beam Rights

                UfoEnergyBeamRight ufoEnergyBeamRight = ufoEnergyBeamRightPool.obtain();
                ufoEnergyBeamRight.init(ufo, energyBeamDirection);
                activeEnergyBeamRights.add(ufoEnergyBeamRight);

            } else if (energyBeamDirection == ENERGY_BEAM_DOWN) {

                // Spawn(obtain) a new energy beam Down from the energy beam Down pool and add to list of active energy beam Downs

                UfoEnergyBeamDown ufoEnergyBeamDown = ufoEnergyBeamDownPool.obtain();
                ufoEnergyBeamDown.init(ufo, energyBeamDirection);
                activeEnergyBeamDowns.add(ufoEnergyBeamDown);

            } else if (energyBeamDirection == ENERGY_BEAM_UP) {

                // Spawn(obtain) a new energy beam Up from the energy beam Up pool and add to list of active energy beam Ups

                UfoEnergyBeamUp ufoEnergyBeamUp = ufoEnergyBeamUpPool.obtain();
                ufoEnergyBeamUp.init(ufo, energyBeamDirection);
                activeEnergyBeamUps.add(ufoEnergyBeamUp);

            }


        }


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

    public float getLastUfoSpawnTime() {
        return lastUfoSpawnTime;
    }

    public void sweepDeadBodies(){

        // If any UFO objects are flagged for deletion, free them from the pool
        // so that they move off the screen and can be reused

        for (UFO ufo : activeUFOs){
            if (!ufo.isActive()){
                activeUFOs.removeValue(ufo, false);
                ufoPool.free(ufo);
            }
        }

        for (UfoEnergyBeamLeft ufoEnergyBeamLeft : activeEnergyBeamLefts){
            if (!ufoEnergyBeamLeft.isActive()){
                activeEnergyBeamLefts.removeValue(ufoEnergyBeamLeft, false);
                ufoEnergyBeamLeftPool.free(ufoEnergyBeamLeft);
            }
        }

        for (UfoEnergyBeamRight ufoEnergyBeamRight : activeEnergyBeamRights){
            if (!ufoEnergyBeamRight.isActive()){
                activeEnergyBeamRights.removeValue(ufoEnergyBeamRight, false);
                ufoEnergyBeamRightPool.free(ufoEnergyBeamRight);
            }
        }

        for (UfoEnergyBeamDown ufoEnergyBeamDown : activeEnergyBeamDowns){
            if (!ufoEnergyBeamDown.isActive()){
                activeEnergyBeamDowns.removeValue(ufoEnergyBeamDown, false);
                ufoEnergyBeamDownPool.free(ufoEnergyBeamDown);
            }
        }

        for (UfoEnergyBeamUp ufoEnergyBeamUp : activeEnergyBeamUps){
            if (!ufoEnergyBeamUp.isActive()){
                activeEnergyBeamUps.removeValue(ufoEnergyBeamUp, false);
                ufoEnergyBeamUpPool.free(ufoEnergyBeamUp);
            }
        }

    }

    public void dispose() {
        ufoSheet.dispose();
        energyBeamSheet.dispose();
        energyBallSheet.dispose();
        energyBeamStaticSheet.dispose();
    }


}
