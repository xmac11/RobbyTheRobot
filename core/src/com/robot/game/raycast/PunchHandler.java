package com.robot.game.raycast;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.screens.playscreens.PlayScreen;

import static com.robot.game.util.constants.Constants.*;
import static com.robot.game.util.constants.Enums.Facing.*;

public class PunchHandler extends RayCastHandler {

    public PunchHandler(PlayScreen playScreen) {
        super(playScreen);
    }

    @Override
    public void executeRayCast() {
        rayCastActive = true;

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

        // play appropriate punch sound
        if(!playScreen.isMuted()) {
            if(closestFixture != null && closestFixture.getUserData() != null && closestFixture.getUserData() instanceof Enemy) {
                assets.soundAssets.punchEnemySound.play(0.4f);
            }
            else {
                assets.soundAssets.punchAirSound.play(0.3f);
            }
        }

        // determine action depending on the result of the raycast
        super.resolveRayCast(PUNCH_IMPULSE_X + Math.abs(robot.getBody().getLinearVelocity().x) / 2, PUNCH_IMPULSE_Y);
    }

    @Override
    public void determineRayPoints() {
        // start raycast from the opposite edge of where the robot is facing
        // this is in order to be able to kill an enemy when the robot overlaps with the enemy

        // facing right, start raycast from the left edge of the robot
        if(robot.getFacing() == RIGHT) {
            rayPointStart.set(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y + PUNCH_OFFSET_Y);
            rayPointEnd.set(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM + PUNCH_RANGE, robot.getBody().getPosition().y + PUNCH_OFFSET_Y); // raycast 24 pixels to the right
        }
        // facing left, start raycast from the right edge of the robot
        else if(robot.getFacing() == LEFT) {
            rayPointStart.set(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y + PUNCH_OFFSET_Y);
            rayPointEnd.set(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM - PUNCH_RANGE, robot.getBody().getPosition().y + PUNCH_OFFSET_Y); // raycast 24 pixels to the left
        }
    }

    // method is used only on debug mode
    public void render(ShapeRenderer shapeRenderer) {
        // draw rectangle line
        if(rayCastActive) {
            shapeRenderer.setProjectionMatrix(playScreen.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);

            // facing right
            if(robot.getFacing() == RIGHT) {
                tempRayPointEnd.add(4f / PPM, 0);
                if(tempRayPointEnd.x > rayPointEnd.x) {
                    rayCastActive = false;
                }

                // lerp from start point to end point
                // If tempRayPointEnd exceeds actual end point, draw the actual end point, otherwise draw the temporary end point, which is between the start and end
                shapeRenderer.line(rayPointStart, tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd : tempRayPointEnd);
            }
            // facing left
            else if(robot.getFacing() == LEFT) {
                tempRayPointEnd.sub(4f / PPM, 0);
                if(tempRayPointEnd.x < rayPointEnd.x) {
                    rayCastActive = false;
                }

                // lerp from start point to end point
                // If tempRayPointEnd exceeds actual end point, draw the actual end point, otherwise draw the temporary end point, which is between the start and end
                shapeRenderer.line(rayPointStart, tempRayPointEnd.x < rayPointEnd.x ? rayPointEnd : tempRayPointEnd);
            }

            // reset end point to zero so that it does not interfere with the laser end point
            if(!rayCastActive) {
                callback.getRayPointEnd().set(0, 0);
            }
            shapeRenderer.end();
        }
    }
}
