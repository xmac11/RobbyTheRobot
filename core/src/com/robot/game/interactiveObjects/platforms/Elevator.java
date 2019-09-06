package com.robot.game.interactiveObjects.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.playscreens.PlayScreen;

public class Elevator extends MovingPlatform {

    private boolean stopped;

    public Elevator(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);
    }

    @Override
    public void update(float delta) {

        // moving horizontally
        if(horizontal) {
            if(outOfRangeX() && !stopped) {
                stop();
                stopped = true;
            }
        }
        // moving vertically
        else {
            if(outOfRangeY() && !stopped) {
                stop();
                stopped = true;
            }
        }
    }

    // stop platform
    private void stop() {
        body.setLinearVelocity(0, 0);
        Gdx.app.log("Elevator", "Platform stopped");

        // for the case of the 'elevator' in the cave, in order for the robot to stop with it
        if(!horizontal && playScreen.getRobot().isOnInteractivePlatform()) {
            playScreen.getRobot().getBody().setLinearVelocity(playScreen.getRobot().getBody().getLinearVelocity().x, 0);
            Gdx.app.log("Elevator", "Robot stopped with vertically moving platform");
        }
    }

    public boolean isStopped() {
        return stopped;
    }
}
