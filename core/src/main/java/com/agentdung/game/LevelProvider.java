package com.agentdung.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class LevelProvider {
    public Array<Wall> walls = new Array<>();
    public Vector2 dungSpawn = new Vector2();
    public Vector2 guardSpawn = new Vector2();
    public Vector2 guardPatrolEnd = new Vector2();
    public Vector2 serverPos = new Vector2();

    public static LevelProvider getLevel(int level) {
        LevelProvider lp = new LevelProvider();
        if (level == 1) {
            lp.walls.add(new Wall(0, 0, 2000, 20));
            lp.walls.add(new Wall(0, 580, 2000, 20));
            lp.walls.add(new Wall(500, 0, 30, 400));
            lp.dungSpawn.set(100, 300);
            lp.guardSpawn.set(800, 300);
            lp.guardPatrolEnd.set(1200, 300);
            lp.serverPos.set(1800, 300);
        } else if (level == 2) {
            // map2
            lp.walls.add(new Wall(0, 0, 1500, 20));
            lp.dungSpawn.set(50, 200);
            lp.serverPos.set(1300, 200);
        }
        return lp;
    }
}
