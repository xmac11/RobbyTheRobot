package com.robot.game.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.*;

public class Parallax {

    private Texture texture;

    private float y, width, height;
    private int srcX;

    private Viewport viewport;
    private Camera camera;
    private Robot robot;
    private float speedScale;
    private boolean waterFlow;

    public Parallax(Viewport viewport, Robot robot, Texture texture, float speedScale, float y, float height, boolean waterFlow) {
        this.viewport = viewport;
        this.camera = viewport.getCamera();
        this.robot = robot;
        this.texture = texture;
        this.speedScale = speedScale;
        this.waterFlow = waterFlow;

        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        this.y = y / PPM;
        this.width =  MAP_WIDTH / PPM;
        this.height = height / PPM;
    }

    public void draw(SpriteBatch batch) {
        // if the robot is in the beginning or the end of the map
        if(robot.getBody().getPosition().x < camera.position.x || robot.getBody().getPosition().x > MAP_WIDTH / PPM - viewport.getWorldWidth() ) {
            //srcX += 0;
            if(waterFlow)
                srcX += 1;
        }
        else {
            // for water image
            if(waterFlow) {
                if(Math.abs(robot.getBody().getLinearVelocity().x) < 1f)
                    srcX += 1;
                else
                    srcX += robot.getBody().getLinearVelocity().x;
            }
            // for background image
            else
                srcX -= robot.getBody().getLinearVelocity().x;
        }

        batch.draw(texture, 0, y, width, height, (int) (srcX * speedScale),0, texture.getWidth(), texture.getHeight(),false,false);
    }
}
