package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.entities.bat.BatPathFollowingAI;
import com.robot.game.entities.bat.BatPatrolling;
import com.robot.game.entities.crab.CrabPathFollowingAI;
import com.robot.game.entities.crab.CrabPatrolling;
import com.robot.game.entities.snake.SnakeArriveAI;
import com.robot.game.entities.snake.SnakePatrolling;
import com.robot.game.interactiveObjects.*;
import com.robot.game.interactiveObjects.collectables.Burger;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.interactiveObjects.collectables.PowerUp;
import com.robot.game.interactiveObjects.ladder.Ladder;
import com.robot.game.interactiveObjects.platforms.FallingPlatform;
import com.robot.game.interactiveObjects.platforms.InteractivePlatform;
import com.robot.game.interactiveObjects.platforms.MovingPlatform;
import com.robot.game.screens.PlayScreen;
import com.robot.game.entities.*;

import static com.robot.game.util.Constants.*;


public class ObjectParser {

    int rectangles = 0;
    int polylines = 0;
    int polygons = 0;

    private PlayScreen playScreen;
    private World world;
    private DelayedRemovalArray<InteractivePlatform> interactivePlatforms;
    private DelayedRemovalArray<Enemy> enemies;
    private DelayedRemovalArray<Collectable> collectables;
    private Trampoline trampoline;
    private ObjectMap<Integer, Array<Body>> jointMap;
    public Array<PrismaticJoint> joints;

    public ObjectParser(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.world = playScreen.getWorld();

        this.interactivePlatforms = new DelayedRemovalArray<>();
        this.enemies = new DelayedRemovalArray<>();
        this.collectables = new DelayedRemovalArray<>();
        this.jointMap = new ObjectMap<>();
        this.joints = new Array<>();

        for(MapObjects objects: playScreen.getLayersObjectArray())
            createTiledObjects(objects);

        createJoints();
        System.out.println("Rectangles: " + rectangles + ", polylines: " + polylines + ", polygons: " + polygons);
    }

    private void createTiledObjects(MapObjects objects) {

        for(MapObject object: objects) {
            BodyDef bodyDef = new BodyDef();

            // determine BodyType (Static, Kinematic, Dynamic)
            determineBodyType(object, bodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            Body body;

            if(object instanceof RectangleMapObject) {
                rectangles++;

                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                // create body
                bodyDef.position.set( (rectangle.getX() + rectangle.getWidth() / 2) / PPM,
                                      (rectangle.getY() + rectangle.getHeight() / 2) / PPM );
                body = world.createBody(bodyDef);

                // create shape
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth() / 2 / PPM,
                                      rectangle.getHeight() / 2 / PPM);

                // create fixture
                fixtureDef.shape = polygonShape;
                assignFilterBits(fixtureDef, object);
                createFixture(body, fixtureDef, object);

                polygonShape.dispose();
            }
            else if(object instanceof PolylineMapObject) {
                polylines++;

                body = world.createBody(bodyDef);
                Shape shape = createPolyline((PolylineMapObject) object);

                // create fixture
                fixtureDef.shape = shape;
                assignFilterBits(fixtureDef, object);
                createFixture(body, fixtureDef, object);

                shape.dispose();
            }
            /*else if(object instanceof PolygonMapObject) {
                polygons++;

                body = world.createBody(bodyDef);
                Shape shape = createPolygon((PolygonMapObject) object);

                // create fixture
                fixtureDef.shape = shape;
                assignFilterBits(fixtureDef, object);
                createFixture(body, fixtureDef, object);

                shape.dispose();
            }*/
            else continue;
        }
    }

    private void determineBodyType(MapObject object, BodyDef bodyDef) {
        if(object.getProperties().containsKey(FISH_PROPERTY)) {
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.gravityScale = 0;
        }
        else if(object.getProperties().containsKey("aiArrive") || object.getProperties().containsKey("dynamicSpike")) {
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.fixedRotation = true;
        }
        else if(object.getProperties().containsKey(FALLING_PLATFORM_PROPERTY) || object.getProperties().containsKey(MOVING_PLATFORM_PROPERTY)
                || object.getProperties().containsKey(ENEMY_PROPERTY)) { // all other enemies are Kinematic bodies
            bodyDef.type = BodyDef.BodyType.KinematicBody;
        }
        else {
            bodyDef.type = BodyDef.BodyType.StaticBody;
        }
    }

