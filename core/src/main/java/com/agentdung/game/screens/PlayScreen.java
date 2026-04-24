package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.entities.Server;
import com.agentdung.game.entities.Wall;
import com.agentdung.game.projectiles.BlobProjectile;
import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.StreamProjectile;
import com.agentdung.game.skills.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashMap;
import java.util.Map;

public class PlayScreen extends ScreenAdapter {
    AgentDungGame game;
    OrthographicCamera camera;

    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    TmxMapLoader mapLoader;

    Player dung;
    Array<Enemy> enemies;
    Array<Skill> skills;
    Array<Projectile> projectiles;
    Array<Rectangle> poopTraps;
    Array<Wall> walls;
    Array<Rectangle> wallRects; // Dùng để truyền vào cho lính check tầm nhìn
    Server targetServer;

    int currentLevel;
    int currentWorld = 1;
    float mapWidth, mapHeight;

    public PlayScreen(AgentDungGame game, int level) {
        this.game = game;
        this.currentLevel = level;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 400 * (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth());

        this.enemies = new Array<>();
        this.projectiles = new Array<>();
        this.poopTraps = new Array<>();
        this.walls = new Array<>();
        this.wallRects = new Array<>();

        initLevel(currentLevel);
    }

    private void initLevel(int level) {
        String mapPath = "maps/map" + currentWorld + "_" + level + ".tmx";

        try {
            if (map != null) map.dispose();
            mapLoader = new TmxMapLoader();
            map = mapLoader.load(mapPath);
            mapRenderer = new OrthogonalTiledMapRenderer(map);

            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);
            mapWidth = map.getProperties().get("width", Integer.class) * tileWidth;
            mapHeight = map.getProperties().get("height", Integer.class) * tileHeight;

        } catch (Exception e) {
            Gdx.app.error("MapError", "Không tìm thấy: " + mapPath + ". Quay lại Menu.");
            game.setScreen(new MenuScreen(game));
            return;
        }

        enemies.clear();
        walls.clear();
        wallRects.clear();
        projectiles.clear();
        poopTraps.clear();

        // 1. Quét Tường
        MapObjects wallObjects = map.getLayers().get("collisions").getObjects();
        for (MapObject obj : wallObjects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            walls.add(new Wall(rect.x, rect.y, rect.width, rect.height));
            wallRects.add(rect);
        }

        // 2. Quét Thực thể (Player & Server)
        MapObjects entityObjects = map.getLayers().get("entities").getObjects();
        for (MapObject obj : entityObjects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            if ("player_spawn".equals(obj.getName())) {
                dung = new Player(rect.x, rect.y);
                dung.setSize(16);
            } else if ("server".equals(obj.getName())) {
                targetServer = new Server(rect.x, rect.y);
            }
        }

        // 3. Quét Kẻ địch (Layer: enemies)
        MapObjects enemyObjects = map.getLayers().get("enemies").getObjects();
        Map<Integer, Vector2> starts = new HashMap<>();
        Map<Integer, Vector2> ends = new HashMap<>();
        Map<Integer, Float> distances = new HashMap<>();
        Map<Integer, Float> angles = new HashMap<>();

        for (MapObject obj : enemyObjects) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                int id = obj.getProperties().get("id", -1, Number.class).intValue();
                String name = obj.getName();

