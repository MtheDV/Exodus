package com.platypi.exodus;

import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

class PixelCollision {

    private Body body;

    private boolean isOneWay;
    private float height;

    PixelCollision(float x, float y, float width, float height, World physicsWorld, boolean isOneWay) {
        // set any pixel coordinates to world coordinates
        x  = x / PIXELS_TO_METERS * SCREEN_RATIO;
        y = y / PIXELS_TO_METERS * SCREEN_RATIO;
        width  = (width - .1f) / PIXELS_TO_METERS * SCREEN_RATIO;
        height = (height - .1f) / PIXELS_TO_METERS * SCREEN_RATIO;
        // create the platform
        BodyDef bodyDef = new BodyDef();            // create the body definer
        bodyDef.type = BodyDef.BodyType.StaticBody; // set the body to be static
        bodyDef.position.set(x + width / 2f, y + height / 2f); // set the position
        body = physicsWorld.createBody(bodyDef);    // create the body based on the body definer
        PolygonShape shape = new PolygonShape();    // create the shape of the body
        shape.setAsBox(width / 2f, height / 2f); // set the shape
        FixtureDef fixtureDef = new FixtureDef();   // create the fixture definition
        fixtureDef.shape      = shape;              // define the shape
        fixtureDef.friction   = 3f;                 // define the friction
        fixtureDef.density    = 1f;                 // define the density
        body.createFixture(fixtureDef);             // create the fixture definition to the body
        shape.dispose();                            // dispose of what you can
        // set whether or not the body is one way
        this.isOneWay = isOneWay;
        this.height = height;
    }

    Body getBody() {
        return body;
    }

    boolean isOneWay() {
        return isOneWay;
    }

    float getHeight() {
        return height;
    }
}
