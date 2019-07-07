package com.robot.game.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.physics.box2d.Body;

import static com.robot.game.util.Constants.ROBOT_CLIMB_SPEED;

public class LadderClimbHandler extends InputAdapter {

    private Body body;

    public LadderClimbHandler(Body body) {
        this.body = body;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.UP)
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
        if(keycode == Input.Keys.DOWN)
            body.setLinearVelocity(0, -ROBOT_CLIMB_SPEED);

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.UP || keycode == Input.Keys.DOWN)
            body.setLinearVelocity(0, 0);

        return true;
    }
}
