package com.robot.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.robot.game.RobotGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		//test comment
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new RobotGame(), config);
	}
}
