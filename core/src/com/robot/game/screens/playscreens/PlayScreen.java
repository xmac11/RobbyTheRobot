package com.robot.game.screens.playscreens;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.Assets;
import com.robot.game.RobotGame;
import com.robot.game.camera.DebugCamera;
import com.robot.game.camera.ShakeEffect;
import com.robot.game.checkpoints.CheckpointData;
import com.robot.game.checkpoints.FileSaver;
import com.robot.game.entities.Robot;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.entities.abstractEnemies.EnemyPathFollowingAI;
import com.robot.game.interactiveObjects.Trampoline;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.interactiveObjects.collectables.CollectableHandler;
import com.robot.game.interactiveObjects.fallingPipes.FallingPipe;
import com.robot.game.interactiveObjects.ladder.LadderClimbHandler;
import com.robot.game.interactiveObjects.platforms.InteractivePlatform;
import com.robot.game.interactiveObjects.spikes.JointHandler;
import com.robot.game.interactiveObjects.spikes.MovingSpike;
import com.robot.game.interactiveObjects.tankBalls.TankBall;
import com.robot.game.interactiveObjects.tankBalls.TankBallSpawner;
import com.robot.game.raycast.LaserHandler;
import com.robot.game.raycast.PunchHandler;
import com.robot.game.screens.huds.FeedbackRenderer;
import com.robot.game.screens.huds.Hud;
import com.robot.game.screens.lostscreens.GameOverScreen;
import com.robot.game.screens.lostscreens.LostLifeScreen;
import com.robot.game.screens.menuscreens.MenuScreen;
import com.robot.game.util.AndroidController;
import com.robot.game.util.ContactManager;
import com.robot.game.util.MyOrthogonalTiledMapRenderer;
import com.robot.game.util.ObjectParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static com.robot.game.util.constants.Constants.*;

public abstract class PlayScreen extends ScreenAdapter {

    // main class reference
    protected RobotGame game;
    protected SpriteBatch batch;

    // reference to current level
    protected int levelID;

    // paused/escaped
    protected boolean paused;
    protected boolean escapePressed;
    protected boolean damageON;
    protected boolean toMenuFromPaused;
    protected boolean muted;

    // assets
    protected Assets assets;

    // Tiled map variables
    protected TiledMap tiledMap;
    protected MyOrthogonalTiledMapRenderer mapRenderer;
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
    protected Trampoline trampoline;

    // tank balls
    protected DelayedRemovalArray<TankBall> tankBalls;
    protected TankBallSpawner tankBallSpawner;

    // Hud
    protected Hud hud;

    // shape renderer
    protected ShapeRenderer shapeRenderer;

    // laser handler
    protected LaserHandler laserHandler;

    // punch handler
    protected PunchHandler punchHandler;

    // box2d light
    // laser
    protected RayHandler rayHandler;
    protected PointLight pointLight;
    // torch
    protected RayHandler rayHandlerTorch;
    protected ConeLight coneLight;
    protected PointLight pointLightHand;
    protected PointLight pointLightHead;

    // moving spikes - prismatic joints - jointHandler
    protected Array<MovingSpike> movingSpikes;
    protected Array<PrismaticJoint> joints;
    protected JointHandler jointHandler;

    // keep track of score on game over or game complete
    protected int scoreOnGameEnd;

    // music
    protected Music music;

    // android
    protected boolean onAndroid;
    protected AndroidController androidController;

    public PlayScreen(RobotGame game, TiledMap tiledMap, int levelID) {
        this.game = game;
        this.batch = game.getBatch();
        this.assets = game.getAssets();
        this.checkpointData = game.getCheckpointData();
        this.tiledMap = tiledMap;
        this.levelID = levelID;
        this.muted = game.getPreferences().getBoolean("muted");
        this.damageON = true;

        int tileSize = tiledMap.getProperties().get("tilewidth", Integer.class);
        this.mapWidth = tiledMap.getProperties().get("width", Integer.class) * tileSize;
        this.mapHeight = tiledMap.getProperties().get("height", Integer.class) * tileSize;
        this.mapRenderer = new MyOrthogonalTiledMapRenderer(tiledMap, 1 / PPM);

        Gdx.app.log("PlayScreen", "New game started.");
        Gdx.app.log("PlayScreen", "Lives " + checkpointData.getLives());
        Gdx.app.log("PlayScreen", "Health " + checkpointData.getHealth());

        // create camera
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM, camera);

