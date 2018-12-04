package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

public interface MovingObject {

    //Interface to represent moving objects in the game

    void increaseWidth(float amount);
    void increaseHeight(float amount);
    float getWidth();
    float getHeight();



}
