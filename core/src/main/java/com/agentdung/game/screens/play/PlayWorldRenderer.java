package com.agentdung.game.screens.play;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.managers.MapManager;
import com.agentdung.game.renderers.LightRenderer;
import com.agentdung.game.screens.CapturedOverlay;
import com.agentdung.game.screens.GameHUD;
import com.agentdung.game.screens.InventoryOverlay;
import com.agentdung.game.screens.PauseOverlay;
import com.agentdung.game.screens.VendingMachineOverlay;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class PlayWorldRenderer {
    private final AgentDungGame game;
    private final MapManager mapManager;
    private final EntityManager entityManager;
    private final LightRenderer lightRenderer;
    private final GameHUD gameHUD;
    private final InventoryOverlay inventoryOverlay;
    private final VendingMachineOverlay vendingMachineOverlay;
    private final CapturedOverlay capturedOverlay;
    private final PauseOverlay pauseOverlay;

    public PlayWorldRenderer(
        AgentDungGame game,
        MapManager mapManager,
        EntityManager entityManager,
        LightRenderer lightRenderer,
        GameHUD gameHUD,
        InventoryOverlay inventoryOverlay,
        VendingMachineOverlay vendingMachineOverlay,
        CapturedOverlay capturedOverlay,
        PauseOverlay pauseOverlay
    ) {
        this.game = game;
        this.mapManager = mapManager;
        this.entityManager = entityManager;
        this.lightRenderer = lightRenderer;
        this.gameHUD = gameHUD;
        this.inventoryOverlay = inventoryOverlay;
        this.vendingMachineOverlay = vendingMachineOverlay;
        this.capturedOverlay = capturedOverlay;
        this.pauseOverlay = pauseOverlay;
    }

    public void render(
        OrthographicCamera camera,
        Player player,
        Array<Skill> skills,
        PlaySessionState state
    ) {
        mapManager.mapRenderer.setView(camera);
        mapManager.mapRenderer.render();

        SpriteBatch batch = game.batch;
        ShapeRenderer shapeRenderer = game.shapeRenderer;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (state.invisibilityTimer > 0) {
            batch.setColor(1, 1, 1, 0.5f);
        } else {
            batch.setColor(1, 1, 1, 1f);
        }

        player.render(batch, shapeRenderer);
        batch.setColor(1, 1, 1, 1f);

        for (Enemy enemy : entityManager.enemies) {
            enemy.render(batch, shapeRenderer);
        }

        mapManager.renderSprites(batch);
        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderSprite(batch);
        }

        entityManager.renderSprites(batch, shapeRenderer, skills);

        if (state.activeVending != null && !state.isVendingOpen && !state.isInventoryOpen && !state.isPaused) {
            drawVendingPrompt(batch, state);
        }
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        mapManager.renderShapes(shapeRenderer);

        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderHpBar(shapeRenderer);
        }

        entityManager.renderShapes(shapeRenderer, batch);
        shapeRenderer.end();

        lightRenderer.renderDarkness(batch, player, camera, state.carrotTimer > 0);

        shapeRenderer.setProjectionMatrix(camera.combined);
        lightRenderer.renderEnemyVision(shapeRenderer, entityManager, mapManager);

        Matrix4 hudMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameHUD.render(skills, hudMatrix, state.hasKey);

        if (state.hasKey) {
            batch.setProjectionMatrix(hudMatrix);
            batch.begin();
            batch.draw(mapManager.keyTexture, Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight() - 52, 32, 32);
            batch.end();
        }

        if (state.isInventoryOpen) inventoryOverlay.render(batch, shapeRenderer, hudMatrix);
        if (state.isVendingOpen) vendingMachineOverlay.render(batch, shapeRenderer, hudMatrix);
        if (state.isCaptured) capturedOverlay.render(shapeRenderer, hudMatrix);
        if (state.isPaused) pauseOverlay.render(shapeRenderer, hudMatrix);
    }

    private void drawVendingPrompt(SpriteBatch batch, PlaySessionState state) {
        if (mapManager.btnUsePromptTex != null && state.activeVending != null) {
            batch.draw(
                mapManager.btnUsePromptTex,
                state.activeVending.bounds.x + state.activeVending.bounds.width / 2 - 16,
                state.activeVending.bounds.y + state.activeVending.bounds.height + 5,
                32, 32
            );
        }
    }
}
