package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.FileSaver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static com.robot.game.util.Constants.*;

public class Collectable extends Sprite {

    PlayScreen playScreen;
    private Sprite burgerSprite;
    private World world;
    private Body body;
    private MapObject object;
    private JSONObject temp;
    private boolean flagToCollect;
    private boolean isDestroyed;
//    private Array<JSONObject> collectedItems;
    private JSONArray collectedItems;

    public Collectable(PlayScreen playScreen, World world, Body body, FixtureDef fixtureDef, MapObject object, /*Array<JSONObject>*/JSONArray collectedItems) {
        this.playScreen = playScreen;
        this.world = world;
        this.body = body;
        this.object = object;
        this.temp = new JSONObject();
        this.collectedItems = collectedItems;
        body.createFixture(fixtureDef).setUserData(this);
        this.burgerSprite = new Sprite(Assets.getInstance().collectableAssets.burgerTexture);

        // set the size of the bat sprite
        burgerSprite.setSize(COLLECTABLE_WIDTH / PPM, COLLECTABLE_HEIGHT / PPM);
        // attach sprite to body
        burgerSprite.setPosition(body.getPosition().x - COLLECTABLE_WIDTH / 2 / PPM, body.getPosition().y - COLLECTABLE_HEIGHT / 2 / PPM);
    }

    public void update(float delta) {
        if(flagToCollect) {
            destroyBody();
        }
    }

    @Override
    public void draw(Batch batch) {
        burgerSprite.draw(batch);
    }

    public void setFlagToCollect() {
        this.flagToCollect = true;
        this.object.getProperties().put("spawn", false);


    }

    protected void destroyBody() {
        world.destroyBody(body);
        isDestroyed = true;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public MapObject getObject() {
        return object;
    }

    public void setSpawn(int collectableID, boolean bool) {

        FileHandle file = Gdx.files.local(LEVEL_1_JSON);
        JSONObject root = null;

        try {
            root = (JSONObject) new JSONParser().parse(file.reader());

            JSONArray child1 = (JSONArray) root.get("layers");

            if(child1 != null) {
                for (Object o : child1) {
                    //System.out.println(((JSONObject) child1.get(i)).keySet());
                    JSONObject obj = ((JSONObject) o);
                    if (COLLECTABLE_OBJECT.equals(obj.get("name"))) {
                        JSONArray child2 = (JSONArray) obj.get("objects");
                        //System.out.println(child2);

                        if (child2 != null) {
                            for (int j = 0; j < child2.size(); j++) {
                                //System.out.println("keyset" + ((JSONObject) child2.get(j)).keySet());
                                JSONObject obj2 = ((JSONObject) child2.get(j));
                                if ((long) obj2.get("id") == collectableID) {
                                    JSONArray child3 = (JSONArray) obj2.get("properties");
                                    //System.out.println(child3);

                                    if (child3 != null) {
                                        for (int k = 0; k < child3.size(); k++) {
                                            //System.out.println(((JSONObject) child3.get(k)).keySet());
                                            JSONObject obj3 = ((JSONObject) child3.get(k));
                                            if ("shouldSpawn".equals(obj3.get("name"))) {
                                                obj3.put("value", bool);

                                                temp.put("id", obj2.get("id"));
                                                temp.put("value", obj3.get("value"));
                                                collectedItems.add(temp);
                                                playScreen.setNewItemCollected(true);
                                                Gdx.app.log("Collectable", "Item was put in JsonArray. " +
                                                        "Size: " + collectedItems.size());
                                            }

                                        }
                                    }

                                }
                            }
                        }

                    }

                }
            }

        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        FileSaver.saveJsonMap(file, root);
    }



    public void getJsonCollectables() {

    }
}
