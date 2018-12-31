package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

public class PixelPuzzleWall {
    private Body body;

    private boolean wallOn;

    private Sprite puzzleWall;

    private int PUZZLEBUTTON_ID;

    PixelPuzzleWall(float x, float y, float width, float height, World physicsWorld, int PUZZLEBUTTON_ID) {
        puzzleWall = new Sprite(new Texture(Gdx.files.internal("Images/Items/puzzlewall.png")));
        puzzleWall.setPosition(x, y);
        puzzleWall.setSize(width + .1f, height + .1f);
        puzzleWall.setRegion(0, 0, 8, 8);
        // set any pixel coordinates to world coordinates
        x  = x / PIXELS_TO_METERS * SCREEN_RATIO;
        y = y / PIXELS_TO_METERS * SCREEN_RATIO;
        width  = (width - .1f) / PIXELS_TO_METERS * SCREEN_RATIO;
        height = (height - .1f) / PIXELS_TO_METERS * SCREEN_RATIO;
        // create the puzzle wall
        BodyDef bodyDef = new BodyDef();            // create the body definer
        bodyDef.type = BodyDef.BodyType.StaticBody; // set the body to be static
        bodyDef.position.set(x + width / 2f, y + height / 2f); // set the position
        body = physicsWorld.createBody(bodyDef);    // create the body based on the body definer
        PolygonShape shape = new PolygonShape();    // create the shape of the body
        shape.setAsBox(width / 2f, height / 2f); // set the shape
        FixtureDef fixtureDef = new FixtureDef();   // create the fixture definition
        fixtureDef.shape      = shape;              // define the shape
        fixtureDef.friction   = 3f;                 // define the friction
        fixtureDef.density    = 3f;                 // define the density
        body.createFixture(fixtureDef);             // create the fixture definition to the body
        shape.dispose();                            // dispose of what you can
        // other body variables
        body.setFixedRotation(true);
        body.setActive(true);
        // set the puzzle id
        this.PUZZLEBUTTON_ID = PUZZLEBUTTON_ID;
        // set the puzzle wall on
        wallOn = true;
    }

    void update() {
        puzzleWall.setPosition(body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO - (puzzleWall.getWidth() / 2), body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO - (puzzleWall.getHeight() / 2));

        if (wallOn) {
            puzzleWall.setRegion(0, 0, 8, 8);
            body.setActive(true);
        }
        else {
            puzzleWall.setRegion(10, 0, 8, 8);
            body.setActive(false);
        }
    }

    void render(SpriteBatch spriteBatch) {
        puzzleWall.draw(spriteBatch);
    }

    void setWallOn(boolean wallOn) { this.wallOn = wallOn; }

    int getPUZZLEBUTTON_ID() { return PUZZLEBUTTON_ID; }

    Body getBody() { return body; }

    Sprite getSprite() { return puzzleWall; }
}
