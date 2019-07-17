package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class FileSaver {

    private static Json json = new Json();
    private static FileHandle file = Gdx.files.local("/files/checkpoints.json");

    public static void saveData(GameData gameData) {
        json.setOutputType(JsonWriter.OutputType.json);
        file.writeString(json.prettyPrint(gameData), false);
        Gdx.app.log("FileSaver", "Data Saved");
    }

    public static GameData loadData() {
        return json.fromJson(GameData.class, file);
    }

    public static FileHandle getFile() {
        return file;
    }
}
