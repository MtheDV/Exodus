package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

class PixelEnemy {

    private Sprite enemySprite;
    private float animationCount;

    private int dead;
    private boolean showDeathAnimation;

    private Body body;

    private boolean moveRight;

    private int width, height;

    private boolean above;

    PixelEnemy(float x, float y, float width, float height, String enemyType, World physicsWorld) {
        // set the width and height
        this.height = (int)height;
        this.width = (int)width;
        // set the sprite of the puzzle box
        enemySprite = new Sprite(new Texture(Gdx.files.internal("Images/Enemies/enemy" + enemyType + ".png")));
        // region
        enemySprite.setSize(width, height);
        enemySprite.setRegion(0, 0, width, height);
        // set the position
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
        shape.setAsBox((width - 1f) / 2f, height / 2f); // set the shape
        FixtureDef fixtureDef = new FixtureDef();   // create the fixture definition
        fixtureDef.shape      = shape;              // define the shape
        fixtureDef.friction   = 3f;                 // define the friction
        fixtureDef.density    = 3f;                 // define the density
        fixtureDef.filter.groupIndex = -5;
        body.createFixture(fixtureDef);             // create the fixture definition to the body
        body.setFixedRotation(true);                // fixed rotation

        // back and forth sensor and killer sensor
        shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 3f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape    = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.groupIndex = -5;
        body.createFixture(fixtureDef);

        // player kill sensor
        shape = new PolygonShape();
        shape.setAsBox((enemySprite.getWidth()) / PIXELS_TO_METERS * SCREEN_RATIO / 2f, (1f) / PIXELS_TO_METERS * SCREEN_RATIO / 2f,
                new Vector2(0 / PIXELS_TO_METERS * SCREEN_RATIO, (enemySprite.getHeight() / 2 - (.25f)) / PIXELS_TO_METERS * SCREEN_RATIO), 0);
        fixtureDef = new FixtureDef();
        fixtureDef.shape    = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.groupIndex = -5;
        body.createFixture(fixtureDef);

        shape.dispose(); // dispose of what you can

        moveRight = (int)(Math.random() * 2) == 1;
        dead = 0;
        showDeathAnimation = false;

        above = false;
    }

    void update(PixelPlayer player) {
        enemySprite.setRegion(width * (int)animationCount, height * dead, width, height);
        enemySprite.setPosition(body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO - (enemySprite.getWidth() / 2),
                body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO  - (enemySprite.getHeight() / 2));

        if (moveRight)
            enemySprite.setFlip(true, false);
        else
            enemySprite.setFlip(false, false);

        checkDead();
        moveEnemy();

        // check if the player is above this enemy and adjust
        float difference = (player.getVelocityY() * PIXELS_TO_METERS / SCREEN_RATIO) / 25f;
        above = (player.getSprite().getY()) >= (enemySprite.getY() + enemySprite.getHeight() + difference);
    }

    private void checkDead() {
        if (dead == 1) {
            body.setActive(false);
            showDeathAnimation = true;
        }
        else
            body.setActive(true);
    }

    private void moveEnemy() {
        if (dead == 0) {
            if (moveRight)
                body.setLinearVelocity(8, 0);
            else
                body.setLinearVelocity(-8, 0);

            animationCount += .05f;
            if (animationCount >= 2)
                animationCount = 0;
        } else {
            if (showDeathAnimation) {
                animationCount += .2f;
                if (animationCount >= 3) {
                    animationCount = 2;
                    showDeathAnimation = false;
                }
            }
        }
    }

    void setDead(boolean dead) {
        if (dead) {
            this.dead = 1;
            showDeathAnimation = true;
            animationCount = 0;
        }
        else
            this.dead = 0;
    }

    void render(SpriteBatch spriteBatch) {
        enemySprite.draw(spriteBatch);
    }

    void changeDirection(boolean moveRight) {
        this.moveRight = moveRight;
    }

    Boolean getDirection() { return moveRight; }

    Body getBody() { return body; }

    Sprite getSprite() { return enemySprite; }

    Boolean isAbove() { return above; }

    void dispose() {
        enemySprite.getTexture().dispose();
    }
}
