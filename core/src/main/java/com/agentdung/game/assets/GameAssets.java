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
    private Texture pausedTitleTex, resumeBtnTex, restartBtnTex, mainMenuBtnTex;
    private Texture progressBgTex;
    private Texture progressTitleTex;
    private final Map<String, Texture> levelButtonTextures = new HashMap<>();
    private Texture settingsBgTex, settingsTitleTex;
    private Texture soundOnTex, soundOffTex, musicOnTex, musicOffTex;
    private Texture textMasterTex, textSfxTex, textMusicTex;
    private Texture vendingTitleTex, vendingBigFrameTex, vendingSmallFrameTex, btnBuyTex;
    private Texture wikiTitleTex;


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
        if (vendingTitleTex == null) vendingTitleTex = new Texture("ui/UI_title_vendingmachine.png");
        if (vendingBigFrameTex == null) vendingBigFrameTex = new Texture("ui/bigframe.png");
        if (vendingSmallFrameTex == null) vendingSmallFrameTex = new Texture("ui/smallframe.png");
        if (btnBuyTex == null) btnBuyTex = new Texture("ui/UI_button_buy.png");
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
        if (wikiTitleTex == null) wikiTitleTex = new Texture("ui/UI_title_wiki.png");
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
            manaTextures.put(SupitSkill.class, new Texture("ui/UI_mana_spit.png"));
            manaTextures.put(VomicSkill.class, new Texture("ui/UI_mana_vomit.png"));
            manaTextures.put(PiiSkill.class, new Texture("ui/UI_mana_pee.png"));
            manaTextures.put(PuupSkill.class, new Texture("ui/UI_mana_poop.png"));

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

    public void loadPauseAssets() {
        if (pausedTitleTex == null) pausedTitleTex = new Texture("ui/UI_title_paused.png");
        if (resumeBtnTex == null) resumeBtnTex = new Texture("ui/UI_button_resume_lite.png");
        if (restartBtnTex == null) restartBtnTex = new Texture("ui/UI_button_restart_lite.png");
        if (mainMenuBtnTex == null) mainMenuBtnTex = new Texture("ui/UI_button_mainmenu_lite.png");
    }
    public void loadProgressAssets(int worldId, int totalLevels, int completedLevels) {
        if (progressBgTex == null)
            progressBgTex = new Texture("ui/UI_frame_general.png");
        if (progressTitleTex == null) {
            progressTitleTex = new Texture("ui/UI_title_progress.png");
            progressTitleTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (backBtnTex == null)
            backBtnTex = new Texture("ui/UI_arrow_left.png");

        for (int i = 1; i <= totalLevels; i++) {
            String path = (i <= completedLevels)
                ? "ui/UI_level_" + i + "_done.png"
                : "ui/UI_level_" + i + "_notdone.png";
            if (!levelButtonTextures.containsKey(path)) {
                Texture tex = new Texture(Gdx.files.internal(path));
                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                levelButtonTextures.put(path, tex);
            }
        }
    }
    public void loadSettingsAssets() {
        if (settingsBgTex == null) {
            settingsBgTex = new Texture("ui/UI_frame_general.png");
            settingsBgTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (settingsTitleTex == null) {
            settingsTitleTex = new Texture("ui/UI_title_settings.png");
            settingsTitleTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (backBtnTex == null) {
            backBtnTex = new Texture("ui/UI_arrow_left.png");
            backBtnTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (soundOnTex == null) {
            soundOnTex = new Texture("ui/UI_soundon.png");
            soundOnTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (soundOffTex == null) {
            soundOffTex = new Texture("ui/UI_soundoff.png");
            soundOffTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (musicOnTex == null) {
            musicOnTex = new Texture("ui/UI_musicon.png");
            musicOnTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (musicOffTex == null) {
            musicOffTex = new Texture("ui/UI_musicoff.png");
            musicOffTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (textMasterTex == null) {
            textMasterTex = new Texture("ui/UI_text_master.png");
            textMasterTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (textSfxTex == null) {
            textSfxTex = new Texture("ui/UI_text_sfx.png");
            textSfxTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        if (textMusicTex == null) {
            textMusicTex = new Texture("ui/UI_text_music.png");
            textMusicTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
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
    public Texture getPausedTitleTex() { return pausedTitleTex; }
    public Texture getResumeBtnTex() {return resumeBtnTex;}
    public Texture getRestartBtnTex() {return restartBtnTex;}
    public Texture getMainMenuBtnTex() {return mainMenuBtnTex;}
    public Texture getProgressBgTex() { return progressBgTex; }
    public Texture getProgressTitleTex() { return progressTitleTex; }
    public Texture getLevelButtonTex(int levelNum, boolean done) {
        String path = done
            ? "ui/UI_level_" + levelNum + "_done.png"
            : "ui/UI_level_" + levelNum + "_notdone.png";
        return levelButtonTextures.get(path);
    }
    public Texture getSoundOnTex() { return soundOnTex; }
    public Texture getSoundOffTex() { return soundOffTex; }
    public Texture getMusicOnTex() { return musicOnTex; }
    public Texture getMusicOffTex() { return musicOffTex; }
    public Texture getTextMasterTex() { return textMasterTex; }
    public Texture getTextSfxTex() { return textSfxTex; }
    public Texture getTextMusicTex() { return textMusicTex; }
    public Texture getSettingsBgTex() { return settingsBgTex; }
    public Texture getSettingsTitleTex() { return settingsTitleTex; }
    public Texture getVendingTitleTex() { return vendingTitleTex; }
    public Texture getVendingBigFrameTex() { return vendingBigFrameTex; }
    public Texture getVendingSmallFrameTex() { return vendingSmallFrameTex; }
    public Texture getBtnBuyTex() { return btnBuyTex; }
    public Texture getWikiTitleTex() { return wikiTitleTex; }




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
        if (pausedTitleTex != null) pausedTitleTex.dispose();
        if (resumeBtnTex != null) resumeBtnTex.dispose();
        if (restartBtnTex != null) restartBtnTex.dispose();
        if (mainMenuBtnTex != null) mainMenuBtnTex.dispose();
        if (progressBgTex != null) progressBgTex.dispose();
        if (progressTitleTex != null) progressTitleTex.dispose();
        if (settingsBgTex != null) settingsBgTex.dispose();
        if (settingsTitleTex != null) settingsTitleTex.dispose();
        if (soundOnTex != null) soundOnTex.dispose();
        if (soundOffTex != null) soundOffTex.dispose();
        if (musicOnTex != null) musicOnTex.dispose();
        if (musicOffTex != null) musicOffTex.dispose();
        if (textMasterTex != null) textMasterTex.dispose();
        if (textSfxTex != null) textSfxTex.dispose();
        if (textMusicTex != null) textMusicTex.dispose();
        if (vendingTitleTex != null) vendingTitleTex.dispose();
        if (vendingBigFrameTex != null) vendingBigFrameTex.dispose();
        if (vendingSmallFrameTex != null) vendingSmallFrameTex.dispose();
        if (btnBuyTex != null) btnBuyTex.dispose();
        if (wikiTitleTex != null) wikiTitleTex.dispose();


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
        for (Texture tex : levelButtonTextures.values()) if (tex != null) tex.dispose();
        levelButtonTextures.clear();
    }
}
