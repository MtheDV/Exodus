package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

public class PixelEnemy {

    private Sprite enemySprite;

    private Body body;

    private float width, height;

    PixelEnemy(float x, float y, float width, float height, World physicsWorld) {
        // set the width and height
        height = 8;
        width = 8;
        // set the sprite of the puzzle box
        enemySprite = new Sprite(new Texture(Gdx.files.internal("Images/Items/puzzlebox.png")));
        enemySprite.setPosition(x, y);
        // set any pixel coordinates to world coordinates
        x  = x / PIXELS_TO_METERS * SCREEN_RATIO;
        y = y / PIXELS_TO_METERS * SCREEN_RATIO;
        width  = (width - .1f) / PIXELS_TO_METERS * SCREEN_RATIO;
        height = (height - .1f) / PIXELS_TO_METERS * SCREEN_RATIO;
        // create the puzzle box
        BodyDef bodyDef = new BodyDef();            // create the body definer
        bodyDef.type = BodyDef.BodyType.DynamicBody; // set the body to be static
        bodyDef.position.set(x + width / 2f, y + height / 2f); // set the position
        body = physicsWorld.createBody(bodyDef);    // create the body based on the body definer
        PolygonShape shape = new PolygonShape();    // create the shape of the body
        shape.setAsBox(width / 2f, height / 2f); // set the shape
        FixtureDef fixtureDef = new FixtureDef();   // create the fixture definition
        fixtureDef.shape      = shape;              // define the shape
        fixtureDef.friction   = 3f;                 // define the friction
        fixtureDef.density    = 3f;                 // define the density
        body.createFixture(fixtureDef);             // create the fixture definition to the body
        body.setFixedRotation(true);                // fixed rotation
        shape.dispose();                            // dispose of what you can
    }

    void update() {
        enemySprite.setPosition(body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO - (enemySprite.getWidth() / 2),
                body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO  - (enemySprite.getHeight() / 2));
    }
}