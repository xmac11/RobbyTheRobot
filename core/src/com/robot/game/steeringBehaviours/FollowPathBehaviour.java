package com.robot.game.steeringBehaviours;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import static com.robot.game.util.Constants.PPM;


public class FollowPathBehaviour {

    private Body body;
    private SeekBehaviour seekBehaviour;
    private int startIndex;
    private Vector2 target = new Vector2();
    private boolean onPath;
    private boolean pathReached;
    private boolean initialTargetSet;
    private  Vector2 normalPoint = new Vector2();
    private Vector2 minStart = new Vector2(), minEnd = new Vector2();


    public FollowPathBehaviour(Body body, float maxLinearVelocity, int startIndex) {
        this.body = body;
        this.seekBehaviour = new SeekBehaviour(body, maxLinearVelocity);
        this.startIndex = startIndex;
    }

    public void follow(Array<Vector2> path) {
        Vector2 predicted = body.getLinearVelocity();
        predicted.setLength(16 / PPM);

        Vector2 predictedPosition = body.getPosition().add(predicted);

        float distance;
        float minDistance = Float.POSITIVE_INFINITY;
        Vector2 start = new Vector2(), end = new Vector2();
        int counter = 0;
        Vector2 temp = new Vector2();
        if(!onPath && !initialTargetSet) {
            for(int i = 0; i < path.size - 1; i++) {
                start = path.get(i).cpy();
                end = path.get(i+1).cpy();

                if(start.x > end.x || start.y > end.y) {
                    temp.set(start);
                    start.set(end);
                    end.set(temp);
                }

                if(isPointOnLine(body.getPosition(), start, end)) {
                    onPath = true;
                    break;
                }

                normalPoint = findNormalPoint(predictedPosition, start, end);
                if(!isPointOnLine(normalPoint.cpy(), start.cpy(), end.cpy())) {
                    normalPoint = body.getPosition().dst(start) < body.getPosition().dst(end) ? start : end;
                    System.out.println("not in line!!!!!!!!!!!!!!!!!!!!");
//                    counter++;
//                    continue;
                }

                distance = predictedPosition.dst(normalPoint);
                //System.out.println(body.getPosition().dst(target));
                if(distance < minDistance) {
                    minDistance = distance;
                    // target = normalPoint;
                    minStart.set(start);
                    minEnd.set(end);

                    // a bit further
                    Vector2 direction = end.cpy().sub(start);
                    direction.setLength(0 / PPM);
                    target = normalPoint.add(direction);
                }
            }
            System.out.println(minStart + " " + minEnd);
            initialTargetSet = true;
        }

        if(/*body.getPosition().dst(target) <= 8 / PPM*/isPointOnLine(body.getPosition(), minStart, minEnd) && !onPath) {
            onPath = true;
            pathReached = true;
            System.out.println("!!!!!!!!!!!!");
        }

        if(!onPath /*&& minDistance > 2 / PPM*/) {
            seekBehaviour.seek(target);
        }

        System.out.println(onPath + " " + pathReached);

        // determine next closest vertex
        if(pathReached) {
            int minIndex = 0;
            float minDistanceToVertex = Float.POSITIVE_INFINITY;
            for(int i = 0; i < 4; i++) {
                if(body.getPosition().dst(path.get(i)) < minDistanceToVertex) {
                    minDistanceToVertex = body.getPosition().dst(path.get(i));
                    minIndex = i;
                }
            }
            startIndex = (minIndex + 1) % 4;
            pathReached = false;
        }

        if(onPath) {
            if(!waypointReached(path.get(startIndex))) {
                target.set(path.get(startIndex));
                seekBehaviour.seek(target);
            }
            else {
                startIndex = (startIndex + 1) % 4;
                target.set(path.get(startIndex));
                seekBehaviour.seek(target);
            }
        }
    }

    private boolean waypointReached(Vector2 waypoint) {
        return Math.abs(waypoint.x - body.getPosition().x) <= 2 / PPM && Math.abs(waypoint.y - body.getPosition().y) <= 2 / PPM;
    }

    private boolean isPointOnLine(Vector2 point, Vector2 a, Vector2 b) {
        return Math.abs(a.dst(point) + point.dst(b) - a.dst(b)) <= 0.01f;
    }

    private Vector2 findNormalPoint(Vector2 p, Vector2 a, Vector2 b) {
        /*Vector2 ap = p.sub(a);

        Vector2 ab = b.cpy().sub(a);
        ab.nor();
        ab.scl(ap.dot(ab));

        return a.cpy().add(ab);*/

        Vector2 ap = p.cpy().sub(a);

        Vector2 ab = b.cpy().sub(a);
        float scale = (ap.dot(ab) / ab.len2());

        return a.cpy().add(ab.scl(scale));
    }

    public Vector2 getTarget() {
        return target;
    }
}
