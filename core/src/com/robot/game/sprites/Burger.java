package com.robot.game.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.ScreenLevel1;

import static com.robot.game.util.Constants.*;

public class Burger extends  Collectable{


    private Sprite burgerSprite;

    public Burger(ScreenLevel1 screenLevel1, Body body, FixtureDef fixtureDef, MapObject object) {
        super(screenLevel1, body, fixtureDef, object);

        this.burgerSprite = new Sprite(assets.collectableAssets.burger);

        // set the size of the bat sprite
        burgerSprite.setSize(BURGER_WIDTH / PPM, BURGER_HEIGHT / PPM);

        // attach sprite to body
        burgerSprite.setPosition(body.getPosition().x - BURGER_WIDTH / 2 / PPM, body.getPosition().y - BURGER_HEIGHT / 2 / PPM);
    }

    @Override
    public void draw(Batch batch) {
        burgerSprite.draw(batch);
    }
}
