package com.robot.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robot.game.screens.PlayScreen;

public class RobotGame extends Game {

	private SpriteBatch batch;

	@Override
	public void create () {
		this.batch = new SpriteBatch();
		super.setScreen(new PlayScreen(this));
		super.render();
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
