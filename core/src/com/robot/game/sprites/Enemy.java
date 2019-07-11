package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import static com.robot.game.util.Constants.MAP_HEIGHT;
import static com.robot.game.util.Constants.PPM;

public abstract class Enemy implements Steerable<Vector2> {

    protected Body body;
    protected FixtureDef fixtureDef;

    protected SteeringBehavior<Vector2> steeringBehavior;
    protected SteeringAcceleration<Vector2> steeringOutput;
    protected FollowPath<Vector2, LinePath.LinePathParam> followPath;
    protected Array<Vector2> wayPoints;
    protected LinePath<Vector2> linePath;

    protected float maxLinearSpeed, maxLinearAcceleration;
    protected float maxAngularSpeed, maxAngularAcceleration;
    protected float boundingRadius;

    protected String platformID;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected float offset;
    protected MapObject object;

    protected boolean aiPathFollowing;

    public Enemy(Body body, FixtureDef fixtureDef, float offset, String platformID, MapObject object, boolean aiPathFollowing) {
        this.body = body;
        this.fixtureDef = fixtureDef;

        this.aiPathFollowing = aiPathFollowing;

        if(aiPathFollowing) {
            this.offset = offset;
            this.platformID = platformID;
            this.object = object;


            if(platformID != null) {
                parseXml();

                this.wayPoints = new Array<>(new Vector2[]{new Vector2((x - offset) / PPM, (y - offset) / PPM),
                        new Vector2((x - offset) / PPM, (y + height + offset) / PPM),
                        new Vector2((x + width + offset) / PPM, (y + height + offset) / PPM),
                        new Vector2((x + width + offset) / PPM, (y - offset) / PPM),
                        new Vector2((x - offset) / PPM, (y - offset) / PPM)});

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
        }


    }

    public abstract void update(float delta);


    private void parseXml() {
        XmlReader reader = new XmlReader();
        XmlReader.Element root = reader.parse(Gdx.files.internal("level1.1.tmx"));
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

    protected void applySteering(SteeringAcceleration<Vector2> steeringOutput, float delta) {
        //        System.out.println("SteeringX: " + steeringOutput.linear.x + " SteeringY: " + steeringOutput.linear.y);

        if(!steeringOutput.linear.isZero())
            body.setLinearVelocity(getLinearVelocity().mulAdd(steeringOutput.linear, delta).limit(getMaxLinearSpeed()));

        //        System.out.println(body.getLinearVelocity());
    }

    // reverse velocity of an enemy
    protected void reverseVelocity(boolean reverseVx, boolean reverseVy) {
        if(reverseVx)
            body.setLinearVelocity(-getLinearVelocity().x, getLinearVelocity().y);
        if(reverseVy)
            body.setLinearVelocity(body.getLinearVelocity().x, -body.getLinearVelocity().y);
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

    public Array<Vector2> getWayPoints() {
        return wayPoints;
    }
}
