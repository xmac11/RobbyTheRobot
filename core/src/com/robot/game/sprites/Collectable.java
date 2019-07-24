package com.robot.game.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.screens.ScreenLevel1;
import com.robot.game.util.Assets;
import com.robot.game.util.CollectableHandler;
import org.json.simple.JSONObject;

import static com.robot.game.util.Constants.*;

public class Collectable extends Sprite {

    private ScreenLevel1 screenLevel1;
    private CollectableHandler collectableHandler;
    private Sprite burgerSprite;
    private World world;
    private Body body;
    private MapObject object;
    private JSONObject temp;
    private boolean flagToCollect;
    private boolean isDestroyed;

    public Collectable(ScreenLevel1 screenLevel1, Body body, FixtureDef fixtureDef, MapObject object) {
        this.screenLevel1 = screenLevel1;
        this.collectableHandler = screenLevel1.getCollectableHandler();
        this.world = screenLevel1.getWorld();
        this.body = body;
        this.object = object;
        this.temp = new JSONObject();
        body.createFixture(fixtureDef).setUserData(this);
        this.burgerSprite = new Sprite(Assets.getInstance().collectableAssets.burger);

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
    }

    protected void destroyBody() {
        world.destroyBody(body);
        isDestroyed = true;
    }

    public Body getBody() {
        return body;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public MapObject getObject() {
        return object;
    }

    public void addToDisableSpawning(int collectableID) {
        collectableHandler.getItemsToDisableSpawning().add(collectableID);
    }

    public CollectableHandler getCollectableHandler() {
        return collectableHandler;
    }
}
