package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.DEBUG_CAM_SPEED;

public class DebugCamera {

    private Camera camera;
    private Robot robot;
    private boolean following;

    public DebugCamera(Camera camera, Robot robot) {
        this.camera = camera;
        this.robot = robot;
        this.following = true;
    }

    public void update(float delta) {
        // reverse boolean when C is pressed
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            following = !following;
        }

        // if following, follow robot, else move camera according to input
        if(following) {
            camera.position.x =  robot.getBody().getPosition().x;
            camera.position.y =  robot.getBody().getPosition().y;
        }
        else {
            if(Gdx.input.isKeyPressed(Input.Keys.A))
                camera.position.x -= delta * DEBUG_CAM_SPEED;
            if(Gdx.input.isKeyPressed(Input.Keys.D))
                camera.position.x += delta * DEBUG_CAM_SPEED;
            if(Gdx.input.isKeyPressed(Input.Keys.W))
                camera.position.y += delta * DEBUG_CAM_SPEED;
            if(Gdx.input.isKeyPressed(Input.Keys.S))
                camera.position.y -= delta * DEBUG_CAM_SPEED;
        }
    }
}
