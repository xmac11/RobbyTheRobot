package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;

import javax.xml.bind.Element;

import static com.robot.game.util.Constants.*;

public class Enemy implements Steerable<Vector2> {

    // AI
    private SteeringBehavior<Vector2> steeringBehavior;
    private SteeringAcceleration steeringOutput;
    private FollowPath<Vector2, LinePath.LinePathParam> followPath;
    public Array<Vector2> wayPoints;
    private LinePath<Vector2> linePath;

    private float maxLinearSpeed, maxLinearAcceleration;
    private float maxAngularSpeed, maxAngularAcceleration;
    private float boundingRadius;


    private Body body;
    private FixtureDef fixtureDef;
    private TiledMap tiledMap;
    private String platformID;
    private float x;
    private float y;
    private float width;
    private float height;
    private float offset;

    // spline
    public CatmullRomSpline<Vector2> splinePath;
    public Vector2 target;
    public float timer;

    public Enemy(Body body, FixtureDef fixtureDef, TiledMap tiledMap, float offset, String platformID) {
        this.body = body;
        this.fixtureDef = fixtureDef;
        this.tiledMap = tiledMap;
        this.offset = offset;
        this.platformID = platformID;
        body.createFixture(fixtureDef).setUserData(this);

        // parse xml tile map to find dimensions of each patrolling platform
        if(platformID != null) {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal("level1.tmx"));
            Array<XmlReader.Element> child1 = root.getChildrenByNameRecursively("objectgroup");

            for(int i = 0; i < child1.size; i++) {
                Array<XmlReader.Element> child2 = child1.get(i).getChildrenByNameRecursively("object");

                for(int j = 0; j < child2.size; j++){
                    if(child2.get(j).getAttributes().get("id").equals(platformID)) {

                        this.x = Float.valueOf(child2.get(j).getAttributes().get("x"));
                        this.y = MAP_HEIGHT - Float.valueOf(child2.get(j).getAttributes().get("y"));

                        XmlReader.Element child = child2.get(j).getChildByName("polygon");
                        String points = child.getAttributes().get("points");
                        String[] pointsArr = points.split(",| ");

                        if(pointsArr.length == 8) {
                            this.width = Float.valueOf(pointsArr[6]) - Float.valueOf(pointsArr[0]);
                            this.height = Math.abs(Float.valueOf(pointsArr[1]) - Float.valueOf(pointsArr[3]));
                        }
                        else
                            throw new IllegalArgumentException("Tile not rectangular");
                    }
                }
            }
        }


//        System.out.println(e.getAttribute("id"));

        /*if(platformID != null) {
            MapObject object = tiledMap.getLayers().get(GROUND_OBJECT).getObjects().get(platformID);
            if(object instanceof RectangleMapObject) {
                Rectangle rec = ((RectangleMapObject) object).getRectangle();

                float x = rec.x;
                float y = rec.y;
                float width = rec.width;
                float height = rec.height;

                System.out.println("x " + x + " width " + width);
            }
        }*/


        if(aiON && platformID != null) {
            this.wayPoints = new Array<>(new Vector2[]{new Vector2((x - offset) / PPM, (y - offset) / PPM),
                    new Vector2((x - offset) / PPM, (y + height + offset) / PPM),
                    new Vector2((x + width + offset) / PPM, (y + height + offset) / PPM),
                    new Vector2((x + width + offset) / PPM, (y - offset) / PPM),
                    new Vector2((x - offset) / PPM, (y - offset) / PPM)});
            /*this.wayPoints = new Array<>(new Vector2[]{new Vector2(176 / PPM, 176 / PPM),
                    new Vector2(176 / PPM, 75 / PPM),
                    new Vector2(50 / PPM, 75 / PPM),
                    new Vector2(50 / PPM, 176 / PPM),
                    new Vector2(176 / PPM, 176 / PPM)});*/
            /*this.wayPoints = new Array<>(new Vector2[]{new Vector2(250 / PPM, 176 / PPM),
                    new Vector2(45 / PPM, 75 / PPM),
                    new Vector2(80 / PPM, 75 / PPM),
                    new Vector2(50 / PPM, 65 / PPM),
                    new Vector2(176 / PPM, 30 / PPM)});*/
            /*this.wayPoints = new Array<>(new Vector2[]{new Vector2(176 / PPM, 176 / PPM),
                    new Vector2(176 / PPM, 75 / PPM),
                    new Vector2(50 / PPM, 75 / PPM),
                    new Vector2(600 / PPM, 300 / PPM),
                    new Vector2(176 / PPM, 176 / PPM)});*/

            this.linePath = new LinePath<>(wayPoints, false);
            this.followPath = new FollowPath<>(this, linePath, 0.1f).setTimeToTarget(0.1f).setArrivalTolerance(0.001f).setDecelerationRadius(8f);
            this.setSteeringBehavior(followPath);
            this.steeringOutput = new SteeringAcceleration<>(new Vector2());

            this.maxLinearSpeed = 3.5f;
            this.maxLinearAcceleration = 500f;
            this.maxAngularSpeed = 3;
            this.maxAngularAcceleration = 3;
            this.boundingRadius = 1f;
        }
        else {
            this.target = new Vector2();
            this.splinePath = new CatmullRomSpline<>(new Vector2[] { new Vector2(176 / PPM, 176 / PPM),
                    new Vector2(176 / PPM, 75 / PPM),
                    new Vector2(50 / PPM, 75 / PPM),
                    new Vector2(50 / PPM, 176 / PPM) }, true);
        }
    }

    public void update(float delta) {

        if(aiON) {
            if(steeringBehavior != null) {
                steeringBehavior.calculateSteering(steeringOutput);
                applySteering(steeringOutput, delta);
            }
        }
        else {
            timer += delta;
            float f = timer / 4f;
            if(f <= 1) {
                Vector2 enemyPosition = body.getWorldCenter();
                splinePath.valueAt(target, f); // this method sets the value of target

                Vector2 positionDelta = new Vector2(target).sub(enemyPosition);

                if(delta > 0.01f)
                    body.setLinearVelocity(positionDelta.scl(1/delta));
            }
            else
                timer = 0;
        }
    }

    private void applySteering(SteeringAcceleration<Vector2> steeringOutput, float delta) {
//        System.out.println("SteeringX: " + steeringOutput.linear.x + " SteeringY: " + steeringOutput.linear.y);

        if(!steeringOutput.linear.isZero())
            body.setLinearVelocity(getLinearVelocity().mulAdd(steeringOutput.linear, delta).limit(getMaxLinearSpeed()));

//        System.out.println(body.getLinearVelocity());
    }


    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return 0;
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return false;
    }

    @Override
    public void setTagged(boolean tagged) {

    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {

    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }

    public SteeringBehavior<Vector2> getSteeringBehavior() {
        return steeringBehavior;
    }

    public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
        this.steeringBehavior = steeringBehavior;
    }

    public String getPlatformID() {
        return platformID;
    }
}


