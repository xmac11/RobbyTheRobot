package com.robot.game.entities.abstractEnemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.robot.game.screens.PlayScreen;
import com.robot.game.steeringBehaviours.FollowPathBehaviour;

import static com.robot.game.util.constants.Constants.*;

public abstract class EnemyPathFollowingAI extends Enemy {

    // EnemyAI
    private Array<Vector2> wayPoints;
    private float maxLinearSpeed;

    // patrolling platform (ai)
    protected String platformID;
    private float platformX;
    private float platformY;
    private float platformWidth;
    private float platformHeight;
    protected float offsetX;
    protected float offsetY;

    public boolean activated;
    protected float activationRange;

    protected ShapeRenderer shapeRenderer;

    protected FollowPathBehaviour followPathBehaviour;

    public EnemyPathFollowingAI(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);
        this.shapeRenderer = playScreen.getShapeRenderer();

        this.offsetX = (float) object.getProperties().get("offsetX") / PPM;
        this.offsetY = (float) object.getProperties().get("offsetY") / PPM;
        this.platformID = (String) object.getProperties().get("platformID");
        this.object = object;

        if(platformID != null) {
            // parse json file to get the waypoints of the platform the enemy should follow
            parseJson();

            // start from upper left corner, move counter clockwise
            this.wayPoints = new Array<>(new Vector2[]{new Vector2((platformX - offsetX), (platformY + platformHeight + offsetY)),
                    new Vector2((platformX - offsetX), (platformY - offsetY)),
                    new Vector2((platformX + platformWidth + offsetX), (platformY - offsetY)),
                    new Vector2((platformX + platformWidth + offsetX), (platformY + platformHeight + offsetY)),
                    new Vector2((platformX - offsetX), (platformY + platformHeight + offsetY))});

            this.maxLinearSpeed = (float) object.getProperties().get("aiSpeed");

            // enemy waits for player to be activated
            if(object.getProperties().containsKey("waitForPlayer")) {
                this.activated = false;
                this.activationRange = (float) object.getProperties().get("waitForPlayer");
            }
            else {
                this.activated = true;
            }

            this.followPathBehaviour = new FollowPathBehaviour(this);
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

                        this.platformWidth = child2.get(j).getFloat("width") / PPM;
                        this.platformHeight = child2.get(j).getFloat("height") / PPM;
                        this.platformX = child2.get(j).getFloat("x") / PPM;
                        this.platformY = (playScreen.getMapHeight() - child2.get(j).getFloat("y")) / PPM - platformHeight; // to get the bottom left corner
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
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.line(points[j], points[j + 1]);
            }
        }
    }

    public void drawTarget() {
        if(platformID != null) {
            shapeRenderer.setColor(Color.RED);
            //System.out.println(followPathBehaviour.target.x + " " + followPathBehaviour.target.y);
            shapeRenderer.circle(followPathBehaviour.getTarget().x, followPathBehaviour.getTarget().y, 6 / PPM, 10);
        }
    }

    public String getPlatformID() {
        return platformID;
    }

    public Array<Vector2> getWayPoints() {
        return wayPoints;
    }

    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    public float getPlatformX() {
        return platformX;
    }

    public float getPlatformY() {
        return platformY;
    }

    public float getPlatformWidth() {
        return platformWidth;
    }

    public float getPlatformHeight() {
        return platformHeight;
    }

    @Override
    public void setToNull() {
        followPathBehaviour.setToNull();
        wayPoints = null;
        platformID = null;
        followPathBehaviour = null;
        Gdx.app.log("EnemyPathFollowing", "Objects were set to null");
    }
}
