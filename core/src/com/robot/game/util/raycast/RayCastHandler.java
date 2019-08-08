package com.robot.game.util.raycast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.entities.Robot;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.StaticMethods;

public abstract class RayCastHandler {

    protected PlayScreen playScreen;
    protected Assets assets;
    protected Robot robot;
    protected World world;
    protected MyRayCastCallback callback;

    protected Vector2 rayPointStart = new Vector2(), rayPointEnd = new Vector2();
    protected Fixture closestFixture;

    protected Vector2 tempRayPointEnd = new Vector2(); // used to lerp from start point to end point

    // boolean used to determine if laser line should be drawn
    protected boolean rayCastActive;

    public RayCastHandler(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.robot = playScreen.getRobot();
        this.world = playScreen.getWorld();
        this.callback = robot.getCallback();
    }

    public abstract void startRayCast();
    public abstract void determineRayPoints();

    protected void resolveRayCast() {
        if(closestFixture == null) return;
        if(closestFixture.getUserData() == null) return;

        if("ground".equals(closestFixture.getUserData())) {
            Gdx.app.log("LaserHandler", "Raycast hit ground");
        }
        else if(closestFixture.getUserData() instanceof Enemy) {
            Enemy enemy = (Enemy) closestFixture.getUserData();
            StaticMethods.killEnemy(robot, enemy);
            Gdx.app.log("LaserHandler", "Raycast hit enemy");
        }
    }
}

