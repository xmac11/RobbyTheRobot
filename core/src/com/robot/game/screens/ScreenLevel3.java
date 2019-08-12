package com.robot.game.screens;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.robot.game.RobotGame;
import com.robot.game.entities.Robot;
import com.robot.game.util.Enums;
import com.robot.game.util.raycast.LaserHandler;
import com.robot.game.util.raycast.PunchHandler;

import static com.robot.game.util.Constants.*;

public class ScreenLevel3 extends PlayScreen {

    private RayHandler rayHandlerTorch;
    private ConeLight coneLight;

    private PointLight pointLightHand;
    private PointLight pointLightHead;

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
//        layersObjectArray.add(tiledMap.getLayers().get(FISH_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(MONSTER_OBJECT).getObjects());
        layersObjectArray.add(tiledMap.getLayers().get(SNAKE_OBJECT).getObjects());
//        layersObjectArray.add(tiledMap.getLayers().get(SPIKE_OBJECT).getObjects());
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
        super.pointLight = new PointLight(rayHandler, 10, Color.CYAN, 48 / PPM, 0, 0);

        // ray handler for torch
        this.rayHandlerTorch = new RayHandler(world);
        rayHandlerTorch.setAmbientLight(0f);

        // create cone light (torch)
        this.coneLight = new ConeLight(rayHandlerTorch, 20, new Color(247f / 255, 242f / 255, 98f / 255, 1),
                288 / PPM, 0 , 0, 0, 50f / 2);
        coneLight.setSoftnessLength(0);
        coneLight.setContactFilter(TORCH_LIGHT_CATEGORY, (short) 0, NOTHING_MASK);

        // point light (hand)
        this.pointLightHand = new PointLight(rayHandlerTorch, 10, Color.GREEN, 16 / PPM, 0 , 0);
        pointLightHand.setContactFilter(TORCH_LIGHT_CATEGORY, (short) 0, NOTHING_MASK);

        // point light (head)
        this.pointLightHead = new PointLight(rayHandlerTorch, 10, new Color(247f / 255, 242f / 255, 98f / 255, 1),
                16 / PPM, 0 , 0);
        pointLightHead.setContactFilter(TORCH_LIGHT_CATEGORY, (short) 0, NOTHING_MASK);

    }

    public void update(float delta) {
        // update common elements
        super.commonUpdates(delta);

        // update torch
        rayHandlerTorch.update();
        rayHandlerTorch.setCombinedMatrix(camera);

        // set position of cone light
        if(robot.getFacing() == Enums.Facing.RIGHT) {
            coneLight.setPosition(robot.getPosition().sub(ROBOT_BODY_WIDTH / 4 / PPM, 0));
            coneLight.setDirection(0);
        }
        else if(robot.getFacing() == Enums.Facing.LEFT) {
            coneLight.setPosition(robot.getPosition().add(ROBOT_BODY_WIDTH / 4 / PPM, 0));
            coneLight.setDirection(180);
        }

        // set position of hand light
        pointLightHand.setPosition(robot.getBody().getPosition().sub(0, ROBOT_BODY_HEIGHT / 4 / PPM));

        // set position of head light
        pointLightHead.setPosition(robot.getBody().getPosition().add(0, ROBOT_BODY_HEIGHT / 3 / PPM));

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
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        game.getBatch().begin();

        // render common elements (interactive platforms, robot, enemies, collectables, feedbackRenderer)
        super.commonRendering(delta);

        // render trampoline
        trampoline.draw(game.getBatch());

        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        //hud.draw(game.getBatch());
        game.getBatch().end();

        // render torch
        rayHandlerTorch.render();

        // render any laser shot
        laserHandler.render(game.getBatch(), shapeRenderer);

        game.getBatch().begin();
        // finally render Hud (hud should be drawn last since it uses a different projection matrix)
        hud.draw(game.getBatch());
        game.getBatch().end();


        if(DEBUG_ON) {
            // render punch lines
            punchHandler.render(shapeRenderer);
            // render box2d shapes and ai paths
            super.renderDebugLines();
        }

        // finally, check if robot is dead
        super.checkIfDead();
    }

    @Override
    public void dispose() {
        Gdx.app.log("ScreenLevel3", "dispose");
        rayHandler.dispose();
        super.dispose();
    }
}
