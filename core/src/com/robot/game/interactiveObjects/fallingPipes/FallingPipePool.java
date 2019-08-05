package com.robot.game.interactiveObjects.fallingPipes;

import com.badlogic.gdx.utils.Pool;
import com.robot.game.screens.PlayScreen;

public class FallingPipePool extends Pool<FallingPipe> {

    private PlayScreen playScreen;

    public FallingPipePool(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    @Override
    protected FallingPipe newObject() {
        return new FallingPipe(playScreen, false);
    }
}
