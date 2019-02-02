package com.platypi.exodus;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

abstract class PixelBoss {

    private Sprite bossImage;
    private Body body;

    private boolean destroy;
    private boolean fullyDestroyed;

    PixelBoss(float x, float y, int width, int height, Sprite bossImage, World physicsWorld) {
        { // CREATING THE SPRITE
            // set the boss sprite
            this.bossImage = bossImage;
            // set the x and y coordinates
            this.bossImage.setPosition(x, y);
            // set the size of the sprite so that not the whole sprite sheet is drawn
            this.bossImage.setSize(width, height);
            this.bossImage.setRegion(0, 0, width, height);

            { // CREATING THE BODY
                // create a body definer
                BodyDef bodyDef = new BodyDef();
                // set the type of body
                bodyDef.type = BodyDef.BodyType.DynamicBody;
                // set the bodies position
                bodyDef.position.set(x / PIXELS_TO_METERS * SCREEN_RATIO, y / PIXELS_TO_METERS * SCREEN_RATIO);
                // create the body based on the body def
                body = physicsWorld.createBody(bodyDef);
                // create the shape of the body (square)
                PolygonShape shape = new PolygonShape();
                shape.setAsBox((bossImage.getWidth()) / PIXELS_TO_METERS * SCREEN_RATIO / 2f,
                        (bossImage.getHeight()) / PIXELS_TO_METERS * SCREEN_RATIO / 2f);
                // set the physics properties of the body
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.friction = 0f;
                fixtureDef.density = 1f;
                fixtureDef.isSensor = true;
                // add the fixture definition to the body
                body.createFixture(fixtureDef);
                // create the top of the boss that gets stepped on
                shape = new PolygonShape();
                shape.setAsBox((bossImage.getWidth()) / PIXELS_TO_METERS * SCREEN_RATIO / 2f, (1f) / PIXELS_TO_METERS * SCREEN_RATIO / 2f,
                        new Vector2(0, (bossImage.getHeight() / 2) / PIXELS_TO_METERS * SCREEN_RATIO), 0);

                // set the physics properties of the body
                fixtureDef = new FixtureDef();
                fixtureDef.shape      = shape;
                fixtureDef.isSensor   = true;
                // add the fixture definition to the body
                body.createFixture(fixtureDef);

                body.setFixedRotation(true);
                body.setGravityScale(0);
                // dispose of the shape
                shape.dispose();
            }

            destroy = false;
            fullyDestroyed = false;
        }
    }

    abstract void render(SpriteBatch spriteBatch);

    abstract void update(float playerX, float playerY, World physicsWorld);

    Sprite getBossImage() {
        return bossImage;
    }

    void setBossImage(Sprite bossImage) {
        this.bossImage = bossImage;
    }

    Body getBody() {
        return body;
    }

    void setBody(Body body) {
        this.body = body;
    }

    void destroy() { destroy = true; }
    void fullyDestroy() { fullyDestroyed = true; }

    boolean isDestroyed() { return destroy; }
    boolean fullyDestroyed() { return fullyDestroyed; }

    void dispose() {
        bossImage.getTexture().dispose();
    }

}