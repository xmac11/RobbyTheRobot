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
        this.mapLayers = new int[] {1, 2, 3, 4, 5, 6, 8, 9, 10};

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

        //// Debug keys for checkpoints ////
        //if(DEBUG_ON)
        this.toggleDebugCheckpoints();

        // handle checkpoints
        this.handleCheckpoints();

        //        System.out.println("Interactive platforms: " + interactivePlatforms.size);
        //        System.out.println("Number of enemies: " + enemies.size);

    }

    @Override
    public void render(float delta) {

        // check if game was paused
        super.processGameStateInput();

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

        // render common elements (interactive platforms, robot, enemies, collectables, feedbackRenderer)
        super.commonRendering(delta);

        // render falling pipes
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.draw(game.getBatch());
        }

        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        hud.draw(game.getBatch());
        game.getBatch().end();

        //        System.out.println("render2: " + game.getBatch().renderCalls);

        if(DEBUG_ON) {
            // render ai paths
            super.renderDebugLines();
        }

        // finally, check if robot is dead
        super.checkIfDead();

        /*// set dead bodies to null
        for(Enemy enemy: enemies) {
            if(enemy.getBody().getFixtureList().size == 0 && !feedbackRenderer.getDamageFromHitToDraw().containsKey(enemy)
            && !feedbackRenderer.getPointsForEnemyToDraw().containsKey(enemy)) {
                enemy.setBodyToNull();
                Gdx.app.log("ScreenLevel1", "Enemy body set to null");
            }
        }

        for(Collectable collectable: collectables) {
            if(collectable.getBody().getFixtureList().size == 0 && !feedbackRenderer.getItemPointsToDraw().containsKey(collectable)) {
                collectable.setBodyToNull();
                Gdx.app.log("ScreenLevel1", "Collectable body set to null");
            }
        }

        for(InteractivePlatform interactivePlatform: interactivePlatforms) {
            if(interactivePlatform.getBody().getFixtureList().size == 0) {
                interactivePlatform.setBodyToNull();
            }
        }*/
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
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
            Gdx.app.log("ScreenLevel1", "Checkpoints deleted");
            FileSaver.getCheckpointFile().delete();
            checkpointDataDeleted = true;

            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                FileSaver.getCollectedItemsFile().delete();
            }
            else {
                newItemCollected = false;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            Gdx.app.log("ScreenLevel1", "First checkpoint set");
            checkpointData.setSpawnLocation(FIRST_CHECKPOINT_LOCATION_L1);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(false);
            checkpointData.setThirdCheckpointActivated(false);
            FileSaver.saveCheckpointData(checkpointData);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            Gdx.app.log("ScreenLevel1", "Second checkpoint set");
            checkpointData.setSpawnLocation(SECOND_CHECKPOINT_LOCATION_L1);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(true);
            checkpointData.setThirdCheckpointActivated(false);
            FileSaver.saveCheckpointData(checkpointData);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            Gdx.app.log("ScreenLevel1", "Third checkpoint set");
            checkpointData.setSpawnLocation(THIRD_CHECKPOINT_LOCATION_L1);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(true);
            checkpointData.setThirdCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
        }
    }

}