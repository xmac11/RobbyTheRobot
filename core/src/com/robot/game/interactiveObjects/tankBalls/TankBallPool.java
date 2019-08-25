package com.robot.game.interactiveObjects.tankBalls;

import com.badlogic.gdx.utils.Pool;
import com.robot.game.screens.playscreens.PlayScreen;

public class TankBallPool extends Pool<TankBall> {

    private PlayScreen playScreen;

    public TankBallPool(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    @Override
    protected TankBall newObject() {
        return new TankBall(playScreen);
    }

}
