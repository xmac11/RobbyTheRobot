package com.robot.game.screens;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.RobotGame;
import com.robot.game.camera.Parallax;
import com.robot.game.interactiveObjects.tankBalls.TankBall;
import com.robot.game.interactiveObjects.tankBalls.TankBallPool;
import com.robot.game.interactiveObjects.tankBalls.TankBallSpawner;
import com.robot.game.util.raycast.LaserHandler;
import com.robot.game.util.raycast.PunchHandler;

import static com.robot.game.util.Constants.*;

public class ScreenLevel2 extends PlayScreen {

    private Array<Parallax> parallaxWaters = new Array<>();

    public ScreenLevel2(RobotGame game) {
        super(game, game.getAssets().tiledMapAssets.tiledMapLevel2, 2);
    }

    @Override
    public void show() {
        Gdx.app.log("ScreenLevel2", "show");

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

        super.rayHandler = new RayHandler(world);
//        rayHandler.setAmbientLight(1);
        rayHandler.setShadows(false);
        super.pointLight = new PointLight(rayHandler, 10, Color.CYAN, 64 / PPM, 0, 0);

        // create parallax water
        this.parallaxWaters.add(new Parallax(this, assets.parallaxAssets.waterTexture,
                1f, 624, 0, 80, 48, true, false));
        this.parallaxWaters.add(new Parallax(this, assets.parallaxAssets.waterTexture,
                1f, 912, 0, 80, 48, true, false));
        this.parallaxWaters.add(new Parallax(this, assets.parallaxAssets.waterTextureBig,
                0, 2496, 0 , 1824, 48, false, false));

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
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        game.getBatch().begin();

        // render common elements (interactive platforms, robot, enemies, collectables, feedbackRenderer)
        super.commonRendering(delta);

        // render parallax water (render after enemy so that fish are behind)
        for(Parallax parallax: parallaxWaters) {
            parallax.draw(game.getBatch());
        }

        // render trampoline
        trampoline.draw(game.getBatch());

        // render fireballs
        for(TankBall tankBall: tankBalls) {
            tankBall.draw(game.getBatch());
        }

        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        hud.draw(game.getBatch());
        game.getBatch().end();


        // render any laser shot
        laserHandler.render(game.getBatch(), shapeRenderer);

        if(DEBUG_ON) {
            // render punch lines
            punchHandler.render(shapeRenderer);
            // render ai paths
            super.renderDebugLines();
        }

        // finally, check if robot is dead
        super.checkIfDead();
    }

    @Override
    public void dispose() {
        Gdx.app.log("ScreenLevel2", "dispose");
        rayHandler.dispose();
        super.dispose();
    }
}
