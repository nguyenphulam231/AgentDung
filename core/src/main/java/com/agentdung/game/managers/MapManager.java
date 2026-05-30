package com.agentdung.game.managers;

import com.agentdung.game.entities.Door;
import com.agentdung.game.entities.Server;
import com.agentdung.game.entities.Wall;
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
    public Server targetServer;
    public Texture keyTexture;

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
        int tileHeight = map.getProperties().get("height", Integer.class);
        mapWidth  = map.getProperties().get("width", Integer.class) * tileWidth;
        mapHeight = map.getProperties().get("height", Integer.class) * tileHeight;

        keyTexture = new Texture("images/key.png");

        // Đọc collisions
        MapObjects wallObjects = map.getLayers().get("collisions").getObjects();
        for (MapObject obj : wallObjects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            walls.add(new Wall(rect.x, rect.y, rect.width, rect.height));
            wallRects.add(rect);
        }

        // Đọc thực thể tĩnh
        MapObjects entityObjects = map.getLayers().get("entities").getObjects();
        for (MapObject obj : entityObjects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            if ("player_spawn".equals(obj.getName())) {
                playerSpawn.set(rect.x, rect.y);
            } else if ("server".equals(obj.getName())) {
                targetServer = new Server(rect.x, rect.y);
            } else if ("key".equals(obj.getName())) {
                keys.add(new Rectangle(rect.x, rect.y, 16, 16));
            } else if ("server_door".equals(obj.getName())) {
                doors.add(new Door(rect.x, rect.y, rect.width, rect.height));
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

    public void renderShapes(ShapeRenderer shapeRenderer) {
        if (targetServer != null) targetServer.render(shapeRenderer);
        for (Door door : doors) door.render(shapeRenderer);
    }

    public void renderSprites(SpriteBatch batch) {
        for (Rectangle key : keys) {
            batch.draw(keyTexture, key.x, key.y, key.width, key.height);
        }
    }

    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (keyTexture != null) keyTexture.dispose();
        walls.clear(); wallRects.clear(); doors.clear(); keys.clear();
        enemyStarts.clear(); enemyEnds.clear();
    }
}
