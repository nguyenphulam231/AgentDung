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

    // --- HAI OVERLAY UI MỚI ---
    private InventoryOverlay inventoryOverlay;
    private VendingMachineOverlay vendingMachineOverlay;

    public Player dung;
    public Array<Skill> skills;

    public boolean hasKey = false;
    int currentLevel;
    int currentWorld;
    private boolean isCaptured = false;
    private boolean isPaused = false;

    // --- DỮ LIỆU KHO ĐỒ & VÍ TIỀN ---
    public final Map<Item.ItemType, Integer> inventory = new HashMap<>();
    public int coinCount = 0;

    // Trạng thái bật/tắt UI phủ
    public boolean isInventoryOpen = false;
    public boolean isVendingOpen = false;

    // Máy bán hàng tự động đang đứng gần
    private VendingMachine activeVending = null;

    // --- BỘ ĐẾM THỜI GIAN HIỆU ỨNG VẬT PHẨM ---
    public float invisibilityTimer = 0f;
    public float shoesTimer = 0f;
    public float clockTimer = 0f;
    public float lemonTimer = 0f;
    public float orangeTimer = 0f;

    // Thuộc tính bổ trợ amulet (bảo hiểm bị lính bắt)
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

        // Khởi tạo thực thể Overlay UI mới
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

        // Reset bộ đếm hiệu ứng vật phẩm
        invisibilityTimer = 0f;
        shoesTimer = 0f;
        clockTimer = 0f;
        lemonTimer = 0f;
        orangeTimer = 0f;
        amuletCount = 0;
        coinCount = 0;
        hasKey = false;

        // Khởi tạo sạch balo đồ
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

        dung = new Player(mapManager.playerSpawn.x, mapManager.playerSpawn.y, this.game);
        dung.setSize(26);

        entityManager.initEnemies(mapManager);
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

        // Render Bản đồ
        mapManager.mapRenderer.setView(camera);
        mapManager.mapRenderer.render();

        // Render Khối hình học
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mapManager.renderShapes(game.shapeRenderer);
        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderHpBar(game.shapeRenderer);
        }
        entityManager.renderShapes(game.shapeRenderer);
        game.shapeRenderer.end();

        // Render Sprites
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // --- TÀNG HÌNH: Nhân vật mờ đi 50% ---
        if (invisibilityTimer > 0) {
            game.batch.setColor(1, 1, 1, 0.5f);
        } else {
            game.batch.setColor(1, 1, 1, 1f);
        }
        dung.draw(game.batch);
        game.batch.setColor(1, 1, 1, 1f); // Trả lại màu gốc cho sprite khác

        for (com.agentdung.game.entities.Enemy e : entityManager.enemies) e.draw(game.batch);
        mapManager.renderSprites(game.batch);
        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderSprite(game.batch);
        }
        entityManager.renderSprites(game.batch, skills);

        // Vẽ nút gợi ý tương tác [Use] nhỏ khi đứng gần Vending Machine ngoài map thế giới
        if (activeVending != null && !isVendingOpen && !isInventoryOpen && !isPaused) {
            batchDrawPrompt();
        }
        game.batch.end();

        // Render Ánh sáng và Tầm nhìn (Nếu tàng hình, không vẽ tầm nhìn quân địch rọi vào mình)
        lightRenderer.renderDarkness(game.batch, dung, camera);
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        if (invisibilityTimer <= 0) {
            lightRenderer.renderEnemyVision(game.shapeRenderer, entityManager, mapManager);
        }

        // --- RENDER HUD VÀ OVERLAY KHÔNG PHỤ THUỘC CAMERA ---
        Matrix4 hudMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Vẽ HUD mana, nút Pause và nút Balo góc màn hình qua lớp GameHUD
        gameHUD.render(skills, hudMatrix, hasKey);

        // Vẽ chìa khóa nếu có
        if (hasKey) {
            game.batch.setProjectionMatrix(hudMatrix);
            game.batch.begin();
            game.batch.draw(mapManager.keyTexture, Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight() - 52, 32, 32);
            game.batch.end();
        }

        // Vẽ giao diện Inventory đè lên nếu mở (Logic game lính vẫn chạy dưới nền)
        if (isInventoryOpen) {
            inventoryOverlay.render(game.batch, game.shapeRenderer, hudMatrix);
        }

        // Vẽ giao diện Vending Machine đè lên
        if (isVendingOpen) {
            vendingMachineOverlay.render(game.batch, game.shapeRenderer, hudMatrix);
        }

        // Nếu bị lính bắt, vẽ giao diện thua cuộc
        if (isCaptured) {
            capturedOverlay.render(game.shapeRenderer, hudMatrix);
        }

        // Nếu đang Pause, vẽ giao diện Pause đè lên trên cùng
        if (isPaused) {
            pauseOverlay.render(game.shapeRenderer, hudMatrix);
        }

        // Bật / tắt tạm dừng nhanh bằng phím ESCAPE
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !isCaptured && !isInventoryOpen && !isVendingOpen) {
            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            setPaused(!isPaused);
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

        // --- CẬP NHẬT BỘ ĐẾM THỜI GIAN HIỆU ỨNG VẬT PHẨM ---
        if (invisibilityTimer > 0) invisibilityTimer -= delta;
        if (shoesTimer > 0) shoesTimer -= delta;
        if (clockTimer > 0) clockTimer -= delta;
        if (lemonTimer > 0) lemonTimer -= delta;
        if (orangeTimer > 0) orangeTimer -= delta;

        // Quản lý đóng mở và bắt sự kiện đầu vào của Inventory / Vending UI độc lập
        if (isInventoryOpen) {
            inventoryOverlay.handleInput();

            // Cho phép nhấn I một lần nữa hoặc click chuột ngoài nút Bag để đóng nhanh balo đồ
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                isInventoryOpen = false;
            }
        }

        if (isVendingOpen) {
            vendingMachineOverlay.handleInput();
            return; // Khác với inventory, mở máy bán hàng sẽ đóng băng di chuyển nhân vật
        }

        // Bắt phím mở hòm đồ khẩn cấp bằng phím I
        if (Gdx.input.isKeyJustPressed(Input.Keys.I) && !isInventoryOpen) {
            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            isInventoryOpen = true;
        }

        // --- NHẬN DIỆN CLICK CHUỘT VÀO NÚT PAUSE HOẶC NÚT BAG TRÊN HUD ---
        if (Gdx.input.justTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPoint.y = Gdx.graphics.getHeight() - touchPoint.y; // Đảo ngược Y chuẩn LibGDX

            // 1. Kiểm tra click nút Pause (Chỉ nhận diện khi hòm đồ cá nhân đang đóng)
            if (!isInventoryOpen && gameHUD.getRectPauseBtn().contains(touchPoint.x, touchPoint.y)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                setPaused(true);
                return;
            }

            // 2. Kiểm tra click nút Bag (Hoạt động như công tắc bật/tắt hòm đồ)
            if (gameHUD.getRectBagBtn().contains(touchPoint.x, touchPoint.y)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();

                if (isInventoryOpen) {
                    isInventoryOpen = false; // Nếu đang mở thì đóng lại
                } else if (!isVendingOpen) {
                    isInventoryOpen = true;  // Nếu đang đóng (và không mở máy bán hàng) thì bật lên
                }
                return;
            }
        }

        // Nếu hòm đồ đang mở, chặn toàn bộ logic gameplay cốt lõi (di chuyển, kỹ năng) bên dưới
        if (isInventoryOpen) {
            return;
        }

        // Kiểm tra khoảng cách đứng gần Vending Machine để bật/tắt prompt nhắc nhở
        activeVending = null;
        for (VendingMachine vm : mapManager.vendingMachines) {
            float dist = Vector2.dst(dung.getPosition().x, dung.getPosition().y, vm.bounds.x + vm.bounds.width/2, vm.bounds.y + vm.bounds.height/2);
            if (dist < 50f) {
                activeVending = vm;
                break;
            }
        }

        // Nếu đứng gần máy bán hàng và nhấn phím E, mở giao diện Vending Machine công cộng
        if (activeVending != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            isVendingOpen = true;
            isInventoryOpen = false; // Tắt luôn inventory tránh chồng chéo UI
            return;
        }

        // Áp dụng chỉ số tốc độ dựa trên hiệu ứng của Shoes vật phẩm cứu trợ
        float originalSpeed = 150f;
        if (shoesTimer > 0) {
            dung.setSpeed(originalSpeed * 1.3f);
        } else {
            dung.setSpeed(originalSpeed);
        }

        // Cập nhật trạng thái Player
        dung.update(delta, dung, mapManager.wallRects);

        // Di chuyển nhân vật
        InputHandler.handleTankMovement(delta, dung, camera, mapManager);

        Rectangle dungRect = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        // Kiểm tra va chạm nhặt Key bí mật ngoài Map thế giới
        for (int i = mapManager.keys.size - 1; i >= 0; i--) {
            if (dungRect.overlaps(mapManager.keys.get(i))) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                hasKey = true;
                inventory.put(Item.ItemType.KEY, 1);
                mapManager.keys.removeIndex(i);
            }
        }

        // --- HỆ THỐNG VA CHẠM NHẶT VẬT PHẨM ĐƯA VÀO BALO/VÍ TIỀN ---
        for (int i = mapManager.items.size - 1; i >= 0; i--) {
            Item item = mapManager.items.get(i);
            Rectangle itemRect = new Rectangle(item.getPosition().x, item.getPosition().y, item.getSize(), item.getSize());

            if (dungRect.overlaps(itemRect)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();

                if (item.type == Item.ItemType.COIN) {
                    coinCount++;
                } else {
                    inventory.put(item.type, inventory.getOrDefault(item.type, 0) + 1);
                    if (item.type == Item.ItemType.AMULET) amuletCount++;
                }
                mapManager.items.removeIndex(i);
            }
        }

        // Cập nhật Cửa phòng máy chủ
        for (Door door : mapManager.doors) {
            float dist = Vector2.dst(dung.getPosition().x, dung.getPosition().y, door.bounds.x, door.bounds.y);
            door.update(delta, dist < 60f, hasKey);
        }

        InputHandler.handleSkillInput(dung, skills, entityManager, this.game);

        // --- KIỂM TRA ĐIỀU KIỆN LÍNH TUẦN TRA BẮT GIỮ (ÁP DỤNG TRẠNG THÁI CLOCK VÀ INVISIBILITY) ---
        if (invisibilityTimer <= 0) { // Nếu tàng hình, lính mù tạm thời hoàn toàn
            // Nếu Clock hoạt động, lính đóng băng AI không cập nhật tìm đường, đứng bất động
            float deltaLogic = (clockTimer > 0) ? 0f : delta;

            entityManager.update(deltaLogic, dung, mapManager, () -> {
                if (!isCaptured) {
                    // Cơ chế Amulet cứu hộ mạng sống một lần
                    if (amuletCount > 0) {
                        amuletCount--;
                        inventory.put(Item.ItemType.AMULET, amuletCount);
                        // Đẩy lính ra xa hoặc cho tàng hình chớp nhoáng để thoát thân
                        invisibilityTimer = 1.5f;
                        return;
                    }
                    isCaptured = true;
                    InputHandler.stopLoopingSounds(this.game);
                    capturedOverlay.playSound();
                }
            });
        }

        // Di chuyển Camera
        camera.position.x = MathUtils.clamp(dung.getPosition().x + dung.getSize() / 2, camera.viewportWidth / 2, mapManager.mapWidth - camera.viewportWidth / 2);
        camera.position.y = MathUtils.clamp(dung.getPosition().y + dung.getSize() / 2, camera.viewportHeight / 2, mapManager.mapHeight - camera.viewportHeight / 2);
        camera.update();

        // Kiểm tra điều kiện thắng cuộc
        if (mapManager.targetServer != null && mapManager.targetServer.hp <= 0) {
            if (currentLevel > game.completedLevelsReal[currentWorld - 1]) {
                game.completedLevelsReal[currentWorld - 1] = currentLevel;
            }
            InputHandler.stopLoopingSounds(this.game);
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
