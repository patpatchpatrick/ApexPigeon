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
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class Teleports {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //Teleport variables
    private Array<Body> teleportArray = new Array<Body>();
    private Animation<TextureRegion> teleportAnimation;
    private Texture teleportSheet;
    private long lastTeleportSpawnTime;
    private final float TELEPORT_WIDTH = 10f;
    private final float TELEPORT_HEIGHT = 10f;

    public Teleports(World gameWorld, AlphaPigeon game, OrthographicCamera camera){

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        initializeTeleportAnimation();

    }

    public void render(float stateTime, SpriteBatch batch){
        TextureRegion teleportCurrentFrame = teleportAnimation.getKeyFrame(stateTime, true);

        // draw all teleport dodgeables using the current animation frame
        for (Body teleport : teleportArray) {

            // draw the teleport if it is active (hasn't been grabbed by the pigeon), otherwise remove it from the array
            if (teleport.isActive()) {
                batch.draw(teleportCurrentFrame, teleport.getPosition().x, teleport.getPosition().y,
                        0, 0, TELEPORT_WIDTH, TELEPORT_HEIGHT, 1, 1, MathUtils.radiansToDegrees * teleport.getAngle());
            } else {
                teleportArray.removeValue(teleport, false);
            }

        }
    }

    public void update(){

    }

    public void spawnTeleports() {

        //spawn two new teleports that start at opposite ends of the screen (x direction) and
        //travel in opposite directions

        //spawn first teleport
        BodyDef teleportBodyDef = new BodyDef();
        teleportBodyDef.type = BodyDef.BodyType.DynamicBody;
        //spawn teleport at random height
        teleportBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - TELEPORT_HEIGHT));
        Body teleportBody = gameWorld.createBody(teleportBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Teleport.json"));
        FixtureDef teleportFixtureDef = new FixtureDef();
        teleportFixtureDef.density = 0.001f;
        teleportFixtureDef.friction = 0.5f;
        teleportFixtureDef.restitution = 0.3f;
        // set the teleport filter categories and masks for collisions
        teleportFixtureDef.filter.categoryBits = game.CATEGORY_TELEPORT;
        teleportFixtureDef.filter.maskBits = game.MASK_TELEPORT;
        //The JSON loader loaders a fixture 1 pixel by 1 pixel... the animation is 100 px x 100 px, so need to scale by a factor of 10
        loader.attachFixture(teleportBody, "Teleport", teleportFixtureDef, TELEPORT_HEIGHT);
        teleportBody.applyForceToCenter(-9.0f, 0, true);

        //spawn second teleport which starts at the opposite side of screen as the first and travels in the opposite direction
        BodyDef teleportTwoBodyDef = new BodyDef();
        teleportTwoBodyDef.type = BodyDef.BodyType.DynamicBody;
        teleportTwoBodyDef.position.set(0, MathUtils.random(0, camera.viewportHeight - TELEPORT_HEIGHT));
        Body teleportTwoBody = gameWorld.createBody(teleportTwoBodyDef);
        loader.attachFixture(teleportTwoBody, "Teleport", teleportFixtureDef, TELEPORT_HEIGHT);
        teleportTwoBody.applyForceToCenter(7.0f, 0, true);

        //Attach data of the opposite teleport to the teleport, so it can be used to transport the pigeon
        //to the opposite teleport's location
        BodyData teleportOneData = new BodyData(false);
        teleportOneData.setOppositeTeleport(teleportTwoBody);
        BodyData teleportTwoData = new BodyData(false);
        teleportTwoData.setOppositeTeleport(teleportBody);
        teleportBody.setUserData(teleportOneData);
        teleportTwoBody.setUserData(teleportTwoData);


        //add teleport to teleports array
        teleportArray.add(teleportBody);
        teleportArray.add(teleportTwoBody);

        //keep track of time the teleport shield was spawned
        lastTeleportSpawnTime = TimeUtils.nanoTime();

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

    public void dispose(){
        teleportSheet.dispose();
    }


}
