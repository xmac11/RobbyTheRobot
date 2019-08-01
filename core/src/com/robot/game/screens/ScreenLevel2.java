package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.robot.game.RobotGame;
import com.robot.game.entities.Enemy;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.interactiveObjects.platforms.InteractivePlatform;
import com.robot.game.util.ObjectParser;

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

        // create object parser
        super.objectParser = new ObjectParser(this);

        // create enemies
        super.enemies = objectParser.getEnemies();

        // create collectables
        super.collectables = objectParser.getCollectables();

        // create interactive platforms
        super.interactivePlatforms = objectParser.getInteractivePlatforms();

        // create trampoline
        super.trampoline = objectParser.getTrampoline();

    }

    protected void update(float delta) {
        super.commonUpdates(delta);

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

        // render interactive platforms
        for(InteractivePlatform platform: interactivePlatforms) {
            if(!platform.isDestroyed()) {
                platform.draw(game.getBatch());
            }
        }

        // render enemies
        for(Enemy enemy: enemies) {
            if(!enemy.isDestroyed()) {
                enemy.draw(game.getBatch());
            }
        }

        // render collectables
        for(Collectable collectable: collectables) {
            if(!collectable.isDestroyed())
                collectable.draw(game.getBatch());
        }

        robot.draw(game.getBatch(), delta);

        trampoline.draw(game.getBatch());

        game.getBatch().end();

        //render box2d debug rectangles
        if(DEBUG_ON)
            debugRenderer.render(world, viewport.getCamera().combined);
    }
}
