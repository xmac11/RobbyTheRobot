package com.robot.game.steeringBehaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.robot.game.entities.abstractEnemies.EnemyPathFollowingAI;

import static com.robot.game.util.constants.Constants.PPM;


public class FollowPathBehaviour {

    private EnemyPathFollowingAI enemy;
    private Body body;
    private SeekBehaviour seekBehaviour;
    private Array<Vector2> wayPoints;
    private int targetIndex;
    private Vector2 target = new Vector2();
    private boolean onPath;
    private boolean pathJustReached;
    private boolean initialTargetSet;

    private Vector2 start = new Vector2(), end = new Vector2(); // for the line currently examined
    private Vector2 chosenStart = new Vector2(), chosenEnd = new Vector2(); // for the chosen line to follow
    private Vector2 temp = new Vector2(); // for swapping start/end

    // for the calculateProjection() method
    private Vector2 v1 = new Vector2();
    private Vector2 v2 = new Vector2();

    public FollowPathBehaviour(EnemyPathFollowingAI enemy) {
        this.enemy = enemy;
        this.body = enemy.getBody();
        this.wayPoints = enemy.getWayPoints();
        this.seekBehaviour = new SeekBehaviour(body, enemy.getMaxLinearSpeed());
    }

    public void follow() {
        float distance;
        float minDistance = Float.POSITIVE_INFINITY;

        // check if we are on the path
        if(!onPath && isPointOnPath(body.getPosition())) {
            onPath = true;
            pathJustReached = true;
        }

        if(!onPath && !initialTargetSet) {

            // loop through the path’s waypoints
            for(int i = 0; i < wayPoints.size - 1; i++) {

                // set the start and end point of segment
                start.set(wayPoints.get(i));
                end.set(wayPoints.get(i+1));

                // check if swapping is needed
                if(start.x > end.x || start.y > end.y) {
                    temp.set(start);
                    start.set(end);
                    end.set(temp);
                }

                // calculate projection
                Vector2 projection = calculateProjection(body.getPosition(), start, end);

                // if projection is not on the path, set the vertex closest to the body as the projection
                if(!isPointOnLine(projection, start, end)) {
                    projection.set(body.getPosition().dst(start) < body.getPosition().dst(end) ? start : end);
                }

                // calculate distance between body's position and projection
                distance = body.getPosition().dst(projection);
                // if distance is less than the min distance so far
                if(distance < minDistance) {
                    minDistance = distance;
                    chosenStart.set(start);
                    chosenEnd.set(end);

                    // set target
                    target.set(projection);
                }
            }
            initialTargetSet = true;
        }

        // if the path is reached for the first time, determine next closest vertex
        if(pathJustReached) {
            int closestIndex = 0;
            float minDistanceToVertex = Float.POSITIVE_INFINITY;

            for(int i = 0; i < 4; i++) {
                if(body.getPosition().dst(wayPoints.get(i)) < minDistanceToVertex) {
                    minDistanceToVertex = body.getPosition().dst(wayPoints.get(i));
                    closestIndex = i;
                }
            }
            targetIndex = (closestIndex + determineNextPoint()) % 4; // finds in which 'quadrant' we are and returns 0 or 1
            pathJustReached = false;
        }

        // if we are not on the path, seek the target
        if(!onPath) {
            seekBehaviour.seek(target);
        }
        // else if we are on the path
        else {
            // if we reach have not reached the target, seek it
            if(!waypointReached(wayPoints.get(targetIndex))) {
                target.set(wayPoints.get(targetIndex));
                seekBehaviour.seek(target);
            }
            // else if we reach the target, move to the next one
            else {
                targetIndex = (targetIndex + 1) % 4;
                target.set(wayPoints.get(targetIndex));
                seekBehaviour.seek(target);
            }
        }
        //System.out.println(onPath + " " + pathReached);
    }

    private boolean waypointReached(Vector2 waypoint) {
        return Math.abs(waypoint.x - body.getPosition().x) <= 2 / PPM && Math.abs(waypoint.y - body.getPosition().y) <= 2 / PPM;
    }

    private boolean isPointOnPath(Vector2 point) {
        for(int i = 0; i < wayPoints.size - 1; i++) {
            start.set(wayPoints.get(i));
            end.set(wayPoints.get(i+1));

            // check if swapping is needed
            if(start.x > end.x || start.y > end.y) {
                temp.set(start);
                start.set(end);
                end.set(temp);
            }
            if(isPointOnLine(point, start, end)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPointOnLine(Vector2 point, Vector2 start, Vector2 end) {
        return Math.abs(start.dst(point) + point.dst(end) - start.dst(end)) <= 0.1f;
    }

    private Vector2 calculateProjection(Vector2 point, Vector2 start, Vector2 end) {
        //Vector2 ap = p.cpy().sub(a);
        v1.set(point);
        v1.sub(start);

        //Vector2 ab = b.cpy().sub(a);
        v2.set(end);
        v2.sub(start);
        float scalar = (v1.dot(v2) / v2.len2());

        v1.set(start);
        return v1.add(v2.scl(scalar)); // a.cpy().add(v2.scl(scale));
    }

    // finds in which 'quadrant' we are and returns 0 or 1
    private int determineNextPoint() {
        // we are on a vertical line
        if(chosenStart.x == chosenEnd.x) {
            // upper left
            if(body.getPosition().x < enemy.getPlatformX() + enemy.getPlatformWidth() / 2 && body.getPosition().y > enemy.getPlatformY() + enemy.getPlatformHeight() / 2) {
                return 1;
            }
            // bottom left
            else if(body.getPosition().x < enemy.getPlatformX() + enemy.getPlatformWidth() / 2) {
                return 0;
            }
            // upper right
            else if(body.getPosition().x > enemy.getPlatformX() + enemy.getPlatformWidth() / 2 && body.getPosition().y > enemy.getPlatformY() + enemy.getPlatformHeight() / 2) {
                return 0;
            }
            // bottom right
            else {
                return 1;
            }
        }
        // we are on a horizontal line
        else {
            // upper left
            if(body.getPosition().y > enemy.getPlatformY() + enemy.getPlatformHeight() / 2 && body.getPosition().x < enemy.getPlatformX() + enemy.getPlatformWidth() / 2) {
                return 0;
            }
            // upper right
            else if(body.getPosition().y > enemy.getPlatformY() + enemy.getPlatformHeight() / 2) {
                return 1;
            }
            // bottom left
            else if(body.getPosition().y < enemy.getPlatformY() + enemy.getPlatformHeight() / 2 && body.getPosition().x < enemy.getPlatformX() + enemy.getPlatformWidth() / 2) {
                return 1;
            }
            // bottom right
            else {
                return 0;
            }
        }
    }

    public Vector2 getTarget() {
        return target;
    }

    public void setToNull() {
        seekBehaviour.setToNull();
        seekBehaviour = null;
        wayPoints = null;
        target = null;
        start = end = null;
        chosenStart = chosenEnd = null;
        temp = null;
        v1 = v2 = null;
        Gdx.app.log("FollowPathBehaviour", "Objects were set to null");
    }
}
