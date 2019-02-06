package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.ufoEnergyBeam;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.resources.Sounds;

//Class shared by all UFO energy beams

public abstract class UfoEnergyBeam extends Dodgeable {

    protected Sound energyBeamSound;
    public float direction;


    public UfoEnergyBeam(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);
    }

    @Override
    public void reset() {
        super.reset();

        //Stop playing energy beam sound when it is no longer active and remove from active sounds
        this.energyBeamSound.stop();
        Sounds.activeSounds.remove(this.energyBeamSound);
    }

}
