package com.robot.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.entities.abstractEnemies.EnemyArriveAI;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.PPM;
import static com.robot.game.util.Enums.Facing;
import static com.robot.game.util.Enums.Facing.LEFT;
import static com.robot.game.util.Enums.Facing.RIGHT;


public class Monster extends EnemyArriveAI {

    private boolean activated;
    private Facing facing;

    public Monster(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        fixtureDef.density = 1;
        body.createFixture(fixtureDef).setUserData(this);

        this.facing = RIGHT;
        setRegion(assets.monsterAssets.monsterTexture);
        setSize(38 / PPM, 48 / PPM);
    }

    @Override
    public void update(float delta) {
        if(Math.abs(robot.getBody().getPosition().x - body.getPosition().x) < 128 / PPM && !activated) {
            arrive.setEnabled(true);
            activated = true;
            Gdx.app.log("Monster", "Arrive was activated for monster");
        }
        if(steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            super.applySteering(steeringOutput, delta);
        }

        // update facing direction
        if(body.getLinearVelocity().x > 0.5f && facing != RIGHT) {
            facing = RIGHT;
        }
        else if(body.getLinearVelocity().x < -0.5f && facing != LEFT) {
            facing = LEFT;
        }

    }

    @Override
    public void draw(Batch batch) {
        if(facing == RIGHT) {
            if(isFlipX())
                flip(true, false);
        }
        else if(facing == LEFT) {
            if(!isFlipX())
                flip(true, false);
        }

        // attach enemy sprite to body
        setPosition(body.getPosition().x - 38f / 2 / PPM, body.getPosition().y - 48f / 2 / PPM);
        super.draw(batch);
    }

    @Override
    public int getDamage() {
        return 0;
    }
}
