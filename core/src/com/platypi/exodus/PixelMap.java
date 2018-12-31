package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

class PixelMap {

    private TiledMap mainMap;
    private MapProperties mainMapProperties;
    private TiledMapRenderer mainMapRenderer;

    private ArrayList<PixelCollision> pixelBoxList;
    private ArrayList<PixelPuzzleBox> pixelPuzzleBoxList;
    private ArrayList<PixelPuzzleButton> pixelPuzzleButtonsList;
    private ArrayList<PixelPuzzleWall> pixelPuzzleWallList;
    private ArrayList<PixelExit> pixelExitList;
    private ArrayList<PixelSign> pixelSignList;
    private ArrayList<PixelTrap> pixelTrapList;

    private Texture background;
    private float backgroundX;
    private float backgroundY;

    private float playerStartX, playerStartY;

    private enum Types {
        WALL, PUZZLEBOX, PUZZLEBUTTON, PUZZLEWALL, PLAYERSTART, PLAYEREND, SIGN, TRAP
    }

    PixelMap() {
        // create the body list
        pixelBoxList           = new ArrayList<PixelCollision>();
        pixelPuzzleBoxList     = new ArrayList<PixelPuzzleBox>();
        pixelPuzzleButtonsList = new ArrayList<PixelPuzzleButton>();
        pixelPuzzleWallList    = new ArrayList<PixelPuzzleWall>();
        pixelExitList          = new ArrayList<PixelExit>();
        pixelSignList          = new ArrayList<PixelSign>();
        pixelTrapList          = new ArrayList<PixelTrap>();

        // set the background
        background  = new Texture(Gdx.files.internal("Images/Tilemap/background.png"));
        backgroundX = 0;
        backgroundY = 0;
    }

    void update(float playerX, float playerY) {
        // update the background so it scrolls slowly
        backgroundX = -playerX / 20f;
        backgroundY = -playerY / 35f;

        // update any objects
        for (PixelPuzzleBox puzzleBox : pixelPuzzleBoxList)
            puzzleBox.update();
        for (PixelPuzzleButton puzzleButton : pixelPuzzleButtonsList)
            puzzleButton.update();
        for (PixelPuzzleWall puzzleWall : pixelPuzzleWallList)
            puzzleWall.update();

        // loop through the buttons and the walls to find id's and set active or not
        for (int i = 0; i < pixelPuzzleButtonsList.size(); i++) {
            for (int j = 0; j < pixelPuzzleWallList.size(); j++) {
                if (pixelPuzzleButtonsList.get(i).getPUZZLEBUTTON_ID() == pixelPuzzleWallList.get(i).getPUZZLEBUTTON_ID()) {
                    if (pixelPuzzleButtonsList.get(i).getDown())
                        pixelPuzzleWallList.get(i).setWallOn(false);
                    else
                        pixelPuzzleWallList.get(i).setWallOn(true);
                }
            }
        }
    }

    void renderBackground(SpriteBatch spriteBatch) {
        spriteBatch.draw(background, backgroundX, backgroundY);
    }

    void render(OrthographicCamera cameraView) {
        mainMapRenderer.setView(cameraView);
        mainMapRenderer.render();
    }

    void renderInFront(OrthographicCamera cameraView) {
        mainMapRenderer.setView(cameraView);
        mainMapRenderer.render(new int[]{2});
    }

    void render(SpriteBatch spriteBatch) {
        for (PixelPuzzleBox puzzleBox : pixelPuzzleBoxList)
            puzzleBox.render(spriteBatch);
        for (PixelPuzzleButton puzzleButton : pixelPuzzleButtonsList)
            puzzleButton.render(spriteBatch);
        for (PixelPuzzleWall puzzleWall : pixelPuzzleWallList)
            puzzleWall.render(spriteBatch);
    }

    void renderFonts(SpriteBatch spriteBatch, OrthographicCamera camera) {
        for (PixelSign pixelSign : pixelSignList)
            pixelSign.drawFont(spriteBatch, camera);
    }

