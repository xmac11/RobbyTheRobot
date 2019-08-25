package com.robot.game.interactiveObjects.tankBalls;

import com.badlogic.gdx.Gdx;
import com.robot.game.entities.Robot;
import com.robot.game.screens.playscreens.PlayScreen;

import static com.robot.game.util.constants.Constants.*;

public class TankBallSpawner {
    private PlayScreen playScreen;
    private Robot robot;
    private TankBallPool tankBallPool;

    private boolean tankActivated;
    private boolean tankDisabled;

    private float tankElapsed;

    public TankBallSpawner(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.robot = playScreen.getRobot();
        this.tankBallPool = playScreen.getTankBallPool();
    }

    public void update(float delta) {
        // first check if tank should be activated / disabled
        if(!tankActivated && !tankDisabled) {
            checkForTankActivation();
        }
        else if(tankActivated) {
            checkForTankDisabling();
        }

        // if it is activated, handle spawning
        if(tankActivated) {
            handleSpawning(delta);
        }
    }

    private void checkForTankActivation() {
        if(Math.abs(robot.getBody().getPosition().x * PPM - TANKBALL_ACTIVATION_AREA.x) <= CHECKPOINT_TOLERANCE &&
                Math.abs(robot.getBody().getPosition().y * PPM - TANKBALL_ACTIVATION_AREA.y) <= CHECKPOINT_TOLERANCE) {

            tankActivated = true;
            tankElapsed = TANKBALL_SPWANING_PERIOD; // so that tankball is spawned upon activation
            Gdx.app.log("TankBallSpawner", "Tank was activated");
        }
    }

    private void checkForTankDisabling() {
        if(Math.abs(robot.getBody().getPosition().x * PPM - TANKBALL_DISABLING_AREA.x) <= CHECKPOINT_TOLERANCE &&
                Math.abs(robot.getBody().getPosition().y * PPM - TANKBALL_DISABLING_AREA.y) <= CHECKPOINT_TOLERANCE) {

            tankActivated = false;
            tankDisabled = true;
            Gdx.app.log("TankBallSpawner", "Tank was disabled");
        }
    }

    private void handleSpawning(float delta) {
        if(tankElapsed > TANKBALL_SPWANING_PERIOD) {
            // obtain tank ball from pool
            TankBall tankBall = tankBallPool.obtain();

            // create its box2d body
            tankBall.createTankBallB2d();

            // play tankball fire sound
            if(!playScreen.isMuted()) {
                playScreen.getAssets().soundAssets.tankballFireSound.play(0.5f);
            }

            // apply impulse
            tankBall.getBody().applyLinearImpulse(TANKBALL_IMPULSE, tankBall.getBody().getWorldCenter(), true);

            // add it to array
            playScreen.getTankBalls().add(tankBall);

            this.tankElapsed = 0;
            Gdx.app.log("TankBallSpawner", "New tank ball was created");
        }
        else {
            this.tankElapsed += delta;
        }
    }

    public void setToNull() {
        robot = null;
        tankBallPool = null;
        playScreen = null;
        Gdx.app.log("TankBallSpawner", "Objects were set to null");
    }
}
