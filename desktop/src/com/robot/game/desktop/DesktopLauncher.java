package com.robot.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.robot.game.util.Constants;
import com.robot.game.RobotGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) Constants.WIDTH;
		config.height = (int) Constants.HEIGHT;
		new LwjglApplication(new RobotGame(), config);
	}
}
