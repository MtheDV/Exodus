package com.platypi.exodus;

import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

public class PixelTrap {

    private Body body;

    PixelTrap(float x, float y, float width, float height, World physicsWorld) {
        // set any pixel coordinates to world coordinates
        x  = x / PIXELS_TO_METERS * SCREEN_RATIO;
        y = y / PIXELS_TO_METERS * SCREEN_RATIO;
        width  = width / PIXELS_TO_METERS * SCREEN_RATIO;
        height = height / PIXELS_TO_METERS * SCREEN_RATIO;
        // create the trap
        BodyDef bodyDef = new BodyDef();            // create the body definer
        bodyDef.type = BodyDef.BodyType.StaticBody; // set the body to be static
        bodyDef.position.set(x + width / 2f, y + height / 2f); // set the position
        body = physicsWorld.createBody(bodyDef);    // create the body based on the body definer
        PolygonShape shape = new PolygonShape();    // create the shape of the body
        shape.setAsBox(width / 2f, height / 2f); // set the shape
        FixtureDef fixtureDef = new FixtureDef();   // create the fixture definition
        fixtureDef.shape      = shape;              // define the shape
        fixtureDef.isSensor   = true;               // set as sensor
        body.createFixture(fixtureDef);             // create the fixture definition to the body
        body.setFixedRotation(true);                // fixed rotation
        shape.dispose();                            // dispose of what you can
    }

    Body getBody() {
        return body;
    }

}
