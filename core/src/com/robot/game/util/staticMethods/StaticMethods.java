package com.robot.game.util.staticMethods;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.robot.game.entities.Robot;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.entities.abstractEnemies.EnemySeekAI;
import com.robot.game.entities.abstractEnemies.EnemyPathFollowingAI;
import com.robot.game.entities.bat.BatPathFollowingAI;
import com.robot.game.entities.bat.BatPatrolling;
import com.robot.game.entities.crab.CrabPathFollowingAI;
import com.robot.game.entities.crab.CrabPatrolling;
import com.robot.game.interactiveObjects.collectables.Food;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.interactiveObjects.collectables.PowerUp;
import com.robot.game.interfaces.Damaging;

import static com.robot.game.util.constants.Constants.*;
import static com.robot.game.util.constants.Enums.Facing;
import static com.robot.game.util.constants.Enums.Facing.*;

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
        if(enemy instanceof BatPathFollowingAI || enemy instanceof BatPatrolling)
            return POINTS_FOR_BAT;
        else if(enemy instanceof CrabPathFollowingAI || enemy instanceof CrabPatrolling)
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
        return collectable instanceof Food ? POINTS_FOR_FOOD : 0;
    }

    public static void queueForPointsRenderer(Robot robot, Collectable collectable) {
        // if collectable is a burger put the value 1 (alpha), else put the robot's initial health (before increasing it)
        float value = collectable instanceof Food ? 1 : (float) robot.getCheckpointData().getHealth();
        robot.getPlayScreen().getFeedbackRenderer().getItemPointsToDraw().put(collectable, value);
    }

    // setter to change the mask bits of a fixture
    public static void setMaskBit(Fixture fixture, short maskBits) {
        Filter filter = fixture.getFilterData();
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

    public static void killEnemy(Robot robot, Enemy enemy, float impulseX, float impulseY) {
        enemy.setDead(true);
        enemy.setFlagToKill();
        enemy.setFlagToChangeMask(true);

        // stop enemy
        enemy.getBody().setLinearVelocity(0, 0);

        // for chasing monsters apply impulse when killed based on robot's facing direction
        if(enemy instanceof EnemySeekAI) {
            enemy.getBody().applyLinearImpulse(robot.getFacing() == RIGHT ? impulseX : -impulseX, impulseY,
                    enemy.getBody().getWorldCenter().x, enemy.getBody().getWorldCenter().y, true);
        }


        // increase points
        StaticMethods.increaseScore(robot, enemy);

        // add enemy (damaging object) to the HashMap in order to render the points gained
        robot.getPlayScreen().getFeedbackRenderer().getPointsForEnemyToDraw().put(enemy, 1f);
    }

    public static void checkToFlipTexture(Sprite sprite, Facing facing) {
        if(facing == RIGHT) {
            if(sprite.isFlipX())
                sprite.flip(true, false);
        }
        else if(facing == LEFT) {
            if(!sprite.isFlipX())
                sprite.flip(true, false);
        }
    }

    public static Body getStaticBodyOfJoint(PrismaticJoint joint) {
        if(joint.getBodyA().getType() == BodyDef.BodyType.StaticBody)
            return joint.getBodyA();
        else
            return joint.getBodyB();
    }
}
