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
import com.robot.game.interactiveObjects.Ladder;
import com.robot.game.interactiveObjects.MovingPlatform;

import static com.robot.game.util.Constants.*;


public class B2dWorldCreator {

    private World world;
    private DelayedRemovalArray<FallingPlatform> fallingPlatforms;
    private Array<MovingPlatform> movingPlatforms;

    public B2dWorldCreator(World world, Array<MapObjects> layersArray) {
        this.world = world;
        this.fallingPlatforms = new DelayedRemovalArray<>();
        this.movingPlatforms = new Array<>();
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
            if(object.getProperties().containsKey(FALLING_PROPERTY) || object.getProperties().containsKey(MOVING_PROPERTY))
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
        else if(object.getProperties().containsKey(FALLING_PROPERTY)) {
            fixtureDef.filter.categoryBits = FALLING_PLATFORM_CATEGORY;
            fixtureDef.filter.maskBits = FALLING_PLATFORM_MASK;
        }
        // moving platform
        else if(object.getProperties().containsKey(MOVING_PROPERTY)) {
            fixtureDef.filter.categoryBits = MOVING_PLATFORM_CATEGORY;
            fixtureDef.filter.maskBits = MOVING_PLATFORM_MASK;
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
            new Ladder(body, fixtureDef);
        }
        // create falling platform
        else if(object.getProperties().containsKey(FALLING_PROPERTY)) {
            float delay = (float) object.getProperties().get("delay");
            FallingPlatform fallingPlatform = new FallingPlatform(world, body, fixtureDef, delay);
            this.fallingPlatforms.add(fallingPlatform);
        }
        // create moving platform
        else if(object.getProperties().containsKey(MOVING_PROPERTY)) {
            float vX = (float) object.getProperties().get("vX");
            float vY = (float) object.getProperties().get("vY");
            MovingPlatform movingPlatform = new MovingPlatform(world, body, fixtureDef, vX, vY);
            this.movingPlatforms.add(movingPlatform);
        }
        // create all other objects
        else {
            body.createFixture(fixtureDef);
        }
    }

    // getter for the falling platforms
    public DelayedRemovalArray<FallingPlatform> getFallingPlatforms() {
        return fallingPlatforms;
    }

    // getter for the moving platforms
    public Array<MovingPlatform> getMovingPlatforms() {
        return movingPlatforms;
    }
}
