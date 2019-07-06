package com.robot.game.interactiveObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Ladder {

    private Body body;
    private String description;

    public Ladder(Body body, FixtureDef fixtureDef, String description) {
        this.body = body;
        this.description = description;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public String getDescription() {
        return description;
    }
}
