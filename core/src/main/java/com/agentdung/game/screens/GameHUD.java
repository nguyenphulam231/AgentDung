package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.skills.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

public class GameHUD {
    private final AgentDungGame game;
    private final Map<Class<? extends Skill>, Texture> manaTextures;

    public GameHUD(AgentDungGame game) {
        this.game = game;
        this.manaTextures = new HashMap<>();
    }

    public void loadTextures() {
        clearTextures();
        manaTextures.put(SpitSkill.class, new Texture("ui/UI_mana_spit.png"));
        manaTextures.put(VomitSkill.class, new Texture("ui/UI_mana_vomit.png"));
        manaTextures.put(PeeSkill.class, new Texture("ui/UI_mana_pee.png"));
        manaTextures.put(PoopSkill.class, new Texture("ui/UI_mana_poop.png"));
    }

    public void render(Array<Skill> skills, Matrix4 hudMatrix) {
        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

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
        game.batch.end();
    }

    public void clearTextures() {
        for (Texture tex : manaTextures.values()) {
            if (tex != null) tex.dispose();
        }
        manaTextures.clear();
    }

    public void dispose() {
        clearTextures();
    }
}
