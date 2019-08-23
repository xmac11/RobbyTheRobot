package com.robot.game.steeringBehaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.robot.game.entities.abstractEnemies.EnemyPathFollowingAI;

import static com.robot.game.util.Constants.PPM;


public class FollowPathBehaviour {

    private Body body;
    private SeekBehaviour seekBehaviour;
    private Array<Vector2> wayPoints;
    private int startIndex;
    private Vector2 target = new Vector2();
    private boolean onPath;
    private boolean pathReached;
    private boolean initialTargetSet;

    private Vector2 start = new Vector2(), end = new Vector2(); // for the line currently examined
    private Vector2 chosenStart = new Vector2(), chosenEnd = new Vector2(); // for the chosen line to follow
    private Vector2 temp = new Vector2(); // for swapping start/end

    // for the calculateProjection() method
    private Vector2 v1 = new Vector2();
    private Vector2 v2 = new Vector2();

    public FollowPathBehaviour(EnemyPathFollowingAI enemy, int startIndex) {
        this.body = enemy.getBody();
        this.wayPoints = enemy.getWayPoints();
        this.seekBehaviour = new SeekBehaviour(body, enemy.getMaxLinearSpeed());
        this.startIndex = startIndex;
    }

    public void follow() {
        Vector2 predicted = body.getLinearVelocity();
        predicted.setLength(16 / PPM);

        Vector2 predictedPosition = body.getPosition().add(predicted);

        float distance;
        float minDistance = Float.POSITIVE_INFINITY;
        if(!onPath && !initialTargetSet) {
            for(int i = 0; i < wayPoints.size - 1; i++) {
                start.set(wayPoints.get(i));
                end.set( wayPoints.get(i+1));

                // check if swapping is needed
                if(start.x > end.x || start.y > end.y) {
                    temp.set(start);
                    start.set(end);
                    end.set(temp);
                }

                // if body is already on the path, break
                if(isPointOnLine(body.getPosition(), start, end)) {
                    onPath = true;
                    break;
                }

                // calculate projection
                Vector2 projection = calculateProjection(predictedPosition, start, end);
                // if projection is not on the path, set the vertex closest to the body as the projection
                if(!isPointOnLine(projection, start, end)) {
                    projection.set(body.getPosition().dst(start) < body.getPosition().dst(end) ? start : end);
                    System.out.println("not in line!!!!!!!!!!!!!!!!!!!!");
                }

                // calculate distance between predicted position and projection
                distance = predictedPosition.dst(projection);
                // if distance is less than the min distance so far
                if(distance < minDistance) {
                    minDistance = distance;
                    chosenStart.set(start);
                    chosenEnd.set(end);

                    target.set(projection);
                }
            }
            initialTargetSet = true;
        }

        if(!onPath && isPointOnLine(body.getPosition(), chosenStart, chosenEnd)) {
            onPath = true;
            pathReached = true;
            System.out.println("!!!!!!!!!!!!");
        }

        // determine next closest vertex
        if(pathReached) {
            int minIndex = 0;
            float minDistanceToVertex = Float.POSITIVE_INFINITY;
            for(int i = 0; i < 4; i++) {
                if(body.getPosition().dst(wayPoints.get(i)) < minDistanceToVertex) {
                    minDistanceToVertex = body.getPosition().dst(wayPoints.get(i));
                    minIndex = i;
                }
            }
            startIndex = (minIndex + 1) % 4;
            pathReached = false;
        }
        else if(!onPath) {
            seekBehaviour.seek(target);
        }
        else { // if(onPath)
            if(!waypointReached(wayPoints.get(startIndex))) {
                target.set(wayPoints.get(startIndex));
                seekBehaviour.seek(target);
            }
            else {
                startIndex = (startIndex + 1) % 4;
                target.set(wayPoints.get(startIndex));
                seekBehaviour.seek(target);
            }
        }
        //System.out.println(onPath);
    }

    private boolean waypointReached(Vector2 waypoint) {
        return Math.abs(waypoint.x - body.getPosition().x) <= 2 / PPM && Math.abs(waypoint.y - body.getPosition().y) <= 2 / PPM;
    }

    private boolean isPointOnPath(Vector2 point) {
        for(int i = 0; i < wayPoints.size - 1; i++) {
            Vector2 start = wayPoints.get(i);
            Vector2 end = wayPoints.get(i+1);

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
