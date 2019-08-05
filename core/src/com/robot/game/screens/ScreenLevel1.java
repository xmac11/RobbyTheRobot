package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.RobotGame;
import com.robot.game.camera.Parallax;
import com.robot.game.interactiveObjects.fallingPipes.FallingPipe;
import com.robot.game.interactiveObjects.fallingPipes.FallingPipePool;
import com.robot.game.interactiveObjects.fallingPipes.FallingPipeSpawner;

import static com.robot.game.util.Constants.*;

public class ScreenLevel1 extends PlayScreen {

    private int[] backgroundWallLayer;
    private int[] mapLayers;

    // earthquake and falling pipes

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
        // create falling pipe pool and spawner
        super.fallingPipePool = new FallingPipePool(this);
        super.fallingPipeSpawner = new FallingPipeSpawner(this);

        // create parallax
        this.parallaxBackground = new Parallax(this, assets.parallaxAssets.backgroundTexture, 0.5f, 192, 260, false);
        this.parallaxBarrels = new Parallax(this, assets.parallaxAssets.barrelsTexture, 1.0f, 0, 75, true);

        //System.out.println(tiledMapLevel1.getLayers().get(GROUND_OBJECT).getObjects().get(250)); // error
        System.out.println("Game started, newly collected items: " + collectableHandler.getCollectedItems().size()); // this should be zero when the game starts
    }

    private void update(float delta) {
        // update common elements
        super.commonUpdates(delta);

        // update parallax
        parallaxBackground.update(delta);
        parallaxBarrels.update(delta);

        // handle earthquake
        fallingPipeSpawner.update(delta);
        //System.out.println("active " + fallingPipes.size + ", free " + fallingPipePool.getFree());

        // update falling pipes
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.update(delta);
        }

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

        laserHandler.render(game.getBatch(), shapeRenderer);

        super.renderDebugLines();

        // finally, check if robot is dead
        super.checkIfDead();
    }

}