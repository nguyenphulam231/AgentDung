package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Door;
import com.agentdung.game.entities.Item;
import com.agentdung.game.entities.Player;
import com.agentdung.game.entities.VendingMachine;
import com.agentdung.game.handlers.InputHandler;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.managers.MapManager;
import com.agentdung.game.renderers.LightRenderer;
import com.agentdung.game.skills.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashMap;
import java.util.Map;

public class PlayScreen extends ScreenAdapter {
    public AgentDungGame game;
    OrthographicCamera camera;

    public MapManager mapManager;
    public EntityManager entityManager;
    LightRenderer lightRenderer;

    private GameHUD gameHUD;
    private CapturedOverlay capturedOverlay;
    private PauseOverlay pauseOverlay;

    private InventoryOverlay inventoryOverlay;
    private VendingMachineOverlay vendingMachineOverlay;

    public Player dung;
    public Array<Skill> skills;

    public boolean hasKey = false;
    int currentLevel;
    int currentWorld;
    private boolean isCaptured = false;
    private boolean isPaused = false;

    public final Map<Item.ItemType, Integer> inventory = new HashMap<>();

    public boolean isInventoryOpen = false;
    public boolean isVendingOpen = false;

    private VendingMachine activeVending = null;

    public float invisibilityTimer = 0f;
    public float shoesTimer = 0f;
    public float clockTimer = 0f;
    public float lemonTimer = 0f;
    public float orangeTimer = 0f;
    public float carrotTimer = 0f;

    public int amuletCount = 0;

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

