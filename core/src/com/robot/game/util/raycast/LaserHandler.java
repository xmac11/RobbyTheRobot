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
import com.robot.game.entities.Robot;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Enums.Facing.LEFT;
import static com.robot.game.util.Enums.Facing.RIGHT;


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
    private boolean rayCastActive;

    // animation
    private float rayCastStartTime;
    private float rayCastElapsed;
    private boolean rayHitAnimActive; // boolean used to determine if laser hit animation should be drawn

    public LaserHandler(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.robot = playScreen.getRobot();
        this.world = playScreen.getWorld();
        this.callback = robot.getCallback();
    }

    public void startRayCast() {
        rayCastStartTime = TimeUtils.nanoTime();
        rayCastActive = true;
        rayHitAnimActive = true;

        determineRayPoints();

        // start from rayPointStart (and will lerp until rayPointEnd)
        tempRayPointEnd.set(rayPointStart);

        /*if(rayPointEnd.isZero()) {
            if(robot.facing == Robot.Facing.RIGHT) {
                rayPointStart.set(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y);
                rayPointEnd.set(rayPointStart.x + SCREEN_WIDTH / PPM, robot.getBody().getPosition().y);
            }
            else if(robot.facing == Robot.Facing.LEFT) {
                rayPointStart.set(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y);
                rayPointEnd.set(rayPointStart.x - SCREEN_WIDTH / PPM, robot.getBody().getPosition().y);
            }
        }*/

        // execute the raycast
        world.rayCast(callback, rayPointStart, rayPointEnd);

        this.closestFixture = callback.getClosestFixture();
        if(!callback.getRayPointEnd().isZero()) {
            this.rayPointEnd = callback.getRayPointEnd();
        }
        callback.setClosestFixture(null);

        // determine action depending on the result of the raycast
        resolveRayCast();
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // draw rectangle line
        if(rayCastActive) {
            shapeRenderer.setProjectionMatrix(playScreen.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            //            shapeRenderer.setColor(Color.CYAN);

            if(robot.getFacing() == RIGHT) {
                tempRayPointEnd.add(2f, 0);
                // give a higher range, just to draw the line
                if(tempRayPointEnd.x > rayPointEnd.x + playScreen.getViewport().getWorldWidth() / 2) {
                    rayCastActive = false;
                }

                // lerp from start point to end point.
                // If tempRayPointEnd exceeds actual end point, draw the actual end point, otherwise draw the temporary end point, which is between the start and end
                /*shapeRenderer.rectLine(robot.getBody().getPosition().add(ROBOT_BODY_WIDTH / 2 / PPM, 0),
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd : tempRayPointEnd, 2 / PPM);*/
                // I'm not using rayPointStart, in order to always draw the laser based on the robot's current position
                shapeRenderer.rectLine(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM + LASER_OFFSET_X,
                        robot.getBody().getPosition().y + LASER_OFFSET_Y,
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
            else if(robot.getFacing() == LEFT) {
                tempRayPointEnd.sub(2f, 0);
                // give a higher range, just to draw the line
                if(tempRayPointEnd.x < rayPointEnd.x - playScreen.getViewport().getWorldWidth() / 2) {
                    rayCastActive = false;
                }

                /*shapeRenderer.rectLine(robot.getBody().getPosition().sub(ROBOT_BODY_WIDTH / 2 / PPM, 0),
                        tempRayPointEnd.x < rayPointEnd.x ? rayPointEnd : tempRayPointEnd, 2 / PPM);*/
                // I'm not using rayPointStart, in order to always draw the laser based on the robot's current position
                shapeRenderer.rectLine(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM - LASER_OFFSET_X,
                        robot.getBody().getPosition().y + LASER_OFFSET_Y,
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

    private void determineRayPoints() {
        // facing right
        if(robot.getFacing() == RIGHT) {
            rayPointStart.set(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM + LASER_OFFSET_X, robot.getBody().getPosition().y + LASER_OFFSET_Y);
            rayPointEnd.set(playScreen.getCamera().position.x + playScreen.getViewport().getWorldWidth() / 2, robot.getBody().getPosition().y + LASER_OFFSET_Y); // raycast until the end of the screen
        }
        // facing left
        else if(robot.getFacing() == LEFT) {
            rayPointStart.set(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM - LASER_OFFSET_X, robot.getBody().getPosition().y + LASER_OFFSET_Y);
            rayPointEnd.set(playScreen.getCamera().position.x - playScreen.getViewport().getWorldWidth() / 2, robot.getBody().getPosition().y + LASER_OFFSET_Y); // raycast until the end of the screen
        }
    }

    private void resolveRayCast() {
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

    private void handleAnimation(SpriteBatch batch) {
        if(rayHitAnimActive) {
            if(rayCastElapsed >= assets.laserAssets.laserExplosionAnimation.getAnimationDuration()) {
                rayHitAnimActive = false;
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
