package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class Rocket extends Dodgeable {

    public final float WIDTH = 10f;

    public Rocket(World gameWorld, AlphaPigeon game, OrthographicCamera camera, Dodgeables dodgeables) {
        super(gameWorld, game, camera, dodgeables);
        this.HEIGHT = 20f;

        //spawn a new rocket
        BodyDef rocketBodyDef = new BodyDef();
        rocketBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn rocket at random height and subtract the width of the rocket since the rocket is rotated 90 degrees
        float rocketSpawnHeight = MathUtils.random(WIDTH, camera.viewportHeight);
        rocketBodyDef.position.set(camera.viewportWidth, rocketSpawnHeight);
        dodgeableBody = gameWorld.createBody(rocketBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Rocket.json"));
        FixtureDef rocketFixtureDef = new FixtureDef();
        rocketFixtureDef.density = 0.001f;
        rocketFixtureDef.friction = 0.5f;
        rocketFixtureDef.restitution = 0.3f;
        // set the rocket filter categories and masks for collisions
        rocketFixtureDef.filter.categoryBits = game.CATEGORY_ROCKET;
        rocketFixtureDef.filter.maskBits = game.MASK_ROCKET;
        loader.attachFixture(dodgeableBody, "Rocket", rocketFixtureDef, 10);
        dodgeableBody.setTransform(dodgeableBody.getPosition(), -90 * MathUtils.degreesToRadians);

        //Determine which torque to apply to rocket depending on if it is spawned on bottom half or top half of screen
        //If spawned on bottom half, rotate rocket CW and move it upwards (done in the update method)
        //If spawned on top half, rotate rocket CCW and move it downwards (done in the update method)
        //Randomize the magnitude of the torque
        float rocketTorque;
        boolean rocketSpawnedInBottomHalfScreen = rocketSpawnHeight < camera.viewportHeight / 2;
        if (rocketSpawnedInBottomHalfScreen) {
            rocketTorque = MathUtils.random(-4f, -2f);
        } else {
            rocketTorque = MathUtils.random(-1f, 0f);
        }

        //Set torque and spawn data on the rocket body so it can be used in the update method
        BodyData rocketData = new BodyData(false);
        rocketData.setRocketData(rocketTorque, rocketSpawnedInBottomHalfScreen);
        dodgeableBody.setUserData(rocketData);
        dodgeableBody.applyTorque(rocketTorque, true);
        // apply the force to the rocket at the height it was spawned and at the end of the rocket
        // ROCKET_HEIGHT is used  for x coordinate of force instead of ROCKET_WIDTH because the rocket is rotated 90 degrees
        dodgeableBody.applyForce(-15.0f, 0, camera.viewportWidth + HEIGHT, rocketSpawnHeight - 5, true);

    }

    public void init() {

        //spawn rocket at random height and subtract the width of the rocket since the rocket is rotated 90 degrees
        //Determine which torque to apply to rocket depending on if it is spawned on bottom half or top half of screen
        //If spawned on bottom half, rotate rocket CW and move it upwards (done in the update method)
        //If spawned on top half, rotate rocket CCW and move it downwards (done in the update method)
        //Randomize the magnitude of the torque
        float rocketSpawnHeight = MathUtils.random(WIDTH, camera.viewportHeight);
        float rocketTorque;
        boolean rocketSpawnedInBottomHalfScreen = rocketSpawnHeight < camera.viewportHeight / 2;
        if (rocketSpawnedInBottomHalfScreen) {
            rocketTorque = MathUtils.random(-4f, -2f);
        } else {
            rocketTorque = MathUtils.random(-1f, 0f);
        }

        //Set torque and spawn data on the rocket body so it can be used in the update method
        BodyData rocketData = new BodyData(false);
        rocketData.setRocketData(rocketTorque, rocketSpawnedInBottomHalfScreen);

        dodgeableBody.setActive(true);

        //Ensure the rocket speed and angular velocity are set back to 0
        Vector2 vel = new Vector2(0f, 0f);
        dodgeableBody.setLinearVelocity(vel);
        dodgeableBody.setAngularVelocity(0);
        dodgeableBody.setUserData(rocketData);
        dodgeableBody.setTransform(camera.viewportWidth, rocketSpawnHeight, -90 * MathUtils.degreesToRadians);
        dodgeableBody.applyTorque(rocketTorque, true);
        // apply the force to the rocket at the height it was spawned and at the end of the rocket
        // ROCKET_HEIGHT is used  for x coordinate of force instead of ROCKET_WIDTH because the rocket is rotated 90 degrees
        dodgeableBody.applyForce(-15.0f, 0, camera.viewportWidth + HEIGHT, rocketSpawnHeight - 5, true);
        this.alive = true;

        applyScrollSpeed();



    }

}