    private ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices =  polyline.getPolyline().getTransformedVertices();
        float[] worldVertices = new float[vertices.length];

        for(int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = vertices[i] / PPM;
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);

        return chainShape;
    }

    /*private ChainShape createPolygon(PolygonMapObject polygon) {
        float[] vertices =  polygon.getPolygon().getTransformedVertices();
        float[] worldVertices = new float[vertices.length + 2]; // +2 to close the polyline

        for(int i = 0; i < worldVertices.length-2; i++) {
            worldVertices[i] = vertices[i] / PPM;
        }
        worldVertices[vertices.length] = vertices[0] / PPM;
        worldVertices[vertices.length + 1] = vertices[1] / PPM;

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);

        return chainShape;
    }*/

    // assign filter bits to bodies
    private void assignFilterBits(FixtureDef fixtureDef, MapObject object) {
        // ladder
        if(object.getProperties().containsKey(LADDER_PROPERTY)) {
            fixtureDef.filter.categoryBits = LADDER_CATEGORY;
            fixtureDef.filter.maskBits = LADDER_MASK;
            fixtureDef.isSensor = true;
        }
        // interactive platform
        else if(object.getProperties().containsKey(INTERACTIVE_PLATFORM_PROPERTY)) {
            fixtureDef.filter.categoryBits = INTERACTIVE_PLATFORM_CATEGORY;
            fixtureDef.filter.maskBits = INTERACTIVE_PLATFORM_MASK;
        }
        // enemy
        else if(object.getProperties().containsKey(ENEMY_PROPERTY)) {
            fixtureDef.filter.categoryBits = ENEMY_CATEGORY;
            fixtureDef.filter.maskBits = ENEMY_MASK;
        }
        // spikes
        else if(object.getProperties().containsKey(SPIKE_PROPERTY)) {
            fixtureDef.filter.categoryBits = SPIKE_CATEGORY;
            fixtureDef.filter.maskBits = SPIKE_MASK;
            fixtureDef.isSensor = true;
        }
        // collectables
        else if(object.getProperties().containsKey(COLLECTABLE_PROPERTY)) {
            fixtureDef.filter.categoryBits = COLLECTABLE_CATEGORY;
            fixtureDef.filter.maskBits = COLLECTABLE_MASK;
            fixtureDef.isSensor = true;
        }
        // wall jumping
        else if(object.getProperties().containsKey(WALL_JUMPING_PROPERTY)) {
            fixtureDef.filter.categoryBits = WALLJUMP_CATEGORY;
            fixtureDef.filter.maskBits = WALLJUMP_MASK;
        }
        // trampoline
        else if(object.getProperties().containsKey(TRAMPOLINE_PROPERTY)) {
            fixtureDef.filter.categoryBits = TRAMPOLINE_CATEGORY;
            fixtureDef.filter.maskBits = TRAMPOLINE_MASK;
        }
        // sensors for disabling arrive ai behavior
        else if(object.getProperties().containsKey("chaseSensor")) {
            fixtureDef.filter.categoryBits = CHASE_SENSOR_CATEGORY;
            fixtureDef.filter.maskBits = CHASE_SENSOR_MASK;
            fixtureDef.isSensor = true;
        }
        // ground
        else {
            fixtureDef.filter.categoryBits = GROUND_CATEGORY;
            fixtureDef.filter.maskBits = GROUND_MASK;
        }
    }

    private void createFixture(Body body, FixtureDef fixtureDef, MapObject object) {
        // create ladder
        if(object.getProperties().containsKey(LADDER_PROPERTY)) {
            String description = (String) object.getProperties().get(LADDER_PROPERTY);
            new Ladder(body, fixtureDef, description);
        }
        // create interactive platforms
        else if(object.getProperties().containsKey(INTERACTIVE_PLATFORM_PROPERTY)) {
            // create falling platform
            if(object.getProperties().containsKey(FALLING_PLATFORM_PROPERTY)) {
                this.interactivePlatforms.add(new FallingPlatform(playScreen, body, fixtureDef, object));
            }
            // create moving platform
            else if(object.getProperties().containsKey(MOVING_PLATFORM_PROPERTY)) {
                this.interactivePlatforms.add(new MovingPlatform(playScreen, body, fixtureDef, object));
            }
        }
        // create enemies
        else if(object.getProperties().containsKey(ENEMY_PROPERTY)) {

            // create bats
            if(object.getProperties().containsKey(BAT_PROPERTY)) {
                if(object.getProperties().containsKey("aiPathFollowing"))
                    this.enemies.add(new BatPathFollowingAI(playScreen, body, fixtureDef, object));
                else
                    this.enemies.add(new BatPatrolling(playScreen, body, fixtureDef, object));
            }

            // create crabs
            else if(object.getProperties().containsKey(CRAB_PROPERTY)) {
                if(object.getProperties().containsKey("aiPathFollowing"))
                    this.enemies.add(new CrabPathFollowingAI(playScreen, body, fixtureDef, object));
                else
                    this.enemies.add(new CrabPatrolling(playScreen, body, fixtureDef, object));
            }

            // create fishes
            else if(object.getProperties().containsKey(FISH_PROPERTY))
                this.enemies.add(new Fish(playScreen, body, fixtureDef, object));

            // create monsters
            else if(object.getProperties().containsKey(MONSTER_PROPERTY))
                this.enemies.add(new Monster(playScreen, body, fixtureDef, object));

            // create snakes
            else if(object.getProperties().containsKey(SNAKE_PROPERTY)) {
                if(object.getProperties().containsKey("aiArrive"))
                    this.enemies.add(new SnakeArriveAI(playScreen, body, fixtureDef, object));
                else
                    this.enemies.add(new SnakePatrolling(playScreen, body, fixtureDef, object));
            }
        }
        // create spikes
        else if(object.getProperties().containsKey(SPIKE_PROPERTY)) {
            new Spike(body, fixtureDef, object, jointMap);
        }
        // create collectables
        else if(object.getProperties().containsKey(COLLECTABLE_PROPERTY)) {

            // check if collectable should spawn (i.e. it has not been already collected)
            if(playScreen.getCollectableHandler().shouldSpawn((int) object.getProperties().get("id"))) {
                // create powerups
                if(object.getProperties().containsKey(POWERUP_PROPERTY)) {
                    this.collectables.add(new PowerUp(playScreen, body, fixtureDef, object));
                }
                // create burgers
                else
                    this.collectables.add(new Burger(playScreen, body, fixtureDef, object));
            }
        }
        // create wall jumping surface
        else if(object.getProperties().containsKey(WALL_JUMPING_PROPERTY)) {
            body.createFixture(fixtureDef).setUserData(WALL_JUMPING_PROPERTY);
        }
        // create trampoline
        else if(object.getProperties().containsKey(TRAMPOLINE_PROPERTY)) {
            this.trampoline = new Trampoline(playScreen, body, fixtureDef);
        }
        // create ground
        else {
            body.createFixture(fixtureDef).setUserData("ground");

            if(object.getProperties().containsKey("prismatic")) {
                int key = (int) object.getProperties().get("prismatic");
                Array<Body> bodyArray = jointMap.get(key);
                if(bodyArray == null) bodyArray = new Array<>();
                bodyArray.add(body);
                jointMap.put((Integer) object.getProperties().get("prismatic"), bodyArray);
            }
        }
    }

    private void createJoints() {
        for(Integer i: jointMap.keys()) {
            Array<Body> bodyArray = jointMap.get(i);

            if(DEBUG_ON && bodyArray.size > 2)
                throw new IllegalArgumentException("Joint array contains more than 2 bodies");

            PrismaticJointDef jointDef = new PrismaticJointDef();
            jointDef.bodyA = bodyArray.get(0);
            jointDef.bodyB = bodyArray.get(1);
            jointDef.enableLimit = true;
            jointDef.lowerTranslation = -176 / PPM;
            jointDef.upperTranslation = 176 / 2f / PPM;
            jointDef.localAxisA.set(0, 1);

//            jointDef.enableMotor = true;
//            jointDef.maxMotorForce = -100;
//            jointDef.motorSpeed = 20;

            this.joints.add((PrismaticJoint) world.createJoint(jointDef));
        }
        Gdx.app.log("ObjectParser", "Joints created");
    }

    // getter for the interactive platforms
    public DelayedRemovalArray<InteractivePlatform> getInteractivePlatforms() {
        return interactivePlatforms;
    }

    // getter for enemies
    public DelayedRemovalArray<Enemy> getEnemies() {
        return enemies;
    }

    // getter for collectables
    public DelayedRemovalArray<Collectable> getCollectables() {
        return collectables;
    }

    public Trampoline getTrampoline() {
        return trampoline;
    }
}
