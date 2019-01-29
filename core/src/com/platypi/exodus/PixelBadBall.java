package com.platypi.exodus;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

class PixelBadBall {

    private Sprite sprite;
    private Body body;
    private float angle;
    private float speed;

    private float fade;
    private boolean goAway;
    private boolean remove;

    PixelBadBall(float x, float y, float angle, float speed, Sprite ballImage, World physicsWorld) {
        this.sprite = ballImage;

        this.angle = (float)Math.toRadians(angle);
        this.speed = speed;

        fade = 1;
        goAway = false;
        remove = false;

        x  = x / PIXELS_TO_METERS * SCREEN_RATIO;
        y = y / PIXELS_TO_METERS * SCREEN_RATIO;
        // create the puzzle box
        BodyDef bodyDef = new BodyDef();            // create the body definer
        bodyDef.type = BodyDef.BodyType.DynamicBody; // set the body to be static
        bodyDef.position.set(x + (ballImage.getWidth() / 2f) / PIXELS_TO_METERS * SCREEN_RATIO,
                y + (ballImage.getHeight() / 2f) / PIXELS_TO_METERS * SCREEN_RATIO); // set the position
        body = physicsWorld.createBody(bodyDef);    // create the body based on the body definer
        CircleShape shape = new CircleShape();    // create the shape of the body
        shape.setRadius((ballImage.getWidth() / 2) / PIXELS_TO_METERS * SCREEN_RATIO);
        FixtureDef fixtureDef = new FixtureDef();   // create the fixture definition
        fixtureDef.shape      = shape;              // define the shape
        fixtureDef.density    = 1f;
        fixtureDef.isSensor   = true;
        body.createFixture(fixtureDef);             // create the fixture definition to the body
        body.setGravityScale(0f);                   // not affected by gravity
        shape.dispose();                            // dispose of what you can
    }

    void update() {
        if (goAway && !remove) {
            body.setActive(false);
            sprite.setAlpha(fade);
            fade -= .1f;

            if (fade <= 0) {
                fade = 0;
                remove = true;
            }
        }
        // update sprite to body coordinates
        sprite.setPosition((body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO) - sprite.getWidth() / 2,
                (body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO) - sprite.getHeight() / 2);
        sprite.setRotation((float)Math.toDegrees(body.getAngle()));

        // rotate body
        body.setAngularVelocity(.25f);

        // move toward direction given
        body.setLinearVelocity(0, 0);
        body.setLinearVelocity((float)Math.cos((double)angle) * speed, (float)Math.sin((double)angle) * speed);
    }

    void render(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    boolean isRemoved() { return remove; }

    void setGoAway() {
        goAway = true;
    }

    void dispose(World physicsWorld) {
        physicsWorld.destroyBody(body);
        sprite.getTexture().dispose();
    }

    Body getBody() { return body; }

}
