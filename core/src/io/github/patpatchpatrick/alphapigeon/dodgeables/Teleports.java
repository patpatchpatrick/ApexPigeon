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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelOneBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.LevelTwoBird;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Teleport;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Teleports {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //Teleport variables
    private final Array<Teleport> activeTeleports = new Array<Teleport>();
    private final Pool<Teleport> teleportsPool;
    private Animation<TextureRegion> teleportAnimation;
    private Texture teleportSheet;
    private long lastTeleportSpawnTime;

    public Teleports(final World gameWorld, final AlphaPigeon game, final OrthographicCamera camera){

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        initializeTeleportAnimation();

        teleportsPool = new Pool<Teleport>() {
            @Override
            protected Teleport newObject() {
                return new Teleport(gameWorld, game, camera);
            }
        };

    }

    public void render(float stateTime, SpriteBatch batch){
        TextureRegion teleportCurrentFrame = teleportAnimation.getKeyFrame(stateTime, true);

        // Render all active teleports
        for (Teleport teleport : activeTeleports) {
            if (teleport.alive) {
                batch.draw(teleportCurrentFrame, teleport.getPosition().x, teleport.getPosition().y,
                        0, 0, teleport.WIDTH, teleport.HEIGHT, 1, 1, teleport.getAngle());
            } else {
                activeTeleports.removeValue(teleport, false);
            }
        }

    }

    public void update(){

        // For all teleports that are off the game screen, free the teleports in the pool
        // so that they can be reused

        for (Teleport teleport : activeTeleports){
            if (teleport.getPosition().x < 0 -  2 * teleport.WIDTH || teleport.getPosition().x > camera.viewportWidth + 2 * teleport.WIDTH){
                activeTeleports.removeValue(teleport, false);
                teleportsPool.free(teleport);
            }
        }

    }

    public void spawnTeleports() {

        //spawn two new teleports that start at opposite ends of the screen (x direction) and
        //travel in opposite directions

        //spawn first teleport
        // Spawn(obtain) a new teleport from the teleports pool and add to list of active teleports

        Teleport teleportOne = teleportsPool.obtain();
        teleportOne.initTeleportOne();
        activeTeleports.add(teleportOne);

        //spawn second teleport
        // Spawn(obtain) a new teleport from the teleports pool and add to list of active teleports

        Teleport teleportTwo = teleportsPool.obtain();
        teleportTwo.initTeleportTwo();
        activeTeleports.add(teleportTwo);

        //Attach data of the opposite teleport to the teleport, so it can be used to transport the pigeon
        //to the opposite teleport's location
        BodyData teleportOneData = new BodyData(false);
        teleportOneData.setOppositeTeleport(teleportTwo);
        teleportOne.setOppositeTeleportData(teleportOneData);

        BodyData teleportTwoData = new BodyData(false);
        teleportTwoData.setOppositeTeleport(teleportOne);
        teleportTwo.setOppositeTeleportData(teleportTwoData);

        //keep track of time the teleport shield was spawned
        lastTeleportSpawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;

    }

    private void initializeTeleportAnimation() {

        // Load the teleport sprite sheet as a Texture
        teleportSheet = new Texture(Gdx.files.internal("sprites/TeleportSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(teleportSheet,
                teleportSheet.getWidth() / 10,
                teleportSheet.getHeight() / 9);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] teleportFrames = new TextureRegion[10 * 9];
        int index = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                teleportFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        teleportAnimation = new Animation<TextureRegion>(0.05f, teleportFrames);

    }

    public long getLastTeleportSpawnTime(){
        return lastTeleportSpawnTime;
    }

    public void sweepDeadBodies(){

        // If the teleport is flagged for deletion, free the object from the pool
        // so that it moves off the screen and can be reused

        for (Teleport teleport : activeTeleports){
            if (!teleport.isActive()){
                activeTeleports.removeValue(teleport, false);
                teleportsPool.free(teleport);
            }
        }

    }

    public void dispose(){
        teleportSheet.dispose();
    }


}
