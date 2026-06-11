package com.agentdung.game.handlers;

import java.util.function.Consumer;

public class SkillInputBinding {
    public enum Trigger { TAP, HOLD }
    public enum SoundMode { NONE, ONE_SHOT, LOOP_WHILE_ACTIVE, ONCE_PER_HOLD }

    private final int key;
    private final int skillIndex;
    private final Trigger trigger;
    private final SoundMode soundMode;
    private final com.badlogic.gdx.audio.Sound sound;
    private final Consumer<SkillActivationContext> onSuccess;

    public SkillInputBinding(
        int key,
        int skillIndex,
        Trigger trigger,
        SoundMode soundMode,
        com.badlogic.gdx.audio.Sound sound,
        Consumer<SkillActivationContext> onSuccess
    ) {
        this.key = key;
        this.skillIndex = skillIndex;
        this.trigger = trigger;
        this.soundMode = soundMode;
        this.sound = sound;
        this.onSuccess = onSuccess;
    }

    public int getKey() { return key; }
    public int getSkillIndex() { return skillIndex; }
    public Trigger getTrigger() { return trigger; }
    public SoundMode getSoundMode() { return soundMode; }
    public com.badlogic.gdx.audio.Sound getSound() { return sound; }
    public Consumer<SkillActivationContext> getOnSuccess() { return onSuccess; }

    public static SkillInputBinding tap(
        int key,
        int skillIndex,
        SoundMode soundMode,
        com.badlogic.gdx.audio.Sound sound,
        Consumer<SkillActivationContext> onSuccess
    ) {
        return new SkillInputBinding(key, skillIndex, Trigger.TAP, soundMode, sound, onSuccess);
    }

    public static SkillInputBinding hold(
        int key,
        int skillIndex,
        SoundMode soundMode,
        com.badlogic.gdx.audio.Sound sound,
        Consumer<SkillActivationContext> onSuccess
    ) {
        return new SkillInputBinding(key, skillIndex, Trigger.HOLD, soundMode, sound, onSuccess);
    }
}
