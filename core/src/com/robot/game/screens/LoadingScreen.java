package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class LoadingScreen extends ScreenAdapter {

    private RobotGame game;
    private Viewport loadingScreenViewport;
    private TextureRegion frame;
    private TextureRegion greenBar;
    private BitmapFont font;

    public LoadingScreen(RobotGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        this.loadingScreenViewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.frame = Assets.getInstance().loadingBarAssets.frame;
        this.greenBar = Assets.getInstance().loadingBarAssets.bar;

        this.font = Assets.getInstance().fontAssets.font;
    }

    @Override
    public void render(float delta) {

//        loadingScreenViewport.getCamera().update();

        // clear game screen
        Gdx.gl.glClearColor(95f / 255, 158f / 255, 160f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(!Assets.getInstance().assetManager.update()) {
            Gdx.app.log("LoadingScreen", "Loading... " + (int) (1.2f * Assets.getInstance().assetManager.getProgress() * 100) + "%");

            game.getBatch().setProjectionMatrix(loadingScreenViewport.getCamera().combined);

            game.getBatch().begin();

            // draw frame
            game.getBatch().draw(frame,
                    loadingScreenViewport.getWorldWidth() / 2 - LOADING_FRAME_WIDTH / 2 / PPM,
                    loadingScreenViewport.getWorldHeight() / 2 - LOADING_FRAME_HEIGHT / 2 / PPM,
                    LOADING_FRAME_WIDTH / PPM,
                    LOADING_FRAME_HEIGHT / PPM);

            // draw bar
            game.getBatch().draw(greenBar,
                    loadingScreenViewport.getWorldWidth() / 2 - LOADING_BAR_OFFSET_X / PPM,
                    loadingScreenViewport.getWorldHeight() / 2 - LOADING_BAR_OFFSET_Y / PPM,
                    1.2f * LOADING_BAR_WIDTH * Assets.getInstance().assetManager.getProgress() / PPM,
                    LOADING_BAR_HEIGHT / PPM);

            // draw font
            font.draw(game.getBatch(),
                    "Loading..." + (int) (1.2f * Assets.getInstance().assetManager.getProgress() * 100) + "%",
                    loadingScreenViewport.getWorldWidth() / 2,
                    loadingScreenViewport.getWorldHeight() / 2 + LOADING_FONT_OFFSET_Y / PPM,
                    0,
                    Align.center,
                    false);

            game.getBatch().end();

        }
        else {
            // dispose loading bar since it will not be needed again
            Assets.getInstance().assetManager.unload("loading_bar.pack");
            Gdx.app.log("LoadingScreen", "Loading bar was disposed");

            // create all necessary game assets
            Assets.getInstance().createGameAssets();
            game.setScreen(new PlayScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        loadingScreenViewport.update(width, height, true);
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
