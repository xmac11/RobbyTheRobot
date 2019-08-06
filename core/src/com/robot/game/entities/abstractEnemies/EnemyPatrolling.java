package com.robot.game.entities.abstractEnemies;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.PPM;

public abstract class EnemyPatrolling extends Enemy {

    // EnemyPatrolling range (non-ai)
    protected float startX;
    protected float startY;
    protected float endX;
    protected float endY;
    protected float vX;
    protected float vY;
    protected boolean horizontal;

    public EnemyPatrolling(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        this.startX = (float) object.getProperties().get("startX");
        this.startY = (float) object.getProperties().get("startY");
        this.endX = (float) object.getProperties().get("endX");
        this.endY = (float) object.getProperties().get("endY");

        this.vX = (float) object.getProperties().get("vX");
        this.vY = (float) object.getProperties().get("vY");

        this.horizontal = (vX != 0);

        body.setLinearVelocity(vX, vY);
    }

    // check if enemy is outside its moving range in x-direction
    protected boolean outOfRangeX() {
        return body.getPosition().x <= startX / PPM || body.getPosition().x >= endX / PPM;
    }

    // check if enemy is outside its moving range in y-direction
    protected boolean outOfRangeY() {
        return body.getPosition().y <= startY / PPM || body.getPosition().y >= endY / PPM;
    }
}
