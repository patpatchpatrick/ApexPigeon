package io.github.patpatchpatrick.alphapigeon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "AlphaPigeon";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new AlphaPigeon(), config);
	}
}
