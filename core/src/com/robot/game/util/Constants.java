package com.robot.game.util;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public final class Constants {

    // Screen - Camera
    public static final float WIDTH = 768;
    public static final float HEIGHT = 432;
    public static final float PPM = 32;
    public static final float DEBUG_CAM_SPEED = 32;


    // Tiled map editor
    private static TiledMap tiledMap = new TmxMapLoader().load("level1.tmx");
    private static MapProperties mapProperties = tiledMap.getProperties();
    public static final int TILE_SIZE = mapProperties.get("tilewidth", Integer.class);
//    public static final int TILE_SIZE = 16;
    public static final float MAP_WIDTH = mapProperties.get("width", Integer.class) * TILE_SIZE;
    public static final float MAP_HEIGHT = mapProperties.get("height", Integer.class) * TILE_SIZE;
    public static final String GROUND_OBJECT = "Ground obj";
    public static final String LADDER_OBJECT = "Ladder obj";

    // COLLISIONS

    // Box2D filter category bits
    public static final short NOTHING_CATEGORY = 0;
    public static final short ROBOT_CATEGORY = 1;
    public static final short GROUND_CATEGORY = 2;
    public static final short LADDER_CATEGORY = 4;

    // Box2D filter mask bits
    public static final short ROBOT_MASK = GROUND_CATEGORY | LADDER_CATEGORY;
    public static final short GROUND_MASK = ROBOT_CATEGORY;
    public static final short LADDER_MASK = ROBOT_CATEGORY;


    // Robot
    public static final float ROBOT_RADIUS = 16;




}
