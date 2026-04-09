package com.agentdung.game;

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

    public PlayScreen(AgentDungGame game, int level) {
        this.game = game;
        this.currentLevel = level;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        initLevel(level);
    }

    private void initLevel(int level) {
        LevelProvider data = LevelProvider.getLevel(level);
        this.walls = data.walls;
        this.dung = new Player(data.dungSpawn.x, data.dungSpawn.y);
        this.dung.size = 32;

        this.guard = new Enemy(data.guardSpawn.x, data.guardSpawn.y);
        this.guard.setPatrolRoute(data.guardSpawn.x, data.guardSpawn.y, data.guardPatrolEnd.x, data.guardPatrolEnd.y);

        this.targetServer = new Server(data.serverPos.x, data.serverPos.y);
        this.projectiles = new Array<>();
        this.poopTraps = new Array<>();

        // refill skills
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

        // HUD
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

        camera.position.set(dung.getPosition().x, dung.getPosition().y, 0);
        camera.update();

        guard.update(delta, dung);

        // step-on-poop check
        Rectangle guardRect = new Rectangle(guard.getPosition().x, guard.getPosition().y, guard.size, guard.size);
        for (int i = poopTraps.size - 1; i >= 0; i--) {
            if (guardRect.overlaps(poopTraps.get(i))) {
                guard.applyPoopEffect();
                poopTraps.removeIndex(i);
            }
        }

        if (guard.detects(dung)) initLevel(currentLevel);
        for (Skill s : skills) s.update(delta);
    }

    private void handleTankMovement(float delta) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        float dx = mousePos.x - (dung.getPosition().x + dung.size / 2);
        float dy = mousePos.y - (dung.getPosition().y + dung.size / 2);
        dung.angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

        float moveSpeed = 220f;
        Vector2 vel = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            vel.x = MathUtils.cosDeg(dung.angle) * moveSpeed;
            vel.y = MathUtils.sinDeg(dung.angle) * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            vel.x = -MathUtils.cosDeg(dung.angle) * moveSpeed;
            vel.y = -MathUtils.sinDeg(dung.angle) * moveSpeed;
        }

        // collide x
        float oldX = dung.getPosition().x;
        dung.getPosition().x += vel.x * delta;
        Rectangle dungRectX = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.size, dung.size);
        for (Wall w : walls) if (Intersector.overlaps(dungRectX, w.bounds)) { dung.getPosition().x = oldX; break; }

        // collide Y
        float oldY = dung.getPosition().y;
        dung.getPosition().y += vel.y * delta;
        Rectangle dungRectY = new Rectangle(dung.getPosition().x, dung.getPosition().y, dung.size, dung.size);
        for (Wall w : walls) if (Intersector.overlaps(dungRectY, w.bounds)) { dung.getPosition().y = oldY; break; }
    }

    private void handleSkillInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            skills.get(0).activate(dung, guard, projectiles);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            skills.get(1).activate(dung, guard, projectiles);
            poopTraps.add(new Rectangle(dung.getPosition().x, dung.getPosition().y, 20, 20));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) skills.get(2).activate(dung, guard, projectiles);
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) skills.get(3).activate(dung, guard, projectiles);
    }

    private void updateProjectiles(float delta) {
        Rectangle guardRect = new Rectangle(guard.getPosition().x, guard.getPosition().y, guard.size, guard.size);
        Rectangle serverRect = new Rectangle(targetServer.x, targetServer.y, targetServer.width, targetServer.height);
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
            Rectangle pRect = new Rectangle(p.position.x, p.position.y, 5, 5);
            if (pRect.overlaps(guardRect)) {
                if (p instanceof BlobProjectile) guard.applySpitEffect();
                else if (p instanceof StreamProjectile) {
                    if (p.color.equals(Color.YELLOW)) guard.applyPeeEffect();
                    else if (p.color.equals(Color.WHITE)) guard.applyVomitEffect();
                }
                projectiles.removeIndex(i);
                continue;
            }
            if (p.color.equals(Color.YELLOW) && pRect.overlaps(serverRect)) {
                targetServer.takeDamage(35 * delta);
                projectiles.removeIndex(i);
            }
            if (!p.isActive()) projectiles.removeIndex(i);
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
    }
}
