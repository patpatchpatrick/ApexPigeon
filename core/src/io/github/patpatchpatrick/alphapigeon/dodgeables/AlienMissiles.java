package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class AlienMissiles {

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //Alien Missile variables
    private Array<Body> alienMissileArray = new Array<Body>();
    private Animation<TextureRegion> alienMissileAnimation;
    private Texture alienMissileSheet;
    private long lastAlienMissileSpawnTime;
    private final float ALIEN_MISSILE_WIDTH = 10f;
    private final float ALIEN_MISSILE_HEIGHT = 10f;

    //Alien Missile Explosion variables
    private Array<Body> alienMissileExplosionArray = new Array<Body>();
    private Animation<TextureRegion> alienMissileExplosionAnimation;
    private Texture alienMissileExplosionSheet;
    private long lastAlienMissileExplosionSpawnTime;
    private final float ALIEN_MISSILE_EXPLOSION_WIDTH = 20f;
    private final float ALIEN_MISSILE_EXPLOSION_HEIGHT = 20f;

    //Alien Missile Corner variables
    private Array<Body> alienMissileCornerArray = new Array<Body>();
    private Animation<TextureRegion> alienMissileCornerAnimation;
    private Texture alienMissileCornerSheet;
    private long lastAlienMissileCornerSpawnTime;
    private final float ALIEN_MISSILE_CORNER_WIDTH = 10f;
    private final float ALIEN_MISSILE_CORNER_HEIGHT = 10f;
    private final float ALIEN_MISSILE_CORNER_FORCE = 5f;
    private final float ALIEN_MISSILE_CORNER_EXPLOSION_FUSE_TIME = 1000;

    //Alien Missile Corner Explosion variables
    private Array<Body> alienMissileCornerExplosionArray = new Array<Body>();
    private Animation<TextureRegion> alienMissileCornerExplosionAnimation;
    private Texture alienMissileCornerExplosionSheet;
    private long lastAlienMissileCornerExplosionSpawnTime;
    private final float ALIEN_MISSILE_CORNER_EXPLOSION_WIDTH = 10f;
    private final float ALIEN_MISSILE_CORNER_EXPLOSION_HEIGHT = 10f;

    public AlienMissiles(World gameWorld, AlphaPigeon game, OrthographicCamera camera){
        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        initializeAlienMissileAnimation();
        initializeAlienMissileExplosionAnimation();
        initializeAlienMissileCornerAnimation();

    }


    public void render(float stateTime, SpriteBatch batch){

        TextureRegion alienMissileCurrentFrame = alienMissileAnimation.getKeyFrame(stateTime, true);
        TextureRegion alienMissileExplosionCurrentFrame = alienMissileExplosionAnimation.getKeyFrame(stateTime, true);
        TextureRegion alienCornerCurrentFrame = alienMissileCornerAnimation.getKeyFrame(stateTime, true);

        // draw all alien missile dodgeables using the current animation frame
        for (Body alienMissile : alienMissileArray) {
            if (alienMissile.isActive()) {
                batch.draw(alienMissileCurrentFrame, alienMissile.getPosition().x, alienMissile.getPosition().y, ALIEN_MISSILE_WIDTH / 2, ALIEN_MISSILE_HEIGHT / 2, ALIEN_MISSILE_WIDTH, ALIEN_MISSILE_HEIGHT, 1, 1, MathUtils.radiansToDegrees * alienMissile.getAngle());
            } else {
                alienMissileArray.removeValue(alienMissile, false);
            }
        }

        // draw all alien missile explosion dodgeables using the current animation frame
        for (Body alienMissileExplosion : alienMissileExplosionArray) {
            if (alienMissileExplosion.isActive()) {
                batch.draw(alienMissileExplosionCurrentFrame, alienMissileExplosion.getPosition().x, alienMissileExplosion.getPosition().y, 0, 0, ALIEN_MISSILE_EXPLOSION_WIDTH, ALIEN_MISSILE_EXPLOSION_HEIGHT, 1, 1, 0);
            } else {
                alienMissileExplosionArray.removeValue(alienMissileExplosion, false);
            }
        }

        // draw all alien corner dodgeables using the current animation frame
        for (Body alienCorner : alienMissileCornerArray) {
            if (alienCorner.isActive()) {
                batch.draw(alienCornerCurrentFrame, alienCorner.getPosition().x, alienCorner.getPosition().y, 0, 0, ALIEN_MISSILE_CORNER_WIDTH, ALIEN_MISSILE_CORNER_HEIGHT, 1, 1, 0);
            } else {
                alienMissileCornerArray.removeValue(alienCorner, false);
            }
        }

        // draw all alien missile corner explosion dodgeables using the current animation frame
        for (Body alienMissileCornerExplosion : alienMissileCornerExplosionArray) {
            if (alienMissileCornerExplosion.isActive()) {
                batch.draw(alienMissileExplosionCurrentFrame, alienMissileCornerExplosion.getPosition().x, alienMissileCornerExplosion.getPosition().y, 0, 0, ALIEN_MISSILE_CORNER_EXPLOSION_WIDTH, ALIEN_MISSILE_CORNER_EXPLOSION_HEIGHT, 1, 1, 0);
            } else {
                alienMissileCornerExplosionArray.removeValue(alienMissileCornerExplosion, false);
            }
        }

    }

    public void update(){

        // Alien Missile
        // If missiles are spawned , explode them after a set amount of time.
        // Exploding the missiles shoots the 4 missile corners in opposing directions away from the center of missile
        for (Body alienMissile : alienMissileArray) {
            if (alienMissile.isActive()) {
                BodyData missileData = (BodyData) alienMissile.getUserData();
                if (missileData != null) {
                    long missileSpawnTime = missileData.getSpawnTime();
                    if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - missileSpawnTime / GameVariables.MILLION_SCALE > 2000) {
                        missileData.setFlaggedForDelete(true);
                        spawnAlienMissileExplosion(alienMissile.getPosition().x, alienMissile.getPosition().y);
                        spawnAlienMissileCorners(alienMissile.getPosition().x, alienMissile.getPosition().y);
                    }
                } else {
                    if (missileData != null) {
                        missileData.setFlaggedForDelete(true);
                    }
                }

            } else {
                alienMissileArray.removeValue(alienMissile, false);
            }
        }

        // Alien Missile Explosions
        // If missiles explosions are spawned , destroy them after a set amount of time.
        for (Body alienMissileExplosion : alienMissileExplosionArray) {
            if (alienMissileExplosion.isActive()) {
                BodyData missileExplosionData = (BodyData) alienMissileExplosion.getUserData();
                if (missileExplosionData != null) {
                    long missileExplosionSpawnTime = missileExplosionData.getExplosionTime();
                    if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - missileExplosionSpawnTime / GameVariables.MILLION_SCALE > 500) {
                        missileExplosionData.setFlaggedForDelete(true);
                    }
                } else {
                    if (missileExplosionData != null) {
                        missileExplosionData.setFlaggedForDelete(true);
                    }
                }

            } else {
                alienMissileExplosionArray.removeValue(alienMissileExplosion, false);
            }
        }

        // Alien Corner Missile
        // If missiles are spawned , explode them after a set amount of time.
        // Exploding the missiles shoots the 4 missile corners in opposing directions away from the center of missile
        for (Body alienCornerMissile : alienMissileCornerArray) {
            if (alienCornerMissile.isActive()) {
                BodyData missileData = (BodyData) alienCornerMissile.getUserData();
                if (missileData != null) {
                    long missileSpawnTime = missileData.getSpawnTime();
                    if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - missileSpawnTime / GameVariables.MILLION_SCALE > ALIEN_MISSILE_CORNER_EXPLOSION_FUSE_TIME) {
                        missileData.setFlaggedForDelete(true);
                        spawnAlienMissileCornerExplosions(alienCornerMissile.getPosition().x, alienCornerMissile.getPosition().y);
                    }
                } else {
                    if (missileData != null) {
                        missileData.setFlaggedForDelete(true);
                    }
                }

            } else {
                alienMissileCornerArray.removeValue(alienCornerMissile, false);
            }
        }

        // Alien Corner Missile Explosions
        // If missiles explosions are spawned , destroy them after a set amount of time.
        for (Body alienCornerMissileExplosion : alienMissileCornerExplosionArray) {
            if (alienCornerMissileExplosion.isActive()) {
                BodyData missileExplosionData = (BodyData) alienCornerMissileExplosion.getUserData();
                if (missileExplosionData != null) {
                    long missileExplosionSpawnTime = missileExplosionData.getExplosionTime();
                    if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - missileExplosionSpawnTime / GameVariables.MILLION_SCALE > 500) {
                        missileExplosionData.setFlaggedForDelete(true);
                    }
                } else {
                    if (missileExplosionData != null) {
                        missileExplosionData.setFlaggedForDelete(true);
                    }
                }

            } else {
                alienMissileExplosionArray.removeValue(alienCornerMissileExplosion, false);
            }
        }

    }

    public void spawnAlienMissile() {

        //spawn a new alien missile
        BodyDef alienMissileBodyDef = new BodyDef();
        alienMissileBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn alien missile at random height
        alienMissileBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - ALIEN_MISSILE_HEIGHT / 2));
        Body alienMissileBody = gameWorld.createBody(alienMissileBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlienMissile.json"));
        FixtureDef alienMissileFixtureDef = new FixtureDef();
        alienMissileFixtureDef.density = 0.001f;
        alienMissileFixtureDef.friction = 0.5f;
        alienMissileFixtureDef.restitution = 0.3f;
        // set the alien missile filter categories and masks for collisions
        alienMissileFixtureDef.filter.categoryBits = game.CATEGORY_ALIEN_MISSILE;
        alienMissileFixtureDef.filter.maskBits = game.MASK_ALIEN_MISSILE;
        loader.attachFixture(alienMissileBody, "Alien Missile", alienMissileFixtureDef, ALIEN_MISSILE_HEIGHT);
        alienMissileBody.applyForceToCenter(-40.0f, 0, true);

        //add alien missile to alien missiles array
        alienMissileArray.add(alienMissileBody);

        BodyData missileData = new BodyData(false);
        missileData.setSpawnTime(TimeUtils.nanoTime());
        alienMissileBody.setUserData(missileData);

        //keep track of time the bird was spawned
        lastAlienMissileSpawnTime = TimeUtils.nanoTime();

    }

    public void spawnAlienMissileExplosion(float explosionPositionX, float explosionPositionY) {

        //spawn a new alien missile explosion
        BodyDef alienExplosionBodyDef = new BodyDef();
        alienExplosionBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn alien explosion at the input position (this will be the position of the center of the alien missile.
        alienExplosionBodyDef.position.set(explosionPositionX - ALIEN_MISSILE_WIDTH/1.5f, explosionPositionY - ALIEN_MISSILE_HEIGHT/2);
        Body alienMissileExplosionBody = gameWorld.createBody(alienExplosionBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlienMissileExplosion.json"));
        FixtureDef alienExplosionFixtureDef = new FixtureDef();
        alienExplosionFixtureDef.density = 0.001f;
        alienExplosionFixtureDef.friction = 0.5f;
        alienExplosionFixtureDef.restitution = 0.3f;
        // set the alien explosion filter categories and masks for collisions
        alienExplosionFixtureDef.filter.categoryBits = game.CATEGORY_ROCKET_EXPLOSION;
        alienExplosionFixtureDef.filter.maskBits = game.MASK_ROCKET_EXPLOSION;
        loader.attachFixture(alienMissileExplosionBody, "Alien Missile Explosion", alienExplosionFixtureDef, ALIEN_MISSILE_EXPLOSION_HEIGHT);
        alienMissileExplosionBody.applyForceToCenter(0, 0, true);

        //Set the time the missile was exploded on the missile explosion  body.  This is used in the update method
        //to destroy the missile explosion body after a set amount of time
        BodyData alienMissileExplosionData = new BodyData(false);
        alienMissileExplosionData.setExplosionData(TimeUtils.nanoTime());
        alienMissileExplosionBody.setUserData(alienMissileExplosionData);

        //add missile explosion to alien missile explosions array
        alienMissileExplosionArray.add(alienMissileExplosionBody);

        //keep track of time the missile was spawned
        lastAlienMissileExplosionSpawnTime = TimeUtils.nanoTime();

    }

    public void spawnAlienMissileCorners(float explosionPositionX, float explosionPositionY){

        //spawn new alien missile corners
        //the corners are the circular missiles that rotates the main missile.  when they are spawned,
        //they travel in 4 different opposite directions away from the main missile
        //generate a random angle theta and send the missiles in 4 opposite directions depending on random angle theta

        float theta = MathUtils.random(0, 90);

        //spawn first alien missile corner
        BodyDef alienCornerBodyDef = new BodyDef();
        alienCornerBodyDef.type = BodyDef.BodyType.DynamicBody;
        alienCornerBodyDef.position.set(explosionPositionX + ALIEN_MISSILE_WIDTH/2 * MathUtils.cosDeg(theta), explosionPositionY + ALIEN_MISSILE_HEIGHT/2 * MathUtils.sinDeg(theta));
        Body alienCornerBody = gameWorld.createBody(alienCornerBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlienMissileCorner.json"));
        FixtureDef alienCornerFixtureDef = new FixtureDef();
        alienCornerFixtureDef.density = 0.001f;
        alienCornerFixtureDef.friction = 0.5f;
        alienCornerFixtureDef.restitution = 0.3f;
        // set the alien corner filter categories and masks for collisions
        alienCornerFixtureDef.filter.categoryBits = game.CATEGORY_ROCKET_EXPLOSION;
        alienCornerFixtureDef.filter.maskBits = game.MASK_ROCKET_EXPLOSION;
        loader.attachFixture(alienCornerBody, "AlienMissileCorner", alienCornerFixtureDef, ALIEN_MISSILE_CORNER_HEIGHT);
        alienCornerBody.applyForceToCenter(ALIEN_MISSILE_CORNER_FORCE* MathUtils.cosDeg(theta), ALIEN_MISSILE_CORNER_FORCE* MathUtils.sinDeg(theta), true);

        //Set the time the corner was spawned on the corner body.  This is used in the update method
        //to destroy the missile explosion body after a set amount of time
        BodyData alienCornerData = new BodyData(false);
        alienCornerData.setSpawnTime(TimeUtils.nanoTime());
        alienCornerBody.setUserData(alienCornerData);
        //add corner to alien corners array
        alienMissileCornerArray.add(alienCornerBody);

        BodyDef alienSecondCornerBodyDef = new BodyDef();
        alienSecondCornerBodyDef.type = BodyDef.BodyType.DynamicBody;
        alienSecondCornerBodyDef.position.set(explosionPositionX + ALIEN_MISSILE_WIDTH/2* MathUtils.sinDeg(theta), explosionPositionY - ALIEN_MISSILE_HEIGHT/2* MathUtils.cosDeg(theta));
        Body alienSecondCornerBody = gameWorld.createBody(alienSecondCornerBodyDef);
        loader.attachFixture(alienSecondCornerBody, "AlienMissileCorner", alienCornerFixtureDef, ALIEN_MISSILE_CORNER_HEIGHT);
        alienSecondCornerBody.applyForceToCenter(ALIEN_MISSILE_CORNER_FORCE* MathUtils.sinDeg(theta), -ALIEN_MISSILE_CORNER_FORCE* MathUtils.cosDeg(theta), true);
        BodyData alienSecondCornerData = new BodyData(false);
        alienSecondCornerData.setSpawnTime(TimeUtils.nanoTime());
        alienSecondCornerBody.setUserData(alienSecondCornerData);
        alienMissileCornerArray.add(alienSecondCornerBody);

        BodyDef alienThirdCornerBodyDef = new BodyDef();
        alienThirdCornerBodyDef.type = BodyDef.BodyType.DynamicBody;
        alienThirdCornerBodyDef.position.set(explosionPositionX - ALIEN_MISSILE_WIDTH/2* MathUtils.cosDeg(theta), explosionPositionY - ALIEN_MISSILE_HEIGHT/2* MathUtils.sinDeg(theta));
        Body alienThirdCornerBody = gameWorld.createBody(alienThirdCornerBodyDef);
        loader.attachFixture(alienThirdCornerBody, "AlienMissileCorner", alienCornerFixtureDef, ALIEN_MISSILE_CORNER_HEIGHT);
        alienThirdCornerBody.applyForceToCenter(-ALIEN_MISSILE_CORNER_FORCE* MathUtils.cosDeg(theta), -ALIEN_MISSILE_CORNER_FORCE* MathUtils.sinDeg(theta), true);
        BodyData alienThirdCornerData = new BodyData(false);
        alienThirdCornerData.setSpawnTime(TimeUtils.nanoTime());
        alienThirdCornerBody.setUserData(alienThirdCornerData);
        alienMissileCornerArray.add(alienThirdCornerBody);

        BodyDef alienFourthCornerBodyDef = new BodyDef();
        alienFourthCornerBodyDef.type = BodyDef.BodyType.DynamicBody;
        alienFourthCornerBodyDef.position.set(explosionPositionX - ALIEN_MISSILE_WIDTH/2* MathUtils.sinDeg(theta), explosionPositionY + ALIEN_MISSILE_HEIGHT/2* MathUtils.cosDeg(theta));
        Body alienFourthCornerBody = gameWorld.createBody(alienFourthCornerBodyDef);
        loader.attachFixture(alienFourthCornerBody, "AlienMissileCorner", alienCornerFixtureDef, ALIEN_MISSILE_CORNER_HEIGHT);
        alienFourthCornerBody.applyForceToCenter(-ALIEN_MISSILE_CORNER_FORCE* MathUtils.sinDeg(theta), ALIEN_MISSILE_CORNER_FORCE* MathUtils.cosDeg(theta), true);
        BodyData alienFourthCornerData = new BodyData(false);
        alienFourthCornerData.setSpawnTime(TimeUtils.nanoTime());
        alienFourthCornerBody.setUserData(alienFourthCornerData);
        alienMissileCornerArray.add(alienFourthCornerBody);

    }

    public void spawnAlienMissileCornerExplosions(float explosionPositionX, float explosionPositionY){

        //spawn new alien missile explosions
        BodyDef alienExplosionBodyDef = new BodyDef();
        alienExplosionBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn alien explosion at the input position (this will be the position of the center of the alien missile.
        alienExplosionBodyDef.position.set(explosionPositionX, explosionPositionY);
        Body alienMissileExplosionBody = gameWorld.createBody(alienExplosionBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/AlienMissileExplosion.json"));
        FixtureDef alienExplosionFixtureDef = new FixtureDef();
        alienExplosionFixtureDef.density = 0.001f;
        alienExplosionFixtureDef.friction = 0.5f;
        alienExplosionFixtureDef.restitution = 0.3f;
        // set the alien explosion filter categories and masks for collisions
        alienExplosionFixtureDef.filter.categoryBits = game.CATEGORY_ROCKET_EXPLOSION;
        alienExplosionFixtureDef.filter.maskBits = game.MASK_ROCKET_EXPLOSION;
        loader.attachFixture(alienMissileExplosionBody, "Alien Missile Explosion", alienExplosionFixtureDef, ALIEN_MISSILE_CORNER_EXPLOSION_HEIGHT);
        alienMissileExplosionBody.applyForceToCenter(0, 0, true);

        //Set the time the missile was exploded on the missile explosion  body.  This is used in the update method
        //to destroy the missile explosion body after a set amount of time
        BodyData alienMissileExplosionData = new BodyData(false);
        alienMissileExplosionData.setExplosionData(TimeUtils.nanoTime());
        alienMissileExplosionBody.setUserData(alienMissileExplosionData);

        //add missile explosion to alien missile explosions array
        alienMissileCornerExplosionArray.add(alienMissileExplosionBody);

        //keep track of time the missile was spawned
        lastAlienMissileCornerExplosionSpawnTime = TimeUtils.nanoTime();


    }


    private void initializeAlienMissileAnimation() {

        // Load the alien missile sprite sheet as a Texture
        alienMissileSheet = new Texture(Gdx.files.internal("sprites/AlienGrenadeSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(alienMissileSheet,
                alienMissileSheet.getWidth() / 4,
                alienMissileSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] alienFrames = new TextureRegion[4 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 4; j++) {
                alienFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        alienMissileAnimation = new Animation<TextureRegion>(0.06f, alienFrames);
    }

    private void initializeAlienMissileExplosionAnimation() {

        // Load the alien missile explosion sprite sheet as a Texture
        alienMissileExplosionSheet = new Texture(Gdx.files.internal("sprites/AlienMissileExplosionSpriteSheet.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(alienMissileExplosionSheet,
                alienMissileExplosionSheet.getWidth() / 7,
                alienMissileExplosionSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] alienFrames = new TextureRegion[7 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 7; j++) {
                alienFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        alienMissileExplosionAnimation = new Animation<TextureRegion>(0.06f, alienFrames);

    }

    private void initializeAlienMissileCornerAnimation() {

        // Load the alien missile sprite sheet as a Texture
        alienMissileCornerSheet = new Texture(Gdx.files.internal("sprites/AlienMissileCorner.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(alienMissileCornerSheet,
                alienMissileCornerSheet.getWidth() / 1,
                alienMissileCornerSheet.getHeight() / 1);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] alienFrames = new TextureRegion[1 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                alienFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        alienMissileCornerAnimation = new Animation<TextureRegion>(1f, alienFrames);
    }

    public float getLastAlienMissileSpawnTime(){
        return lastAlienMissileSpawnTime;
    }

    public void dispose(){

        alienMissileSheet.dispose();
        alienMissileExplosionSheet.dispose();
        alienMissileCornerSheet.dispose();
        alienMissileCornerExplosionSheet.dispose();
    }

}
