package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.physics.box2d.Body;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.*;

public class LadderClimbHandler extends InputAdapter {

    private Robot robot;
    private Body body;

    public LadderClimbHandler(Robot robot) {
        this.robot = robot;
        this.body = robot.getBody();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.UP && !robot.isFallingOffLadder()) {
            Gdx.app.log("LadderClimbHandler", "climbing up");
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
        }
        if(keycode == Input.Keys.DOWN && !robot.isFallingOffLadder()) {
            Gdx.app.log("LadderClimbHandler", "climbing down");
            body.setLinearVelocity(0, -ROBOT_CLIMB_SPEED);
        }

        if(keycode == Input.Keys.SPACE && !robot.isFallingOffLadder()) {
            Gdx.app.log("LadderClimbHandler", "jumped off ladder");
            robot.setFallingOffLadder(true);
            body.setGravityScale(1); // turn on gravity, then jump
            body.setLinearVelocity(body.getLinearVelocity().x, ROBOT_JUMP_SPEED);
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if((keycode == Input.Keys.UP || keycode == Input.Keys.DOWN) && !robot.isFallingOffLadder()) {
            body.setLinearVelocity(0, 0);
            Gdx.app.log("LadderClimbHandler", "stopped climbing");
        }

        return true;
    }
}
