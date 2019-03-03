package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

class PixelPlayer {

    // direction of the player
    enum Directions {
        LEFT, RIGHT, UP
    }
    private Directions leftRight;

    // actions that the player can do
    private enum Actions {
        JUMPING, WALKING, STANDING, FALLING, PUSHING
    }
    private Actions action;

    // jumping variable
    private boolean jumping;

    // player walk animation variable
    private float walkAnimateCounter;

    // coordinates of the player
    private Sprite sprite;

    // move speed
    private float moveSpeed;

    // player body for box2d physics
    private Body body;

    // create a camera that follows the player
    private OrthographicCamera camera;
    // create a viewport for the camera
    private Viewport viewport;
    // for moving the camera that follows at a delay, lerping a distance
    private float lerp;

    // camera shake variables
    private float shakeDuration;
    private float shakeIntensity;
    private float shakeTimer;

    // camera move variables
    private Vector2 cameraMove;
    private boolean moveCamera;
    private float holdMove;
    private float holdTimer;

    // delta time
    private float delta;

    PixelPlayer(float x, float y, World physicsWorld) {
        { // CREATING THE SPRITE
            // set the player sprite
            sprite = new Sprite(new Texture(Gdx.files.internal("Images/Player/playerNew.png")));
            // set the x and y coordinates
            sprite.setPosition(x, y);
            // set the size of the sprite so that not the whole sprite sheet is drawn
            sprite.setSize(8, 16);
            sprite.setRegion(0, 0, 8, 16);
        }

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
            shape.setAsBox((sprite.getWidth() - 1f) / PIXELS_TO_METERS * SCREEN_RATIO / 2f, (sprite.getHeight() - .1f) / PIXELS_TO_METERS * SCREEN_RATIO / 2f);
            // set the physics properties of the body
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape      = shape;
            fixtureDef.friction   = 0f;
            fixtureDef.density    = 1f;
            // add the fixture definition to the body
            body.createFixture(fixtureDef);
            body.setFixedRotation(true);
            // SENSOR FOR THE GROUND
            // create the shape of the body (square)
            shape = new PolygonShape();
            shape.setAsBox((sprite.getWidth() - .5f) / PIXELS_TO_METERS * SCREEN_RATIO / 2f, (1f) / PIXELS_TO_METERS * SCREEN_RATIO / 2f,
                    new Vector2(.05f / PIXELS_TO_METERS * SCREEN_RATIO, - (sprite.getHeight() / 2 - (1 / 2f)) / PIXELS_TO_METERS * SCREEN_RATIO), 0);
            // set the physics properties of the body
            fixtureDef = new FixtureDef();
            fixtureDef.shape      = shape;
            fixtureDef.friction   = 0f;
            fixtureDef.density    = 1f;
            // add the fixture definition to the body
            body.createFixture(fixtureDef);

            // dispose of what I can so there are as little to no memory leaks
            shape.dispose();
        }

