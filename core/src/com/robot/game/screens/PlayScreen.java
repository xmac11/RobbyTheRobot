package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
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
import com.robot.game.camera.ShakeEffect;
import com.robot.game.interactiveObjects.FallingPipe;
import com.robot.game.interactiveObjects.Trampoline;
import com.robot.game.interactiveObjects.collectables.CollectableHandler;
import com.robot.game.interactiveObjects.ladder.LadderClimbHandler;
import com.robot.game.interactiveObjects.platforms.InteractivePlatform;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.entities.Enemy;
import com.robot.game.entities.Robot;
import com.robot.game.screens.huds.FeedbackRenderer;
import com.robot.game.screens.huds.Hud;
import com.robot.game.util.*;
import com.robot.game.util.checkpoints.CheckpointData;
import com.robot.game.util.checkpoints.FileSaver;
import org.json.simple.JSONArray;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Constants.PPM;

public abstract class PlayScreen extends ScreenAdapter {

    // main class reference
    protected RobotGame game;

    protected int levelID;

    // assets
    protected Assets assets;

    // Tiled map variables
    protected TiledMap tiledMap;
    protected OrthogonalTiledMapRenderer mapRenderer;
    protected float mapWidth;
    protected float mapHeight;

    // checkpoint data
    protected CheckpointData checkpointData;
    protected boolean checkpointDataDeleted;

    // Box2d variables
    protected World world;
    protected ContactManager contactManager;
    protected Box2DDebugRenderer debugRenderer;

    // object parser
    protected ObjectParser objectParser;
    protected Array<MapObjects> layersObjectArray;

    // shake effect
    protected ShakeEffect shakeEffect;

    // robot
    protected Robot robot;

    // ladder climb handler
    protected LadderClimbHandler ladderClimbHandler;

    // interactive platforms
    protected DelayedRemovalArray<InteractivePlatform> interactivePlatforms;

    // enemies
    protected DelayedRemovalArray<Enemy> enemies;

    // falling pipes
    protected DelayedRemovalArray<FallingPipe> fallingPipes;
    protected PipeBodyCache pipeBodyCache;

    // feedback renderer
    protected FeedbackRenderer feedbackRenderer;

    // camera variables
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected DebugCamera debugCamera;

    // collectables
    protected CollectableHandler collectableHandler;
    protected DelayedRemovalArray<Collectable> collectables;
    protected JSONArray collectedItems;
    protected boolean newItemCollected;
    protected boolean doNotSaveInHide;

    // trampoline
    public Trampoline trampoline;

    // Hud
    protected Hud hud;

    // debug lines for AI paths
    protected ShapeRenderer shapeRenderer;

    public PlayScreen(RobotGame game, TiledMap tiledMap, int levelID) {
        this.game = game;
        this.assets = game.getAssets();
        this.tiledMap = tiledMap;
        this.levelID = levelID;

        int tileSize = tiledMap.getProperties().get("tilewidth", Integer.class);
        this.mapWidth = tiledMap.getProperties().get("width", Integer.class) * tileSize;
        this.mapHeight = tiledMap.getProperties().get("height", Integer.class) * tileSize;
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / PPM);

        // if file with game data exists, load it, otherwise create new one
        if(FileSaver.getCheckpointFile().exists()) {
            this.checkpointData = FileSaver.loadCheckpointData();
        }
        else {
            this.checkpointData = new CheckpointData();
            checkpointData.setDefaultData();
            FileSaver.saveCheckpointData(checkpointData);
        }

        Gdx.app.log("PlayScreen", "New game started.");
        Gdx.app.log("PlayScreen", "Lives " + checkpointData.getLives());
        Gdx.app.log("PlayScreen", "Health " + checkpointData.getHealth());

