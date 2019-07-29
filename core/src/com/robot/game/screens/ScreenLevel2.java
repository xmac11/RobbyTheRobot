package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.robot.game.RobotGame;
import com.robot.game.interactiveObjects.InteractivePlatform;
import com.robot.game.util.ObjectParser;

import static com.robot.game.util.Constants.*;

public class ScreenLevel2 extends PlayScreen{

    public ScreenLevel2(RobotGame game) {
        super(game, game.getAssets().tiledMapAssets.tiledMapLevel2);
    }

    @Override
    public void show() {
        Gdx.app.log("ScreenLevel2", "show");

        // create tiled objects
        super.layersObjectArray = new Array<>();
        layersObjectArray.add(tiledMap.getLayers().get(GROUND_OBJECT).getObjects());

        // create object parser
        super.objectParser = new ObjectParser(this);

        // create interactive platforms
        super.interactivePlatforms = objectParser.getInteractivePlatforms();

        System.out.println(interactivePlatforms.size);
    }

    protected void update(float delta) {
        // perform physics simulation
        world.step(1 / 60f, 8, 3);

        // update interactive platforms (do this first if robot should be moving along with it)
        for(int i = 0; i < interactivePlatforms.size; i++) {
            InteractivePlatform platform = interactivePlatforms.get(i);

            // if platform is active, update it
            platform.update(delta);

            // if platform is destroyed, remove from array
            if(platform.isDestroyed())
                interactivePlatforms.removeIndex(i);
        }

        // update robot
        robot.update(delta);

        super.updateViews(delta);
    }

    @Override
    public void render(float delta) {
        // first perform all necessary updates
        this.update(delta);

        // clear game screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        game.getBatch().begin();
        robot.draw(game.getBatch(), delta);
        game.getBatch().end();

        //render box2d debug rectangles
        if(DEBUG_ON)
            debugRenderer.render(world, viewport.getCamera().combined);
    }
}