        initLevel(currentLevel);
    }

    public void initLevel(int level) {
        InputHandler.stopLoopingSounds(this.game);
        isCaptured = false;
        isPaused = false;
        isInventoryOpen = false;
        isVendingOpen = false;
        activeVending = null;

        invisibilityTimer = 0f;
        shoesTimer = 0f;
        clockTimer = 0f;
        lemonTimer = 0f;
        orangeTimer = 0f;
        carrotTimer = 0f;

        amuletCount = 0;
        hasKey = false;

        inventory.clear();
        for (Item.ItemType type : Item.ItemType.values()) {
            inventory.put(type, 0);
        }

        if (skills != null) {
            for (Skill s : skills) {
                if (s instanceof VomitSkill) ((VomitSkill) s).dispose();
                if (s instanceof SpitSkill) ((SpitSkill) s).dispose();
                if (s instanceof PoopSkill) ((PoopSkill) s).dispose();
            }
        }

        mapManager.loadLevel(currentWorld, level);

        // 1. Khởi tạo đối tượng người chơi trước
        dung = new Player(mapManager.playerSpawn.x, mapManager.playerSpawn.y, this.game);
        dung.setSize(26);

        // 2. ĐÃ SỬA: Truyền thực thể 'dung' vào hệ thống quản lý quái để AI cấu trúc định vị được mục tiêu
        entityManager.initEnemies(mapManager, dung);
        entityManager.clearAll();

        gameHUD.loadTextures();

        this.skills = new Array<>();
        skills.add(new SpitSkill());
        skills.add(new PoopSkill());
        skills.add(new PeeSkill());
        skills.add(new VomitSkill());
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0, 0, 0, 1);

        // --- GIAI ĐOẠN 1: RENDER BẢN ĐỒ VÀ NỀN GAME ---
        mapManager.mapRenderer.setView(camera);
        mapManager.mapRenderer.render();

        // --- GIAI ĐOẠN 2: MỞ BATCH HÌNH ẢNH (SPRITE/TEXTURE) ---
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (invisibilityTimer > 0) {
            game.batch.setColor(1, 1, 1, 0.5f);
        } else {
            game.batch.setColor(1, 1, 1, 1f);
        }

        // Vẽ Player
        dung.render(game.batch, game.shapeRenderer);
        game.batch.setColor(1, 1, 1, 1f); // Reset màu batch

        // Vẽ danh sách Enemy qua đa hình
        for (com.agentdung.game.entities.Enemy e : entityManager.enemies) {
            e.render(game.batch, game.shapeRenderer);
        }

        // Render các Sprite của Map
        mapManager.renderSprites(game.batch);
        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderSprite(game.batch);
        }

        // Vẽ Sprite của đạn/bẫy sinh học
        entityManager.renderSprites(game.batch, game.shapeRenderer, skills);

        if (activeVending != null && !isVendingOpen && !isInventoryOpen && !isPaused) {
            batchDrawPrompt();
        }
        game.batch.end();

        // --- GIAI ĐOẠN 3: MỞ HÌNH KHỐI (SHAPERENDERER ĐỂ VẼ THANH MÁU, TIA NƯỚC, DEBUG) ---
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        mapManager.renderShapes(game.shapeRenderer);

        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderHpBar(game.shapeRenderer);
        }

        // Vẽ các loại đạn khối hình học (Shape)
        entityManager.renderShapes(game.shapeRenderer, game.batch);

        game.shapeRenderer.end();

        // --- GIAI ĐOẠN 4: HIỆU ỨNG ÁNH SÁNG & TẦM NHÌN LÍNH ---
        lightRenderer.renderDarkness(game.batch, dung, camera, carrotTimer > 0);

        game.shapeRenderer.setProjectionMatrix(camera.combined);
        lightRenderer.renderEnemyVision(game.shapeRenderer, entityManager, mapManager);

        // --- GIAI ĐOẠN 5: RENDER GIAO DIỆN (HUD & OVERLAY) ---
        Matrix4 hudMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameHUD.render(skills, hudMatrix, hasKey);

        if (hasKey) {
            game.batch.setProjectionMatrix(hudMatrix);
            game.batch.begin();
            game.batch.draw(mapManager.keyTexture, Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight() - 52, 32, 32);
            game.batch.end();
        }

        if (isInventoryOpen)         inventoryOverlay.render(game.batch, game.shapeRenderer, hudMatrix);
        if (isVendingOpen)           vendingMachineOverlay.render(game.batch, game.shapeRenderer, hudMatrix);
        if (isCaptured)              capturedOverlay.render(game.shapeRenderer, hudMatrix);
        if (isPaused)                pauseOverlay.render(game.shapeRenderer, hudMatrix);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !isCaptured) {
            if (isInventoryOpen) {
                isInventoryOpen = false;
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            } else if (isVendingOpen) {
                isVendingOpen = false;
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            } else if (!isPaused) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                setPaused(true);
            } else if (isPaused) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                setPaused(false);
            }
        }
    }

    private void update(float delta) {
        if (isCaptured) {
            capturedOverlay.handleInput();
            return;
        }

        if (isPaused) {
            pauseOverlay.handleInput();
            return;
        }

        if (invisibilityTimer > 0) invisibilityTimer -= delta;
        if (shoesTimer > 0) shoesTimer -= delta;
        if (clockTimer > 0) clockTimer -= delta;
        if (lemonTimer > 0) lemonTimer -= delta;
        if (orangeTimer > 0) orangeTimer -= delta;
        if (carrotTimer > 0) carrotTimer -= delta;

        if (isInventoryOpen) {
            inventoryOverlay.handleInput();
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                isInventoryOpen = false;
            }
        }

        if (isVendingOpen) {
            vendingMachineOverlay.handleInput();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I) && !isInventoryOpen) {
            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            isInventoryOpen = true;
        }

        if (Gdx.input.justTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPoint.y = Gdx.graphics.getHeight() - touchPoint.y;

            if (!isInventoryOpen && gameHUD.getRectPauseBtn().contains(touchPoint.x, touchPoint.y)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                setPaused(true);
                return;
            }

            if (gameHUD.getRectBagBtn().contains(touchPoint.x, touchPoint.y)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                if (isInventoryOpen) {
                    isInventoryOpen = false;
                } else if (!isVendingOpen) {
                    isInventoryOpen = true;
                }
                return;
            }
        }

        if (isInventoryOpen) return;

        activeVending = null;
        for (VendingMachine vm : mapManager.vendingMachines) {
            float dist = Vector2.dst(dung.getPosition().x, dung.getPosition().y, vm.bounds.x + vm.bounds.width/2, vm.bounds.y + vm.bounds.height/2);
            if (dist < 50f) {
                activeVending = vm;
                break;
            }
        }

        if (activeVending != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            isVendingOpen = true;
            isInventoryOpen = false;
            return;
        }

        float originalSpeed = 150f;
        if (shoesTimer > 0) {
            dung.setSpeed(originalSpeed * 1.3f);
        } else {
            dung.setSpeed(originalSpeed);
        }

        // 3. ĐÃ SỬA: Cập nhật trạng thái nội bộ của nhân vật chuẩn hóa không tham số thừa
        dung.update(delta);

        // 4. ĐÃ SỬA: Gọi cơ chế điều khiển hướng đi của nhân vật từ phím bấm
        InputHandler.handleTankMovement(delta, dung, camera, mapManager);

        // 5. ĐÃ SỬA: Ép EntityManager xử lý dịch chuyển tịnh tiến + quét va chạm tường tập trung cho Player
        entityManager.moveEntityWithWallCollision(dung, delta, mapManager);

        Rectangle dungRect = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        for (int i = mapManager.keys.size - 1; i >= 0; i--) {
            if (dungRect.overlaps(mapManager.keys.get(i))) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                hasKey = true;
                inventory.put(Item.ItemType.KEY, 1);
                mapManager.keys.removeIndex(i);
            }
        }

        for (int i = mapManager.items.size - 1; i >= 0; i--) {
            Item item = mapManager.items.get(i);
            Rectangle itemRect = new Rectangle(item.getPosition().x, item.getPosition().y, item.getSize(), item.getSize());

            if (dungRect.overlaps(itemRect)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                if (item.type == Item.ItemType.COIN) {
                    game.globalCoinCount++;
                } else {
                    inventory.put(item.type, inventory.getOrDefault(item.type, 0) + 1);
                    if (item.type == Item.ItemType.AMULET) amuletCount++;
                }
                mapManager.items.removeIndex(i);
            }
        }

        for (Door door : mapManager.doors) {
            float dist = Vector2.dst(dung.getPosition().x, dung.getPosition().y, door.bounds.x, door.bounds.y);
            door.update(delta, dist < 60f, hasKey);
        }

        InputHandler.handleSkillInput(dung, skills, entityManager, this.game);

        float deltaLogic = (clockTimer > 0) ? 0f : delta;

        // Cập nhật quái vật & xử lý bắt giữ
        entityManager.update(deltaLogic, dung, mapManager, () -> {
            if (invisibilityTimer <= 0) {
                if (!isCaptured) {
                    if (amuletCount > 0) {
                        amuletCount--;
                        inventory.put(Item.ItemType.AMULET, amuletCount);
                        invisibilityTimer = 1.5f;
                        return;
                    }
                    isCaptured = true;
                    InputHandler.stopLoopingSounds(this.game);
                    capturedOverlay.playSound();
                }
            }
        });

        camera.position.x = MathUtils.clamp(dung.getPosition().x + dung.getSize() / 2, camera.viewportWidth / 2, mapManager.mapWidth - camera.viewportWidth / 2);
        camera.position.y = MathUtils.clamp(dung.getPosition().y + dung.getSize() / 2, camera.viewportHeight / 2, mapManager.mapHeight - camera.viewportHeight / 2);
        camera.update();

        if (mapManager.targetServer != null && mapManager.targetServer.hp <= 0) {
            if (currentLevel > game.completedLevelsReal[currentWorld - 1]) {
                game.completedLevelsReal[currentWorld - 1] = currentLevel;
            }
            InputHandler.stopLoopingSounds(this.game);
            game.saveProgress();
            game.setScreen(new LevelDoneScreen(game, currentWorld, currentLevel));
        }

        for (Skill s : skills) s.update(delta);
    }

    private void batchDrawPrompt() {
        if (mapManager.btnUsePromptTex != null && activeVending != null) {
            game.batch.draw(mapManager.btnUsePromptTex, activeVending.bounds.x + activeVending.bounds.width/2 - 16, activeVending.bounds.y + activeVending.bounds.height + 5, 32, 32);
        }
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (this.isPaused) {
            InputHandler.stopLoopingSounds(this.game);
        }
    }

    @Override
    public void dispose() {
        InputHandler.stopLoopingSounds(this.game);
        if (gameHUD != null) gameHUD.dispose();
        if (capturedOverlay != null) capturedOverlay.dispose();
        if (pauseOverlay != null) pauseOverlay.dispose();
        if (inventoryOverlay != null) inventoryOverlay.dispose();
        if (vendingMachineOverlay != null) vendingMachineOverlay.dispose();

        mapManager.dispose();
        entityManager.dispose();
        lightRenderer.dispose();

        if (skills != null) {
            for (Skill s : skills) {
                if (s instanceof VomitSkill) ((VomitSkill) s).dispose();
                if (s instanceof SpitSkill) ((SpitSkill) s).dispose();
                if (s instanceof PoopSkill) ((PoopSkill) s).dispose();
            }
        }
    }
}
