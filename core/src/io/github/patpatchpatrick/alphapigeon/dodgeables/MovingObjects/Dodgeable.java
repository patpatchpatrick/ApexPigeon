package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public abstract class Dodgeable implements Pool.Poolable {

    //Class for generic dodgeable items in the game
    //This class implements poolable because objects used in this class can and should be pooled when not in use

    public boolean alive;
    public Body dodgeableBody;

    protected float forceMultiplier = 1f;

    protected AlphaPigeon game;
    protected World gameWorld;
    protected OrthographicCamera camera;

    // HOLD
    // Hold the object for a set amount of time (seconds)
    // After allotted time, apply a force to the object if necessary.
    public boolean isHeld = false;
    protected long timeHoldWillBeReleased = 0;
    protected float forceXApplyAfterHold = 0f;
    protected float forceYApplyAfterHold = 0f;


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

    public void holdPosition(long numberOfSeconds, float forceXToApplyAfterHold, float forceYToApplyAfterHold){
        //Hold the dodgeables position for a certain number of seconds and afterwards
        // apply a given force to it
        this.forceXApplyAfterHold = forceXToApplyAfterHold;
        this.forceYApplyAfterHold = forceYToApplyAfterHold;

        //Set velocity to 0 when dodgeable is held
        Vector2 vel = new Vector2(0f, 0f);
        dodgeableBody.setLinearVelocity(vel);

        // Set times for when hold should be initiated and be released in milliseconds
        long currentTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
        this.timeHoldWillBeReleased = currentTime + numberOfSeconds * 1000;
        this.isHeld = true;
    }

    public void checkIfCanBeUnheld(){

        // If the currentTime is past the time that the hold will be released, release the hold
        // and apply the given force

        long currentTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
        if (this.timeHoldWillBeReleased <= currentTime){
            this.dodgeableBody.applyForceToCenter(this.forceXApplyAfterHold, this.forceYApplyAfterHold,true);
            this.isHeld = false;
        }

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

        //Reset hold variables
        this.isHeld = false;
        this.timeHoldWillBeReleased = 0;
        this.forceXApplyAfterHold = 0f;
        this.forceYApplyAfterHold = 0f;
        BodyData data = (BodyData) dodgeableBody.getUserData();
        if (data != null){
            data.setFlaggedForDelete(false);
        }

    }
}
