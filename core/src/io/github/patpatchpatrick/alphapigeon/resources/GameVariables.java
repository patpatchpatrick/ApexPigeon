package io.github.patpatchpatrick.alphapigeon.resources;

public class GameVariables {

    //Camera variables
    public static final float WORLD_WIDTH = 80;
    public static final float WORLD_HEIGHT = 48;

    //Pigeon Variables
        // The pigeon's speed in m/s
        // This speed is slowly increased as the game progresses
        // Changes to the pigeon's speed are made in the "Gameplay" class
        // The pigeon's speed also affects the speed of bird dodgeables (they fly faster relative to pigeon as pigeon's speed increases)
        // The pigeon's speed also affects the speed of the scrolling background (it scrolls faster as the pigeon speeds up)
        // The pigeon's speed also affects the high score (it increases faster as time goes on)
        // The pigeon's speed reaches the max speed after 10 minutes (600 seconds)
    public static float pigeonSpeed = 9;
    public static final float PIGEON_MAX_SPEED = 300;

    //Collisions
        // Filter Categories
    public final static short CATEGORY_PIGEON = 0x0001;
    public final static short CATEGORY_LEVEL_ONE_BIRD = 0x0002;
    public final static short CATEGORY_METEOR = 0x0004;
    public final static short CATEGORY_LEVEL_TWO_BIRD = 0x0008;
    public final static short CATEGORY_POWERUP = 0x0016;
    public final static short CATEGORY_TELEPORT = 0x0032;
    public final static short CATEGORY_ROCKET = 0x0064;
    public final static short CATEGORY_ROCKET_EXPLOSION = 0x0128;
    public final static short CATEGORY_ALIEN_MISSILE = 0x0256;
    public final static short CATEGORY_UFO = 0x0512;

        // Filter Masks
    public final  static short MASK_PIGEON = CATEGORY_LEVEL_ONE_BIRD | CATEGORY_METEOR | CATEGORY_LEVEL_TWO_BIRD
            | CATEGORY_POWERUP | CATEGORY_TELEPORT | CATEGORY_ROCKET | CATEGORY_ROCKET_EXPLOSION
            | CATEGORY_ALIEN_MISSILE | CATEGORY_UFO;
    public final static short MASK_LEVEL_ONE_BIRD = CATEGORY_PIGEON | CATEGORY_METEOR | CATEGORY_ROCKET ;
    public final static short MASK_METEOR = CATEGORY_PIGEON | CATEGORY_LEVEL_ONE_BIRD | CATEGORY_LEVEL_TWO_BIRD;
    public final static short MASK_LEVEL_TWO_BIRD = CATEGORY_PIGEON | CATEGORY_METEOR | CATEGORY_ROCKET;
    public final static short MASK_POWERUP = CATEGORY_PIGEON;
    public final static short MASK_TELEPORT = CATEGORY_PIGEON;
    public final static short MASK_ROCKET = CATEGORY_PIGEON | CATEGORY_LEVEL_ONE_BIRD | CATEGORY_LEVEL_TWO_BIRD;
    public final static short MASK_ROCKET_EXPLOSION = CATEGORY_PIGEON;
    public final static short MASK_ALIEN_MISSILE = CATEGORY_PIGEON;
    public final static short MASK_UFO = CATEGORY_PIGEON;


}
