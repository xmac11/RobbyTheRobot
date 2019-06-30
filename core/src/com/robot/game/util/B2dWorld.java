package com.robot.game.util;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.robot.game.util.Constants.PPM;


public class B2dWorld {

    public static void createTiledObjects(World world, MapObjects objects) {

        /* ChainShapes are meant as you use them, for terrain and other static stuff.
        If you create a body with ChainShape and make it dynamic, it won't behave well.
        It probably won't rotate, and there will be no collisions with other chainshapes (they are not defined in box2d).

        So ChainShapes are meant for static bodies only.*/

        for(MapObject object: objects) {
            BodyDef bodyDef = new BodyDef();
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
                body.createFixture(fixtureDef);

                polygonShape.dispose();
            }
            else if(object instanceof PolylineMapObject) {
                Shape shape =  createPolyline((PolylineMapObject) object);
                body = world.createBody(bodyDef);
                fixtureDef.shape = shape;
                body.createFixture(fixtureDef);

                shape.dispose();
            }
            else if(object instanceof PolygonMapObject) {
                Shape shape =  createPolygon((PolygonMapObject) object);
                body = world.createBody(bodyDef);
                fixtureDef.shape = shape;
                body.createFixture(fixtureDef);

                shape.dispose();
            }
            else continue;
        }
    }

    private static ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices =  polyline.getPolyline().getTransformedVertices();
        Vector2[] worldVertices =  new Vector2[vertices.length / 2];

        for(int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[2*i] / PPM, vertices[2*i+1] / PPM);
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);

        return chainShape;
    }

    private static ChainShape createPolygon(PolygonMapObject polygon) {
        float[] vertices =  polygon.getPolygon().getTransformedVertices();
        Vector2[] worldVertices =  new Vector2[vertices.length / 2];

        for(int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[2*i] / PPM, vertices[2*i+1] / PPM);
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);

        return chainShape;
    }

}
