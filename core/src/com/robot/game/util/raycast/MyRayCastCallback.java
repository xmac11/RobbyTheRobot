package com.robot.game.util.raycast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.robot.game.interactiveObjects.ladder.Ladder;
import com.robot.game.interactiveObjects.tankBalls.TankBall;

public class MyRayCastCallback implements RayCastCallback {

    private Vector2 rayPointEnd = new Vector2();
    public Fixture closestFixture;

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        // if ray intersects with ladder or tank ball, ignore it
        if(fixture.getUserData() instanceof Ladder || fixture.getUserData() instanceof TankBall) {
            return 1;
        }
        this.rayPointEnd.set(point);
        this.closestFixture = fixture;
        return fraction;
    }

    public Vector2 getRayPointEnd() {
        return rayPointEnd;
    }

    public Fixture getClosestFixture() {
        return closestFixture;
    }
}