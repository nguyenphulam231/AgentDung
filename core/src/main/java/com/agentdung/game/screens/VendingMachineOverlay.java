package com.agentdung.game.screens;

import com.agentdung.game.entities.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import java.util.Map;

public class VendingMachineOverlay {
    private final PlayScreen screen;
    private final BitmapFont font;

    private Texture titleTex, bigFrameTex, smallFrameTex, btnBuyTex;
    private final Array<VendingSlot> slots = new Array<>();
    private VendingSlot selectedSlot = null;
    private Rectangle btnBuyBounds;

    // Biến quản lý thời gian hiển thị thông báo thiếu tiền
    private float notEnoughCoinsTimer = 0f;

    // --- CỐ ĐỊNH: Định nghĩa kích thước màn hình ảo cho giao diện UI tránh lỗi tràn chữ ---
    private final float VIRTUAL_WIDTH = 800f;
    private final float VIRTUAL_HEIGHT = 600f;

    public VendingMachineOverlay(PlayScreen screen) {
        this.screen = screen;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.2f); // Chỉnh kích thước chữ vừa vặn với độ phân giải ảo

        titleTex = new Texture("ui/UI_title_vendingmachine.png");
        bigFrameTex = new Texture("ui/bigframe.png");
        smallFrameTex = new Texture("ui/smallframe.png");
        btnBuyTex = new Texture("ui/UI_button_buy.png");

