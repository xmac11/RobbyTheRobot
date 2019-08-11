package com.robot.game.interactiveObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.util.Damaging;

import static com.robot.game.util.Constants.DAMAGE_FROM_SPIKE;
import static com.robot.game.util.Constants.DAMAGE_ON;

public class Spike implements Damaging {

    protected Body body;
    private boolean mightWalk;
    private Vector2 respawnLocation;

    public Spike(Body body, FixtureDef fixtureDef, MapObject object) {
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
        return DAMAGE_ON ? DAMAGE_FROM_SPIKE : 0;
    }

    public boolean mightBeWalked() {
        return mightWalk;
    }

    public Vector2 getRespawnLocation() {
        return respawnLocation;
    }
}
