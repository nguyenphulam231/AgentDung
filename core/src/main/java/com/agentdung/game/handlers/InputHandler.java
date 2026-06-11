package com.agentdung.game.handlers;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.managers.MapManager;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class InputHandler {

    // --- BIẾN KIỂM SOÁT TRẠNG THÁI ÂM THANH KHI GIỮ PHÍM ---
    private static long peeSoundId = -1;
    private static boolean vomitSoundPlayed = false;

    /**
     * CHUẨN OOP - TÍNH BAO ĐÓNG & KHẮC PHỤC TỐC ĐỘ:
     * ĐÃ SỬA: Tính toán hướng di chuyển và nhân thẳng giá trị moveSpeed (140f) vào trục X, Y.
     * Giúp đưa độ lớn vận tốc của Player về đúng quỹ đạo tính toán vật lý giống như Enemy.
     */
    public static void handleTankMovement(float delta, Player dung, OrthographicCamera camera, MapManager mapManager) {
        // 1. Tính toán góc quay của nhân vật hướng theo con trỏ chuột
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        float cx = dung.getPosition().x + dung.getSize() / 2f;
        float cy = dung.getPosition().y + dung.getSize() / 2f;
        dung.setAngle(MathUtils.atan2(mousePos.y - cy, mousePos.x - cx) * MathUtils.radiansToDegrees);

        // 2. Tính toán vận tốc dựa trên phím bấm W / S kết hợp góc quay xe tăng
        float moveSpeed = 140f; // Tốc độ gốc của nhân vật
        dung.getVelocity().set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dung.getVelocity().x =  MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            dung.getVelocity().y =  MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dung.getVelocity().x = -MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            dung.getVelocity().y = -MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }
    }

    /**
     * CHUẨN OOP - ĐA HÌNH & KẾ THỪA:
     * Quản lý kích hoạt kỹ năng dựa trên các lớp con của Skill.
     */
    public static void handleSkillInput(Player dung, Array<Skill> skills, EntityManager entityManager, AgentDungGame game) {
        // Tối ưu tìm mục tiêu gần nhất
        Enemy target = null;
        float minDistance = 180f;
        for (Enemy e : entityManager.enemies) {
            float d = Vector2.dst(dung.getPosition().x, dung.getPosition().y, e.getPosition().x, e.getPosition().y);
            if (d < minDistance) {
                minDistance = d;
                target = e;
            }
        }

        // --- 1. CHIÊU SPIT (Hydro-shot - Phím Q) ---
        if (skills.size > 0 && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (skills.get(0).activate(dung, target, entityManager.projectiles)) {
                if (game.isMasterOn && game.isSfxOn && game.spitSound != null) {
                    game.spitSound.play();
                }
            }
        }

        // --- 2. CHIÊU POOP (Bio-bomb - Phím E) ---
        if (skills.size > 1 && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (skills.get(1).activate(dung, target, entityManager.projectiles)) {
                entityManager.poopTraps.add(new Rectangle(dung.getPosition().x + 5, dung.getPosition().y + 5, 16, 16));
                if (game.isMasterOn && game.isSfxOn && game.poopSound != null) {
                    game.poopSound.play();
                }
            }
        }

        // --- 3. CHIÊU PEE (Golden-stream - Phím C) ---
        if (skills.size > 2 && Gdx.input.isKeyPressed(Input.Keys.C)) {
            if (skills.get(2).activate(dung, target, entityManager.projectiles)) {
                if (peeSoundId == -1 && game.peeSound != null) {
                    if (game.isMasterOn && game.isSfxOn) {
                        peeSoundId = game.peeSound.play();
                        game.peeSound.setLooping(peeSoundId, true);
                    }
                }
            } else {
                stopPeeSound(game);
            }
        } else {
            stopPeeSound(game);
        }

        // --- 4. CHIÊU VOMIT (Rainbow-blast - Phím V) ---
        if (skills.size > 3 && Gdx.input.isKeyPressed(Input.Keys.V)) {
            if (skills.get(3).activate(dung, target, entityManager.projectiles)) {
                if (!vomitSoundPlayed && game.vomitSound != null) {
                    if (game.isMasterOn && game.isSfxOn) {
                        game.vomitSound.play();
                    }
                    vomitSoundPlayed = true;
                }
            } else {
                vomitSoundPlayed = false;
            }
        } else {
            vomitSoundPlayed = false;
        }
    }

    private static void stopPeeSound(AgentDungGame game) {
        if (peeSoundId != -1 && game.peeSound != null) {
            game.peeSound.stop(peeSoundId);
            peeSoundId = -1;
        }
    }

    public static void stopLoopingSounds(AgentDungGame game) {
        stopPeeSound(game);
        vomitSoundPlayed = false;
    }
}
