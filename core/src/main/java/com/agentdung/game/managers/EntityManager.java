package com.agentdung.game.managers;

import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.SpriteProjectile;

import com.agentdung.game.skills.PoopSkill;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;

public class EntityManager {
    public Array<Enemy> enemies = new Array<>();
    public Array<Projectile> projectiles = new Array<>();
    public Array<Rectangle> poopTraps = new Array<>();

    // Cập nhật hàm khởi tạo: Truyền thêm Player dung vào để gán đích ngắm AI cho Enemy
    public void initEnemies(MapManager mapManager, Player dung) {
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

    public void update(float delta, Player dung, MapManager mapManager, Runnable onPlayerDetected) {
        // ---- 1. CẬP NHẬT ĐẠN (GIỮ NGUYÊN LOGIC GỐC) ----
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
                if (pRect.overlaps(new Rectangle(
                    e.getPosition().x,
                    e.getPosition().y,
                    e.getSize(),
                    e.getSize()))) {

                    p.applyEffect(e);

                    projectiles.removeIndex(i);
                    hit = true;
                    break;
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

        // ---- 2. CẬP NHẬT QUÁI & XỬ LÝ VA CHẠM TẬP TRUNG ----
        for (Enemy e : enemies) {
            // Bước A: Quái tự chạy update logic AI không tham số thừa
            e.update(delta);

            // Bước B: Gọi hàm va chạm trung tâm, quét dựa trên dữ liệu MapManager
            moveEntityWithWallCollision(e, delta, mapManager);

            // Bước C: Quái tự kiểm tra tầm nhìn quét người chơi
            if (e.detects(mapManager.wallRects)) {
                onPlayerDetected.run();
                return;
            }

            // Bước D: Kiểm tra giẫm bẫy mìn phân
            Rectangle guardRect = new Rectangle(e.getPosition().x, e.getPosition().y, e.getSize(), e.getSize());
            for (int i = poopTraps.size - 1; i >= 0; i--) {
                if (guardRect.overlaps(poopTraps.get(i))) {
                    e.applyPoopEffect();
                    poopTraps.removeIndex(i);
                }
            }
        }
    }

    /**
     * HÀM VẬT LÝ DÙNG CHUNG (TÁI SỬ DỤNG MÃ NGUỒN) - CHUẨN OOP:
     * ĐÃ SỬA: Loại bỏ hoàn toàn phép nhân 'currentSpeed' dư thừa.
     * Vì cả Player (từ InputHandler) lẫn Enemy (từ AI nội bộ) đều đã tự nhân speed vào vector vận tốc gốc.
     * Hàm này giờ chỉ làm đúng trách nhiệm cộng di chuyển vật lý 'velocity * delta' và xử lý trượt tường.
     */
    public void moveEntityWithWallCollision(com.agentdung.game.entities.Entity entity, float delta, MapManager mapManager) {
        if (entity.getVelocity().len() <= 0.1f) return;

        // --- XỬ LÝ KIỂM TRA DI CHUYỂN THEO TRỤC X ---
        float oldX = entity.getPosition().x;
        entity.getPosition().x += entity.getVelocity().x * delta;
        Rectangle rectX = new Rectangle(entity.getPosition().x, entity.getPosition().y, entity.getSize(), entity.getSize());

        boolean collideX = false;
        for (com.agentdung.game.entities.Wall w : mapManager.walls) {
            if (Intersector.overlaps(rectX, w.bounds)) { collideX = true; break; }
        }
        for (com.agentdung.game.entities.Door d : mapManager.doors) {
            if (!d.isOpen && Intersector.overlaps(rectX, d.bounds)) { collideX = true; break; }
        }
        if (collideX) {
            entity.getPosition().x = oldX; // Trả về vị trí cũ nếu va chạm
        }

        // --- XỬ LÝ KIỂM TRA DI CHUYỂN THEO TRỤC Y ---
        float oldY = entity.getPosition().y;
        entity.getPosition().y += entity.getVelocity().y * delta;
        Rectangle rectY = new Rectangle(entity.getPosition().x, entity.getPosition().y, entity.getSize(), entity.getSize());

        boolean collideY = false;
        for (com.agentdung.game.entities.Wall w : mapManager.walls) {
            if (Intersector.overlaps(rectY, w.bounds)) { collideY = true; break; }
        }
        for (com.agentdung.game.entities.Door d : mapManager.doors) {
            if (!d.isOpen && Intersector.overlaps(rectY, d.bounds)) { collideY = true; break; }
        }
        if (collideY) {
            entity.getPosition().y = oldY; // Trả về vị trí cũ nếu va chạm
        }
    }

    public void renderShapes(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        for (Projectile p : projectiles) {
            if (!(p instanceof SpriteProjectile)) {
                p.render(batch, shapeRenderer);
            }
        }
    }

    public void renderSprites(SpriteBatch batch, ShapeRenderer shapeRenderer, Array<Skill> skills) {
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

        for (Projectile p : projectiles) {
            if (p instanceof SpriteProjectile) {
                p.render(batch, shapeRenderer);
            }
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
