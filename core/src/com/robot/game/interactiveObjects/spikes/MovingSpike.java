package com.robot.game.interactiveObjects.spikes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class MovingSpike extends Spike {

    private Sprite sprite;
    public Sprite baseSpirte;
    public Sprite stickSpirte;
    private boolean inBalancePosition;
    private boolean attacking;
    private int id;
    private float upperTranslationA;
    private float upperTranslationB;
    private float timeElapsed;

    public MovingSpike(Body body, FixtureDef fixtureDef, MapObject object, ObjectMap jointMap, Assets assets) {
        super(body, fixtureDef, object);
        this.sprite = new Sprite(assets.trapAssets.trapSpikes);
        this.baseSpirte = new Sprite(assets.trapAssets.trapBase);
        this.stickSpirte = new Sprite(assets.trapAssets.trapStick);
        this.inBalancePosition = true;

        this.upperTranslationA = (float) object.getProperties().get("upperTranslationA") / PPM;
        this.upperTranslationB = (float) object.getProperties().get("upperTranslationB") / PPM;

        this.id = (int) object.getProperties().get("prismatic");
        Array<Body> bodyArray = (Array) jointMap.get(id);
        if(bodyArray == null) bodyArray = new Array<>();
        bodyArray.add(body);
        jointMap.put(object.getProperties().get("prismatic"), bodyArray);

        sprite.setSize(MOVING_SPIKE_WIDTH / PPM, MOVING_SPIKE_HEIGHT / PPM);
        baseSpirte.setSize(32 / PPM, 8 / PPM);
    }

    public void draw(Batch batch) {
        // draw base
        baseSpirte.draw(batch);

        // attach moving spike sprite to body
        sprite.setPosition(body.getPosition().x - MOVING_SPIKE_WIDTH / 2 / PPM, body.getPosition().y - MOVING_SPIKE_HEIGHT / 2 / PPM);
        sprite.draw(batch);
    }

    public boolean isInBalancePosition() {
        return inBalancePosition;
    }

    public void setInBalancePosition(boolean inBalancePosition) {
        this.inBalancePosition = inBalancePosition;
        Gdx.app.log("MovingSpike", "inBalancePosition = " + inBalancePosition + " - " +  this);
    }

    public int getId() {
        return id;
    }

    public float getUpperTranslationA() {
        return upperTranslationA;
    }

    public float getUpperTranslationB() {
        return upperTranslationB;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(float timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public Sprite getBaseSpirte() {
        return baseSpirte;
    }

    public Sprite getStickSpirte() {
        return stickSpirte;
    }
}