    void loadMap(TiledMap newMap, World physicsWorld, PixelPlayer player) {
        // reset the map
        Array<Body> bodies = new Array<Body>();
        physicsWorld.getBodies(bodies);
        for (Body body : bodies)
            if (body != player.getBody())
                physicsWorld.destroyBody(body);

        // clear the lists
        pixelExitList.clear();
        pixelPuzzleWallList.clear();
        pixelPuzzleButtonsList.clear();
        pixelPuzzleBoxList.clear();
        pixelBoxList.clear();
        pixelSignList.clear();
        pixelTrapList.clear();

        // set the map
        mainMap = newMap;
        // set the renderer
        mainMapRenderer = new OrthogonalTiledMapRenderer(mainMap);
        // get the properties
        mainMapProperties = mainMap.getProperties();

        // check the collision walls layer of the tilemap to get the objects within it
        getLayerObjects("collision walls", physicsWorld, Types.WALL);
        // add the puzzle boxes to the world
        getLayerObjects("puzzle boxes", physicsWorld, Types.PUZZLEBOX);
        // add the puzzle buttons to the world
        getLayerObjects("puzzle buttons", physicsWorld, Types.PUZZLEBUTTON);
        // add the puzzle walls to the world
        getLayerObjects("puzzle walls", physicsWorld, Types.PUZZLEWALL);
        // get the player spawn positions
        getLayerObjects("player spawn", physicsWorld, Types.PLAYERSTART);
        // get the end position
        getLayerObjects("exit", physicsWorld, Types.PLAYEREND);
        // get the sign positions
        getLayerObjects("signs", physicsWorld, Types.SIGN);
        // get the trap properties
        getLayerObjects("traps", physicsWorld, Types.TRAP);
    }

    private void createBox(float x, float y, float width, float height, World physicsWorld, boolean isOneWay) {
        // add the body to the list
        pixelBoxList.add(
                new PixelCollision(x, y, width, height, physicsWorld, isOneWay)
        );
    }

    private void createPuzzleBox(float x, float y, World physicsWorld) {
        // add the body to the list
        pixelPuzzleBoxList.add(
                new PixelPuzzleBox(x, y, physicsWorld)
        );
    }

    private void createPuzzleButton(float x, float y, World physicsWorld, int puzzleID) {
        // add the body to the list
        pixelPuzzleButtonsList.add(
                new PixelPuzzleButton(x, y, physicsWorld, puzzleID)
        );
    }

    private void createPuzzleWall(float x, float y, float width, float height, World physicsWorld, int puzzleID) {
        // add the body to the list
        pixelPuzzleWallList.add(
                new PixelPuzzleWall(x, y, width, height, physicsWorld, puzzleID)
        );
    }

    private void createExit(float x, float y, float width, float height, World physicsWorld) {
        // add the body to the list
        pixelExitList.add(
                new PixelExit(x, y, width, height, physicsWorld)
        );
    }

    private void createSign(float x, float y, String text, float fontSize, World physicsWorld) {
        // add a sign to the list
        pixelSignList.add(
                new PixelSign(text, fontSize, x, y, physicsWorld)
        );
    }

    private void createTrap(float x, float y, float width, float height, World physicsWorld) {
        // add a sign to the list
        pixelTrapList.add(
                new PixelTrap(x, y, width, height, physicsWorld)
        );
    }

