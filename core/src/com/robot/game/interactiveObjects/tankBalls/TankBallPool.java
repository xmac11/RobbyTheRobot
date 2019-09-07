package com.robot.game.interactiveObjects.tankBalls;

import com.badlogic.gdx.utils.Pool;

public class TankBallPool extends Pool<TankBall> {

    private TankBallSpawner tankBallSpawner;

    public TankBallPool(TankBallSpawner tankBallSpawner) {
        this.tankBallSpawner = tankBallSpawner;
    }

    @Override
    protected TankBall newObject() {
        return new TankBall(tankBallSpawner);
    }
}
