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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
    int currentWorld = 1;

    public PlayScreen(AgentDungGame game, int level) {
        this.game = game;
        this.currentLevel = level;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 400 * (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth());

        this.mapManager = new MapManager();
        this.entityManager = new EntityManager();
        this.lightRenderer = new LightRenderer();
        this.manaTextures = new HashMap<>();

        initLevel(currentLevel);
    }

    private void initLevel(int level) {
        // --- CẬP NHẬT: Dừng toàn bộ âm thanh đang lặp (như tiếng đái) trước khi nạp lại map/level ---
        InputHandler.stopLoopingSounds(this.game);

        if (skills != null) {
            for (Skill s : skills) {
                if (s instanceof VomitSkill) ((VomitSkill) s).dispose();
                if (s instanceof SpitSkill) ((SpitSkill) s).dispose();
                if (s instanceof PoopSkill) ((PoopSkill) s).dispose();
            }
        }

        mapManager.loadLevel(currentWorld, level);

        // --- CẬP NHẬT: TRUYỀN THÊM THAM SỐ GAME VÀO KHỞI TẠO PLAYER ---
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

        // 1. Render Bản đồ
        mapManager.mapRenderer.setView(camera);
        mapManager.mapRenderer.render();

        // 2. Render Khối hình học (ShapeRenderer)
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mapManager.renderShapes(game.shapeRenderer);
        entityManager.renderShapes(game.shapeRenderer);
        game.shapeRenderer.end();

        // 3. Render Sprite ảnh (SpriteBatch)
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        dung.draw(game.batch);
        for (com.agentdung.game.entities.Enemy e : entityManager.enemies) e.draw(game.batch);
        mapManager.renderSprites(game.batch);
        entityManager.renderSprites(game.batch, skills);
        game.batch.end();

        // 4 & 5. Render Mặt nạ Bóng tối FBO
        lightRenderer.renderDarkness(game.batch, dung, camera);

        // 6. Render Đèn pin của Lính xuyên bóng tối
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        lightRenderer.renderEnemyVision(game.shapeRenderer, entityManager, mapManager);

        // 7. Vẽ thanh Mana trên đầu nhân vật
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        dung.render(game.shapeRenderer);
        game.shapeRenderer.end();

        // 8. Vẽ HUD
        game.batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        game.batch.begin();
        renderHUD();
        if (hasKey) game.batch.draw(mapManager.keyTexture, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 50, 32, 32);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (game.clickSound != null) game.clickSound.play();
            game.setScreen(new MenuScreen(game));
        }
    }

    private void update(float delta) {
        InputHandler.handleTankMovement(delta, dung, camera, mapManager);
        dung.update(delta, dung, mapManager.wallRects);

        // Hộp va chạm của Agent Dũng
        Rectangle dungRect = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        // Xử lý ăn chìa khóa
        for (int i = mapManager.keys.size - 1; i >= 0; i--) {
            if (dungRect.overlaps(mapManager.keys.get(i))) {
                // ĐÃ CẬP NHẬT: Phát âm thanh khi nhặt được chìa khóa
                if (game.clickSound != null) game.clickSound.play();
                hasKey = true;
                mapManager.keys.removeIndex(i);
            }
        }

        // --- THÊM: Xử lý va chạm ăn vật phẩm hồi mana ---
        for (int i = mapManager.items.size - 1; i >= 0; i--) {
            Item item = mapManager.items.get(i);
            // Lấy trực tiếp tọa độ và kích thước từ lớp cha Entity để tạo vùng va chạm
            Rectangle itemRect = new Rectangle(item.getPosition().x, item.getPosition().y, item.getSize(), item.getSize());

            if (dungRect.overlaps(itemRect)) {
                if (item.type == Item.ItemType.ROTTEN_MEAT) {
                    // ĐÃ CẬP NHẬT: Phát âm thanh nhặt thịt thiu (Tận dụng tạm clickSound hoặc bạn tùy biến sau)
                    if (game.clickSound != null) game.clickSound.play();

                    // Ăn thịt thiu hồi ngay 40 mana cho chiêu Ị và Nôn
                    for (Skill s : skills) {
                        if (s instanceof PoopSkill || s instanceof VomitSkill) {
                            s.gainMana(40f);
                        }
                    }
                } else if (item.type == Item.ItemType.WATER) {
                    // ĐÃ CẬP NHẬT: Phát âm thanh nhặt/uống chai nước từ tổng kho AgentDungGame
                    if (game.pickWaterSound != null) game.pickWaterSound.play();

                    // Uống nước hồi ngay 50 mana cho chiêu Đái
                    for (Skill s : skills) {
                        if (s instanceof PeeSkill) {
                            s.gainMana(50f);
                        }
                    }
                }
                // Xóa vật phẩm khỏi danh sách sau khi đã ăn thành công
                mapManager.items.removeIndex(i);
            }
        }

        // Cập nhật Cửa
        for (Door door : mapManager.doors) {
            float dist = Vector2.dst(dung.getPosition().x, dung.getPosition().y, door.bounds.x, door.bounds.y);
            door.update(delta, dist < 60f, hasKey);
        }

        // --- CẬP NHẬT: Truyền thêm tham số this.game vào để InputHandler xử lý âm thanh kỹ năng ---
        InputHandler.handleSkillInput(dung, skills, entityManager, this.game);

        // Gọi EntityManager cập nhật thực thể sống kèm hàm callback nếu bị lính bắt
        entityManager.update(delta, dung, mapManager, () -> initLevel(currentLevel));

        // Camera theo dõi nhân vật mượt mà
        camera.position.x = MathUtils.clamp(dung.getPosition().x + dung.getSize() / 2, camera.viewportWidth / 2, mapManager.mapWidth - camera.viewportWidth / 2);
        camera.position.y = MathUtils.clamp(dung.getPosition().y + dung.getSize() / 2, camera.viewportHeight / 2, mapManager.mapHeight - camera.viewportHeight / 2);
        camera.update();

        // Kiểm tra điều kiện qua màn khi sập nguồn Server
        if (mapManager.targetServer != null && mapManager.targetServer.hp <= 0) {
            currentLevel++;
            initLevel(currentLevel);
        }

        // Vòng lặp cập nhật các kỹ năng (Mana Khạc sẽ tự tăng ở đây nhờ BaseSkill mới)
        for (Skill s : skills) s.update(delta);
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
        // --- CẬP NHẬT: Chủ động ngắt toàn bộ âm thanh kỹ năng đang lặp khi rời màn hình chơi ---
        InputHandler.stopLoopingSounds(this.game);

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
