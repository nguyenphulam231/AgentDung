package com.agentdung.game.core;

import com.badlogic.gdx.math.Vector2;

public class LevelProvider {

    public static final int TILE_SIZE = 16;

    /**
     * Trả về đường dẫn file map dựa trên world và level
     */
    public static String getMapPath(int world, int level) {
        // Trả về maps/map1_1.tmx...
        return "maps/map" + world + "_" + level + ".tmx";
    }

}
