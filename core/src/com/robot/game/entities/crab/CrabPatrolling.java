package com.robot.game.entities.crab;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.EnemyPatrolling;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.SpriteDrawing;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;

public class CrabPatrolling extends EnemyPatrolling {

    public CrabPatrolling(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        // set the size of the crab sprite
        sprite.setSize(CRAB_WIDTH / PPM, CRAB_HEIGHT / PPM);

    }

    @Override
    public void update(float delta) {
        super.updateHorizontalPatrolling(delta);
    }

    @Override
    public void draw(Batch batch) {
        SpriteDrawing.drawCrab(batch, sprite, assets, this);
    }

    @Override
    public int getDamage() {
        return DAMAGE_ON ? DAMAGE_FROM_CRAB : 0;
    }
}
