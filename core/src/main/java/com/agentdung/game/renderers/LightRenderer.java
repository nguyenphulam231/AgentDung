package com.agentdung.game.renderers;

import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.managers.MapManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class LightRenderer {
    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    private Texture lightMask;

    public LightRenderer() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);
        createLightMask();
    }

    private void createLightMask() {
        int size = 256;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float center = size / 2f;
        float maxDist = size / 2f;
        float solidRadius = 0.7f;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dist = Vector2.dst(x, y, center, center) / maxDist;
                float alpha = (dist < solidRadius) ? 1.0f : (dist < 1.0f ? 1.0f - (dist - solidRadius) / (1.0f - solidRadius) : 0);
                pixmap.drawPixel(x, y, Color.rgba8888(1, 1, 1, alpha));
            }
        }
        lightMask = new Texture(pixmap);
        pixmap.dispose();
    }

    public void renderDarkness(SpriteBatch batch, Player dung, OrthographicCamera camera) {
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0.9f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        float viewRadius = 350f;
        batch.draw(lightMask,
            dung.getPosition().x + dung.getSize()/2f - viewRadius/2f,
            dung.getPosition().y + dung.getSize()/2f - viewRadius/2f,
            viewRadius, viewRadius);
        batch.end();
        fbo.end();

        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.draw(fboRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    public void renderEnemyVision(ShapeRenderer shapeRenderer, EntityManager entityManager, MapManager mapManager) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy e : entityManager.enemies) {
            e.drawVision(shapeRenderer, mapManager.wallRects);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        if (fbo != null) fbo.dispose();
        if (lightMask != null) lightMask.dispose();
    }
}
