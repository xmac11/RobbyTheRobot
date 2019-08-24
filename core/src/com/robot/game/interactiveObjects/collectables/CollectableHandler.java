package com.robot.game.interactiveObjects.collectables;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.robot.game.util.checkpoints.FileSaver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static com.robot.game.util.Constants.*;

public class CollectableHandler {

    // reference of the current level
    private int levelID;

    // This JSONArray stores all JSONObject of the collected items
    private JSONArray collectedItems;

    /* In this array collectables are cached in order to be disabled from being respawned if the robot dies, because it has already
     * collected them. They will be disabled altogether when the robot dies, so as not to slow down the game when it is in progress. */
    private Array<Integer> itemsToDisableSpawning;

    public CollectableHandler(int levelID) {
        this.levelID = levelID;
        this.collectedItems = new JSONArray();
        this.itemsToDisableSpawning = new Array<>();
    }

    // parse json map file to determine if a collectable object should be spawned
    public boolean shouldSpawn(int collectableID) {
        JsonReader reader = new JsonReader();
        JsonValue root;
        // if on android
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            root = reader.parse(Gdx.files.local(Gdx.files.getLocalStoragePath() + "level" + levelID + ".json"));
        }
        else {
            root = reader.parse(Gdx.files.internal(FOLDER_NAME + "level" + levelID + ".json"));
        }
        JsonValue child1 = root.get("layers");

        boolean shouldBreakI = false;
        boolean shouldBreakJ = false;

        for (int i = 0; i < child1.size; i++) {

            if(shouldBreakI)
                break;
            if (child1.get(i).has("name") && COLLECTABLE_OBJECT.equals(child1.get(i).getString("name"))) {
                shouldBreakI = true;
                JsonValue child2 = child1.get(i).get("objects");
                //                System.out.println(child2);

                for (int j = 0; j < child2.size; j++) {
                    if(shouldBreakJ)
                        break;
                    if (child2.get(j).has("id") && child2.get(j).getInt("id") == collectableID) {
                        shouldBreakJ = true;
                        JsonValue child3 = child2.get(j).get("properties");

                        for(int k = 0; k < child3.size; k++) {
                            if(COLLECTABLE_SPAWNING_PROPERTY.equals(child3.get(k).getString("name"))) {
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
        FileHandle file;
        // if on android
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            file = Gdx.files.local(Gdx.files.getLocalStoragePath() + "level" + levelID + ".json");
            //System.out.println(file.exists());
        }
        else {
            file = Gdx.files.local(FOLDER_NAME + "level" + levelID + ".json");
        }
        JSONObject root = null;

        try {
            root = (JSONObject) new JSONParser().parse(file.reader());

            JSONArray child1 = (JSONArray) root.get("layers");

            boolean shouldBreakI = false;
            boolean shouldBreakJ = false;

            if(child1 != null) {
                for(int i = 0; i < child1.size(); i++) {
                    if(shouldBreakI)
                        break;
                    //System.out.println(((JSONObject) child1.get(i)).keySet());
                    JSONObject obj = ((JSONObject) child1.get(i));
                    if(COLLECTABLE_OBJECT.equals(obj.get("name"))) {
                        shouldBreakI = true;
                        JSONArray child2 = (JSONArray) obj.get("objects");
                        //System.out.println(child2);

                        if (child2 != null) {
                            for(int j = 0; j < child2.size(); j++) {
                                if(shouldBreakJ)
                                    break;
                                //System.out.println("keyset" + ((JSONObject) child2.get(j)).keySet());
                                JSONObject obj2 = ((JSONObject) child2.get(j));
                                if((long) obj2.get("id") == collectableID) {
                                    shouldBreakJ = true;
                                    JSONArray child3 = (JSONArray) obj2.get("properties");
                                    //System.out.println(child3);

                                    if(child3 != null) {
                                        for(int k = 0; k < child3.size(); k++) {
                                            //System.out.println(((JSONObject) child3.get(k)).keySet());
                                            JSONObject obj3 = ((JSONObject) child3.get(k));
                                            if(COLLECTABLE_SPAWNING_PROPERTY.equals(obj3.get("name"))) {
                                                obj3.put("value", bool);

                                                JSONObject temp = new JSONObject();
                                                temp.put("id", obj2.get("id"));
                                                temp.put("value", obj3.get("value"));
                                                this.collectedItems.add(temp);
                                                Gdx.app.log("CollectableHandler", "Item was put in JsonArray. " +
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

    public Array<Integer> getItemsToDisableSpawning() {
        return itemsToDisableSpawning;
    }

    public void setToNull() {
        //collectedItems = null;
        //itemsToDisableSpawning = null;
        Gdx.app.log("CollectableHandler", "Objects were set to null");
    }
}
