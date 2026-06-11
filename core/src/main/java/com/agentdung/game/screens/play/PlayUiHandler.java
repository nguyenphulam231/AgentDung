package com.agentdung.game.screens.play;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.handlers.InputHandler;
import com.agentdung.game.screens.GameHUD;
import com.agentdung.game.screens.InventoryOverlay;
import com.agentdung.game.screens.VendingMachineOverlay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

public class PlayUiHandler {
    private final AgentDungGame game;
    private final GameHUD gameHUD;
    private final InventoryOverlay inventoryOverlay;
    private final VendingMachineOverlay vendingMachineOverlay;
    private final InputHandler inputHandler;

    public PlayUiHandler(
        AgentDungGame game,
        GameHUD gameHUD,
        InventoryOverlay inventoryOverlay,
        VendingMachineOverlay vendingMachineOverlay,
        InputHandler inputHandler
    ) {
        this.game = game;
        this.gameHUD = gameHUD;
        this.inventoryOverlay = inventoryOverlay;
        this.vendingMachineOverlay = vendingMachineOverlay;
        this.inputHandler = inputHandler;
    }

    public boolean update(float delta, PlaySessionState state, Runnable onPauseChanged) {
        if (state.isInventoryOpen) {
            inventoryOverlay.handleInput();
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                playClickSound();
                state.isInventoryOpen = false;
            }
        }

        if (state.isVendingOpen) {
            vendingMachineOverlay.handleInput();
            return true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I) && !state.isInventoryOpen) {
            playClickSound();
            state.isInventoryOpen = true;
        }

        if (Gdx.input.justTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPoint.y = Gdx.graphics.getHeight() - touchPoint.y;

            if (!state.isInventoryOpen && gameHUD.getRectPauseBtn().contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                setPaused(state, true, onPauseChanged);
                return true;
            }

            if (gameHUD.getRectBagBtn().contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                if (state.isInventoryOpen) {
                    state.isInventoryOpen = false;
                } else if (!state.isVendingOpen) {
                    state.isInventoryOpen = true;
                }
                return true;
            }
        }

        if (state.isInventoryOpen) return true;

        if (state.activeVending != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            playClickSound();
            state.isVendingOpen = true;
            state.isInventoryOpen = false;
            return true;
        }

        return false;
    }

    public void handleEscapeKey(PlaySessionState state, Runnable onPauseChanged) {
        if (!Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || state.isCaptured) return;

        if (state.isInventoryOpen) {
            playClickSound();
            state.isInventoryOpen = false;
        } else if (state.isVendingOpen) {
            playClickSound();
            state.isVendingOpen = false;
        } else if (!state.isPaused) {
            playClickSound();
            setPaused(state, true, onPauseChanged);
        } else {
            playClickSound();
            setPaused(state, false, onPauseChanged);
        }
    }

    public void setPaused(PlaySessionState state, boolean paused, Runnable onPauseChanged) {
        state.isPaused = paused;
        if (paused) {
            inputHandler.stopLoopingSounds();
        }
        if (onPauseChanged != null) {
            onPauseChanged.run();
        }
    }

    private void playClickSound() {
        if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
            game.clickSound.play();
        }
    }
}
