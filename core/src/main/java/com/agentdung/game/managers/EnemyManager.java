package com.agentdung.game.managers;

import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {
    public Array<Enemy> enemies = new Array<>();

    public void init(MapManager mapManager, Player dung) {
        enemies.clear();
        for (Integer guardId : mapManager.enemyStarts.keySet()) {
            if (mapManager.enemyEnds.containsKey(guardId)) {
                Enemy guard = new Enemy(mapManager.enemyStarts.get(guardId).x, mapManager.enemyStarts.get(guardId).y, dung);
                guard.setPatrolRoute(mapManager.enemyStarts.get(guardId).x, mapManager.enemyStarts.get(guardId).y,
                    mapManager.enemyEnds.get(guardId).x, mapManager.enemyEnds.get(guardId).y);
                enemies.add(guard);
            }
        }
    }

    public void update(float delta, MapManager mapManager, Runnable onPlayerDetected) {
        for (Enemy e : enemies) {
            e.update(delta);
            CollisionUtils.moveWithCollision(e, delta, mapManager);
            if (e.detects(mapManager.wallRects)) onPlayerDetected.run();
        }
    }

    public void dispose() {
        for (Enemy e : enemies) e.dispose();
        enemies.clear();
    }
}