        btnBuyBounds = new Rectangle();
        setupVendingProducts();
    }

    private void setupVendingProducts() {
        slots.clear();
        // Máy bán tự động có bán tất cả các vật phẩm ngoại trừ KEY và COIN
        for (Item.ItemType type : Item.ItemType.values()) {
            if (type == Item.ItemType.KEY || type == Item.ItemType.COIN) continue;
            slots.add(new VendingSlot(type));
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shape, Matrix4 originalHudMatrix) {
        // Giảm delta time cho bộ đếm thông báo
        if (notEnoughCoinsTimer > 0) {
            notEnoughCoinsTimer -= Gdx.graphics.getDeltaTime();
        }

        // --- QUAN TRỌNG: Tạo ma trận chiếu dựa trên không gian ảo 800x600 ---
        Matrix4 overlayMatrix = new Matrix4().setToOrtho2D(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // 1. Phủ nền đen sẫm màu mờ
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        shape.setProjectionMatrix(overlayMatrix);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0, 0, 0, 0.75f);
        shape.rect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        shape.end();

        batch.setProjectionMatrix(overlayMatrix);
        batch.begin();

        // 2. Vẽ Tiêu đề Vending Machine chính giữa phía trên
        float titleX = VIRTUAL_WIDTH / 2f - titleTex.getWidth() / 2f;
        float titleY = VIRTUAL_HEIGHT - titleTex.getHeight() - 30;
        batch.draw(titleTex, titleX, titleY);

        // Hiển thị Ví tiền hiện tại và hướng dẫn nút bấm
        font.setColor(Color.GOLD);
        font.draw(batch, "Your Coins: " + screen.coinCount, 40, VIRTUAL_HEIGHT - 40);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[Press ESC to Exit]", VIRTUAL_WIDTH - 220, VIRTUAL_HEIGHT - 40);
        font.setColor(Color.WHITE);

        // 3. Vẽ Khung mô tả lớn bên trái
        float bigX = VIRTUAL_WIDTH * 0.08f;
        float bigY = VIRTUAL_HEIGHT * 0.18f;
        float bigW = VIRTUAL_WIDTH * 0.38f;
        float bigH = VIRTUAL_HEIGHT * 0.55f;
        batch.draw(bigFrameTex, bigX, bigY, bigW, bigH);

        // 4. Vẽ Lưới các mặt hàng thương mại bên phải (Đổi thành 4 cột cho gọn gàng)
        float startSmallX = VIRTUAL_WIDTH * 0.52f;
        float smallY = VIRTUAL_HEIGHT * 0.62f;
        float slotSize = 54f;
        float gap = 15f;

        for (int i = 0; i < slots.size; i++) {
            int col = i % 4;
            int row = i / 4;
            float x = startSmallX + col * (slotSize + gap);
            float y = smallY - row * (slotSize + gap);

            VendingSlot slot = slots.get(i);
            slot.bounds.set(x, y, slotSize, slotSize);

            batch.draw(smallFrameTex, x, y, slotSize, slotSize);

            // --- SỬA LỖI MEMORY LEAK: Trích xuất ảnh an toàn từ cache MapManager có sẵn ---
            Texture texIcon = screen.mapManager.vendingMachineTexture; // Ảnh tạm nếu lỗi
            try {
                java.lang.reflect.Field field = screen.mapManager.getClass().getDeclaredField("itemTextures");
                field.setAccessible(true);
                Map<?, Texture> mapTex = (Map<?, Texture>) field.get(screen.mapManager);
                for (Object holder : mapTex.keySet()) {
                    if (holder.toString().equals(slot.type.name())) {
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

            // Hiển thị nhãn giá tiền xu bằng số nhỏ ngay dưới ô sản phẩm
            font.draw(batch, slot.type.price + "C", x + 6, y + 18);

            if (selectedSlot == slot) {
                batch.end();
                shape.begin(ShapeRenderer.ShapeType.Line);
                shape.setColor(Color.GREEN);
                shape.rect(x, y, slotSize, slotSize);
                shape.end();
                batch.begin();
            }
        }

        // 5. Hiện thông số giá cả khi click lựa chọn vật phẩm
        if (selectedSlot != null) {
            float textX = bigX + 25;
            float textY = bigY + bigH - 40;
            font.setColor(Color.GREEN);
            font.draw(batch, "PRODUCT: " + selectedSlot.type.displayName, textX, textY);
            font.setColor(Color.WHITE);
            font.draw(batch, selectedSlot.type.description, textX, textY - 40);

            font.draw(batch, "PRICE: " + selectedSlot.type.price + " Coins", bigX + 25, bigY + 80);

            int inBag = screen.inventory.getOrDefault(selectedSlot.type, 0);
            font.draw(batch, "In Inventory: " + inBag, bigX + 25, bigY + 45);
        } else {
            font.draw(batch, "Select an item to purchase", bigX + 25, bigY + bigH - 40);
        }

        // 6. Nút bấm mua hàng BUY
        float btnW = 110f;
        float btnH = 40f;
        float btnX = VIRTUAL_WIDTH * 0.90f - btnW;
        float btnY = bigY;
        btnBuyBounds.set(btnX, btnY, btnW, btnH);
        batch.draw(btnBuyTex, btnX, btnY, btnW, btnH);

        // 7. VẼ THÔNG BÁO KHÔNG ĐỦ TIỀN (Hiển thị nổi bật dạng chữ màu đỏ phía trên nút BUY một chút)
        if (notEnoughCoinsTimer > 0) {
            font.setColor(Color.RED);
            // Hiệu ứng nhấp nháy nhẹ dựa trên thời gian thực giúp người chơi chú ý hơn
            if ((int)(notEnoughCoinsTimer * 5) % 2 == 0) {
                font.draw(batch, "You don't have enough coins!", bigX + 25, bigY + 120);
            }
            font.setColor(Color.WHITE);
        }

        batch.end();
    }

    public void handleInput() {
        // Cho phép nhấn ESC để thoát nhanh khỏi máy bán hàng
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.clickSound != null) screen.game.clickSound.play();
            screen.isVendingOpen = false;
            return;
        }

        if (Gdx.input.justTouched()) {
            // --- ĐỒNG BỘ: Chuyển đổi vị trí chuột click tương ứng không gian ảo 800x600 ---
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touch.x = (touch.x / (float) Gdx.graphics.getWidth()) * VIRTUAL_WIDTH;
            touch.y = (1 - touch.y / (float) Gdx.graphics.getHeight()) * VIRTUAL_HEIGHT;

            // Kiểm tra click chọn hàng trên kệ máy bán tự động
            for (VendingSlot slot : slots) {
                if (slot.bounds.contains(touch.x, touch.y)) {
                    if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.clickSound != null) screen.game.clickSound.play();
                    selectedSlot = slot;
                    // Reset timer thông báo khi đổi lựa chọn vật phẩm khác
                    notEnoughCoinsTimer = 0f;
                    return;
                }
            }

            // Kiểm tra click mua hàng qua nút BUY
            if (btnBuyBounds.contains(touch.x, touch.y) && selectedSlot != null) {
                if (screen.coinCount >= selectedSlot.type.price) {
                    if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.clickSound != null) screen.game.clickSound.play();

                    // Thực hiện trừ tiền xu thành công
                    screen.coinCount -= selectedSlot.type.price;

                    // Cộng thẳng số lượng vào ba lô lưu trữ của PlayScreen
                    int curCount = screen.inventory.getOrDefault(selectedSlot.type, 0);
                    screen.inventory.put(selectedSlot.type, curCount + 1);

                    if (selectedSlot.type == Item.ItemType.AMULET) screen.amuletCount++;

                    // Mua thành công thì tắt trạng thái cảnh báo nếu có trước đó
                    notEnoughCoinsTimer = 0f;
                } else {
                    // --- THAY ĐỔI: Kích hoạt bộ đếm thời gian 2 giây hiển thị thông báo thiếu tiền ---
                    notEnoughCoinsTimer = 2.0f;

                    // Có thể thêm một âm thanh thông báo lỗi (error sound) tại đây nếu game của bạn có sẵn
                    if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.clickSound != null) {
                        screen.game.clickSound.play(); // Tạm thời dùng clickSound hoặc sound fail tùy ý
                    }
                }
            }
        }
    }

    public void dispose() {
        if (titleTex != null) titleTex.dispose();
        if (bigFrameTex != null) bigFrameTex.dispose();
        if (smallFrameTex != null) smallFrameTex.dispose();
        if (btnBuyTex != null) btnBuyTex.dispose();
        if (font != null) font.dispose();
    }

    private static class VendingSlot {
        public Item.ItemType type;
        public Rectangle bounds;

        public VendingSlot(Item.ItemType type) {
            this.type = type;
            this.bounds = new Rectangle();
        }
    }
}
