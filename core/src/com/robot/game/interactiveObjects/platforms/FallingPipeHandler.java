package com.robot.game.interactiveObjects.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.camera.ShakeEffect;
import com.robot.game.entities.Robot;
import com.robot.game.interactiveObjects.FallingPipe;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class FallingPipeHandler {

    private PlayScreen playScreen;
    private Robot robot;
    private DelayedRemovalArray<FallingPipe> fallingPipes;
    private ShakeEffect shakeEffect;

    private boolean earthquakeHappened;
    private boolean pipesStartedFalling;
    private boolean pipesDisabled;

    private float pipeStartTime;
    private float pipeElapsed;

    public FallingPipeHandler(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.robot = playScreen.getRobot();
        this.fallingPipes = playScreen.getFallingPipes();
        this.shakeEffect = playScreen.getShakeEffect();
    }

    public void handleEarthquake() {
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
                fallingPipes.add(new FallingPipe(playScreen, false));
            }
        }
    }

    private void checkForEarthquake() {
        // if robot is in the shake area and the shake is not already active, start it
        if(Math.abs(robot.getBody().getPosition().x * PPM - PIPES_START_X) <= 48) {
            Gdx.app.log("FallingPipeHandler", "Earthquake activated");
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
