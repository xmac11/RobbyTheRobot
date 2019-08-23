package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.RobotGame;
import com.robot.game.camera.Parallax;
import com.robot.game.interactiveObjects.fallingPipes.FallingPipe;
import com.robot.game.interactiveObjects.fallingPipes.FallingPipeSpawner;
import com.robot.game.util.checkpoints.FileSaver;

import static com.robot.game.util.Constants.*;

public class ScreenLevel1 extends PlayScreen {

    private int[] backgroundWallLayer;
    private int[] mapLayers;

    // earthquake and falling pipes
    private FallingPipeSpawner fallingPipeSpawner;

    // parallax scrolling
    private Parallax parallaxBackground;
    private Parallax parallaxBarrels;

    public ScreenLevel1(RobotGame game) {
        super(game, game.getAssets().tiledMapAssets.tiledMapLevel1, 1);
    }

    @Override
    public void show() {
        Gdx.app.log("ScreenLevel1", "show");

        // create tiled objects
        super.layersObjectArray = new Array<>();
        layersObjectArray.add(tiledMap.getLayers().get(GROUND_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(LADDER_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(BAT_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(CRAB_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(SPIKE_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(COLLECTABLE_OBJECT).getObjects());

        this.backgroundWallLayer = new int[] {0};
        this.mapLayers = new int[] {1, 2, 3, 4, 5, 6, 8, 9};

        // creates objectParser, interactivePlatforms, enemies and collectables
        super.createCommonObjectLayers();

        // create falling pipes and cache 5 pipes
        super.fallingPipes = new DelayedRemovalArray<>();
        for(int i = 0; i < 5; i++) {
            fallingPipes.add(new FallingPipe(this, true));
        }
        // create falling pipe handler
        this.fallingPipeSpawner = new FallingPipeSpawner(this);

        // create parallax
        this.parallaxBackground = new Parallax(this, assets.parallaxAssets.backgroundTexture, 0.5f, 0, 192, mapWidth, 260, false, true);
        this.parallaxBarrels = new Parallax(this, assets.parallaxAssets.barrelsTexture, 1.0f, 0, 0, mapWidth, 75, true, true);

        // music
        super.music = assets.musicAssets.level1Music;
        music.setLooping(true);
        if(!muted) {
            music.play();
        }

        //System.out.println(tiledMapLevel1.getLayers().get(GROUND_OBJECT).getObjects().get(250)); // error
        System.out.println("Game started, newly collected items: " + collectableHandler.getCollectedItems().size()); // this should be zero when the game starts
    }

    private void update(float delta) {
        // update common elements
        super.commonUpdates(delta);

        // update parallax
        parallaxBackground.update(delta);
        parallaxBarrels.update(delta);

        // update falling pipes
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.update(delta);
        }

        // handle earthquake
        fallingPipeSpawner.update(delta);

        // check for mute or ESC
        super.processGameStateInput();

        //// Debug keys for checkpoints ////

        //if(DEBUG_ON)
        this.toggleDebugCheckpoints();

        // handle checkpoints
        this.handleCheckpoints();
    }

    @Override
    public void render(float delta) {

        // check if game was paused/resumed
        super.checkPauseOrResume();

        // if game is not paused, perform all necessary updates
        if(!paused) {
            this.update(delta);
        }

        // update view
        super.updateViews(delta);

        // clear game screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render wall
        mapRenderer.render(backgroundWallLayer);

        // FIRST BATCH
        // render background
        batch.disableBlending();
        batch.begin();
        parallaxBackground.draw(batch);
        batch.end();
        //        System.out.println("render1: " + batch.renderCalls);

        // render map
        mapRenderer.render(mapLayers);

        // SECOND BATCH
        batch.enableBlending();
        batch.begin();

        // render foreground (waves and barrels)
        parallaxBarrels.draw(batch);

        // render common elements (interactive platforms, robot, enemies, collectables, feedbackRenderer)
        super.commonRendering(delta);

        // render falling pipes
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.draw(batch);
        }

        // render feedback
        // This has to be done within the game's viewport and not the hud's, since the position of the bodies are needed.
        feedbackRenderer.draw(batch, delta);
        batch.end();

        //        System.out.println("render2: " + batch.renderCalls);

        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        hud.draw(batch);

        if(DEBUG_ON) {
            // render box2d shapes and ai paths
            super.renderDebugLines();
        }

        // finally, check if robot is dead, level completed or game exited
        if(escapePressed || toMenuFromPaused) {
            Gdx.app.log("ScreenLevel1","Menu screen was set by ESC or PAUSE PANEL");
            super.returnToMenu();
        }
        else if(robot.isDead()) {
            super.handleRobotDeath();
        }
        else {
            this.checkIfLevelComplete();
        }

        //if(DEBUG_ON)
        super.toggleDebugLevels();
    }

    @Override
    public void dispose() {
        Gdx.app.log("ScreenLevel1", "dispose");
        setToNull();
        super.dispose();
    }

    private void setToNull() {
        parallaxBackground.setToNull();
        parallaxBarrels.setToNull();
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.setToNull();
        }
        fallingPipeSpawner.setToNull();

        backgroundWallLayer = null;
        mapLayers = null;
        fallingPipeSpawner = null;
        parallaxBackground = null;
        parallaxBarrels = null;

        Gdx.app.log("ScreenLevel1", "Objects were set to null");
    }

    private void handleCheckpoints() {
        // First checkpoint
        if(!checkpointData.isFirstCheckpointActivated()) {
            this.checkFirstCheckpoint();
        }
        // Second checkpoint
        else if(!checkpointData.isSecondCheckpointActivated()) {
            this.checkSecondCheckpoint();
        }
        else if(!checkpointData.isThirdCheckpointActivated()) {
            this.checkThirdCheckpoint();
        }
    }

    // Checkpoints
    private void checkFirstCheckpoint() {
        if( Math.abs( (robot.getBody().getPosition().x - FIRST_CHECKPOINT_LOCATION_L1.x) * PPM )  <= CHECKPOINT_TOLERANCE) {
            Gdx.app.log("ScreenLevel1","First checkpoint activated!");
            checkpointData.setSpawnLocation(FIRST_CHECKPOINT_LOCATION_L1);
            checkpointData.setFirstCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
        }
    }

    private void checkSecondCheckpoint() {
        if( Math.abs( (robot.getBody().getPosition().x - SECOND_CHECKPOINT_LOCATION_L1.x) * PPM )  <= CHECKPOINT_TOLERANCE
                && Math.abs( (robot.getBody().getPosition().y - SECOND_CHECKPOINT_LOCATION_L1.y) * PPM )  <= CHECKPOINT_TOLERANCE) {

            Gdx.app.log("ScreenLevel1","Second checkpoint activated!");
            checkpointData.setSpawnLocation(SECOND_CHECKPOINT_LOCATION_L1);
            checkpointData.setSecondCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
        }
    }

    private void checkThirdCheckpoint() {
        if( Math.abs( (robot.getBody().getPosition().x - THIRD_CHECKPOINT_LOCATION_L1.x - 80 / PPM) * PPM )  <= CHECKPOINT_TOLERANCE) {
            Gdx.app.log("ScreenLevel1","Third checkpoint activated!");
            checkpointData.setSpawnLocation(THIRD_CHECKPOINT_LOCATION_L1);
            checkpointData.setThirdCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
        }
    }

    // Debug keys for checkpoints

    private void toggleDebugCheckpoints() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            Gdx.app.log("ScreenLevel1", "Checkpoints reset");
            // reset level data
            checkpointData.setDefaultLevelData(levelID);
            // save data
            FileSaver.saveCheckpointData(checkpointData);

            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                boolean deleted = FileSaver.getCollectedItemsFile().delete();
                Gdx.app.log("ScreenLevel1", "collectedItems.json deleted = " + deleted);
            }
            else {
                newItemCollected = false;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("ScreenLevel1", "First checkpoint set");
            checkpointData.setSpawnLocation(FIRST_CHECKPOINT_LOCATION_L1);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(false);
            checkpointData.setThirdCheckpointActivated(false);
            FileSaver.saveCheckpointData(checkpointData);
            // transfer body
            robot.getBody().setTransform(FIRST_CHECKPOINT_LOCATION_L1, 0);
            robot.getBody().setAwake(true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("ScreenLevel1", "Second checkpoint set");
            checkpointData.setSpawnLocation(SECOND_CHECKPOINT_LOCATION_L1);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(true);
            checkpointData.setThirdCheckpointActivated(false);
            FileSaver.saveCheckpointData(checkpointData);
            // transfer body
            robot.getBody().setTransform(SECOND_CHECKPOINT_LOCATION_L1, 0);
            robot.getBody().setAwake(true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("ScreenLevel1", "Third checkpoint set");
            checkpointData.setSpawnLocation(THIRD_CHECKPOINT_LOCATION_L1);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(true);
            checkpointData.setThirdCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
            // transfer body
            robot.getBody().setTransform(THIRD_CHECKPOINT_LOCATION_L1, 0);
            robot.getBody().setAwake(true);
        }
    }

    @Override
    public void checkIfLevelComplete() {
        if(Math.abs( robot.getBody().getPosition().x * PPM - 6880)  <= 16
                && Math.abs( robot.getBody().getPosition().y * PPM - 344 )  <= 16) {

            Gdx.app.log("ScreenLevel1", "Level complete!!!");

            doNotSaveInHide = true;

            // stop music
            music.stop();

            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                boolean deleted = FileSaver.getCollectedItemsFile().delete();
                System.out.println(deleted + "!!!!!!!!!");
                Gdx.app.log("ScreenLevel1", "collectedItems.json deleted = " + deleted);
            }

            // set levelID
            checkpointData.setLevelID(2);

            // set corresponding spawn location of level2
            checkpointData.setSpawnLocation(SPAWN_LOCATION_L2);

            // set number of lives to 3
            checkpointData.setLives(3);

            // set all checkpoints of new level to false
            checkpointData.setCheckpointsToFalse();

            // save game data
            FileSaver.saveCheckpointData(checkpointData);

            // start level2
            this.dispose();
            game.setScreen(new ScreenLevel2(game));
        }
    }

}