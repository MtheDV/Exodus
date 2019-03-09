package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import static com.platypi.exodus.PixelMenu.musicOn;
import static com.platypi.exodus.PixelMenu.soundOn;

class PixelSounds {
    // sounds
    private Sound menuButtonHit, jumpSfx, pauseSfx, resumeSfx, landEnemySfx, finishLevelSfx, noLevelSfx, levelSelectSfx, destroyBossSfx, newBossSfx;
    // music
    private Music menuMusic, bossMusic;

    PixelSounds() {
        menuButtonHit = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_menu_move4.wav"));
        jumpSfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_movement_jump8.wav"));
        pauseSfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_sounds_pause1_out.wav"));
        resumeSfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_sounds_pause1_in.wav"));
        landEnemySfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_movement_jump16_landing.wav"));
        finishLevelSfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_sound_neutral11.wav"));
        noLevelSfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_movement_jump16_landing.wav"));
        levelSelectSfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_menu_move1.wav"));
        destroyBossSfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_exp_medium5.wav"));
        newBossSfx = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_exp_medium13.wav"));

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/Sample_3.mp3"));
//        levelMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/Mellow-Puzzler.mp3"));
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/Boss.mp3"));
    }


    void playMusic(String music, boolean loop) {
        if (musicOn) {
            if (music.equals("menu")) {
                if (!menuMusic.isPlaying()) {
                    menuMusic.play();
                    menuMusic.setPosition(22);
                    menuMusic.setVolume(1.5f);
                    menuMusic.setLooping(loop);
                    bossMusic.stop();
                }
            }
//            if (music.equals("level")) {
//                if (!levelMusic.isPlaying()) {
//                    levelMusic.play();
//                    levelMusic.setVolume(.8f);
//                    levelMusic.setLooping(loop);
//                    menuMusic.stop();
//                    bossMusic.stop();
//                }
//            }
            if (music.equals("boss")) {
                if (!bossMusic.isPlaying()) {
                    bossMusic.play();
                    bossMusic.setVolume(1f);
                    bossMusic.setLooping(loop);
                    menuMusic.stop();
                }
            }
        }
    }

    void stopMusic() {
        menuMusic.stop();
        bossMusic.stop();
    }

    void playSound(String sound) {
        if (soundOn) {
            if (sound.equals("button")) {
                long id = menuButtonHit.play(.2f);
                menuButtonHit.setPitch(id, .8f);
            }
            if (sound.equals("jump")) {
                long id = jumpSfx.play(.35f);
                jumpSfx.setPitch(id, 1.1f);
            }
            if (sound.equals("pause")) {
                long id = pauseSfx.play(.25f);
                pauseSfx.setPitch(id, .8f);
            }
            if (sound.equals("resume")) {
                long id = resumeSfx.play(.15f);
                resumeSfx.setPitch(id, .8f);
            }
            if (sound.equals("land")) {
                long id = landEnemySfx.play(.3f);
                landEnemySfx.setPitch(id, .8f);
            }
            if (sound.equals("finishLevel")) {
                long id = finishLevelSfx.play(.25f);
                finishLevelSfx.setPitch(id, 1f);
            }
            if (sound.equals("noLevel")) {
                long id = noLevelSfx.play(.7f);
                noLevelSfx.setPitch(id, .8f);
            }
            if (sound.equals("select")) {
                long id = levelSelectSfx.play(.15f);
                levelSelectSfx.setPitch(id, .8f);
            }
            if (sound.equals("destroyBoss")) {
                long id = destroyBossSfx.play(.7f);
                destroyBossSfx.setPitch(id, .2f);
            }
            if (sound.equals("newBoss")) {
                long id = newBossSfx.play(.9f);
                newBossSfx.setPitch(id, .5f);
            }
        }
    }
}
