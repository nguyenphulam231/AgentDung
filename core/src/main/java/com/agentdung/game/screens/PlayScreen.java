package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Player;
import com.agentdung.game.handlers.InputHandler;
import com.agentdung.game.handlers.SkillSoundPlayer;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.managers.MapManager;
import com.agentdung.game.renderers.LightRenderer;
import com.agentdung.game.screens.play.GameplayUpdater;
import com.agentdung.game.screens.play.LevelInitializer;
import com.agentdung.game.screens.play.PlaySessionState;
import com.agentdung.game.screens.play.PlayUiHandler;
import com.agentdung.game.screens.play.PlayWorldRenderer;
import com.agentdung.game.screens.play.SkillResourceHelper;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class PlayScreen extends ScreenAdapter {
    public final AgentDungGame game;
    public final PlaySessionState state = new PlaySessionState();

    public MapManager mapManager;
    public EntityManager entityManager;

    public Player dung;
    public Array<Skill> skills;

    int currentLevel;
    int currentWorld;

    private OrthographicCamera camera;
    private LightRenderer lightRenderer;

    private GameHUD gameHUD;
    private CapturedOverlay capturedOverlay;
    private PauseOverlay pauseOverlay;
    private InventoryOverlay inventoryOverlay;
    private VendingMachineOverlay vendingMachineOverlay;

    private final SkillSoundPlayer skillSoundPlayer;
    private final InputHandler inputHandler;

    private LevelInitializer levelInitializer;
    private PlayWorldRenderer worldRenderer;
    private GameplayUpdater gameplayUpdater;
    private PlayUiHandler uiHandler;

    public PlayScreen(AgentDungGame game, int world, int level) {
        this.game = game;
        this.currentWorld = world;
        this.currentLevel = level;

        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 400 * (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth());

        this.mapManager = new MapManager();
        this.entityManager = new EntityManager();
        this.lightRenderer = new LightRenderer();

        this.gameHUD = new GameHUD(game);
        this.capturedOverlay = new CapturedOverlay(this);
        this.pauseOverlay = new PauseOverlay(this);
        this.inventoryOverlay = new InventoryOverlay(this);
        this.vendingMachineOverlay = new VendingMachineOverlay(this);

        this.skillSoundPlayer = new SkillSoundPlayer();
        this.inputHandler = new InputHandler(game, skillSoundPlayer);
        game.setSkillSoundStopper(() -> skillSoundPlayer.stopAll(game));

        this.levelInitializer = new LevelInitializer(game, mapManager, entityManager, gameHUD, inputHandler);
        this.worldRenderer = new PlayWorldRenderer(
            game, mapManager, entityManager, lightRenderer, gameHUD,
            inventoryOverlay, vendingMachineOverlay, capturedOverlay, pauseOverlay
        );
        this.gameplayUpdater = new GameplayUpdater(
            game, mapManager, entityManager, capturedOverlay, inputHandler
        );
        this.uiHandler = new PlayUiHandler(game, gameHUD, inventoryOverlay, vendingMachineOverlay, inputHandler);

        initLevel(currentLevel);
    }

    public void initLevel(int level) {
        LevelInitializer.LevelSetupResult result = levelInitializer.setup(currentWorld, level, state, skills);
        this.dung = result.player;
        this.skills = result.skills;
        this.currentLevel = level;
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0, 0, 0, 1);
        worldRenderer.render(camera, dung, skills, state);
        uiHandler.handleEscapeKey(state, null);
    }

    private void update(float delta) {
        if (state.isCaptured) {
            capturedOverlay.handleInput();
            return;
        }

        if (state.isPaused) {
            pauseOverlay.handleInput();
            return;
        }

        state.tickBuffs(delta);

        if (uiHandler.update(delta, state, null)) {
            return;
        }

        gameplayUpdater.update(delta, dung, skills, state, camera, currentWorld, currentLevel);
    }

    public void setPaused(boolean paused) {
        uiHandler.setPaused(state, paused, null);
    }

    @Override
    public void dispose() {
        game.setSkillSoundStopper(null);
        inputHandler.stopLoopingSounds();
        if (gameHUD != null) gameHUD.dispose();

        if (inventoryOverlay != null) inventoryOverlay.dispose();
        if (vendingMachineOverlay != null) vendingMachineOverlay.dispose();

        mapManager.dispose();
        entityManager.dispose();
        lightRenderer.dispose();
        SkillResourceHelper.disposeAll(skills);
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }
}
