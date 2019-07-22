package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static com.robot.game.util.Constants.COLLECTABLE_OBJECT;
import static com.robot.game.util.Constants.LEVEL_1_JSON;

public class CollectableHandler {

    // This JSONArray stores all JSONObject of the collected items
    private JSONArray collectedItems;

    /* In this array I cache collectables to be disabled from being respawned if robot dies, because it has already
     * collected them. Thee will be disabled altogether when the robot dies, so as not to slow down the game when it
     * is in progress. */
    private Array<Integer> toDisableSpawning;

    public CollectableHandler() {
        this.collectedItems = new JSONArray();
        this.toDisableSpawning = new Array<>();
    }

    // parse json map file to determine if a collectable object should be spawned
    public static boolean shouldSpawn(int collectableID) {
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

    // parse json map file and override the boolean value of whether a particular collectable should be respawned
    public void setSpawn(int collectableID, boolean bool) {

        FileHandle file = Gdx.files.local(LEVEL_1_JSON);
        JSONObject root = null;

        try {
            root = (JSONObject) new JSONParser().parse(file.reader());

            JSONArray child1 = (JSONArray) root.get("layers");

            if(child1 != null) {
                for(int i = 0; i < child1.size(); i++) {
                    //System.out.println(((JSONObject) child1.get(i)).keySet());
                    JSONObject obj = ((JSONObject) child1.get(i));
                    if (COLLECTABLE_OBJECT.equals(obj.get("name"))) {
                        JSONArray child2 = (JSONArray) obj.get("objects");
                        //System.out.println(child2);

                        if (child2 != null) {
                            for(int j = 0; j < child2.size(); j++) {
                                //System.out.println("keyset" + ((JSONObject) child2.get(j)).keySet());
                                JSONObject obj2 = ((JSONObject) child2.get(j));
                                if((long) obj2.get("id") == collectableID) {
                                    JSONArray child3 = (JSONArray) obj2.get("properties");
                                    //System.out.println(child3);

                                    if(child3 != null) {
                                        for (int k = 0; k < child3.size(); k++) {
                                            //System.out.println(((JSONObject) child3.get(k)).keySet());
                                            JSONObject obj3 = ((JSONObject) child3.get(k));
                                            if("shouldSpawn".equals(obj3.get("name"))) {
                                                obj3.put("value", bool);

                                                JSONObject temp = new JSONObject();
                                                temp.put("id", obj2.get("id"));
                                                temp.put("value", obj3.get("value"));
                                                this.collectedItems.add(temp);
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
        catch (IOException | ParseException e) { e.printStackTrace(); }

        // finally save the json tiled map file
        if(root != null)
            FileSaver.saveJsonMap(file, root);
    }

    // getters
    public JSONArray getCollectedItems() {
        return collectedItems;
    }

    public Array<Integer> getToDisableSpawning() {
        return toDisableSpawning;
    }
}
