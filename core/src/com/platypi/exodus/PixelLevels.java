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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/*
 * TODO: CREATE A LAYOUT FOR THE LEVEL LOADER
 */

public class PixelLevels implements Screen, GestureDetector.GestureListener {
    // sprite batch
    private SpriteBatch spriteBatch;

    // player animation sprite
    private Sprite playerAnimation;

    // scrolling background
    private static float scrollCounter;
    private float scrollDestX;
    private boolean scroll;
    private boolean scrollRight;
    private Sprite black;

    private static int[] scrollDestinations = { 0, 192, 384, 576, 768, 960, 1152};

    // left and right arrow
    private Sprite rightArrow;
    private Sprite leftArrow;

    // back button
    private Sprite backArrow;

    // current world
    static int world;

    // level selected
    static int levelSelected = 1;

    // level locked
    private Sprite lock;
    private boolean shakeLock;
    private int shakeCounter;
    private boolean shakeUp;

    // transition
    private PixelTransition transitioner;
    private boolean fadedIn;
    private boolean fadedOut;
    private boolean transitionOut;
    private boolean newLevel;
    private boolean backToMenu;

    // fonts
    private FreeTypeFontGenerator fontGenerator;
    private BitmapFont fontSmall;
    private BitmapFont fontLarge;
    private float selectAlpha;
    private boolean selectAlphaIn;
    private float selectAlpha2;
    private boolean selectAlphaIn2;
    private boolean selectAlphaInOut;

    // camera and viewport for the screen
    private OrthographicCamera guiCamera;
    private Viewport guiViewport;
    private OrthographicCamera fontCamera;
    private Viewport fontViewport;

    // mouse position
    private Vector3 mousePos;
    // mouse down when entering screen
    private boolean mouseKeyDown;

    // hold the game class to call other screens
    private Game superGame;

    private GestureDetector gestureDetector;

