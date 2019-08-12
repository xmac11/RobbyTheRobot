package com.robot.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robot.game.screens.LoadingScreen;
import com.robot.game.screens.ScreenLevel1;
import com.robot.game.screens.ScreenLevel2;
import com.robot.game.screens.ScreenLevel3;
import com.robot.game.util.Assets;
import com.robot.game.util.checkpoints.FileSaver;
import com.robot.game.util.checkpoints.CheckpointData;

public class RobotGame extends Game {

	private Assets assets;
	private SpriteBatch batch;

	@Override
	public void create () {
		Gdx.app.log("RobotGame", "create");

		// create and load assets
		this.assets = new Assets();
		assets.load();

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
		assets.dispose();
	}

	public Assets getAssets() {
		return assets;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public void respawn(CheckpointData checkpointData, int levelID) {
		Gdx.app.log("RobotGame", "Respawning...");
		// first save game data, then restart game
		FileSaver.saveCheckpointData(checkpointData);

		switch(levelID) {
			case 1:
				setScreen(new ScreenLevel1(this));
				break;
			case 2:
				setScreen(new ScreenLevel2(this));
				break;
			case 3:
				setScreen(new ScreenLevel3(this));
				break;
		}

	}

}
