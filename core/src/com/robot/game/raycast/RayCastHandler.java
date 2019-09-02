package com.robot.game.raycast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.entities.Monster;
import com.robot.game.entities.Robot;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.screens.playscreens.PlayScreen;
import com.robot.game.Assets;
import com.robot.game.util.staticMethods.StaticMethods;

import static com.robot.game.util.constants.Constants.WALL_JUMPING_PROPERTY;

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

    protected void resolveRayCast(float impulseX, float impulseY) {
        if(closestFixture == null) return;
        if(closestFixture.getUserData() == null) return;

        if("ground".equals(closestFixture.getUserData())) {
            Gdx.app.log("RayCastHandler", "Raycast hit ground");
        }
        else if(WALL_JUMPING_PROPERTY.equals(closestFixture.getUserData())) {
            Gdx.app.log("RayCastHandler", "Raycast hit wall jumping surface");
        }
        else if(closestFixture.getUserData() instanceof Enemy) {
            Enemy enemy = (Enemy) closestFixture.getUserData();

            // for the case that the enemy overlaps with the robot
            // in this case, the enemy will have become a sensor with zero gravity
            if(enemy.getBody().getFixtureList().size != 0) {
                enemy.getBody().getFixtureList().first().setSensor(false);
            }
            Gdx.app.log("RayCastHandler", "Enemy sensor = FALSE");

            // if enemy is a Monster (dynamic body) turn gravity back on
            if(enemy instanceof Monster && enemy.getBody().getGravityScale() == 0) {
                enemy.getBody().setGravityScale(1);
                Gdx.app.log("RayCastHandler", "Gravity was turned back on for the Monster");
            }

            StaticMethods.killEnemy(robot, enemy, impulseX, impulseY);
            Gdx.app.log("RayCastHandler", "Raycast hit enemy");
        }
    }

    public void setToNull() {
        robot = null;
        callback = null;
        rayPointStart = null;
        rayPointEnd = null;
        closestFixture = null;
        tempRayPointEnd = null;
        playScreen = null;
        Gdx.app.log("RayCastHandler", "Objects were set to null");
    }
}

