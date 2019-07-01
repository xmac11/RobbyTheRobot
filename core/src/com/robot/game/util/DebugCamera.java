package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.*;

public class DebugCamera {

    private Viewport viewport;
    private Camera camera;
    private Robot robot;
    private boolean following;

    public DebugCamera(Viewport viewport, Robot robot) {
        this.viewport = viewport;
        this.camera = viewport.getCamera();
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
            //camera.position.x =  robot.getBody().getPosition().x; // camera follows the robot horizontally
            camera.position.x =  camera.position.x + (robot.getBody().getPosition().x - camera.position.x) * .1f; // camera follows the robot horizontally with interpolation
            camera.position.y = viewport.getWorldHeight() / 2; // keep camera always centered vertically
            //camera.position.y = camera.position.y + (robot.getBody().getPosition().y - camera.position.y) * .1f
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
        // finally clamp the position of the camera within the map
        camera.position.x = MathUtils.clamp(camera.position.x,
                                       viewport.getWorldWidth() / 2,
                                      MAP_WIDTH / PPM - viewport.getWorldWidth() / 2);
        /*camera.position.y = MathUtils.clamp(camera.position.y,
                viewport.getWorldHeight() / 2,
                MAP_HEIGHT / PPM - viewport.getWorldHeight() / 2);*/

        camera.update();
    }
}
