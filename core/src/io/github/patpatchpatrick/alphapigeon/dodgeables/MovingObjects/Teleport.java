package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Teleports;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class Teleport extends Dodgeable {

    public final float WIDTH = 10f;
    public final float HEIGHT = 10f;
    private final float FORCE_ONE = -9.0f;
    private final float FORCE_TWO = 7.0f;
    public  float yPosition = 0f;

    public Teleport(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //create teleport body
        BodyDef teleportBodyDef = new BodyDef();
        teleportBodyDef.type = BodyDef.BodyType.DynamicBody;
        teleportBodyDef.position.set(0,0);
        dodgeableBody = gameWorld.createBody(teleportBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Teleport.json"));
        FixtureDef teleportFixtureDef = new FixtureDef();
        teleportFixtureDef.density = 0.001f;
        teleportFixtureDef.friction = 0.5f;
        teleportFixtureDef.restitution = 0.3f;
        // set the teleport filter categories and masks for collisions
        teleportFixtureDef.filter.categoryBits = game.CATEGORY_TELEPORT;
        teleportFixtureDef.filter.maskBits = game.MASK_TELEPORT;
        //The JSON loader loaders a fixture 1 pixel by 1 pixel... the animation is 100 px x 100 px, so need to scale by a factor of 10
        loader.attachFixture(dodgeableBody, "Teleport", teleportFixtureDef, HEIGHT);

    }

    public void initTeleportOne(float spawnHeight) {

        // Initialize teleport one
        // If a spawn height was specified, use that height, otherwise spawn at a random height

        if (spawnHeight == Teleports.VERT_POSITION_RANDOM){

            // Initialize first teleport (moves in different direction from second teleport)
            yPosition = MathUtils.random(0, camera.viewportHeight - HEIGHT);

            dodgeableBody.setActive(true);
            dodgeableBody.setTransform(camera.viewportWidth, yPosition, dodgeableBody.getAngle());
            dodgeableBody.applyForceToCenter(FORCE_ONE, 0, true);
            this.alive = true;

        } else {

            // Initialize first teleport (moves in different direction from second teleport)
            yPosition = spawnHeight;

            dodgeableBody.setActive(true);
            dodgeableBody.setTransform(camera.viewportWidth, yPosition, dodgeableBody.getAngle());
            dodgeableBody.applyForceToCenter(FORCE_ONE, 0, true);
            this.alive = true;


        }


    }

    public void initTeleportTwo(float spawnHeight) {

        // Initialize second teleport (moves in different direction from first teleport)
        // The spawn height of the 2nd teleport is equal to that of the first teleport

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(0, spawnHeight, dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(FORCE_TWO, 0, true);
        this.alive = true;

    }

    public void setOppositeTeleportData(BodyData teleportData){

        //Set the data for the opposite teleport

        dodgeableBody.setUserData(teleportData);

    }
}
