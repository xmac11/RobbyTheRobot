package com.robot.game.interactiveObjects.fallingPipes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.camera.ShakeEffect;
import com.robot.game.entities.Robot;
import com.robot.game.screens.playscreens.PlayScreen;

import static com.robot.game.util.constants.Constants.*;

public class FallingPipeSpawner {

    private PlayScreen playScreen;
    private Robot robot;
    private DelayedRemovalArray<FallingPipe> fallingPipes;
    private ShakeEffect shakeEffect;

    private boolean earthquakeHappened;
    private boolean pipesStartedFalling;
    private boolean pipesDisabled;

    private float pipeElapsed;

    public FallingPipeSpawner(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.robot = playScreen.getRobot();
        this.shakeEffect = playScreen.getShakeEffect();

        this.fallingPipes = new DelayedRemovalArray<>();
        createInitialPipes();
    }

    private void createInitialPipes() {
        for(int i = 0; i < 5; i++) {
            fallingPipes.add(new FallingPipe(this, true));
        }
    }

    public void update(float delta) {
        // update falling pipes
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.update(delta);
        }

        // check for earthquake
        if(!earthquakeHappened && !pipesStartedFalling && !pipesDisabled) {
            checkForEarthquake();
        }

        // if earthquake happened, spawn pipes when appropriate
        if(earthquakeHappened) {
            // activate cached pipes
            for(FallingPipe fallingPipe : fallingPipes) {
                fallingPipe.getBody().setAwake(true);
                fallingPipe.getBody().setGravityScale(1);
            }
            pipeElapsed = -3f; //add extra 3 seconds timeout initially
            earthquakeHappened = false;
            pipesStartedFalling = true;
        }

        if(pipesStartedFalling && !pipesDisabled) {
            if(checkDisablingPipes())
                this.pipesDisabled = true;

            if(!pipesDisabled && shouldSpawnPipe(delta)) {
                // play falling pipe sound
                if(!playScreen.isMuted()) {
                    playScreen.getAssets().soundAssets.fallingPipeSound.play();
                }

                // follow up earthquakes with probability 50%
                if(MathUtils.random() > 0.5f) {
                    shakeEffect.shake(EARTH_SHAKE_INTENSITY, EARTH_SHAKE_TIME / 10);
                }

                fallingPipes.add(new FallingPipe(this, false));
            }
        }
    }

    public void draw(SpriteBatch batch) {
        // render falling pipes
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.draw(batch);
        }
    }

    private void checkForEarthquake() {
        // if robot is in the shake area and the shake is not already active, start it
        if(Math.abs(robot.getBody().getPosition().x * PPM - PIPES_START_X) <= 48) {
            Gdx.app.log("FallingPipeSpawner", "Earthquake activated");

            // play falling many pipes sound
            if(!playScreen.isMuted()) {
                playScreen.getAssets().soundAssets.fallingManyPipesSound.play(0.6f);
            }

            shakeEffect.shake(EARTH_SHAKE_INTENSITY, EARTH_SHAKE_TIME);
            earthquakeHappened = true;
        }
    }

    private boolean checkDisablingPipes() {
        return robot.getBody().getPosition().x > PIPES_END_X / PPM;
    }

    private boolean shouldSpawnPipe(float delta) {
        if(pipeElapsed >= PIPES_SPAWNING_PERIOD) {
            this.pipeElapsed = 0;
            return true;
        }
        else  {
            this.pipeElapsed += delta;
            return false;
        }
    }

    public PlayScreen getPlayScreen() {
        return playScreen;
    }

    public void setToNull() {
        for(FallingPipe fallingPipe: fallingPipes) {
            fallingPipe.setToNull();
        }
        fallingPipes = null;
        robot = null;
        shakeEffect = null;
        playScreen = null;
        Gdx.app.log("FallingPipeSpawner", "Objects were set to null");
    }
}
