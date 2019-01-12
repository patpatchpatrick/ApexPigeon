package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class AlienMissileCorner extends Dodgeable {

    public final float WIDTH = 10f;
    public final float HEIGHT = 10f;
    private final float FORCE = 5f;


    public AlienMissileCorner(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn first alien missile corner
        BodyDef alienCornerBodyDef = new BodyDef();
        alienCornerBodyDef.type = BodyDef.BodyType.DynamicBody;
        alienCornerBodyDef.position.set(-100,-100);
        dodgeableBody = gameWorld.createBody(alienCornerBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlienMissileCorner.json"));
        FixtureDef alienCornerFixtureDef = new FixtureDef();
        alienCornerFixtureDef.density = 0.001f;
        alienCornerFixtureDef.friction = 0.5f;
        alienCornerFixtureDef.restitution = 0.3f;
        // set the alien corner filter categories and masks for collisions
        alienCornerFixtureDef.filter.categoryBits = GameVariables.CATEGORY_ROCKET_EXPLOSION;
        alienCornerFixtureDef.filter.maskBits = GameVariables.MASK_ROCKET_EXPLOSION;
        loader.attachFixture(dodgeableBody, "AlienMissileCorner", alienCornerFixtureDef, HEIGHT);
        dodgeableBody.applyForceToCenter(0,0, true);

    }

    public void initFirstCorner(float explosionPositionX, float explosionPositionY, float theta) {

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(explosionPositionX + WIDTH/2 * MathUtils.cosDeg(theta), explosionPositionY + HEIGHT/2 * MathUtils.sinDeg(theta), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(FORCE* MathUtils.cosDeg(theta), FORCE* MathUtils.sinDeg(theta), true);
        this.alive = true;

        //Set the time the corner was spawned on the corner body.  This is used in the update method
        //to destroy the missile explosion body after a set amount of time
        BodyData alienCornerData = new BodyData(false);
        alienCornerData.setSpawnTime(TimeUtils.nanoTime()/GameVariables.MILLION_SCALE);
        dodgeableBody.setUserData(alienCornerData);

    }

    public void initSecondCorner(float explosionPositionX, float explosionPositionY, float theta) {

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(explosionPositionX + WIDTH/2* MathUtils.sinDeg(theta), explosionPositionY - HEIGHT/2* MathUtils.cosDeg(theta), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(FORCE* MathUtils.sinDeg(theta), -FORCE* MathUtils.cosDeg(theta), true);
        this.alive = true;

        //Set the time the corner was spawned on the corner body.  This is used in the update method
        //to destroy the missile explosion body after a set amount of time
        BodyData alienCornerData = new BodyData(false);
        alienCornerData.setSpawnTime(TimeUtils.nanoTime()/GameVariables.MILLION_SCALE);
        dodgeableBody.setUserData(alienCornerData);


    }

    public void initThirdCorner(float explosionPositionX, float explosionPositionY, float theta) {

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(explosionPositionX - WIDTH/2* MathUtils.cosDeg(theta), explosionPositionY - HEIGHT/2* MathUtils.sinDeg(theta), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(-FORCE* MathUtils.cosDeg(theta), -FORCE* MathUtils.sinDeg(theta), true);
        this.alive = true;

        //Set the time the corner was spawned on the corner body.  This is used in the update method
        //to destroy the missile explosion body after a set amount of time
        BodyData alienCornerData = new BodyData(false);
        alienCornerData.setSpawnTime(TimeUtils.nanoTime()/GameVariables.MILLION_SCALE);
        dodgeableBody.setUserData(alienCornerData);


    }

    public void initFourthCorner(float explosionPositionX, float explosionPositionY, float theta) {

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(explosionPositionX - WIDTH/2* MathUtils.sinDeg(theta), explosionPositionY + HEIGHT/2* MathUtils.cosDeg(theta), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(-FORCE* MathUtils.sinDeg(theta), FORCE* MathUtils.cosDeg(theta), true);
        this.alive = true;

        //Set the time the corner was spawned on the corner body.  This is used in the update method
        //to destroy the missile explosion body after a set amount of time
        BodyData alienCornerData = new BodyData(false);
        alienCornerData.setSpawnTime(TimeUtils.nanoTime()/GameVariables.MILLION_SCALE);
        dodgeableBody.setUserData(alienCornerData);

    }


}
