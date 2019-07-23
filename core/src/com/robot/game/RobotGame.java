package com.robot.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robot.game.screens.LoadingScreen;
import com.robot.game.screens.ScreenLevel1;
import com.robot.game.util.Assets;
import com.robot.game.util.FileSaver;
import com.robot.game.util.CheckpointData;

public class RobotGame extends Game {

	private SpriteBatch batch;

	@Override
	public void create () {
		Gdx.app.log("RobotGame", "create");
		// load assets
		Assets.getInstance().load();

		this.batch = new SpriteBatch();
		super.setScreen(new LoadingScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log("RobotGame", "resize");
		super.resize(width, height);
	}

	@Override
	public void dispose () {
		Gdx.app.log("RobotGame", "dispose");
		super.dispose();
		batch.dispose();
		Assets.getInstance().dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public void respawn(CheckpointData checkpointData) {
		Gdx.app.log("RobotGame", "Respawning...");
		// first save game data, then restart game
		FileSaver.saveCheckpointData(checkpointData);
		setScreen(new ScreenLevel1(this));
	}

}
