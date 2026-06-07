package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Item extends Entity {

    public enum ItemType {
        BEER("Beer", "images/beer.png", 15, "Hoi 10% chieu Vomit\nva 30% chieu Pee."),
        AMULET("Amulet", "images/amulet.png", 50, "Bi linh phat hien mot lan\nkhong sao."),
        CARROT("Carrot", "images/carrot.png", 10, "Mo rong khoang nhin thay\ncua nhan vat."),
        CLOCK("Clock", "images/clock.png", 40, "Dong bang thoi gian trong 2s.\nToan bo linh dung im."),
        COIN("Coin", "images/coin.png", 0, "Tien de mua cac vat pham\no Vending Machine."),
        INVISIBILITY("Invisibility", "images/invisibility.png", 35, "Tang hinh trong 2s.\nLinh khong the nhin thay ban."),
        KEY("Key", "images/key.png", 0, "Dung de mo cua phong Server.\nKhong ban trong may."),
        LEMON("Lemon", "images/lemon.png", 20, "Tang toc do hoi mana\nchieu Spit them 40% trong 10s."),
        ORANGE("Orange", "images/orange.png", 25, "Tang hoi mana Spit 15%\ntrong 10s, tang 10% Pee."),
        ROTTEN_EGG("Rotten Egg", "images/rotten_egg.png", 12, "Hoi 20% mana chieu Vomit."),
        ROTTEN_MEAT("Rotten Meat", "images/rotten_meat.png", 8, "Hoi 10% mana chieu Vomit,\nhoi 20% mana chieu Poop."),
        SHOES("Shoes", "images/shoes.png", 30, "Tang toc do di chuyen\nthem 30% trong 5s."),
        WATER("Water", "images/water.png", 8, "Hoi 40% mana chieu Pee."),
        WHISKEY("Whiskey", "images/whiskey.png", 18, "Hoi 20% mana chieu Vomit.");

        public final String displayName;
        public final String texturePath;
        public final int price;
        public final String description;

        ItemType(String displayName, String texturePath, int price, String description) {
            this.displayName = displayName;
            this.texturePath = texturePath;
            this.price = price;
            this.description = description;
        }
    }

    public ItemType type;
    public Rectangle bounds;

    public Item(float x, float y, ItemType type) {
        super(x, y, 0f, 16f); // Vật phẩm đứng im, kích thước 16x16
        this.type = type;
        this.bounds = new Rectangle(x, y, this.size, this.size);
    }

    @Override
    public void render(ShapeRenderer shape) {}

    @Override
    public void update(float delta, Player player, Array<Rectangle> walls) {}
}
