package com.robot.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robot.game.screens.LoadingScreen;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.FileSaver;
import com.robot.game.util.GameData;

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

	public void respawn(GameData gameData) {
		// first save game data, then restart game
		FileSaver.saveData(gameData);
		setScreen(new PlayScreen(this));
	}

}
