package com.robot.game.entities.abstractEnemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public abstract class EnemyPathFollowingAI extends Enemy implements Steerable<Vector2> {

    // EnemyAI
    protected SteeringBehavior<Vector2> steeringBehavior;
    protected SteeringAcceleration<Vector2> steeringOutput;
    protected FollowPath<Vector2, LinePath.LinePathParam> followPath;
    protected Array<Vector2> wayPoints;
    protected LinePath<Vector2> linePath;

    protected float maxLinearSpeed, maxLinearAcceleration;
    protected float maxAngularSpeed, maxAngularAcceleration;
    protected float boundingRadius;

    // patrolling platform (ai)
    protected String platformID;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected float offsetX;
    protected float offsetY;

    protected ShapeRenderer shapeRenderer;

    public EnemyPathFollowingAI(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);
        this.shapeRenderer = playScreen.getShapeRenderer();

        this.offsetX = (float) object.getProperties().get("offsetX");
        this.offsetY = (float) object.getProperties().get("offsetY");
        this.platformID = (String) object.getProperties().get("platformID");
        this.object = object;

        if(platformID != null) {
            // parse json file to get the waypoints of the platform the enemy should follow
            parseJson();

            this.wayPoints = new Array<>(new Vector2[]{new Vector2((x - offsetX) / PPM, (y - offsetY) / PPM),
                    new Vector2((x - offsetX) / PPM, (y + height + offsetY) / PPM),
                    new Vector2((x + width + offsetX) / PPM, (y + height + offsetY) / PPM),
                    new Vector2((x + width + offsetX) / PPM, (y - offsetY) / PPM),
                    new Vector2((x - offsetX) / PPM, (y - offsetY) / PPM)});

            this.linePath = new LinePath<>(wayPoints, false);
            float pathOffset = (float) object.getProperties().get("pathOffset");
            this.followPath = new FollowPath<>(this, linePath, pathOffset).setTimeToTarget(0.1f).setArrivalTolerance(0.001f).setDecelerationRadius(8f);
            this.steeringBehavior = followPath;
            this.steeringOutput = new SteeringAcceleration<>(new Vector2());

            this.maxLinearSpeed = (float) object.getProperties().get("aiSpeed");
            this.maxLinearAcceleration = 500f;
            this.maxAngularSpeed = 3;
            this.maxAngularAcceleration = 3;
            this.boundingRadius = 1f;
        }
    }

    protected void applySteering(SteeringAcceleration<Vector2> steeringOutput, float delta) {
        //        System.out.println("SteeringX: " + steeringOutput.linear.x + " SteeringY: " + steeringOutput.linear.y);

        if(!steeringOutput.linear.isZero())
            body.setLinearVelocity(getLinearVelocity().mulAdd(steeringOutput.linear, delta).limit(getMaxLinearSpeed()));

        //        System.out.println(body.getLinearVelocity());
    }

    // parse json file to get the waypoints of the platform the enemy should follow
    private void parseJson() {
        JsonReader reader = new JsonReader();
        JsonValue root =  reader.parse(Gdx.files.internal(FOLDER_NAME + "level" + playScreen.getLevelID() + ".json"));
        JsonValue child1 = root.get("layers");

        //System.out.println(child1.size);

        boolean shouldBreakI = false;
        boolean shouldBreakJ = false;

        for(int i = 0; i < child1.size; i++) {

            if(shouldBreakI)
                break;
            if(child1.get(i).has("name") && child1.get(i).getString("name").equals(GROUND_OBJECT)) {
                shouldBreakI = true;
                JsonValue child2 = child1.get(i).get("objects");

                //System.out.println(child2.size);
                for(int j = 0; j < child2.size; j++) {

                    if(shouldBreakJ)
                        break;
                    if(child2.get(j).has("id") && child2.get(j).getString("id").equals(platformID)) {
                        shouldBreakJ = true;

                        this.width = child2.get(j).getFloat("width");
                        this.height = child2.get(j).getFloat("height");
                        this.x = child2.get(j).getFloat("x");
                        this.y = playScreen.getMapHeight() - child2.get(j).getFloat("y") - height; // to get the bottom left corner
                    }
                }
            }
        }
    }

    public void drawAiPath() {
        if(platformID != null) {
            int k = wayPoints.size;
            Vector2[] points = new Vector2[k];

            for (int j = 0; j < k; j++) {
                points[j] = wayPoints.get(j);
            }

            for (int j = 0; j < k - 1; j++) {
                points[j] = wayPoints.get(j);
                shapeRenderer.line(points[j], points[j + 1]);
            }
        }
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

    public FollowPath<Vector2, LinePath.LinePathParam> getFollowPath() {
        return followPath;
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
