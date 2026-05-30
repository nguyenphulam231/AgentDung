package com.agentdung.game.managers;

import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.projectiles.BlobProjectile;
import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.SpriteProjectile;
import com.agentdung.game.projectiles.StreamProjectile;
import com.agentdung.game.skills.PoopSkill;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EntityManager {
    public Array<Enemy> enemies = new Array<>();
    public Array<Projectile> projectiles = new Array<>();
    public Array<Rectangle> poopTraps = new Array<>();

    public void initEnemies(MapManager mapManager) {
        enemies.clear();
        for (Integer guardId : mapManager.enemyStarts.keySet()) {
            if (mapManager.enemyEnds.containsKey(guardId)) {
                Enemy guard = new Enemy(mapManager.enemyStarts.get(guardId).x, mapManager.enemyStarts.get(guardId).y);
                guard.setPatrolRoute(mapManager.enemyStarts.get(guardId).x, mapManager.enemyStarts.get(guardId).y,
                    mapManager.enemyEnds.get(guardId).x, mapManager.enemyEnds.get(guardId).y);
                enemies.add(guard);
            }
        }
    }

    public void update(float delta, Player dung, MapManager mapManager, Runnable onPlayerDetected) {
        // Update Đạn
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
            if (!p.isActive()) { projectiles.removeIndex(i); continue; }

            Rectangle pRect = new Rectangle(p.getPosition().x, p.getPosition().y, 5, 5);
            boolean hit = false;

            for (com.agentdung.game.entities.Wall w : mapManager.walls) {
                if (pRect.overlaps(w.bounds)) { projectiles.removeIndex(i); hit = true; break; }
            }
            if (hit) continue;

            for (Enemy e : enemies) {
                if (pRect.overlaps(new Rectangle(e.getPosition().x, e.getPosition().y, e.getSize(), e.getSize()))) {
                    if (p instanceof BlobProjectile) { e.applySpitEffect(); }
                    else if (p instanceof SpriteProjectile) {
                        if (p.getColor().equals(Color.WHITE)) e.applyVomitEffect();
                        else if (p.getColor().equals(Color.CYAN)) e.applySpitEffect();
                    } else if (p instanceof StreamProjectile) {
                        if (p.getColor().equals(Color.YELLOW)) e.applyPeeEffect();
                        else if (p.getColor().equals(new Color(0.5f, 0.25f, 0, 1))) e.applyPoopEffect();
                        else if (p.getColor().equals(Color.WHITE)) e.applyVomitEffect();
                    }
                    projectiles.removeIndex(i); hit = true; break;
                }
            }
            if (hit) continue;

            if (p.getColor().equals(Color.YELLOW) && mapManager.targetServer != null) {
                Rectangle serverRect = new Rectangle(mapManager.targetServer.x, mapManager.targetServer.y, mapManager.targetServer.width, mapManager.targetServer.height);
                if (pRect.overlaps(serverRect)) {
                    mapManager.targetServer.takeDamage(40 * delta);
                    projectiles.removeIndex(i);
                }
            }
        }

        // Update Quái & Bẫy mìn
        for (Enemy e : enemies) {
            e.update(delta, dung, mapManager.wallRects);
            if (e.detects(dung, mapManager.wallRects)) {
                onPlayerDetected.run();
                return;
            }
            Rectangle guardRect = new Rectangle(e.getPosition().x, e.getPosition().y, e.getSize(), e.getSize());
            for (int i = poopTraps.size - 1; i >= 0; i--) {
                if (guardRect.overlaps(poopTraps.get(i))) {
                    e.applyPoopEffect();
                    poopTraps.removeIndex(i);
                }
            }
        }
    }

    public void renderShapes(ShapeRenderer shapeRenderer) {
        for (Projectile p : projectiles) {
            if (!(p instanceof SpriteProjectile)) p.render(shapeRenderer);
        }
    }

    public void renderSprites(SpriteBatch batch, Array<Skill> skills) {
        // Vẽ bẫy phân lấy ảnh từ PoopSkill như yêu cầu cũ
        PoopSkill poopSkillInstance = null;
        for (Skill s : skills) {
            if (s instanceof PoopSkill) { poopSkillInstance = (PoopSkill) s; break; }
        }
        if (poopSkillInstance != null && poopTraps.size > 0) {
            TextureRegion poopSprite = poopSkillInstance.getPoopRegion();
            for (Rectangle trap : poopTraps) {
                batch.draw(poopSprite, trap.x, trap.y, trap.width, trap.height);
            }
        }

        // Vẽ đạn có ảnh
        for (Projectile p : projectiles) {
            if (p instanceof SpriteProjectile) ((SpriteProjectile) p).render(batch);
        }
    }

    public void clearAll() {
        projectiles.clear();
        poopTraps.clear();
    }

    public void dispose() {
        for (Enemy e : enemies) e.dispose();
        enemies.clear();
        clearAll();
    }
}
