package io.github.patpatchpatrick.alphapigeon;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GameCenterClient;
import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        AlphaPigeon game = new AlphaPigeon() {
            @Override
            public void create() {
                gsClient = new GameCenterClient(((IOSApplication) Gdx.app).getUIViewController());
                super.create();
            }
        };
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(game, config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}