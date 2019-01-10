package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Birds;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;

public class LevelOneBird extends Dodgeable {

    //Class for the bird dodgeable

    public final float WIDTH = 6f;
    public final float HEIGHT = 6f;
    public final float FORCE_X = -9.0f;

    public LevelOneBird(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn a new level one bird
        BodyDef levelOneBirdBodyDef = new BodyDef();
        levelOneBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelOneBirdBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT));
        dodgeableBody = gameWorld.createBody(levelOneBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/LevelOnePigeon.json"));
        FixtureDef levelOneBirdFixtureDef = new FixtureDef();
        levelOneBirdFixtureDef.density = 0.001f;
        levelOneBirdFixtureDef.friction = 0.5f;
        levelOneBirdFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        levelOneBirdFixtureDef.filter.categoryBits = game.CATEGORY_LEVEL_ONE_BIRD;
        levelOneBirdFixtureDef.filter.maskBits = game.MASK_LEVEL_ONE_BIRD;
        loader.attachFixture(dodgeableBody, "BackwardsPigeon", levelOneBirdFixtureDef, HEIGHT);


    }

    public void init(float totalGameTime, float verticalPosition) {

        //initialize a bird
        //can specify the vertical position, otherwise if position = VERT_POSITION_RANDOM use random position

        //Set the force multiplier for object
        //the force multiplier increases over time so bird goes faster over time
        setForceMultiplier(totalGameTime);

        if (verticalPosition == Birds.VERT_POSITION_RANDOM){
            //Initiate the object
            dodgeableBody.setActive(true);
            dodgeableBody.setTransform(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT), dodgeableBody.getAngle());
            dodgeableBody.applyForceToCenter(this.forceMultiplier * FORCE_X, 0, true);
            this.alive = true;
        } else {

            //Initiate the object
            dodgeableBody.setActive(true);
            dodgeableBody.setTransform(camera.viewportWidth, verticalPosition, dodgeableBody.getAngle());
            dodgeableBody.applyForceToCenter(this.forceMultiplier * FORCE_X, 0, true);
            this.alive = true;
        }



    }

    private void setForceMultiplier(float totalGameTime){

        //Set the force multiplier for the object
        //The force multiplier is the magnitude by which object's force/speed is increased
        //The force multiplier increases over time

        this.forceMultiplier = 1f + totalGameTime * 0.000015f;
        // The maximum force multiplier for this object is 10
        if (this.forceMultiplier >= 10f){
            this.forceMultiplier = 10f;
        }
    }

}
