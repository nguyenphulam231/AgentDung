package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Door;
import com.agentdung.game.entities.Item;
import com.agentdung.game.entities.Player;
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

public class PlayScreen extends ScreenAdapter {
    AgentDungGame game;
    OrthographicCamera camera;

    MapManager mapManager;
    EntityManager entityManager;
    LightRenderer lightRenderer;

    // Các thành phần UI bóc tách
    private GameHUD gameHUD;
    private CapturedOverlay capturedOverlay;
    private PauseOverlay pauseOverlay;

    Player dung;
    Array<Skill> skills;

    private boolean hasKey = false;
    int currentLevel;
    int currentWorld;
    private boolean isCaptured = false;
    private boolean isPaused = false;

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

        initLevel(currentLevel);
    }

    public void initLevel(int level) {
        InputHandler.stopLoopingSounds(this.game);
        isCaptured = false;
        isPaused = false;

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
        hasKey = false;

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
        dung.draw(game.batch);
        for (com.agentdung.game.entities.Enemy e : entityManager.enemies) e.draw(game.batch);
        mapManager.renderSprites(game.batch);
        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderSprite(game.batch);
        }
        entityManager.renderSprites(game.batch, skills);
        game.batch.end();

        // Render Ánh sáng và Tầm nhìn
        lightRenderer.renderDarkness(game.batch, dung, camera);
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        lightRenderer.renderEnemyVision(game.shapeRenderer, entityManager, mapManager);

        // --- RENDER HUD VÀ OVERLAY KHÔNG PHỤ THUỘC CAMERA ---
        Matrix4 hudMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Vẽ HUD mana và nút Pause góc màn hình qua lớp GameHUD (Đầy đủ 3 tham số)
        gameHUD.render(skills, hudMatrix, hasKey);

        // Vẽ chìa khóa nếu có (Dịch sang trái một chút x: -110 để tránh đè nút Pause)
        if (hasKey) {
            game.batch.setProjectionMatrix(hudMatrix);
            game.batch.begin();
            game.batch.draw(mapManager.keyTexture, Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight() - 52, 32, 32);
            game.batch.end();
        }

        // Nếu bị lính bắt, vẽ giao diện thua cuộc
        if (isCaptured) {
            capturedOverlay.render(game.shapeRenderer, hudMatrix);
        }

        // --- ĐÈ GIAO DIỆN PAUSE LÊN TRÊN CÙNG KHI BẤM ESC HOẶC CLICK PAUSE ---
        if (isPaused) {
            pauseOverlay.render(game.shapeRenderer, hudMatrix);
        }

        // Bật / tắt tạm dừng nhanh bằng phím ESCAPE (Chỉ nhận diện khi chưa bị bắt)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !isCaptured) {
            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            setPaused(!isPaused);
        }
    }

    private void update(float delta) {
        // 1. Nếu đang bị bắt, chuyển quyền xử lý cho CapturedOverlay
        if (isCaptured) {
            capturedOverlay.handleInput();
            return;
        }

        // 2. Nếu đang Pause, chuyển quyền xử lý đầu vào hoàn toàn cho PauseOverlay
        if (isPaused) {
            pauseOverlay.handleInput();
            return;
        }

        // 3. Nhận diện click chuột vào nút Pause trên HUD khi đang chơi bình thường
        if (Gdx.input.justTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPoint.y = Gdx.graphics.getHeight() - touchPoint.y; // Chuyển đổi hệ tọa độ y lộn ngược của LibGDX

            if (gameHUD.getRectPauseBtn().contains(touchPoint.x, touchPoint.y)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                setPaused(true);
                return; // Ngắt update ngay lập tức nhằm tránh dính lệnh click sang cơ chế khác
            }
        }

        // --- TOÀN BỘ LOGIC GAMEPLAY CỐT LÕI SẼ DỪNG LẠI KHI PAUSE HOẶC BỊ BẮT ---
        InputHandler.handleTankMovement(delta, dung, camera, mapManager);
        dung.update(delta, dung, mapManager.wallRects);

        Rectangle dungRect = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        // Ăn chìa khóa
        for (int i = mapManager.keys.size - 1; i >= 0; i--) {
            if (dungRect.overlaps(mapManager.keys.get(i))) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                hasKey = true;
                mapManager.keys.removeIndex(i);
            }
        }

        // Ăn vật phẩm hồi phục
        for (int i = mapManager.items.size - 1; i >= 0; i--) {
            Item item = mapManager.items.get(i);
            Rectangle itemRect = new Rectangle(item.getPosition().x, item.getPosition().y, item.getSize(), item.getSize());

            if (dungRect.overlaps(itemRect)) {
                if (item.type == Item.ItemType.ROTTEN_MEAT) {
                    if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                    for (Skill s : skills) {
                        if (s instanceof PoopSkill || s instanceof VomitSkill) s.gainMana(40f);
                    }
                } else if (item.type == Item.ItemType.WATER) {
                    if (game.isMasterOn && game.isSfxOn && game.pickWaterSound != null) game.pickWaterSound.play();
                    for (Skill s : skills) {
                        if (s instanceof PeeSkill) s.gainMana(50f);
                    }
                }
                mapManager.items.removeIndex(i);
            }
        }

        // Cập nhật Cửa
        for (Door door : mapManager.doors) {
            float dist = Vector2.dst(dung.getPosition().x, dung.getPosition().y, door.bounds.x, door.bounds.y);
            door.update(delta, dist < 60f, hasKey);
        }

        InputHandler.handleSkillInput(dung, skills, entityManager, this.game);

        // Kích hoạt trạng thái bị bắt
        entityManager.update(delta, dung, mapManager, () -> {
            if (!isCaptured) {
                isCaptured = true;
                InputHandler.stopLoopingSounds(this.game);
                capturedOverlay.playSound();
            }
        });

        // Di chuyển Camera theo Agent Dũng mượt mà
        camera.position.x = MathUtils.clamp(dung.getPosition().x + dung.getSize() / 2, camera.viewportWidth / 2, mapManager.mapWidth - camera.viewportWidth / 2);
        camera.position.y = MathUtils.clamp(dung.getPosition().y + dung.getSize() / 2, camera.viewportHeight / 2, mapManager.mapHeight - camera.viewportHeight / 2);
        camera.update();

        // Kiểm tra điều kiện thắng (Sập nguồn server máy chủ)
        if (mapManager.targetServer != null && mapManager.targetServer.hp <= 0) {
            if (currentLevel > game.completedLevelsReal[currentWorld - 1]) {
                game.completedLevelsReal[currentWorld - 1] = currentLevel;
            }
            InputHandler.stopLoopingSounds(this.game);
            game.setScreen(new LevelDoneScreen(game, currentWorld, currentLevel));
        }

        for (Skill s : skills) s.update(delta);
    }

    /**
     * Hàm hỗ trợ thay đổi trạng thái pause và xử lý ngắt âm thanh vòng lặp kỹ năng kéo dài
     */
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
