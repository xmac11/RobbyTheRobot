package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.robot.game.sprites.*;

import static com.robot.game.util.Constants.*;

public class StaticMethods {

    // increase score depending on enemy killed
    public static void increaseScore(Robot robot, Enemy enemy) {
        robot.getCheckpointData().increaseScore( StaticMethods.getPointsForEnemy(enemy) );
    }

    // decrease robot's health depending on the enemy that hit it
    public static void decreaseHealth(Robot robot, Enemy enemy) {
        robot.getCheckpointData().decreaseHealth( StaticMethods.getDamageFromEnemy(enemy) );
    }

    public static void increaseHealth(Robot robot, PowerUp powerUp) {
        if(powerUp.isFullHeal())
            robot.getCheckpointData().setHealth(100);
        else
            robot.getCheckpointData().increaseHealth(HEALTH_FOR_POWERUP);
    }

    // get the number of points that should be added depending on the enemy killed
    public static int getPointsForEnemy(Enemy enemy) {
        return enemy instanceof Bat ? POINTS_FOR_BAT : POINTS_FOR_CRAB;
    }

    // get the damage depending on the enemy that hit it
    public static int getDamageFromEnemy(Enemy enemy) {
        return enemy instanceof Bat ? DAMAGE_FROM_BAT : DAMAGE_FROM_CRAB;
    }

    // increase score depending on item collected
    public static void increaseScore(Robot robot, Collectable collectable) {
        robot.getCheckpointData().increaseScore( StaticMethods.getPointsForCollectable(collectable) );
    }

    // get the number of points that should be added depending on the item collected
    public static int getPointsForCollectable(Collectable collectable) {
        return collectable instanceof Burger ? POINTS_FOR_BURGER : 0;
    }

    public static void queueForPointsRenderer(Robot robot, Collectable collectable) {
        // if collectable is a burger put the value 1 (alpha), else put the robot's initial health (before increasing it)
        float value = collectable instanceof Burger ? 1 : (float) robot.getCheckpointData().getHealth();
        robot.getPlayScreen().getPointsRenderer().getItemPointsToDraw().put(collectable, value);
    }

    // setter to change the mask bits of a fixture
    public static void setMaskBit(Fixture fixture, short maskBits) {
        Filter filter = new Filter();
        filter.maskBits = maskBits;
        fixture.setFilterData(filter);
        Gdx.app.log("StaticMethods", "Mask bits changed");
    }

    // setter to change the category bits of a fixture
    public static void setCategoryBit(Fixture fixture, short categoryBits) {
        Filter filter = fixture.getFilterData();
        filter.categoryBits = categoryBits;
        fixture.setFilterData(filter);
        Gdx.app.log("StaticMethods", "Category bits changed");
    }
}
