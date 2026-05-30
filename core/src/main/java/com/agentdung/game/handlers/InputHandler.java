package com.agentdung.game.handlers;

import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.managers.MapManager;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class InputHandler {

    public static void handleTankMovement(float delta, Player dung, OrthographicCamera camera, MapManager mapManager) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        float cx = dung.getPosition().x + dung.getSize() / 2f;
        float cy = dung.getPosition().y + dung.getSize() / 2f;
        dung.setAngle(MathUtils.atan2(mousePos.y - cy, mousePos.x - cx) * MathUtils.radiansToDegrees);

        float moveSpeed = 140f;
        dung.getVelocity().set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dung.getVelocity().x =  MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            dung.getVelocity().y =  MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dung.getVelocity().x = -MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            dung.getVelocity().y = -MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }

        float oldX = dung.getPosition().x;
        dung.getPosition().x += dung.getVelocity().x * delta;
        Rectangle dungRectX = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        for (com.agentdung.game.entities.Wall w : mapManager.walls) if (Intersector.overlaps(dungRectX, w.bounds)) { dung.getPosition().x = oldX; break; }
        for (com.agentdung.game.entities.Door d : mapManager.doors) if (!d.isOpen && Intersector.overlaps(dungRectX, d.bounds)) { dung.getPosition().x = oldX; break; }

        float oldY = dung.getPosition().y;
        dung.getPosition().y += dung.getVelocity().y * delta;
        Rectangle dungRectY = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        for (com.agentdung.game.entities.Wall w : mapManager.walls) if (Intersector.overlaps(dungRectY, w.bounds)) { dung.getPosition().y = oldY; break; }
        for (com.agentdung.game.entities.Door d : mapManager.doors) if (!d.isOpen && Intersector.overlaps(dungRectY, d.bounds)) { dung.getPosition().y = oldY; break; }
    }

    public static void handleSkillInput(Player dung, Array<Skill> skills, EntityManager entityManager) {
        Enemy target = null;
        float minDistance = 180f;
        for (Enemy e : entityManager.enemies) {
            float d = com.badlogic.gdx.math.Vector2.dst(dung.getPosition().x, dung.getPosition().y, e.getPosition().x, e.getPosition().y);
            if (d < minDistance) { minDistance = d; target = e; }
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            skills.get(0).activate(dung, target, entityManager.projectiles);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (skills.get(1).activate(dung, target, entityManager.projectiles))
                entityManager.poopTraps.add(new Rectangle(dung.getPosition().x + 5, dung.getPosition().y + 5, 16, 16));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) skills.get(2).activate(dung, target, entityManager.projectiles);
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) skills.get(3).activate(dung, target, entityManager.projectiles);
    }
}
