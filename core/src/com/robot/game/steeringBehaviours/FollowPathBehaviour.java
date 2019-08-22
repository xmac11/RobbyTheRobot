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

    public FollowPathBehaviour(Body body, float maxLinearVelocity, int startIndex) {
        this.body = body;
        this.seekBehaviour = new SeekBehaviour(body, maxLinearVelocity);
        this.startIndex = startIndex;
    }

    public void follow(Array<Vector2> path) {

//        Vector2 predicted = body.getLinearVelocity();
//        predicted.setLength(32 / PPM);
        //System.out.println(predicted);

//        Vector2 predictedPosition = body.getPosition().add(predicted);

//        Vector2 start = path.get(1).cpy();
//        Vector2 end = path.get(2).cpy();
//
//        Vector2 normalPoint = findNormalPoint(predictedPosition, start, end);
//        //System.out.println(normalPoint);
//
//
//        // a bit further
//        Vector2 direction = end.sub(start);
//        direction.setLength(16 / PPM);
//        Vector2 target = normalPoint.add(direction);
//        //System.out.println(target);
//
//        float distance = predicted.dst(normalPoint);
//
//        if(distance > 0.1f /*|| body.getPosition().x < start.x || body.getPosition().x > end.x*/) {
//            System.out.println("seeking");
//            seek.seek(target);
//        }

        /*Vector2 target = path.get(0).cpy();
        float distance;
        float worldRecord = 100000;
        if(!onPath) {
            for(int i = 0; i < path.size - 1; i++) {
                Vector2 start = path.get(i).cpy();
                Vector2 end = path.get(i+1).cpy();

                Vector2 normalPoint = findNormalPoint(predictedPosition, start, end);
                if(normalPoint.x < start.x || normalPoint.x > end.x) {
                    normalPoint = end.cpy();
                }

                distance = predicted.dst(normalPoint);
                System.out.println(body.getPosition().dst(target));
                if(distance < worldRecord) {
                    worldRecord = distance;
                    // target = normalPoint;

                    // a bit further
                    Vector2 direction = end.cpy().sub(start);
                    direction.setLength(16 / PPM);
                    target = normalPoint.add(direction);
                }
            }
            //System.out.println(worldRecord);
            if(worldRecord > 0.1f) {
                //            System.out.println(target);
                seek.seek(target);
            }
        }*/

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

    private boolean waypointReached(Vector2 waypoint) {
        return Math.abs(waypoint.x - body.getPosition().x) <= 2 / PPM && Math.abs(waypoint.y - body.getPosition().y) <= 2 / PPM;
    }

    private Vector2 findNormalPoint(Vector2 p, Vector2 a, Vector2 b) {
        Vector2 ap = p.sub(a);

        Vector2 ab = b.cpy().sub(a);
        ab.nor();
        ab.scl(ap.dot(ab));

        return a.cpy().add(ab);
    }
}
