package com.agentdung.game.managers;

import com.agentdung.game.assets.GameAssets; // Import GameAssets vào manager
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {
    public Array<Enemy> enemies = new Array<>();

    // Thêm tham số GameAssets assets vào hàm init
    public void init(MapManager mapManager, Player dung, GameAssets assets) {
        enemies.clear();
        for (Integer guardId : mapManager.enemyStarts.keySet()) {
            if (mapManager.enemyEnds.containsKey(guardId)) {
                // Truyền thêm assets vào constructor của Enemy ở đây
                Enemy guard = new Enemy(
                    mapManager.enemyStarts.get(guardId).x,
                    mapManager.enemyStarts.get(guardId).y,
                    dung,
                    assets
                );

                guard.setPatrolRoute(
                    mapManager.enemyStarts.get(guardId).x, mapManager.enemyStarts.get(guardId).y,
                    mapManager.enemyEnds.get(guardId).x, mapManager.enemyEnds.get(guardId).y
                );
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
        // Không gọi e.dispose() nữa vì texture của Enemy đã do GameAssets quản lý toàn cục
        enemies.clear();
    }
}
