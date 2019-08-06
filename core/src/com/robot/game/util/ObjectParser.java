package com.robot.game.util;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.entities.bat.BatAI;
import com.robot.game.entities.bat.BatPatrolling;
import com.robot.game.entities.crab.CrabAI;
import com.robot.game.entities.crab.CrabPatrolling;
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
    private Assets assets;
    private World world;
    private DelayedRemovalArray<InteractivePlatform> interactivePlatforms;
    private DelayedRemovalArray<Enemy> enemies;
    private DelayedRemovalArray<Collectable> collectables;
    private Trampoline trampoline;

    public ObjectParser(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.world = playScreen.getWorld();

        this.interactivePlatforms = new DelayedRemovalArray<>();
        this.enemies = new DelayedRemovalArray<>();
        this.collectables = new DelayedRemovalArray<>();

        for(MapObjects objects: playScreen.getLayersObjectArray())
            createTiledObjects(world, objects);

        System.out.println("Rectangles: " + rectangles + ", polylines: " + polylines + ", polygons: " + polygons);
    }

    private void createTiledObjects(World world, MapObjects objects) {

        for(MapObject object: objects) {
            BodyDef bodyDef = new BodyDef();

            if(object.getProperties().containsKey(FISH_PROPERTY)) {
                bodyDef.type = BodyDef.BodyType.DynamicBody;
                bodyDef.gravityScale = 0;
            }
            else if(object.getProperties().containsKey(FALLING_PLATFORM_PROPERTY) || object.getProperties().containsKey(MOVING_PLATFORM_PROPERTY) || object.getProperties().containsKey(ENEMY_PROPERTY))
                bodyDef.type = BodyDef.BodyType.KinematicBody;
            else
                bodyDef.type = BodyDef.BodyType.StaticBody;
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
                    this.enemies.add(new BatAI(playScreen, body, fixtureDef, object));
                else
                    this.enemies.add(new BatPatrolling(playScreen, body, fixtureDef, object));
            }
            // create crabs
            else if(object.getProperties().containsKey(CRAB_PROPERTY)) {
                if(object.getProperties().containsKey("aiPathFollowing"))
                    this.enemies.add(new CrabAI(playScreen, body, fixtureDef, object));
                else
                    this.enemies.add(new CrabPatrolling(playScreen, body, fixtureDef, object));
            }
            else if(object.getProperties().containsKey(FISH_PROPERTY))
                this.enemies.add(new Fish(playScreen, body, fixtureDef, object));
        }
        // create spikes
        else if(object.getProperties().containsKey(SPIKE_PROPERTY)) {
            new Spike(body, fixtureDef, object);
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

    public Trampoline getTrampoline() {
        return trampoline;
    }
}
