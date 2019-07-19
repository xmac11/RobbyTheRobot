package com.robot.game.interactiveObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Spike {

    private Body body;
    private FixtureDef fixtureDef;
    private boolean mightWalk;
    private Vector2 respawnLocation;

    public Spike(Body body, FixtureDef fixtureDef, MapObject object) {
        this.body = body;
        this.fixtureDef = fixtureDef;
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

    public boolean mightBeWalked() {
        return mightWalk;
    }

    public Vector2 getRespawnLocation() {
        return respawnLocation;
    }
}
