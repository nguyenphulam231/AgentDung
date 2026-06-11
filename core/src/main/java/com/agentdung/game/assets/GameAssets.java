package com.agentdung.game.assets;

import com.agentdung.game.entities.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

public class GameAssets {
    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture serverTexture;
    private Music backgroundMusic;
    private Sound clickSound, pickWaterSound;
    private Sound spitSound, poopSound, peeSound, vomitSound;

    // --- THÊM TÀI NGUYÊN CHO MAP ---
    private Texture keyTexture;
    private Texture btnUsePromptTex;
    private Texture vendingMachineTexture;
    private final Map<Item.ItemType, Texture> itemTextures = new HashMap<>();

    public void loadPlayerTexture(int characterId, int variantId) {
        if (playerTexture != null) playerTexture.dispose();
        String path = "images/player" + characterId + "_" + variantId + ".png";
        playerTexture = new Texture(Gdx.files.internal(path));
        playerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void loadEnemyTexture() {
        if (enemyTexture == null) {
            enemyTexture = new Texture(Gdx.files.internal("images/Patroler.png"));
            enemyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public void loadServerTexture() {
        if (serverTexture == null) {
            serverTexture = new Texture(Gdx.files.internal("images/server.png"));
            serverTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    /**
     * Nạp toàn bộ tài nguyên tĩnh dùng chung cho các Bản đồ
     */
    public void loadMapAssets() {
        if (keyTexture == null) keyTexture = new Texture("images/key.png");
        if (btnUsePromptTex == null) btnUsePromptTex = new Texture("ui/UI_button_use.png");

        if (vendingMachineTexture == null) {
            vendingMachineTexture = new Texture("images/vending_machine.png");
            vendingMachineTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        if (itemTextures.isEmpty()) {
            for (Item.ItemType type : Item.ItemType.values()) {
                Texture tex = new Texture(type.texturePath);
                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                itemTextures.put(type, tex);
            }
        }
    }

    public void loadSounds() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/theme_music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click_sound.ogg"));
        pickWaterSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pick_water.ogg"));

        spitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_spit.ogg"));
        poopSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_poop.ogg"));
        peeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_pee.ogg"));
        vomitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_vomit.ogg"));
    }

    public Texture getPlayerTexture() { return playerTexture; }
    public Texture getEnemyTexture() { return enemyTexture; }
    public Texture getServerTexture() { return serverTexture; }
    public Texture getKeyTexture() { return keyTexture; }
    public Texture getBtnUsePromptTex() { return btnUsePromptTex; }
    public Texture getVendingMachineTexture() { return vendingMachineTexture; }
    public Texture getItemTexture(Item.ItemType type) { return itemTextures.get(type); }
    public Music getBackgroundMusic() { return backgroundMusic; }
    public Sound getClickSound() { return clickSound; }
    public Sound getPickWaterSound() { return pickWaterSound; }
    public Sound getSpitSound() { return spitSound; }
    public Sound getPoopSound() { return poopSound; }
    public Sound getPeeSound() { return peeSound; }
    public Sound getVomitSound() { return vomitSound; }

    public void dispose() {
        if (playerTexture != null) playerTexture.dispose();
        if (enemyTexture != null) enemyTexture.dispose();
        if (serverTexture != null) serverTexture.dispose();
        if (keyTexture != null) keyTexture.dispose();
        if (btnUsePromptTex != null) btnUsePromptTex.dispose();
        if (vendingMachineTexture != null) vendingMachineTexture.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (clickSound != null) clickSound.dispose();
        if (pickWaterSound != null) pickWaterSound.dispose();
        if (spitSound != null) spitSound.dispose();
        if (poopSound != null) poopSound.dispose();
        if (peeSound != null) peeSound.dispose();
        if (vomitSound != null) vomitSound.dispose();
        for (Texture tex : itemTextures.values()) {
            if (tex != null) tex.dispose();
        }
        itemTextures.clear();
    }
}
