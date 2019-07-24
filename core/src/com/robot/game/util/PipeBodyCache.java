package com.robot.game.util;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PipeBodyCache {

    private BodyDef bodyDef;
    private FixtureDef fixtureDef;

    public PipeBodyCache() {
        this.bodyDef = new BodyDef();
        this.bodyDef.type = BodyDef.BodyType.DynamicBody;

        this.fixtureDef = new FixtureDef();
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }
}
