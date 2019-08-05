package com.robot.game.util.raycast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.Enemy;
import com.robot.game.entities.Robot;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;

public class LaserHandler {

    private PlayScreen playScreen;
    private Assets assets;
    private Robot robot;
    private World world;
    private MyRayCastCallback callback;

    private Vector2 rayPointStart = new Vector2(), rayPointEnd = new Vector2();
    private Fixture closestFixture;
    private Vector2 tempRayPointEnd = new Vector2(); // used to lerp from start point to end point

    // boolean used to determine if laser line should be drawn
    private boolean rayCastStarted;

    // animation
    private float rayCastStartTime;
    private float rayCastElapsed;
    private boolean rayAnimActive; // boolean used to determine if laser hit animation should be drawn

    public LaserHandler(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.robot = playScreen.getRobot();
        this.world = playScreen.getWorld();
        this.callback = robot.getCallback();
    }

    public void startRayCast() {
        rayCastStartTime = TimeUtils.nanoTime();
        rayCastStarted = true;
        rayAnimActive = true;

        determineRayPointStart();

        // start from rayPointStart (and will lerp until rayPointEnd)
        tempRayPointEnd.set(rayPointStart);

        // execute the raycast
        world.rayCast(callback, rayPointStart, rayPointEnd);

        this.closestFixture = callback.getClosestFixture();
        this.rayPointEnd = callback.getRayPointEnd();
        callback.closestFixture = null;

        // determine action depending on the result of the raycast
        resolveRayCast();
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // draw rectangle line
        if(rayCastStarted) {
            shapeRenderer.setProjectionMatrix(playScreen.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            //            shapeRenderer.setColor(Color.CYAN);

            if(robot.facing == Robot.Facing.RIGHT) {
                tempRayPointEnd.add(2f, 0);
                if(tempRayPointEnd.x > rayPointEnd.x + SCREEN_WIDTH / 2f / PPM)
                    rayCastStarted = false;

                // lerp from start point to end point.
                // If tempRayPointEnd exceeds actual end point, draw the actual end point, otherwise draw the temporary end point, which is between the start and end
                /*shapeRenderer.rectLine(robot.getBody().getPosition().add(ROBOT_BODY_WIDTH / 2 / PPM, 0),
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd : tempRayPointEnd, 2 / PPM);*/
                shapeRenderer.rectLine(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM,
                        robot.getBody().getPosition().y,
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd.x: tempRayPointEnd.x,
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd.y: tempRayPointEnd.y,
                        2 / PPM,
                        Color.GREEN,
                        Color.CYAN);

                /*shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rectLine(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM,
                        robot.getBody().getPosition().y + 1f / PPM,
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd.x: tempRayPointEnd.x,
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd.y + 1f / PPM : tempRayPointEnd.y + 1f / PPM,
                        0.5f / PPM);
                shapeRenderer.rectLine(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM,
                        robot.getBody().getPosition().y - 1f / PPM,
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd.x: tempRayPointEnd.x,
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd.y - 1f / PPM : tempRayPointEnd.y - 1f / PPM,
                        0.5f / PPM);*/
            }
            else if(robot.facing == Robot.Facing.LEFT) {
                tempRayPointEnd.sub(2f, 0);
                if(tempRayPointEnd.x < rayPointEnd.x - SCREEN_WIDTH / 2f / PPM)
                    rayCastStarted = false;

                /*shapeRenderer.rectLine(robot.getBody().getPosition().sub(ROBOT_BODY_WIDTH / 2 / PPM, 0),
                        tempRayPointEnd.x < rayPointEnd.x ? rayPointEnd : tempRayPointEnd, 2 / PPM);*/
                shapeRenderer.rectLine(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM,
                        robot.getBody().getPosition().y,
                        tempRayPointEnd.x < rayPointEnd.x ? rayPointEnd.x: tempRayPointEnd.x,
                        tempRayPointEnd.x < rayPointEnd.x ? rayPointEnd.y: tempRayPointEnd.y,
                        2 / PPM,
                        Color.GREEN,
                        Color.CYAN);
            }
            shapeRenderer.end();
        }

        // draw animation of hit target
        handleAnimation(batch);
    }

    private void determineRayPointStart() {
        if(robot.facing == Robot.Facing.RIGHT) {
            rayPointStart.set(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y);
            rayPointEnd.set(rayPointStart.x + SCREEN_WIDTH / PPM, robot.getBody().getPosition().y);
        }
        else if(robot.facing == Robot.Facing.LEFT) {
            rayPointStart.set(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y);
            rayPointEnd.set(rayPointStart.x - SCREEN_WIDTH / PPM, robot.getBody().getPosition().y);
        }
    }

    private void resolveRayCast() {
        if(closestFixture == null) return;
        if(closestFixture.getUserData() == null) return;

        if("ground".equals(closestFixture.getUserData())) {
            Gdx.app.log("Robot", "Raycast hit ground");
        }
        else if(closestFixture.getUserData() instanceof Enemy) {

            Enemy enemy = (Enemy) closestFixture.getUserData();

            enemy.setDead(true);
            enemy.setFlagToKill();

            // if following a path, disable it
            if (enemy.isAiPathFollowing()) {
                enemy.getFollowPath().setEnabled(false);
            }

            // stop enemy
            enemy.getBody().setLinearVelocity(0, 0);

            // set enemy's mask bits to "nothing"
            StaticMethods.setMaskBit(closestFixture, NOTHING_MASK);

            // increase points
            StaticMethods.increaseScore(robot, enemy);

            // add enemy (damaging object) to the HashMap in order to render the points gained
            playScreen.getFeedbackRenderer().getPointsForEnemyToDraw().put(enemy, 1f);
            Gdx.app.log("Robot", "Raycast hit enemy");
        }
    }

    private void handleAnimation(SpriteBatch batch) {
        if(rayAnimActive) {
            if(rayCastElapsed > 0.2f) {
                rayAnimActive = false;
                rayCastStartTime = 0;
                rayCastElapsed = 0;
            }
            else {
                batch.begin();
                batch.setProjectionMatrix(playScreen.getCamera().combined);
                batch.draw(assets.laserAssets.laserExplosionAnimation.getKeyFrame(rayCastElapsed),
                        rayPointEnd.x - 64f / 2 / PPM,
                        rayPointEnd.y - 64f / 2 / PPM,
                        64f / PPM,
                        64f / PPM);
                batch.end();

                rayCastElapsed = (TimeUtils.nanoTime() - rayCastStartTime) * MathUtils.nanoToSec;
            }
        }
    }
}