                if ("guard_start".equals(name)) {
                    starts.put(id, new Vector2(r.x, r.y));
                    distances.put(id, obj.getProperties().get("viewDistance", 100f, Number.class).floatValue());
                    angles.put(id, obj.getProperties().get("viewAngle", 60f, Number.class).floatValue());
                } else if ("guard_end".equals(name)) {
                    ends.put(id, new Vector2(r.x, r.y));
                }
            }
        }

        for (Integer guardId : starts.keySet()) {
            if (ends.containsKey(guardId)) {
                Vector2 startPos = starts.get(guardId);
                Vector2 endPos = ends.get(guardId);

                Enemy guard = new Enemy(startPos.x, startPos.y);
                guard.setPatrolRoute(startPos.x, startPos.y, endPos.x, endPos.y);
                guard.setViewDistance(distances.getOrDefault(guardId, 100f));
                guard.setViewAngle(angles.getOrDefault(guardId, 60f));

                enemies.add(guard);
            }
        }

        this.skills = new Array<>();
        skills.add(new SpitSkill());
        skills.add(new PoopSkill());
        skills.add(new PeeSkill());
        skills.add(new VomitSkill());
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0, 0, 0, 1);

        mapRenderer.setView(camera);
        mapRenderer.render();

        game.shapeRenderer.setProjectionMatrix(camera.combined);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (targetServer != null) targetServer.render(game.shapeRenderer);

        for (Rectangle trap : poopTraps) {
            game.shapeRenderer.setColor(new Color(0.5f, 0.25f, 0, 1));
            game.shapeRenderer.rect(trap.x, trap.y, trap.width, trap.height);
        }

        // 1. Vẽ tầm nhìn của lính trước (để nó nằm dưới thân lính)
        for (Enemy e : enemies) {
            e.drawVision(game.shapeRenderer, wallRects);
        }

        // 2. Vẽ nhân vật và thân lính
        dung.render(game.shapeRenderer);
        for (Enemy e : enemies) {
            e.render(game.shapeRenderer); // Gọi hàm render không tham số để hiện thân lính
        }

        for (Projectile p : projectiles) p.render(game.shapeRenderer);

        game.shapeRenderer.end();

        Matrix4 hudMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.shapeRenderer.setProjectionMatrix(hudMatrix);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderHUD();
        game.shapeRenderer.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) game.setScreen(new MenuScreen(game));
    }

    private void update(float delta) {
        handleTankMovement(delta);
        handleSkillInput();
        updateProjectiles(delta);

        camera.position.x = MathUtils.clamp(dung.getPosition().x + dung.getSize() / 2, camera.viewportWidth / 2, mapWidth - camera.viewportWidth / 2);
        camera.position.y = MathUtils.clamp(dung.getPosition().y + dung.getSize() / 2, camera.viewportHeight / 2, mapHeight - camera.viewportHeight / 2);
        camera.update();

        for (Enemy e : enemies) {
            e.update(delta, dung, wallRects);

            // Lính check tầm nhìn với danh sách wallRects
            if (e.detects(dung, wallRects)) {
                initLevel(currentLevel);
                return;
            }

            Rectangle guardRect = new Rectangle(e.getPosition().x, e.getPosition().y, e.getSize(), e.getSize());
            for (int i = poopTraps.size - 1; i >= 0; i--) {
                if (guardRect.overlaps(poopTraps.get(i))) {
                    e.applyPoopEffect();
                    poopTraps.removeIndex(i);
                }
            }
        }

        if (targetServer != null && targetServer.hp <= 0) {
            currentLevel++;
            initLevel(currentLevel);
        }

        for (Skill s : skills) s.update(delta);
    }

    private void handleTankMovement(float delta) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        float dx = mousePos.x - (dung.getPosition().x + dung.getSize() / 2);
        float dy = mousePos.y - (dung.getPosition().y + dung.getSize() / 2);
        dung.setAngle(MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees);

        float moveSpeed = 140f;
        Vector2 vel = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            vel.x = MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            vel.y = MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            vel.x = -MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            vel.y = -MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }

        float oldX = dung.getPosition().x;
        dung.getPosition().x += vel.x * delta;
        Rectangle dungRectX = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());
        for (Wall w : walls) {
            if (Intersector.overlaps(dungRectX, w.bounds)) {
                dung.getPosition().x = oldX;
                break;
            }
        }

        float oldY = dung.getPosition().y;
        dung.getPosition().y += vel.y * delta;
        Rectangle dungRectY = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());
        for (Wall w : walls) {
            if (Intersector.overlaps(dungRectY, w.bounds)) {
                dung.getPosition().y = oldY;
                break;
            }
        }
    }

    private void handleSkillInput() {
        Enemy target = null;
        float minDistance = 180f;
        for (Enemy e : enemies) {
            float d = Vector2.dst(dung.getPosition().x, dung.getPosition().y, e.getPosition().x, e.getPosition().y);
            if (d < minDistance) {
                minDistance = d;
                target = e;
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            skills.get(0).activate(dung, target, projectiles);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (skills.get(1).activate(dung, target, projectiles)) {
                poopTraps.add(new Rectangle(dung.getPosition().x, dung.getPosition().y, 16, 16));
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) skills.get(2).activate(dung, target, projectiles);
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) skills.get(3).activate(dung, target, projectiles);
    }

    private void updateProjectiles(float delta) {
        if (targetServer == null) return;
        Rectangle serverRect = new Rectangle(targetServer.x, targetServer.y, targetServer.width, targetServer.height);

        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
            Rectangle pRect = new Rectangle(p.getPosition().x, p.getPosition().y, 5, 5);
            boolean hit = false;

            for (Wall w : walls) {
                if (pRect.overlaps(w.bounds)) {
                    projectiles.removeIndex(i);
                    hit = true;
                    break;
                }
            }
            if (hit) continue;

            for (Enemy e : enemies) {
                Rectangle guardRect = new Rectangle(e.getPosition().x, e.getPosition().y, e.getSize(), e.getSize());
                if (pRect.overlaps(guardRect)) {
                    if (p instanceof BlobProjectile) e.applySpitEffect();
                    else if (p instanceof StreamProjectile) {
                        if (p.getColor().equals(Color.YELLOW)) e.applyPeeEffect();
                        else if (p.getColor().equals(Color.WHITE)) e.applyVomitEffect();
                    }
                    hit = true;
                    break;
                }
            }

            if (hit) { projectiles.removeIndex(i); continue; }

            if (p.getColor().equals(Color.YELLOW) && pRect.overlaps(serverRect)) {
                targetServer.takeDamage(40 * delta);
                projectiles.removeIndex(i);
                continue;
            }

            if (!hit && i < projectiles.size && !p.isActive()) projectiles.removeIndex(i);
        }
    }

    private void renderHUD() {
        for (int i = 0; i < skills.size; i++) {
            Skill s = skills.get(i);
            float xPos = 20, yPos = Gdx.graphics.getHeight() - 40 - (i * 25);
            game.shapeRenderer.setColor(Color.DARK_GRAY);
            game.shapeRenderer.rect(xPos, yPos, 150, 15);
            game.shapeRenderer.setColor(s.getManaColor());
            game.shapeRenderer.rect(xPos, yPos, 150 * s.getManaPercent(), 15);
        }
        if (targetServer != null) {
            game.shapeRenderer.setColor(Color.RED);
            game.shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() - 30, 200 * (targetServer.hp / 100f), 20);
        }
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}
