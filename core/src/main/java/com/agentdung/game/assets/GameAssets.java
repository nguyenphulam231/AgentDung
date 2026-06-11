package com.agentdung.game.assets;

import com.agentdung.game.entities.Item;
import com.agentdung.game.screens.CustomizeScreen;
import com.agentdung.game.skills.*;
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
    private Texture titleCapturedTex, btnRestartTex, btnProgressMenuTex, btnMainMenuTex;
    private Sound capturedSound;
    private Texture keyTexture;
    private Texture btnUsePromptTex;
    private Texture vendingMachineTexture;
    private final Map<Item.ItemType, Texture> itemTextures = new HashMap<>();
    private Map<Class<? extends Skill>, Texture> manaTextures = new HashMap<>();
    private Texture btnPauseTex, btnBagTex;
    private Texture guideFrameTex, backBtnTex;
    private Texture invTitleTex, invBigFrameTex, invSmallFrameTex, invBtnUseTex;
    private Texture levelDoneBgTex, titleLevelDoneTex, btnNextTex, btnProgressTex, btnMainTex;
    private Texture missionBgTex, mapTagTex, progressTex, perTex, titleMissionTex;
    private Texture[] mapTitleTexs = new Texture[5];
    private Texture[] numberTexs = new Texture[10];

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

    public void loadGuideAssets() {
        guideFrameTex = new Texture("ui/UI_frame_guide.png");
        // Nếu bạn đã có backBtn trong hàm loadCustomizeAssets,
        // hãy đảm bảo dùng chung biến đó hoặc nạp tại đây
        if (backBtnTex == null) backBtnTex = new Texture("ui/UI_arrow_left.png");
    }


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

    public void loadCapturedAssets() {
        titleCapturedTex = new Texture("ui/UI_title_captured.png");
        btnRestartTex = new Texture("ui/UI_button_restart_red.png");
        btnProgressMenuTex = new Texture("ui/UI_button_progressmenu_red.png");
        btnMainMenuTex = new Texture("ui/UI_button_mainmenu_red.png");
        capturedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/captured.ogg"));

    }

    public void loadHUDAssets() {
        // Chỉ nạp nếu chưa nạp (kiểm tra null để tránh load trùng)
        if (btnPauseTex == null) {
            manaTextures.put(SpitSkill.class, new Texture("ui/UI_mana_spit.png"));
            manaTextures.put(VomitSkill.class, new Texture("ui/UI_mana_vomit.png"));
            manaTextures.put(PeeSkill.class, new Texture("ui/UI_mana_pee.png"));
            manaTextures.put(PoopSkill.class, new Texture("ui/UI_mana_poop.png"));

            btnPauseTex = new Texture("ui/UI_button_pause.png");
            btnBagTex = new Texture("ui/UI_button_bag.png");
            coinTex = new Texture("images/coin.png");
        }
    }

    public void loadInventoryAssets() {
        invTitleTex = new Texture("UI/UI_title_inventory.png");
        invBigFrameTex = new Texture("UI/bigframe.png");
        invSmallFrameTex = new Texture("UI/smallframe.png");
        invBtnUseTex = new Texture("UI/UI_button_use.png");
    }

    public void loadLevelDoneAssets() {
        levelDoneBgTex = new Texture("ui/UI_frame_general.png");
        titleLevelDoneTex = new Texture("ui/UI_title_leveldone.png");
        btnNextTex = new Texture("ui/UI_button_nextlevel.png");
        btnProgressTex = new Texture("ui/UI_button_progressmenu.png");
        btnMainTex = new Texture("ui/UI_button_mainmenu.png");

        // Apply Filter cho Pixel Art
        levelDoneBgTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        titleLevelDoneTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnNextTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnProgressTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnMainTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void loadMissionsAssets() {
        missionBgTex = new Texture("ui/UI_background.png");
        backBtnTex = new Texture("ui/UI_arrow_left.png");
        mapTagTex = new Texture("ui/UI_maptag.png");
        progressTex = new Texture("ui/UI_progress.png");
        perTex = new Texture("ui/UI_per.png");
        titleMissionTex = new Texture("ui/UI_title_mission.png");

        String[] mapNames = {"UI_Ronin", "UI_Nostra", "UI_Sinal", "UI_Gumi", "UI_Cartel"};
        for (int i = 0; i < 5; i++) {
            mapTitleTexs[i] = new Texture("ui/" + mapNames[i] + ".png");
        }
        for (int i = 0; i < 10; i++) {
            numberTexs[i] = new Texture("ui/UI_number" + i + ".png");
        }
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
    public Texture getTitleCapturedTex() { return titleCapturedTex; }
    public Texture getBtnRestartTex() { return btnRestartTex; }
    public Texture getBtnProgressMenuTex() { return btnProgressMenuTex; }
    public Texture getBtnMainMenuTex() { return btnMainMenuTex; }
    public Sound getCapturedSound() { return capturedSound; }
    public Texture getManaTexture(Class<? extends Skill> skillClass) { return manaTextures.get(skillClass); }
    public Texture getBtnPauseTex() { return btnPauseTex; }
    public Texture getBtnBagTex() { return btnBagTex; }
    public Texture getGuideFrameTex() { return guideFrameTex; }
    public Texture getBackBtnTex() { return backBtnTex; }
    public Texture getInvTitleTex() { return invTitleTex; }
    public Texture getInvBigFrameTex() { return invBigFrameTex; }
    public Texture getInvSmallFrameTex() { return invSmallFrameTex; }
    public Texture getInvBtnUseTex() { return invBtnUseTex; }
    public Texture getLevelDoneBgTex() { return levelDoneBgTex; }
    public Texture getTitleLevelDoneTex() { return titleLevelDoneTex; }
    public Texture getBtnNextTex() { return btnNextTex; }
    public Texture getBtnProgressTex() { return btnProgressTex; }
    public Texture getBtnMainTex() { return btnMainTex; }
    public Texture getMissionBgTex() { return missionBgTex; }
    public Texture getMapTagTex() { return mapTagTex; }
    public Texture getProgressTex() { return progressTex; }
    public Texture getPerTex() { return perTex; }
    public Texture getTitleMissionTex() { return titleMissionTex; }
    public Texture getMapTitleTex(int index) { return mapTitleTexs[index]; }
    public Texture getNumberTex(int index) {
        if (index >= 0 && index < numberTexs.length) {
            return numberTexs[index];
        }
        return null; // Hoặc trả về một texture mặc định nếu cần
    }


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
        if (titleCapturedTex != null) titleCapturedTex.dispose();
        if (btnRestartTex != null) btnRestartTex.dispose();
        if (btnProgressMenuTex != null) btnProgressMenuTex.dispose();
        if (btnMainMenuTex != null) btnMainMenuTex.dispose();
        if (capturedSound != null) capturedSound.dispose();
        if (btnPauseTex != null) btnPauseTex.dispose();
        if (btnBagTex != null) btnBagTex.dispose();
        if (coinTex != null) coinTex.dispose();
        if (guideFrameTex != null) guideFrameTex.dispose();
        if (invTitleTex != null) invTitleTex.dispose();
        if (invBigFrameTex != null) invBigFrameTex.dispose();
        if (invSmallFrameTex != null) invSmallFrameTex.dispose();
        if (invBtnUseTex != null) invBtnUseTex.dispose();
        if (levelDoneBgTex != null) levelDoneBgTex.dispose();
        if (titleLevelDoneTex != null) titleLevelDoneTex.dispose();
        if (btnNextTex != null) btnNextTex.dispose();
        if (btnProgressTex != null) btnProgressTex.dispose();
        if (btnMainTex != null) btnMainTex.dispose();
        if (missionBgTex != null) missionBgTex.dispose();
        if (backBtnTex != null) backBtnTex.dispose();
        if (mapTagTex != null) mapTagTex.dispose();
        if (progressTex != null) progressTex.dispose();
        if (perTex != null) perTex.dispose();
        if (titleMissionTex != null) titleMissionTex.dispose();


        for (Texture tex : itemTextures.values()) {
            if (tex != null) tex.dispose();
        }
        itemTextures.clear();
        for (Texture tex : thumbnails.values()) tex.dispose();
        thumbnails.clear();
        for (Texture tex : manaTextures.values()) tex.dispose();
        for (Texture t : mapTitleTexs) if (t != null) t.dispose();
        for (Texture t : numberTexs) {
            if (t != null) t.dispose();
        }
    }
}
