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

public class AlienMissileExplosion extends Dodgeable {

    public final float WIDTH = 20f;
    public final float HEIGHT = 20f;

    public AlienMissileExplosion(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn a new alien missile explosion
        BodyDef alienExplosionBodyDef = new BodyDef();
        alienExplosionBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn alien explosion at the input position (this will be the position of the center of the alien missile.
        alienExplosionBodyDef.position.set(0,0);
        dodgeableBody = gameWorld.createBody(alienExplosionBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlienMissileExplosion.json"));
        FixtureDef alienExplosionFixtureDef = new FixtureDef();
        alienExplosionFixtureDef.density = 0.001f;
        alienExplosionFixtureDef.friction = 0.5f;
        alienExplosionFixtureDef.restitution = 0.3f;
        // set the alien explosion filter categories and masks for collisions
        alienExplosionFixtureDef.filter.categoryBits = GameVariables.CATEGORY_ROCKET_EXPLOSION;
        alienExplosionFixtureDef.filter.maskBits = GameVariables.MASK_ROCKET_EXPLOSION;
        loader.attachFixture(dodgeableBody, "Alien Missile Explosion", alienExplosionFixtureDef, HEIGHT);
        dodgeableBody.applyForceToCenter(0, 0, true);

    }

    public void init(float explosionPositionX, float explosionPositionY) {

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(explosionPositionX - WIDTH/2, explosionPositionY - HEIGHT/2, dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(0, 0, true);
        this.alive = true;

        //Set the time the missile was exploded on the missile explosion  body.  This is used in the update method
        //to destroy the missile explosion body after a set amount of time
        BodyData alienMissileExplosionData = new BodyData(false);
        alienMissileExplosionData.setSpawnTime(TimeUtils.nanoTime()/GameVariables.MILLION_SCALE);
        dodgeableBody.setUserData(alienMissileExplosionData);

    }

}
