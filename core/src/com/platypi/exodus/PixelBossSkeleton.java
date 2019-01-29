package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

class PixelBossSkeleton extends PixelBoss {

    private Array<PixelBadBall> fireBalls;
    private float rotateBullets;

    private int moveSensor;
    private int stopFlyingAt;
    private int stopShootingAt;
    private int amountRotated;

    private float shootBallsTimer;
    private float shootBallsSpeed;

    private Vector2 origin;

    private Stages stages;
    private Action action;

    private float stage;
    private int health;

    enum Stages {
        FLY_AT_PLAYER, SHOOT_FIRE_BALLS, DIE
    }

    enum Action {
        NORMAL, APPEAR, DISAPPEAR, CREATE
    }

    PixelBossSkeleton(float x, float y, World physicsWorld) {
        super(x, y, new Sprite(new Texture(Gdx.files.internal("Images/Enemies/Bosses/boss1.png"))), physicsWorld);
        getBody().setFixedRotation(false);

        origin = new Vector2(x / PIXELS_TO_METERS * SCREEN_RATIO, y / PIXELS_TO_METERS * SCREEN_RATIO);

        moveSensor = 180;

        shootBallsTimer = 0;
        shootBallsSpeed = 3;

        stage = 1;
        health = 1;

        stages = Stages.FLY_AT_PLAYER;
        action = Action.CREATE;
        getBossImage().setScale(0);

        fireBalls = new Array<PixelBadBall>();
    }

    @Override
    void render(SpriteBatch spriteBatch) {
        getBossImage().draw(spriteBatch);

        for (PixelBadBall fireBall : fireBalls)
            fireBall.render(spriteBatch);
    }

    @Override
    void update(float playerX, float playerY, World physicsWorld) {
        getBossImage().setPosition((getBody().getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO) - getBossImage().getWidth() / 2,
                (getBody().getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO) - getBossImage().getHeight() / 2);
        getBossImage().setRotation((float)Math.toDegrees(getBody().getAngle()));

        // update the fireball
        for (int i = 0; i < fireBalls.size; i++) {
            fireBalls.get(i).update();

            if (fireBalls.get(i).isRemoved()) {
                fireBalls.get(i).dispose(physicsWorld);
                fireBalls.removeIndex(i);
            }
        }

        if (health <= 0) {
            destroy();
            action = Action.DISAPPEAR;
            stages = Stages.DIE;
        }

        switch (action) {
            case APPEAR:
                getBossImage().setScale(getBossImage().getScaleX() + .05f);
                if (getBossImage().getScaleX() >= 1) {
                    getBossImage().setScale(1, 1);
                    // all is normal now
                    action = Action.NORMAL;
                }
                break;
            case CREATE:
                getBossImage().setScale(getBossImage().getScaleX() + .01f);
                if (getBossImage().getScaleX() >= 1) {
                    getBossImage().setScale(1, 1);
                    // all is normal now
                    action = Action.NORMAL;
                }
                break;
            case DISAPPEAR:
                getBossImage().setAlpha(getBossImage().getColor().a - .01f);
                if (getBossImage().getColor().a <= 0) {
                    if (isDestroyed()) {
                        getBossImage().setAlpha(0);
                        if (getBody() != null)
                            physicsWorld.destroyBody(getBody());
                        fullyDestroy();
                    } else {
                        getBossImage().setAlpha(0);
                        getBossImage().setScale(0, 0);
                        getBossImage().setAlpha(1f);
                        // appear at origin when gone
                        // change boss placement to it's original spawn area
                        getBody().setLinearVelocity(0, 0);
                        getBody().setAngularVelocity(0);
                        getBody().setTransform(origin, 0);
                        // now appear
                        action = Action.APPEAR;
                    }
                }
                break;
        }

        switch (stages) {
            case FLY_AT_PLAYER:
                if (action == Action.NORMAL) {
                    // rotate
                    getBody().setAngularVelocity(.5f);

                    moveSensor++;
                    if (moveSensor >= 90 * stage) {
                        getBody().setLinearVelocity(0, 0);
                        getBody().applyForceToCenter((((getBody().getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO) - playerX) ) * -150,
                                (((getBody().getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO) - playerY)) * -150, true);
                        moveSensor = 0;
                    }

                    stopFlyingAt++;
                    if (stopFlyingAt >= 500 / stage) {
                        // disappear
                        action = Action.DISAPPEAR;
                        // change stage
                        stages = Stages.SHOOT_FIRE_BALLS;
                        stopFlyingAt = 0;
                    }
                }
                break;
            case SHOOT_FIRE_BALLS:
                if (action == Action.NORMAL) {
                    shootBallsTimer++;
                    if (shootBallsTimer >= 30 * stage) {
                        // sho0t fireballs
                        float nextAngle = rotateBullets;
                        for (int i = 0; i < 3; i++) {
                            fireBalls.add(
                                    new PixelBadBall(getBossImage().getX() + getBossImage().getWidth() / 2, getBossImage().getY() + getBossImage().getHeight() / 2,
                                            nextAngle, shootBallsSpeed, new Sprite(new Texture(Gdx.files.internal("Images/Enemies/fireBall.png"))), physicsWorld)
                            );
                            nextAngle += 360 / 3f;
                        }
                        rotateBullets += 15f * stage;
                        shootBallsTimer = 0;
                    }
                }
                break;
            case DIE:
                getBody().setAngularVelocity(10f);
                break;
        }
    }

    void setAction(Action action) { this.action = action; }
    Action getAction() { return action; }

    void setStages(Stages stages) {
        this.stages = stages;
        if (stages == Stages.FLY_AT_PLAYER) {
            // remove the bullets
            for (PixelBadBall fireBall : fireBalls)
                fireBall.setGoAway();
            // reset the variables
            stopShootingAt = 0;
            amountRotated = 0;
        }
    }
    Stages getStages() { return stages; }

    int getHealth() { return health; }

    void stageUP() {
        stage -= .1f;
        health--;
    }

    Array<PixelBadBall> getFireBalls() { return fireBalls; }
}
