package com.robot.game.interactiveObjects.ladder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.physics.box2d.Body;
import com.robot.game.entities.Robot;

import static com.robot.game.util.Constants.*;

public class LadderClimbHandler extends InputAdapter {

    private Robot robot;
    private Body body;

    public LadderClimbHandler(Robot robot) {
        this.robot = robot;
        this.body = robot.getBody();
    }

    /* Check if UP key was pressed within the last 0.3 seconds and it is still being pressed.
     * This has to be checked because the keydown() method will not be executed if the key was pressed before
     * the input processor was set to LadderClimbHandler, i.e. before the robot got on the ladder*/
    public void checkForClimbTimer() {
        if(robot.getClimbTimer() > 0 && (Gdx.input.isKeyPressed(Input.Keys.UP) || robot.getPlayScreen().getAndroidController().isUpPressed() )) {
            Gdx.app.log("LadderClimbHandler", "climbTimer > 0");
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
            robot.resetClimbTimer();
        }
    }

    @Override
    public boolean keyDown(int keycode) {

        // climb up while on ladder
        if(keycode == Input.Keys.UP && !robot.isFallingOffLadder()) {
            climb(1);
        }
        // climb up up while falling off ladder (grabs ladder)
        else if(keycode == Input.Keys.UP) {
            grabOnLadder();
        }

        // climb down
        if(keycode == Input.Keys.DOWN && !robot.isFallingOffLadder()) {
            climb(-1);
        }

        // jump off ladder
        if(keycode == Input.Keys.SPACE && !robot.isFallingOffLadder()) {
            jumpOffLadder();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if((keycode == Input.Keys.UP || keycode == Input.Keys.DOWN) && !robot.isFallingOffLadder()) {
            stopClimbing();
        }

        return true;
    }

    public void climb(int direction) {
        if(direction == 1) {
            Gdx.app.log("LadderClimbHandler", "climbing up");
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
        }
        else if(direction == -1) {
            Gdx.app.log("LadderClimbHandler", "climbing down");
            body.setLinearVelocity(0, -ROBOT_CLIMB_SPEED);
        }
    }

    public void stopClimbing() {
        Gdx.app.log("LadderClimbHandler", "stopped climbing");
        body.setLinearVelocity(0, 0);
    }

    public void grabOnLadder() {
        Gdx.app.log("LadderClimbHandler", "grabbed on ladder while falling");
        robot.setFallingOffLadder(false);
        body.setGravityScale(0);
        body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
    }

    public void jumpOffLadder() {
        Gdx.app.log("LadderClimbHandler", "jumped off ladder");
        // play jump sound
        if(!robot.getPlayScreen().isMuted()) {
            robot.getPlayScreen().getAssets().soundAssets.jumpSound.play();
        }

        robot.setFallingOffLadder(true);
        body.setGravityScale(1); // turn on gravity, then jump
        body.setLinearVelocity(body.getLinearVelocity().x, ROBOT_JUMP_SPEED);
    }

    public void setToNull() {
        robot = null;
        Gdx.app.log("LadderClimbHandler", "Objects were set to null");
    }
}