    private void getLayerObjects(String layerName, World physicsWorld, Types type) {
        // check the collision walls layer of the tilemap to get the objects within it
        if (mainMap.getLayers().get(layerName) != null) {
            for (MapObject worldObjects : mainMap.getLayers().get(layerName).getObjects()) {
                if (worldObjects instanceof RectangleMapObject) {
                    if (type == Types.WALL) {
                        // create a rectangle based on each rectangle object
                        Rectangle collisionRectangle = ((RectangleMapObject) worldObjects).getRectangle();
                        // create a box2d rectangle for the game
                        createBox(collisionRectangle.x, collisionRectangle.y, collisionRectangle.width, collisionRectangle.height, physicsWorld, Boolean.valueOf(String.valueOf(worldObjects.getProperties().get("isOneWay"))));
                    }
                    if (type == Types.PUZZLEBOX) {
                        // create a rectangle based on each rectangle object
                        Rectangle puzzleRectangle = ((RectangleMapObject) worldObjects).getRectangle();
                        // create the puzzle box
                        createPuzzleBox(puzzleRectangle.x, puzzleRectangle.y, physicsWorld);
                    }
                    if (type == Types.PUZZLEBUTTON) {
                        // create a rectangle based on each rectangle object
                        Rectangle puzzleRectangle = ((RectangleMapObject) worldObjects).getRectangle();
                        // create the puzzle button
                        createPuzzleButton(puzzleRectangle.x, puzzleRectangle.y, physicsWorld, Integer.valueOf(String.valueOf(worldObjects.getProperties().get("PUZZLE_ID"))));
                    }
                    if (type == Types.PUZZLEWALL) {
                        // create a rectangle based on each rectangle object
                        Rectangle puzzleRectangle = ((RectangleMapObject) worldObjects).getRectangle();
                        // create the puzzle button
                        createPuzzleWall(puzzleRectangle.x, puzzleRectangle.y, puzzleRectangle.width, puzzleRectangle.height, physicsWorld, Integer.valueOf(String.valueOf(worldObjects.getProperties().get("PUZZLE_ID"))));
                    }
                    if (type == Types.PLAYERSTART) {
                        // create rectangle of the player start position
                        Rectangle startRectangle = ((RectangleMapObject) worldObjects).getRectangle();
                        // set the position
                        playerStartX = startRectangle.x;
                        playerStartY = startRectangle.y;
                    }
                    if (type == Types.PLAYEREND) {
                        // create rectangle of the player start position
                        Rectangle endRectangle = ((RectangleMapObject) worldObjects).getRectangle();
                        // set the position
                        createExit(endRectangle.x, endRectangle.y, endRectangle.width, endRectangle.height, physicsWorld);
                    }
                    if (type == Types.SIGN) {
                        // create rectangle of the player start position
                        Rectangle signRectangle = ((RectangleMapObject) worldObjects).getRectangle();
                        // set the position
                        createSign(signRectangle.x, signRectangle.y, String.valueOf(worldObjects.getProperties().get("text")), Float.valueOf(String.valueOf(worldObjects.getProperties().get("scale"))), physicsWorld);
                    }
                    if (type == Types.TRAP) {
                        // create rectangle of the player start position
                        Rectangle trapRectangle = ((RectangleMapObject) worldObjects).getRectangle();
                        // set the position
                        createTrap(trapRectangle.x, trapRectangle.y, trapRectangle.width, trapRectangle.height, physicsWorld);
                    }
                }
            }
        }
    }

    float getPlayerStartX() { return  playerStartX; }
    float getPlayerStartY() { return  playerStartY; }

    MapProperties getMainMapProperties() { return mainMapProperties; }

    ArrayList<PixelCollision> getPixelBoxList() { return pixelBoxList; }

    ArrayList<PixelPuzzleBox> getPixelPuzzleBoxList() { return pixelPuzzleBoxList; }

    ArrayList<PixelPuzzleButton> getPixelPuzzleButtonsList() { return pixelPuzzleButtonsList; }

    ArrayList<PixelExit> getPixelExitList() { return pixelExitList; }

    ArrayList<PixelSign> getPixelSignList() { return pixelSignList; }

    ArrayList<PixelTrap> getPixelTrapList() { return pixelTrapList; }

    void dispose() {
        mainMap.dispose();
        for (PixelPuzzleBox puzzleBox : pixelPuzzleBoxList)
            puzzleBox.getSprite().getTexture().dispose();
        for (PixelPuzzleButton puzzleButton : pixelPuzzleButtonsList)
            puzzleButton.getSprite().getTexture().dispose();
        for (PixelPuzzleWall puzzleWall : pixelPuzzleWallList)
            puzzleWall.getSprite().getTexture().dispose();
    }

}
