package com.robot.game.screens.playscreens;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.robot.game.RobotGame;
import com.robot.game.screens.GameCompletedScreen;
import com.robot.game.util.constants.Enums;
import com.robot.game.checkpoints.FileSaver;
import com.robot.game.raycast.LaserHandler;
import com.robot.game.raycast.PunchHandler;

import static com.robot.game.util.constants.Constants.*;

public class ScreenLevel3 extends PlayScreen {

    public ScreenLevel3(RobotGame game) {
        super(game, game.getAssets().tiledMapAssets.tiledMapLevel3, 3);
    }

    @Override
    public void show() {
        Gdx.app.log("ScreenLevel3", "show");

        // create tiled objects
        super.layersObjectArray = new Array<>();
        layersObjectArray.add(tiledMap.getLayers().get(GROUND_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(LADDER_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(MONSTER_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(SNAKE_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(COLLECTABLE_OBJECT).getObjects());

        // creates objectParser, interactivePlatforms, enemies and collectables
        super.createCommonObjectLayers();

        // create trampoline
        super.trampoline = objectParser.getTrampoline();

        // create laser handler
        super.laserHandler = new LaserHandler(this);

        // create punch handler
        super.punchHandler = new PunchHandler(this);

        // create ray handler (box2d lights)
        super.rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(1f);

        // create point light (laser)
        super.pointLight = new PointLight(rayHandler, 10, Color.CYAN, 48 / PPM, 0, 0);

        // create second ray handler for torch
        super.rayHandlerTorch = new RayHandler(world);
        rayHandlerTorch.setAmbientLight(0f);

        // create cone light (torch)
        super.coneLight = new ConeLight(rayHandlerTorch, 20, new Color(247f / 255, 242f / 255, 98f / 255, 1),
                288 / PPM, 0 , 0, 0, 50f / 2);
        coneLight.setSoftnessLength(0);
        coneLight.setContactFilter(TORCH_LIGHT_CATEGORY, (short) 0, NOTHING_MASK);

        // point light (hand)
        super.pointLightHand = new PointLight(rayHandlerTorch, 10, Color.GREEN, 16 / PPM, 0 , 0);
        pointLightHand.setContactFilter(TORCH_LIGHT_CATEGORY, (short) 0, NOTHING_MASK);

        // point light (head) -- if the robot doesn't have the torch, it is placed on the torch
        super.pointLightHead = new PointLight(rayHandlerTorch, 10, new Color(247f / 255, 242f / 255, 98f / 255, 1),
                robot.hasTorch() ? 16 / PPM : 32 / PPM, 264 / PPM , 40 / PPM);
        pointLightHead.setContactFilter(TORCH_LIGHT_CATEGORY, (short) 0, NOTHING_MASK);

        if(!robot.hasTorch()) {
            coneLight.setActive(false);
        }

        // music
        super.music = assets.musicAssets.level3Music;
        music.setLooping(true);
        if(!muted) {
            music.play();
        }
    }

    public void update(float delta) {
        // update common elements
        super.commonUpdates(delta);

        // update ray handler for torch
        rayHandlerTorch.update();
        rayHandlerTorch.setCombinedMatrix(camera);

        if(robot.hasTorch()) {
            // set position of cone light
            if(robot.getFacing() == Enums.Facing.RIGHT) {
                coneLight.setPosition(robot.getBody().getPosition().sub(ROBOT_BODY_WIDTH / 4 / PPM, 0));
                coneLight.setDirection(0);
            }
            else if(robot.getFacing() == Enums.Facing.LEFT) {
                coneLight.setPosition(robot.getBody().getPosition().add(ROBOT_BODY_WIDTH / 4 / PPM, 0));
                coneLight.setDirection(180);
            }

            // set position of head light
            pointLightHead.setPosition(robot.getBody().getPosition().add(0, ROBOT_BODY_HEIGHT / 3 / PPM));
        }
        // set position of hand light (gun)
        pointLightHand.setPosition(robot.getBody().getPosition().sub(0, ROBOT_BODY_HEIGHT / 4 / PPM));

        // check for mute or ESC
        super.processGameStateInput();
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

        mapRenderer.render();

        batch.begin();

        // render common elements (interactive platforms, robot, enemies, collectables, feedbackRenderer)
        super.commonRendering(delta);

        // render trampoline
        trampoline.draw(batch);

        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        //hud.draw(batch);
        batch.end();

        // render torch
        rayHandlerTorch.render();

        // render any laser shot
        laserHandler.render(batch, shapeRenderer);

        batch.begin();
        // render feedback
        // This has to be done within the game's viewport and not the hud's, since the position of the bodies are needed.
        feedbackRenderer.draw(batch, delta);
        batch.end();

        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        hud.draw(batch);

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
            Gdx.app.log("ScreenLevel3","Menu screen was set by ESC or PAUSE PANEL");
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
        Gdx.app.log("ScreenLevel3", "dispose");
        rayHandler.dispose();
        rayHandlerTorch.dispose();
        setToNull();
        super.dispose();
    }

    @Override
    public void checkIfLevelComplete() {
        if(Math.abs( robot.getBody().getPosition().x * PPM - 2112)  <= 16
                && Math.abs( robot.getBody().getPosition().y * PPM - 64 )  <= 16) {

            Gdx.app.log("ScreenLevel3", "Level complete!!!");

            doNotSaveInHide = true;

            // stop music
            music.stop();

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
                Gdx.app.log("ScreenLevel3", "collectedItems.json deleted = " + deleted);
            }

            // keep track of score because it will be reset to zero
            super.scoreOnGameEnd = checkpointData.getScore();

            // reset data
            checkpointData.setDefaultRobotData();
            checkpointData.setDefaultLevelData(3);
            checkpointData.setGameCompleted(true);

            // save game data
            FileSaver.saveCheckpointData(checkpointData);

            // load game completed assets
            game.getAssets().loadGameCompletedAssets();

            // set GameCompletedScreen
            this.dispose();
            game.setScreen(new GameCompletedScreen(this));
        }
    }

    private void setToNull() {
        laserHandler.setToNull();
        punchHandler.setToNull();
    }
}