    PixelLevels(Game superGame, int world) {
        // initialize the game class
        super();
        this.superGame = superGame;

        // set the world
        this.world = world;

        // initialize the sprite batch
        spriteBatch = new SpriteBatch();

        // initialize the camera and viewport
        guiCamera   = new OrthographicCamera(200, 200 / ((float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
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
        fontParameter.size = 16;
        fontParameter.color = Color.WHITE;
        fontLarge = fontGenerator.generateFont(fontParameter);
        fontLarge.getData().setScale(2.5f);

        // player animation
        playerAnimation = new Sprite(new Texture(Gdx.files.internal("Images/Player/playerNew.png")));
        // set the size of the sprite so that not the whole sprite sheet is drawn
        playerAnimation.setSize(16, 32);
        playerAnimation.setRegion(0, 0, 8, 16);
        // set the x and y coordinates
        playerAnimation.setPosition(guiCamera.viewportWidth / 2 - playerAnimation.getWidth() / 2, guiCamera.viewportHeight / 2 - playerAnimation.getHeight() / 2);

        // scrolling background
        black = new Sprite(new Texture(Gdx.files.internal("Images/GUI/black.png")));
        black.setSize(guiCamera.viewportWidth, 20);

        // transition sequencer
        transitioner  = new PixelTransition(guiCamera);
        transitionOut = true;
        newLevel      = false;
        backToMenu    = false;
        fadedIn       = false;
        fadedOut      = false;

        // left and right arrows
        leftArrow  = new Sprite(new Texture(Gdx.files.internal("Images/GUI/arrow.png")));
        leftArrow.setFlip(true, false);
        leftArrow.setPosition(15, guiCamera.viewportHeight / 2 - leftArrow.getHeight() / 2);
        rightArrow = new Sprite(new Texture(Gdx.files.internal("Images/GUI/arrow.png")));
        rightArrow.setPosition(guiCamera.viewportWidth - rightArrow.getWidth() - 15, guiCamera.viewportHeight / 2 - leftArrow.getHeight() / 2);

        // back button
        backArrow = new Sprite(new Texture(Gdx.files.internal("Images/GUI/back.png")));
        backArrow.setSize(16, 16);
        backArrow.setRegion(0, 0, 16, 16);
        backArrow.setPosition(6, guiCamera.viewportHeight - backArrow.getHeight() - 4);

        // lock
        lock = new Sprite(new Texture(Gdx.files.internal("Images/GUI/lock.png")));
        lock.setScale(2);
        shakeLock = false;
        shakeUp = true;

        // mouse down
        mouseKeyDown = true;

        // alpha font
        selectAlphaIn    = false;
        selectAlphaIn2   = false;
        selectAlphaInOut = true;

        // scroll
        scroll = false;

        // gesture detector
        gestureDetector = new GestureDetector(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gestureDetector);
    }

    @Override
    public void render(float delta) {
        { // UPDATING
            // update cameras
            guiCamera.update();
            fontCamera.update();

            // buttons back to their up states if the screen is being pressed
            if (!Gdx.input.isTouched()) {
                backArrow.setRegion(0, 0, 16, 16);
            }

            // change alpha of font
            if (selectAlphaInOut) {
                if (selectAlphaIn)
                    selectAlpha += .01f;
                else
                    selectAlpha -= .01f;

                if (selectAlpha >= 1) {
                    selectAlpha = 1;
                    selectAlphaIn = false;
                } else if (selectAlpha <= 0) {
                    selectAlpha = 0;
                    selectAlphaIn = true;
                    selectAlphaInOut = false;
                }
            } else {
                if (selectAlphaIn2)
                    selectAlpha2 += .01f;
                else
                    selectAlpha2 -= .01f;

                if (selectAlpha2 >= 1) {
                    selectAlpha2 = 1;
                    selectAlphaIn2 = false;
                } else if (selectAlpha2 <= 0) {
                    selectAlpha2 = 0;
                    selectAlphaIn2 = true;
                    selectAlphaInOut = true;
                }
            }

            // shake the lock if locked
            if (shakeLock) {
                if (shakeUp)
                    lock.setRotation(lock.getRotation() + 1f);
                else
                    lock.setRotation(lock.getRotation() - 1f);

                if (lock.getRotation() > 3f) {
                    shakeUp = false;
                    shakeCounter++;
                }

                if (lock.getRotation() < -3f) {
                    shakeUp = true;
                    shakeCounter++;
                }

                if (shakeCounter >= 6) {
                    lock.setRotation(0);
                    shakeLock = false;
                    shakeCounter = 0;
                }
            }

            { // TRANSITIONS
                // update transitions
                transitioner.update();

                // check for transitions
                if (transitionOut) {
                    transitioner.setFrameSpeed(1f);

                    if (!fadedOut)
                        transitioner.setTransition(-1);
                    if (transitioner.isOut())
                        fadedOut = true;
                    if (fadedOut) {
                        fadedOut      = false;
                        fadedIn       = false;
                        transitionOut = false;
                    }
                }
                if (newLevel) {
                    transitioner.setFrameSpeed(.75f);

                    if (!fadedIn)
                        transitioner.setTransition(1);
                    if (transitioner.isIn())
                        fadedIn = true;
                    if (fadedIn) {
                        newLevel();
                        return;
                    }
                }
                if (backToMenu) {
                    transitioner.setFrameSpeed(.75f);

                    if (!fadedIn)
                        transitioner.setTransition(1);
                    if (transitioner.isIn())
                        fadedIn = true;
                    if (fadedIn) {
                        superGame.setScreen(new PixelMenu(superGame));
                        this.dispose();
                        return;
                    }
                }
            }

            // check for key presses
            // check if on desktop
            if (!mouseKeyDown) {
                if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.D))
                        levelSelected++;
                    else if (Gdx.input.isKeyJustPressed(Input.Keys.A))
                        levelSelected--;
                    else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                        newLevel();
                        return; // RETURN BECAUSE SINCE EVERYTHING HAS BEEN DISPOSED OF, IT WILL CRASH IF THE RENDERING IS RUN
                    }
                }
            } else if (!Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) && !Gdx.input.isTouched())
                mouseKeyDown = false;

