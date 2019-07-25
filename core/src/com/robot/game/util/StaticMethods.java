package com.robot.game.util;

import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.robot.game.sprites.Bat;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Constants.DAMAGE_FROM_CRAB;

public class StaticMethods {

    public static void increaseScore(Robot robot, Enemy enemy) {
        robot.getCheckpointData().increaseScore( StaticMethods.getPointsForEnemy(enemy) );
    }

    public static void decreaseHealth(Robot robot, Enemy enemy) {
        robot.getCheckpointData().decreaseHealth( StaticMethods.getDamageFromEnemy(enemy) );
    }

    public static int getPointsForEnemy(Enemy enemy) {
        return enemy instanceof Bat ? POINTS_FOR_BAT : POINTS_FOR_CRAB;
    }

    public static int getDamageFromEnemy(Enemy enemy) {
        return enemy instanceof Bat ? DAMAGE_FROM_BAT : DAMAGE_FROM_CRAB;
    }

    public static void setMaskBit(Fixture fixture, short maskBits) {
        Filter filter = new Filter();
        filter.maskBits = maskBits;
        fixture.setFilterData(filter);
    }

    public static void setCategoryBit(Fixture fixture, short categoryBits) {
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        fixture.setFilterData(filter);
    }
}
