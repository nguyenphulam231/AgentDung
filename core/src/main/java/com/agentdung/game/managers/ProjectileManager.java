package com.agentdung.game.managers;

import com.agentdung.game.projectiles.Projectile;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class ProjectileManager {
    public Array<Projectile> projectiles = new Array<>();

    public void update(float delta, EnemyManager enemyManager, MapManager mapManager) {
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
            if (!p.isActive()) { projectiles.removeIndex(i); continue; }

            Rectangle pRect = new Rectangle(p.getPosition().x, p.getPosition().y, 5, 5);

            // Va chạm tường
            for (var w : mapManager.walls) {
                if (pRect.overlaps(w.bounds)) { projectiles.removeIndex(i); return; }
            }

            // Va chạm địch
            for (var e : enemyManager.enemies) {
                if (pRect.overlaps(new Rectangle(e.getPosition().x, e.getPosition().y, e.getSize(), e.getSize()))) {
                    p.applyEffect(e);
                    projectiles.removeIndex(i);
                    return;
                }
            }

            // Va chạm server
            if (p.getColor().equals(Color.YELLOW) && mapManager.targetServer != null) {
                if (pRect.overlaps(new Rectangle(mapManager.targetServer.x, mapManager.targetServer.y, mapManager.targetServer.width, mapManager.targetServer.height))) {
                    mapManager.targetServer.takeDamage(40 * delta);
                    projectiles.removeIndex(i);
                }
            }
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer sr, boolean spriteBased) {
        for (Projectile p : projectiles) {
            if (p.isSpriteBased() == spriteBased) p.render(batch, sr);
        }
    }
}
