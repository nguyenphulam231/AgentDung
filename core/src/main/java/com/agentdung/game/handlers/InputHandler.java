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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class InputHandler {
    private static final float MOVE_SPEED = 140f;
    private static final float TARGET_RANGE = 180f;

    private final AgentDungGame game;
    private final SkillSoundPlayer soundPlayer;
    private final Array<SkillInputBinding> skillBindings;

    public InputHandler(AgentDungGame game, SkillSoundPlayer soundPlayer) {
        this.game = game;
        this.soundPlayer = soundPlayer;
        this.skillBindings = createDefaultBindings();
    }

    private Array<SkillInputBinding> createDefaultBindings() {
        Array<SkillInputBinding> bindings = new Array<>();

        bindings.add(SkillInputBinding.tap(
            Input.Keys.Q, 0,
            SkillInputBinding.SoundMode.ONE_SHOT,
            game.assets.getSpitSound(),
            null
        ));

        bindings.add(SkillInputBinding.tap(
            Input.Keys.E, 1,
            SkillInputBinding.SoundMode.ONE_SHOT,
            game.assets.getPoopSound(),
            ctx -> ctx.entityManager.poopTraps.add(new Rectangle(
                ctx.player.getPosition().x + 5,
                ctx.player.getPosition().y + 5,
                16, 16
            ))
        ));

        bindings.add(SkillInputBinding.hold(
            Input.Keys.C, 2,
            SkillInputBinding.SoundMode.LOOP_WHILE_ACTIVE,
            game.assets.getPeeSound(),
            null
        ));

        bindings.add(SkillInputBinding.hold(
            Input.Keys.V, 3,
            SkillInputBinding.SoundMode.ONCE_PER_HOLD,
            game.assets.getVomitSound(),
            null
        ));

        return bindings;
    }

    public void handleTankMovement(float delta, Player player, OrthographicCamera camera, MapManager mapManager) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        float cx = player.getPosition().x + player.getSize() / 2f;
        float cy = player.getPosition().y + player.getSize() / 2f;
        player.setAngle(MathUtils.atan2(mousePos.y - cy, mousePos.x - cx) * MathUtils.radiansToDegrees);

        player.getVelocity().set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.getVelocity().x = MathUtils.cosDeg(player.getAngle()) * MOVE_SPEED;
            player.getVelocity().y = MathUtils.sinDeg(player.getAngle()) * MOVE_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.getVelocity().x = -MathUtils.cosDeg(player.getAngle()) * MOVE_SPEED;
            player.getVelocity().y = -MathUtils.sinDeg(player.getAngle()) * MOVE_SPEED;
        }
    }

    public void handleSkillInput(Player player, Array<Skill> skills, EntityManager entityManager) {
        Enemy target = findNearestEnemy(player, entityManager);

        for (SkillInputBinding binding : skillBindings) {
            processBinding(binding, player, target, entityManager, skills);
        }
    }

    public void stopLoopingSounds() {
        soundPlayer.stopAll(game);
    }

    private void processBinding(
        SkillInputBinding binding,
        Player player,
        Enemy target,
        EntityManager entityManager,
        Array<Skill> skills
    ) {
        if (binding.getSkillIndex() >= skills.size) return;

        boolean keyActive = binding.getTrigger() == SkillInputBinding.Trigger.TAP
            ? Gdx.input.isKeyJustPressed(binding.getKey())
            : Gdx.input.isKeyPressed(binding.getKey());

        if (!keyActive) {
            handleKeyReleased(binding);
            return;
        }

        Skill skill = skills.get(binding.getSkillIndex());
        SkillActivationContext context = new SkillActivationContext(
            player, target, entityManager, game, skills, binding.getSkillIndex()
        );

        if (skill.activate(player, target, entityManager.getProjectiles())) {
            if (binding.getOnSuccess() != null) {
                binding.getOnSuccess().accept(context);
            }
            handleActivationSound(binding);
        } else {
            handleActivationFailed(binding);
        }
    }

    private void handleKeyReleased(SkillInputBinding binding) {
        switch (binding.getSoundMode()) {
            case LOOP_WHILE_ACTIVE:
                soundPlayer.stopLoop(game, binding.getSound());
                break;
            case ONCE_PER_HOLD:
                soundPlayer.resetHoldSound();
                break;
            default:
                break;
        }
    }

    private void handleActivationSound(SkillInputBinding binding) {
        switch (binding.getSoundMode()) {
            case ONE_SHOT:
                soundPlayer.playOneShot(game, binding.getSound());
                break;
            case LOOP_WHILE_ACTIVE:
                soundPlayer.startLoop(game, binding.getSound());
                break;
            case ONCE_PER_HOLD:
                soundPlayer.playOncePerHold(game, binding.getSound());
                break;
            default:
                break;
        }
    }

    private void handleActivationFailed(SkillInputBinding binding) {
        if (binding.getSoundMode() == SkillInputBinding.SoundMode.ONCE_PER_HOLD) {
            soundPlayer.resetHoldSound();
        }
        if (binding.getSoundMode() == SkillInputBinding.SoundMode.LOOP_WHILE_ACTIVE) {
            soundPlayer.stopLoop(game, binding.getSound());
        }
    }

    private Enemy findNearestEnemy(Player player, EntityManager entityManager) {
        Enemy target = null;
        float minDistance = TARGET_RANGE;
        for (Enemy enemy : entityManager.getEnemies()) {
            float distance = Vector2.dst(
                player.getPosition().x, player.getPosition().y,
                enemy.getPosition().x, enemy.getPosition().y
            );
            if (distance < minDistance) {
                minDistance = distance;
                target = enemy;
            }
        }
        return target;
    }
}
