package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.robot.game.interactiveObjects.*;
import com.robot.game.screens.PlayScreen;
import com.robot.game.sprites.Bat;
import com.robot.game.sprites.Collectable;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Crab;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static com.robot.game.util.Constants.*;


public class ObjectParser {

    PlayScreen playScreen;
    private World world;
    private DelayedRemovalArray<InteractivePlatform> interactivePlatforms;
    private DelayedRemovalArray<Enemy> enemies;
    private DelayedRemovalArray<Collectable> collectables;
//    private Array<JSONObject> collectedItems;
    private JSONArray collectedItems;

    public ObjectParser(PlayScreen playScreen, World world, Array<MapObjects> layersObjectArray) {
        this.playScreen = playScreen;
        this.world = world;
        this.interactivePlatforms = new DelayedRemovalArray<>();
        this.enemies = new DelayedRemovalArray<>();
        this.collectables = new DelayedRemovalArray<>();
//        this.collectedItems = new Array<>();
        this.collectedItems = new JSONArray();
        for(MapObjects objects: layersObjectArray)
            createTiledObjects(world, objects);
    }

    private void createTiledObjects(World world, MapObjects objects) {

        /* ChainShapes are meant as you use them, for terrain and other static stuff.
        If you create a body with ChainShape and make it dynamic, it won't behave well.
        It probably won't rotate, and there will be no collisions with other chainshapes (they are not defined in box2d).

        So ChainShapes are meant for static bodies only.*/

        for(MapObject object: objects) {
            BodyDef bodyDef = new BodyDef();
            if(object.getProperties().containsKey(FALLING_PLATFORM_PROPERTY) || object.getProperties().containsKey(MOVING_PLATFORM_PROPERTY) || object.getProperties().containsKey(ENEMY_PROPERTY))
                bodyDef.type = BodyDef.BodyType.KinematicBody;
            else
                bodyDef.type = BodyDef.BodyType.StaticBody;
            FixtureDef fixtureDef = new FixtureDef();
            Body body;

            if(object instanceof RectangleMapObject) {

                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                // create body
                bodyDef.position.set( (rectangle.getX() + rectangle.getWidth() / 2) / PPM,
                                      (rectangle.getY() + rectangle.getHeight() / 2) / PPM );
                body = world.createBody(bodyDef);

                // create shape
                PolygonShape polygonShape =  new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth() / 2 / PPM,
                                      rectangle.getHeight() / 2 / PPM);

                // create fixture
                fixtureDef.shape = polygonShape;
                assignFilterBits(fixtureDef, object);
                createFixture(body, fixtureDef, object);

                polygonShape.dispose();
            }
            else if(object instanceof PolylineMapObject) {

                body = world.createBody(bodyDef);
                Shape shape =  createPolyline((PolylineMapObject) object);

                // create fixture
                fixtureDef.shape = shape;
                assignFilterBits(fixtureDef, object);
                createFixture(body, fixtureDef, object);

                shape.dispose();
            }
            else if(object instanceof PolygonMapObject) {

                body = world.createBody(bodyDef);
                Shape shape =  createPolygon((PolygonMapObject) object);

                // create fixture
                fixtureDef.shape = shape;
                assignFilterBits(fixtureDef, object);
                createFixture(body, fixtureDef, object);

                shape.dispose();
            }
            else continue;
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

    private ChainShape createPolygon(PolygonMapObject polygon) {
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
    }

    // assign filter bits to bodies
    private void assignFilterBits(FixtureDef fixtureDef, MapObject object) {
        // ladder
        if(object.getProperties().containsKey(LADDER_PROPERTY)) {
            fixtureDef.filter.categoryBits = LADDER_CATEGORY;
            fixtureDef.filter.maskBits = LADDER_MASK;
            fixtureDef.isSensor = true;
        }
        // falling platform
        else if(object.getProperties().containsKey(FALLING_PLATFORM_PROPERTY)) {
            fixtureDef.filter.categoryBits = FALLING_PLATFORM_CATEGORY;
            fixtureDef.filter.maskBits = FALLING_PLATFORM_MASK;
        }
        // moving platform
        else if(object.getProperties().containsKey(MOVING_PLATFORM_PROPERTY)) {
            fixtureDef.filter.categoryBits = MOVING_PLATFORM_CATEGORY;
            fixtureDef.filter.maskBits = MOVING_PLATFORM_MASK;
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
        // create falling platform
        else if(object.getProperties().containsKey(FALLING_PLATFORM_PROPERTY)) {
            InteractivePlatform fallingPlatform = new FallingPlatform(world, body, fixtureDef, object);
            this.interactivePlatforms.add(fallingPlatform);
        }
        // create moving platform
        else if(object.getProperties().containsKey(MOVING_PLATFORM_PROPERTY)) {
            InteractivePlatform movingPlatform = new MovingPlatform(world, body, fixtureDef, object);
            this.interactivePlatforms.add(movingPlatform);
        }
        // create enemies
        else if(object.getProperties().containsKey(ENEMY_PROPERTY)) {
            Enemy enemy;

            if(object.getProperties().containsKey(BAT_PROPERTY))
                enemy = new Bat(world, body, fixtureDef, object);
            else //if(object.getProperties().containsKey(SPIDER_PROPERTY))
                enemy = new Crab(world, body, fixtureDef, object);

            this.enemies.add(enemy);
        }
        // create spikes
        else if(object.getProperties().containsKey(SPIKE_PROPERTY)) {
            new Spike(body, fixtureDef, object);
        }
        // create collectables
        else if(object.getProperties().containsKey(COLLECTABLE_PROPERTY)) {
            if(shouldSpawn((int) object.getProperties().get("id")))
                collectables.add(new Collectable(playScreen, world, body, fixtureDef, object, collectedItems));
        }
        // create ground
        else {
            body.createFixture(fixtureDef).setUserData("ground");
        }
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

    private boolean shouldSpawn(int collectableID) {
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(Gdx.files.internal(LEVEL_1_JSON));
        JsonValue child1 = root.get("layers");

        for (int i = 0; i < child1.size; i++) {

            if (child1.get(i).has("name") && child1.get(i).getString("name").equals(COLLECTABLE_OBJECT)) {
                JsonValue child2 = child1.get(i).get("objects");
                //                System.out.println(child2);

                for (int j = 0; j < child2.size; j++) {

                    if (child2.get(j).has("id") && child2.get(j).getInt("id") == collectableID) {
                        JsonValue child3 = child2.get(j).get("properties");
                        for(int k = 0; k < child3.size; k++) {
                            if(child3.get(k).getString("name").equals("shouldSpawn")) {
                                return child3.get(k).getBoolean("value");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /*public void resetSpawningOfCollectables() {
        FileHandle file = Gdx.files.local(LEVEL_1_JSON);
        JSONObject root = null;

        try {
            root = (JSONObject) new JSONParser().parse(file.reader());
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }


        for(JSONObject object: collectedItems) {
            object.put("value", true);
        }
        FileSaver.saveJsonMap(file, root);
    }*/

    public /*Array<JSONObject>*/JSONArray getCollectedItems() {
        return collectedItems;
    }
}
