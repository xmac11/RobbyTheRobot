package com.robot.game.util.checkpoints;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static com.robot.game.util.constants.Constants.*;

public class FileSaver {

    private static Json json = new Json();
    private static FileHandle checkPointFile = Gdx.files.local(FOLDER_NAME + "checkpoints.json");
    private static FileHandle collectedItemsFile = Gdx.files.local(FOLDER_NAME + "collectedItems.json");

    // CHECKPOINTS

    /** Saves the game data needed for checkpoints to a json file */
    public static void saveCheckpointData(CheckpointData checkpointData) {
//        if(checkpointData != null) {
            json.setOutputType(JsonWriter.OutputType.json);
            checkPointFile.writeString(json.prettyPrint(checkpointData), false);
            Gdx.app.log("FileSaver", "Checkpoint data saved");
//        }
    }

    /** Reads the json file containing checkpoints data and returns the data as a class instance */
    public static CheckpointData loadCheckpointData() {
        return json.fromJson(CheckpointData.class, checkPointFile);
    }

    // COLLECTABLES

    /** Saves the new root to the specified file (overrides the json tiled map file) */
    public static void saveJsonMap(FileHandle file, JSONObject root) {
        json.setOutputType(JsonWriter.OutputType.json);
        file.writeString(json.prettyPrint(root.toString()), false);
        Gdx.app.log("FileSaver", "Json Map Saved");
    }

    /** Loops through a given JSONArray of collected items and saves their IDs to a json file */
    public static void saveCollectedItems(JSONArray collectedItems) {
        json.setOutputType(JsonWriter.OutputType.json);
        JSONArray jsonArray;

        // if the file exists, load it to JSONArray, otherwise create new one
        if(collectedItemsFile.exists()) {
            jsonArray = loadCollectedItemsFile();
        }
       else {
            jsonArray = new JSONArray();
        }

        for(Object object: collectedItems) {
            jsonArray.add(object);
        }

        collectedItemsFile.writeString(json.prettyPrint(jsonArray.toString()), false);
        Gdx.app.log("FileSaver", "collectedItems.json was saved");
    }

    /** When the robot dies and has no more lives, this method reads the saved file of collected items
     * (whose spawning has been disabled) and resets their spawning in the corresponding level to TRUE */
    public static void resetSpawningOfCollectables(int levelID) {

        // read the file with collected items and store them in a JSONArray
        JSONArray jsonArray = loadCollectedItemsFile();

        for(Object object: jsonArray) {
            JSONObject obj = (JSONObject) object;
            FileSaver.resetSpawningOfCollectable((long) obj.get("id"), levelID);
        }

        Gdx.app.log("FileSaver", "Collectables reset");
    }

    /** Reads the json file containing the items that have been collected and disabled from being spawned
     *  and returns them as a JSONArray */
    public static JSONArray loadCollectedItemsFile() {
        JSONArray jsonArray = null;

        try {
            jsonArray = (JSONArray) new JSONParser().parse(collectedItemsFile.reader());
        }
        catch (IOException | ParseException e) { e.printStackTrace();}

        return jsonArray;
    }

    // getter for the checkpoint file
    public static FileHandle getCheckpointFile() {
        return checkPointFile;
    }

    // getter for the collected items file
    public static FileHandle getCollectedItemsFile() {
        return collectedItemsFile;
    }

    /** Given the ID of the collectable item to be reset to be spawned and the level it refers to,
     *  the method reads the tiled map json file, overrides the boolean value of whether a particular
     *  collectable should be respawned to TRUE.
     *  Finally saves the tiled map json file */
    public static void resetSpawningOfCollectable(long collectableID, int levelID) {
        FileHandle file;
        // if on android
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            file = Gdx.files.local(Gdx.files.getLocalStoragePath() + "level" + levelID + ".json");
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

                        if(child2 != null) {
                            for(int j = 0; j < child2.size(); j++) {
                                if(shouldBreakJ)
                                    break;
                                //System.out.println("keyset" + ((JSONObject) child2.get(j)).keySet());
                                JSONObject obj2 = ((JSONObject) child2.get(j));
                                //System.out.println((long) obj2.get("id"));
                                if((long) obj2.get("id") == collectableID) {
                                    shouldBreakJ = true;
                                    JSONArray child3 = (JSONArray) obj2.get("properties");
                                    //System.out.println(child3);

                                    if(child3 != null) {
                                        for(int k = 0; k < child3.size(); k++) {
                                            //System.out.println(((JSONObject) child3.get(k)).keySet());
                                            JSONObject obj3 = ((JSONObject) child3.get(k));
                                            if(COLLECTABLE_SPAWNING_PROPERTY.equals(obj3.get("name"))) {
                                                obj3.put("value", true);
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
}
