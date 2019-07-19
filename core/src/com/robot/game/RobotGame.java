package com.robot.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;

public class RobotGame extends Game {

	private SpriteBatch batch;

	@Override
	public void create () {
		// load assets
		Assets.getInstance().load();

		this.batch = new SpriteBatch();
		super.setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
//		super.resize(width, height);
	}

	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
		Assets.getInstance().dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

}
