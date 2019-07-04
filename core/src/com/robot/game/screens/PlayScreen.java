package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.interactiveObjects.MovingPlatform;
import com.robot.game.sprites.Robot;
import com.robot.game.util.*;

import static com.robot.game.util.Constants.*;

public class PlayScreen extends ScreenAdapter {

    private RobotGame game;

    // entities
    private Robot robot;
    private MovingPlatform movingPlatform;

    // camera variables
    private OrthographicCamera camera;
    private Viewport viewport;
    private DebugCamera debugCamera;

    // Tiled map variables
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Array<MapObjects> layersArray;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private B2dWorldCreator b2dWorldCreator;

    public PlayScreen(RobotGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        System.out.println("show");

        // create camera
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(Constants.WIDTH / PPM, Constants.HEIGHT / PPM, camera);

        // load map and set up map renderer
        this.tiledMap = new TmxMapLoader().load("level1.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / PPM);

        // create box2d world
        this.world = new World(new Vector2(0, -9.81f), true);
        world.setContactListener(new ContactManager());
        this.debugRenderer = new Box2DDebugRenderer();

        // create tiled objects
        this.layersArray = new Array<>();
        layersArray.add(tiledMap.getLayers().get(GROUND_OBJECT).getObjects());
        layersArray.add(tiledMap.getLayers().get(LADDER_OBJECT).getObjects());

        this.b2dWorldCreator = new B2dWorldCreator(world, layersArray);
//        B2dWorldCreator.createTiledObjects(world, tiledMap.getLayers().get(GROUND_OBJECT).getObjects());
//        B2dWorldCreator.createTiledObjects(world, tiledMap.getLayers().get(LADDER_OBJECT).getObjects());

        // create robot
        this.robot = new Robot(world);

        // create moving platform
        this.movingPlatform = b2dWorldCreator.getMovingPlatform();

        // create debug camera
        this.debugCamera = new DebugCamera(viewport, robot);
    }

    public void update(float delta) {
        world.step(1 / 60f, 8, 3);

        // update robot
        robot.update(delta);

        // update moving platform
        movingPlatform.update();

        // update camera
        debugCamera.update(delta);

        // only render what the camera can see
        mapRenderer.setView(camera);
        game.getBatch().setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        this.update(delta); // perform all necessary updates

        // clear game screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render the map
        mapRenderer.render();

        game.getBatch().begin();
        Sprite robotSprite = robot.getRobotSprite();
        robotSprite.setSize(32 / PPM, 64 / PPM);
        robotSprite.draw(game.getBatch());
        game.getBatch().end();

        //render box2d debug rectangles
        debugRenderer.render(world, viewport.getCamera().combined);

    }

    @Override
    public void resize(int width, int height) {
        System.out.println("resize");
        viewport.update(width, height, true);
        camera.update();
    }

    @Override
    public void hide() {
        System.out.println("hide");
        this.dispose();
    }

    @Override
    public void dispose() {
        System.out.println("dispose");
//        batch.dispose();
        tiledMap.dispose();
        mapRenderer.dispose();
        world.dispose();
        debugRenderer.dispose();
        robot.dispose();
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
