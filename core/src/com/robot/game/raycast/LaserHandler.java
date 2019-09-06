package com.robot.game.raycast;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.screens.playscreens.PlayScreen;

import static com.robot.game.util.constants.Constants.*;
import static com.robot.game.util.constants.Enums.Facing.LEFT;
import static com.robot.game.util.constants.Enums.Facing.RIGHT;


public class LaserHandler extends RayCastHandler {

    // animation
    private float rayCastStartTime;
    private float rayCastElapsed;
    private boolean rayHitAnimActive; // boolean used to determine if laser hit animation should be drawn

    public LaserHandler(PlayScreen playScreen) {
        super(playScreen);
    }

    @Override
    public void executeRayCast() {
        // play laser sound
        if(!playScreen.isMuted()) {
            assets.soundAssets.laserSound.play(0.3f);
        }

        rayCastStartTime = TimeUtils.nanoTime();
        rayCastActive = true;
        rayHitAnimActive = true;

        // determine start and end points of the ray
        this.determineRayPoints();

        // start from rayPointStart (and will lerp until rayPointEnd)
        tempRayPointEnd.set(rayPointStart);

        // execute the raycast
        world.rayCast(callback, rayPointStart, rayPointEnd);

        this.closestFixture = callback.getClosestFixture();
        if(!callback.getRayPointEnd().isZero()) {
            this.rayPointEnd = callback.getRayPointEnd();
        }
        callback.setClosestFixture(null);

        // determine action depending on the result of the raycast
        super.resolveRayCast(LASER_IMPULSE_X, LASER_IMPULSE_Y);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // draw rectangle line
        if(rayCastActive) {

            float startX = 0;
            float startY = 0;

            shapeRenderer.setProjectionMatrix(playScreen.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            if(robot.getFacing() == RIGHT) {
                tempRayPointEnd.add(64f / PPM, 0);
                // give a higher range, just to draw the line
                if(tempRayPointEnd.x > rayPointEnd.x + playScreen.getViewport().getWorldWidth() / 2) {
                    rayCastActive = false;
                }

                // lerp from start point to end point.
                // If tempRayPointEnd exceeds actual end point, draw the actual end point, otherwise draw the temporary end point, which is between the start and end
                startX = robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM + LASER_OFFSET_X;
                startY = robot.getBody().getPosition().y + LASER_OFFSET_Y;
                // I'm not using rayPointStart, in order to always draw the laser based on the robot's current position
                shapeRenderer.rectLine(startX, startY,
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd.x: tempRayPointEnd.x,
                        tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd.y: tempRayPointEnd.y,
                        2 / PPM,
                        Color.GREEN,
                        Color.CYAN);
            }
            else if(robot.getFacing() == LEFT) {
                tempRayPointEnd.sub(64f / PPM, 0);
                // give a higher range, just to draw the line
                if(tempRayPointEnd.x < rayPointEnd.x - playScreen.getViewport().getWorldWidth() / 2) {
                    rayCastActive = false;
                }

                // lerp from start point to end point.
                // If tempRayPointEnd exceeds actual end point, draw the actual end point, otherwise draw the temporary end point, which is between the start and end
                startX = robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM - LASER_OFFSET_X;
                startY = robot.getBody().getPosition().y + LASER_OFFSET_Y;
                // I'm not using rayPointStart, in order to always draw the laser based on the robot's current position
                shapeRenderer.rectLine(startX, startY,
                        tempRayPointEnd.x < rayPointEnd.x ? rayPointEnd.x: tempRayPointEnd.x,
                        tempRayPointEnd.x < rayPointEnd.x ? rayPointEnd.y: tempRayPointEnd.y,
                        2 / PPM,
                        Color.GREEN,
                        Color.CYAN);
            }
            shapeRenderer.end();

            // render Light
            playScreen.getPointLight().setPosition(startX, startY);
            playScreen.getRayHandler().setCombinedMatrix(playScreen.getCamera());
            playScreen.getRayHandler().updateAndRender();
        }

        // draw animation of hit target
        handleAnimation(batch);

        // reset end point to zero so that it does not interfere with the punch end point
        if(!callback.getRayPointEnd().isZero() && !rayCastActive && !rayHitAnimActive) {
            callback.getRayPointEnd().set(0, 0);
        }
    }

    @Override
    public void determineRayPoints() {
        // start raycast from the opposite edge of where the robot is facing
        // this is in order to be able to kill an enemy when the robot overlaps with the enemy

        // facing right, start raycast from the left edge of the robot
        if(robot.getFacing() == RIGHT) {
            rayPointStart.set(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y + LASER_OFFSET_Y);
            rayPointEnd.set(playScreen.getCamera().position.x + playScreen.getViewport().getWorldWidth() / 2, robot.getBody().getPosition().y + LASER_OFFSET_Y); // raycast until the end of the screen
        }
        // facing left, start raycast from the right edge of the robot
        else if(robot.getFacing() == LEFT) {
            rayPointStart.set(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y + LASER_OFFSET_Y);
            rayPointEnd.set(playScreen.getCamera().position.x - playScreen.getViewport().getWorldWidth() / 2, robot.getBody().getPosition().y + LASER_OFFSET_Y); // raycast until the end of the screen
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
                batch.setProjectionMatrix(playScreen.getCamera().combined); // can be omitted
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
