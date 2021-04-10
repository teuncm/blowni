package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Blowni;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Blowni.getWIDTH();
		config.height = Blowni.getHEIGHT();
		config.title = Blowni.getTITLE();
		new LwjglApplication(new Blowni(), config);
	}
}
