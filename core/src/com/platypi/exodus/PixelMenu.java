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
 * TODO: CREATE A BETTER MORE DETAILED WORLD, MORE WORLDS
 */

class PixelMenu implements Screen, GestureDetector.GestureListener {

    // saving and loading level data
    static Preferences worldPrefs = Gdx.app.getPreferences("World Prefs");

    // sprite batch
    private SpriteBatch spriteBatch;

    // sprite for the world
    private Sprite worldSprite;
    private Sprite starBackground;
    private Sprite black;

    // back button
    private Sprite backArrow;

    // lock
    private Sprite lock;
    private float showLevels;

    // world sprite transition elements
    private float worldAngle;
    private boolean worldSpinToLevel;
    private static int worldDestinationsPick = 0;
    private static float[] worldAngleDestinations = {270, 0, 90, 180};
    private static boolean worldPick = false;
    private float worldUpWidth;
    private float worldDownWidth;
    private boolean worldGrow;
    private boolean backToMenu;

    // world pick section
    private boolean moveToNearestWorld;
    private float nearestAngle;

    // title
    private Texture title;
    private float titleBounce;
    private boolean titleBounceUp;
    private static float titlePositionY;

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

    // mouse position
    private Vector3 mousePos;

    // transition
    private PixelTransition transitioner;
    private boolean enterScreen;
    private boolean exitScreen;

    // worlds
    static PixelWorlds worlds = new PixelWorlds();
    private static boolean loadedWorld = false;

    // hold the game class to call other screens
    private Game superGame;
    // gesture detector
    private GestureDetector gestureDetector;

