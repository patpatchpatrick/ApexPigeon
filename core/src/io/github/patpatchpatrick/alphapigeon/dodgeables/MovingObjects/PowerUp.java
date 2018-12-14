package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class PowerUp extends Dodgeable {

    public final float WIDTH = 8f;
    public final float HEIGHT = 4.8f;
    private final float FORCE_X = -9.0f;


    public PowerUp(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn a new PowerUp Shield
        BodyDef powerUpShieldBodyDef = new BodyDef();
        powerUpShieldBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn PowerUp shield at random height
        powerUpShieldBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT));
        dodgeableBody = gameWorld.createBody(powerUpShieldBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/PowerUpShield.json"));
        FixtureDef powerUpShieldFixtureDef = new FixtureDef();
        powerUpShieldFixtureDef.density = 0.001f;
        powerUpShieldFixtureDef.friction = 0.5f;
        powerUpShieldFixtureDef.restitution = 0.3f;
        // set the powerup shield filter categories and masks for collisions
        powerUpShieldFixtureDef.filter.categoryBits = game.CATEGORY_POWERUP_SHIELD;
        powerUpShieldFixtureDef.filter.maskBits = game.MASK_POWERUP;
        //The JSON loader loaders a fixture 1 pixel by 1 pixel... the animation is 80 px x 48 px, so need to scale by a factor of 8 since the width is the limiting factor
        loader.attachFixture(dodgeableBody, "PowerUpShield", powerUpShieldFixtureDef, WIDTH);
        dodgeableBody.applyForceToCenter(FORCE_X, 0, true);

    }

    public void init() {

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(FORCE_X, 0, true);
        this.alive = true;

    }
}
