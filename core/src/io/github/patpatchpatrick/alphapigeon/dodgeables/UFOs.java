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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.EnergyBall;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.UFO;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam.UfoEnergyBeamDown;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam.UfoEnergyBeamLeft;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam.UfoEnergyBeamRight;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam.UfoEnergyBeamUp;
import io.github.patpatchpatrick.alphapigeon.levels.Gameplay;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class UFOs {

    Dodgeables dodgeables;
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
    private float lastUfoSpawnTime;
    private HashMap<Float, Float> lastSpawnTimeByLevel = new HashMap<Float, Float>();

    //UFO tracking variables

    //UFO Energy Ball variables
    private Animation<TextureRegion> energyBallAnimation;
    private Texture energyBallSheet;
    private final float ENERGY_BALL_INITIAL_WIDTH = 5f;
    private final float ENERGY_BALL_INITIAL_HEIGHT = ENERGY_BALL_INITIAL_WIDTH / 2;
    private final float ENERGY_BALL_TIME_BEFORE_SPAWN  = 5f; //seconds

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


    public UFOs(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera, final Dodgeables dodgeables) {

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;
        this.dodgeables = dodgeables;

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

                // draw the UFO vehicle at the UFO body position
                batch.draw(ufoCurrentFrame, ufo.getPosition().x, ufo.getPosition().y, 0, 0, ufo.WIDTH, ufo.HEIGHT, 1, 1, ufo.getAngle());

                // Energy ball/beam render methods
                if (ufo.energyBallIsSpawned) {

                    //Render all energy balls associated with the UFO
                    for (EnergyBall energyBall : ufo.energyBalls) {

                        Boolean energyBallIsCharged = energyBall.isCharged();
                        Boolean energyBallAnimationIsComplete = energyBall.animationIsComplete();

                        // If the energy ball is not fully charged, render it using its current width and height
                        // If the ball is fully charged, render the energy ball to beam transition animation
                        // If the transition animation is complete, render the energy beam
                        if (!energyBallIsCharged) {
                            batch.draw(energyBallCurrentFrame, energyBall.energyBallXPosition, energyBall.energyBallYPosition, 0, 0, energyBall.getWidth(), energyBall.getHeight(), 1, 1, 0);
                        } else if (!energyBallAnimationIsComplete) {
                            batch.draw(energyBeamCurrentFrame, energyBall.energyBeamXPosition, energyBall.energyBeamYPosition, ENERGY_BEAM_WIDTH / 2, ENERGY_BEAM_HEIGHT / 2, ENERGY_BEAM_WIDTH, ENERGY_BEAM_HEIGHT, energyBall.energyBeamXScale, 1, energyBall.energyBeamRotation);
                        }

                    }
                }
            } else {
                activeUFOs.removeValue(ufo, false);
                dodgeables.activeDodgeables.removeValue(ufo, false);
            }
        }

        // Render all active energy beams
        for (UfoEnergyBeamLeft ufoEnergyBeamLeft : activeEnergyBeamLefts) {
            if (ufoEnergyBeamLeft.alive) {
                renderEnergyBeam(ufoEnergyBeamLeft, batch, energyBallCurrentFrame, energyBeamStaticCurrentFrame, energyBeamCurrentFrame);
            } else {
                activeEnergyBeamLefts.removeValue(ufoEnergyBeamLeft, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamLeft, false);
            }
        }

        // Render all active energy beams
        for (UfoEnergyBeamRight ufoEnergyBeamRight : activeEnergyBeamRights) {
            if (ufoEnergyBeamRight.alive) {
                renderEnergyBeam(ufoEnergyBeamRight, batch, energyBallCurrentFrame, energyBeamStaticCurrentFrame, energyBeamCurrentFrame);
            } else {
                activeEnergyBeamRights.removeValue(ufoEnergyBeamRight, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamRight, false);
            }
        }

        // Render all active energy beams
        for (UfoEnergyBeamDown ufoEnergyBeamDown : activeEnergyBeamDowns) {
            if (ufoEnergyBeamDown.alive) {
                renderEnergyBeam(ufoEnergyBeamDown, batch, energyBallCurrentFrame, energyBeamStaticCurrentFrame, energyBeamCurrentFrame);
            } else {
                activeEnergyBeamDowns.removeValue(ufoEnergyBeamDown, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamDown, false);
            }
        }

        // Render all active energy beams
        for (UfoEnergyBeamUp ufoEnergyBeamUp : activeEnergyBeamUps) {
            if (ufoEnergyBeamUp.alive) {
                renderEnergyBeam(ufoEnergyBeamUp, batch, energyBallCurrentFrame, energyBeamStaticCurrentFrame, energyBeamCurrentFrame);
            } else {
                activeEnergyBeamUps.removeValue(ufoEnergyBeamUp, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamUp, false);
            }
        }


    }

    private void renderEnergyBeam(Dodgeable energyBeam, SpriteBatch batch, TextureRegion energyBallCurrentFrame, TextureRegion energyBeamStaticCurrentFrame, TextureRegion energyBeamCurrentFrame) {

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

    public void update() {

        float currentTime = Gameplay.totalGameTime;


        //Update all ufos
        //If the ufo is spawned long enough... create an energy ball that will ultimately turn
        //into an energy beam when it is fully charged
        //Slowly increment the energy ball size until it is fully charged
        //After the energy ball width matches the beam width, it is fully charged and ready spawn an energy beam object
        for (UFO ufo : activeUFOs) {
            if (!ufo.energyBallIsSpawned) {
                if (currentTime - ufo.spawnTime > ENERGY_BALL_TIME_BEFORE_SPAWN) {
                    //Spawn an energy ball after a set amount of time (seconds) if it is not spawned
                    spawnEnergyBalls(ufo);
                }
            } else {
                // If energy balls are spawned, for each energy ball, grow the energy ball
                // until the energy ball width equals the beam width.  After the ball width equals
                // the beam width it is charged.
                // After the ball is charged, the animation for a charged energy ball turning into a beam
                // will begin.  The animation last 12 frames, so after all 12 frames are complete, the
                // energy ball has officially turned into an energy beam and an energy beam object can be spawned
                for (int i = 0, n = ufo.energyBalls.size; i < n; i++) {
                    EnergyBall energyBall = ufo.energyBalls.get(i);
                    Boolean energyBeamIsSpawned = energyBall.getEnergyBeamIsSpawned();
                    if (energyBall != null) {
                        if (energyBall.getWidth() >= ENERGY_BEAM_WIDTH) {
                            energyBall.setCharged(true);
                            energyBall.incrementFrameNumber();
                            if (energyBall.getFrameNumber() > 12f && !energyBeamIsSpawned) {
                                energyBall.setAnimationIsComplete(true);
                                energyBall.setEnergyBeamIsSpawned(true);
                                //Spawn the energy beam associated with the energy ball
                                spawnEnergyBeam(ufo, energyBall);
                            }
                        } else {
                            energyBall.increaseWidth(0.2f);
                            energyBall.increaseHeight(0.1f);
                        }
                    }
                }
            }


            //EVALUATE IF UFOS NEED TO BE HELD
            if (ufo.stopInCenterOfScreen && !ufo.isHeld && ufo.getPosition().x < camera.viewportWidth / 2 - ufo.WIDTH / 2) {
                //If ufo is set to stop in center of the screen and the x position is equal to center of screen,
                //hold the UFO
                ufo.holdPosition(ufo.timeToHold, ufo.FORCE_X, 0);
            }
            if (ufo.stopInRightCenterOfScreen && !ufo.isHeld && ufo.getPosition().x < camera.viewportWidth - ufo.WIDTH) {
                //If ufo is set to stop in center of the screen and the x position is equal to center of screen,
                //hold the UFO
                ufo.holdPosition(ufo.timeToHold, ufo.FORCE_X, 0);
            }
            if (ufo.stopInTopRightCornerOfScreen && !ufo.isHeld && ufo.getPosition().x < camera.viewportWidth - ufo.WIDTH) {
                //If ufo is set to stop in top right corner of the screen and the x position is one UFO's width from the corner,
                //hold the UFO
                ufo.holdPosition(ufo.timeToHold, ufo.FORCE_X, 0);
            }
            if (ufo.stopInBottomLeftCornerOfScreen && !ufo.isHeld && ufo.getPosition().x > 0) {
                //If ufo is set to stop in bottom left corner of the screen and the x position is one UFO's width from the corner,
                //hold the UFO
                ufo.holdPosition(ufo.timeToHold, -ufo.FORCE_X, 0);
            }

            if (ufo.isHeld) {

                ufo.checkIfCanBeUnheld();
            }

        }

        // Update the positions of the active energy balls/beams to match the active UFO positions
        // Ensure that the energy ball and energy beam animations move with the UFO
        for (UFO renderedUfos : activeUFOs) {
            if (renderedUfos.alive) {
                if (renderedUfos.energyBallIsSpawned) {

                    //Update positions for all energy balls associated with the UFO
                    for (EnergyBall energyBall : renderedUfos.energyBalls) {

                        float energyBallWidth = energyBall.getWidth();
                        float energyBallHeight = energyBall.getHeight();
                        float energyBallDirection = energyBall.getDirection();

                        //Get positions of UFOs and EnergyBeams relative to UFOs
                        float ufoXPosition = renderedUfos.getPosition().x;
                        float ufoYPosition = renderedUfos.getPosition().y + UFO_HEIGHT / 2;
                        energyBall.energyBallXPosition = 0;
                        energyBall.energyBallYPosition = ufoYPosition - ENERGY_BEAM_HEIGHT / 2;
                        energyBall.energyBeamXScale = 1;
                        energyBall.energyBallXPosition = 0;
                        energyBall.energyBallYPosition = ufoYPosition - energyBallHeight / 2;
                        energyBall.energyBeamRotation = 0;

                        //Set the energy ball and beam position, scale and rotation to match the UFO
                        //So that the rendered animations stay aligned as the UFO moves
                        if (energyBallDirection == ENERGY_BEAM_LEFT) {
                            energyBall.energyBallXPosition = ufoXPosition - energyBallWidth;
                            energyBall.energyBeamXPosition = ufoXPosition - ENERGY_BEAM_WIDTH;
                            energyBall.energyBeamYPosition = ufoYPosition - 20f;
                        } else if (energyBallDirection == ENERGY_BEAM_RIGHT) {
                            energyBall.energyBallXPosition = ufoXPosition + UFO_WIDTH + 1f - (0.88f) * energyBallWidth;
                            energyBall.energyBeamXPosition = ufoXPosition + UFO_WIDTH;
                            energyBall.energyBeamYPosition = ufoYPosition - 20f;
                            energyBall.energyBeamXScale = -1;
                        } else if (energyBallDirection == ENERGY_BEAM_DOWN) {
                            energyBall.energyBallXPosition = ufoXPosition + 8f - (0.94f) * energyBallWidth;
                            energyBall.energyBallYPosition = ufoYPosition - UFO_WIDTH / 4 - (0.6f) * energyBallHeight;
                            energyBall.energyBeamXPosition = ufoXPosition - 32f;
                            energyBall.energyBeamYPosition = ufoYPosition - 62f;
                            energyBall.energyBeamRotation = 90;
                        } else if (energyBallDirection == ENERGY_BEAM_UP) {
                            energyBall.energyBallXPosition = ufoXPosition + 8f - (0.94f) * energyBallWidth;
                            energyBall.energyBallYPosition = ufoYPosition - UFO_WIDTH / 4 - (0.4f) * energyBallHeight + UFO_HEIGHT - 6f;
                            energyBall.energyBeamXPosition = ufoXPosition - 33f;
                            energyBall.energyBeamYPosition = ufoYPosition - 63f + ENERGY_BEAM_VERTICAL_HEIGHT + UFO_HEIGHT / 2;
                            energyBall.energyBeamRotation = -90;
                        }

                    }
                }
            } else {
                activeUFOs.removeValue(renderedUfos, false);
                dodgeables.activeDodgeables.removeValue(renderedUfos, false);
            }
        }



        //REMOVE OFF SCREEN UFOs
        for (UFO ufo : activeUFOs) {
            if (ufo.getPosition().x < 0 - ufo.WIDTH || ufo.getPosition().x > camera.viewportWidth + 2 * ufo.WIDTH ||
                    ufo.getPosition().y < 0 - ufo.HEIGHT || ufo.getPosition().y > camera.viewportHeight + 2 *ufo.HEIGHT) {
                activeUFOs.removeValue(ufo, false);
                dodgeables.activeDodgeables.removeValue(ufo, false);
                ufoPool.free(ufo);
            }
        }


        //UPDATE ENERGY BEAMS ATTACHED TO UFOs
        //Ensure energy beam velocities match ufo velocities
        //Remove energy beams that are off screen
        for (UfoEnergyBeamLeft ufoEnergyBeamLeft : activeEnergyBeamLefts) {
            //Make the energy beam velocity match the UFO velocity
            ufoEnergyBeamLeft.dodgeableBody.setLinearVelocity(ufoEnergyBeamLeft.ufo.dodgeableBody.getLinearVelocity());
            Boolean removeBeamFromPool = false;
            BodyData ufoData = (BodyData) ufoEnergyBeamLeft.dodgeableBody.getUserData();
            if (ufoData != null) {
                UFO ufo = ufoData.getUfo();
                if (ufo == null || !ufo.alive) {
                    removeBeamFromPool = true;
                }
            } else {
                removeBeamFromPool = true;
            }

            if (removeBeamFromPool) {
                activeEnergyBeamLefts.removeValue(ufoEnergyBeamLeft, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamLeft, false);
                ufoEnergyBeamLeftPool.free(ufoEnergyBeamLeft);
            }
        }

        //UPDATE ENERGY BEAMS ATTACHED TO UFOs
        //Ensure energy beam velocities match ufo velocities
        //Remove energy beams that are off screen
        for (UfoEnergyBeamRight ufoEnergyBeamRight : activeEnergyBeamRights) {
            //Make the energy beam velocity match the UFO velocity
            ufoEnergyBeamRight.dodgeableBody.setLinearVelocity(ufoEnergyBeamRight.ufo.dodgeableBody.getLinearVelocity());
            Boolean removeBeamFromPool = false;
            BodyData ufoData = (BodyData) ufoEnergyBeamRight.dodgeableBody.getUserData();
            if (ufoData != null) {
                UFO ufo = ufoData.getUfo();
                if (ufo == null || !ufo.alive) {
                    removeBeamFromPool = true;
                }
            } else {
                removeBeamFromPool = true;
            }

            if (removeBeamFromPool) {
                activeEnergyBeamRights.removeValue(ufoEnergyBeamRight, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamRight, false);
                ufoEnergyBeamRightPool.free(ufoEnergyBeamRight);
            }
        }

        //UPDATE ENERGY BEAMS ATTACHED TO UFOs
        //Ensure energy beam velocities match ufo velocities
        //Remove energy beams that are off screen
        for (UfoEnergyBeamDown ufoEnergyBeamDown : activeEnergyBeamDowns) {
            //Make the energy beam velocity match the UFO velocity
            ufoEnergyBeamDown.dodgeableBody.setLinearVelocity(ufoEnergyBeamDown.ufo.dodgeableBody.getLinearVelocity());
            Boolean removeBeamFromPool = false;
            BodyData ufoData = (BodyData) ufoEnergyBeamDown.dodgeableBody.getUserData();
            if (ufoData != null) {
                UFO ufo = ufoData.getUfo();
                if (ufo == null || !ufo.alive) {
                    removeBeamFromPool = true;
                }
            } else {
                removeBeamFromPool = true;
            }

            if (removeBeamFromPool) {
                activeEnergyBeamDowns.removeValue(ufoEnergyBeamDown, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamDown, false);
                ufoEnergyBeamDownPool.free(ufoEnergyBeamDown);
            }
        }

        //UPDATE ENERGY BEAMS ATTACHED TO UFOs
        //Ensure energy beam velocities match ufo velocities
        //Remove energy beams that are off screen
        for (UfoEnergyBeamUp ufoEnergyBeamUp : activeEnergyBeamUps) {
            //Make the energy beam velocity match the UFO velocity
            ufoEnergyBeamUp.dodgeableBody.setLinearVelocity(ufoEnergyBeamUp.ufo.dodgeableBody.getLinearVelocity());
            Boolean removeBeamFromPool = false;
            BodyData ufoData = (BodyData) ufoEnergyBeamUp.dodgeableBody.getUserData();
            if (ufoData != null) {
                UFO ufo = ufoData.getUfo();
                if (ufo == null || !ufo.alive) {
                    removeBeamFromPool = true;
                }
            } else {
                removeBeamFromPool = true;
            }

            if (removeBeamFromPool) {
                activeEnergyBeamUps.removeValue(ufoEnergyBeamUp, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamUp,  false);
                ufoEnergyBeamUpPool.free(ufoEnergyBeamUp);
            }
        }

    }

    public void spawnUfo(float direction, float level) {

        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs

        UFO ufo = ufoPool.obtain();
        ufo.init(direction);
        activeUFOs.add(ufo);
        dodgeables.activeDodgeables.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastUfoSpawnTime);

    }

    public void spawnHorizontalUfo(float direction, float level) {

        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs
        // Horizontal Ufos start in the vertical middle of the right of the screen and move slowly
        // towards the left of the screen horizontally

        UFO ufo = ufoPool.obtain();
        ufo.initHorizontal(direction);
        activeUFOs.add(ufo);
        dodgeables.activeDodgeables.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastUfoSpawnTime);

    }

    public void spawnVerticalUfo(float direction, float level) {
        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs
        // Vertical Ufos start in the top of the screen in the horizontal middle and move slowly
        // towards the bottom of the screen vertically

        UFO ufo = ufoPool.obtain();
        ufo.initVertical(direction);
        activeUFOs.add(ufo);
        dodgeables.activeDodgeables.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastUfoSpawnTime);
    }

    public void spawnStopInCenterUfo(float direction, float timeToHoldInCenter, float level) {


        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs
        // StopInCenter Ufos stop in the center of the screen for a certain amount of time

        UFO ufo = ufoPool.obtain();
        ufo.initStopInCenter(direction, timeToHoldInCenter);
        activeUFOs.add(ufo);
        dodgeables.activeDodgeables.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastUfoSpawnTime);

    }

    public void spawnStopInRightCenterUfo(float direction, float timeToHoldInRightCenter, float level) {


        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs
        // StopInRightCenter Ufos stop in the center of the screen on the far right (near the edge)
        // for a certain amount of time

        UFO ufo = ufoPool.obtain();
        ufo.initStopInRightCenter(direction, timeToHoldInRightCenter);
        activeUFOs.add(ufo);
        dodgeables.activeDodgeables.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastUfoSpawnTime);

    }

    public void spawnTopRightCornerUfo(float direction, float timeToHold, float level){

        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs
        // StopInCorner Ufos stop in the corner of the screen for a certain amount of time

        UFO ufo = ufoPool.obtain();
        ufo.initStopInTopRightCorner(direction, timeToHold);
        activeUFOs.add(ufo);
        dodgeables.activeDodgeables.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastUfoSpawnTime);

    }

    public void spawnBottomLeftCornerUfo(float direction, float timeToHold, float level){

        // Spawn(obtain) a new UFO from the UFO pool and add to list of active UFOs
        // StopInCorner Ufos stop in the corner of the screen for a certain amount of time

        UFO ufo = ufoPool.obtain();
        ufo.initStopInBottomLeftCorner(direction, timeToHold);
        activeUFOs.add(ufo);
        dodgeables.activeDodgeables.add(ufo);


        //keep track of time the ufo was spawned
        lastUfoSpawnTime = Gameplay.totalGameTime;
        lastSpawnTimeByLevel.put(level, lastUfoSpawnTime);

    }

    public void spawnEnergyBalls(UFO ufo) {

        //Spawn a UFO energy ball
        //The energy ball is what spawns next to the UFO before the energy beam is launched
        //The energy ball slowly grows in size over time until it has enough energy to shoot the beam
        //The direction input is the side of the UFO that the energy ball/beam will shoot from

        //If the direction is RANDOM, spawn the ball in a random direction
        //If the direction is ALL, spawn four balls in all directions
        //If the direction is HORIZONTAL or VERTICAL,  spawn two balls to make a line in that direction
        //Otherwise, spawn the ball in chosen direction

        if (ufo.direction == ENERGY_BEAM_RANDOM) {
            float randomEnergyBeamDirection = MathUtils.random(0, 3);
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, randomEnergyBeamDirection));
            ufo.energyBallIsSpawned = true;
        } else if (ufo.direction == ENERGY_BEAM_ALL_DIRECTIONS) {
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_LEFT));
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_UP));
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_RIGHT));
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_DOWN));
            ufo.energyBallIsSpawned = true;
        } else if (ufo.direction == ENERGY_BEAM_HORIZONAL_DIRECTIONS) {
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_LEFT));
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_RIGHT));
            ufo.energyBallIsSpawned = true;
        } else if (ufo.direction == ENERGY_BEAM_VERTICAL_DIRECTIONS) {
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_UP));
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ENERGY_BEAM_DOWN));
            ufo.energyBallIsSpawned = true;
        } else {
            ufo.energyBalls.add(new EnergyBall(ENERGY_BALL_INITIAL_WIDTH, ENERGY_BALL_INITIAL_HEIGHT, ufo.direction));
            ufo.energyBallIsSpawned = true;
        }


    }

    private void spawnEnergyBeam(UFO ufo, EnergyBall energyBall) {

        // Spawn energy beam associated with the energy ball

        //get the direction of the beam from the direction of the energy ball attached to the UFO
        float energyBeamDirection = energyBall.getDirection();

        if (energyBeamDirection == ENERGY_BEAM_LEFT) {

            // Spawn(obtain) a new energy beam left from the energy beam left pool and add to list of active energy beam lefts

            UfoEnergyBeamLeft ufoEnergyBeamLeft = ufoEnergyBeamLeftPool.obtain();
            ufoEnergyBeamLeft.init(ufo, energyBeamDirection);
            activeEnergyBeamLefts.add(ufoEnergyBeamLeft);
            dodgeables.activeDodgeables.add(ufoEnergyBeamLeft);
            ufo.energyBeams.add(ufoEnergyBeamLeft);

            //Energy ball has now spawned, so reset it
            energyBall.reset();


        } else if (energyBeamDirection == ENERGY_BEAM_RIGHT) {

            // Spawn(obtain) a new energy beam Right from the energy beam Right pool and add to list of active energy beam Rights

            UfoEnergyBeamRight ufoEnergyBeamRight = ufoEnergyBeamRightPool.obtain();
            ufoEnergyBeamRight.init(ufo, energyBeamDirection);
            activeEnergyBeamRights.add(ufoEnergyBeamRight);
            dodgeables.activeDodgeables.add(ufoEnergyBeamRight);
            ufo.energyBeams.add(ufoEnergyBeamRight);

            //Energy ball has now spawned, so reset it
            energyBall.reset();

        } else if (energyBeamDirection == ENERGY_BEAM_DOWN) {

            // Spawn(obtain) a new energy beam Down from the energy beam Down pool and add to list of active energy beam Downs

            UfoEnergyBeamDown ufoEnergyBeamDown = ufoEnergyBeamDownPool.obtain();
            ufoEnergyBeamDown.init(ufo, energyBeamDirection);
            activeEnergyBeamDowns.add(ufoEnergyBeamDown);
            dodgeables.activeDodgeables.add(ufoEnergyBeamDown);
            ufo.energyBeams.add(ufoEnergyBeamDown);

            //Energy ball has now spawned, so reset it
            energyBall.reset();

        } else if (energyBeamDirection == ENERGY_BEAM_UP) {

            // Spawn(obtain) a new energy beam Up from the energy beam Up pool and add to list of active energy beam Ups

            UfoEnergyBeamUp ufoEnergyBeamUp = ufoEnergyBeamUpPool.obtain();
            ufoEnergyBeamUp.init(ufo, energyBeamDirection);
            activeEnergyBeamUps.add(ufoEnergyBeamUp);
            dodgeables.activeDodgeables.add(ufoEnergyBeamUp);
            ufo.energyBeams.add(ufoEnergyBeamUp);

            //Energy ball has now spawned, so reset it
            energyBall.reset();

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

    public float getLastUfoSpawnTime(float level) {

        if (lastSpawnTimeByLevel.get(level) == null){
            return 0;
        } else {
            return lastUfoSpawnTime;
        }

    }

    public void sweepDeadBodies() {

        // If any UFO objects are flagged for deletion, free them from the pool
        // so that they move off the screen and can be reused

        for (UFO ufo : activeUFOs) {
            if (!ufo.isActive()) {
                activeUFOs.removeValue(ufo, false);
                dodgeables.activeDodgeables.removeValue(ufo, false);
                ufoPool.free(ufo);
            }
        }

        for (UfoEnergyBeamLeft ufoEnergyBeamLeft : activeEnergyBeamLefts) {
            if (!ufoEnergyBeamLeft.isActive()) {
                activeEnergyBeamLefts.removeValue(ufoEnergyBeamLeft, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamLeft, false);
                ufoEnergyBeamLeftPool.free(ufoEnergyBeamLeft);
            }
        }

        for (UfoEnergyBeamRight ufoEnergyBeamRight : activeEnergyBeamRights) {
            if (!ufoEnergyBeamRight.isActive()) {
                activeEnergyBeamRights.removeValue(ufoEnergyBeamRight, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamRight, false);
                ufoEnergyBeamRightPool.free(ufoEnergyBeamRight);
            }
        }

        for (UfoEnergyBeamDown ufoEnergyBeamDown : activeEnergyBeamDowns) {
            if (!ufoEnergyBeamDown.isActive()) {
                activeEnergyBeamDowns.removeValue(ufoEnergyBeamDown, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamDown, false);
                ufoEnergyBeamDownPool.free(ufoEnergyBeamDown);
            }
        }

        for (UfoEnergyBeamUp ufoEnergyBeamUp : activeEnergyBeamUps) {
            if (!ufoEnergyBeamUp.isActive()) {
                activeEnergyBeamUps.removeValue(ufoEnergyBeamUp, false);
                dodgeables.activeDodgeables.removeValue(ufoEnergyBeamUp, false);
                ufoEnergyBeamUpPool.free(ufoEnergyBeamUp);
            }
        }

    }

    public void resetSpawnTimes(){
        lastUfoSpawnTime = 0;
    }

    public void dispose() {
        ufoSheet.dispose();
        energyBeamSheet.dispose();
        energyBallSheet.dispose();
        energyBeamStaticSheet.dispose();

    }


}
