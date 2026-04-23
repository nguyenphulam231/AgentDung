package com.agentdung.game.core;

import com.badlogic.gdx.math.Vector2;

public class LevelProvider {
    // Bây giờ LevelProvider chỉ đóng vai trò cung cấp thông tin cơ bản về Level
    // Việc nạp các thực thể (Wall, Enemy) sẽ do PlayScreen xử lý trực tiếp từ file TMX

    public static final int TILE_SIZE = 16; // Cập nhật khớp với thiết kế 16x16 của bạn

    /**
     * Trả về đường dẫn file map dựa trên world và level
     */
    public static String getMapPath(int world, int level) {
        // Trả về maps/map1_1.tmx, maps/map1_2.tmx...
        return "maps/map" + world + "_" + level + ".tmx";
    }

    /**
     * Bạn có thể thêm các thông số đặc biệt cho từng level ở đây nếu cần
     * Ví dụ: Thời gian giới hạn, số lượng lính tối đa, hoặc tên nhiệm vụ
     */
    public static String getLevelName(int world, int level) {
        if (world == 1) {
            switch (level) {
                case 1: return "Trụ sở 1- Tầng 1";
                case 2: return "Khu vực Lưu trữ Dữ liệu";
                default: return "Nhiệm vụ bí mật";
            }
        }
        return "Unknown Location";
    }
}
