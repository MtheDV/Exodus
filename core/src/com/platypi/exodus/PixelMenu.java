package com.platypi.exodus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/*
 * TODO: CREATE BETTER IMAGES FOR THE MENU, GET THE GIT WORKING!
 */

class PixelMenu implements Screen, GestureDetector.GestureListener {

    // sprite batch
    private SpriteBatch spriteBatch;

    // sprite for the world
    private Sprite worldSprite;
    private Sprite starBackground;
    private Sprite black;

    // title
    private Texture title;
    private float titleBounce;
    private boolean titleBounceUp;

    // fonts
    private FreeTypeFontGenerator fontGenerator;
    private BitmapFont fontSmall;

    // font alpha
    private float fontAlpha;
    private boolean fontAlphaIn;

    // camera and viewport for the screen
    private OrthographicCamera guiCamera;
    private Viewport guiViewport;
    private OrthographicCamera fontCamera;
    private Viewport fontViewport;

    // mouse down
    private boolean mouseKeyDown;

    // transition
    private PixelTransition transitioner;
    private boolean enterScreen;
    private boolean exitScreen;

    // hold the game class to call other screens
    private Game superGame;
    // gesture detector
    private GestureDetector gestureDetector;

    PixelMenu(Game superGame) {
        // initialize the game class
        super();
        this.superGame = superGame;

        // initialize the sprite batch
        spriteBatch = new SpriteBatch();

        // initialize the camera and viewport
        guiCamera   = new OrthographicCamera(200, 200 / ((float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        guiCamera.position.set(guiCamera.viewportWidth / 2f, guiCamera.viewportHeight / 2f, 0);
        guiViewport = new FitViewport(guiCamera.viewportWidth, guiCamera.viewportHeight, guiCamera);
        guiViewport.apply();
        fontCamera   = new OrthographicCamera(800, 800 / ((float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        fontCamera.position.set(fontCamera.viewportWidth / 2f, fontCamera.viewportHeight / 2f, 0);
        fontViewport = new FitViewport(fontCamera.viewportWidth, fontCamera.viewportHeight, fontCamera);
        fontViewport.apply();

        // initialize the fonts
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/pixels.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 16;
        fontParameter.color = Color.WHITE;
        fontSmall = fontGenerator.generateFont(fontParameter);
        fontSmall.getData().setScale(1.5f);

        // font alpha
        fontAlphaIn = true;

        // mouse down
        mouseKeyDown = true;

        // world sprite
        worldSprite = new Sprite(new Texture(Gdx.files.internal("Images/Title/worldselector.png")));
        worldSprite.setCenter(guiCamera.viewportWidth / 2, guiCamera.viewportHeight / 2);
        worldSprite.setPosition(guiCamera.viewportWidth / 2 - worldSprite.getWidth() / 2, -worldSprite.getHeight() / 2);

        starBackground = new Sprite(new Texture(Gdx.files.internal("Images/Title/starbackground.png")));
        starBackground.setPosition(0, 0);

        black = new Sprite(new Texture(Gdx.files.internal("Images/GUI/black.png")));
        black.setSize(guiCamera.viewportWidth, 20);

        // title
        title = new Texture(Gdx.files.internal("Images/Title/title.png"));
        titleBounceUp = false;

        // transiitoner
        transitioner  = new PixelTransition(guiCamera);
        enterScreen   = true;
        exitScreen    = false;

        // gesture detector
        gestureDetector = new GestureDetector(this);
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(gestureDetector); }

    @Override
    public void render(float delta) {
        { // UPDATING
            // update cameras
            guiCamera.update();
            fontCamera.update();

            // rotate world
            worldSprite.setRotation(worldSprite.getRotation() + .25f);

            if (worldSprite.getRotation() > 360)
                worldSprite.setRotation(0);

            // move title letters
            if (titleBounceUp)
                titleBounce += .0075f;
            else
                titleBounce -= .0075f;

            if (titleBounce > 1)
                titleBounceUp = false;
            if (titleBounce < -1)
                titleBounceUp = true;

            // change alpha of font
            if (fontAlphaIn)
                fontAlpha += .01f;
            else
                fontAlpha -= .01f;

            if (fontAlpha >= 1) {
                fontAlpha = 1;
                fontAlphaIn = false;
            }
            else if (fontAlpha <= 0) {
                fontAlpha = 0;
                fontAlphaIn = true;
            }

            fontSmall.setColor(1, 1, 1, fontAlpha);

            // check for key presses
            // if any key is pressed or the screen is tapped, start the game
            if (!mouseKeyDown) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                    exitScreen = true;
                }
            } else {
                if (!Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) && !Gdx.input.isTouched())
                    mouseKeyDown = false;
            }

            { // TRANSITIONS
                // update transition sequencer
                transitioner.update();

                // check for transitions
                if (enterScreen) {
                    transitioner.setFrameSpeed(1f);

                    if (!transitioner.isOut())
                        transitioner.setTransition(-1);
                    else
                        enterScreen = false;
                }
                if (exitScreen) {
                    transitioner.setFrameSpeed(.75f);

                    if (!transitioner.isIn())
                        transitioner.setTransition(1);
                    else {
                        superGame.setScreen(new PixelLevels(this.superGame));
                        superGame.dispose();
                        return; // RETURN BECAUSE SINCE EVERYTHING HAS BEEN DISPOSED OF, IT WILL CRASH IF THE RENDERING IS RUN
                    }
                }
            }
        }

        { // RENDERING
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // start rendering
            spriteBatch.begin();

            // set the projection matrix to the gui
            spriteBatch.setProjectionMatrix(guiCamera.combined);

            // star background
            starBackground.draw(spriteBatch);

            // draw the world
            worldSprite.draw(spriteBatch);

            // draw black bars
            black.setPosition(0, 0);
            black.draw(spriteBatch);
            black.setPosition(0, guiCamera.viewportHeight - black.getHeight());
            black.draw(spriteBatch);

            // draw the title
            spriteBatch.draw(title, guiCamera.viewportWidth / 2 - title.getWidth() / 2f, guiCamera.viewportHeight - title.getHeight() - 10 + titleBounce);

            // set the projection matrix to the font camera
            spriteBatch.setProjectionMatrix(fontCamera.combined);

            // draw the font
            if (Gdx.app.getType() == Application.ApplicationType.Android)
                fontSmall.draw(spriteBatch, "TAP TO START", 0, fontCamera.viewportHeight / 2f - 70, fontCamera.viewportWidth, Align.center, false);
            else if (Gdx.app.getType() == Application.ApplicationType.Desktop)
                fontSmall.draw(spriteBatch, "PRESS ANY KEY", 0, fontCamera.viewportHeight / 2f - 70, fontCamera.viewportWidth, Align.center, false);

            // draw the transitions
            spriteBatch.setProjectionMatrix(guiCamera.combined);

            transitioner.render(spriteBatch);

            // stop rendering
            spriteBatch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        guiViewport.update(width, height);
        fontViewport.update(width, height);
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
        fontSmall.dispose();
        fontGenerator.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        exitScreen = true;
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
