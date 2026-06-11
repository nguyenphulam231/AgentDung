package com.agentdung.game.screens.play;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Door;
import com.agentdung.game.entities.Item;
import com.agentdung.game.entities.Player;
import com.agentdung.game.entities.VendingMachine;
import com.agentdung.game.handlers.InputHandler;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.managers.MapManager;
import com.agentdung.game.screens.CapturedOverlay;
import com.agentdung.game.screens.LevelDoneScreen;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameplayUpdater {
    private static final float BASE_PLAYER_SPEED = 150f;
    private static final float VENDING_INTERACT_DISTANCE = 50f;
    private static final float DOOR_INTERACT_DISTANCE = 60f;

    private final AgentDungGame game;
    private final MapManager mapManager;
    private final EntityManager entityManager;
    private final CapturedOverlay capturedOverlay;

    public GameplayUpdater(
        AgentDungGame game,
        MapManager mapManager,
        EntityManager entityManager,
        CapturedOverlay capturedOverlay
    ) {
        this.game = game;
        this.mapManager = mapManager;
        this.entityManager = entityManager;
        this.capturedOverlay = capturedOverlay;
    }

    public void update(
        float delta,
        Player player,
        Array<Skill> skills,
        PlaySessionState state,
        OrthographicCamera camera,
        int currentWorld,
        int currentLevel
    ) {
        applyMovementBuff(player, state);
        player.update(delta);

        InputHandler.handleTankMovement(delta, player, camera, mapManager);
        entityManager.moveEntityWithWallCollision(player, delta, mapManager);

        Rectangle playerRect = new Rectangle(
            player.getPosition().x,
            player.getPosition().y,
            player.getSize(),
            player.getSize()
        );

        collectKeys(playerRect, state);
        collectItems(playerRect, state);
        updateDoors(delta, player, state);
        updateNearbyVending(player, state);

        InputHandler.handleSkillInput(player, skills, entityManager, game);

        float deltaLogic = state.getLogicDelta(delta);
        entityManager.update(deltaLogic, player, mapManager, () -> {
            if (!state.isCaptured && state.handleDetection()) {
                InputHandler.stopLoopingSounds(game);
                capturedOverlay.playSound();
            }
        });

        updateCamera(camera, player);
        checkLevelComplete(state, currentWorld, currentLevel);
        for (Skill skill : skills) skill.update(delta);
    }

    private void applyMovementBuff(Player player, PlaySessionState state) {
        if (state.shoesTimer > 0) {
            player.setSpeed(BASE_PLAYER_SPEED * 1.3f);
        } else {
            player.setSpeed(BASE_PLAYER_SPEED);
        }
    }

    private void collectKeys(Rectangle playerRect, PlaySessionState state) {
        for (int i = mapManager.keys.size - 1; i >= 0; i--) {
            if (playerRect.overlaps(mapManager.keys.get(i))) {
                playClickSound();
                state.hasKey = true;
                state.inventory.put(Item.ItemType.KEY, 1);
                mapManager.keys.removeIndex(i);
            }
        }
    }

    private void collectItems(Rectangle playerRect, PlaySessionState state) {
        for (int i = mapManager.items.size - 1; i >= 0; i--) {
            Item item = mapManager.items.get(i);
            Rectangle itemRect = new Rectangle(
                item.getPosition().x,
                item.getPosition().y,
                item.getSize(),
                item.getSize()
            );

            if (playerRect.overlaps(itemRect)) {
                playClickSound();
                if (item.type == Item.ItemType.COIN) {
                    game.globalCoinCount++;
                } else {
                    state.inventory.put(item.type, state.inventory.getOrDefault(item.type, 0) + 1);
                    if (item.type == Item.ItemType.AMULET) state.amuletCount++;
                }
                mapManager.items.removeIndex(i);
            }
        }
    }

    private void updateDoors(float delta, Player player, PlaySessionState state) {
        for (Door door : mapManager.doors) {
            float dist = Vector2.dst(
                player.getPosition().x,
                player.getPosition().y,
                door.bounds.x,
                door.bounds.y
            );
            door.update(delta, dist < DOOR_INTERACT_DISTANCE, state.hasKey);
        }
    }

    private void updateNearbyVending(Player player, PlaySessionState state) {
        state.activeVending = null;
        for (VendingMachine machine : mapManager.vendingMachines) {
            float dist = Vector2.dst(
                player.getPosition().x,
                player.getPosition().y,
                machine.bounds.x + machine.bounds.width / 2,
                machine.bounds.y + machine.bounds.height / 2
            );
            if (dist < VENDING_INTERACT_DISTANCE) {
                state.activeVending = machine;
                break;
            }
        }
    }

    private void updateCamera(OrthographicCamera camera, Player player) {
        camera.position.x = MathUtils.clamp(
            player.getPosition().x + player.getSize() / 2,
            camera.viewportWidth / 2,
            mapManager.mapWidth - camera.viewportWidth / 2
        );
        camera.position.y = MathUtils.clamp(
            player.getPosition().y + player.getSize() / 2,
            camera.viewportHeight / 2,
            mapManager.mapHeight - camera.viewportHeight / 2
        );
        camera.update();
    }

    private void checkLevelComplete(PlaySessionState state, int currentWorld, int currentLevel) {
        if (mapManager.targetServer == null || mapManager.targetServer.hp > 0) return;

        if (currentLevel > game.completedLevelsReal[currentWorld - 1]) {
            game.completedLevelsReal[currentWorld - 1] = currentLevel;
        }
        InputHandler.stopLoopingSounds(game);
        game.saveProgress();
        game.setScreen(new LevelDoneScreen(game, currentWorld, currentLevel));
    }

    private void playClickSound() {
        if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
            game.clickSound.play();
        }
    }
}
