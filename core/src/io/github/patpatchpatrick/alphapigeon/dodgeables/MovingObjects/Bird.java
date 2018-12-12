package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Bird implements Pool.Poolable{

    public Vector2 position;
    public boolean alive;

    private AlphaPigeon game;
    private World gameWorld;
    private OrthographicCamera camera;

    private Body levelOneBirdBody;

    private final float LEVEL_ONE_BIRD_WIDTH = 6f;
    private final float LEVEL_ONE_BIRD_HEIGHT = 6f;
    private final float LEVEL_ONE_FORCE_X = -9.0f;

    public Bird(World gameWorld, AlphaPigeon game, OrthographicCamera camera){
        this.position =  new Vector2();
        this.alive = false;

        this.gameWorld = gameWorld;
        this.game = game;
        this.camera = camera;

        //spawn a new level one bird
        BodyDef levelOneBirdBodyDef = new BodyDef();
        levelOneBirdBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn bird at random height
        levelOneBirdBodyDef.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - LEVEL_ONE_BIRD_HEIGHT));
        levelOneBirdBody = gameWorld.createBody(levelOneBirdBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/LevelOnePigeon.json"));
        FixtureDef levelOneBirdFixtureDef = new FixtureDef();
        levelOneBirdFixtureDef.density = 0.001f;
        levelOneBirdFixtureDef.friction = 0.5f;
        levelOneBirdFixtureDef.restitution = 0.3f;
        // set the bird filter categories and masks for collisions
        levelOneBirdFixtureDef.filter.categoryBits = game.CATEGORY_LEVEL_ONE_BIRD;
        levelOneBirdFixtureDef.filter.maskBits = game.MASK_LEVEL_ONE_BIRD;
        loader.attachFixture(levelOneBirdBody, "BackwardsPigeon", levelOneBirdFixtureDef, LEVEL_ONE_BIRD_HEIGHT);


    }

    public void update(){
        this.position = levelOneBirdBody.getPosition();
    }

    public void init(){

        levelOneBirdBody.setActive(true);
        this.position.set(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - LEVEL_ONE_BIRD_HEIGHT));
        levelOneBirdBody.setTransform(this.position, levelOneBirdBody.getAngle());
        levelOneBirdBody.applyForceToCenter(LEVEL_ONE_FORCE_X, 0, true);
        this.alive = true;

    }

    @Override
    public void reset() {

        levelOneBirdBody.setActive(false);
        this.position.set(-90,  -90);
        levelOneBirdBody.setTransform(this.position, levelOneBirdBody.getAngle());
        Vector2 vel = levelOneBirdBody.getLinearVelocity();
        vel.x = 0f;
        vel.y = 0f;
        levelOneBirdBody.setLinearVelocity(vel);
        this.alive = false;

    }


}
