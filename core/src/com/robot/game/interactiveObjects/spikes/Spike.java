package com.robot.game.interactiveObjects.spikes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.playscreens.PlayScreen;
import com.robot.game.interfaces.Damaging;

import static com.robot.game.util.constants.Constants.DAMAGE_FROM_SPIKE;

public class Spike implements Damaging {

    protected PlayScreen playScreen;
    protected Body body;
    private boolean mightWalk;

    public Spike(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        this.playScreen = playScreen;
        this.body = body;
        this.mightWalk = (boolean) object.getProperties().get("mightWalk");

        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public int getDamage() {
        return playScreen.isDamageON() ? DAMAGE_FROM_SPIKE : 0;
    }

    public boolean mightBeWalked() {
        return mightWalk;
    }

    public void setToNull() {
        playScreen = null;
        Gdx.app.log("Spike", "Objects were set to null");
    }
}
