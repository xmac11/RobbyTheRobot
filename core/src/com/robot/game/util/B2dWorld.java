package com.robot.game.util;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.interactiveObjects.Ladder;

import static com.robot.game.util.Constants.*;


public class B2dWorld {

    public static void createTiledObjects(World world, MapObjects objects) {

        /* ChainShapes are meant as you use them, for terrain and other static stuff.
        If you create a body with ChainShape and make it dynamic, it won't behave well.
        It probably won't rotate, and there will be no collisions with other chainshapes (they are not defined in box2d).

        So ChainShapes are meant for static bodies only.*/

        for(MapObject object: objects) {
            BodyDef bodyDef = new BodyDef();
            if(object.getProperties().containsKey("moving"))
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

    private static ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices =  polyline.getPolyline().getTransformedVertices();
        float[] worldVertices = new float[vertices.length];

        for(int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = vertices[i] / PPM;
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);

        return chainShape;
    }

    private static ChainShape createPolygon(PolygonMapObject polygon) {
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
    private static void assignFilterBits(FixtureDef fixtureDef, MapObject object) {
        if(object.getProperties().containsKey("ladder")) {
            fixtureDef.filter.categoryBits = LADDER_CATEGORY;
            fixtureDef.filter.maskBits = LADDER_MASK;
            fixtureDef.isSensor = true;
        }
        else {
            fixtureDef.filter.categoryBits = GROUND_CATEGORY;
            fixtureDef.filter.maskBits = GROUND_MASK;
        }
    }

    private static void createFixture(Body body, FixtureDef fixtureDef, MapObject object) {
        if(object.getProperties().containsKey("ladder")) {
            new Ladder(body, fixtureDef);
        }
        else {
            body.createFixture(fixtureDef);
        }
    }

}
