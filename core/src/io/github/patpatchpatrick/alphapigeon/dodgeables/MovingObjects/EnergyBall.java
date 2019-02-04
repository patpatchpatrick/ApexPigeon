package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.audio.Sound;

import io.github.patpatchpatrick.alphapigeon.resources.SettingsManager;

public class EnergyBall implements MovingObject {

    //Energy ball that is created by UFOs before the laser energy beams projectiles are launched out of UFO

    private float width = 0f;
    private float height = 0f;
    private boolean isCharged = false;
    private float frameNumber = 0;
    private boolean animationIsComplete = false;
    private float direction;
    private boolean energyBeamIsSpawned = false;
    private Sound energyBallSound;

    //Variables to track the energy ball positions and positions of the beam associated with the energy ball
    //These are all updated in the UFOs class update method
    public float energyBeamXPosition = 0;
    public float energyBeamYPosition = 0;
    public float energyBeamXScale = 1;
    public float energyBallXPosition = 0;
    public float energyBallYPosition = 0;
    public float energyBeamRotation = 0;

    public EnergyBall() {
    }

    public EnergyBall(float width, float height, float direction, Sound energyBallSound) {
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.energyBallSound = energyBallSound;
        this.energyBallSound.loop(SettingsManager.gameVolume);
    }

    public void setEnergyBeamIsSpawned(Boolean energyBeamSpawned){
        // Boolean to keep track of whether energy beam associated with energy ball is spawned
        this.energyBeamIsSpawned = energyBeamSpawned;
    }

    public boolean getEnergyBeamIsSpawned(){
        return this.energyBeamIsSpawned;
    }


    @Override
    public void increaseWidth(float amount) {
        this.width += amount;
    }

    @Override
    public void increaseHeight(float amount) {
        this.height += amount;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    public void setCharged(Boolean isCharged) {
        this.isCharged = isCharged;
    }

    public boolean isCharged() {
        return this.isCharged;

    }

    public void incrementFrameNumber(){
        this.frameNumber++;
    }

    public float getFrameNumber(){
        return frameNumber;
    }

    public void setAnimationIsComplete(boolean animationIsComplete){
        this.animationIsComplete = animationIsComplete;
    }

    public boolean animationIsComplete(){
        return this.animationIsComplete;
    }

    public void setDirection(float direction){
        this.direction =  direction;
    }

    public float getDirection(){
        return this.direction;
    }

    public void reset(){
        this.energyBallSound.stop();
    }

}
