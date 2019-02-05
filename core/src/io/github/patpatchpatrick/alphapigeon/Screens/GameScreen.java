package io.github.patpatchpatrick.alphapigeon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.Pigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.dodgeables.PowerUps;
import io.github.patpatchpatrick.alphapigeon.levels.Gameplay;
import io.github.patpatchpatrick.alphapigeon.resources.AccelerometerController;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.HighScore;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;
import io.github.patpatchpatrick.alphapigeon.resources.ScrollingBackground;
import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

public class GameScreen implements Screen {
    AlphaPigeon game;

    //GAME STATE
    public enum State {
        PAUSE,
        RUN,
        RESUME,
        STOPPED
    }

    public State state = State.RUN;
    private boolean gameIsOver = false;
    private PlayServices playServices;
    private DatabaseAndPreferenceManager databaseAndPreferenceManager;
    private InputProcessor inputProcessor;

    private OrthographicCamera camera;
    private Viewport viewport;
    private AccelerometerController accelerometerController;
    private Pigeon pigeon;
    public Dodgeables dodgeables;
    public ScrollingBackground scrollingBackground;
    public HighScore highScore;
    private float stateTime;
    private float deltaTime;
    private Body pigeonBody;
    private Gameplay gameplay;
    Box2DDebugRenderer debugRenderer;
    World world;

    //Variables
    final float PIGEON_WIDTH = 10.0f;
    final float PIGEON_HEIGHT = 5.0f;
    private final float PIGEON_KEY_INPUT_FORCE = 50.0f;
    private final float PIGEON_TOUCH_INPUT_FORCE = 10.0f;

    //Textures
    private Texture pauseButton;
    private Texture resumeButton;
    private final float PAUSE_RESUME_BUTTON_WIDTH = 2.7f;
    private final float PAUSE_RESUME_BUTTON_HEIGHT = 2.9f;
    private final float PAUSE_RESUME_BUTTON_X1 = 0.8f;
    private final float PAUSE_RESUME_BUTTON_Y1 = 44.4f;

    public GameScreen(AlphaPigeon game, PlayServices playServices, DatabaseAndPreferenceManager databaseAndPreferenceManager) {
        this.game = game;
        this.playServices = playServices;
        this.databaseAndPreferenceManager = databaseAndPreferenceManager;

        world = new World(new Vector2(0, 0), true);
        //debugRenderer = new Box2DDebugRenderer();

        // set initial time to 0
        stateTime = 0f;

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT);
        //the viewport object will handle camera's attributes
        //the aspect provided (worldWidth/worldHeight) will be kept


