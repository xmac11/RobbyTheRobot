package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.robot.game.sprites.Collectable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import static com.robot.game.util.Constants.COLLECTABLE_OBJECT;
import static com.robot.game.util.Constants.LEVEL_1_JSON;

public class FileSaver {

    private static Json json = new Json();
    private static FileHandle file = Gdx.files.local("/files/checkpoints.json");
    private static FileHandle collectedFile = Gdx.files.local("/files/collectedItems.json");

    public static void saveData(GameData gameData) {
        json.setOutputType(JsonWriter.OutputType.json);
        file.writeString(json.prettyPrint(gameData), false);
        Gdx.app.log("FileSaver", "Data Saved");
    }

    public static void saveJsonMap(FileHandle file, JSONObject root) {
        json.setOutputType(JsonWriter.OutputType.json);
        file.writeString(json.prettyPrint(root.toString()), false);
        Gdx.app.log("FileSaver", "Json Map Saved");
    }

    public static void saveCollectedItems(/*Array<JSONObject>*/JSONArray collectedItems) {
        json.setOutputType(JsonWriter.OutputType.json);
        JSONArray jsonArray;
        if(collectedFile.exists()) {
            jsonArray = loadCollectedFile();
        }
       else {
            jsonArray = new JSONArray();
        }


        for(Object object: collectedItems)
            jsonArray.add(object);
        collectedFile.writeString(json.prettyPrint(jsonArray.toString()), false);
        Gdx.app.log("FileSaver", "collectedItems.json was saved");
    }

    public static void resetSpawningOfCollectables() {

        JSONArray jsonArray = loadCollectedFile();

        for(Object object: jsonArray) {
            JSONObject obj = (JSONObject) object;
            methodname((long) obj.get("id"));
        }
        Gdx.app.log("FileSaver", "Collectables reset");
    }

    public static GameData loadData() {
        return json.fromJson(GameData.class, file);
    }

    public static JSONArray loadCollectedFile() {
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) new JSONParser().parse(collectedFile.reader());
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static FileHandle getFile() {
        return file;
    }

    public static FileHandle getCollectedFile() {
        return collectedFile;
    }

    public static void methodname(long collectableID) {
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
                                System.out.println((long) obj2.get("id"));
                                if ((long) obj2.get("id") == collectableID) {
                                    JSONArray child3 = (JSONArray) obj2.get("properties");
                                    //System.out.println(child3);

                                    if (child3 != null) {
                                        for (int k = 0; k < child3.size(); k++) {
                                            //System.out.println(((JSONObject) child3.get(k)).keySet());
                                            JSONObject obj3 = ((JSONObject) child3.get(k));
                                            if ("shouldSpawn".equals(obj3.get("name"))) {
                                                Gdx.app.log("Collectable", "Before: " + obj3.get("value"));
                                                obj3.put("value", true);
                                                Gdx.app.log("Collectable", "After: " + obj3.get("value"));
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
}
