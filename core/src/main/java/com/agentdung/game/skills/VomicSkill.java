package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.SpriteProjectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class VomicSkill extends BaseSkill {

    private TextureRegion[] vomitTypes;
    private final int TOTAL_TYPES = 7;

    public VomicSkill() {
        super("VM", 0.8f, Color.WHITE, SkillKind.VOMIT);

        vomitTypes = new TextureRegion[TOTAL_TYPES];

        for (int i = 0; i < TOTAL_TYPES; i++) {
            Texture tex = new Texture(Gdx.files.internal("images/vomit_type" + (i + 1) + ".png"));
            vomitTypes[i] = new TextureRegion(tex);
        }
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        float spawnX = player.getPosition().x + player.getSize() / 2;
        float spawnY = player.getPosition().y + player.getSize() / 2;

        int burstCount = 8;

        for (int i = 0; i < burstCount; i++) {
            int randomIndex = MathUtils.random(0, TOTAL_TYPES - 1);
            TextureRegion selectedRegion = vomitTypes[randomIndex];

            float randomAngle = player.getAngle() + MathUtils.random(-18f, 18f);
            float randomSpeed = MathUtils.random(250f, 350f);

            // Truyền thêm '4f, 4f' vào cuối để ép kích thước hạt vomit thành 4x4 pixel
            projectiles.add(new SpriteProjectile(
                spawnX,
                spawnY,
                randomAngle,
                randomSpeed,
                selectedRegion,
                4f, // Chiều rộng tùy chỉnh (Width)
                4f  // Chiều cao tùy chỉnh (Height)
            ));
        }
    }

    public void dispose() {
        if (vomitTypes != null) {
            for (TextureRegion region : vomitTypes) {
                if (region != null && region.getTexture() != null) {
                    region.getTexture().dispose();
                }
            }
        }
    }
}