        //Set viewport to stretch or fit viewport depending on whether user has enabled full screen mode setting
        if (SettingsManager.fullScreenModeIsOn) {
            viewport = new StretchViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);
        } else {
            viewport = new FitViewport(GameVariables.WORLD_WIDTH, GameVariables.WORLD_HEIGHT, camera);
        }


        // initialize game resources
        this.scrollingBackground = new ScrollingBackground();
        this.highScore = new HighScore();
        this.pigeon = new Pigeon(world, game, this);
        this.dodgeables = new Dodgeables(this.pigeon, world, game, camera);
        pigeonBody = this.pigeon.getBody();

        // Create the accelerometerController class and input processor to read user input
        accelerometerController = new AccelerometerController(this.pigeon, this.camera);
        createInputProcessor();

        // initialize the gameplay class
        gameplay = new Gameplay(this.dodgeables);

        // create contact listener to listen for if the Pigeon collides with another object
        // if the pigeon collides with another object, the game is over
        createContactListener();

        //Get most recent updated user settings from mobile device db/prefs
        SettingsManager.updateSettings();

        if (playServices != null) {
            //Hide ads on game screen
            playServices.showBannerAds(false);
        }

        //Initialize pause and play button textures
        pauseButton = new Texture(Gdx.files.internal("textures/icons/PauseButton.png"));
        resumeButton = new Texture(Gdx.files.internal("textures/icons/ResumeButton.png"));

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update the state time
        // if game is paused, there is no change in time
        deltaTime = state == State.PAUSE ? 0 : Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        // tell the camera to update its matrices
        camera.update();

        //Update method called before rendering
        if (state != State.PAUSE) {
            update();
        }

        //debugRenderer.render(world, camera.combined);
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch and draw the game world and objects within it
        game.batch.begin();
        scrollingBackground.render(game.batch);
        highScore.render(game.batch);
        gameplay.render(stateTime, game.batch);
        pigeon.render(stateTime, game.batch);
        if (state == State.PAUSE) {
            game.batch.draw(resumeButton, PAUSE_RESUME_BUTTON_X1, PAUSE_RESUME_BUTTON_Y1, PAUSE_RESUME_BUTTON_WIDTH, PAUSE_RESUME_BUTTON_HEIGHT);
        } else {
            game.batch.draw(pauseButton, PAUSE_RESUME_BUTTON_X1, PAUSE_RESUME_BUTTON_Y1, PAUSE_RESUME_BUTTON_WIDTH, PAUSE_RESUME_BUTTON_HEIGHT);
        }
        game.batch.end();

        if (gameIsOver) {
            gameOver();
        }


    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
        this.scrollingBackground.resize(width, height);


    }

    @Override
    public void pause() {

        state = State.PAUSE;

        //TODO currently the energy beam/ball continues to grow after game is paused and sounds continue to play

    }

    @Override
    public void resume() {

        state = State.RUN;

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        // dispose of all the native resources... CALL THIS METHOD MANUALLY WHEN YOU EXIT A SCREEN
        resumeButton.dispose();
        pauseButton.dispose();
        pigeon.dispose();
        dodgeables.dispose();
        highScore.dispose();
        scrollingBackground.dispose();
        world.dispose();

    }

    public void update() {

        // step the world
        world.step(1 / 60f, 6, 2);


        sweepDeadBodies();

        // update all the game resources
        scrollingBackground.update(deltaTime);
        highScore.update(deltaTime);
        gameplay.update(stateTime);
        pigeon.update(stateTime);

        // process user input
        Gdx.input.setInputProcessor(inputProcessor);
        accelerometerController.processAccelerometerInput();


        // make sure the pigeon stays within the screen bounds
        if (pigeonBody.getPosition().x < 0) {
            Vector2 vel = pigeonBody.getLinearVelocity();
            vel.x = 0f;
            pigeonBody.setLinearVelocity(vel);
            pigeonBody.setTransform(new Vector2(0, pigeonBody.getPosition().y), pigeonBody.getAngle());
        }
        if (pigeonBody.getPosition().x > camera.viewportWidth - PIGEON_WIDTH) {
            Vector2 vel = pigeonBody.getLinearVelocity();
            vel.x = 0f;
            pigeonBody.setLinearVelocity(vel);
            pigeonBody.setTransform(new Vector2(camera.viewportWidth - PIGEON_WIDTH, pigeonBody.getPosition().y), pigeonBody.getAngle());
        }
        if (pigeonBody.getPosition().y < 0) {
            Vector2 vel = pigeonBody.getLinearVelocity();
            vel.y = 0f;
            pigeonBody.setLinearVelocity(vel);
            pigeonBody.setTransform(new Vector2(pigeonBody.getPosition().x, 0), pigeonBody.getAngle());
        }
        if (pigeonBody.getPosition().y > camera.viewportHeight - PIGEON_HEIGHT) {
            Vector2 vel = pigeonBody.getLinearVelocity();
            vel.y = 0f;
            pigeonBody.setLinearVelocity(vel);
            pigeonBody.setTransform(new Vector2(pigeonBody.getPosition().x, camera.viewportHeight - PIGEON_HEIGHT), pigeonBody.getAngle());
        }

    }

    private void createContactListener() {

        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {

                //Get fixures and bodies
                final Fixture fixtureA = contact.getFixtureA();
                final Fixture fixtureB = contact.getFixtureB();
                final Body fixtureABody = fixtureA.getBody();
                Body fixtureBBody = fixtureB.getBody();

                //Get the category of fixtures involved in collision
                short fixtureACategory = fixtureA.getFilterData().categoryBits;
                short fixtureBCategory = fixtureB.getFilterData().categoryBits;

                //Boolean collision checks used to determine action to take
                Boolean pigeonInvolvedInCollision = fixtureACategory == GameVariables.CATEGORY_PIGEON || fixtureBCategory == GameVariables.CATEGORY_PIGEON;
                Boolean birdInvolvedInCollision = fixtureACategory == GameVariables.CATEGORY_LEVEL_ONE_BIRD || fixtureACategory == GameVariables.CATEGORY_LEVEL_TWO_BIRD
                        || fixtureBCategory == GameVariables.CATEGORY_LEVEL_ONE_BIRD || fixtureBCategory == GameVariables.CATEGORY_LEVEL_TWO_BIRD;
                Boolean meteorInvolvedInCollision = fixtureACategory == GameVariables.CATEGORY_METEOR || fixtureBCategory == GameVariables.CATEGORY_METEOR;
                Boolean powerUpShieldInvolvedInCollision = fixtureACategory == GameVariables.CATEGORY_POWERUP || fixtureBCategory == GameVariables.CATEGORY_POWERUP;
                Boolean powerUpInvolvedInCollision = powerUpShieldInvolvedInCollision;
                Boolean teleportInvolvedInCollision = fixtureACategory == GameVariables.CATEGORY_TELEPORT || fixtureBCategory == GameVariables.CATEGORY_TELEPORT;
                Boolean rocketInvolvedInCollision = fixtureACategory == GameVariables.CATEGORY_ROCKET || fixtureBCategory == GameVariables.CATEGORY_ROCKET;

                short powerUpType = GameVariables.CATEGORY_PIGEON;

                //Collision logic for pigeon:
                //First, check if a power up is involved
                //If so, get the power-up type and power-up the pigeon
                //If pigeon contacts teleport,  teleport it
                //If pigeon already has a power-up applied, apply appropriate action depending on the power-up applied
                //If pigeon has normal contact with an enemy, the game is over
                if (pigeonInvolvedInCollision) {
                    if (powerUpInvolvedInCollision) {
                        Fixture powerUpFixture;
                        Fixture collidedEnemyFixture;
                        if (fixtureA.getFilterData().categoryBits == GameVariables.CATEGORY_POWERUP) {
                            powerUpFixture = fixtureA;
                            collidedEnemyFixture = fixtureB;

                        } else {
                            powerUpFixture = fixtureB;
                            collidedEnemyFixture = fixtureA;
                        }
                        pigeon.powerUp(powerUpFixture);
                    } else if (teleportInvolvedInCollision) {
                        Fixture teleportFixture;
                        if (fixtureA.getFilterData().categoryBits == GameVariables.CATEGORY_TELEPORT) {
                            teleportFixture = fixtureA;
                        } else {
                            teleportFixture = fixtureB;
                        }
                        pigeon.teleport(teleportFixture);
                    } else if (pigeon.getPowerUpType() == PowerUps.POWER_UP_TYPE_SHIELD) {
                        //If bird has a shield power up, destroy the dodgeable it collides with
                        destroyNonPigeonBody(fixtureA, fixtureB);
                    } else {
                        // If the pigeon is involved in the collision and does not have a shield applied, the game is over
                        gameIsOver = true;
                    }
                }

                //Collision logic for rocket
                //If a rocket is involved in a collision, and the pigeon is not involved, then the
                //rocket collided with an enemy so it should explode
                //Spawn a rocket explosion in the position of the fixture that is not the rocket, since
                //the enemy will explode at collision. Then, destroy the rocket and the enemy it collided with
                //The spawned rocket and destroyed bodies must be run on a separate thread so the world isn't locked
                if (rocketInvolvedInCollision) {
                    if (!pigeonInvolvedInCollision) {
                        final Body fixtureAExplosionBody = fixtureABody;
                        final Body fixtureBExplosionBody = fixtureBBody;
                        if (fixtureA.getFilterData().categoryBits == GameVariables.CATEGORY_ROCKET) {
                            Gdx.app.postRunnable(new Runnable() {

                                @Override
                                public void run() {
                                    dodgeables.spawnRocketExplosion(fixtureBExplosionBody.getWorldCenter().x, fixtureBExplosionBody.getWorldCenter().y);
                                    destroyBody(fixtureA);
                                    destroyBody(fixtureB);
                                }
                            });

                        } else {

                            Gdx.app.postRunnable(new Runnable() {

                                @Override
                                public void run() {
                                    dodgeables.spawnRocketExplosion(fixtureAExplosionBody.getWorldCenter().x, fixtureAExplosionBody.getWorldCenter().y);
                                    destroyBody(fixtureA);
                                    destroyBody(fixtureB);
                                }
                            });
                        }
                    }
                }

                if (birdInvolvedInCollision) {
                    //if bird is involved in any collision, play a chirp sound
                    Gdx.app.postRunnable(new Runnable() {

                        @Override
                        public void run() {
                            Sounds.birdSound.play(SettingsManager.gameVolume);
                        }
                    });
                }


            }

            @Override
            public void endContact(Contact contact) {
                //Gdx.app.log("endContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });

    }

    private void destroyNonPigeonBody(Fixture fixtureA, Fixture fixtureB) {

        // Pigeon is charged and hits a dodgeable enemy
        // destroy the body that the pigeon touches

        if (fixtureA.getFilterData().categoryBits == GameVariables.CATEGORY_PIGEON) {
            fixtureB.getBody().setUserData(new BodyData(true));
        } else if (fixtureB.getFilterData().categoryBits == GameVariables.CATEGORY_PIGEON) {
            fixtureA.getBody().setUserData(new BodyData(true));
        }

        // zap the enemy that pigeon touches while charged... play zap sound effect
        pigeon.zapEnemy();

    }


    private void destroyBody(Fixture fixture) {
        fixture.getBody().setUserData(new BodyData(true));
    }

    private void gameOver() {

        // bird has crashed, game is over
        // play game over sound
        // stop counting the high currentScore
        // reset all dodgeables to stop sounds
        // destroy all world bodies (in postRunnable so the world is not locked)
        // dispose of game disposables
        // set screen to game over screen

        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run() {
                Sounds.gameOverSound.play(SettingsManager.gameVolume);
                highScore.stopCounting();
                for (Dodgeable dodgeable : dodgeables.activeDodgeables) {
                    dodgeable.reset();
                }
                Array<Body> bodies = new Array<Body>();
                world.getBodies(bodies);
                for (int i = 0; i < bodies.size; i++) {
                    world.destroyBody(bodies.get(i));
                }
                dispose();
                game.setScreen(new GameOverScreen(game, playServices, databaseAndPreferenceManager, highScore));
            }
        });


    }

    private void createInputProcessor() {

        inputProcessor = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {

                //If the user has touch controls turned on, apply a force on the bird depending on which button is pushed
                if (SettingsManager.touchSettingIsOn) {
                    switch (keycode) {
                        case Input.Keys.LEFT:
                            pigeonBody.applyForceToCenter(-PIGEON_KEY_INPUT_FORCE * SettingsManager.touchSensitivity, 0, true);
                            break;
                        case Input.Keys.RIGHT:
                            pigeonBody.applyForceToCenter(PIGEON_KEY_INPUT_FORCE * SettingsManager.touchSensitivity, 0, true);
                            break;
                        case Input.Keys.DOWN:
                            pigeonBody.applyForceToCenter(0, -PIGEON_KEY_INPUT_FORCE * SettingsManager.touchSensitivity, true);
                            break;
                        case Input.Keys.UP:
                            pigeonBody.applyForceToCenter(0, PIGEON_KEY_INPUT_FORCE * SettingsManager.touchSensitivity, true);
                            break;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                //Get the mouse coordinates and unproject to the world coordinates
                Vector3 mousePos = new Vector3(screenX, screenY, 0);
                camera.unproject(mousePos, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

                if (mousePos.x > PAUSE_RESUME_BUTTON_X1 && mousePos.x < PAUSE_RESUME_BUTTON_X1 + PAUSE_RESUME_BUTTON_WIDTH &&
                        mousePos.y > PAUSE_RESUME_BUTTON_Y1 && mousePos.y < PAUSE_RESUME_BUTTON_Y1 + PAUSE_RESUME_BUTTON_HEIGHT) {
                    if (button == Input.Buttons.LEFT) {
                        //Toggle Pause/Resume game if the Pause/Resume button is pushed
                        if (state == State.RUN) {
                            state = State.PAUSE;
                        } else {
                            state = State.RUN;
                        }
                        return true;
                    }
                } else if (SettingsManager.touchSettingIsOn && button == Input.Buttons.LEFT) {
                    //If the user has touch controls turned on, apply a force on the bird proportional to
                    //the distance from the bird that the user touched
                    pigeonBody.applyForceToCenter(PIGEON_TOUCH_INPUT_FORCE * SettingsManager.touchSensitivity * (mousePos.x - pigeonBody.getPosition().x),
                            PIGEON_TOUCH_INPUT_FORCE * SettingsManager.touchSensitivity * (mousePos.y - pigeonBody.getPosition().y), true);
                    return true;
                }


                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        };

    }

    public void sweepDeadBodies() {

        this.dodgeables.sweepDeadBodies();

    }

}