        // create box2d world
        this.world = new World(new Vector2(0, -9.81f), true);
        this.contactManager = new ContactManager();
        world.setContactListener(contactManager);
        if(DEBUG_ON) {
            this.debugRenderer = new Box2DDebugRenderer();
        }

        // create collectable handler
        this.collectableHandler = new CollectableHandler(levelID);
        this.collectedItems = collectableHandler.getCollectedItems();

        // create shake effect
        this.shakeEffect = new ShakeEffect();

        // create robot
        this.robot = new Robot(this);

        // create ladder climb handler
        this.ladderClimbHandler = new LadderClimbHandler(robot);

        // points to draw
        this.feedbackRenderer = new FeedbackRenderer(robot);

        // create debug camera
        this.debugCamera = new DebugCamera(this);

        // create hud
        this.hud = new Hud(this);

        // create shape renderer
        this.shapeRenderer = new ShapeRenderer();

        // create android controller
        this.onAndroid = (Gdx.app.getType() == Application.ApplicationType.Android);
        this.androidController = new AndroidController(this);

        // if we are on android, and the android file does not exist, parse the file stored in assets folder and save it in an android file (different location)
        if(onAndroid) {
            FileHandle file = Gdx.files.internal(FOLDER_NAME + "level" + levelID + ".json");
            FileHandle androidFile = Gdx.files.local(Gdx.files.getLocalStoragePath() + "level" + levelID + ".json");

            // if the android file does not exist
            if(!androidFile.exists()) {
                JSONObject root = null;
                try {
                    // parse the file stored in assets folder
                    root = (JSONObject) new JSONParser().parse(file.reader());
                }
                catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                // and save it in an android file (different location)
                if(root != null) {
                    FileSaver.saveJsonMap(androidFile, root);
                }
            }
        }

