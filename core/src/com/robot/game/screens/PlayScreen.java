package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.sprites.Robot;
import com.robot.game.util.B2dWorld;
import com.robot.game.util.Constants;
import com.robot.game.util.DebugCamera;

import static com.robot.game.util.Constants.*;

public class PlayScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private Robot robot;

    // camera variables
    private OrthographicCamera camera;
    private Viewport viewport;
//    private DebugCamera debugCamera;

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
//        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0); // not needed(?) if I set viewport update centercamera to true

        // load map and set up map renderer
        this.tiledMap = new TmxMapLoader().load("level1.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / PPM);



        // create box2d world
        this.world = new World(new Vector2(0, -9.81f), true);
        this.debugRenderer = new Box2DDebugRenderer();

        // create tiled objects
        B2dWorld.createTiledObjects(world, tiledMap.getLayers().get(GROUND_OBJECT).getObjects());
        B2dWorld.createTiledObjects(world, tiledMap.getLayers().get(LADDER_OBJECT).getObjects());

        // create robot
        this.robot = new Robot(this);

        // create debug camera
//        this.debugCamera = new DebugCamera(viewport.getCamera(), robot);

    }

    public void update(float delta) {
        world.step(1 / 60f, 8, 3);

        handleInput(delta);

        robot.update(delta);

        // camera updates (maybe new method)
        // camera follows robot
        camera.position.x = robot.getBody().getPosition().x;

        // clamp camera within map
        MapProperties mapProperties = tiledMap.getProperties();
        final float MAP_WIDTH = mapProperties.get("width", Integer.class) * TILE_SIZE;
        final float MAP_HEIGHT = mapProperties.get("height", Integer.class) * TILE_SIZE;
        camera.position.x = MathUtils.clamp(camera.position.x,
                                       viewport.getWorldWidth() / 2,
                                      MAP_WIDTH / PPM - viewport.getWorldWidth() / 2);

        camera.update(); // update camera at every render cycle
//        debugCamera.update(delta);

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
        debugRenderer.render(world, viewport.getCamera().combined);

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
        world.dispose();
//        debugRenderer.dispose();
    }

    public void handleInput(float dt) {
        int horizontalForce = 0; // reset every time

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            //            gameCam.position.x += 5 * dt;
            horizontalForce += 2;
            robot.getBody().applyLinearImpulse(new Vector2(0.1f, 0), robot.getBody().getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            //            gameCam.position.x -= 5 * dt;
            horizontalForce -= 2;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            //            gameCam.position.y += 5 * dt;
            robot.getBody().applyForceToCenter(0, 60, false);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            //            gameCam.position.y -= 5 * dt;
        }
        robot.getBody().setLinearVelocity(horizontalForce * 5, robot.getBody().getLinearVelocity().y);
    }

    public Robot getRobot() {
        return robot;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public World getWorld() {
        return world;
    }
}
