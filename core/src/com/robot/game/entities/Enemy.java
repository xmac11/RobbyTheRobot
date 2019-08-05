package com.robot.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.*;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.Damaging;

import static com.robot.game.util.Constants.*;

public abstract class Enemy extends Sprite implements Steerable<Vector2>, Damaging {

    protected PlayScreen playScreen;
    protected Assets assets;

    // Box2D
    protected World world;
    protected Body body;
    protected FixtureDef fixtureDef;
    protected MapObject object;

    // AI
    protected SteeringBehavior<Vector2> steeringBehavior;
    protected SteeringAcceleration<Vector2> steeringOutput;
    protected FollowPath<Vector2, LinePath.LinePathParam> followPath;
    protected Array<Vector2> wayPoints;
    protected LinePath<Vector2> linePath;

    protected float maxLinearSpeed, maxLinearAcceleration;
    protected float maxAngularSpeed, maxAngularAcceleration;
    protected float boundingRadius;

    // Patrolling platform (ai)
    protected String platformID;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected float offsetX;
    protected float offsetY;

    // Patrolling range (non-ai)
    protected float startX;
    protected float startY;
    protected float endX;
    protected float endY;
    protected float vX;
    protected float vY;
    protected boolean horizontal;

    protected boolean aiPathFollowing;
    protected boolean flagToKill;
    protected boolean dead; // only used for bat

    // animation
    protected TextureRegion textureRegion;
    protected float startTimeAnim;
    protected float elapsedAnim;
    protected float deadStartTime;
    protected float deadElapsed;

    public boolean flagToChangeMask;

    public Enemy(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.world = playScreen.getWorld();
        this.body = body;
        this.fixtureDef = fixtureDef;
        if(!object.getProperties().containsKey("noRestitution"))
            fixtureDef.restitution = 0.5f;

        this.aiPathFollowing = (boolean) object.getProperties().get("aiPathFollowing");

        if(aiPathFollowing) {
            this.offsetX = (float) object.getProperties().get("offsetX");
            this.offsetY = (float) object.getProperties().get("offsetY");
            this.platformID = (String) object.getProperties().get("platformID");
            this.object = object;

            if(platformID != null) {
//                parseXml();
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
                this.setSteeringBehavior(followPath);
                this.steeringOutput = new SteeringAcceleration<>(new Vector2());

                this.maxLinearSpeed = (float) object.getProperties().get("aiSpeed");
                this.maxLinearAcceleration = 500f;
                this.maxAngularSpeed = 3;
                this.maxAngularAcceleration = 3;
                this.boundingRadius = 1f;
            }
        }
        else {
            this.startX = (float) object.getProperties().get("startX");
            this.startY = (float) object.getProperties().get("startY");
            this.endX = (float) object.getProperties().get("endX");
            this.endY = (float) object.getProperties().get("endY");

            this.vX = (float) object.getProperties().get("vX");
            this.vY = (float) object.getProperties().get("vY");

            this.horizontal = (vX != 0);

            body.setLinearVelocity(vX, vY);
        }

        // animation
        startTimeAnim = TimeUtils.nanoTime();
    }

    public abstract void update(float delta);

    @Override
    public abstract int getDamage();

    // parse xml to get the waypoints of the platform the enemy should follow
    private void parseXml() {
        float start =  System.nanoTime();
        XmlReader reader = new XmlReader();
        XmlReader.Element root = reader.parse(Gdx.files.internal(LEVEL_1_TMX));
        Array<XmlReader.Element> child1 = root.getChildrenByNameRecursively("objectgroup");

        for(int i = 0; i < child1.size; i++) {
            Array<XmlReader.Element> child2 = child1.get(i).getChildrenByNameRecursively("object");

            for(int j = 0; j < child2.size; j++){
                if(child2.get(j).getAttributes().get("id").equals(platformID)) {

                    this.x = Float.valueOf(child2.get(j).getAttributes().get("x"));
                    this.y = playScreen.getMapHeight() - Float.valueOf(child2.get(j).getAttributes().get("y"));

                    XmlReader.Element properties = child2.get(j).getChildByName("properties");
                    Array<XmlReader.Element> property = properties.getChildrenByNameRecursively("property");
                    for(int k = 0; k < property.size; k++) {
                        if(property.get(k).getAttributes().get("name").equals("width"))
                            this.width = Float.valueOf(property.get(k).getAttributes().get("value"));
                        else if(property.get(k).getAttributes().get("name").equals("height"))
                            this.height =  Float.valueOf(property.get(k).getAttributes().get("value"));
                    }
                }
            }
        }
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

    // check if enemy is outside its moving range in x-direction
    protected boolean outOfRangeX() {
        return body.getPosition().x < startX / PPM || body.getPosition().x > endX / PPM;
    }

    // check if enemy is outside its moving range in y-direction
    protected boolean outOfRangeY() {
        return body.getPosition().y < startY / PPM || body.getPosition().y > endY / PPM;
    }

    public void setFlagToKill() {
        this.flagToKill = true;
        // keep track of the time enemy was killed
        deadStartTime = TimeUtils.nanoTime();
    }

    protected void destroyBody() {
        world.destroyBody(body);
        Gdx.app.log("Enemy", "Body destroyed");

        playScreen.getEnemies().removeValue(this, false);
        Gdx.app.log("Enemy", "Enemy was removed from array");
    }

    public boolean isAiPathFollowing() {
        return aiPathFollowing;
    }

    public Body getBody() {
        return body;
    }

    public FollowPath<Vector2, LinePath.LinePathParam> getFollowPath() {
        return followPath;
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

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
