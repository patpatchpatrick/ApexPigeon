package io.github.patpatchpatrick.alphapigeon.resources;
import com.badlogic.gdx.physics.box2d.Body;

public class BodyData {

    // Class used to store data on bodies

    private Boolean flaggedForDelete;
    private Body oppositeTeleport;

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
}
