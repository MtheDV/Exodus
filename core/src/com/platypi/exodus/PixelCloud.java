package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class PixelCloud {

    private Sprite cloud;
    private float moveSpeed;
    private boolean inFrontOfWorld;

    PixelCloud(OrthographicCamera guiCamera, Sprite black, boolean outsideScreen) {
        // set the move speed
        while (moveSpeed <= .1f && moveSpeed >= -.1f)
            moveSpeed = (float)(Math.random() * 1.5f - .75f);

        // create the cloud image
        cloud = new Sprite(new Texture(Gdx.files.internal("Images/Title/clouds.png")));
        cloud.setRegion((int)(Math.random() * 3) * 32, 0, 32, 16);
        cloud.setSize(32, 16);
        if (!outsideScreen)
            cloud.setPosition((int)(Math.random() * guiCamera.viewportWidth),
                    (int)(Math.random() * (guiCamera.viewportHeight - 2 * black.getHeight()) + black.getHeight() - 10));
        else {
            if (moveSpeed > 0)
                cloud.setPosition(-cloud.getWidth(),
                        (int)(Math.random() * (guiCamera.viewportHeight - 2 * black.getHeight()) + black.getHeight() - 40));
            else
                cloud.setPosition(guiCamera.viewportWidth,
                        (int)(Math.random() * (guiCamera.viewportHeight - 2 * black.getHeight()) + black.getHeight() - 40));
        }
        // set the image to be flipped or not
        cloud.setFlip(((int)(Math.random() * 2)) == 1, false);

        // make it in front of the world or not
        inFrontOfWorld = (int)(Math.random() * 3) == 0;
    }

    void render(SpriteBatch spriteBatch) {
        cloud.draw(spriteBatch);
    }

    void update() {
        cloud.translateX(moveSpeed);
    }

    Sprite getCloud() {
        return cloud;
    }

    boolean isInFrontOfWorld() {
        return inFrontOfWorld;
    }

    void dispose() {
        cloud.getTexture().dispose();
    }

}
