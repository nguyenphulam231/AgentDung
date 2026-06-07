package com.agentdung.game.screens;

import com.agentdung.game.entities.Item;
import com.agentdung.game.skills.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector3;
import java.util.Map;

public class InventoryOverlay {
    private final PlayScreen screen;
    private final BitmapFont font;

    private Texture titleTex, bigFrameTex, smallFrameTex, btnUseTex;
    private final Array<ItemSlot> slots = new Array<>();
    private Item.ItemType selectedType = null; // Lưu loại vật phẩm đang chọn thay vì lưu Slot tĩnh
    private Rectangle btnUseBounds;

    private final float VIRTUAL_WIDTH = 800f;
    private final float VIRTUAL_HEIGHT = 600f;
    private final int MAX_SLOTS = 12; // Cố định hiển thị 12 ô (3 hàng x 4 cột)

    public InventoryOverlay(PlayScreen screen) {
        this.screen = screen;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.2f);

        titleTex = new Texture("UI/UI_title_inventory.png");
        bigFrameTex = new Texture("UI/bigframe.png");
        smallFrameTex = new Texture("UI/smallframe.png");
        btnUseTex = new Texture("UI/UI_button_use.png");

        btnUseBounds = new Rectangle();

        // Khởi tạo sẵn 12 vùng chứa (Slot) cố định vị trí trên màn hình
        for (int i = 0; i < MAX_SLOTS; i++) {
            slots.add(new ItemSlot());
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shape, Matrix4 originalHudMatrix) {
        Matrix4 overlayMatrix = new Matrix4().setToOrtho2D(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // 1. Vẽ nền đen mờ
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        shape.setProjectionMatrix(overlayMatrix);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0, 0, 0, 0.6f);
        shape.rect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        shape.end();

        batch.setProjectionMatrix(overlayMatrix);
        batch.begin();

        // 2. Vẽ Tiêu đề & Tiền xu
        float titleX = VIRTUAL_WIDTH / 2f - titleTex.getWidth() / 2f;
        float titleY = VIRTUAL_HEIGHT - titleTex.getHeight() - 30;
        batch.draw(titleTex, titleX, titleY);
        font.draw(batch, "Coin: " + screen.coinCount, 40, VIRTUAL_HEIGHT - 40);

        // 3. Vẽ Khung thông tin chi tiết bên trái
        float bigX = VIRTUAL_WIDTH * 0.08f;
        float bigY = VIRTUAL_HEIGHT * 0.18f;
        float bigW = VIRTUAL_WIDTH * 0.38f;
        float bigH = VIRTUAL_HEIGHT * 0.55f;
        batch.draw(bigFrameTex, bigX, bigY, bigW, bigH);

        // 4. LỌC DANH SÁCH: Chỉ lấy những Item có số lượng > 0 trong hành trang
        Array<Item.ItemType> activeItems = new Array<>();
        for (Item.ItemType type : Item.ItemType.values()) {
            if (type == Item.ItemType.COIN) continue;
            int count = screen.inventory.getOrDefault(type, 0);
            if (count > 0) {
                activeItems.add(type);
            }
        }

        // Nếu item đang chọn bỗng nhiên hết số lượng (bằng 0), hủy chọn nó
        if (selectedType != null && screen.inventory.getOrDefault(selectedType, 0) <= 0) {
            selectedType = null;
        }

        // 5. Vẽ lưới 12 ô đồ cố định bên phải
        float startSmallX = VIRTUAL_WIDTH * 0.52f;
        float smallY = VIRTUAL_HEIGHT * 0.62f;
        float slotSize = 54f;
        float gap = 15f;

        for (int i = 0; i < MAX_SLOTS; i++) {
            int col = i % 4; // 4 cột
            int row = i / 4; // 3 hàng
            float x = startSmallX + col * (slotSize + gap);
            float y = smallY - row * (slotSize + gap);

            ItemSlot slot = slots.get(i);
            slot.bounds.set(x, y, slotSize, slotSize);

            // Ô đồ này tạm thời chưa chứa loại item nào
            slot.assignedType = null;

            // Vẽ khung ô nhỏ làm nền (Lúc nào cũng vẽ đủ 12 ô trống)
            batch.draw(smallFrameTex, x, y, slotSize, slotSize);

            // Nếu ô này được phân phối một item có số lượng > 0
            if (i < activeItems.size) {
                Item.ItemType currentType = activeItems.get(i);
                slot.assignedType = currentType; // Gắn loại item vào ô để xử lý click chuột

                // Lấy ảnh từ bộ nạp tập trung MapManager
                Texture texIcon = null;
                try {
                    java.lang.reflect.Field field = screen.mapManager.getClass().getDeclaredField("itemTextures");
                    field.setAccessible(true);
                    Map<?, Texture> mapTex = (Map<?, Texture>) field.get(screen.mapManager);
                    for (Object holder : mapTex.keySet()) {
                        if (holder.toString().equals(currentType.name())) {
                            texIcon = mapTex.get(holder);
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Fallback an toàn
                }

                if (texIcon != null) {
                    batch.draw(texIcon, x + 7, y + 7, slotSize - 14, slotSize - 14);
                }

                // Vẽ số lượng thực tế (Không lo bị x0 nữa)
                int count = screen.inventory.get(currentType);
                font.draw(batch, "x" + count, x + slotSize - 25, y + 18);

                // Vẽ viền highlight màu vàng nếu item thuộc ô này đang được chọn
                if (selectedType == currentType) {
                    batch.end();
                    shape.begin(ShapeRenderer.ShapeType.Line);
                    shape.setColor(Color.YELLOW);
                    shape.rect(x, y, slotSize, slotSize);
                    shape.end();
                    batch.begin();
                }
            }
        }

        // 6. Điền thông tin mô tả chi tiết vào khung lớn bên trái
        if (selectedType != null) {
            float textX = bigX + 25;
            float textY = bigY + bigH - 40;
            font.setColor(Color.GOLD);
            font.draw(batch, "ITEM: " + selectedType.displayName, textX, textY);
            font.setColor(Color.WHITE);
            font.draw(batch, selectedType.description, textX, textY - 40);

            int ownCount = screen.inventory.get(selectedType);
            font.draw(batch, "You own: " + ownCount, bigX + 25, bigY + 45);
        } else {
            font.draw(batch, "Select an item to view info", bigX + 25, bigY + bigH - 40);
        }

        // 7. Vẽ Nút bấm USE
        float btnW = 110f;
        float btnH = 40f;
        float btnX = VIRTUAL_WIDTH * 0.90f - btnW;
        float btnY = bigY;
        btnUseBounds.set(btnX, btnY, btnW, btnH);
        batch.draw(btnUseTex, btnX, btnY, btnW, btnH);

        batch.end();
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touch.x = (touch.x / (float) Gdx.graphics.getWidth()) * VIRTUAL_WIDTH;
            touch.y = (1 - touch.y / (float) Gdx.graphics.getHeight()) * VIRTUAL_HEIGHT;

            // Kiểm tra click trúng ô nào trên lưới đồ
            for (ItemSlot slot : slots) {
                if (slot.bounds.contains(touch.x, touch.y)) {
                    // Chỉ kích hoạt chọn nếu ô đó thực sự đang chứa một item
                    if (slot.assignedType != null) {
                        if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.clickSound != null) screen.game.clickSound.play();
                        selectedType = slot.assignedType;
                    }
                    return;
                }
            }

            // Kiểm tra click trúng nút USE
            if (btnUseBounds.contains(touch.x, touch.y) && selectedType != null) {
                int count = screen.inventory.getOrDefault(selectedType, 0);
                if (count > 0) {
                    if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.clickSound != null) screen.game.clickSound.play();

                    // Trừ đi 1 vật phẩm tiêu hao trong balo đồ
                    screen.inventory.put(selectedType, count - 1);
                    applyItemEffect(selectedType);

                    if (selectedType == Item.ItemType.AMULET) screen.amuletCount--;
                }
            }
        }
    }

    private void applyItemEffect(Item.ItemType type) {
        switch (type) {
            case BEER:
                for (Skill s : screen.skills) {
                    if (s instanceof VomitSkill) s.gainMana(10f);
                    if (s instanceof PeeSkill) s.gainMana(30f);
                }
                break;
            case AMULET:
                screen.amuletCount++;
                break;
            case CARROT:
                break;
            case CLOCK:
                screen.clockTimer = 2.0f;
                break;
            case INVISIBILITY:
                screen.invisibilityTimer = 2.0f;
                break;
            case LEMON:
                screen.lemonTimer = 10.0f;
                break;
            case ORANGE:
                screen.orangeTimer = 10.0f;
                for (Skill s : screen.skills) {
                    if (s instanceof PeeSkill) s.gainMana(10f);
                }
                break;
            case ROTTEN_EGG:
                for (Skill s : screen.skills) {
                    if (s instanceof VomitSkill) s.gainMana(20f);
                }
                break;
            case ROTTEN_MEAT:
                for (Skill s : screen.skills) {
                    if (s instanceof VomitSkill) s.gainMana(10f);
                    if (s instanceof PoopSkill) s.gainMana(20f);
                }
                break;
            case SHOES:
                screen.shoesTimer = 5.0f;
                break;
            case WATER:
                for (Skill s : screen.skills) {
                    if (s instanceof PeeSkill) s.gainMana(40f);
                }
                break;
            case WHISKEY:
                for (Skill s : screen.skills) {
                    if (s instanceof VomitSkill) s.gainMana(20f);
                }
                break;
        }
    }

    public void dispose() {
        if (titleTex != null) titleTex.dispose();
        if (bigFrameTex != null) bigFrameTex.dispose();
        if (smallFrameTex != null) smallFrameTex.dispose();
        if (btnUseTex != null) btnUseTex.dispose();
        if (font != null) font.dispose();
    }

    private static class ItemSlot {
        public Rectangle bounds;
        public Item.ItemType assignedType; // Loại item đang được gán vào ô này (null nếu ô trống)

        public ItemSlot() {
            this.bounds = new Rectangle();
            this.assignedType = null;
        }
    }
}
