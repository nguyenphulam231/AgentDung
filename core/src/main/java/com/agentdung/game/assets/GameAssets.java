package com.agentdung.game.assets;

import com.agentdung.game.entities.Item;
import com.agentdung.game.screens.CustomizeScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

public class GameAssets {
    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture serverTexture;
    private Music backgroundMusic;
    private Sound clickSound, pickWaterSound;
    private Sound spitSound, poopSound, peeSound, vomitSound;
    private Texture menuBg, btnPlay, btnCustomize, btnTitle, btnGuide, btnSettings, btnWiki, coinTex;
    private Texture customizeBg, slotBg, backBtn, titleCustomize;
    private Map<String, Texture> thumbnails = new HashMap<>();
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

    public void loadMenuAssets() {
        menuBg = new Texture("ui/UI_background.png");
        btnPlay = new Texture("ui/UI_button_play.png");
        btnCustomize = new Texture("ui/UI_button_customize.png");
        btnTitle = new Texture("ui/UI_title_agentdung.png");
        btnGuide = new Texture("ui/UI_button_guide.png");
        btnSettings = new Texture("ui/UI_button_settings.png");
        btnWiki = new Texture("ui/UI_button_wiki.png");
        coinTex = new Texture("images/coin.png");
    }

    /**
     * Overload không tham số — tự khởi tạo danh sách nhân vật.
     * Gọi từ AgentDungGame để load trước khi vào màn hình Customize.
     */
    public void loadCustomizeAssets() {
        Array<CustomizeScreen.CharacterData> characterList = new Array<>();
        characterList.add(new CustomizeScreen.CharacterData("Agent Dung", 1,
            new String[]{"Classic", "Snow", "Desert", "Stealth", "VIP"}));
        characterList.add(new CustomizeScreen.CharacterData("Shadow Agent", 2,
            new String[]{"Ninja", "Assassin", "Ghost", "Midnight", "Eclipse"}));
        characterList.add(new CustomizeScreen.CharacterData("Cyber Soldier", 3,
            new String[]{"Matrix", "Neon", "Mecha", "Quantum", "Overdrive"}));
        characterList.add(new CustomizeScreen.CharacterData("Ghost Fighter", 4,
            new String[]{"Phantom", "Specter", "Wraith", "Haunted", "Spirit"}));
        characterList.add(new CustomizeScreen.CharacterData("Medic Agent", 5,
            new String[]{"Doctor", "Biohazard", "Vaccine", "Nanotech", "FirstAid"}));
        characterList.add(new CustomizeScreen.CharacterData("Heavy Gunner", 6,
            new String[]{"Commando", "Juggernaut", "Doomsday", "Wasteland", "Vulcan"}));
        loadCustomizeAssets(characterList);
    }

    /**
     * Nạp toàn bộ tài nguyên cho màn hình Customize.
     * Truyền vào danh sách nhân vật để biết cần load thumbnail nào.
     *
     * @param characterList danh sách CharacterData từ CustomizeScreen
     */
    public void loadCustomizeAssets(Array<CustomizeScreen.CharacterData> characterList) {
        if (customizeBg == null) {
            customizeBg = new Texture("ui/UI_frame_general.png");
        }
        if (slotBg == null) {
            slotBg = new Texture("ui/UI_slot_background.png");
            slotBg.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (backBtn == null) {
            backBtn = new Texture("ui/UI_arrow_left.png");
            backBtn.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (titleCustomize == null) {
            titleCustomize = new Texture("ui/UI_title_customize.png");
            titleCustomize.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        // Load thumbnails cho tất cả nhân vật và variant
        for (CustomizeScreen.CharacterData character : characterList) {
            for (int i = 0; i < character.variants.length; i++) {
                int variantId = i + 1;
                String key = "thumbnails/thumbnail_player_" + character.id + "_" + variantId + ".png";
                if (!thumbnails.containsKey(key)) {
                    if (Gdx.files.internal(key).exists()) {
                        Texture tex = new Texture(Gdx.files.internal(key));
                        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                        thumbnails.put(key, tex);
                    } else {
                        Gdx.app.error("GameAssets", "Không tìm thấy thumbnail: " + key);
                        thumbnails.put(key, new Texture(Gdx.files.internal("thumbnails/placeholder.png")));
                    }
                }
            }
        }
    }

    public Texture getThumbnail(int charId, int variantId) {
        String key = "thumbnails/thumbnail_player_" + charId + "_" + variantId + ".png";
        Texture tex = thumbnails.get(key);
        if (tex == null) {
            Gdx.app.error("GameAssets", "Thumbnail chưa được load: " + key + ". Gọi loadCustomizeAssets() trước.");
            return new Texture(Gdx.files.internal("thumbnails/placeholder.png"));
        }
        return tex;
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
    public Texture getBtnCustomize() { return btnCustomize; }
    public Texture getBtnTitle() { return btnTitle; }
    public Texture getBtnGuide() { return btnGuide; }
    public Texture getBtnSettings() { return btnSettings; }
    public Texture getBtnWiki() { return btnWiki; }
    public Texture getCoinTex() { return coinTex; }
    public Texture getMenuBg() { return menuBg; }
    public Texture getBtnPlay() { return btnPlay; }
    public Texture getCustomizeBg() { return customizeBg; }
    public Texture getSlotBg() { return slotBg; }
    public Texture getBackBtn() { return backBtn; }
    public Texture getTitleCustomize() { return titleCustomize; }


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
        if (menuBg != null) menuBg.dispose();
        if (btnPlay != null) btnPlay.dispose();
        if (btnCustomize != null) btnCustomize.dispose();
        if (btnTitle != null) btnTitle.dispose();
        if (btnGuide != null) btnGuide.dispose();
        if (btnSettings != null) btnSettings.dispose();
        if (btnWiki != null) btnWiki.dispose();
        if (coinTex != null) coinTex.dispose();
        if (customizeBg != null) customizeBg.dispose();
        if (slotBg != null) slotBg.dispose();
        if (backBtn != null) backBtn.dispose();
        if (titleCustomize != null) titleCustomize.dispose();

        for (Texture tex : itemTextures.values()) {
            if (tex != null) tex.dispose();
        }
        itemTextures.clear();
        for (Texture tex : thumbnails.values()) tex.dispose();
        thumbnails.clear();
    }
}
