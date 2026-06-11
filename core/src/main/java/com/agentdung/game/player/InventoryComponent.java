package com.agentdung.game.player;

import java.util.ArrayList;
import java.util.List;

public class InventoryComponent {
    // Tạm thời dùng String đại diện cho tên/ID vật phẩm.
    // Sau này bạn có thể đổi String thành class Item của riêng bạn.
    private List<String> items;
    private int capacity;

    public InventoryComponent(int capacity) {
        this.items = new ArrayList<>();
        this.capacity = capacity;
    }

    public boolean addItem(String item) {
        if (items.size() < capacity) {
            items.add(item);
            return true;
        }
        return false; // Túi đồ đầy
    }

    public boolean removeItem(String item) {
        return items.remove(item);
    }

    public boolean hasItem(String item) {
        return items.contains(item);
    }

    public List<String> getItems() {
        return items;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void clearInventory() {
        items.clear();
    }
}