        // create camera
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM, camera);

        // create box2d world
        this.world = new World(new Vector2(0, -9.81f), true);
        this.contactManager = new ContactManager();
        world.setContactListener(contactManager);
        if(DEBUG_ON)
            this.debugRenderer = new Box2DDebugRenderer();

        // create collectable handler
        this.collectableHandler = new CollectableHandler(levelID);
        this.collectedItems = collectableHandler.getCollectedItems();

        // create shake effect
        this.shakeEffect = new ShakeEffect();

        // create robot
        this.robot = new Robot(this);

        // create ladder climb handler
        this.ladderClimbHandler = new LadderClimbHandler(robot);

        // create falling pipes and cache 5 pipes
        this.fallingPipes = new DelayedRemovalArray<>();
        this.pipeBodyCache = new PipeBodyCache();
        for(int i = 0; i < 5; i++) {
            fallingPipes.add(new FallingPipe(this, true));
        }

        // points to draw
        this.feedbackRenderer = new FeedbackRenderer(robot);

        // create debug camera
        this.debugCamera = new DebugCamera(this);

        // create hud
        this.hud = new Hud(this);

        if(DEBUG_ON)
            this.shapeRenderer = new ShapeRenderer();
    }

    /*@Override
    public void show() {

    }*/

    protected void createCommonObjectLayers() {
        // create object parser
        this.objectParser = new ObjectParser(this);

        // create interactive platforms
        this.interactivePlatforms = objectParser.getInteractivePlatforms();

        // create enemies
        this.enemies = objectParser.getEnemies();

        // create collectables
        this.collectables = objectParser.getCollectables();
    }

    protected void commonUpdates(float delta) {
        // perform physics simulation
        world.step(1 / 60f, 8, 3);

        // update interactive platforms (do this first if robot should be moving along with it)
        for(int i = 0; i < interactivePlatforms.size; i++) {
            InteractivePlatform platform = interactivePlatforms.get(i);

            // if platform is active, update it
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
            enemy.update(delta);

            // for path-following bat that is activated when the robot gets near it
            if(Math.abs(enemy.getBody().getPosition().x - robot.getBody().getPosition().x) < 128 / PPM)
                enemy.getBody().setActive(true);

            if(enemy.isDestroyed())
                enemies.removeIndex(i);
        }

        // update collectables
        for(int i = 0; i < collectables.size; i++) {
            Collectable collectable = collectables.get(i);
            collectable.update(delta);

            if(collectable.isDestroyed())
                collectables.removeIndex(i);
        }
    }

    protected void updateViews(float delta) {
        // update camera
        debugCamera.update(delta);
        hud.getHudViewport().getCamera().update();

        // only render what the camera can see
        mapRenderer.setView(camera);
        game.getBatch().setProjectionMatrix(camera.combined);
    }

    protected void commonRendering(float delta) {
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

        // render collectables
        for(Collectable collectable: collectables) {
            if(!collectable.isDestroyed())
                collectable.draw(game.getBatch());
        }

        // render feedback
        // This has to be done within the game's viewport and not the hud's, since the position of the bodies are needed.
        feedbackRenderer.draw(game.getBatch(), delta);

        // render Hud
        hud.draw(game.getBatch());
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
        // unless checkpoints are wiped out, save the game every time it is closed
        if(!checkpointDataDeleted) {
            FileSaver.saveCheckpointData(checkpointData);
        }
        // if any items were collected and they have not been already saved in the checkIfDead() method, save them
        if(newItemCollected && !doNotSaveInHide) {
            for(int collectableID: collectableHandler.getItemsToDisableSpawning()) {
                collectableHandler.setSpawn(collectableID, false);
            }
            FileSaver.saveCollectedItems(collectedItems);
        }
        this.dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("PlayScreen", "dispose");
        mapRenderer.dispose();
        world.dispose();
        if(DEBUG_ON)
            debugRenderer.dispose();
    }


    public RobotGame getGame() {
        return game;
    }

    public Assets getAssets() {
        return assets;
    }

    public World getWorld() {
        return world;
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    public CheckpointData getCheckpointData() {
        return checkpointData;
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

    public DelayedRemovalArray<FallingPipe> getFallingPipes() {
        return fallingPipes;
    }

    public ShakeEffect getShakeEffect() {
        return shakeEffect;
    }

    public LadderClimbHandler getLadderClimbHandler() {
        return ladderClimbHandler;
    }

    public void setCheckpointDataDeleted(boolean checkpointDataDeleted) {
        this.checkpointDataDeleted = checkpointDataDeleted;
    }

    public Array<MapObjects> getLayersObjectArray() {
        return layersObjectArray;
    }

    public void setNewItemCollected(boolean newItemCollected) {
        this.newItemCollected = newItemCollected;
    }

    public CollectableHandler getCollectableHandler() {
        return collectableHandler;
    }

    public FeedbackRenderer getFeedbackRenderer() {
        return feedbackRenderer;
    }

    public PipeBodyCache getPipeBodyCache() {
        return pipeBodyCache;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public int getLevelID() {
        return levelID;
    }

    protected void checkIfDead() {
        // robot died but has remaining lives
        if(robot.isDead() && checkpointData.getLives() >= 0) {
            Gdx.app.log("PlayScreen", "Player died");
            // loop through all items that have been collected and disable their spawning
            for(int collectableID: collectableHandler.getItemsToDisableSpawning()) {
                collectableHandler.setSpawn(collectableID, false);
            }
            // if a new item has been collected in this session, save the file with collected items and disable saving from the hide() method
            if(newItemCollected) {
                FileSaver.saveCollectedItems(collectedItems);
                doNotSaveInHide = true;
            }
            // finally restart the game
            game.respawn(checkpointData, levelID);
        }
        // robot died and has no remaining lives
        else if(robot.isDead()) {
            Gdx.app.log("PlayScreen", "Player died, no more lives left :(");

            // reset checkpoint data
            checkpointData.setDefaultData();

            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                FileSaver.getCollectedItemsFile().delete();
            }
            // finally restart the game
            game.respawn(checkpointData, levelID);
        }
    }
}
