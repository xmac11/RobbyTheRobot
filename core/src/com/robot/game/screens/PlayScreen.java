package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.Constants;

public class PlayScreen extends ScreenAdapter {

    private SpriteBatch batch;

    // camera variables
    private OrthographicCamera camera;
    private Viewport viewport;

    // Tiled map variables
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    @Override
    public void show() {
        this.batch = new SpriteBatch();

        // create camera
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(Constants.WIDTH, Constants.HEIGHT, camera);

        // load map and set up map renderer
        this.tiledMap = new TmxMapLoader().load("level1.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    public void update(float dt) {
//        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0); // not needed(?) if I set viewport update centercamera to true
        camera.update(); // update camera at every render cycle
        mapRenderer.setView(camera); // only render what the gameCam can see (could be in the render method probably)
    }

    @Override
    public void render(float delta) {
        this.update(delta); // perform all necessary updates

        // clear game screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        // render the map
        mapRenderer.render();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update(); // update camera at every render cycle
    }


    @Override
    public void hide() {
        this.dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
