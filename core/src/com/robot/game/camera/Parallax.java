package com.robot.game.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.screens.PlayScreen;
import com.robot.game.entities.Robot;

import static com.robot.game.util.Constants.PPM;

public class Parallax {

    private Texture texture;

    private float y, width, height;
    private int srcX;

    private PlayScreen playScreen;
    private Viewport viewport;
    private Camera camera;
    private Robot robot;
    private float speedScale;
    private boolean waterFlow;

    public Parallax(PlayScreen playScreen, Texture texture, float speedScale, float y, float height, boolean waterFlow) {
        this.playScreen = playScreen;
        this.viewport = playScreen.getViewport();
        this.camera = viewport.getCamera();
        this.robot = playScreen.getRobot();
        this.texture = texture;
        this.speedScale = speedScale;
        this.waterFlow = waterFlow;

        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        this.y = y / PPM;
        this.width =  playScreen.getMapWidth() / PPM;
        this.height = height / PPM;
    }

    public void draw(SpriteBatch batch) {
        // if the robot is in the beginning or the end of the map
        if(robot.getBody().getPosition().x < camera.position.x || robot.getBody().getPosition().x > playScreen.getMapWidth() / PPM - viewport.getWorldWidth() ) {
            //srcX += 0;
            if(waterFlow)
                srcX += 1;
        }
        // for keeping robot in the lhs of the screen (instead of the above "if")
        /*if(robot.getBody().getPosition().x < viewport.getWorldWidth() / 2 || robot.getBody().getPosition().x > MAP_WIDTH / PPM - viewport.getWorldWidth()) {
            //srcX += 0;
            if(waterFlow)
                srcX += 1;
        }*/
        else {
            // for water image
            if(waterFlow) {
                if(robot.getBody().getLinearVelocity().x > 4f)
                    srcX += 2;
                else if( robot.getBody().getLinearVelocity().x < -4f)
                    srcX += 1.5;
                else
                    srcX += 1;
            }
            // for background image
            else
                srcX -= robot.getBody().getLinearVelocity().x;
        }

        batch.draw(texture, 0, y, width, height, (int) (srcX * speedScale),0, texture.getWidth(), texture.getHeight(),false,false);
    }
}
