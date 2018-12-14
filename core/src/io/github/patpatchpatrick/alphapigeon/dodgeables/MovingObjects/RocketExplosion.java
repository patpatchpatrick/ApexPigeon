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

public class RocketExplosion extends Dodgeable {

    public final float WIDTH = 30f;
    public final float HEIGHT = 30f;

    public RocketExplosion(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn a new rocket explosion
        BodyDef rocketExplosionBodyDef = new BodyDef();
        rocketExplosionBodyDef.type = BodyDef.BodyType.DynamicBody;

        rocketExplosionBodyDef.position.set(-100,-100);
        dodgeableBody = gameWorld.createBody(rocketExplosionBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/RocketExplosion.json"));
        FixtureDef rocketExplosionFixtureDef = new FixtureDef();
        rocketExplosionFixtureDef.density = 0.001f;
        rocketExplosionFixtureDef.friction = 0.5f;
        rocketExplosionFixtureDef.restitution = 0.3f;
        // set the rocket explosion filter categories and masks for collisions
        rocketExplosionFixtureDef.filter.categoryBits = game.CATEGORY_ROCKET_EXPLOSION;
        rocketExplosionFixtureDef.filter.maskBits = game.MASK_ROCKET_EXPLOSION;
        loader.attachFixture(dodgeableBody, "RocketExplosion", rocketExplosionFixtureDef, WIDTH);
        dodgeableBody.applyForceToCenter(0, 0, true);

    }

    public void init(float explosionPositionX, float explosionPositionY) {

        //spawn rocket explosion at the input position (this will be the position of the enemy that was hit
        // with the rocket.

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(explosionPositionX - WIDTH/2, explosionPositionY - HEIGHT/2, dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(0, 0, true);
        this.alive = true;

        //Set the time the rocket was exploded on the rocket.  This is used in the update method
        //to destroy the rocket explosion after a set amount of time
        BodyData rocketExplosionData = new BodyData(false);
        rocketExplosionData.setExplosionData(TimeUtils.nanoTime() / GameVariables.MILLION_SCALE);
        dodgeableBody.setUserData(rocketExplosionData);

    }




}
