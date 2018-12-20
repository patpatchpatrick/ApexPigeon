package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

public class UFO extends Dodgeable {

    public final float WIDTH = 15f;
    public final float HEIGHT = WIDTH;
    private final float FORCE_X = -9.0f;
    public float direction = 0f;
    public boolean energyBallIsSpawned = false;
    public long spawnTime;
    public Array<EnergyBall> energyBalls = new Array<EnergyBall>();

    public UFO(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);

        //spawn a new ufo
        BodyDef ufoBodyDef = new BodyDef();
        ufoBodyDef.type = BodyDef.BodyType.DynamicBody;

        //spawn ufo at random height
        ufoBodyDef.position.set(-100,-100);
        dodgeableBody = gameWorld.createBody(ufoBodyDef);
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("json/Ufo.json"));
        FixtureDef ufoFixtureDef = new FixtureDef();
        ufoFixtureDef.density = 0.001f;
        ufoFixtureDef.friction = 0.5f;
        ufoFixtureDef.restitution = 0.3f;
        // set the ufo filter categories and masks for collisions
        ufoFixtureDef.filter.categoryBits = game.CATEGORY_UFO;
        ufoFixtureDef.filter.maskBits = game.MASK_UFO;
        loader.attachFixture(dodgeableBody, "Ufo", ufoFixtureDef, HEIGHT);


    }

    public void init(float direction) {

        //Set the direction which the energy beams associated with the UFO should fire
        this.direction = direction;

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(camera.viewportWidth, MathUtils.random(0, camera.viewportHeight - HEIGHT), dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(FORCE_X, 0, true);
        this.alive = true;

        //keep track of time the ufo was spawned
        spawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;



    }

    public void initHorizontal(float direction){

        //Set the direction which the energy beams associated with the UFO should fire
        this.direction = direction;

        dodgeableBody.setActive(true);
        dodgeableBody.setTransform(camera.viewportWidth, ( camera.viewportHeight - HEIGHT)/2, dodgeableBody.getAngle());
        dodgeableBody.applyForceToCenter(FORCE_X, 0, true);
        this.alive = true;

        //keep track of time the ufo was spawned
        spawnTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;


        }

    @Override
    public void reset() {
        super.reset();

        //Clear all values set on previous UFOs
        this.energyBallIsSpawned = false;
        this.energyBalls.clear();
        this.spawnTime = 0;

    }
}
