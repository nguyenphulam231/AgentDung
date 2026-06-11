package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.StreamProjectile; // QUAY LẠI: Dùng lại StreamProjectile để ra hạt vuông li ti
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PuupSkill extends BaseSkill {

    // Giữ lại biến ảnh này để PlayScreen mượn vẽ bẫy mìn
    private TextureRegion poopRegion;

    public PuupSkill() {
        // poop, tốn 40 mana (dạng bột phát), màu nâu
        super("Ị", 40f, new Color(0.5f, 0.25f, 0, 1), SkillKind.POOP);

        // Nạp file ảnh bẫy mìn
        Texture poopTexture = new Texture(Gdx.files.internal("images/shit.png"));
        this.poopRegion = new TextureRegion(poopTexture);
    }

    // Để PlayScreen gọi lấy ảnh đi vẽ bẫy dưới sàn map
    public TextureRegion getPoopRegion() {
        return this.poopRegion;
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        // Góc 180 độ so với hướng nhìn để đảm bảo bắn ra từ phía sau
        float backAngle = player.getAngle() + 180;

        // BẮN RA HẠT VUÔNG NHỎ màu nâu li ti
        projectiles.add(new StreamProjectile(
            player.getPosition().x + player.getSize()/2,
            player.getPosition().y + player.getSize()/2,
            backAngle,
            150,
            this.color // Sử dụng màu nâu (new Color(0.5f, 0.25f, 0, 1))
        ));
    }

    // Giải phóng tài nguyên ảnh khi đóng màn chơi
    public void dispose() {
        if (poopRegion != null) {
            poopRegion.getTexture().dispose();
        }
    }
}
