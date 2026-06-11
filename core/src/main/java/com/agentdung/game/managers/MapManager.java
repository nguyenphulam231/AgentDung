package com.agentdung.game.managers;

import com.agentdung.game.entities.Door;
import com.agentdung.game.entities.Item;
import com.agentdung.game.entities.Server;
import com.agentdung.game.entities.Wall;
import com.agentdung.game.entities.VendingMachine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

public class MapManager {
    public TiledMap map;
    public OrthogonalTiledMapRenderer mapRenderer;
    public Array<Wall> walls = new Array<>();
    public Array<Rectangle> wallRects = new Array<>();
    public Array<Door> doors = new Array<>();
    public Array<Rectangle> keys = new Array<>();
    public Array<Item> items = new Array<>();

    // --- Mảng quản lý máy bán hàng trên Map ---
    public Array<VendingMachine> vendingMachines = new Array<>();

    public Server targetServer;
    public Texture keyTexture;

    // --- Texture hiển thị cho Máy bán hàng tự động ---
    public Texture vendingMachineTexture;

    // Nút tương tác nhanh hiện lên khi đứng gần Vending Machine
    public Texture btnUsePromptTex;

    private final Map<Item.ItemType, Texture> itemTextures = new HashMap<>();

    public float mapWidth, mapHeight;
    public Vector2 playerSpawn = new Vector2();
    public Map<Integer, Vector2> enemyStarts = new HashMap<>();
    public Map<Integer, Vector2> enemyEnds = new HashMap<>();

    public void loadLevel(int world, int level) {
        dispose();
        String mapPath = "maps/map" + world + "_" + level + ".tmx";
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        int tileWidth  = map.getProperties().get("tilewidth", Integer.class);
        mapWidth  = map.getProperties().get("width", Integer.class) * tileWidth;
        mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("height", Integer.class);

        keyTexture = new Texture("images/key.png");
        btnUsePromptTex = new Texture("ui/UI_button_use.png");

        // --- Nạp texture cho máy bán hàng tự động  ---
        vendingMachineTexture = new Texture("images/vending_machine.png");
        vendingMachineTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Nạp tự động toàn bộ Texture ảnh từ thư mục assets/images/ theo danh sách Enum
        for (Item.ItemType type : Item.ItemType.values()) {
            Texture tex = new Texture(type.texturePath);
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            // Ánh xạ gián tiếp để đảm bảo tính an toàn dữ liệu
            itemTextures.put(type, tex);
        }

        // Đọc va chạm tường
        MapObjects wallObjects = map.getLayers().get("collisions").getObjects();
        for (MapObject obj : wallObjects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            walls.add(new Wall(rect.x, rect.y, rect.width, rect.height));
            wallRects.add(rect);
        }

        // Đọc các thực thể tĩnh từ layer entities
        MapObjects entityObjects = map.getLayers().get("entities").getObjects();
        for (MapObject obj : entityObjects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            String name = obj.getName();

            if ("player_spawn".equals(name)) {
                playerSpawn.set(rect.x, rect.y);
            } else if ("server".equals(name)) {
                targetServer = new Server(rect.x, rect.y);
            } else if ("key".equals(name)) {
                keys.add(new Rectangle(rect.x, rect.y, 16, 16));
            } else if ("server_door".equals(name)) {
                doors.add(new Door(rect.x, rect.y, rect.width, rect.height));
            }
            // --- Đọc thực thể máy bán hàng tự động Vending Machine ---
            else if ("vending_machine".equals(name)) {
                vendingMachines.add(new VendingMachine(rect.x, rect.y, rect.width, rect.height));
            }
            // --- Đọc các vật phẩm rơi tự do ngoài Map (Đã mở rộng đầy đủ loại item) ---
            else if ("item_meat".equals(name) || "item_rotten_meat".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.ROTTEN_MEAT));
            } else if ("item_water".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.WATER));
            } else if ("item_beer".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.BEER));
            } else if ("item_coin".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.COIN));
            } else if ("item_shoes".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.SHOES));
            } else if ("item_clock".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.CLOCK));
            } else if ("item_amulet".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.AMULET));
            } else if ("item_invisibility".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.INVISIBILITY));
            } else if ("item_lemon".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.LEMON));
            } else if ("item_orange".equals(name)) {
                items.add(new Item(rect.x, rect.y, Item.ItemType.ORANGE));
            }
        }

        // Đọc lính tuần tra
        MapObjects enemyObjects = map.getLayers().get("enemies").getObjects();
        for (MapObject obj : enemyObjects) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                int id = obj.getProperties().get("id", -1, Number.class).intValue();
                if ("guard_start".equals(obj.getName())) enemyStarts.put(id, new Vector2(r.x, r.y));
                else if ("guard_end".equals(obj.getName())) enemyEnds.put(id, new Vector2(r.x, r.y));
            }
        }
    }

    public Texture getItemTexture(Item.ItemType type) {
        return itemTextures.get(type);
    }

    public void renderShapes(ShapeRenderer shapeRenderer) {
        for (Door door : doors) door.render(shapeRenderer);
    }

    public void renderSprites(SpriteBatch batch) {
        // 1. Vẽ máy bán hàng tự động (Vending Machines) lên bản đồ trước để tránh đè lên item nhỏ
        for (VendingMachine vm : vendingMachines) {
            if (vendingMachineTexture != null) {
                batch.draw(vendingMachineTexture, vm.bounds.x, vm.bounds.y, vm.bounds.width, vm.bounds.height);
            }
        }

        // 2. Vẽ chìa khóa bí mật
        for (Rectangle key : keys) {
            batch.draw(keyTexture, key.x, key.y, key.width, key.height);
        }

        // 3. Vẽ tự động toàn bộ danh sách item rơi trên mặt đất bằng bộ Texture Map tập trung
        for (Item item : items) {
            Texture tex = itemTextures.get(item.type);
            if (tex != null) {
                batch.draw(tex, item.getPosition().x, item.getPosition().y, item.getSize(), item.getSize());
            }
        }
    }

    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (keyTexture != null) keyTexture.dispose();
        if (btnUsePromptTex != null) btnUsePromptTex.dispose();

        // --- Giải phóng vùng nhớ của ảnh Máy bán hàng ---
        if (vendingMachineTexture != null) {
            vendingMachineTexture.dispose();
            vendingMachineTexture = null;
        }

        // Giải phóng sạch sẽ vùng nhớ tránh memory leak cho các Texture
        for (Texture tex : itemTextures.values()) {
            if (tex != null) tex.dispose();
        }
        itemTextures.clear();

        if (targetServer != null) {
            targetServer.dispose();
        }

        walls.clear();
        wallRects.clear();
        doors.clear();
        keys.clear();
        items.clear();
        vendingMachines.clear(); // Xóa sạch danh sách máy khi chuyển cảnh
        enemyStarts.clear();
        enemyEnds.clear();
    }
}
