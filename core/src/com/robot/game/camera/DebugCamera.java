package com.robot.game.camera;

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
    private boolean shakeJustStopped;

    public DebugCamera(Viewport viewport, Robot robot) {
        this.viewport = viewport;
        this.camera = viewport.getCamera();
//        camera.position.x =  robot.getBody().getCameraDisplacement().x;
        this.robot = robot;
//        camera.position.x = robot.getBody().getPosition().x;
//        camera.position.y = robot.getBody().getPosition().y;
        this.following = true;
    }

    public void update(float delta) {

        // reverse boolean when 'C' is pressed
        if(DEBUG_ON && Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            following = !following;
        }

        // if following, follow robot, else move camera according to input
        if(following) {

            if(ShakeEffect.isShakeON() /*|| ShakeEffect.getTimeToShake() > 0*/) {
                ShakeEffect.update();
                camera.translate(ShakeEffect.getCameraDisplacement());
            }

            // update camera's position
            if(!ShakeEffect.isShakeON()) {
                // camera follows the robot
                camera.position.x = robot.getBody().getPosition().x;
                camera.position.y = robot.getBody().getPosition().y;
            }
            // when camera is shaking, lerp to robot's position to get a smooth transition
            else {
                camera.position.x = MathUtils.lerp(camera.position.x, robot.getBody().getPosition().x, 0.2f);
                camera.position.y = MathUtils.lerp(camera.position.y, robot.getBody().getPosition().y, 0.2f);
            }

            /*if(shakeJustStopped) {
                System.out.println("Shake just stopped");
                camera.position.x = MathUtils.lerp(camera.position.x, robot.getBody().getPosition().x, 0.01f);
                camera.position.y = MathUtils.lerp(camera.position.y, robot.getBody().getPosition().y, 0.01f);
                shakeJustStopped = false;
            }*/
        }
        // camera is not following the player
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
        if(following) {
            camera.position.x = MathUtils.clamp(camera.position.x,
                    viewport.getWorldWidth() / 2,
                    MAP_WIDTH / PPM - viewport.getWorldWidth() / 2);
            camera.position.y = MathUtils.clamp(camera.position.y,
                    viewport.getWorldHeight() / 2,
                    MAP_HEIGHT / PPM - viewport.getWorldHeight() / 2);
        }
        camera.update();
    }
}
