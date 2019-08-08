package com.robot.game.util.raycast;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Constants.LASER_OFFSET_Y;
import static com.robot.game.util.Enums.Facing.LEFT;
import static com.robot.game.util.Enums.Facing.RIGHT;

public class PunchHandler extends RayCastHandler {

    public PunchHandler(PlayScreen playScreen) {
        super(playScreen);
    }

    @Override
    public void startRayCast() {
        rayCastActive = true;

        determineRayPoints();

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
        super.resolveRayCast();
    }

    @Override
    public void determineRayPoints() {
        // facing right
        if(robot.getFacing() == RIGHT) {
            rayPointStart.set(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y + 4 / PPM);
            rayPointEnd.set(robot.getBody().getPosition().x + ROBOT_BODY_WIDTH / 2 / PPM + 16 / PPM, robot.getBody().getPosition().y + 4 / PPM); // raycast 16 pixels to the right
        }
        // facing left
        else if(robot.getFacing() == LEFT) {
            rayPointStart.set(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM, robot.getBody().getPosition().y + 4 / PPM);
            rayPointEnd.set(robot.getBody().getPosition().x - ROBOT_BODY_WIDTH / 2 / PPM - 16 / PPM, robot.getBody().getPosition().y + 4 / PPM); // raycast 16 pixels to the left
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        // draw rectangle line
        if(rayCastActive) {
            shapeRenderer.setProjectionMatrix(playScreen.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);

            if(robot.getFacing() == RIGHT) {
                tempRayPointEnd.add(2f / PPM, 0);
                // give a higher range, just to draw the line
                if(tempRayPointEnd.x > rayPointEnd.x) {
                    rayCastActive = false;
                }

                // lerp from start point to end point.
                shapeRenderer.line(rayPointStart, tempRayPointEnd.x > rayPointEnd.x ? rayPointEnd : tempRayPointEnd);
                // If tempRayPointEnd exceeds actual end point, draw the actual end point, otherwise draw the temporary end point, which is between the start and end
            }
            else if(robot.getFacing() == LEFT) {
                tempRayPointEnd.sub(2f / PPM, 0);
                // give a higher range, just to draw the line
                if(tempRayPointEnd.x < rayPointEnd.x) {
                    rayCastActive = false;
                }

                shapeRenderer.line(rayPointStart, tempRayPointEnd.x < rayPointEnd.x ? rayPointEnd : tempRayPointEnd);
            }
            shapeRenderer.end();
        }
    }
}
