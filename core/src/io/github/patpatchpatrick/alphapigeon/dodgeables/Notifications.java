package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

public class Notifications {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;
    private Dodgeables dodgeables;
    private ExclamationMark exclamationMark;

    //Exclamation mark notification variables
    //Exclamation marks are used to notify of dodgeables coming from the left, top or bottom of screen
    //Dodgeable enemies normally don't come from these directions so it will notify the user to be ready
    private Texture exclamationMarkSheet;
    private Animation<TextureRegion> exclamationMarkAnimation;
    //Direction where exclamation mark will be spawned
    public static final float DIRECTION_LEFT = 0f;
    public static final float DIRECTION_BOTTOM = 1f;
    public static final float DIRECTION_TOP = 2f;

    //Notifications that display on screen to notify the player of something

    public Notifications(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera, Dodgeables dodgeables) {
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;
        this.dodgeables = dodgeables;

        initializeExclamationMarkAnimation();

        exclamationMark = new ExclamationMark(this.camera);
    }

    public void render(float stateTime, SpriteBatch batch) {

        TextureRegion exclamationMarkCurrentFrame = exclamationMarkAnimation.getKeyFrame(stateTime, true);

        // Render all active exclamation mark notifications
        if (exclamationMark.exclamationMarkLeftSpawned) {
            batch.draw(exclamationMarkCurrentFrame, exclamationMark.LEFT_POSITION_X, exclamationMark.LEFT_POSITION_Y, 0, 0, exclamationMark.WIDTH, exclamationMark.HEIGHT, 1, 1, 0);
        }
        if (exclamationMark.exclamationMarkBottomSpawned) {
            batch.draw(exclamationMarkCurrentFrame, exclamationMark.BOTTOM_POSITION_X, exclamationMark.BOTTOM_POSITION_Y, 0, 0, exclamationMark.WIDTH, exclamationMark.HEIGHT, 1, 1, 0);
        }
        if (exclamationMark.exclamationMarkTopSpawned) {
            batch.draw(exclamationMarkCurrentFrame, exclamationMark.TOP_POSITION_X, exclamationMark.TOP_POSITION_Y, 0, 0, exclamationMark.WIDTH, exclamationMark.HEIGHT, 1, 1, 0);
        }


    }

    public void update() {

        //Update exclamation mark notifications
        exclamationMark.update();

    }

    public void spawnExclamationMarkNotification(float direction) {

        //Exclamation marks are used to notify of dodgeables coming from the left, top or bottom of screen
        //Dodgeable enemies normally don't come from these directions so it will notify the user to be ready

        exclamationMark.spawnExclamationMark(direction);


    }

    private void initializeExclamationMarkAnimation() {

        // Load the exclamation mark sprite sheet as a Texture
        exclamationMarkSheet = new Texture(Gdx.files.internal("sprites/ExclamationMarkSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(exclamationMarkSheet,
                exclamationMarkSheet.getWidth() / 2,
                exclamationMarkSheet.getHeight() / 1);

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
        exclamationMarkAnimation = new Animation<TextureRegion>(0.05f, frames);


    }

    public void dispose() {
        exclamationMarkSheet.dispose();
    }

    public static class ExclamationMark {
        //Class to define exclamation mark notifications

        public ExclamationMark(OrthographicCamera camera) {
            this.camera = camera;
            LEFT_POSITION_X = 0;
            LEFT_POSITION_Y = camera.viewportHeight / 2 - HEIGHT / 2;
            BOTTOM_POSITION_X = camera.viewportWidth / 2 - WIDTH / 2;
            BOTTOM_POSITION_Y = 1f;
            TOP_POSITION_X = camera.viewportWidth / 2 - WIDTH / 2;
            TOP_POSITION_Y = camera.viewportHeight - HEIGHT;
        }

        private OrthographicCamera camera;

        public static final float DURATION = 2500f;

        private static long lastNotificationSpawnTime;
        public static boolean notificationSpawned = false;

        public static boolean exclamationMarkLeftSpawned = false;
        private static long lastExclamationMarkLeftSpawnTime;

        public static boolean exclamationMarkTopSpawned = false;
        private static long lastExclamationMarkTopSpawnTime;

        public static boolean exclamationMarkBottomSpawned = false;
        private static long lastExclamationMarkBottomSpawnTime;

        //Render variables
        protected final float HEIGHT = 5f;
        protected final float WIDTH = HEIGHT;
        protected final float LEFT_POSITION_X;
        protected final float LEFT_POSITION_Y;
        protected final float BOTTOM_POSITION_X;
        protected final float BOTTOM_POSITION_Y;
        protected final float TOP_POSITION_X;
        protected final float TOP_POSITION_Y;


        public void update() {

            long currentTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
            // If the exclamation mark display duration has passed, stop showing the exclamation mark
            // (i.e. set the spawned boolean to false)
            Boolean anExclamationMarkIsSpawned = exclamationMarkLeftSpawned || exclamationMarkBottomSpawned || exclamationMarkTopSpawned;
            if (anExclamationMarkIsSpawned) {
                if (currentTime - lastExclamationMarkLeftSpawnTime > DURATION) {
                    exclamationMarkLeftSpawned = false;
                }
                if (currentTime - lastExclamationMarkBottomSpawnTime > DURATION) {
                    exclamationMarkBottomSpawned = false;
                }
                if (currentTime - lastExclamationMarkTopSpawnTime > DURATION) {
                    exclamationMarkTopSpawned = false;
                }

            }


        }

        public static void spawnExclamationMark(float direction) {

            if (direction == Notifications.DIRECTION_LEFT) {

                exclamationMarkLeftSpawned = true;
                //keep track of time the exclamation mark was spawned
                long currentTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
                lastExclamationMarkLeftSpawnTime = currentTime;
                lastNotificationSpawnTime = currentTime;
                notificationSpawned = true;
                Sounds.notificationSound.play();


            } else if (direction == Notifications.DIRECTION_BOTTOM) {

                exclamationMarkBottomSpawned = true;
                //keep track of time the exclamation mark was spawned
                long currentTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
                lastExclamationMarkBottomSpawnTime = currentTime;
                lastNotificationSpawnTime = currentTime;
                notificationSpawned = true;
                Sounds.notificationSound.play();


            } else if (direction == Notifications.DIRECTION_TOP) {

                exclamationMarkTopSpawned = true;
                //keep track of time the exclamation mark was spawned
                long currentTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
                lastExclamationMarkTopSpawnTime = currentTime;
                lastNotificationSpawnTime = currentTime;
                notificationSpawned = true;
                Sounds.notificationSound.play();

            }

        }

        public static boolean notificationIsComplete() {
            //If Exclamation Mark notification duration has passed, return true.
            long currentTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
            if (currentTime - lastNotificationSpawnTime > ExclamationMark.DURATION) {
                return true;
            }
            return false;
        }


    }

}
