package com.platypi.exodus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class PixelSplash implements Screen {

    // sprite batch
    private SpriteBatch spriteBatch;

    // image for splash
    private Sprite splashSprite;
    private float animateFrames;
    private float waitBeforeAnimate;
    private float waitAfterAnimate;
    private boolean doneAnimating;

    // camera
    private OrthographicCamera guiCamera;
    private Viewport guiViewport;

    // sound effect
    private Sound whirl;
    private boolean waitToPlay;

    // game to change screens
    private Game superGame;

    PixelSplash(Game game) {
        // set the game
        this.superGame = game;

        // set sprite batch
        spriteBatch = new SpriteBatch();

        // initialize the camera and viewport
        guiCamera   = new OrthographicCamera(200, 200 / ((float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        guiCamera.position.set(guiCamera.viewportWidth / 2f, guiCamera.viewportHeight / 2f, 0);
        guiViewport = new FitViewport(guiCamera.viewportWidth, guiCamera.viewportHeight, guiCamera);
        guiViewport.apply();

        // initialize the splash sprite
        splashSprite = new Sprite(new Texture(Gdx.files.internal("Images/Title/splash.png")));
        splashSprite.setSize(64, 16);
        splashSprite.setRegion(0, 0, 64, 16);
        splashSprite.setPosition(guiCamera.viewportWidth / 2 - splashSprite.getWidth() / 2, guiCamera.viewportHeight / 2 - splashSprite.getHeight() / 2);
        doneAnimating = false;

        // sound effect
        whirl = Gdx.audio.newSound(Gdx.files.internal("Music/SFX/sfx_sounds_powerup8.wav"));
        waitToPlay = false;
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        { // UPDATE METHODS
            guiCamera.update();

            waitBeforeAnimate += .75f;

            if (waitBeforeAnimate >= (2 / delta) && waitAfterAnimate <= 0) {
                animateFrames += 1.5f;

                if (!waitToPlay) {
                    long id = whirl.play(.15f);
                    whirl.setPitch(id, .8f);
                    waitToPlay = true;
                }
            }

            if (animateFrames >= 18)
                waitAfterAnimate += .75f;

            if (waitAfterAnimate >= (1 / delta))
                doneAnimating = true;

            if (doneAnimating) {
                superGame.setScreen(new PixelMenu(superGame));
                this.dispose();
                return;
            }

            // update the splash screen
            splashSprite.setRegion(64 * (int)animateFrames,0, 64, 16);
        }

        { // DRAW METHODS
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();

            spriteBatch.setProjectionMatrix(guiCamera.combined);

            // draw the splash screen
            splashSprite.draw(spriteBatch);

            spriteBatch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        guiViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        splashSprite.getTexture().dispose();
        whirl.dispose();
    }
}
