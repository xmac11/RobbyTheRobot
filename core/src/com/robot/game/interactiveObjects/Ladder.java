package com.robot.game.interactiveObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Ladder {

    private Body body;

    public Ladder(Body body, FixtureDef fixtureDef) {

        this.body = body;
        body.createFixture(fixtureDef).setUserData(this);
    }
}
