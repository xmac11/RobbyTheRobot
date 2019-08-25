package com.robot.game.interactiveObjects.spikes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.playscreens.PlayScreen;
import com.robot.game.util.Damaging;

import static com.robot.game.util.constants.Constants.DAMAGE_FROM_SPIKE;

public class Spike implements Damaging {

    protected PlayScreen playScreen;
    protected Body body;
    private boolean mightWalk;
    private Vector2 respawnLocation;

    public Spike(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        this.playScreen = playScreen;
        this.body = body;
        this.mightWalk = (boolean) object.getProperties().get("mightWalk");

        /*if(mightWalk) {
            int respawnID = (int) object.getProperties().get("respawnID");

            switch(respawnID) {
                case 1:
                    this.respawnLocation = SPIKE_RESPAWN_1;
            }
        }*/

        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public int getDamage() {
        return playScreen.isDamageON() ? DAMAGE_FROM_SPIKE : 0;
    }

    public boolean mightBeWalked() {
        return mightWalk;
    }

    public Vector2 getRespawnLocation() {
        return respawnLocation;
    }

    public void setToNull() {
        playScreen = null;
        Gdx.app.log("Spike", "Objects were set to null");
    }
}
