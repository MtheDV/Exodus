package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

class PixelBossWorm extends PixelBoss {

    private boolean moveDirection;
    private int moveCounter;
    private int waitCounter;

    private int health;

    private float stage;
    private Stages stages;
    private Action action;

    enum Stages {
        MOVE_TOWARDS_PLAYER, JUMP, DIE
    }

    enum Action {
        NORMAL, DISAPPEAR
    }

    PixelBossWorm(float x, float y, World physicsWorld) {
        super(x, y, 32, 32, new Sprite(new Texture(Gdx.files.internal("Images/Enemies/Bosses/boss2.png"))), physicsWorld, true);

        moveDirection = false;
        moveCounter = 0;
        waitCounter = 0;

        stage  = 1;
        health = 3;

        stages = Stages.MOVE_TOWARDS_PLAYER;
        action = Action.NORMAL;
    }

    @Override
    void render(SpriteBatch spriteBatch) {
        // flip to the direction it's facing
        if (moveDirection)
            getBossImage().setFlip(true, false);
        else
            getBossImage().setFlip(false, false);

        getBossImage().draw(spriteBatch);
    }

    @Override
    void update(float playerX, float playerY, World physicsWorld) {
        getBossImage().setPosition((getBody().getPosition().x * PIXELS_TO_METERS / SCREEN_RATIO) - getBossImage().getWidth() / 2,
                (getBody().getPosition().y * PIXELS_TO_METERS / SCREEN_RATIO) - getBossImage().getHeight() / 2);
        getBossImage().setRotation((float)Math.toDegrees(getBody().getAngle()));

        if (health <= 0) {
            destroy();
            action = Action.DISAPPEAR;
            stages = Stages.DIE;
        }

        switch (action) {
            case DISAPPEAR:
                getBossImage().setAlpha(getBossImage().getColor().a - .01f);
                if (getBossImage().getColor().a <= 0) {
                    if (isDestroyed()) {
                        getBossImage().setAlpha(0);
                        if (getBody() != null)
                            physicsWorld.destroyBody(getBody());
                        fullyDestroy();
                    }
                }
                break;

                default: break;
        }

        switch (stages) {
            case MOVE_TOWARDS_PLAYER:
                // set the image to the open shell
                getBossImage().setRegion(0, 0, (int)getBossImage().getWidth(), (int)getBossImage().getHeight());
                getBossImage().setOriginCenter();

                if (action == Action.NORMAL) {
                    moveDirection = playerX >= getBossImage().getX() + getBossImage().getWidth() / 2; // if the player is to the right set true else set false

                    waitCounter++;

                    if (waitCounter == (int) (100 * stage)) {
                        if (moveDirection)
                            getBody().applyForceToCenter(13000f / (stage / 1.2f), 0, true); // 13000f
                        else
                            getBody().applyForceToCenter(-13000f / (stage / 1.2f), 0, true); // -13000f
                    }

                    if (waitCounter >= 200 * stage) {
                        if (moveCounter >= (int) (3 / stage)) {
                            getBody().setLinearVelocity(0, 0);
                            action = Action.NORMAL;
                            // go to jumping stage
                            stages = Stages.JUMP;
                            waitCounter = 0;
                            moveCounter = 0;
                        } else {
                            getBody().setLinearVelocity(0, 0);
                            moveCounter++;
                            waitCounter = 0;
                        }
                    }
                }

                break;
            case JUMP:
                // set the image to the first animation
                getBossImage().setRegion((int)getBossImage().getWidth(), 0, (int)getBossImage().getWidth(), (int)getBossImage().getHeight());
                getBossImage().setOriginCenter();

                if (action == Action.NORMAL) {
                    moveDirection = playerX >= getBossImage().getX() + getBossImage().getWidth() / 2; // if the player is to the right set true else set false

                    waitCounter++;

                    if (waitCounter == (int) (50 * stage)) {
                        if (moveDirection)
                            getBody().applyForceToCenter((100f * (playerX - getBossImage().getX())) / stage, 25000, true);
                        else
                            getBody().applyForceToCenter((100f * (playerX - getBossImage().getX())) / stage, 25000, true);
                    }

                    if (waitCounter >= 200 * stage) {
                        if (moveCounter >= 5 * (stage / 1.2)) {
                            getBody().setLinearVelocity(0, getBody().getLinearVelocity().y);
                            action = Action.NORMAL;
                            // go to jumping stage
                            stages = Stages.MOVE_TOWARDS_PLAYER;
                            waitCounter = 0;
                            moveCounter = 0;
                        } else {
                            getBody().setLinearVelocity(0, 0);
                            moveCounter++;
                            waitCounter = 0;
                        }
                    }
                }

                break;
            case DIE:
                getBody().setActive(false);
                break;

                default: break;
        }
    }

    void setAction(Action action) { this.action = action; }
    Action getAction() { return action; }

    void setStages(Stages stages) {
        this.stages = stages;

        moveCounter = 0;
        waitCounter = 0;
    }
    Stages getStages() { return stages; }

    int getHealth() { return health; }

    void stageUP() {
        stage -= .1f;
        health--;
        waitCounter = 0;
        moveCounter = 0;
        System.out.println(stage);
    }
}
