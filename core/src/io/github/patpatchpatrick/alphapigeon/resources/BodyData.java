package io.github.patpatchpatrick.alphapigeon.resources;
import com.badlogic.gdx.physics.box2d.Body;

public class BodyData {

    // Class used to store data on bodies

    private Boolean flaggedForDelete;

    //Teleport Data
    private Body oppositeTeleport;

    //Rocket Data
    private float rocketYForce = 0f;


    public BodyData(boolean flagForDelete) {
        flaggedForDelete = flagForDelete;
    }

    public Boolean isFlaggedForDelete() {
        if (flaggedForDelete != null) {
            return flaggedForDelete;
        } else {
            return false;
        }
    }

    public void setOppositeTeleport(Body oppositeTeleport){
        // Method used for teleport bodies, to store info about its counterpart teleport
        this.oppositeTeleport = oppositeTeleport;
    }

    public Body getOppositeTeleport(){
        // Method used for teleport bodies, to return info about its counterpart teleport
        return oppositeTeleport;
    }

    public void setRocketData(float torque, boolean rocketSpawnedInBottomHalfOfScreen){
        // Based on the rockets torque and where the rocket is spawned, calculate what force should
        // be applied to rocket in the Y direction
        if (rocketSpawnedInBottomHalfOfScreen){
            rocketYForce = -0.2f * torque - 0.2f;
        } else {
            rocketYForce = -0.3f * torque - 0.6f;
        }
    }

    public float getRocketYForce(){
        return rocketYForce;
    }


}
