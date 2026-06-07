package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.skills.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

public class GameHUD {
    private final AgentDungGame game;
    private final Map<Class<? extends Skill>, Texture> manaTextures;

    // Quản lý tài nguyên và vùng va chạm của nút Pause
    private Texture btnPauseTex;
    private final Rectangle rectPauseBtn;

    // --- THÊM MỚI: Quản lý tài nguyên và vùng va chạm của nút Balo (Bag) ---
    private Texture btnBagTex;
    private final Rectangle rectBagBtn;

    public GameHUD(AgentDungGame game) {
        this.game = game;
        this.manaTextures = new HashMap<>();
        this.rectPauseBtn = new Rectangle();
        this.rectBagBtn = new Rectangle(); // <-- Khởi tạo vùng va chạm cho nút Bag
    }

    public void loadTextures() {
        clearTextures();

        // Nạp texture cho các thanh mana kỹ năng
        manaTextures.put(SpitSkill.class, new Texture("ui/UI_mana_spit.png"));
        manaTextures.put(VomitSkill.class, new Texture("ui/UI_mana_vomit.png"));
        manaTextures.put(PeeSkill.class, new Texture("ui/UI_mana_pee.png"));
        manaTextures.put(PoopSkill.class, new Texture("ui/UI_mana_poop.png"));

        // Nạp texture cho nút Pause
        btnPauseTex = new Texture(Gdx.files.internal("ui/UI_button_pause.png"));
        btnPauseTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // --- THÊM MỚI: Nạp texture cho nút Bag từ thư mục ui ---
        btnBagTex = new Texture(Gdx.files.internal("ui/UI_button_bag.png"));
        btnBagTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    // Hàm render nhận đầy đủ 3 tham số: skills, hudMatrix, và hasKey để đồng bộ với PlayScreen
    public void render(Array<Skill> skills, Matrix4 hudMatrix, boolean hasKey) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

        // 1. Vẽ các thanh Mana của Agent Dũng ở góc trái màn hình
        float startX = 20;
        float targetWidth = 150;
        for (int i = 0; i < skills.size; i++) {
            Skill s = skills.get(i);
            Texture tex = manaTextures.get(s.getClass());
            if (tex != null) {
                float progress = s.getManaPercent();
                float startY = sh - 40 - (i * 25);
                int srcWidth = (int) (tex.getWidth() * progress);
                float drawWidth = targetWidth * progress;
                if (srcWidth > 0) {
                    game.batch.draw(tex, startX, startY, drawWidth, 15, 0, 0, srcWidth, tex.getHeight(), false, false);
                }
            }
        }

        // 2. Định vị và vẽ nút Pause ở góc phải trên cùng (Kích thước 40x40, cách lề 20px)
        float btnSize = 40f;
        float pauseX = sw - btnSize - 20f;
        float pauseY = sh - btnSize - 20f;
        rectPauseBtn.set(pauseX, pauseY, btnSize, btnSize);

        if (btnPauseTex != null) {
            game.batch.draw(btnPauseTex, rectPauseBtn.x, rectPauseBtn.y, rectPauseBtn.width, rectPauseBtn.height);
        }

        // --- THÊM MỚI: Định vị và vẽ nút Bag nằm dịch sang bên trái nút Pause 15px ---
        float bagX = pauseX - btnSize - 15f;
        float bagY = pauseY;
        rectBagBtn.set(bagX, bagY, btnSize, btnSize);

        if (btnBagTex != null) {
            game.batch.draw(btnBagTex, rectBagBtn.x, rectBagBtn.y, rectBagBtn.width, rectBagBtn.height);
        }

        game.batch.end();
    }

    // Cung cấp Rectangle va chạm để PlayScreen kiểm tra click chuột vào nút Pause
    public Rectangle getRectPauseBtn() {
        return rectPauseBtn;
    }

    // --- THÊM MỚI: Cung cấp Rectangle va chạm để PlayScreen kiểm tra click chuột vào nút Bag ---
    public Rectangle getRectBagBtn() {
        return rectBagBtn;
    }

    public void clearTextures() {
        // Giải phóng các texture mana
        for (Texture tex : manaTextures.values()) {
            if (tex != null) tex.dispose();
        }
        manaTextures.clear();

        // Giải phóng triệt để texture nút pause
        if (btnPauseTex != null) {
            btnPauseTex.dispose();
            btnPauseTex = null;
        }

        // --- THÊM MỚI: Giải phóng triệt để texture nút bag ---
        if (btnBagTex != null) {
            btnBagTex.dispose();
            btnBagTex = null;
        }
    }

    public void dispose() {
        clearTextures();
    }
}
