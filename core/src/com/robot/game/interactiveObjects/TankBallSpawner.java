package com.robot.game.interactiveObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.screens.PlayScreen;

public class TankBallSpawner {
    private PlayScreen playScreen;
    private TankBallPool tankBallPool;
    private float tankStartTime;
    private float tankElapsed;

    public TankBallSpawner(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.tankBallPool = playScreen.tankBallPool;
    }

    public void handleSpawning() {
        if(tankElapsed > 2f) {
            TankBall tankBall = tankBallPool.obtain();
            tankBall.createTankBallB2d();
            tankBall.getBody().applyLinearImpulse(new Vector2(0, 10), tankBall.getBody().getWorldCenter(), true);
            playScreen.tankBalls.add(tankBall);

            this.tankStartTime = TimeUtils.nanoTime();
            this.tankElapsed = 0;
            Gdx.app.log("TankBallSpawner", "New tank ball was created");
        }
        else {
            this.tankElapsed = (TimeUtils.nanoTime() - tankStartTime) * MathUtils.nanoToSec;
        }
    }
}
