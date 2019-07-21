package com.robot.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class Collectable extends Sprite {

    PlayScreen playScreen;
    private Sprite burgerSprite;
    private World world;
    private Body body;
    private MapObject object;
    private boolean flagToCollect;
    private boolean isDestroyed;

    public Collectable(PlayScreen playScreen, World world, Body body, FixtureDef fixtureDef, MapObject object) {
        this.playScreen = playScreen;
        this.world = world;
        this.body = body;
        this.object = object;
        body.createFixture(fixtureDef).setUserData(this);
        this.burgerSprite = new Sprite(Assets.getInstance().collectableAssets.burgerTexture);

        // set the size of the bat sprite
        burgerSprite.setSize(COLLECTABLE_WIDTH / PPM, COLLECTABLE_HEIGHT / PPM);
        // attach sprite to body
        burgerSprite.setPosition(body.getPosition().x - COLLECTABLE_WIDTH / 2 / PPM, body.getPosition().y - COLLECTABLE_HEIGHT / 2 / PPM);
    }

    public void update(float delta) {
        if(flagToCollect) {
            destroyBody();
        }
    }

    @Override
    public void draw(Batch batch) {
        burgerSprite.draw(batch);
    }

    public void setFlagToCollect() {
        this.flagToCollect = true;
        this.object.getProperties().put("spawn", false);


    }

    protected void destroyBody() {
        world.destroyBody(body);
        isDestroyed = true;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
