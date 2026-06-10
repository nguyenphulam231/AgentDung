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

    // ĐÃ THÊM: Biến lưu trữ hình ảnh của bãi nước bọt khạc ra
    private TextureRegion spitRegion;

    public SpitSkill() {
        // Gọi constructor BaseSkill: spit, tốn 15 mana, màu xanh lơ (Cyan)
        super("Khạc", 15f, Color.CYAN);

        //  Nạp file ảnh spit.png
        Texture spitTexture = new Texture(Gdx.files.internal("images/spit.png"));
        this.spitRegion = new TextureRegion(spitTexture);
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        // Tính toán vị trí xuất phát từ tâm của Player
        float spawnX = player.getPosition().x + player.getSize() / 2;
        float spawnY = player.getPosition().y + player.getSize() / 2;

        // Thay vì new BlobProjectile, ta tạo SpriteProjectile với hình ảnh nước bọt
        projectiles.add(new SpriteProjectile(
            spawnX,
            spawnY,
            player.getAngle(),
            400,
            spitRegion,
            Color.CYAN
        ));
    }

    // Giải phóng tài nguyên ảnh khi đóng màn chơi để tránh tràn RAM
    public void dispose() {
        if (spitRegion != null) {
            spitRegion.getTexture().dispose();
        }
    }
}
