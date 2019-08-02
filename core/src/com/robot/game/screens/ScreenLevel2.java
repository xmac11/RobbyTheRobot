package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.RobotGame;
import com.robot.game.interactiveObjects.TankBall;
import com.robot.game.interactiveObjects.TankBallPool;
import com.robot.game.interactiveObjects.TankBallSpawner;

import static com.robot.game.util.Constants.*;

public class ScreenLevel2 extends PlayScreen{

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
        layersObjectArray.add(tiledMap.getLayers().get(BAT_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(SPIKE_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(COLLECTABLE_OBJECT).getObjects());

        // creates objectParser, interactivePlatforms, enemies and collectables
        super.createCommonObjectLayers();

        // create trampoline
        super.trampoline = objectParser.getTrampoline();

        super.tankBalls = new DelayedRemovalArray<>();
        super.tankBallPool = new TankBallPool(this);
        super.tankBallSpawner = new TankBallSpawner(this);
    }

    protected void update(float delta) {
        // update common elements
        super.commonUpdates(delta);

        tankBallSpawner.handleSpawning();
        System.out.println("active " + tankBalls.size + ", free " + tankBallPool.getFree());

        for(int i = 0; i < tankBalls.size; i++) {
            TankBall tankBall = tankBalls.get(i);
            tankBall.update(delta);
        }

        // update view
        super.updateViews(delta);
    }

    @Override
    public void render(float delta) {
        // first perform all necessary updates
        this.update(delta);

        // clear game screen
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        game.getBatch().begin();

        // render common elements (interactive platforms, robot, enemies, collectables, feedbackRenderer)
        super.commonRendering(delta);

        // render trampoline
        trampoline.draw(game.getBatch());

        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        hud.draw(game.getBatch());

        game.getBatch().end();

        //render box2d debug rectangles
        if(DEBUG_ON) {
            debugRenderer.render(world, viewport.getCamera().combined);
        }

        // finally, check if robot is dead
        super.checkIfDead();
    }
}
