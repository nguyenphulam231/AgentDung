package com.agentdung.game.core;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameMap {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public GameMap(String path) {
        map = new TmxMapLoader().load(path);
        renderer = new OrthogonalTiledMapRenderer(map);
    }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public TiledMap getTiledMap() { return map; }

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}
