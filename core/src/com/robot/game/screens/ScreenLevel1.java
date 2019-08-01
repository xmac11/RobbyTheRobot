package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.RobotGame;
import com.robot.game.camera.Parallax;
import com.robot.game.entities.Enemy;
import com.robot.game.interactiveObjects.FallingPipe;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.interactiveObjects.platforms.InteractivePlatform;

import static com.robot.game.util.Constants.*;

public class ScreenLevel1 extends PlayScreen {

    private int[] backgroundWallLayer;
    private int[] mapLayers;

    // earthquake
    private boolean earthquakeHappened;
    private boolean pipesStartedFalling;
    private boolean pipesDisabled;
    private float pipeStartTime;
    private float pipeElapsed;

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

        // create parallax
        this.parallaxBackground = new Parallax(this, assets.parallaxAssets.backgroundTexture, 0.5f, 192, 260, false);
        this.parallaxBarrels = new Parallax(this, assets.parallaxAssets.barrelsTexture, 1.0f, 0, 75, true);

        //System.out.println(tiledMapLevel1.getLayers().get(GROUND_OBJECT).getObjects().get(250)); // error
        System.out.println("Game started, newly collected items: " + collectableHandler.getCollectedItems().size()); // this should be zero when the game starts
    }

    private void update(float delta) {
        // update common elements
        super.commonUpdates(delta);

        // handle earthquake
        handleEarthquake();

        // update falling pipes
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.update(delta);
        }

        // update view
        super.updateViews(delta);

        //        System.out.println("Interactive platforms: " + interactivePlatforms.size);
        //        System.out.println("Number of enemies: " + enemies.size);

    }

    @Override
    public void render(float delta) {
        // first perform all necessary updates
        this.update(delta);

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
        super.checkIfDead();
    }


    private void handleEarthquake() {
        if(!earthquakeHappened && !pipesStartedFalling && !pipesDisabled)
            checkForEarthquake();

        if(earthquakeHappened) {
            // activate cached pipes
            for(FallingPipe fallingPipe : fallingPipes) {
                fallingPipe.getBody().setAwake(true);
                fallingPipe.getBody().setGravityScale(1);
            }
            this.pipeStartTime = TimeUtils.nanoTime() + 3 / MathUtils.nanoToSec; //add extra 3 seconds timeout initially
            earthquakeHappened = false;
            pipesStartedFalling = true;
        }

        if(pipesStartedFalling && !pipesDisabled) {
            if(checkDisablingPipes())
                this.pipesDisabled = true;

            if(!pipesDisabled && shouldSpawnPipe()) {
                // follow up earthquakes with probability 45%
                if(MathUtils.random() > 0.55f)
                        shakeEffect.shake(EARTH_SHAKE_INTENSITY, EARTH_SHAKE_TIME / 10);
                fallingPipes.add(new FallingPipe(this, false));
            }
        }
    }

    private void checkForEarthquake() {
        // if robot is in the shake area and the shake is not already active, start it
        if(Math.abs(robot.getBody().getPosition().x * PPM - PIPES_START_X) <= 48) {
            Gdx.app.log("ScreenLevel1", "Earthquake activated");
            shakeEffect.shake(EARTH_SHAKE_INTENSITY, EARTH_SHAKE_TIME);
            earthquakeHappened = true;
        }
    }

    private boolean checkDisablingPipes() {
        return robot.getBody().getPosition().x > PIPES_END_X / PPM;
    }


    public boolean shouldSpawnPipe() {
        if(pipeElapsed >= PIPES_SPAWNING_PERIOD) {
            this.pipeStartTime = TimeUtils.nanoTime();
            this.pipeElapsed = 0;
            return true;
        }
        else  {
            this.pipeElapsed = (TimeUtils.nanoTime() - pipeStartTime) * MathUtils.nanoToSec;
            return false;
        }
    }

}