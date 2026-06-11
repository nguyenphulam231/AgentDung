package com.agentdung.game.managers;

import com.agentdung.game.entities.Entity;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class CollisionUtils {
    public static void moveWithCollision(Entity entity, float delta, MapManager mapManager) {
        if (entity.getVelocity().len() <= 0.1f) return;

        float oldX = entity.getPosition().x;
        entity.getPosition().x += entity.getVelocity().x * delta;
        Rectangle rectX = new Rectangle(entity.getPosition().x, entity.getPosition().y, entity.getSize(), entity.getSize());

        if (isCollidingWithMap(rectX, mapManager)) entity.getPosition().x = oldX;

        float oldY = entity.getPosition().y;
        entity.getPosition().y += entity.getVelocity().y * delta;
        Rectangle rectY = new Rectangle(entity.getPosition().x, entity.getPosition().y, entity.getSize(), entity.getSize());

        if (isCollidingWithMap(rectY, mapManager)) entity.getPosition().y = oldY;
    }

    private static boolean isCollidingWithMap(Rectangle rect, MapManager mapManager) {
        for (var w : mapManager.walls) if (Intersector.overlaps(rect, w.bounds)) return true;
        for (var d : mapManager.doors) {
            if (!d.isOpen && Intersector.overlaps(rect, d.getBounds())) return true;
        }
        return false;
    }
}
