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
 * TODO: CREATE BETTER IMAGES FOR THE MENU, ADD THE WORLD SELECTION BOX, CREATE A BETTER MORE DETAILED WORLD
 */

class PixelMenu implements Screen, GestureDetector.GestureListener {

    // sprite batch
    private SpriteBatch spriteBatch;

    // sprite for the world
    private Sprite worldSprite;
    private Sprite starBackground;
    private Sprite black;

    // world sprite transition elements
    private float worldAngle;
    private boolean worldSpinToLevel;
    private static int worldDestinationsPick = 0;
    private static float[] worldAngleDestinations = {270, 0, 90, 180};
    private boolean worldPick;

    // world pick section
    private boolean moveToNearestWorld;
    private float nearestAngle;

    // title
    private Texture title;
    private float titleBounce;
    private boolean titleBounceUp;

    // fonts
    private FreeTypeFontGenerator fontGenerator;
    private BitmapFont fontSmall;
    private BitmapFont fontLarge;

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

    // mouse position
    private Vector3 mousePos;

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
        guiCamera = new OrthographicCamera(200, 200 / ((float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        guiCamera.position.set(guiCamera.viewportWidth / 2f, guiCamera.viewportHeight / 2f, 0);
        guiViewport = new FitViewport(guiCamera.viewportWidth, guiCamera.viewportHeight, guiCamera);
        guiViewport.apply();
        fontCamera = new OrthographicCamera(800, 800 / ((float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
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

        // world transitions
        worldAngle = 0;
        worldSpinToLevel = false;
        worldPick = false;

        // title
        title = new Texture(Gdx.files.internal("Images/Title/title.png"));
        titleBounceUp = false;

        // transition sequencer
        transitioner = new PixelTransition(guiCamera);
        enterScreen = true;
        exitScreen = false;
        moveToNearestWorld = false;

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

            // update world angle
            worldSprite.setRotation(worldAngle);

            if (!worldSpinToLevel && !worldPick) {
                // update angle
                worldAngle += .25f;

                if (worldAngle > 360)
                    worldAngle = 0;
            }

            // move title letters
            if (titleBounceUp)
                titleBounce += .01f;
            else
                titleBounce -= .01f;

            if (titleBounce > 1.5f)
                titleBounceUp = false;
            if (titleBounce < -1.5f)
                titleBounceUp = true;

            // change alpha of font
            if (fontAlphaIn)
                fontAlpha += .01f;
            else
                fontAlpha -= .01f;

            if (fontAlpha >= 1) {
                fontAlpha = 1;
                fontAlphaIn = false;
            } else if (fontAlpha <= 0) {
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
                if (worldSpinToLevel && !worldPick) {

                    // move the title screen away
                    if (titleBounce <= 100)
                        titleBounce += 2;

                    // update the world angle
                    worldAngle += (worldAngleDestinations[worldDestinationsPick] + worldAngle) * delta;

                    if (worldAngle <= worldAngleDestinations[worldDestinationsPick] + 1 && worldAngle >= worldAngleDestinations[worldDestinationsPick] - 1) {
                        worldAngle = worldAngleDestinations[worldDestinationsPick];
                        worldPick = true;
                        worldSpinToLevel = false;
                    }

                    // if it's above 360, go to 0
                    if (worldAngle > 360)
                        worldAngle = 0;
                }
                if (moveToNearestWorld) {
                    // update the world angle
                    if (worldAngle > nearestAngle)
                        worldAngle += (nearestAngle + worldAngle) * delta;
                    if (worldAngle < nearestAngle)
                        worldAngle -= (nearestAngle + worldAngle) * delta;

                    if (worldAngle <= nearestAngle + 1 && worldAngle >= nearestAngle - 1) {
                        worldAngle = nearestAngle;
                        moveToNearestWorld = false;
                    }

                    // if it's above 360, go to 0
                    if (worldAngle > 360)
                        worldAngle = 0;
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
                fontSmall.draw(spriteBatch, "TAP TO START", 0, 50, fontCamera.viewportWidth, Align.center, false);
            else if (Gdx.app.getType() == Application.ApplicationType.Desktop)
                fontSmall.draw(spriteBatch, "PRESS ANY KEY", 0, 50, fontCamera.viewportWidth, Align.center, false);

            // change the projection to the gui camera
            spriteBatch.setProjectionMatrix(guiCamera.combined);

            // draw the transitions
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
        if (!worldPick)
            worldSpinToLevel = true;
        else
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
        // update the mouse
        mousePos = new Vector3(x, y, 0);
        guiCamera.unproject(mousePos, guiViewport.getScreenX(), guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());

        if (worldPick) {
            worldAngle -= deltaX / 2;
        }

        if (worldAngle > 360)
            worldAngle = 0;

        moveToNearestWorld = false;

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        // move to nearest angle
        moveToNearestWorld = true;
        for (int i = 0; i < worldAngleDestinations.length; i++) {
            if (worldAngle <= worldAngleDestinations[i] + (360 / worldAngleDestinations.length / 2) && worldAngle >= worldAngleDestinations[i] - (360 / worldAngleDestinations.length / 2))
                nearestAngle = worldAngleDestinations[i];
            System.out.println(nearestAngle);
        }
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
