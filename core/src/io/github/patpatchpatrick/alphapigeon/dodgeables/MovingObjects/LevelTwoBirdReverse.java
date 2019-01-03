package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class LevelTwoBirdReverse extends Dodgeable {

    //Level two bird that flies in the reverse direction

    public final float WIDTH = 12f;
    public final float HEIGHT = 12f;
    public final float FORCE_X = 18.0f;


    public LevelTwoBirdReverse(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn a new level two bird
        BodyDef levelTwoBirdBodyDef = new BodyDef();
        levelTwoBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelTwoBirdBodyDef.position.set(0 - WIDTH, MathUtils.random(0, camera.viewportHeight - HEIGHT));
        dodgeableBody = gameWorld.createBody(levelTwoBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/LevelTwoBirdReverse.json"));
        FixtureDef levelTwoBirdFixtureDef = new FixtureDef();
        levelTwoBirdFixtureDef.density = 0.001f;
        levelTwoBirdFixtureDef.friction = 0.5f;
        levelTwoBirdFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        levelTwoBirdFixtureDef.filter.categoryBits = game.CATEGORY_LEVEL_TWO_BIRD;
        levelTwoBirdFixtureDef.filter.maskBits = game.MASK_LEVEL_TWO_BIRD;
        loader.attachFixture(dodgeableBody, "LevelTwoBird", levelTwoBirdFixtureDef, WIDTH);
    }

    public void init(float totalGameTime) {

        //Set the force multiplier for object
        setForceMultiplier(totalGameTime);

        //Initiate the object
        dodgeableBody.setActive(true);
        dodgeableBody.setFixedRotation(true);
        dodgeableBody.setTransform(0 - WIDTH, MathUtils.random(0, camera.viewportHeight - HEIGHT), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(this.forceMultiplier * FORCE_X, 0, true);
        this.alive = true;

    }

    private void setForceMultiplier(float totalGameTime){

        //Set the force multiplier for the object
        //The force multiplier is the magnitude by which object's force/speed is increased
        //The force multiplier increases over time

        this.forceMultiplier = 1f + totalGameTime * 0.000005f;
        // The maximum force multiplier for this object is 4
        if (this.forceMultiplier >= 4f){
            this.forceMultiplier = 4f;
        }
    }
}
