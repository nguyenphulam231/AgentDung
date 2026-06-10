package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Item extends Entity {

    public enum ItemType {
        BEER("Beer", "images/beer.png", 15, "Restores 10% Rainbow-Blast mana\nand 30% Golden Stream mana."),
        AMULET("Amulet", "images/amulet.png", 50, "Negates one instance of being\ndetected by enemies."),
        CARROT("Carrot", "images/carrot.png", 10, "Increases the character's\nfield of view."),
        CLOCK("Time Freezer", "images/clock.png", 40, "Freezes time for 2 seconds.\nAll enemies remain stationary."),
        COIN("Coin", "images/coin.png", 0, "Currency used to purchase items\nat the Vending Machine."),
        INVISIBILITY("Invisibility Potion", "images/invisibility.png", 35, "Become invisible for 2 seconds.\nEnemies cannot detect you."),
        KEY("Key", "images/key.png", 0, "Used to unlock the Server Room.\nNot sold in machines."),
        LEMON("Lemon", "images/lemon.png", 20, "Increases Hydro-shot mana regen\nby 40% for 10 seconds."),
        ORANGE("Orange", "images/orange.png", 25, "Hydro-shot mana regen +15%\nand Golden Stream +10% for 10s."),
        ROTTEN_EGG("Magic Egg", "images/rotten_egg.png", 12, "Restores 20% mana to\nRainbow-Blast."),
        ROTTEN_MEAT("Magic Meat", "images/rotten_meat.png", 8, "Restores 10% Rainbow-Blast mana,\n20% mana to Bio-bomb."),
        SHOES("Shoes", "images/shoes.png", 30, "Increases movement speed by\n30% for 5 seconds."),
        WATER("Water", "images/water.png", 8, "Restores 40% mana to\nGolden Stream."),
        WHISKEY("Whiskey", "images/whiskey.png", 18, "Restores 20% mana to\nRainbow-Blast.");

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
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        // Code vẽ hình ảnh vật phẩm (Vũ khí, máu, vật phẩm nhiệm vụ...)
        // Ví dụ: batch.draw(itemTexture, position.x, position.y, size, size);
    }

    @Override
    public void update(float delta, Player player, Array<Rectangle> walls) {}
}
