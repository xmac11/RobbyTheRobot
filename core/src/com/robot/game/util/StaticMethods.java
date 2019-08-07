package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.entities.bat.BatAI;
import com.robot.game.entities.bat.BatPatrolling;
import com.robot.game.entities.crab.CrabAI;
import com.robot.game.entities.crab.CrabPatrolling;
import com.robot.game.interactiveObjects.collectables.Burger;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.interactiveObjects.collectables.PowerUp;
import com.robot.game.entities.*;

import static com.robot.game.util.Constants.*;

public class StaticMethods {

    // increase score depending on enemy killed
    public static void increaseScore(Robot robot, Enemy enemy) {
        robot.getCheckpointData().increaseScore( StaticMethods.getPointsForEnemy(enemy) );
    }

    // decrease robot's health depending on the enemy that hit it
    public static void decreaseHealth(Robot robot, Damaging damaging) {
        robot.getCheckpointData().decreaseHealth(damaging.getDamage());
    }

    public static void increaseHealth(Robot robot, PowerUp powerUp) {
        if(powerUp.isFullHeal())
            robot.getCheckpointData().setHealth(100);
        else
            robot.getCheckpointData().increaseHealth(HEALTH_FOR_POWERUP);
    }

    // get the number of points that should be added depending on the enemy killed
    public static int getPointsForEnemy(Enemy enemy) {
        if(enemy instanceof BatAI || enemy instanceof BatPatrolling)
            return POINTS_FOR_BAT;
        else if(enemy instanceof CrabAI || enemy instanceof CrabPatrolling)
            return POINTS_FOR_CRAB;
        else //if(enemy instanceof Fish)
            return POINTS_FOR_FISH;
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
        robot.getPlayScreen().getFeedbackRenderer().getItemPointsToDraw().put(collectable, value);
    }

    // setter to change the mask bits of a fixture
    public static void setMaskBit(Fixture fixture, short maskBits) {
        Filter filter = new Filter();
        filter.maskBits = maskBits;
        fixture.setFilterData(filter);
        Gdx.app.log("StaticMethods", "Mask bits changed");
        System.out.println(filter.maskBits);
    }

    // setter to change the category bits of a fixture
    public static void setCategoryBit(Fixture fixture, short categoryBits) {
        Filter filter = fixture.getFilterData();
        filter.categoryBits = categoryBits;
        fixture.setFilterData(filter);
        Gdx.app.log("StaticMethods", "Category bits changed");
    }
}
