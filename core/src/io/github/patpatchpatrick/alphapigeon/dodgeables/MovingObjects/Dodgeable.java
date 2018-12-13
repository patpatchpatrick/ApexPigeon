package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;

public abstract class Dodgeable implements Pool.Poolable {

    //Class for generic dodgeable items in the game
    //This class implements poolable because objects used in this class can and should be pooled when not in use

    public boolean alive;
    public Body dodgeableBody;

    protected AlphaPigeon game;
    protected World gameWorld;
    protected OrthographicCamera camera;


    public Dodgeable(World gameWorld, AlphaPigeon game, OrthographicCamera camera){

        this.alive = false;
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

    }

    public Vector2 getPosition(){
        return dodgeableBody.getPosition();
    }

    public float getAngle(){
        return MathUtils.radiansToDegrees * dodgeableBody.getAngle();
    }

    public boolean isActive(){
        //Determine if the body is active or not
        //Bodies that should be removed from the screen are no longer active
        //These bodies are flagged for delete in the BodyData attached to the body
        BodyData data = (BodyData) dodgeableBody.getUserData();
        if (data != null) {
            if (data.isFlaggedForDelete()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void reset() {
        //Reset the body
        //Set all velocities to 0 and set the body off the screen
        //Set the body to inactive so there are no off-screen collisions
        //Set flagged for delete to false because the body has already been removed form screen

        dodgeableBody.setActive(false);
        dodgeableBody.setTransform(300, 300, 0);
        Vector2 vel = new Vector2(0f, 0f);
        dodgeableBody.setLinearVelocity(vel);
        dodgeableBody.setAngularVelocity(0);
        this.alive = false;
        BodyData data = (BodyData) dodgeableBody.getUserData();
        if (data != null){
            data.setFlaggedForDelete(false);
        }

    }
}
