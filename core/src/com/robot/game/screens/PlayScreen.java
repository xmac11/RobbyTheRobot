package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.camera.DebugCamera;
import com.robot.game.camera.Parallax;
import com.robot.game.interactiveObjects.InteractivePlatform;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Robot;
import com.robot.game.util.*;

import static com.robot.game.util.Constants.*;

public class PlayScreen extends ScreenAdapter {

    // main class reference
    private RobotGame game;

    // game data
    private GameData gameData;

    // robot
    private Robot robot;

    // interactive platforms
    private DelayedRemovalArray<InteractivePlatform> interactivePlatforms;

    // enemies
    private DelayedRemovalArray<Enemy> enemies;

    // camera variables
    private OrthographicCamera camera;
    private Viewport viewport;
    private DebugCamera debugCamera;

    // Tiled map variables
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Array<MapObjects> layersObjectArray;
    private int[] backgroundWallLayer;
    private int[] mapLayers;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private ObjectParser objectParser;

    // parallax scrolling
    private Parallax parallaxBackground;
    private Parallax parallaxBarrels;

    // Hud
    private Hud hud;

    // debug lines for AI paths
    private ShapeRenderer shapeRenderer;

    public PlayScreen(RobotGame game) {
        this.game = game;

        // if file with game data exists, load it, otherwise create new one
        if(FileSaver.getFile().exists()) {
            this.gameData = FileSaver.loadData();
        }
        else {
            this.gameData = new GameData();
            gameData.setDefaultData();
            FileSaver.saveData(gameData);
        }
        Gdx.app.log("PlayScreen", "Lives " + gameData.getLives());
        Gdx.app.log("PlayScreen", "Health " + gameData.getHealth());
    }

    @Override
    public void show() {
        Gdx.app.log("PlayScreen", "show");

        if(DEBUG_ON)
            this.shapeRenderer = new ShapeRenderer();

        // create camera
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM, camera);

        // load map and set up map renderer
        this.tiledMap = new TmxMapLoader().load("level1.1.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / PPM);

        // create box2d world
        this.world = new World(new Vector2(0, -9.81f /*0*/), true);
        world.setContactListener(new ContactManager());
        if(DEBUG_ON)
            this.debugRenderer = new Box2DDebugRenderer();

        // create tiled objects
        this.layersObjectArray = new Array<>();
        layersObjectArray.add(tiledMap.getLayers().get(GROUND_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(LADDER_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(BAT_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(CRAB_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(SPIKE_OBJECT).getObjects());

        this.backgroundWallLayer = new int[] {1};
        this.mapLayers = new int[] {2, 3, 4, 5, 6, 7, 8, 9, 11};

        this.objectParser = new ObjectParser(world, layersObjectArray);

        // create robot
        this.robot = new Robot(this);

        // create interactive platforms
        this.interactivePlatforms = objectParser.getInteractivePlatforms();

        // create enemy
        this.enemies = objectParser.getEnemies();

        // create debug camera
        this.debugCamera = new DebugCamera(viewport, robot);

        // create parallax
        this.parallaxBackground = new Parallax(viewport, robot, Assets.getInstance().parallaxAssets.backgroundTexture, 0.5f, 192, 260, false);
        this.parallaxBarrels = new Parallax(viewport, robot, Assets.getInstance().parallaxAssets.barrelsTexture, 1.0f, 0, 75, true);

        // create hud
        this.hud = new Hud(this);

    }

    private void update(float delta) {
        world.step(1 / 60f, 8, 3);

        // update interactive platforms (do this first if robot should be moving along with it)
        for(int i = 0; i < interactivePlatforms.size; i++) {
            InteractivePlatform platform = interactivePlatforms.get(i);
            // if robot is within a certain distance from the platform, activate the platform
            //            if(Math.abs(platform.getBody().getSpawnLocation().x - robot.getBody().getSpawnLocation().x) < viewport.getWorldWidth())
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
//        hud.getHudViewport().getCamera().update();

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

        //render wall
        mapRenderer.render(backgroundWallLayer);

        // FIRST BATCH
        // render background
        game.getBatch().disableBlending();
        game.getBatch().begin();
        parallaxBackground.draw(game.getBatch());
        game.getBatch().end();
        //        System.out.println("render1: " + game.getBatch().renderCalls);

        // render map
        mapRenderer.render(mapLayers);

        // SECOND BATCH
        game.getBatch().enableBlending();
        game.getBatch().begin();

        // render foreground (waves and barrels)
        parallaxBarrels.draw(game.getBatch());

        // render interactive platforms
        for(InteractivePlatform platform: interactivePlatforms) {
            if(!platform.isDestroyed()) {
                platform.draw(game.getBatch());
            }
        }

        // render robot
        robot.draw(game.getBatch(), delta);

        // render enemies
        for(Enemy enemy: enemies) {
            if(!enemy.isDestroyed()) {
                enemy.draw(game.getBatch());
            }
        }

        // render Hud
        hud.draw(game.getBatch());

        game.getBatch().end();

        //        System.out.println("render2: " + game.getBatch().renderCalls);

        //render box2d debug rectangles
        if(DEBUG_ON) {
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

        // finally, check if robot is dead
        checkIfDead();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("PlayScreen", "resize");
        viewport.update(width, height, true);
        hud.getHudViewport().update(width, height, true);
        camera.update();
    }

    @Override
    public void hide() {
        Gdx.app.log("PlayScreen", "hide");
        // save the game every time it is closed
        FileSaver.saveData(gameData);
        this.dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("PlayScreen", "dispose");
        tiledMap.dispose();
        mapRenderer.dispose();
        world.dispose();
        if(DEBUG_ON)
            debugRenderer.dispose();
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

    public GameData getGameData() {
        return gameData;
    }

    public RobotGame getGame() {
        return game;
    }

    private void checkIfDead() {
        if(robot.isDead() && gameData.getLives() >= 0) {
            Gdx.app.log("PlayScreen", "Player died");
            game.respawn(gameData);

            // with setTransform
            /*robot.setDead(false);
            robot.getBody().setTransform(gameData.getSpawnLocation(), 0);*/
        }
        else if(robot.isDead()) {
            Gdx.app.log("PlayScreen", "Player died, no more lives left :(");
            gameData.setDefaultData();
            game.respawn(gameData);
        }
    }

}