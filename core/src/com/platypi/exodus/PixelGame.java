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
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/*
 * TODO: ADD ENEMIES, CHANGE TEXTURE PACK, CREATE MORE LEVELS, BETTER TRANSITION ANIMATION, BETTER PAUSE MENU
 *
 * TODO: **SAVE STATES! SAVE WHEN THE USER LEAVES THE GAME, SAVE WHEN THE PLAYER LEAVES THE LEVEL, ETC...**
 */

public class PixelGame implements Screen, GestureDetector.GestureListener {

    // create a batch
    private SpriteBatch spriteBatch;

    // create a player class
    private PixelPlayer player;

    // new level initiators
    private boolean setNewLevel;
    private boolean finishedLevel;
    private boolean fadedIn;
    private boolean fadedOut;
    private boolean resetLevel;
    private boolean toLevelSelect;

    // create the map class
    private PixelMap maps;

    // camera class for the gui
    private OrthographicCamera guiCamera;
    private Viewport guiViewport;
    // move player buttons
    private Sprite leftButton;
    private Sprite rightButton;
    private Sprite upButton;
    // pause button
    private Sprite pauseButton;
    // camera class for the gui
    private OrthographicCamera fontCamera;
    private Viewport fontViewport;
    private BitmapFont pauseFont;

    // mouse coordinates
    private Vector3 mousePos;

    // transition sequencer
    private PixelTransition transitioner;

    // pause menu
    private boolean pause;
    private Sprite pauseBackground;
    private Sprite homeButton;
    private Sprite restartButton;
    private Sprite resumeButton;
    private Texture scrollPause;
    private float scrollBackground1;
    private float scrollBackground2;

    // physics world for collisions
    private World physicsWorld;
    // debug renderer
//    private Box2DDebugRenderer debugRenderer;
//    private Matrix4 debugMatrix;
    // scale for the world
    final static float SCREEN_RATIO = 15f;
    final static float PIXELS_TO_METERS = 100f;

    // game class to call future screens
    private final Game superGame;
    // gesture detector
    private GestureDetector gestureDetector;
    // level the player is currently playing on
    private static final PixelLevelData levelData = new PixelLevelData();