        // set input processor
        Gdx.input.setInputProcessor(onAndroid ? androidController.getStage() : null);
    }

    public abstract void checkIfLevelComplete();

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
        for(InteractivePlatform interactivePlatform: interactivePlatforms) {
            interactivePlatform.update(delta);
        }

        // update robot
        robot.update(delta);

        // update enemies
        for(Enemy enemy: enemies) {
            enemy.update(delta);
        }

        // update collectables
        for(Collectable collectable: collectables) {
            collectable.update(delta);
        }
    }

    protected void updateViews(float delta) {
        // update camera
        debugCamera.update(delta);
        hud.getHudViewport().getCamera().update();

        // only render what the camera can see
        mapRenderer.setView(camera);
        batch.setProjectionMatrix(camera.combined);
    }

    protected void commonRendering(float delta) {
        // render interactive platforms
        for(InteractivePlatform platform: interactivePlatforms) {
            platform.draw(batch);
        }

        // render robot
        robot.draw(batch, delta);

        // render enemies
        for(Enemy enemy: enemies) {
            enemy.draw(batch);
        }

        // render collectables
        for(Collectable collectable: collectables) {
            collectable.draw(batch);
        }
    }

    protected void renderAndroid() {
        if(onAndroid && !paused) {
            androidController.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("PlayScreen", "resize");
        viewport.update(width, height, true);
        hud.getHudViewport().update(width, height, true);
        if(onAndroid) {
            androidController.resize(width, height);
        }
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
        //this.dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("PlayScreen", "dispose");
        mapRenderer.dispose();
        world.dispose();
        shapeRenderer.dispose();
        hud.dispose();
        if(DEBUG_ON) {
            debugRenderer.dispose();
        }
        setToNull();
    }

    private void setToNull() {
        shakeEffect.setToNull();
        debugCamera.setToNull();
        for(Enemy enemy: enemies) {
            enemy.setToNull();
        }
        for(InteractivePlatform platform: interactivePlatforms) {
            platform.setToNull();
        }
        for(Collectable collectable: collectables) {
            collectable.setToNull();
        }
        ladderClimbHandler.setToNull();
        feedbackRenderer.setToNull();
        objectParser.setToNull();
        robot.setToNull();


        tiledMap = null;
        mapRenderer = null;
        contactManager = null;
        objectParser = null;
        layersObjectArray = null;
        shakeEffect = null;
        ladderClimbHandler = null;
        interactivePlatforms = null;
        enemies = null;
        fallingPipes = null;
        feedbackRenderer = null;
        camera = null;
        viewport = null;
        debugCamera = null;
        collectables = null;
        trampoline = null;
        tankBalls = null;
        tankBallSpawner = null;
        hud = null;
        laserHandler = null;
        punchHandler = null;
        movingSpikes = null;
        joints = null;
        jointHandler = null;
        robot = null;
        music = null;
        Gdx.app.log("PlayScreen", "Objects were set to null");
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

    public DelayedRemovalArray<InteractivePlatform> getInteractivePlatforms() {
        return interactivePlatforms;
    }

    public DelayedRemovalArray<Enemy> getEnemies() {
        return enemies;
    }

    public DelayedRemovalArray<Collectable> getCollectables() {
        return collectables;
    }

    public boolean isNewItemCollected() {
        return newItemCollected;
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

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public int getLevelID() {
        return levelID;
    }

    public DelayedRemovalArray<TankBall> getTankBalls() {
        return tankBalls;
    }

    /*public TankBallPool getTankBallPool() {
        return tankBallPool;
    }*/

    public LaserHandler getLaserHandler() {
        return laserHandler;
    }

    public PunchHandler getPunchHandler() {
        return punchHandler;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        Gdx.app.log("PlayScreen", "Paused = " + paused);
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        Gdx.app.log("PlayScreen", "Muted = " + muted);
    }

    public Music getMusic() {
        return music;
    }

    public void updateInputProcOnPauseOrResume() {
        if(paused) {
            Gdx.input.setInputProcessor(hud.getStage());
        }
        else {
            Gdx.input.setInputProcessor(onAndroid ? androidController.getStage() :
                    (robot.isOnLadder() ? ladderClimbHandler : null));
        }
        Gdx.app.log("PlayScreen", "InputProcessor = " + Gdx.input.getInputProcessor());
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public ConeLight getConeLight() {
        return coneLight;
    }

    public PointLight getPointLightHand() {
        return pointLightHand;
    }

    public PointLight getPointLightHead() {
        return pointLightHead;
    }

    public ObjectParser getObjectParser() {
        return objectParser;
    }

    public Array<PrismaticJoint> getJoints() {
        return joints;
    }

    public Array<MovingSpike> getMovingSpikes() {
        return movingSpikes;
    }

    public boolean isDamageON() {
        return damageON;
    }

    public void setDamageON(boolean damageON) {
        this.damageON = damageON;
        Gdx.app.log("PlayScreen", "damageON = " + damageON);
    }

    public MyOrthogonalTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public int getScoreOnGameEnd() {
        return scoreOnGameEnd;
    }

    public void setToMenuFromPaused(boolean toMenuFromPaused) {
        this.toMenuFromPaused = toMenuFromPaused;
        Gdx.app.log("PlayScreen", "toMenuFromPaused = " + toMenuFromPaused);
    }

    public AndroidController getAndroidController() {
        return androidController;
    }

    public boolean isOnAndroid() {
        return onAndroid;
    }

    protected void checkPauseOrResume() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.P) || androidController.isPauseClicked()) {
            setPaused(!paused);

            // update boolean for tiled animation
            mapRenderer.setMapAnimationActive(!mapRenderer.isMapAnimationActive());

            // update input processor
            updateInputProcOnPauseOrResume();

            if(paused) {
                // pause music
                music.pause();
            }
            else {
                // when resumed with 'P' key, restore selection to zero for next time game is paused (in case MENU was selected)
                hud.setSelection(0);

                // resume music
                if(!muted) {
                    music.play();
                }
            }

            // if on android, un-flag pause clicked
            if(onAndroid) {
                androidController.setPauseClicked(false);
            }
        }
    }


    protected void processGameStateInput() {
        // return to menu screen with ESC
        if(DEBUG_KEYS_ON && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            escapePressed = true;
            Gdx.app.log("PlayScreen", "escapePressed = true");
        }
        // mute
        else if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            boolean reversedMuted = !muted;
            setMuted(reversedMuted);
            // update preferences
            game.getPreferences().putBoolean("muted", reversedMuted);
            game.getPreferences().flush();

            if(muted) {
                music.pause();
            }
            else {
                music.play();
            }
        }
    }

    protected void renderDebugLines() {
        //render box2d debug rectangles
        debugRenderer.render(world, viewport.getCamera().combined);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(Enemy enemy: enemies) {
            if(enemy instanceof EnemyPathFollowingAI) {
                ((EnemyPathFollowingAI) enemy).drawAiPath();
            }
        }
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(Enemy enemy: enemies) {
            if(enemy instanceof EnemyPathFollowingAI && ((EnemyPathFollowingAI) enemy).activated) {
                ((EnemyPathFollowingAI) enemy).drawTarget();
            }
        }
        shapeRenderer.end();
    }

    protected void handleRobotDeath() {
        // first stop music
        music.stop();

        // robot died but has remaining lives
        if(checkpointData.getLives() >= 0) {
            Gdx.app.log("PlayScreen", "Player died");

            // if a new item has been collected in this session, save the file with collected items and disable saving from the hide() method
            if(newItemCollected) {
                doNotSaveInHide = true;
            }
            // finally set screen to LostLifeScreen
            dispose();
            game.setScreen(new LostLifeScreen(this));
        }
        // robot died and has no remaining lives
        else {
            Gdx.app.log("PlayScreen", "Player died, no more lives left");

            // keep track of score because it will be reset to zero
            this.scoreOnGameEnd = checkpointData.getScore();

            // reset checkpoint data
            checkpointData.setDefaultRobotData();
            checkpointData.setDefaultLevelData(levelID);

            // save game data
            FileSaver.saveCheckpointData(checkpointData);

            // so that if any item was collected during last life, they won't be saved in a file
            doNotSaveInHide = true;

            // finally set screen to GameOverScreen
            dispose();
            game.setScreen(new GameOverScreen(this));
        }
    }

    protected void returnToMenu() {
        // stop music
        music.stop();

        // set MenuScreen
        this.dispose();
        game.setScreen(new MenuScreen(game));
    }

    // debug keys for moving between levels
    protected void toggleDebugLevels() {
        // toggle damage on/off
        if(Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            setDamageON(!damageON);
        }
        // switch to level1
        else if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            Gdx.app.log("PlayScreen", "Level 1 was set from debug keys");

            // stop muisc
            music.stop();

            doNotSaveInHide = true;

            // set default data
            checkpointData.setDefaultRobotData();
            checkpointData.setDefaultLevelData(1);

            // save game data
            FileSaver.saveCheckpointData(checkpointData);

            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                boolean deleted = false;
                for(int i = 0; i < 30; i++) {
                    deleted = FileSaver.getCollectedItemsFile().delete();
                    System.out.println(i);
                    if(deleted) break;
                    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                    System.gc();
                }
                Gdx.app.log("PlayScreen", "collectedItems.json deleted = " + deleted);
            }

            // start level1
            dispose();
            game.setScreen(new ScreenLevel1(game));
        }
        // switch to level 2
        else if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            Gdx.app.log("PlayScreen", "Level 2 was set from debug keys");
            // stop muisc
            music.stop();

            doNotSaveInHide = true;

            // set default data
            checkpointData.setDefaultRobotData();
            checkpointData.setDefaultLevelData(2);

            // save game data
            FileSaver.saveCheckpointData(checkpointData);

            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                boolean deleted = false;
                for(int i = 0; i < 30; i++) {
                    deleted = FileSaver.getCollectedItemsFile().delete();
                    System.out.println(i);
                    if(deleted) break;
                    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                    System.gc();
                }
                Gdx.app.log("PlayScreen", "collectedItems.json deleted = " + deleted);
            }

            // start level2
            dispose();
            game.setScreen(new ScreenLevel2(game));
        }

        // switch to level3
        else if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            Gdx.app.log("PlayScreen", "Level 3 was set from debug keys");
            // stop muisc
            music.stop();

            doNotSaveInHide = true;

            // set default data
            checkpointData.setDefaultRobotData();
            checkpointData.setDefaultLevelData(3);
            checkpointData.setLevelID(3); // since setting the default data in level 3, sets the level to 2
            checkpointData.setSpawnLocation(SPAWN_LOCATION_L3);

            // save game data
            FileSaver.saveCheckpointData(checkpointData);


            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                boolean deleted = false;
                for(int i = 0; i < 30; i++) {
                    deleted = FileSaver.getCollectedItemsFile().delete();
                    System.out.println(i);
                    if(deleted) break;
                    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                    System.gc();
                }
                Gdx.app.log("PlayScreen", "collectedItems.json deleted = " + deleted);
            }

            // start level3
            dispose();
            game.setScreen(new ScreenLevel3(game));
        }
    }
}
