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
    private Assets assets;
    private Viewport loadingScreenViewport;
    private TextureRegion frame;
    private TextureRegion greenBar;
    private BitmapFont font;

    public LoadingScreen(RobotGame game) {
        this.game = game;
        this.assets = game.getAssets();
    }

    @Override
    public void show() {
        this.loadingScreenViewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.frame = assets.loadingScreenAssets.frame;
        this.greenBar = assets.loadingScreenAssets.bar;

        this.font = assets.fontAssets.font;
    }

    @Override
    public void render(float delta) {

//        loadingScreenViewport.getCamera().update();

        // clear game screen
        Gdx.gl.glClearColor(0f / 255, 139f / 255, 139f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(!assets.getAssetManager().update()) {
            Gdx.app.log("LoadingScreen", "Loading... " + (int) (assets.getAssetManager().getProgress() * 100) + "%");

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
                    LOADING_BAR_WIDTH * assets.getAssetManager().getProgress() / PPM,
                    LOADING_BAR_HEIGHT / PPM);

            // draw loadingScreenFont
            font.draw(game.getBatch(),
                    "Loading..." + (int) (assets.getAssetManager().getProgress() * 100) + "%",
                    loadingScreenViewport.getWorldWidth() / 2,
                    loadingScreenViewport.getWorldHeight() / 2 + LOADING_FONT_OFFSET_Y / PPM,
                    0,
                    Align.center,
                    false);

            game.getBatch().end();

        }
        else {
            // dispose loading bar since it will not be needed again
            assets.getAssetManager().unload("loading_bar.pack");
            Gdx.app.log("LoadingScreen", "Loading bar atlas was disposed");

            // create all necessary game assets
            assets.createGameAssets();
            game.setScreen(new ScreenLevel2(game));
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
