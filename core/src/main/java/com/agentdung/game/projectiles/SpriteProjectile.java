package com.agentdung.game.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.agentdung.game.entities.Enemy;

public class SpriteProjectile extends Projectile {

    private TextureRegion textureRegion;
    private float width;
    private float height;
    private float angle;

    public SpriteProjectile(float x, float y, float angle, float speed, TextureRegion textureRegion) {
        this(x, y, angle, speed, textureRegion, Color.WHITE);
    }

    public SpriteProjectile(float x, float y, float angle, float speed,
                            TextureRegion textureRegion, Color color) {
        super(x, y, angle, speed, color, 0.4f);

        this.textureRegion = textureRegion;
        this.angle = angle;
        this.width = textureRegion.getRegionWidth();
        this.height = textureRegion.getRegionHeight();
    }

    public SpriteProjectile(float x, float y, float angle, float speed,
                            TextureRegion textureRegion,
                            float customWidth, float customHeight) {

        super(x, y, angle, speed, Color.WHITE, 0.4f);

        this.textureRegion = textureRegion;
        this.angle = angle;
        this.width = customWidth;
        this.height = customHeight;
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        batch.draw(
            textureRegion,
            position.x - width / 2f,
            position.y - height / 2f,
            width / 2f,
            height / 2f,
            width,
            height,
            1f,
            1f,
            this.angle
        );
    }

    @Override
    public boolean isSpriteBased() {
        return true;
    }

    @Override
    public void applyEffect(Enemy enemy) {
        if (color.equals(Color.CYAN)) {
            enemy.applySpitEffect();
        }
        else if (color.equals(Color.WHITE)) {
            enemy.applyVomitEffect();
        }
    }
}
