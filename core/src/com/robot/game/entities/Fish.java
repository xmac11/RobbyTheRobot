package com.robot.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class Fish extends Enemy {

    public Fish(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        // set the size of the crab sprite
        setSize(16f / PPM, 28f / PPM);

        setRegion(new Texture("level2/fish1.png"));
    }
    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(Batch batch) {
        // attach enemy sprite to body
        setPosition(body.getPosition().x - 16f / 2 / PPM, body.getPosition().y - 28f / 2 / PPM);
        super.draw(batch);
    }

    @Override
    public int getDamage() {
        return 0;
    }
}
