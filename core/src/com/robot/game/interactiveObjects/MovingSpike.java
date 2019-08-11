package com.robot.game.interactiveObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.PPM;

public class MovingSpike extends Spike {

    private Sprite sprite;
    private boolean inBalancePosition;
    private boolean attacking;
    private int id;
    private float upperTranslationA;
    private float upperTranslationB;
    private float timeElapsed;

    public MovingSpike(Body body, FixtureDef fixtureDef, MapObject object, ObjectMap jointMap, Assets assets) {
        super(body, fixtureDef, object);
        this.sprite = new Sprite(assets.trapAssets.trapTexture);
        this.inBalancePosition = true;

        this.upperTranslationA = (float) object.getProperties().get("upperTranslationA") / PPM;
        this.upperTranslationB = (float) object.getProperties().get("upperTranslationB") / PPM;

        this.id = (int) object.getProperties().get("prismatic");
        Array<Body> bodyArray = (Array) jointMap.get(id);
        if(bodyArray == null) bodyArray = new Array<>();
        bodyArray.add(body);
        jointMap.put(object.getProperties().get("prismatic"), bodyArray);

        sprite.setSize(64 / PPM, 32 / PPM);
    }

    public void draw(Batch batch) {
        // attach enemy sprite to body
        sprite.setPosition(body.getPosition().x - 64f / 2 / PPM, body.getPosition().y - 32f / 2 / PPM);
        sprite.draw(batch);
    }

    public boolean isInBalancePosition() {
        return inBalancePosition;
    }

    public void setInBalancePosition(boolean inBalancePosition) {
        this.inBalancePosition = inBalancePosition;
        Gdx.app.log("MovingSpike", "inBalancePosition = " + inBalancePosition);
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
}
