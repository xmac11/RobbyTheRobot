package com.robot.game.interactiveObjects.tankBalls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.Robot;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class TankBallSpawner {
    private PlayScreen playScreen;
    private Robot robot;
    private TankBallPool tankBallPool;

    private boolean tankActivated;
    private boolean tankDisabled;

    private float tankStartTime;
    private float tankElapsed;

    public TankBallSpawner(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.robot = playScreen.getRobot();
        this.tankBallPool = playScreen.getTankBallPool();
    }

    public void update() {
        // first check if tank should be activated / disabled
        if(!tankActivated && !tankDisabled) {
            checkForTankActivation();
        }
        else if(tankActivated) {
            checkForTankDisabling();
        }

        // if it is activated, handle spawning
        if(tankActivated) {
            handleSpawning();
        }
    }

    private void checkForTankActivation() {
        if(Math.abs(robot.getBody().getPosition().x * PPM - 4350) <= CHECKPOINT_TOLERANCE &&
                Math.abs(robot.getBody().getPosition().y * PPM - 780) <= CHECKPOINT_TOLERANCE) {

            tankActivated = true;
            Gdx.app.log("TankBallSpawner", "Tank was activated");
        }
    }

    private void checkForTankDisabling() {
        if(Math.abs(robot.getBody().getPosition().x * PPM - 4592) <= CHECKPOINT_TOLERANCE &&
                Math.abs(robot.getBody().getPosition().y * PPM - 80) <= CHECKPOINT_TOLERANCE) {

            tankActivated = false;
            tankDisabled = true;
            Gdx.app.log("TankBallSpawner", "Tank was disabled");
        }
    }

    private void handleSpawning() {
        if(tankElapsed > TANKBALL_SPWANING_PERIOD) {
            TankBall tankBall = tankBallPool.obtain();
            tankBall.createTankBallB2d();
            tankBall.getBody().applyLinearImpulse(TANKBALL_IMPULSE, tankBall.getBody().getWorldCenter(), true);
            playScreen.getTankBalls().add(tankBall);

            this.tankStartTime = TimeUtils.nanoTime();
            this.tankElapsed = 0;
            Gdx.app.log("TankBallSpawner", "New tank ball was created");
        }
        else {
            this.tankElapsed = (TimeUtils.nanoTime() - tankStartTime) * MathUtils.nanoToSec;
        }
    }
}
