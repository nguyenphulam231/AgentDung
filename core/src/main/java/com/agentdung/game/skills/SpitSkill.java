package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.SpriteProjectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class SpitSkill extends BaseSkill {

    private TextureRegion spitRegion;

    public SpitSkill() {
        // Kh???c (Hydro-shot) t???n 15 mana, m??u cyan, v?? C?? t??? ?????ng h???i (true)
        // D??ng Unicode escape cho "Kh???c"
        super("Kh\u1EA1c", 15f, Color.CYAN, true);

        Texture spitTexture = new Texture(Gdx.files.internal("images/spit.png"));
        this.spitRegion = new TextureRegion(spitTexture);
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        float spawnX = player.getPosition().x + player.getSize() / 2;
        float spawnY = player.getPosition().y + player.getSize() / 2;

        projectiles.add(new SpriteProjectile(
            spawnX,
            spawnY,
            player.getAngle(),
            400,
            spitRegion,
            Color.CYAN
        ));
    }

    public void dispose() {
        if (spitRegion != null) {
            spitRegion.getTexture().dispose();
        }
    }
}
