package io.github.patpatchpatrick.alphapigeon.resources;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.EnergyBall;

public class BodyData {

    // Class used to store data on bodies

    private Boolean flaggedForDelete;

    private long spawnTime= 9999;

    //TELEPORT DATA
    private Body oppositeTeleport;

    //ROCKET DATA
    private float rocketYForce = 0f;
    private long lastRocketExplosionTime = 99999;

    //UFO DATA
    private boolean ufoEnergyBallSpawned = false;
    private EnergyBall energyBall;


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

    public void setFlaggedForDelete(boolean flaggedForDelete){
        this.flaggedForDelete = flaggedForDelete;
    }

    public void setSpawnTime(long spawnTime){
        this.spawnTime = spawnTime;
    }

    public long getSpawnTime(){
        return this.spawnTime;
    }

    //TELEPORT DATA

    public void setOppositeTeleport(Body oppositeTeleport){
        // Method used for teleport bodies, to store info about its counterpart teleport
        this.oppositeTeleport = oppositeTeleport;
    }

    public Body getOppositeTeleport(){
        // Method used for teleport bodies, to return info about its counterpart teleport
        return oppositeTeleport;
    }

    //ROCKET DATA

    public void setRocketData(float torque, boolean rocketSpawnedInBottomHalfOfScreen){
        // Based on the rockets torque and where the rocket is spawned, calculate what force should
        // be applied to rocket in the Y direction
        if (rocketSpawnedInBottomHalfOfScreen){
            rocketYForce = -0.2f * torque - 0.2f;
        } else {
            rocketYForce = -0.3f * torque - 0.6f;
        }
    }

    public void setExplosionData(long lastRocketExplosionTime){
        this.lastRocketExplosionTime = lastRocketExplosionTime;
    }

    public long getExplosionTime(){
        return this.lastRocketExplosionTime;
    }

    public float getRocketYForce(){
        return rocketYForce;
    }

    //UFO DATA

    public void setUfoEnergyBallIsSpawned(boolean ballSpawned){
        this.ufoEnergyBallSpawned = ballSpawned;
    }

    public boolean ufoEnergyBallIsSpawned(){
        return this.ufoEnergyBallSpawned;
    }

    public void  setEnergyBall(EnergyBall energyBall){
        this.energyBall = energyBall;
    }

    public EnergyBall getEnergyBall(){
        return this.energyBall;
    }




}
