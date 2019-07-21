package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.robot.game.sprites.Collectable;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class FileSaver {

    private static Json json = new Json();
    private static FileHandle file = Gdx.files.local("/files/checkpoints.json");
    private static FileHandle collectableFile = Gdx.files.local("collectable.json");

    public static void saveData(GameData gameData) {
        json.setOutputType(JsonWriter.OutputType.json);
        file.writeString(json.prettyPrint(gameData), false);
        Gdx.app.log("FileSaver", "Data Saved");
    }

    public static void saveCollectable(FileHandle file, JSONObject root) {
        json.setOutputType(JsonWriter.OutputType.json);
        file.writeString(json.prettyPrint(root.toString()), false);
    }

    public static GameData loadData() {
        return json.fromJson(GameData.class, file);
    }

    public static FileHandle getFile() {
        return file;
    }
}
