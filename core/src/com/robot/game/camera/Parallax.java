package com.robot.game.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.screens.PlayScreen;
import com.robot.game.entities.Robot;

import static com.robot.game.util.Constants.PPM;

public class Parallax {

    private Texture texture;

    private float x, y, width, height;
    private int srcX;

    private PlayScreen playScreen;
    private Viewport viewport;
    private Camera camera;
    private Robot robot;
    private float speedScale;
    private boolean waterFlow;
    private boolean bindWithRobot;

    public Parallax(PlayScreen playScreen, Texture texture, float speedScale, float x, float y, float width, float height, boolean waterFlow, boolean bindWithRobot) {
        this.playScreen = playScreen;
        this.viewport = playScreen.getViewport();
        this.camera = viewport.getCamera();
        this.robot = playScreen.getRobot();
        this.texture = texture;
        this.speedScale = speedScale;
        this.waterFlow = waterFlow;
        this.bindWithRobot = bindWithRobot;

        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        this.x = x / PPM;
        this.y = y / PPM;
        this.width =  width / PPM;
        this.height = height / PPM;
    }

    public void update(float delta) {
        // background water moves independently with robot's velocity
        if(!bindWithRobot && waterFlow) {
            srcX += 1;
        }
        // background moves according to robot's velocity
        else {
            // if the robot is in the beginning or the end of the map
            if(robot.getBody().getPosition().x < camera.position.x || robot.getBody().getPosition().x > playScreen.getMapWidth() / PPM - viewport.getWorldWidth()) {
                //srcX += 0;
                if (waterFlow)
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
                    else if(robot.getBody().getLinearVelocity().x < -4f)
                        srcX += 1.5;
                    else
                        srcX += 1;
                }
                // for background image
                else
                    srcX -= robot.getBody().getLinearVelocity().x;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height, (int) (srcX * speedScale),0, texture.getWidth(), texture.getHeight(),false,false);
    }

    public void setToNull() {
        viewport = null;
        camera = null;
        robot = null;
        texture = null;
        Gdx.app.log("Parallax", "Objects set to null");
    }
}