    // constructor
    PixelGame(Game game) {
        // initialize the game class
        super();
        superGame = game;

        // create the sprite batch
        spriteBatch = new SpriteBatch();

        // create the physics world
        physicsWorld = new World(new Vector2(0, -98f), true);
        // create the debug renderer
//        debugRenderer = new Box2DDebugRenderer();

        // new level initiators
        setNewLevel = true;
        finishedLevel = false;
        resetLevel = false;
        fadedIn = false;
        fadedOut = false;
        toLevelSelect = false;

        // initialize the maps class
        maps = new PixelMap();
        // load the map
        maps.loadMap(new TmxMapLoader().load(levelData.getLevel(PixelLevels.levelSelected).getFileName()), physicsWorld, player);

        // find x and y positions of player from the map
        final float playerX = maps.getPlayerStartX() + 4;
        float playerY = maps.getPlayerStartY() + 8;
        // create the player
        player = new PixelPlayer(playerX, playerY, physicsWorld);

        // cameras for the gui
        guiCamera = new OrthographicCamera(200, 200 / ((float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        guiCamera.position.set(guiCamera.viewportWidth / 2f, guiCamera.viewportHeight / 2f, 0);
        guiViewport = new FitViewport(guiCamera.viewportWidth, guiCamera.viewportHeight, guiCamera);
        guiViewport.apply();
        // cameras for the fonts
        fontCamera = new OrthographicCamera(800, 800 / ((float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        fontCamera.position.set(fontCamera.viewportWidth / 2f, fontCamera.viewportHeight / 2f, 0);
        fontViewport = new FitViewport(fontCamera.viewportWidth, fontCamera.viewportHeight, fontCamera);
        fontViewport.apply();

        // set the transition sequence
        transitioner = new PixelTransition(guiCamera);

        // gui buttons
        leftButton = new Sprite(new Texture(Gdx.files.internal("Images/GUI/buttonarrow.png")));
        leftButton.setSize(24, 24);
        leftButton.setRegion(0, 0, 24, 24);
        leftButton.setPosition(5, 5);
        leftButton.setFlip(true, false);
        leftButton.setAlpha(.3f);
        rightButton = new Sprite(new Texture(Gdx.files.internal("Images/GUI/buttonarrow.png")));
        rightButton.setSize(24, 24);
        rightButton.setRegion(0, 0, 24, 24);
        rightButton.setPosition(39, 5);
        rightButton.setAlpha(.3f);
        upButton = new Sprite(new Texture(Gdx.files.internal("Images/GUI/buttonarrowup.png")));
        upButton.setSize(24, 24);
        upButton.setRegion(0, 0, 24, 24);
        upButton.setPosition(guiCamera.viewportWidth - 29, 5);
        upButton.setAlpha(.3f);
        pauseButton = new Sprite(new Texture(Gdx.files.internal("Images/GUI/pause.png")));
        pauseButton.setPosition(guiCamera.viewportWidth - pauseButton.getWidth() - 2, guiCamera.viewportHeight - pauseButton.getHeight() - 1);

        // pause menu
        pause = false;
        pauseBackground = new Sprite(new Texture(Gdx.files.internal("Images/GUI/black.png")));
        pauseBackground.setSize(guiCamera.viewportWidth, guiCamera.viewportHeight);
        pauseBackground.setAlpha(.5f);
        pauseBackground.setPosition(0, 0);
        homeButton = new Sprite(new Texture(Gdx.files.internal("Images/GUI/home.png")));
        homeButton.setSize(16, 16);
        homeButton.setRegion(0, 0, 16, 16);
        homeButton.setPosition(guiCamera.viewportWidth / 2 - homeButton.getWidth() / 2 - 4 - homeButton.getWidth(), guiCamera.viewportHeight / 2 - homeButton.getWidth() / 2);
        restartButton = new Sprite(new Texture(Gdx.files.internal("Images/GUI/restart.png")));
        restartButton.setSize(16, 16);
        restartButton.setRegion(0, 0, 16, 16);
        restartButton.setPosition(guiCamera.viewportWidth / 2 - restartButton.getWidth() / 2, guiCamera.viewportHeight / 2 - restartButton.getWidth() / 2);
        resumeButton = new Sprite(new Texture(Gdx.files.internal("Images/GUI/resume.png")));
        resumeButton.setSize(16, 16);
        resumeButton.setRegion(0, 0, 16, 16);
        resumeButton.setPosition(guiCamera.viewportWidth / 2 + resumeButton.getWidth() / 2 + 4, guiCamera.viewportHeight / 2 - resumeButton.getWidth() / 2);
        scrollPause = new Texture(Gdx.files.internal("Images/Tilemap/pauseScrolling.png"));
        scrollBackground1 = 0;
        scrollBackground2 = scrollPause.getWidth();

        // font for pause menu
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/pixels.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.color = Color.WHITE;
        fontParameter.size  = 16;
        pauseFont = fontGenerator.generateFont(fontParameter);

        // WORLD COLLISIONS //
        physicsWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if ((contact.getFixtureA() == player.getBody().getFixtureList().get(1) || contact.getFixtureB() == player.getBody().getFixtureList().get(1)))
                    if (!contact.getFixtureA().isSensor() && !contact.getFixtureB().isSensor())
                        if (player.getVelocityY() <= 0)
                            player.resetJumping();
                for (PixelPuzzleBox pixelPuzzleBox : maps.getPixelPuzzleBoxList())
                    for (PixelPuzzleButton pixelPuzzleButton : maps.getPixelPuzzleButtonsList())
                        if ((contact.getFixtureA().getBody() == pixelPuzzleBox.getBody() && contact.getFixtureB().getBody() == pixelPuzzleButton.getBody())
                                || contact.getFixtureA().getBody() == pixelPuzzleButton.getBody() && contact.getFixtureB().getBody() == pixelPuzzleBox.getBody())
                            pixelPuzzleButton.setDown(true);
                for (PixelExit pixelExit : maps.getPixelExitList())
                    if ((contact.getFixtureA().getBody() == player.getBody() && contact.getFixtureB().getBody() == pixelExit.getBody())
                            || (contact.getFixtureA().getBody() == pixelExit.getBody() && contact.getFixtureB().getBody() == player.getBody()))
                        finishedLevel = true;
                for (PixelSign pixelSign : maps.getPixelSignList())
                    if ((contact.getFixtureA().getBody() == player.getBody() && contact.getFixtureB().getBody() == pixelSign.getBody())
                            || (contact.getFixtureA().getBody() == pixelSign.getBody() && contact.getFixtureB().getBody() == player.getBody()))
                        pixelSign.setFade(1);
                for (PixelTrap pixelTrap : maps.getPixelTrapList())
                    if ((contact.getFixtureA().getBody() == player.getBody() && contact.getFixtureB().getBody() == pixelTrap.getBody())
                            || (contact.getFixtureA().getBody() == pixelTrap.getBody() && contact.getFixtureB().getBody() == player.getBody()))
                        resetLevel = true;
            }

            @Override
            public void endContact(Contact contact) {
                for (PixelPuzzleBox pixelPuzzleBox : maps.getPixelPuzzleBoxList())
                    for (PixelPuzzleButton pixelPuzzleButton : maps.getPixelPuzzleButtonsList())
                        if ((contact.getFixtureA().getBody() == pixelPuzzleBox.getBody() && contact.getFixtureB().getBody() == pixelPuzzleButton.getBody())
                                || contact.getFixtureA().getBody() == pixelPuzzleButton.getBody() && contact.getFixtureB().getBody() == pixelPuzzleBox.getBody())
                            pixelPuzzleButton.setDown(false);
                for (PixelSign pixelSign : maps.getPixelSignList())
                    if ((contact.getFixtureA().getBody() == player.getBody() && contact.getFixtureB().getBody() == pixelSign.getBody())
                            || (contact.getFixtureA().getBody() == pixelSign.getBody() && contact.getFixtureB().getBody() == player.getBody()))
                        pixelSign.setFade(-1);
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                for (PixelCollision pixelCollision : maps.getPixelBoxList()) {
                    if ((contact.getFixtureA().getBody() == player.getBody() && contact.getFixtureB().getBody() == pixelCollision.getBody())
                            || contact.getFixtureA().getBody() == pixelCollision.getBody() && contact.getFixtureB().getBody() == player.getBody()) {
                        if (pixelCollision.isOneWay()) {
                            if (player.getVelocityY() <= 0 && player.getBody().getFixtureList().get(1).getBody().getPosition().y >= pixelCollision.getBody().getPosition().y + pixelCollision.getHeight())
                                // moving in the upward direction toward the ledge
                                contact.setEnabled(true);
                            else
                                contact.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        // WORLD COLLISIONS //

        // set gesture detector
        gestureDetector = new GestureDetector(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gestureDetector);
    }

    @Override
    public void render(float delta) {

        // UPDATING SEQUENCE //
        {
            // get the delta
            delta = Gdx.graphics.getDeltaTime();

            // update the gui camera
            guiCamera.update();
            fontCamera.update();

            { // WORLD UPDATING
                if (!pause) {
                    // update the physics world
                    physicsWorld.step(delta, 6, 2);

                    // update the player
                    if (!resetLevel)
                        player.update(maps.getMainMapProperties());

                    // update the map
                    maps.update(player.getSprite().getX(), player.getSprite().getY());

                    if (Gdx.input.isKeyJustPressed(Input.Keys.P))
                        pause = true;

                    // change buttons back to their up state
                    leftButton.setRegion(0, 0, 24, 24);
                    leftButton.setFlip(true, false);
                    rightButton.setRegion(0, 0, 24, 24);
                    upButton.setRegion(0, 0, 24, 24);

                    // check if on android
                    if (Gdx.app.getType() == Application.ApplicationType.Android) {
                        // loop through a max of 5 fingers
                        for (int i = 0; i < 5; i++) {
                            // buttons presses
                            if (Gdx.input.isTouched(i)) {
                                // update the mouse
                                mousePos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                                guiCamera.unproject(mousePos, guiViewport.getScreenX(), guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());
                                // check each button if the mouse is in each
                                if ((mousePos.x >= leftButton.getX() && mousePos.x <= leftButton.getX() + leftButton.getWidth()) && (mousePos.y >= leftButton.getY() && mousePos.y <= leftButton.getY() + leftButton.getHeight())) {
                                    player.move(PixelPlayer.Directions.LEFT, 17);
                                    leftButton.setRegion(24, 0, 24, 24);
                                    leftButton.setFlip(true, false);
                                }
                                if ((mousePos.x >= rightButton.getX() && mousePos.x <= rightButton.getX() + rightButton.getWidth()) && (mousePos.y >= rightButton.getY() && mousePos.y <= rightButton.getY() + rightButton.getHeight())) {
                                    player.move(PixelPlayer.Directions.RIGHT, 17);
                                    rightButton.setRegion(24, 0, 24, 24);
                                }
                                if ((mousePos.x >= upButton.getX() && mousePos.x <= upButton.getX() + upButton.getWidth()) && (mousePos.y >= upButton.getY() && mousePos.y <= upButton.getY() + upButton.getHeight())) {
                                    if (!player.getJumping())
                                        player.move(PixelPlayer.Directions.UP, 24);
                                    upButton.setRegion(24, 0, 24, 24);
                                }
                                if ((mousePos.x >= pauseButton.getX() && mousePos.x <= pauseButton.getX() + pauseButton.getWidth()) && (mousePos.y >= pauseButton.getY() && mousePos.y <= pauseButton.getY() + pauseButton.getHeight())) {
                                    pause = true;
                                }
                            }
                        }
                    }
                } else {
                    // set buttons to their up state if your not touching them
                    if (!Gdx.input.isTouched()) {
                        homeButton.setRegion(0, 0, 16, 16);
                        restartButton.setRegion(0, 0, 16, 16);
                        resumeButton.setRegion(0, 0, 16, 16);
                    }

                    // update scroll information
                    scrollBackground1 -= 1f;
                    scrollBackground2 -= 1;

                    if (scrollBackground1 <= -scrollPause.getWidth())
                        scrollBackground1 = scrollPause.getWidth();
                    if (scrollBackground2 <= -scrollPause.getWidth())
                        scrollBackground2 = scrollPause.getWidth();
                }
            }

            { // TRANSITION UPDATING
                // check if the player has died
                if (resetLevel) {
                    // set fade speed
                    transitioner.setFrameSpeed(3f);

                    // set fade in
                    if (!fadedIn)
                        transitioner.setTransition(1);

                    if (transitioner.isIn())
                        fadedIn = true;

                    if (fadedIn) {
                        // restart the level
                        superGame.setScreen(new PixelGame(this.superGame));
                        this.dispose();
                        return;
                    }
                }
                // move to the level selection screen
                if (setNewLevel) {
                    // set fade speed
                    transitioner.setFrameSpeed(1f);

                    // set fade in
                    if (!fadedOut)
                        transitioner.setTransition(-1);

                    if (transitioner.isOut())
                        fadedOut = true;

                    if (fadedOut) {
                        setNewLevel = false;
                        fadedOut = false;
                        fadedIn = false;
                    }
                }
                // move to the next level
                if (toLevelSelect) {
                    // set fade speed
                    transitioner.setFrameSpeed(.75f);

                    // set fade in
                    if (!fadedIn)
                        transitioner.setTransition(1);

                    if (transitioner.isIn())
                        fadedIn = true;

                    if (fadedIn) {
                        // go back to the menu
                        superGame.setScreen(new PixelLevels(this.superGame));
                        this.dispose();
                        return;
                    }
                }
                // move to the level selection screen
                if (finishedLevel) {
                    // set fade speed
                    transitioner.setFrameSpeed(.75f);

                    // set fade in
                    if (!fadedIn)
                        transitioner.setTransition(1);

                    if (transitioner.isIn())
                        fadedIn = true;

                    if (fadedIn) {
                        // check if the next level is available, then go to it
                        if (PixelLevels.levelSelected < levelData.size()) {
                            PixelLevels.levelSelected++; // move to next level
                            superGame.setScreen(new PixelGame(this.superGame));
                            this.dispose();
                        } else {
                            // go back to the menu
                            superGame.setScreen(new PixelLevels(this.superGame));
                            this.dispose();
                        }
                        return;
                    }
                }

                // update the transition sequence
                transitioner.update();
            }
        }
        // ----------------- //

        // DRAWING SEQUENCE //
        {
            // clear the screen
            Gdx.gl20.glClearColor(0, 0, .05f, 1);
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

            { // BACKGROUND RENDERING //
                spriteBatch.setProjectionMatrix(player.getCamera().combined);
                spriteBatch.begin();
                maps.renderBackground(spriteBatch);
                spriteBatch.end();
            }

            { // TILEMAP DRAWING BEHIND THE PLAYER //
                // draw the tilemap
                maps.render(player.getCamera());
            }

            { // WORLD DRAWING //
                // start rendering the batch
                spriteBatch.begin();

                // set the projection matrix to the player's camera
                spriteBatch.setProjectionMatrix(player.getCamera().combined);
                // get the projection matrix for the debug renderer
                //debugMatrix = player.getCamera().combined.scl(PIXELS_TO_METERS / SCREEN_RATIO);

                // draw the map items
                maps.render(spriteBatch);

                // draw the player
                player.render(spriteBatch);

                spriteBatch.end();
            }

            { // TILEMAP DRAWING IN FRONT OF THE PLAYER //
                // draw the tilemap
                maps.renderInFront(player.getCamera());
            }

            { // FONT DRAWING
                // start drawing
                spriteBatch.begin();

                // set the projection matrix
                spriteBatch.setProjectionMatrix(fontCamera.combined);

                // draw fonts
                maps.renderFonts(spriteBatch, fontCamera);

                // stop drawing
                spriteBatch.end();
            }

            { // GUI DRAWING //
                // start drawing
                spriteBatch.begin();

                // set the projection matrix to the gui's camera
                spriteBatch.setProjectionMatrix(guiCamera.combined);

                // pause button
                pauseButton.draw(spriteBatch);

                // movement buttons
                leftButton.draw(spriteBatch);
                rightButton.draw(spriteBatch);
                upButton.draw(spriteBatch);

                { // PAUSE SCREEN
                    if (pause) {
                        pauseBackground.draw(spriteBatch);

                        // scrolling background
                        spriteBatch.draw(scrollPause, scrollBackground1, guiCamera.viewportHeight / 2 - scrollPause.getHeight() / 2f);
                        spriteBatch.draw(scrollPause, scrollBackground2, guiCamera.viewportHeight / 2 - scrollPause.getHeight() / 2f);

                        // to the font camera matrix
                        spriteBatch.setProjectionMatrix(fontCamera.combined);

                        // paused text
                        pauseFont.draw(spriteBatch, "PAUSED", 1, fontCamera.viewportHeight / 2 + 70, fontCamera.viewportWidth, Align.center, false);

                        // back to the gui camera matrix
                        spriteBatch.setProjectionMatrix(guiCamera.combined);

                        // buttons
                        homeButton.draw(spriteBatch);
                        restartButton.draw(spriteBatch);
                        resumeButton.draw(spriteBatch);
                    }
                }

                // render the transition sequence in front of everything
                transitioner.render(spriteBatch);

                // stop rendering the batch
                spriteBatch.end();
            }

            // draw the box2d bodies
            //debugRenderer.render(physicsWorld, debugMatrix);
        }
        // ---------------- //
    }

    @Override
    public void resize(int width, int height) {
        guiViewport.update(width, height);
        player.resize(width, height);
        fontViewport.update(width, height);
    }

    @Override
    public void pause() {
        pause = true;
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        pause();
    }

    @Override
    public void dispose() {
        // batch
        spriteBatch.dispose();
        // classes
        maps.dispose();
        physicsWorld.dispose();
        player.dispose();
        transitioner.dispose();
        // buttons
        leftButton.getTexture().dispose();
        rightButton.getTexture().dispose();
        upButton.getTexture().dispose();
        pauseBackground.getTexture().dispose();
        homeButton.getTexture().dispose();
        restartButton.getTexture().dispose();
        resumeButton.getTexture().dispose();
        scrollPause.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {mousePos = new Vector3(x, y, 0);
        mousePos = new Vector3(x, y, 0);
        guiCamera.unproject(mousePos, guiViewport.getScreenX(), guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());
        // check each button if the mouse is in it
        if ((mousePos.x >= homeButton.getX() && mousePos.x <= homeButton.getX() + homeButton.getWidth()) && (mousePos.y >= homeButton.getY() && mousePos.y <= homeButton.getY() + homeButton.getHeight()))
            homeButton.setRegion(16, 0, 16, 16);
        if ((mousePos.x >= restartButton.getX() && mousePos.x <= restartButton.getX() + restartButton.getWidth()) && (mousePos.y >= restartButton.getY() && mousePos.y <= restartButton.getY() + restartButton.getHeight()))
            restartButton.setRegion(16, 0, 16, 16);
        if ((mousePos.x >= resumeButton.getX() && mousePos.x <= resumeButton.getX() + resumeButton.getWidth()) && (mousePos.y >= resumeButton.getY() && mousePos.y <= resumeButton.getY() + resumeButton.getHeight()))
            resumeButton.setRegion(16, 0, 16, 16);

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        mousePos = new Vector3(x, y, 0);
        guiCamera.unproject(mousePos, guiViewport.getScreenX(), guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());
        // check each button if the mouse is in it
        if ((mousePos.x >= homeButton.getX() && mousePos.x <= homeButton.getX() + homeButton.getWidth()) && (mousePos.y >= homeButton.getY() && mousePos.y <= homeButton.getY() + homeButton.getHeight())) {
            toLevelSelect = true;
        }
        if ((mousePos.x >= restartButton.getX() && mousePos.x <= restartButton.getX() + restartButton.getWidth()) && (mousePos.y >= restartButton.getY() && mousePos.y <= restartButton.getY() + restartButton.getHeight())) {
            resetLevel = true;
        }
        if ((mousePos.x >= resumeButton.getX() && mousePos.x <= resumeButton.getX() + resumeButton.getWidth()) && (mousePos.y >= resumeButton.getY() && mousePos.y <= resumeButton.getY() + resumeButton.getHeight())) {
            pause = false;
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