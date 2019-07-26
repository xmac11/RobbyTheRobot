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

       /* Check if UP key is already pressed.
        * This has to be checked because the keydown() method will not be executed if the key was pressed before
        * the creation of the LadderClimbHandler, i.e. before the robot got on the ladder*/
        checkIfShouldClimb();
    }

    private void checkIfShouldClimb() {

        // isKeyPressed mutually exclusive with the setOnLadder() method
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            Gdx.app.log("LadderClimbHandler", "Constructor: UP key is pressed");
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
        }
    }

    @Override
    public boolean keyDown(int keycode) {

        // climb up up while on ladder
        if(keycode == Input.Keys.UP && !robot.isFallingOffLadder()) {
            Gdx.app.log("LadderClimbHandler", "climbing up");
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
        }
        // climb up up while falling off ladder (grabs ladder)
        else if(keycode == Input.Keys.UP) {
            Gdx.app.log("LadderClimbHandler", "grabbed on ladder while falling");
            robot.setFallingOffLadder(false);
            body.setGravityScale(0);
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
        }

        // climb down
        if(keycode == Input.Keys.DOWN && !robot.isFallingOffLadder()) {
            Gdx.app.log("LadderClimbHandler", "climbing down");
            body.setLinearVelocity(0, -ROBOT_CLIMB_SPEED);
        }

        // jump off ladder
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
