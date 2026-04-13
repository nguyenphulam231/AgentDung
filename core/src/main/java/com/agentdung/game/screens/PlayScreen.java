package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.core.LevelProvider;
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
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class PlayScreen extends ScreenAdapter {
    AgentDungGame game;
    OrthographicCamera camera;

    Player dung;
    Enemy guard;
    Array<Skill> skills;
    Array<Projectile> projectiles;
    Array<Rectangle> poopTraps;
    Array<Wall> walls;
    Server targetServer;
    int currentLevel;
    int currentWorld = 1; // Mặc định là world 1

    public PlayScreen(AgentDungGame game, int level) {
        this.game = game;
        this.currentLevel = level;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        initLevel(currentLevel);
    }

    private void initLevel(int level) {
        // Gọi LevelProvider với cả World và Level
        LevelProvider data = LevelProvider.getLevel(currentWorld, level);

        // Cập nhật dữ liệu từ Provider
        this.walls = data.walls;
        this.dung = new Player(data.dungSpawn.x, data.dungSpawn.y);
        this.dung.setSize(32);

        this.guard = new Enemy(data.guardSpawn.x, data.guardSpawn.y);
        this.guard.setPatrolRoute(data.guardSpawn.x, data.guardSpawn.y, data.guardPatrolEnd.x, data.guardPatrolEnd.y);

        this.targetServer = new Server(data.serverPos.x, data.serverPos.y);

        // Reset các mảng thực thể để tránh rác từ level cũ
        this.projectiles = new Array<>();
        this.poopTraps = new Array<>();

        // Nạp lại skills
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
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Wall w : walls) w.render(game.shapeRenderer);
        targetServer.render(game.shapeRenderer);

        for (Rectangle trap : poopTraps) {
            game.shapeRenderer.setColor(new Color(0.5f, 0.25f, 0, 1));
            game.shapeRenderer.rect(trap.x, trap.y, trap.width, trap.height);
        }

        dung.render(game.shapeRenderer);
        guard.render(game.shapeRenderer);
        for (Projectile p : projectiles) p.render(game.shapeRenderer);
        game.shapeRenderer.end();

        // HUD cố định trên màn hình
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

        // Camera mượt mà bám theo Dũng
        camera.position.x = dung.getPosition().x + dung.getSize() / 2;
        camera.position.y = dung.getPosition().y + dung.getSize() / 2;
        camera.update();

        guard.update(delta, dung);

        // Kiểm tra dẫm phân
        Rectangle guardRect = new Rectangle(guard.getPosition().x, guard.getPosition().y, guard.getSize(), guard.getSize());
        for (int i = poopTraps.size - 1; i >= 0; i--) {
            if (guardRect.overlaps(poopTraps.get(i))) {
                guard.applyPoopEffect();
                poopTraps.removeIndex(i);
            }
        }

        // Nếu bị bắt -> Reset level hiện tại
        if (guard.detects(dung)) {
            initLevel(currentLevel);
        }

        // KIỂM TRA PHÁ HỦY SERVER -> QUA MÀN
        if (targetServer.hp <= 0) {
            currentLevel++;
            // Bạn có thể thêm logic check nếu hết level thì về Menu, ở đây mình tạm gọi level tiếp theo
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

        float moveSpeed = 220f;
        Vector2 vel = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            vel.x = MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            vel.y = MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            vel.x = -MathUtils.cosDeg(dung.getAngle()) * moveSpeed;
            vel.y = -MathUtils.sinDeg(dung.getAngle()) * moveSpeed;
        }

        // Xử lý va chạm X
        float oldX = dung.getPosition().x;
        dung.getPosition().x += vel.x * delta;
        Rectangle dungRectX = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.getSize(), dung.getSize());
        for (Wall w : walls) {
            if (Intersector.overlaps(dungRectX, w.bounds)) {
                dung.getPosition().x = oldX;
                break;
            }
        }

        // Xử lý va chạm Y
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
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            skills.get(0).activate(dung, guard, projectiles);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (skills.get(1).activate(dung, guard, projectiles)) {
                poopTraps.add(new Rectangle(dung.getPosition().x, dung.getPosition().y, 20, 20));
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) skills.get(2).activate(dung, guard, projectiles);
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) skills.get(3).activate(dung, guard, projectiles);
    }

    private void updateProjectiles(float delta) {
        Rectangle guardRect = new Rectangle(guard.getPosition().x, guard.getPosition().y, guard.getSize(), guard.getSize());
        Rectangle serverRect = new Rectangle(targetServer.x, targetServer.y, targetServer.width, targetServer.height);

        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
            Rectangle pRect = new Rectangle(p.getPosition().x, p.getPosition().y, 5, 5);

            // Bắn trúng lính
            if (pRect.overlaps(guardRect)) {
                if (p instanceof BlobProjectile) guard.applySpitEffect();
                else if (p instanceof StreamProjectile) {
                    if (p.getColor().equals(Color.YELLOW)) guard.applyPeeEffect();
                    else if (p.getColor().equals(Color.WHITE)) guard.applyVomitEffect();
                }
                projectiles.removeIndex(i);
                continue;
            }

            // Bắn trúng Server (Chỉ tia nước tiểu mới gây dame mạnh cho điện tử)
            if (p.getColor().equals(Color.YELLOW) && pRect.overlaps(serverRect)) {
                targetServer.takeDamage(40 * delta); // Tăng dame một chút cho nhanh qua màn
                projectiles.removeIndex(i);
                continue;
            }

            // Xóa đạn nếu chạm tường (tùy chọn)
            for(Wall w : walls) {
                if(pRect.overlaps(w.bounds)) {
                    projectiles.removeIndex(i);
                    break;
                }
            }

            if (i < projectiles.size && !p.isActive()) projectiles.removeIndex(i);
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

        // Hiển thị máu Server ở trên đầu màn hình cho dễ nhìn
        game.shapeRenderer.setColor(Color.RED);
        game.shapeRenderer.rect(Gdx.graphics.getWidth()/2 - 100, Gdx.graphics.getHeight() - 30, 200 * (targetServer.hp / 100f), 20);
    }
}
