package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

class PixelBoss {

    private Sprite bossImage;
    private Body body;

    private float moveSensor;

    private enum BOSS_STAGE {
        STAGE1, STAGE2, STAGE3
    }

    private int healthPoints;

    PixelBoss(float x, float y, int bossID, World physicsWorld) {
        { // CREATING THE SPRITE
            // set the boss sprite
            if (bossID == 1) {
                bossImage = new Sprite(new Texture(Gdx.files.internal("Images/Enemies/Bosses/boss1.png")));
                // set the x and y coordinates
                bossImage.setPosition(x, y);
                // set the size of the sprite so that not the whole sprite sheet is drawn
                bossImage.setSize(32, 32);
                bossImage.setRegion(0, 0, 32, 32);

                healthPoints = 3;

                moveSensor = 0;

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
                    fixtureDef.shape    = shape;
                    fixtureDef.friction = 0f;
                    fixtureDef.density  = 1f;
                    fixtureDef.isSensor = true;
                    // add the fixture definition to the body
                    body.createFixture(fixtureDef);
                    body.setFixedRotation(true);
                    body.setGravityScale(0);
                    // dispose of the shape
                    shape.dispose();
                }
            }
        }
    }

    void render(SpriteBatch spriteBatch) {
        bossImage.draw(spriteBatch);
    }

    void update(float playerX, float playerY) {
        bossImage.setPosition((body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO) - bossImage.getWidth() / 2,
                (body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO) - bossImage.getHeight() / 2);

        moveSensor++;
        if (moveSensor >= 60) {
            System.out.println("X : Y");
            System.out.println((body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO) - playerX);
            System.out.println((body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO) - playerY);
            body.applyLinearImpulse(((body.getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO) - playerX) * -2,
                    ((body.getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO) - playerY) * -2,
                    playerX / PIXELS_TO_METERS * SCREEN_RATIO, playerY / PIXELS_TO_METERS * SCREEN_RATIO, true);
            moveSensor = 0;
        }
    }

    void dispose() {
        bossImage.getTexture().dispose();
    }

}
