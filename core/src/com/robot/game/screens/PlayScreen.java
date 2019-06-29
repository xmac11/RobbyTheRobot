package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.util.B2dWorld;
import com.robot.game.util.Constants;

import static com.robot.game.util.Constants.LADDER_OBJECT;
import static com.robot.game.util.Constants.PPM;

public class PlayScreen extends ScreenAdapter {

    private SpriteBatch batch;

    // camera variables
    private OrthographicCamera camera;
    private Viewport viewport;

    // Tiled map variables
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer debugRenderer;

    @Override
    public void show() {
        System.out.println("show");
        this.batch = new SpriteBatch();

        // create camera
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(Constants.WIDTH / PPM, Constants.HEIGHT / PPM, camera);

        // load map and set up map renderer
        this.tiledMap = new TmxMapLoader().load("level1.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / PPM);

        // create box2d world
        this.world = new World(new Vector2(0, 0), true);
        this.debugRenderer = new Box2DDebugRenderer();

        // create ladder object
        B2dWorld.createTiledObjects(world, tiledMap.getLayers().get(LADDER_OBJECT).getObjects());
    }

    public void update(float delta) {
        world.step(1 / 60f, 8, 3);

//        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0); // not needed(?) if I set viewport update centercamera to true
        camera.update(); // update camera at every render cycle
        mapRenderer.setView(camera); // only render what the gameCam can see (could be in the render method probably)
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        this.update(delta); // perform all necessary updates

        // clear game screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render the map
        mapRenderer.render();

        //render box2d debug rectangles
        debugRenderer.render(world, camera.combined);

    }

    @Override
    public void resize(int width, int height) {
        System.out.println("resize");
        viewport.update(width, height, true);
        camera.update(); // update camera at every render cycle
    }


    @Override
    public void hide() {
        System.out.println("hide");
        this.dispose();
    }

    @Override
    public void dispose() {
        System.out.println("dispose");
        batch.dispose();
        tiledMap.dispose();
        mapRenderer.dispose();
    }

}
