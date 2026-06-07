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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    AgentDungGame game;
    OrthographicCamera camera;

    // Các thành phần Manager và Renderer tách biệt
    MapManager mapManager;
    EntityManager entityManager;
    LightRenderer lightRenderer;

    Player dung;
    Array<Skill> skills;
    private Map<Class<? extends Skill>, Texture> manaTextures;

    private boolean hasKey = false;
    int currentLevel;
    int currentWorld; // Chuyển thành biến động nhận từ màn hình chọn Map

    // --- CÁC BIẾN CHO TRẠNG THÁI BỊ BẮT (CAPTURED) ---
    private boolean isCaptured = false;
    private Sound capturedSound;
    private Texture titleCapturedTex;
    private Texture btnRestartTex;
    private Texture btnProgressMenuTex;
    private Texture btnMainMenuTex;

    // Định vị vùng va chạm cho các nút bấm trên HUD (Sử dụng Rectangle)
    private Rectangle rectRestartBtn;
    private Rectangle rectProgressBtn;
    private Rectangle rectMainMenuBtn;

    // --- Constructor nhận cả Map (World) và Level thực tế ---
    public PlayScreen(AgentDungGame game, int world, int level) {
        this.game = game;
        this.currentWorld = world;
        this.currentLevel = level;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 400 * (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth());

        this.mapManager = new MapManager();
        this.entityManager = new EntityManager();
        this.lightRenderer = new LightRenderer();
        this.manaTextures = new HashMap<>();

        // Nạp tài nguyên hình ảnh và âm thanh khi thua cuộc
        capturedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/captured.ogg"));
        titleCapturedTex = new Texture(Gdx.files.internal("ui/UI_title_captured.png"));
        btnRestartTex = new Texture(Gdx.files.internal("ui/UI_button_restart_red.png"));
        btnProgressMenuTex = new Texture(Gdx.files.internal("ui/UI_button_progressmenu_red.png"));
        btnMainMenuTex = new Texture(Gdx.files.internal("ui/UI_button_mainmenu_red.png"));

        // Cài đặt bộ lọc điểm ảnh sắc nét cho Pixel Art
        titleCapturedTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnRestartTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnProgressMenuTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnMainMenuTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        rectRestartBtn = new Rectangle();
        rectProgressBtn = new Rectangle();
        rectMainMenuBtn = new Rectangle();

        initLevel(currentLevel);
    }

    private void initLevel(int level) {
        // --- Dừng toàn bộ âm thanh đang lặp trước khi nạp lại map/level ---
        InputHandler.stopLoopingSounds(this.game);
        isCaptured = false; // Đặt lại trạng thái chưa bị bắt khi chơi lại

        if (skills != null) {
            for (Skill s : skills) {
                if (s instanceof VomitSkill) ((VomitSkill) s).dispose();
                if (s instanceof SpitSkill) ((SpitSkill) s).dispose();
                if (s instanceof PoopSkill) ((PoopSkill) s).dispose();
            }
        }

        // Tải map động theo cấu trúc map[currentWorld]_[level].tmx bên trong MapManager
        mapManager.loadLevel(currentWorld, level);

        dung = new Player(mapManager.playerSpawn.x, mapManager.playerSpawn.y, this.game);
        dung.setSize(26);

        entityManager.initEnemies(mapManager);
        entityManager.clearAll();
        hasKey = false;

        for (Texture tex : manaTextures.values()) if (tex != null) tex.dispose();
        manaTextures.clear();
        manaTextures.put(SpitSkill.class, new Texture("ui/UI_mana_spit.png"));
        manaTextures.put(VomitSkill.class, new Texture("ui/UI_mana_vomit.png"));
        manaTextures.put(PeeSkill.class, new Texture("ui/UI_mana_pee.png"));
        manaTextures.put(PoopSkill.class, new Texture("ui/UI_mana_poop.png"));

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

        // Render Khối hình học (ShapeRenderer)
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mapManager.renderShapes(game.shapeRenderer);

        // --- Vẽ thanh máu của Server bằng ShapeRenderer lên trên màn chơi ---
        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderHpBar(game.shapeRenderer);
        }

        entityManager.renderShapes(game.shapeRenderer);
        game.shapeRenderer.end();

        // 3. Render Sprite ảnh
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        dung.draw(game.batch);
        for (com.agentdung.game.entities.Enemy e : entityManager.enemies) e.draw(game.batch);
        mapManager.renderSprites(game.batch);

        // --- Vẽ hình ảnh ảnh
        if (mapManager.targetServer != null) {
            mapManager.targetServer.renderSprite(game.batch);
        }

        entityManager.renderSprites(game.batch, skills);
        game.batch.end();

        // Render Mặt nạ Bóng tối FBO
        lightRenderer.renderDarkness(game.batch, dung, camera);

        // Render Đèn pin của Lính xuyên bóng tối
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        lightRenderer.renderEnemyVision(game.shapeRenderer, entityManager, mapManager);

        // 8. Vẽ HUD và Giao diện Bị bắt nếu có
        game.batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        game.batch.begin();
        renderHUD();
        if (hasKey) game.batch.draw(mapManager.keyTexture, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 50, 32, 32);
        game.batch.end();

        // --- ĐÈ GIAO DIỆN CAPTURED LÊN TRÊN CÙNG KHI THUA ---
        if (isCaptured) {
            renderCapturedOverlay();
        }

        // --- SỬA LỖI: Thêm stopLoopingSounds khi bấm ESCAPE để tránh lặp tiếng đái ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            InputHandler.stopLoopingSounds(this.game);
            game.setScreen(new MenuScreen(game));
        }
    }

    private void update(float delta) {
        // Nếu đã bị lính bắt, đóng băng toàn bộ game logic, chỉ cập nhật xử lý nhấn nút menu
        if (isCaptured) {
            handleCapturedInput();
            return;
        }

        InputHandler.handleTankMovement(delta, dung, camera, mapManager);
        dung.update(delta, dung, mapManager.wallRects);

        // Hộp va chạm của Agent Dũng
        Rectangle dungRect = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        // Xử lý ăn chìa khóa
        for (int i = mapManager.keys.size - 1; i >= 0; i--) {
            if (dungRect.overlaps(mapManager.keys.get(i))) {
                // ĐÃ CẬP NHẬT: Chỉ phát âm thanh nếu SFX hệ thống đang bật
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                hasKey = true;
                mapManager.keys.removeIndex(i);
            }
        }

        // Xử lý va chạm ăn vật phẩm hồi mana
        for (int i = mapManager.items.size - 1; i >= 0; i--) {
            Item item = mapManager.items.get(i);
            Rectangle itemRect = new Rectangle(item.getPosition().x, item.getPosition().y, item.getSize(), item.getSize());

            if (dungRect.overlaps(itemRect)) {
                if (item.type == Item.ItemType.ROTTEN_MEAT) {
                    // ĐÃ CẬP NHẬT: Kiểm tra cài đặt âm thanh khi nhặt thịt thiu
                    if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();

                    // Ăn thịt thiu hồi ngay 40 mana cho chiêu Ị và Nôn
                    for (Skill s : skills) {
                        if (s instanceof PoopSkill || s instanceof VomitSkill) {
                            s.gainMana(40f);
                        }
                    }
                } else if (item.type == Item.ItemType.WATER) {
                    // ĐÃ CẬP NHẬT: Kiểm tra cài đặt âm thanh khi nhặt/uống chai nước
                    if (game.isMasterOn && game.isSfxOn && game.pickWaterSound != null) game.pickWaterSound.play();

                    // Uống nước hồi ngay 50 mana cho chiêu Đái
                    for (Skill s : skills) {
                        if (s instanceof PeeSkill) {
                            s.gainMana(50f);
                        }
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

        // Cập nhật âm thanh kỹ năng tuân thủ cấu hình Settings
        InputHandler.handleSkillInput(dung, skills, entityManager, this.game);

        // KÍCH HOẠT LẠI KHI BỊ LÍNH BẮT (ĐÃ FIX: Hiện panel đỏ thay vì rỗng)
        entityManager.update(delta, dung, mapManager, () -> {
            if (!isCaptured) {
                isCaptured = true;
                InputHandler.stopLoopingSounds(this.game); // Dừng tiếng xả đái/ị lặp đi lặp lại tức thì

                // Phát âm thanh captured.ogg tuân theo thiết lập hệ thống âm thanh của bạn
                if (game.isMasterOn && game.isSfxOn && capturedSound != null) {
                    capturedSound.play();
                }
            }
        });

        // Camera theo dõi nhân vật mượt mà
        camera.position.x = MathUtils.clamp(dung.getPosition().x + dung.getSize() / 2, camera.viewportWidth / 2, mapManager.mapWidth - camera.viewportWidth / 2);
        camera.position.y = MathUtils.clamp(dung.getPosition().y + dung.getSize() / 2, camera.viewportHeight / 2, mapManager.mapHeight - camera.viewportHeight / 2);
        camera.update();

        // --- Kiểm tra điều kiện qua màn khi sập nguồn Server ---
        if (mapManager.targetServer != null && mapManager.targetServer.hp <= 0) {
            // Nếu level vừa qua lớn hơn kỉ lục cũ của map này, cập nhật kỉ lục mới chạy thật
            if (currentLevel > game.completedLevelsReal[currentWorld - 1]) {
                game.completedLevelsReal[currentWorld - 1] = currentLevel;
            }

            // --- Dập tắt âm thanh PeeSkill ngay lập tức trước khi nhảy sang LevelDoneScreen ---
            InputHandler.stopLoopingSounds(this.game);

            // Chuyển hướng sang giao diện kết quả LevelDoneScreen
            game.setScreen(new LevelDoneScreen(game, currentWorld, currentLevel));
        }

        // Vòng lặp cập nhật các kỹ năng
        for (Skill s : skills) s.update(delta);
    }

    /**
     * Hàm vẽ hiệu ứng Vignette ám đỏ mờ góc màn hình cùng chữ CAPTURED và các nút điều hướng màu đỏ
     */
    /**
     * Hàm vẽ hiệu ứng Vignette ám đỏ mờ góc màn hình cùng chữ CAPTURED và các nút điều hướng màu đỏ
     */
    private void renderCapturedOverlay() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        Matrix4 hudMatrix = new Matrix4().setToOrtho2D(0, 0, sw, sh);

        // --- 1. VẼ HIỆU ỨNG VIỀN ĐỎ MỜ NHẠT DẦN VÀO TRONG (VIGNETTE) ---
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shapeRenderer.setProjectionMatrix(hudMatrix);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int edgeLayers = 15; // Số lớp vẽ đè tạo dải gradient mờ
        float maxEdgeWidth = sw * 0.15f; // Độ rộng tối đa của góc mờ đỏ tính từ viền

        for (int i = 0; i < edgeLayers; i++) {
            float progress = (float) i / edgeLayers;
            float alpha = progress * 0.45f; // Viền ngoài cùng đậm nhất đạt 0.45 alpha
            game.shapeRenderer.setColor(new Color(0.7f, 0f, 0f, alpha));

            float thicknessX = (1f - progress) * maxEdgeWidth;
            float thicknessY = (1f - progress) * maxEdgeWidth * (sh / sw);

            // Cạnh viền dưới
            game.shapeRenderer.rect(0, 0, sw, thicknessY);
            // Cạnh viền trên
            game.shapeRenderer.rect(0, sh - thicknessY, sw, thicknessY);
            // Cạnh viền trái
            game.shapeRenderer.rect(0, thicknessY, thicknessX, sh - (2 * thicknessY));
            // Cạnh viền phải
            game.shapeRenderer.rect(sw - thicknessX, thicknessY, thicknessX, sh - (2 * thicknessY));
        }
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // --- 2. VẼ TEXTURE TIÊU ĐỀ VÀ CÁC NÚT BẤM ĐỎ ---
        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

        // Vẽ chữ tiêu đề lớn giữa màn hình "CAPTURED" cách đỉnh màn hình một khoảng cân đối
        float titleW = 420f;
        float titleH = 90f;
        float titleX = (sw - titleW) / 2f;
        float titleY = sh * 0.68f;
        game.batch.draw(titleCapturedTex, titleX, titleY, titleW, titleH);

        // Tỷ lệ nút bấm cố định ngang 360 cao 50
        float btnW = 360f;
        float btnH = 50f;
        float btnX = (sw - btnW) / 2f;

        // --- GIẢI PHÁP CỐ ĐỊNH KHOẢNG CÁCH ---
        // Lấy nút Progress Menu ở giữa làm gốc (vị trí bằng 35% chiều cao màn hình)
        float progressY = sh * 0.35f;
        float btnGap = 25f; // Khoảng cách cố định bằng Pixel giữa các nút khi co giãn màn hình

        // Nút trên bằng gốc + chiều cao nút + khoảng cách
        float restartY = progressY + btnH + btnGap;
        // Nút dưới bằng gốc - chiều cao nút - khoảng cách
        float mainMenuY = progressY - btnH - btnGap;

        // Cập nhật vị trí tọa độ hitbox hình chữ nhật để bắt trúng click chuột trái
        rectRestartBtn.set(btnX, restartY, btnW, btnH);
        rectProgressBtn.set(btnX, progressY, btnW, btnH);
        rectMainMenuBtn.set(btnX, mainMenuY, btnW, btnH);

        // Đổ hình ảnh nút ra màn hình HUD
        game.batch.draw(btnRestartTex, rectRestartBtn.x, rectRestartBtn.y, rectRestartBtn.width, rectRestartBtn.height);
        game.batch.draw(btnProgressMenuTex, rectProgressBtn.x, rectProgressBtn.y, rectProgressBtn.width, rectProgressBtn.height);
        game.batch.draw(btnMainMenuTex, rectMainMenuBtn.x, rectMainMenuBtn.y, rectMainMenuBtn.width, rectMainMenuBtn.height);

        game.batch.end();
    }

    /**
     * Nhận diện hành vi click chuột vào tọa độ của các nút menu đỏ khi đang bị bắt
     */
    private void handleCapturedInput() {
        if (Gdx.input.justTouched()) {
            // Đổi hệ tọa độ LibGDX Touch (gốc trên bên trái) sang HUD (gốc dưới bên trái)
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPoint.y = Gdx.graphics.getHeight() - touchPoint.y;

            // Nhấn nút Restart đỏ
            if (rectRestartBtn.contains(touchPoint.x, touchPoint.y)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                initLevel(currentLevel);
            }
            // Nhấn nút Progress Menu đỏ (Đã sửa đổi để chuyển hướng chuẩn xác và truyền dữ liệu map thế giới)
            else if (rectProgressBtn.contains(touchPoint.x, touchPoint.y)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                game.setScreen(new ProgressScreen(game, currentWorld));
            }
            // Nhấn nút Main Menu đỏ
            else if (rectMainMenuBtn.contains(touchPoint.x, touchPoint.y)) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        }
    }

    private void renderHUD() {
        float startX = 20;
        float targetWidth = 150;
        for (int i = 0; i < skills.size; i++) {
            Skill s = skills.get(i);
            Texture tex = manaTextures.get(s.getClass());
            if (tex != null) {
                float progress = s.getManaPercent();
                float startY = Gdx.graphics.getHeight() - 40 - (i * 25);
                int srcWidth = (int) (tex.getWidth() * progress);
                float drawWidth = targetWidth * progress;
                if (srcWidth > 0) {
                    game.batch.draw(tex, startX, startY, drawWidth, 15, 0, 0, srcWidth, tex.getHeight(), false, false);
                }
            }
        }
    }

    @Override
    public void dispose() {
        // Chủ động ngắt toàn bộ âm thanh kỹ năng đang lặp khi rời màn hình chơi
        InputHandler.stopLoopingSounds(this.game);

        // Giải phóng triệt để bộ nhớ các Texture và Sound mới thêm của màn hình Captured
        if (capturedSound != null) capturedSound.dispose();
        if (titleCapturedTex != null) titleCapturedTex.dispose();
        if (btnRestartTex != null) btnRestartTex.dispose();
        if (btnProgressMenuTex != null) btnProgressMenuTex.dispose();
        if (btnMainMenuTex != null) btnMainMenuTex.dispose();

        mapManager.dispose();
        entityManager.dispose();
        lightRenderer.dispose();
        if (manaTextures != null) {
            for (Texture tex : manaTextures.values()) if (tex != null) tex.dispose();
            manaTextures.clear();
        }
        if (skills != null) {
            for (Skill s : skills) {
                if (s instanceof VomitSkill) ((VomitSkill) s).dispose();
                if (s instanceof SpitSkill) ((SpitSkill) s).dispose();
                if (s instanceof PoopSkill) ((PoopSkill) s).dispose();
            }
        }
    }
}
