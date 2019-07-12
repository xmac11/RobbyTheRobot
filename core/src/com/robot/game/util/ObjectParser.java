package com.robot.game.util;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.interactiveObjects.FallingPlatform;
import com.robot.game.interactiveObjects.InteractivePlatform;
import com.robot.game.interactiveObjects.Ladder;
import com.robot.game.interactiveObjects.MovingPlatform;
import com.robot.game.sprites.Bat;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Crab;

import static com.robot.game.util.Constants.*;


public class ObjectParser {

    private World world;
    private DelayedRemovalArray<InteractivePlatform> interactivePlatforms;
    private DelayedRemovalArray<Enemy> enemies;

    public ObjectParser(World world, Array<MapObjects> layersArray) {
        this.world = world;
        this.interactivePlatforms = new DelayedRemovalArray<>();
        this.enemies = new DelayedRemovalArray<>();
        for(MapObjects objects: layersArray)
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
        else if(object.getProperties().containsKey(SPIKE_PROPERTY)) {
            fixtureDef.filter.categoryBits = SPIKE_CATEGORY;
            fixtureDef.filter.maskBits = SPIKE_MASK;
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
                enemy = new Bat(body, fixtureDef, object);

            else //if(object.getProperties().containsKey(SPIDER_PROPERTY))
                enemy = new Crab(body, fixtureDef, object);
            this.enemies.add(enemy);
        }
        // create spikes
        else if(object.getProperties().containsKey(SPIKE_PROPERTY))
            body.createFixture(fixtureDef).setUserData(SPIKE_PROPERTY);
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
}
