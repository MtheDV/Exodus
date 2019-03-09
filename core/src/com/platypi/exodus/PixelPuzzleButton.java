package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;
class PixelPuzzleButton {

    private boolean down;

    private Body body;

    private Sprite puzzleButton;

    private boolean playSound;

    private int PUZZLEBUTTON_ID;

    PixelPuzzleButton(float x, float y, World physicsWorld, int PUZZLEBUTTON_ID) {
        puzzleButton = new Sprite(new Texture(Gdx.files.internal("Images/Items/puzzlebutton.png")));
        puzzleButton.setPosition(x, y);
        puzzleButton.setSize(8, 8);
        puzzleButton.setRegion(0, 0, 8, 8);
        // set any pixel coordinates to world coordinates
        x  = x / PIXELS_TO_METERS * SCREEN_RATIO;
        y = y / PIXELS_TO_METERS * SCREEN_RATIO;
        // create the puzzle box
        BodyDef bodyDef = new BodyDef();            // create the body definer
        bodyDef.type = BodyDef.BodyType.StaticBody; // set the body to be static
        bodyDef.position.set(x + (8 / PIXELS_TO_METERS * SCREEN_RATIO) / 2f, y + (8 / PIXELS_TO_METERS * SCREEN_RATIO) / 2f); // set the position
        body = physicsWorld.createBody(bodyDef);    // create the body based on the body definer
        PolygonShape shape = new PolygonShape();    // create the shape of the body
        shape.setAsBox((8 / PIXELS_TO_METERS * SCREEN_RATIO) / 2f, (3 / PIXELS_TO_METERS * SCREEN_RATIO) / 2f); // set the shape
        FixtureDef fixtureDef = new FixtureDef();   // create the fixture definition
        fixtureDef.shape      = shape;              // define the shape
        fixtureDef.friction   = 3f;                 // define the friction
        fixtureDef.density    = 3f;                 // define the density
        fixtureDef.isSensor   = true;               // makes the button a sensor
        body.createFixture(fixtureDef);             // create the fixture definition to the body
        shape.dispose();                            // dispose of what you can
        // other body variables
        body.setFixedRotation(true);
        // set the down boolean to false
        down = false;
        // set the puzzle id
        this.PUZZLEBUTTON_ID = PUZZLEBUTTON_ID;
    }

    void update() {
        puzzleButton.setPosition(body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO - (puzzleButton.getWidth() / 2), body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO - (puzzleButton.getHeight() / 2));

        if (down)
            puzzleButton.setRegion(8, 0, 8, 8);
        else
            puzzleButton.setRegion(0, 0, 8, 8);
    }

    void render(SpriteBatch spriteBatch) {
        puzzleButton.draw(spriteBatch);
    }

    void setDown(boolean down) {
        this.down = down;
    }

    Boolean getDown() { return down; }

    void allowedPlaySound(boolean playSound) { this.playSound = playSound; }
    Boolean playSound() { return playSound; }

    int getPUZZLEBUTTON_ID() { return PUZZLEBUTTON_ID; }

    Body getBody() { return body; }

    Sprite getSprite() { return puzzleButton; }
}
