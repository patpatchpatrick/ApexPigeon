package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.patpatchpatrick.alphapigeon.Pigeon;

public class Controller {

    private Pigeon pigeon;
    private Body pigeonBody;
    private OrthographicCamera camera;

    private Boolean accelerometerAvailable;

    //Variables
    private final float PIGEON_INPUT_FORCE = 7.0f;

    public Controller(Pigeon pigeon, OrthographicCamera camera){
        this.pigeon = pigeon;
        pigeonBody = this.pigeon.getBody();
        this.camera = camera;

        //Determine if an accelerometer is available
        accelerometerAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
    }

    public void processTouchInput(){
        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            // the camera unproject method converts touchPos coordinates to the
            // camera coordinate system
            camera.unproject(touchPos);
            pigeonBody.applyForceToCenter(0.3f * (touchPos.x - pigeonBody.getPosition().x), 0.3f * (touchPos.y - pigeonBody.getPosition().y), true);
        }
    }

    public void processKeyInput(){
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            pigeonBody.applyForceToCenter(-PIGEON_INPUT_FORCE, 0,  true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            pigeonBody.applyForceToCenter(PIGEON_INPUT_FORCE, 0,  true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            pigeonBody.applyForceToCenter(0, PIGEON_INPUT_FORCE,  true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            pigeonBody.applyForceToCenter(0, -PIGEON_INPUT_FORCE,  true);
        }

    }

    public void processAccelerometerInput(){
        if (accelerometerAvailable){
            float accelX = Gdx.input.getAccelerometerX();
            float accelY = Gdx.input.getAccelerometerY();

            float yForce = -accelX;
            float xForce = accelY;

            pigeonBody.applyForceToCenter(xForce, yForce, true);

        }

    }



}
