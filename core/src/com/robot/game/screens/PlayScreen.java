package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.interactiveObjects.InteractivePlatform;
import com.robot.game.sprites.Bat;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Robot;
import com.robot.game.sprites.Crab;
import com.robot.game.util.ObjectParser;
import com.robot.game.util.ContactManager;
import com.robot.game.util.DebugCamera;
import com.robot.game.util.Parallax;

import static com.robot.game.util.Constants.*;

public class PlayScreen extends ScreenAdapter {

    public Texture background;
    private Vector2 positionCache = new Vector2();
    private Parallax parallaxBackground;
    private Parallax parallaxForeground;

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
    private Array<MapObjects> layersObjectArray;
    private int[] backgroundLayers;
    private int[] foregroundLayers;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private ObjectParser objectParser;

    public PlayScreen(RobotGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        System.out.println("show");

        if(debug_on)
            this.shapeRenderer = new ShapeRenderer();

        // create camera
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM, camera);

        // load map and set up map renderer
        this.tiledMap = new TmxMapLoader().load("level1.1.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / PPM);

        // create box2d world
        this.world = new World(new Vector2(0, -9.81f /*0*/), true);
        world.setContactListener(new ContactManager());
        if(debug_on)
            this.debugRenderer = new Box2DDebugRenderer();

        // create tiled objects
        this.layersObjectArray = new Array<>();
        layersObjectArray.add(tiledMap.getLayers().get(GROUND_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(LADDER_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(BAT_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(CRAB_OBJECT).getObjects());

        this.backgroundLayers = new int[] {1,2,3,4,5,6,7};
        this.foregroundLayers = new int[] {9};

        this.objectParser = new ObjectParser(world, layersObjectArray);

        // create robot
        this.robot = new Robot(world);

        // create interactive platforms
        this.interactivePlatforms = objectParser.getInteractivePlatforms();

        // create enemy
        this.enemies = objectParser.getEnemies();

        // create debug camera
        this.debugCamera = new DebugCamera(viewport, robot);

        this.parallaxBackground = new Parallax(viewport, robot, new Texture(Gdx.files.internal("background2.png")), 0.5f, 156, 292, false);
        this.parallaxForeground = new Parallax(viewport, robot, new Texture(Gdx.files.internal("water2.png")), 1.0f, 0, 75, true);
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
        for(int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);
            if(enemy.getBody().isActive())
                enemy.update(delta);
            if(enemy.isDestroyed())
                enemies.removeIndex(i);
        }

        // update camera
        debugCamera.update(delta);

        // only render what the camera can see
        mapRenderer.setView(camera);
        game.getBatch().setProjectionMatrix(camera.combined);

//        System.out.println("Interactive platforms: " + interactivePlatforms.size);
//        System.out.println("Number of enemies: " + enemies.size);

    }

    @Override
    public void render(float delta) {
        this.update(delta); // perform all necessary updates

        // clear game screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // FIRST BATCH

        game.getBatch().disableBlending();
        game.getBatch().begin();
        // draw background
        parallaxBackground.draw(game.getBatch());
        game.getBatch().end();
//        System.out.println("render1: " + game.getBatch().renderCalls);

        // RENDER MAP
        mapRenderer.render(backgroundLayers);

        // SECOND BATCH

        game.getBatch().enableBlending();
        game.getBatch().begin();

        Sprite robotSprite = robot.getRobotSprite();
        robotSprite.setSize(ROBOT_SPRITE_WIDTH / PPM, ROBOT_SPRITE_HEIGHT / PPM);
        robotSprite.draw(game.getBatch());
        for(Enemy enemy: enemies) {
            if(!enemy.isDestroyed() && enemy instanceof Bat) {
                Sprite batSprite = ((Bat) enemy).batSprite;
                batSprite.setSize(BAT_WIDTH / PPM, BAT_HEIGHT / PPM);
                batSprite.draw(game.getBatch());
            }
            else if(!enemy.isDestroyed()){
                Sprite spiderSprite = ((Crab) enemy).spiderSprite;
                spiderSprite.setSize(CRAB_WIDTH / PPM, CRAB_HEIGHT / PPM);
                spiderSprite.draw(game.getBatch());
            }
        }
        // draw foreground
        parallaxForeground.draw(game.getBatch());
        game.getBatch().end();

//        System.out.println("render2: " + game.getBatch().renderCalls);

        mapRenderer.render(foregroundLayers);

        //render box2d debug rectangles
        if(debug_on) {
            debugRenderer.render(world, viewport.getCamera().combined);

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < enemies.size; i++) {
                if (enemies.get(i).getPlatformID() != null) {
                    int k = enemies.get(i).getWayPoints().size;
                    Vector2[] points = new Vector2[k];

                    for (int j = 0; j < k; j++) {
                        points[j] = enemies.get(i).getWayPoints().get(j);
                    }

                    for (int j = 0; j < k - 1; j++) {
                        points[j] = enemies.get(i).getWayPoints().get(j);
                        shapeRenderer.line(points[j], points[j + 1]);
                    }
                }

            }

            shapeRenderer.end();
        }
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
        if(debug_on)
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
