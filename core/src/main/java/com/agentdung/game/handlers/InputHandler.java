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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class InputHandler {

    // --- BIẾN KIỂM SOÁT TRẠNG THÁI ÂM THANH KHI GIỮ PHÍM ---
    private static long peeSoundId = -1;
    private static boolean vomitSoundPlayed = false;

    public static void handleTankMovement(float delta, Player dung, OrthographicCamera camera, MapManager mapManager) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        float cx = dung.getPosition().x + dung.getSize() / 2f;
        float cy = dung.getPosition().y + dung.getSize() / 2f;
        dung.setAngle(MathUtils.atan2(mousePos.y - cy, mousePos.x - cx) * MathUtils.radiansToDegrees);

        float moveSpeed = 140f;
        dung.getVelocity().set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dung.getVelocity().x =  MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            dung.getVelocity().y =  MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dung.getVelocity().x = -MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            dung.getVelocity().y = -MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }

        float oldX = dung.getPosition().x;
        dung.getPosition().x += dung.getVelocity().x * delta;
        Rectangle dungRectX = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        for (com.agentdung.game.entities.Wall w : mapManager.walls) if (Intersector.overlaps(dungRectX, w.bounds)) { dung.getPosition().x = oldX; break; }
        for (com.agentdung.game.entities.Door d : mapManager.doors) if (!d.isOpen && Intersector.overlaps(dungRectX, d.bounds)) { dung.getPosition().x = oldX; break; }

        float oldY = dung.getPosition().y;
        dung.getPosition().y += dung.getVelocity().y * delta;
        Rectangle dungRectY = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());

        for (com.agentdung.game.entities.Wall w : mapManager.walls) if (Intersector.overlaps(dungRectY, w.bounds)) { dung.getPosition().y = oldY; break; }
        for (com.agentdung.game.entities.Door d : mapManager.doors) if (!d.isOpen && Intersector.overlaps(dungRectY, d.bounds)) { dung.getPosition().y = oldY; break; }
    }

    public static void handleSkillInput(Player dung, Array<Skill> skills, EntityManager entityManager, AgentDungGame game) {
        Enemy target = null;
        float minDistance = 180f;
        for (Enemy e : entityManager.enemies) {
            float d = com.badlogic.gdx.math.Vector2.dst(dung.getPosition().x, dung.getPosition().y, e.getPosition().x, e.getPosition().y);
            if (d < minDistance) { minDistance = d; target = e; }
        }

        // --- 1. CHIÊU SPIT (Khạc - Ấn phát một) ---
        // Đổi từ Input.Keys.NUM_1 sang Input.Keys.Q
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (skills.get(0).activate(dung, target, entityManager.projectiles)) {
                // ĐÃ ĐẤU NỐI VẬT LÝ: Kiểm tra cài đặt SFX hệ thống trước khi phát âm thanh
                if (game.isMasterOn && game.isSfxOn && game.spitSound != null) {
                    game.spitSound.play();
                }
            }
        }

        // --- 2. CHIÊU POOP (Ị - Ấn phát một) ---
        // Đổi từ Input.Keys.NUM_2 sang Input.Keys.E
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (skills.get(1).activate(dung, target, entityManager.projectiles)) {
                entityManager.poopTraps.add(new Rectangle(dung.getPosition().x + 5, dung.getPosition().y + 5, 16, 16));
                // ĐÃ ĐẤU NỐI VẬT LÝ: Kiểm tra cài đặt SFX hệ thống trước khi phát âm thanh
                if (game.isMasterOn && game.isSfxOn && game.poopSound != null) {
                    game.poopSound.play();
                }
            }
        }

        // --- 3. CHIÊU PEE (Đái - Giữ nút xả liên tục & lặp âm thanh) ---
        // Đổi từ Input.Keys.NUM_3 sang Input.Keys.C
        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            if (skills.get(2).activate(dung, target, entityManager.projectiles)) {
                // Nếu chiêu kích hoạt thành công (còn mana) và chưa tạo vòng lặp âm thanh
                if (peeSoundId == -1 && game.peeSound != null) {
                    // ĐÃ ĐẤU NỐI VẬT LÝ: Chỉ thực sự kích hoạt loop âm thanh nếu SFX được bật
                    if (game.isMasterOn && game.isSfxOn) {
                        peeSoundId = game.peeSound.play();
                        game.peeSound.setLooping(peeSoundId, true);
                    }
                }
            } else {
                // Hết mana xả thì phải dừng tiếng đái lặp lại ngay lập tức
                if (peeSoundId != -1 && game.peeSound != null) {
                    game.peeSound.stop(peeSoundId);
                    peeSoundId = -1;
                }
            }
        } else {
            // Khi nhả phím C, dừng âm thanh đang lặp lại
            if (peeSoundId != -1 && game.peeSound != null) {
                game.peeSound.stop(peeSoundId);
                peeSoundId = -1;
            }
        }

        // --- 4. CHIÊU VOMIT (Nôn - Giữ nút xả liên tục nhưng chỉ kêu frame đầu tiên) ---
        // Đổi từ Input.Keys.NUM_4 sang Input.Keys.V
        if (Gdx.input.isKeyPressed(Input.Keys.V)) {
            if (skills.get(3).activate(dung, target, entityManager.projectiles)) {
                // Nếu chiêu kích hoạt thành công và chưa phát âm thanh đoạn đầu
                if (!vomitSoundPlayed && game.vomitSound != null) {
                    // ĐÃ ĐẤU NỐI VẬT LÝ: Chỉ phát âm thanh frame đầu nếu SFX được cho phép
                    if (game.isMasterOn && game.isSfxOn) {
                        game.vomitSound.play();
                    }
                    vomitSoundPlayed = true; // Khóa lại để các frame giữ sau không phát nữa (bất kể cấu hình âm thanh)
                }
            } else {
                // Hết mana không nôn được nữa thì reset trạng thái âm thanh
                vomitSoundPlayed = false;
            }
        } else {
            // Khi nhả phím V, reset lại cờ hiệu để lần bấm sau lại kêu tiếp đoạn đầu
            vomitSoundPlayed = false;
        }
    }

    // Hàm bổ trợ để PlayScreen hoặc SettingsScreen có thể chủ động tắt tiếng loop từ bên ngoài
    public static void stopLoopingSounds(AgentDungGame game) {
        if (peeSoundId != -1 && game.peeSound != null) {
            game.peeSound.stop(peeSoundId);
            peeSoundId = -1;
        }
        vomitSoundPlayed = false;
    }
}
