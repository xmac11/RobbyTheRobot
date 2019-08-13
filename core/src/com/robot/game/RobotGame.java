package com.robot.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robot.game.screens.*;
import com.robot.game.util.Assets;
import com.robot.game.util.checkpoints.FileSaver;
import com.robot.game.util.checkpoints.CheckpointData;

public class RobotGame extends Game {

	private Assets assets;
	private SpriteBatch batch;
	private CheckpointData checkpointData;

	@Override
	public void create () {
		Gdx.app.log("RobotGame", "create");

		// create and load assets
		this.assets = new Assets();
		assets.load();

		this.batch = new SpriteBatch();

		// if file with game data exists, load it, otherwise create new one
		if(FileSaver.getCheckpointFile().exists()) {
			this.checkpointData = FileSaver.loadCheckpointData();
		}
		else {
			Gdx.app.log("PlayScreen", "New file was created");
			this.checkpointData = new CheckpointData();
			checkpointData.setDefaultData(1);
			FileSaver.saveCheckpointData(checkpointData);
		}

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

	public CheckpointData getCheckpointData() {
		return checkpointData;
	}

	public void respawn(PlayScreen playScreen, CheckpointData checkpointData, int levelID) {
		Gdx.app.log("RobotGame", "Respawning...");
		// first save game data, then restart game
		FileSaver.saveCheckpointData(checkpointData);

		// dispose screen
		playScreen.dispose();

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
