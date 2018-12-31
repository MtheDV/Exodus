package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class PixelTransition {
    private Sprite transitionSequence;
    private int transition;
    private float frame;
    private float frameSpeed;
    private static int direction;

    PixelTransition(OrthographicCamera renderedCamera) {
        transitionSequence = new Sprite(new Texture(Gdx.files.internal("Images/Title/transition-sequence.png")));
        transitionSequence.setSize(360, 200);
        transitionSequence.setPosition(renderedCamera.viewportWidth / 2 - transitionSequence.getWidth() / 2, 0);
        transitionSequence.setRegion(0, 0, 360, 200);

        frame = 0;
        frameSpeed = 1;
        direction = (int)(Math.random() * 4);
    }

    void update() {
        switch (transition) {
            case 1:
                In();
                break;
            case -1:
                Out();
                break;
        }

        transitionSequence.setRegion(360 * (int)frame, 0, 360, 200);

        switch (direction) {
            case 0:
                transitionSequence.setFlip(false, false);
                break;
            case 1:
                transitionSequence.setFlip(true, false);
                break;
            case 2:
                transitionSequence.setFlip(true, true);
                break;
            case 3:
                transitionSequence.setFlip(false, true);
                break;
        }
    }

    void render(SpriteBatch spriteBatch) {
        transitionSequence.draw(spriteBatch);
    }

    private void In() {
        if (frame > 0)
            frame -= frameSpeed;
        if (frame <= 0) {
            frame = 0;
            setTransition(0);
        }
    }

    private void Out() {
        if (frame < 30)
            frame += frameSpeed;
        if (frame >= 30) {
            frame = 30;
            setTransition(0);
        }
    }

    void setFrameSpeed(float frameSpeed) { this.frameSpeed = frameSpeed; }

    void setTransition(int transition) {
        this.transition = transition;
    }

    boolean isIn() {
        return frame <= 0f;
    }

    boolean isOut() {
        return frame >= 30f;
    }

    void dispose() {
        transitionSequence.getTexture().dispose();
    }
}
