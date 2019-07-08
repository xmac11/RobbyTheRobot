package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.interactiveObjects.InteractivePlatform;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Robot;
import com.robot.game.util.B2dWorldCreator;
import com.robot.game.util.Constants;
import com.robot.game.util.ContactManager;
import com.robot.game.util.DebugCamera;

import static com.robot.game.util.Constants.*;

public class PlayScreen extends ScreenAdapter {

    private ShapeRenderer shapeRenderer;

    private RobotGame game;

    // entities
    private Robot robot;

    // interactive platforms
    private DelayedRemovalArray<InteractivePlatform> interactivePlatforms;

    // enemy
    private DelayedRemovalArray<Enemy> enemies;

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

        this.shapeRenderer = new ShapeRenderer();

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
        layersArray.add(tiledMap.getLayers().get(ENEMY_OBJECT).getObjects());

        this.b2dWorldCreator = new B2dWorldCreator(world, layersArray);

        // create robot
        this.robot = new Robot(world);

        // create interactive platforms
        this.interactivePlatforms = b2dWorldCreator.getInteractivePlatforms();

        // create enemy
        this.enemies = b2dWorldCreator.getEnemies();

        // create debug camera
        this.debugCamera = new DebugCamera(viewport, robot);
    }

    private void update(float delta) {
        world.step(1 / 60f, 8, 3);

        // update interactive platforms (do this first if robot should be moving along with it)
        for(int i = 0; i < interactivePlatforms.size; i++) {
            InteractivePlatform platform = interactivePlatforms.get(i);
            // if robot is within a certain distance from the platform, activate the platform
//            if(Math.abs(platform.getBody().getPosition().x - robot.getBody().getPosition().x) < viewport.getWorldWidth())
//                platform.getBody().setActive(true);
//            else
//                platform.getBody().setActive(false);
            // if platform is active, update it
            if(platform.getBody().isActive())
                platform.update(delta);
            // if platform is destroyed, remove from array
            if(platform.isDestroyed())
                interactivePlatforms.removeIndex(i);
        }

        // update robot
        robot.update(delta);

        // update enemies
        for(int i = 0; i < enemies.size; i++)
            enemies.get(i).update(delta);

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

        // Debug draw the path of the bat
        int k = 100;
        Vector2[] points = new Vector2[k];
        for(int i = 0; i < k; ++i) {
            points[i] = new Vector2();
            enemies.get(0).path.valueAt(points[i], ((float) i) / ((float) k - 1));
        }


        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(int i = 0; i < k - 1; ++i) {
            shapeRenderer.line(points[i], points[i + 1]);
        }

//        shapeRenderer.circle(enemies.get(0).target.x, enemies.get(0).target.y, 1.0f);
        shapeRenderer.end();
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
