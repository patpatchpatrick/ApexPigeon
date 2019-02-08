package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class LevelTwoBird extends Dodgeable {

    public final static float WIDTH = 12f;
    public final static float HEIGHT = 12f;
    public final float FORCE_X = -18.0f;

    public LevelTwoBird(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn a new level two bird
        BodyDef levelTwoBirdBodyDef = new BodyDef();
        levelTwoBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelTwoBirdBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT));
        dodgeableBody = gameWorld.createBody(levelTwoBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/LevelTwoBird.json"));
        FixtureDef levelTwoBirdFixtureDef = new FixtureDef();
        levelTwoBirdFixtureDef.density = 0.001f;
        levelTwoBirdFixtureDef.friction = 0.5f;
        levelTwoBirdFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        levelTwoBirdFixtureDef.filter.categoryBits = GameVariables.CATEGORY_LEVEL_TWO_BIRD;
        levelTwoBirdFixtureDef.filter.maskBits = GameVariables.MASK_LEVEL_TWO_BIRD;
        loader.attachFixture(dodgeableBody, "LevelTwoBird", levelTwoBirdFixtureDef, WIDTH);
    }

    public void init(float totalGameTime) {

        //Set the force multiplier for object
        setForceMultiplier(totalGameTime);

        //Initiate the object
        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(this.forceMultiplier * FORCE_X, 0, true);
        this.alive = true;

    }

    private void setForceMultiplier(float totalGameTime){

        //Set the force multiplier for the object
        //The force multiplier is the magnitude by which object's force is increased
        //The force multiplier increases over time and is proportional to the pigeon's speed
        //The force multiplier was designed so force is 1f when pigeon's initial speed is 9 (m/s) and
        //the force multiplier is 4f when pigeon's max speed of 300 (m/s) is reached.

        this.forceMultiplier = (1f / 97f) * GameVariables.pigeonSpeed + (88f / 97f);
        Gdx.app.log("L2ForceMulti", "" + this.forceMultiplier);

    }
}
