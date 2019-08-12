package com.robot.game.interactiveObjects.collectables;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class Food extends Collectable {

    public Food(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        if(MathUtils.random() > 0.5f) {
            super.sprite = new Sprite(assets.collectableAssets.burger);
        }
        else if(MathUtils.random() > 0.5f) {
            super.sprite = new Sprite(assets.collectableAssets.donut_pink);
        }
        else {
            super.sprite = new Sprite(assets.collectableAssets.donut_red);
        }

        // set the size of the bat sprite
        sprite.setSize(FOOD_WIDTH / PPM, FOOD_HEIGHT / PPM);

        // attach sprite to body
        sprite.setPosition(body.getPosition().x - FOOD_WIDTH / 2 / PPM, body.getPosition().y - FOOD_HEIGHT / 2 / PPM);
    }
}