    PixelMenu(Game superGame) {
        // initialize the game class
        super();
        this.superGame = superGame;

        // load world data if the first time loading the game
        while (!loadedWorld) {
            worlds.read(worldPrefs);
            loadedWorld = true;
        }

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
        fontParameter.size = 16;
        fontParameter.color = Color.WHITE;
        fontParameter.borderGamma = 0f;
        fontParameter.borderWidth = 1f;
        fontParameter.borderColor = Color.BLACK;
        fontLarge = fontGenerator.generateFont(fontParameter);
        fontLarge.getData().setScale(2.5f);

        // font alpha
        fontAlphaIn = true;

        // world sprite
        worldSprite = new Sprite(new Texture(Gdx.files.internal("Images/Title/worldselector.png")));
        worldSprite.setCenter(worldSprite.getWidth() / 2, worldSprite.getHeight() / 2);
        worldSprite.setPosition(guiCamera.viewportWidth / 2 - worldSprite.getWidth() / 2, -worldSprite.getHeight() / 2);

        // lock
        lock = new Sprite(new Texture(Gdx.files.internal("Images/GUI/lock.png")));
        lock.setPosition(guiCamera.viewportWidth / 2 - lock.getWidth() / 2, guiCamera.viewportHeight / 2 - lock.getHeight() / 2);
        lock.setOrigin(lock.getWidth() / 2, -worldSprite.getOriginY());
        showLevels = 1;

        starBackground = new Sprite(new Texture(Gdx.files.internal("Images/Title/starbackground.png")));
        starBackground.setPosition(0, 0);

        black = new Sprite(new Texture(Gdx.files.internal("Images/GUI/black.png")));
        if (worldPick)
            black.setSize(guiCamera.viewportWidth, 25);
        else
            black.setSize(guiCamera.viewportWidth, 20);

        // back button
        backArrow = new Sprite(new Texture(Gdx.files.internal("Images/GUI/back.png")));
        backArrow.setSize(16, 16);
        backArrow.setRegion(0, 0, 16, 16);
        backArrow.setPosition(6, guiCamera.viewportHeight - backArrow.getHeight() - 4);

        // world transitions
        worldAngle = worldAngleDestinations[worldDestinationsPick];
        worldSpinToLevel = false;
        worldUpWidth = 1.1f;
        worldDownWidth = 1;

        // title
        title = new Texture(Gdx.files.internal("Images/Title/title.png"));
        titleBounceUp = false;
        if (titlePositionY == 0)
            titlePositionY = guiCamera.viewportHeight - title.getHeight() - 10;

        // transition sequencer
        transitioner = new PixelTransition(guiCamera);
        enterScreen = true;
        exitScreen = false;
        moveToNearestWorld = false;
        backToMenu = false;

        // unlock worlds
        for (int i = 0; i < worlds.getTotalWorlds(); i++)
            if (i != 0)
                if (worlds.getWorld(i - 1).isUnlocked() && worlds.getWorld(i - 1).getCompletedLevels() == worlds.getWorld(i - 1).getTotalLevels())
                    worlds.getWorld(i).setUnlocked(true);

        // reset the selected level
        PixelLevels.levelSelected = 1;

        // save the game state
        worlds.write(worldPrefs);
        worldPrefs.flush();

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

            // not touching screen
            if (!Gdx.input.isTouched()) {
                worldGrow = false;
                backArrow.setRegion(0, 0, 16, 16);
            }

            // update world angle
            if (worldAngle > 360)
                worldAngle = 0;
            else if (worldAngle < 0)
                worldAngle = 360;

            worldSprite.setRotation(worldAngle);

            if (!worldSpinToLevel && !worldPick) {
                // update angle
                worldAngle += .25f;
            }

            if (moveToNearestWorld) {
                // update the world angle
                if (worldAngle > nearestAngle)
                    worldAngle += (nearestAngle + worldAngle) * delta;
                if (worldAngle < nearestAngle)
                    worldAngle -= (nearestAngle - worldAngle) * delta;

                if (worldAngle <= nearestAngle + 2 && worldAngle >= nearestAngle - 2) {
                    worldAngle = nearestAngle;
                    moveToNearestWorld = false;
                }
            }

            if (worldGrow) {
                if (worldSprite.getScaleX() < worldUpWidth)
                    worldSprite.setScale(worldSprite.getScaleX() + .01f);
                if (showLevels > 0)
                    showLevels -= .1f;
                else
                    showLevels = 0;
            }
            else {
                if (worldSprite.getScaleX() > worldDownWidth)
                    worldSprite.setScale(worldSprite.getScaleX() - .01f);
                if (showLevels < 1)
                    showLevels += .1f;
                else
                    showLevels = 1;
            }

            // move world up and down
            worldSprite.setPosition(guiCamera.viewportWidth / 2 - worldSprite.getWidth() / 2, -worldSprite.getHeight() / 2 + titleBounce);

            // move title letters
            if (titleBounceUp)
                titleBounce += .015f;
            else
                titleBounce -= .015f;

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
                        superGame.setScreen(new PixelLevels(this.superGame, worldDestinationsPick));
                        superGame.dispose();
                        return; // RETURN BECAUSE SINCE EVERYTHING HAS BEEN DISPOSED OF, IT WILL CRASH IF THE RENDERING IS RUN
                    }
                }
                if (worldSpinToLevel && !worldPick && !backToMenu) {
                    // move the title screen away
                    if (titlePositionY <= guiCamera.viewportHeight + title.getHeight() + 10)
                        titlePositionY += 2;

                    // change size of black bar
                    if (black.getHeight() < 25)
                        black.setSize(black.getWidth(), black.getHeight() + 1);

                    // update the world angle
                    if (worldAngleDestinations[worldDestinationsPick] + 180 > 360) {
                        if (worldAngle <= worldAngleDestinations[worldDestinationsPick] + 180 - 360)
                            worldAngle += (worldAngle - worldAngleDestinations[worldDestinationsPick] - 360) * 6 * delta;
                        if (worldAngle >= worldAngleDestinations[worldDestinationsPick] - 180)
                            worldAngle -= (worldAngle - worldAngleDestinations[worldDestinationsPick]) * 6 * delta;
                    }
                    else if (worldAngleDestinations[worldDestinationsPick] - 180 < 0) {
                        if (worldAngle <= worldAngleDestinations[worldDestinationsPick] + 180)
                            worldAngle += (worldAngle - worldAngleDestinations[worldDestinationsPick]) * 6 * delta;
                        if (worldAngle >= worldAngleDestinations[worldDestinationsPick] - 180 + 360)
                            worldAngle -= (worldAngle - worldAngleDestinations[worldDestinationsPick] + 360) * 6 * delta;
                    }
                    else {
                        if (worldAngle <= worldAngleDestinations[worldDestinationsPick] + 180)
                            worldAngle += (worldAngle - worldAngleDestinations[worldDestinationsPick]) * 6 * delta;
                        if (worldAngle >= worldAngleDestinations[worldDestinationsPick] - 180)
                            worldAngle -= (worldAngle - worldAngleDestinations[worldDestinationsPick]) * 6 * delta;
                    }

                    if (worldAngle <= worldAngleDestinations[worldDestinationsPick] + .5 && worldAngle >= worldAngleDestinations[worldDestinationsPick] - .5 &&
                            titlePositionY >= guiCamera.viewportHeight + title.getHeight() + 10 && black.getHeight() >= 25) {
                        worldAngle = worldAngleDestinations[worldDestinationsPick];
                        worldPick = true;
                        worldSpinToLevel = false;
                    }
                }
                if (backToMenu && !worldSpinToLevel) {
                    // move the title screen in
                    if (titlePositionY >= guiCamera.viewportHeight - title.getHeight() - 10)
                        titlePositionY -= 2;

                    // change size of black bar
                    if (black.getHeight() > 20)
                        black.setSize(black.getWidth(), black.getHeight() - 1);

                    if (titlePositionY <= guiCamera.viewportHeight - title.getHeight() - 10 && black.getHeight() <= 20) {
                        titlePositionY = guiCamera.viewportHeight - title.getHeight() - 10;
                        black.setSize(black.getWidth(), 20);
                        backToMenu = false;
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

            // lock
            if (worldPick) {
                for (int i = 0; i < worlds.getTotalWorlds(); i++) {
                    if (!worlds.getWorld(i).isUnlocked()) {
                        lock.setRotation(worldAngle - worldAngleDestinations[i]);
                        lock.draw(spriteBatch);
                    }
                }
            }

            // draw black bars
            black.setPosition(0, 0);
            black.draw(spriteBatch);
            black.setPosition(0, guiCamera.viewportHeight - black.getHeight());
            black.draw(spriteBatch);

            if (worldPick) {
                // exit button
                backArrow.draw(spriteBatch);
            }

            // draw the title
            if (!worldPick)
                spriteBatch.draw(title, guiCamera.viewportWidth / 2 - title.getWidth() / 2f, titlePositionY + titleBounce);

            // set the projection matrix to the font camera
            spriteBatch.setProjectionMatrix(fontCamera.combined);

            // draw the font
            if (!worldPick) {
                fontSmall.draw(spriteBatch, "TAP TO START", 0, 50, fontCamera.viewportWidth, Align.center, false);
            }
            else {
                fontLarge.setColor(1, 1, 1, showLevels);
                // completed levels
                if (worldDestinationsPick < worlds.getTotalWorlds())
                    if (worlds.getWorld(worldDestinationsPick).isUnlocked())
                        fontLarge.draw(spriteBatch, worlds.getWorld(worldDestinationsPick).getCompletedLevels() + "/" + worlds.getWorld(worldDestinationsPick).getTotalLevels(),
                                0, fontCamera.viewportHeight - 150, fontCamera.viewportWidth, Align.center, false);

                fontLarge.setColor(1, 1, 1, 1);
                // text inside the black bars
                fontLarge.draw(spriteBatch, "WORLD " + (worldDestinationsPick + 1), 0, fontCamera.viewportHeight - 35, fontCamera.viewportWidth, Align.center, false);
                fontSmall.draw(spriteBatch, "SELECT A WORLD", 0, 60, fontCamera.viewportWidth, Align.center, false);
            }

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
        fontLarge.dispose();
        worldSprite.getTexture().dispose();
        starBackground.getTexture().dispose();
        black.getTexture().dispose();
        backArrow.getTexture().dispose();
        title.dispose();
        transitioner.dispose();
        fontGenerator.dispose();
        lock.getTexture().dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        // update the mouse
        mousePos = new Vector3(x, y, 0);
        guiCamera.unproject(mousePos, guiViewport.getScreenX(), guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());

        if (worldPick) {
            worldGrow = true;

            // button shown as down when the player pressed it
            if ((mousePos.x >= backArrow.getX() && mousePos.x <= backArrow.getX() + backArrow.getWidth()) && (mousePos.y >= backArrow.getY() && mousePos.y <= backArrow.getY() + backArrow.getHeight()))
                backArrow.setRegion(16, 0, 16, 16);
        }

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (!worldPick) {
            worldSpinToLevel = true;
            worldDestinationsPick = 0;
        }
        else {// check each button if the mouse is in each
            if ((mousePos.x >= backArrow.getX() && mousePos.x <= backArrow.getX() + backArrow.getWidth()) && (mousePos.y >= backArrow.getY() && mousePos.y <= backArrow.getY() + backArrow.getHeight())) {
                // go back to menu
                worldPick = false;
                backToMenu = true;
            } else {
                if (worlds.getWorld(worldDestinationsPick).isUnlocked())
                    exitScreen = true;
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
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        // update the mouse
        mousePos = new Vector3(x, y, 0);
        guiCamera.unproject(mousePos, guiViewport.getScreenX(), guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());

        if (worldPick) {
            // move angle based on speed
            worldAngle -= deltaX / 2.5f;

            // grow sprite to show movement
            worldGrow = true;
        }

        if (worldAngle > 360)
            worldAngle = 0;

        moveToNearestWorld = false;

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        if (worldPick)
            if (worldAngle != worldAngleDestinations[worldDestinationsPick])
                for (int i = 0; i < worldAngleDestinations.length; i++) {
                    if (worldAngleDestinations[i] + (360f / worldAngleDestinations.length / 2) > 360) {
                        if (worldAngle <= worldAngleDestinations[i] + (360f / worldAngleDestinations.length / 2) - 360
                                || worldAngle >= worldAngleDestinations[i] - (360f / worldAngleDestinations.length / 2)) {
                            worldAngle = worldAngleDestinations[i];
                            worldDestinationsPick = i;
                        }
                    }
                    else if (worldAngleDestinations[i] - (360 / worldAngleDestinations.length / 2) < 0) {
                        if (worldAngle <= worldAngleDestinations[i] + (360f / worldAngleDestinations.length / 2)
                                || worldAngle >= worldAngleDestinations[i] - (360f / worldAngleDestinations.length / 2) + 360) {
                            worldAngle = worldAngleDestinations[i];
                            worldDestinationsPick = i;
                        }
                    }
                    else {
                        if (worldAngle <= worldAngleDestinations[i] + (360f / worldAngleDestinations.length / 2)
                                && worldAngle >= worldAngleDestinations[i] - (360f / worldAngleDestinations.length / 2)) {
                            worldAngle = worldAngleDestinations[i];
                            worldDestinationsPick = i;
                        }
                    }
                }

        return true;
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
