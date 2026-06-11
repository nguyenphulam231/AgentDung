package com.agentdung.game.managers;

import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.skills.PoopSkill;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class EntityManager {
    public EnemyManager enemyManager = new EnemyManager();
    public ProjectileManager projectileManager = new ProjectileManager();
    public Array<Rectangle> poopTraps = new Array<>();

    public void update(float delta, Player dung, MapManager mapManager, Runnable onPlayerDetected) {
        projectileManager.update(delta, enemyManager, mapManager);

        enemyManager.update(delta, mapManager, onPlayerDetected);

        // Logic bẫy
        for (var e : enemyManager.enemies) {
            Rectangle gRect = new Rectangle(e.getPosition().x, e.getPosition().y, e.getSize(), e.getSize());
            for (int i = poopTraps.size - 1; i >= 0; i--) {
                if (gRect.overlaps(poopTraps.get(i))) {
                    e.applyPoopEffect();
                    poopTraps.removeIndex(i);
                }
            }
        }
    }

    public void clearAll() {
        projectileManager.projectiles.clear();
        poopTraps.clear();
    }

    public Array<Projectile> getProjectiles() {
        return projectileManager.projectiles;
    }
    public Array<Enemy> getEnemies() {
        return enemyManager.enemies;
    }
    public void renderShapes(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        projectileManager.render(batch, shapeRenderer, false);
    }
    public void renderSprites(SpriteBatch batch, ShapeRenderer sr, Array<Skill> skills) {
        // Render Bẫy
        for (Skill s : skills) {
            if (s instanceof PoopSkill) {
                for (Rectangle trap : poopTraps) {
                    batch.draw(((PoopSkill) s).getPoopRegion(), trap.x, trap.y, trap.width, trap.height);
                }
                break;
            }
        }
        projectileManager.render(batch, sr, true);
    }

    public void dispose() {
        enemyManager.dispose();
        projectileManager.projectiles.clear();
        poopTraps.clear();
    }
}
