package com.agentdung.game.core;

import com.agentdung.game.entities.Wall;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class LevelProvider {
    public Array<Wall> walls = new Array<>();
    public Vector2 dungSpawn = new Vector2();
    public Vector2 guardSpawn = new Vector2();
    public Vector2 guardPatrolEnd = new Vector2();
    public Vector2 serverPos = new Vector2();

    // Kích thước mỗi ô gạch (Tile Size)
    public static final int TILE_SIZE = 40;

    public static LevelProvider getLevel(int world, int level) {
        LevelProvider lp = new LevelProvider();

        // Thiết kế Map bằng String cho trực quan
        // # là tường, . là trống, D là Dũng, G là Guard, E là End (Guard Patrol), S là Server
        String[] mapData;

        if (world == 1 && level == 1) {
            mapData = new String[]{
                "##################################################",
                "#................................................#",
                "#...S............................................#",
                "#........#######.................................#",
                "#........#.....#........................G....E...#",
                "#...D....#.....#.................................#",
                "#........#.......................................#",
                "##################################################"
            };
        } else if (world == 1 && level == 2) {
            mapData = new String[]{
                "##################################################",
                "#D.......#..........#............................#",
                "###......#....G.....#.......#######.......S......#",
                "#........#..........#.......#.....#..............#",
                "#....#####....E.....#########.....#..............#",
                "#................................................#",
                "##################################################"
            };
        } else {
            return getLevel(1, 1); // Mặc định
        }

        lp.parseMap(mapData);
        return lp;
    }

    // Hàm tự động chuyển đổi ký tự thành vật thể trong Game
    private void parseMap(String[] data) {
        // Duyệt từ dưới lên trên vì tọa độ Y trong LibGDX bắt đầu từ đáy
        for (int y = 0; y < data.length; y++) {
            String row = data[data.length - 1 - y];
            for (int x = 0; x < row.length(); x++) {
                char tile = row.charAt(x);
                float px = x * TILE_SIZE;
                float py = y * TILE_SIZE;

                switch (tile) {
                    case '#':
                        walls.add(new Wall(px, py, TILE_SIZE, TILE_SIZE));
                        break;
                    case 'D':
                        dungSpawn.set(px, py);
                        break;
                    case 'G':
                        guardSpawn.set(px, py);
                        break;
                    case 'E':
                        guardPatrolEnd.set(px, py);
                        break;
                    case 'S':
                        serverPos.set(px, py);
                        break;
                }
            }
        }
    }
}
