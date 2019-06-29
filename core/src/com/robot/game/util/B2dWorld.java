package com.robot.game.util;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
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
            Rectangle rectangle;
            if(object instanceof RectangleMapObject) {
                 rectangle = ((RectangleMapObject) object).getRectangle();
            }
            else continue;

            // create body
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            System.out.println(rectangle.getX() + " " + rectangle.getY());
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / PPM,
                                 (rectangle.getY() + rectangle.getHeight() / 2) / PPM);
            Body body = world.createBody(bodyDef);

            // create shape
            PolygonShape shape =  new PolygonShape();
            shape.setAsBox(rectangle.getWidth() / 2 / PPM,
                                rectangle.getHeight() / 2 / PPM);

            // create fixture
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);

            shape.dispose();
        }
    }

    private static ChainShape createPolygon(PolygonMapObject/*RectangleMapObject*/ polygon) {
        float[] vertices =  polygon.getPolygon().getTransformedVertices();
//        float[] worldVertices =  new float[vertices.length];
        Vector2[] worldVertices =  new Vector2[vertices.length / 2];

        for(int i = 0; i < worldVertices.length; i++) {
//            worldVertices[i] = vertices[i] / PPM;
            worldVertices[i] = new Vector2(vertices[i*2] / PPM, vertices[i*2+1] / PPM);
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);
        chainShape.dispose();

        System.out.println("returned");
        return chainShape;
    }

}
