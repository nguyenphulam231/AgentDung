package com.agentdung.game.handlers;

import com.agentdung.game.core.AgentDungGame;

public class SkillSoundPlayer {
    private long peeSoundId = -1;
    private boolean vomitSoundPlayed = false;

    public void playOneShot(AgentDungGame game, com.badlogic.gdx.audio.Sound sound) {
        if (canPlay(game) && sound != null) {
            sound.play();
        }
    }

    public void startLoop(AgentDungGame game, com.badlogic.gdx.audio.Sound sound) {
        if (peeSoundId == -1 && canPlay(game) && sound != null) {
            peeSoundId = sound.play();
            sound.setLooping(peeSoundId, true);
        }
    }

    public void stopLoop(AgentDungGame game, com.badlogic.gdx.audio.Sound sound) {
        if (peeSoundId != -1 && sound != null) {
            sound.stop(peeSoundId);
            peeSoundId = -1;
        }
    }

    public void playOncePerHold(AgentDungGame game, com.badlogic.gdx.audio.Sound sound) {
        if (!vomitSoundPlayed && canPlay(game) && sound != null) {
            sound.play();
            vomitSoundPlayed = true;
        }
    }

    public void resetHoldSound() {
        vomitSoundPlayed = false;
    }

    public void stopAll(AgentDungGame game) {
        stopLoop(game, game.assets.getPeeSound());
        vomitSoundPlayed = false;
    }

    private boolean canPlay(AgentDungGame game) {
        return game.isMasterOn && game.isSfxOn;
    }
}
