package com.robot.game.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.physics.box2d.Body;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.ROBOT_CLIMB_SPEED;
import static com.robot.game.util.Constants.ROBOT_JUMP_IMPULSE;

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
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
            System.out.println("climb");
        }
        if(keycode == Input.Keys.DOWN && !robot.isFallingOffLadder())
            body.setLinearVelocity(0, -ROBOT_CLIMB_SPEED);

        if(keycode == Input.Keys.SPACE && !robot.isFallingOffLadder()) {
            robot.setFallingOffLadder(true);
            body.setGravityScale(1); // turn on gravity, then jump
            body.applyLinearImpulse(ROBOT_JUMP_IMPULSE, body.getWorldCenter(), true);
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if((keycode == Input.Keys.UP || keycode == Input.Keys.DOWN) && !robot.isFallingOffLadder())
            body.setLinearVelocity(0, 0);

        return true;
    }
}
