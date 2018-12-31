package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Align;

import static com.platypi.exodus.PixelGame.PIXELS_TO_METERS;
import static com.platypi.exodus.PixelGame.SCREEN_RATIO;

class PixelSign {

    private String text;

    private BitmapFont font;

    private Body body;

    private float alpha;
    private int fade;

    PixelSign(String text, float fontScale, float x, float y, World physicsWorld) {
        // set the text
        this.text = text;

        // fade value
        fade = 0;

        // create the font
        FreeTypeFontGenerator fontLoader = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/pixels.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.color = Color.WHITE;
        fontParameter.size = 16;
        fontParameter.borderWidth = 2f;
        fontParameter.borderColor = Color.BLACK;
        font = fontLoader.generateFont(fontParameter);
        font.getData().setScale(fontScale);

        // create the body
        // set any pixel coordinates to world coordinates
        x  = x / PIXELS_TO_METERS * SCREEN_RATIO;
        y = y / PIXELS_TO_METERS * SCREEN_RATIO;
        // create the puzzle box
        BodyDef bodyDef = new BodyDef();            // create the body definer
        bodyDef.type = BodyDef.BodyType.StaticBody; // set the body to be static
        bodyDef.position.set(x + (8 / PIXELS_TO_METERS * SCREEN_RATIO) / 2f, y + (8 / PIXELS_TO_METERS * SCREEN_RATIO) / 2f); // set the position
        body = physicsWorld.createBody(bodyDef);    // create the body based on the body definer
        PolygonShape shape = new PolygonShape();    // create the shape of the body
        shape.setAsBox((8 / PIXELS_TO_METERS * SCREEN_RATIO) / 2f, (8 / PIXELS_TO_METERS * SCREEN_RATIO) / 2f); // set the shape
        FixtureDef fixtureDef = new FixtureDef();   // create the fixture definition
        fixtureDef.shape      = shape;              // define the shape
        fixtureDef.isSensor   = true;               // makes the button a sensor
        body.createFixture(fixtureDef);             // create the fixture definition to the body
        shape.dispose();                            // dispose of what you can
        // other body variables
        body.setFixedRotation(true);
    }

    void drawFont(SpriteBatch spriteBatch, OrthographicCamera camera) {
        // fade in or out
        switch (fade) {
            case 1:
                fadeIn();
                break;
            case -1:
                fadeOut();
                break;
        }

        // set the alpha
        font.setColor(1, 1, 1, alpha);

        // display
        font.draw(spriteBatch, text, 40, camera.viewportHeight - 80, camera.viewportWidth - 80, Align.center, true);
    }

    void setFade(int fade) {
        this.fade = fade;
    }

    private void fadeIn() {
        if (alpha < 1)
            alpha += .1f;
        if (alpha >= 1) {
            alpha = 1;
            fade  = 0;
        }
    }

    private void fadeOut() {
        if (alpha > 0)
            alpha -= .1f;
        if (alpha <= 0) {
            alpha = 0;
            fade  = 0;
        }
    }

    Body getBody() { return body; }
}
