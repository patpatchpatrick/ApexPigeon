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

public class AlienMissile extends Dodgeable {

    public final float WIDTH = 10f;
    public final float HEIGHT = 10f;
    private final float FORCE_X = -40.0f;

    public AlienMissile(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn a new alien missile
        BodyDef alienMissileBodyDef = new BodyDef();
        alienMissileBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn alien missile at random height
        alienMissileBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT / 2));
        dodgeableBody = gameWorld.createBody(alienMissileBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlienMissile.json"));
        FixtureDef alienMissileFixtureDef = new FixtureDef();
        alienMissileFixtureDef.density = 0.001f;
        alienMissileFixtureDef.friction = 0.5f;
        alienMissileFixtureDef.restitution = 0.3f;
        // set the alien missile filter categories and masks for collisions
        alienMissileFixtureDef.filter.categoryBits = game.CATEGORY_ALIEN_MISSILE;
        alienMissileFixtureDef.filter.maskBits = game.MASK_ALIEN_MISSILE;
        loader.attachFixture(dodgeableBody, "Alien Missile", alienMissileFixtureDef, HEIGHT);

    }

    public void init() {

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT / 2), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(FORCE_X, 0, true);
        this.alive = true;

        BodyData missileData = new BodyData(false);
        missileData.setSpawnTime(TimeUtils.nanoTime() / GameVariables.MILLION_SCALE);
        dodgeableBody.setUserData(missileData);

    }

}