            if (scroll) {
                // update scroll position
                scrollCounter -= (scrollDestX + scrollCounter) * .5f;

                if (!scrollRight) {
                    if (scrollCounter <= -scrollDestX + .2f) {
                        scrollCounter = -scrollDestX;
                        scroll = false;
                    }
                }
                else {
                    if (scrollCounter >= scrollDestX - .2f) {
                        scrollCounter = -scrollDestX;
                        scroll = false;
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

            // scrolling background
            spriteBatch.draw(PixelMenu.worlds.getWorld(world).getLevelSelectBackground(), scrollCounter - 20,
                    guiCamera.viewportHeight / 2 - PixelMenu.worlds.getWorld(world).getLevelSelectBackground().getHeight() / 2f,
                    PixelMenu.worlds.getWorld(world).getLevelSelectBackground().getWidth() * 2, PixelMenu.worlds.getWorld(world).getLevelSelectBackground().getHeight() * 2);

            // black bars on the top and bottom
            black.setPosition(0, 0);
            black.draw(spriteBatch);
            black.setPosition(0, guiCamera.viewportHeight - black.getHeight());
            black.draw(spriteBatch);

            // exit button
            backArrow.draw(spriteBatch);

            // draw the player animation
            playerAnimation.draw(spriteBatch);

            // locked levels button
            for (int i = 0; i < PixelMenu.worlds.getWorld(world).size(); i++) {
                if (!PixelMenu.worlds.getWorld(world).getLevel(i + 1).isUnlocked()) {
                    lock.setPosition((guiCamera.viewportWidth / 2 - lock.getWidth() / 2) + scrollDestinations[i] + scrollCounter, guiCamera.viewportHeight / 2 - lock.getHeight() / 2);
                    lock.draw(spriteBatch);
                }
            }

            // set the projection matrix to the font camera
            spriteBatch.setProjectionMatrix(fontCamera.combined);

            // draw the texts
            // level text
            fontLarge.draw(spriteBatch, "LEVEL " + levelSelected, 0, fontCamera.viewportHeight - 35, fontCamera.viewportWidth, Align.center, false);
            // begin texts
            fontSmall.setColor(1, 1, 1, selectAlpha);
            fontSmall.draw(spriteBatch, "SWIPE LEFT OR RIGHT", 0, 70, fontCamera.viewportWidth, Align.center, false);
            fontSmall.setColor(1, 1, 1, selectAlpha2);
            if (PixelMenu.worlds.getWorld(world).getLevel(levelSelected).isUnlocked())
                fontSmall.draw(spriteBatch, "TAP TO BEGIN", 0, 70, fontCamera.viewportWidth, Align.center, false);
            else
                fontSmall.draw(spriteBatch, "LEVEL LOCKED", 0, 70, fontCamera.viewportWidth, Align.center, false);

            // set the projection matrix to the back to the gui for the transitions
            spriteBatch.setProjectionMatrix(guiCamera.combined);

            transitioner.render(spriteBatch);

            // stop rendering
            spriteBatch.end();
        }
    }

    private void newLevel() {
        superGame.setScreen(new PixelGame(this.superGame));
        this.dispose();
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
        fontLarge.dispose();
        fontGenerator.dispose();
        leftArrow.getTexture().dispose();
        rightArrow.getTexture().dispose();
        backArrow.getTexture().dispose();
        black.getTexture().dispose();
        transitioner.dispose();
        lock.getTexture().dispose();
    }

    // GESTURES //

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        // update the mouse
        mousePos = new Vector3(x, y, 0);
        guiCamera.unproject(mousePos, guiViewport.getScreenX(), guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());
        // button shown as down when the player pressed it
        if ((mousePos.x >= backArrow.getX() && mousePos.x <= backArrow.getX() + backArrow.getWidth()) && (mousePos.y >= backArrow.getY() && mousePos.y <= backArrow.getY() + backArrow.getHeight()))
            backArrow.setRegion(16, 0, 16, 16);

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        // update the mouse
        mousePos = new Vector3(x, y, 0);
        guiCamera.unproject(mousePos, guiViewport.getScreenX(), guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());
        // check each button if the mouse is in each
        if ((mousePos.x >= backArrow.getX() && mousePos.x <= backArrow.getX() + backArrow.getWidth()) && (mousePos.y >= backArrow.getY() && mousePos.y <= backArrow.getY() + backArrow.getHeight())) {
            // go back to menu
            backToMenu = true;
        } else {
            // new level
            if (PixelMenu.worlds.getWorld(world).getLevel(levelSelected).isUnlocked())
                newLevel = true;
            else {
                shakeLock = true;
            }
        }

        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
            if (velocityX < 0) {
                levelSelected++;
                scrollRight = false;
            }
            if (velocityX > 0) {
                levelSelected--;
                scrollRight = true;
            }

            // check if the level selected doesn't go below 0 or higher then the max levels
            if (levelSelected <= 0)
                levelSelected = 1;
            else if (levelSelected > PixelMenu.worlds.getWorld(world).size())
                levelSelected = PixelMenu.worlds.getWorld(world).size();
            else {
                // update scroll information
                scrollDestX = scrollDestinations[levelSelected - 1];
                scroll = true;
            }

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
