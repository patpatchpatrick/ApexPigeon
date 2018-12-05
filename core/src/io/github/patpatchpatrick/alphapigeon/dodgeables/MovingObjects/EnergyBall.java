package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

public class EnergyBall implements MovingObject {

    //Energy ball that is created by UFOs before the laser energy beams projectiles are launched out of UFO

    private float width = 0f;
    private float height = 0f;
    private boolean isCharged = false;
    private float frameNumber = 0;
    private boolean animationIsComplete = false;
    private float direction;

    public EnergyBall() {
    }

    public EnergyBall(float width, float height, float direction) {
        this.width = width;
        this.height = height;
        this.direction = direction;
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

}
