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
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
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
    Array<Rectangle> wallRects;
    Server targetServer;

    // --- BIẾN CHO TẦM NHÌN (FOG OF WAR) ---
    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    private Texture lightMask;

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
        initFBO();
        createLightMask();
    }

    private void initFBO() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);
    }

    private void createLightMask() {
        int size = 256;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float center = size / 2f;
        float maxDist = size / 2f;

        float solidRadius = 0.7f;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dist = Vector2.dst(x, y, center, center) / maxDist;
                float alpha;
                if (dist < solidRadius) {
                    alpha = 1.0f;
                } else if (dist < 1.0f) {
                    alpha = 1.0f - (dist - solidRadius) / (1.0f - solidRadius);
                } else {
                    alpha = 0;
                }
                pixmap.drawPixel(x, y, Color.rgba8888(1, 1, 1, alpha));
            }
        }
        lightMask = new Texture(pixmap);
        pixmap.dispose();
    }

    private void initLevel(int level) {
        String mapPath = "maps/map" + currentWorld + "_" + level + ".tmx";
        try {
            if (map != null) map.dispose();
            mapLoader = new TmxMapLoader();
            map = mapLoader.load(mapPath);
            mapRenderer = new OrthogonalTiledMapRenderer(map);

            int tileWidth  = map.getProperties().get("tilewidth",  Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);
            mapWidth  = map.getProperties().get("width",  Integer.class) * tileWidth;
            mapHeight = map.getProperties().get("height", Integer.class) * tileHeight;
        } catch (Exception e) {
            Gdx.app.error("MapError", "Không tìm thấy map. Quay lại Menu.");
            game.setScreen(new MenuScreen(game));
            return;
        }

        enemies.clear();
        walls.clear();
        wallRects.clear();
        projectiles.clear();
        poopTraps.clear();

        MapObjects wallObjects = map.getLayers().get("collisions").getObjects();
        for (MapObject obj : wallObjects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            walls.add(new Wall(rect.x, rect.y, rect.width, rect.height));
            wallRects.add(rect);
        }

        MapObjects entityObjects = map.getLayers().get("entities").getObjects();
        for (MapObject obj : entityObjects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            if ("player_spawn".equals(obj.getName())) {
                dung = new Player(rect.x, rect.y);
                dung.setSize(26);
            } else if ("server".equals(obj.getName())) {
                targetServer = new Server(rect.x, rect.y);
            }
        }

        MapObjects enemyObjects = map.getLayers().get("enemies").getObjects();
        Map<Integer, Vector2> starts = new HashMap<>();
        Map<Integer, Vector2> ends = new HashMap<>();
        for (MapObject obj : enemyObjects) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                int id = obj.getProperties().get("id", -1, Number.class).intValue();
                if ("guard_start".equals(obj.getName())) starts.put(id, new Vector2(r.x, r.y));
                else if ("guard_end".equals(obj.getName())) ends.put(id, new Vector2(r.x, r.y));
            }
        }
        for (Integer guardId : starts.keySet()) {
            if (ends.containsKey(guardId)) {
                Enemy guard = new Enemy(starts.get(guardId).x, starts.get(guardId).y);
                guard.setPatrolRoute(starts.get(guardId).x, starts.get(guardId).y, ends.get(guardId).x, ends.get(guardId).y);
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

        // --- 1. VẼ GAME BÌNH THƯỜNG ---
        mapRenderer.setView(camera);
        mapRenderer.render();

        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (targetServer != null) targetServer.render(game.shapeRenderer);
        for (Rectangle trap : poopTraps) {
            game.shapeRenderer.setColor(new Color(0.5f, 0.25f, 0, 1));
            game.shapeRenderer.rect(trap.x, trap.y, trap.width, trap.height);
        }
        for (Enemy e : enemies) e.render(game.shapeRenderer);
        for (Projectile p : projectiles) p.render(game.shapeRenderer);
        game.shapeRenderer.end();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        dung.draw(game.batch);
        game.batch.end();

        // --- 2. VẼ LỚP MẶT NẠ BÓNG TỐI TRONG FBO ---
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0.9f); // Độ mờ của bóng tối
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        // Chỉ đục lỗ bóng tối cho vị trí của Player
        game.batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        float viewRadius = 350f;
        game.batch.draw(lightMask,
            dung.getPosition().x + dung.getSize()/2f - viewRadius/2f,
            dung.getPosition().y + dung.getSize()/2f - viewRadius/2f,
            viewRadius, viewRadius);
        game.batch.end();
        fbo.end();

        // --- 3. DÁN LỚP BÓNG TỐI LÊN MÀN HÌNH ---
        game.batch.begin();
        // Reset về chế độ trộn chuẩn để vẽ FBO
        game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        game.batch.draw(fboRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // --- 3.5 VẼ TẦM NHÌN CỦA LÍNH (HIỆU ỨNG ĐÈN PIN) ---
        // Sử dụng Additive Blending để đèn lính sáng rực lên trên nền bóng tối
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE); // Chế độ cộng màu rực rỡ

        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Màu vàng nhạt mờ cho đèn pin lính
        game.shapeRenderer.setColor(1f, 1f, 0.7f, 0.4f);
        for (Enemy e : enemies) {
            e.drawVision(game.shapeRenderer, wallRects);
        }
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // --- 4. VẼ HUD VÀ THANH MANA ---
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        dung.render(game.shapeRenderer);
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
        dung.update(delta, dung, wallRects);
        handleSkillInput();
        updateProjectiles(delta);

        camera.position.x = MathUtils.clamp(dung.getPosition().x + dung.getSize() / 2, camera.viewportWidth / 2, mapWidth - camera.viewportWidth / 2);
        camera.position.y = MathUtils.clamp(dung.getPosition().y + dung.getSize() / 2, camera.viewportHeight / 2, mapHeight - camera.viewportHeight / 2);
        camera.update();

        for (Enemy e : enemies) {
            e.update(delta, dung, wallRects);
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
        for (Wall w : walls) if (Intersector.overlaps(dungRectX, w.bounds)) { dung.getPosition().x = oldX; break; }

        float oldY = dung.getPosition().y;
        dung.getPosition().y += dung.getVelocity().y * delta;
        Rectangle dungRectY = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());
        for (Wall w : walls) if (Intersector.overlaps(dungRectY, w.bounds)) { dung.getPosition().y = oldY; break; }
    }

    private void handleSkillInput() {
        Enemy target = null;
        float minDistance = 180f;
        for (Enemy e : enemies) {
            float d = Vector2.dst(dung.getPosition().x, dung.getPosition().y, e.getPosition().x, e.getPosition().y);
            if (d < minDistance) { minDistance = d; target = e; }
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            skills.get(0).activate(dung, target, projectiles);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (skills.get(1).activate(dung, target, projectiles))
                poopTraps.add(new Rectangle(dung.getPosition().x + 5, dung.getPosition().y + 5, 16, 16));
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
            for (Wall w : walls) if (pRect.overlaps(w.bounds)) { projectiles.removeIndex(i); hit = true; break; }
            if (hit) continue;
            for (Enemy e : enemies) {
                if (pRect.overlaps(new Rectangle(e.getPosition().x, e.getPosition().y, e.getSize(), e.getSize()))) {
                    if (p instanceof BlobProjectile) e.applySpitEffect();
                    else if (p instanceof StreamProjectile) {
                        if (p.getColor().equals(Color.YELLOW)) e.applyPeeEffect();
                        else if (p.getColor().equals(Color.WHITE)) e.applyVomitEffect();
                    }
                    hit = true; break;
                }
            }
            if (hit) { projectiles.removeIndex(i); continue; }
            if (p.getColor().equals(Color.YELLOW) && pRect.overlaps(serverRect)) {
                targetServer.takeDamage(40 * delta);
                projectiles.removeIndex(i);
                continue;
            }
            if (i < projectiles.size && !p.isActive()) projectiles.removeIndex(i);
        }
    }

    private void renderHUD() {
        for (int i = 0; i < skills.size; i++) {
            Skill s = skills.get(i);
            game.shapeRenderer.setColor(Color.DARK_GRAY);
            game.shapeRenderer.rect(20, Gdx.graphics.getHeight() - 40 - (i * 25), 150, 15);
            game.shapeRenderer.setColor(s.getManaColor());
            game.shapeRenderer.rect(20, Gdx.graphics.getHeight() - 40 - (i * 25), 150 * s.getManaPercent(), 15);
        }
        //if (targetServer != null) {
        //    game.shapeRenderer.setColor(Color.RED);
        //    game.shapeRenderer.rect(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() - 30, 200 * (targetServer.hp / 100f), 20);
        //}
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (fbo != null) fbo.dispose();
        if (lightMask != null) lightMask.dispose();
    }
}
