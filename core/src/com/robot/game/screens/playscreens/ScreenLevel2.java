package com.robot.game.screens.playscreens;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.RobotGame;
import com.robot.game.camera.Parallax;
import com.robot.game.interactiveObjects.spikes.MovingSpike;
import com.robot.game.interactiveObjects.tankBalls.TankBall;
import com.robot.game.interactiveObjects.tankBalls.TankBallPool;
import com.robot.game.interactiveObjects.tankBalls.TankBallSpawner;
import com.robot.game.interactiveObjects.spikes.JointHandler;
import com.robot.game.util.checkpoints.FileSaver;
import com.robot.game.util.raycast.LaserHandler;
import com.robot.game.util.raycast.PunchHandler;

import static com.robot.game.util.constants.Constants.*;

public class ScreenLevel2 extends PlayScreen {

    private Array<Parallax> parallaxWaters = new Array<>();
    private Sprite instructions;

    public ScreenLevel2(RobotGame game) {
        super(game, game.getAssets().tiledMapAssets.tiledMapLevel2, 2);
    }

    @Override
    public void show() {
        Gdx.app.log("ScreenLevel2", "show");

        // instructions
        if(!onAndroid) {
            this.instructions = new Sprite(assets.mainMenuAssets.instructions);
            instructions.setSize(instructions.getWidth() / 2 / PPM, instructions.getHeight() / 2 / PPM);
            instructions.setPosition(32 / PPM, 64 / PPM);
        }

        // create tiled objects
        super.layersObjectArray = new Array<>();
        layersObjectArray.add(tiledMap.getLayers().get(GROUND_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(WALL_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(LADDER_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(FISH_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(MONSTER_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(SNAKE_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(SPIKE_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(COLLECTABLE_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get("Chase sensor obj").getObjects());

        // creates objectParser, interactivePlatforms, enemies and collectables
        super.createCommonObjectLayers();

        // create trampoline
        super.trampoline = objectParser.getTrampoline();

        // create laser handler
        super.laserHandler = new LaserHandler(this);

        // create punch handler
        super.punchHandler = new PunchHandler(this);

        // create tank balls, pool and spawner
        super.tankBalls = new DelayedRemovalArray<>();
        super.tankBallPool = new TankBallPool(this);
        super.tankBallSpawner = new TankBallSpawner(this);

        // create ray handler (box2d lights)
        super.rayHandler = new RayHandler(world);
        rayHandler.setShadows(false);
        super.pointLight = new PointLight(rayHandler, 10, Color.CYAN, 48 / PPM, 0, 0);

        // create parallax water
        this.parallaxWaters.add(new Parallax(this, assets.parallaxAssets.waterTexture,
                1f, 800, 0, 80, 48, true, false));
        this.parallaxWaters.add(new Parallax(this, assets.parallaxAssets.waterTexture,
                1f, 1088, 0, 80, 48, true, false));
        this.parallaxWaters.add(new Parallax(this, assets.parallaxAssets.waterTextureBig,
                0, 2672, 0 , 1824, 48, false, false));

        // create moving spikes - prismatic joints and jointHandler
        super.movingSpikes = objectParser.getMovingSpikes();
        super.joints = objectParser.getJoints();
        super.jointHandler = new JointHandler(this);

        // music
        super.music = assets.musicAssets.level2Music;
        music.setLooping(true);
        if(!muted) {
            music.play();
        }
    }

    protected void update(float delta) {
        // update common elements
        super.commonUpdates(delta);

        for(Parallax parallax: parallaxWaters) {
            parallax.update(delta);
        }

        // update tank ball spawner
        tankBallSpawner.update(delta);
        //System.out.println("active " + tankBalls.size + ", free " + tankBallPool.getFree());

        // update tank balls
        for(TankBall tankBall: tankBalls) {
            tankBall.update(delta);
        }

        // update prismatic joints of moving spikes
        jointHandler.update(delta);

        // check for mute or ESC
        super.processGameStateInput();


        // debug keys for moving between checkpoints of level 2
        if(DEBUG_KEYS_ON) {
            this.toggleDebugCheckpointsL2();
        }

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

        // render map
        mapRenderer.render();

        batch.begin();

        // render instructions
        if(!onAndroid) {
            instructions.draw(batch);
        }

        // render common elements (interactive platforms, robot, enemies, collectables, feedbackRenderer)
        super.commonRendering(delta);

        // render parallax water (render after enemy so that fish are behind)
        for(Parallax parallax: parallaxWaters) {
            parallax.draw(batch);
        }

        // render trampoline
        trampoline.draw(batch);

        // render fireballs
        for(TankBall tankBall: tankBalls) {
            tankBall.draw(batch);
        }

        // render moving spikes (joint handler renders the stick)
        jointHandler.draw(batch);
        for(MovingSpike movingSpike: movingSpikes) {
            movingSpike.draw(batch);
        }

        // render feedback
        // This has to be done within the game's viewport and not the hud's, since the position of the bodies are needed.
        feedbackRenderer.draw(batch, delta);
        batch.end();

        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        hud.draw(batch);

        // render any laser shot
        laserHandler.render(batch, shapeRenderer);

        // render android controllers
        renderAndroid();

        if(DEBUG_ON) {
            // render punch lines
            punchHandler.render(shapeRenderer);
            // render box2d shapes and ai paths
            super.renderDebugLines();
        }

        // finally, check if robot is dead, level completed or game exited
        if(escapePressed || toMenuFromPaused) {
            Gdx.app.log("ScreenLevel2","Menu screen was set by ESC or PAUSE PANEL");
            super.returnToMenu();
        }
        else if(robot.isDead()) {
            super.handleRobotDeath();
        }
        else {
            this.checkIfLevelComplete();
        }

        // debug keys for moving between levels
        if(DEBUG_KEYS_ON) {
            super.toggleDebugLevels();
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("ScreenLevel2", "dispose");
        rayHandler.dispose();
        setToNull();
        super.dispose();
    }

    private void setToNull() {
        for(Parallax parallax: parallaxWaters) {
            parallax.setToNull();
        }
        jointHandler.setToNull();
        for(MovingSpike movingSpike: movingSpikes) {
            movingSpike.setToNull();
        }
        for(TankBall tankBall: tankBalls) {
            tankBall.setToNull();
        }
        tankBallSpawner.setToNull();
        laserHandler.setToNull();
        punchHandler.setToNull();
        parallaxWaters = null;
        instructions = null;
        Gdx.app.log("ScreenLevel2", "Objects were set to null");
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
        if( Math.abs( (robot.getBody().getPosition().x - FIRST_CHECKPOINT_LOCATION_L2.x) * PPM )  <= CHECKPOINT_TOLERANCE
                && Math.abs( (robot.getBody().getPosition().y - FIRST_CHECKPOINT_LOCATION_L2.y) * PPM )  <= CHECKPOINT_TOLERANCE) {

            Gdx.app.log("ScreenLevel2","First checkpoint activated!");
            checkpointData.setSpawnLocation(FIRST_CHECKPOINT_LOCATION_L2);
            checkpointData.setFirstCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
        }
    }

    private void checkSecondCheckpoint() {
        if( Math.abs( (robot.getBody().getPosition().x - SECOND_CHECKPOINT_LOCATION_L2.x) * PPM )  <= CHECKPOINT_TOLERANCE
                && Math.abs( (robot.getBody().getPosition().y - SECOND_CHECKPOINT_LOCATION_L2.y) * PPM )  <= CHECKPOINT_TOLERANCE) {

            Gdx.app.log("ScreenLevel2","Second checkpoint activated!");
            checkpointData.setSpawnLocation(SECOND_CHECKPOINT_LOCATION_L2);
            checkpointData.setSecondCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
        }
    }

    private void checkThirdCheckpoint() {
        if( Math.abs( (robot.getBody().getPosition().x - THIRD_CHECKPOINT_LOCATION_L2.x) * PPM )  <= CHECKPOINT_TOLERANCE
                && Math.abs( (robot.getBody().getPosition().y - THIRD_CHECKPOINT_LOCATION_L2.y) * PPM )  <= CHECKPOINT_TOLERANCE) {

            Gdx.app.log("ScreenLevel2","Third checkpoint activated!");
            checkpointData.setSpawnLocation(THIRD_CHECKPOINT_LOCATION_L2);
            checkpointData.setThirdCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
        }
    }

    // Debug keys for checkpoints of level 2

    private void toggleDebugCheckpointsL2() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            Gdx.app.log("ScreenLevel2", "Checkpoints reset");
            // reset level data
            checkpointData.setDefaultLevelData(levelID);
            // save data
            FileSaver.saveCheckpointData(checkpointData);

            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                boolean deleted = FileSaver.getCollectedItemsFile().delete();
                Gdx.app.log("ScreenLevel2", "collectedItems.json deleted = " + deleted);
            }
            else {
                newItemCollected = false;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Gdx.app.log("ScreenLevel2", "First checkpoint set");
            checkpointData.setSpawnLocation(FIRST_CHECKPOINT_LOCATION_L2);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(false);
            checkpointData.setThirdCheckpointActivated(false);
            FileSaver.saveCheckpointData(checkpointData);
            // transfer body
            robot.getBody().setTransform(FIRST_CHECKPOINT_LOCATION_L2, 0);
            robot.getBody().setAwake(true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Gdx.app.log("ScreenLevel2", "Second checkpoint set");
            checkpointData.setSpawnLocation(SECOND_CHECKPOINT_LOCATION_L2);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(true);
            checkpointData.setThirdCheckpointActivated(false);
            FileSaver.saveCheckpointData(checkpointData);
            // transfer body
            robot.getBody().setTransform(SECOND_CHECKPOINT_LOCATION_L2, 0);
            robot.getBody().setAwake(true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            Gdx.app.log("ScreenLevel2", "Third checkpoint set");
            checkpointData.setSpawnLocation(THIRD_CHECKPOINT_LOCATION_L2);
            checkpointData.setFirstCheckpointActivated(true);
            checkpointData.setSecondCheckpointActivated(true);
            checkpointData.setThirdCheckpointActivated(true);
            FileSaver.saveCheckpointData(checkpointData);
            // transfer body
            robot.getBody().setTransform(THIRD_CHECKPOINT_LOCATION_L2, 0);
            robot.getBody().setAwake(true);
        }
    }

    @Override
    public void checkIfLevelComplete() {
        if(Math.abs( robot.getBody().getPosition().x * PPM - 5344)  <= 16
                && Math.abs( robot.getBody().getPosition().y * PPM - 80 )  <= 16) {

            Gdx.app.log("ScreenLevel2", "Level complete!!!");

            doNotSaveInHide = true;

            // stop music
            music.stop();

            /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
             * reset their spawning in the corresponding level and delete the file */
            if(FileSaver.getCollectedItemsFile().exists()) {
                FileSaver.resetSpawningOfCollectables(levelID);
                boolean deleted = FileSaver.getCollectedItemsFile().delete();
                Gdx.app.log("ScreenLevel2", "collectedItems.json deleted = " + deleted);
            }

            // set levelID
            checkpointData.setLevelID(3);

            // set corresponding spawn location of level3
            checkpointData.setSpawnLocation(SPAWN_LOCATION_L3);

            // set all checkpoints of new level to false
            checkpointData.setCheckpointsToFalse();

            // save game data
            FileSaver.saveCheckpointData(checkpointData);

            // start level3
            this.dispose();
            game.setScreen(new ScreenLevel3(game));
        }

    }
}