        { // CREATING THE CAMERA AND VIEWPORT
            // set the player camera
            camera = new OrthographicCamera(200, 200 / ((float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
            camera.position.set(x, y - sprite.getHeight() / 2, 0f);
            // set the viewport
            viewport = new FitViewport(camera.viewportWidth, camera.viewportHeight, camera);
            viewport.apply();
            // set the speed that the camera follows the player
            lerp = 5.5f;
        }

        { // OTHER PLAYER VARIABLES
            leftRight = Directions.RIGHT;
            action    = Actions.STANDING;

            // jumping variable
            jumping  = false;

            // walking animation variable
            walkAnimateCounter = 0;

            // move speed
            moveSpeed = 0;

            // move to location variables
            moveCamera = false;
            cameraMove = new Vector2(0, 0);
        }

    }

    void update(MapProperties mapProperties) {
        // update the delta
        delta = Gdx.graphics.getDeltaTime();

        // update the jump
        updateMovement();

        // get the input from the user
        getInput();

        // update the sprite to the body
        updateSprite();

        // update the camera
        cameraUpdate(mapProperties);
    }

    void render(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    private void getInput() {
        // if the w a s d keys are pressed, move the player
        if (Gdx.input.isKeyPressed(Keys.A))
            move(Directions.LEFT, 17);
        if (Gdx.input.isKeyPressed(Keys.D))
            move(Directions.RIGHT, 17);

        // if g key is pressed shake screen
        if (Gdx.input.isKeyJustPressed(Keys.G))
            shakeCamera(360, 5);

        if ((Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.SPACE)) && !jumping)
            move(Directions.UP, 24);
    }

    private void cameraUpdate(MapProperties mapProperties) {
        if (!moveCamera) {
            // set the position based off the x and y coordinates
            camera.position.set(camera.position.x += (sprite.getX() + sprite.getWidth() / 2 - camera.position.x) * lerp * delta,
                    camera.position.y += (sprite.getY() - camera.position.y) * lerp * delta, 0);
        } else {
            // set the position based off the x and y coordinates of the given move
            camera.position.set(camera.position.x += (cameraMove.x - camera.position.x) * lerp * delta,
                    camera.position.y += (cameraMove.y - camera.position.y) * lerp * delta, 0);

            if (camera.position.x >= cameraMove.x - 2 && camera.position.x <= cameraMove.x + 2) {
                if (camera.position.y >= cameraMove.y - 2 && camera.position.y <= cameraMove.y + 2) {
                    holdTimer += 1/delta;

                    if (holdTimer >= holdMove) {
                        holdTimer = 0;
                        holdMove  = 0;
                        moveCamera = false;
                    }
                }
            }
        }

        // if the camera is at the edge of the map stop it from moving that way
        if (camera.position.x - camera.viewportWidth / 2 <= 0)
            camera.position.set(camera.viewportWidth / 2, camera.position.y, 0);
        if (camera.position.x + camera.viewportWidth / 2 >= mapProperties.get("width", Integer.class) * 8)
            camera.position.set(mapProperties.get("width", Integer.class) * 8- camera.viewportWidth / 2, camera.position.y, 0);
        if (camera.position.y - camera.viewportHeight / 2 <= 0)
            camera.position.set(camera.position.x, camera.viewportHeight / 2, 0);
        if (camera.position.y + camera.viewportHeight / 2 >= mapProperties.get("height", Integer.class) * 8)
            camera.position.set(camera.position.x, mapProperties.get("height", Integer.class) * 8- camera.viewportHeight / 2, 0);

        // shake camera if variables require
        if (shakeTimer < shakeDuration) {
            float x = (float) (Math.cos(Math.random() * 360) * 0.9f * shakeIntensity);
            float y = (float) (Math.sin(Math.random() * 360) * 0.9f * shakeIntensity);
            camera.translate(-x, -y);

            shakeTimer += delta;
        }

        // update the camera
        camera.update();
    }

    private void updateSprite() {
        // set the position of the sprite based on the bodies position
        sprite.setPosition(body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO - (sprite.getWidth() / 2f),
                body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO - (sprite.getHeight() / 2f));

        // update based on the animate counter;
        if (action == Actions.STANDING)
            sprite.setRegion(0, 0, 8, 16);
        if (action == Actions.WALKING)
            sprite.setRegion(8 * (int) walkAnimateCounter, 0, 8, 16);
        if (action == Actions.JUMPING)
            sprite.setRegion(8, 16, 8, 16);
        if (action == Actions.FALLING)
            sprite.setRegion(0, 16, 8, 16);
        if (action == Actions.PUSHING)
            sprite.setRegion(8 * (int) walkAnimateCounter, 32, 8, 16);

        // check what direction the player is facing
        if (leftRight == Directions.LEFT)
            sprite.setFlip(true, false);
        else
            sprite.setFlip(false, false);

        // reset the walking animation counter if it is above the max frames
        if (walkAnimateCounter >= 6)
            walkAnimateCounter = 0;
    }

    private void updateMovement() {
        // always apply a moving force to the player
        body.setLinearVelocity(moveSpeed, body.getLinearVelocity().y);

        // check the velocities of x to change the state the player is in
        if (!jumping) {
            if (body.getLinearVelocity().x == 0)
                action = Actions.STANDING;
            else
                action = Actions.WALKING;
        }

        if (jumping) {
            if (body.getLinearVelocity().y > 0)
                action = Actions.JUMPING;
            if (body.getLinearVelocity().y < -10)
                action = Actions.FALLING;
        }

        // stop player moving left or right if they're not pressing a key
        if (!Gdx.input.isKeyPressed(Keys.A) || !Gdx.input.isKeyPressed(Keys.D))
            moveSpeed = 0;
    }

    void move(Directions direction, float amount) {
        // move the body based on the given direction
        if (direction == Directions.RIGHT) {
            //body.setLinearVelocity(amount * dashSpeed, body.getLinearVelocity().y);
            leftRight = Directions.RIGHT;
            // set the speed
            moveSpeed = amount;
            // add to the walking counter for animation
            walkAnimateCounter += 0.25f;
        }
        if (direction == Directions.LEFT) {
            //body.setLinearVelocity(-amount * dashSpeed, body.getLinearVelocity().y);
            leftRight = Directions.LEFT;
            // set the speed
            moveSpeed = -amount;
            // add to the walking counter for animation
            walkAnimateCounter += 0.25f;
        }
        if (direction == Directions.UP) {
            jumping = true;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.setLinearVelocity(body.getLinearVelocity().x, amount);
        }
    }

    void shakeCamera(float duration, float intensity) {
        shakeTimer = 0;
        shakeDuration = duration / 1000f;
        shakeIntensity = intensity;
    }

    void moveCamera(Vector2 cameraMove, float holdMove) {
        moveCamera = true;
        this.cameraMove = cameraMove;
        this.holdMove   = holdMove;
    }

    void zoomCamera(float zoom) {
        camera.zoom = zoom;
    }

    void resetJumping() {
        this.jumping = false;
    }

    boolean getJumping() {
        return jumping;
    }

    float getVelocityY() { return body.getLinearVelocity().y; }

    OrthographicCamera getCamera() {
        return camera;
    }

    Body getBody() {
        return body;
    }

    Sprite getSprite() { return sprite; }

    void resize(int width, int height) {
        viewport.update(width, height);
    }

    void dispose() {
        sprite.getTexture().dispose();
    }
}
