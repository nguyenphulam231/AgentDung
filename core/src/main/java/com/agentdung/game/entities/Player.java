package com.agentdung.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Player extends Entity {
    private float maxMana = 100f;
    private float currentMana = 100f;
    private float regenRate = 10f;

    public Player(float x, float y) {
        super(x, y, 200, 30); // Speed 200, Size 30
    }

    @Override
    public void update(float delta, Player self) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        float dx = mouseX - (position.x + size / 2);
        float dy = mouseY - (position.y + size / 2);

        // mouse angle update
        this.angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

        // move
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.x += MathUtils.cosDeg(angle) * speed * delta;
            position.y += MathUtils.sinDeg(angle) * speed * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.x -= MathUtils.cosDeg(angle) * speed * delta;
            position.y -= MathUtils.sinDeg(angle) * speed * delta;
        }

        // 3. Hồi mana theo thời gian
        if (currentMana < maxMana) {
            currentMana += regenRate * delta;
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        // player
        shape.setColor(Color.GREEN);
        shape.rect(position.x, position.y, size / 2, size / 2, size, size, 1, 1, angle);

        // nose
        shape.setColor(Color.FOREST);
        float noseWidth = 10;
        float noseHeight = 6;
        shape.rect(position.x + size - 5, position.y + (size / 2) - (noseHeight / 2),
            - (size / 2 - 5), noseHeight / 2, noseWidth, noseHeight, 1, 1, angle);

        // Small mana bar
        shape.setColor(Color.GRAY);
        shape.rect(position.x, position.y + size + 5, size, 5);
        shape.setColor(Color.CYAN);
        shape.rect(position.x, position.y + size + 5, size * (currentMana / maxMana), 5);
    }

    public float getCurrentMana() { return currentMana; }
    public float getAngle() { return angle; }
}
