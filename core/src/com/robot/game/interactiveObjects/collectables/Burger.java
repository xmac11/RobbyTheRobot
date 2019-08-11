package com.robot.game.interactiveObjects.collectables;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class Burger extends Collectable {

    public Burger(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        super.sprite = new Sprite(assets.collectableAssets.burger);

        // set the size of the bat sprite
        sprite.setSize(BURGER_WIDTH / PPM, BURGER_HEIGHT / PPM);

        // attach sprite to body
        sprite.setPosition(body.getPosition().x - BURGER_WIDTH / 2 / PPM, body.getPosition().y - BURGER_HEIGHT / 2 / PPM);
    }
}
