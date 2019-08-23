package com.robot.game.interactiveObjects.fallingPipes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.camera.ShakeEffect;
import com.robot.game.entities.Robot;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

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
        this.fallingPipes = playScreen.getFallingPipes();
        this.shakeEffect = playScreen.getShakeEffect();
    }

    public void update(float delta) {
        if(!earthquakeHappened && !pipesStartedFalling && !pipesDisabled) {
            checkForEarthquake();
        }

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

                fallingPipes.add(new FallingPipe(playScreen, false));
            }
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

    public void setToNull() {
        robot = null;
        fallingPipes = null;
        shakeEffect = null;
        playScreen = null;
        Gdx.app.log("FallingPipeSpawner", "Objects were set to null");
    }
}
