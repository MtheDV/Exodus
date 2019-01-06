package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class PixelWorlds extends PixelLevelData {

    private Texture levelSelectBackground;

    private int worldID;

    PixelWorlds(int world) {
        // initialize the levels
        super(world);

        // set the id
        worldID = world;

        // set the scrolling background for the world
        levelSelectBackground = new Texture(Gdx.files.internal("Images/Tilemap/scrollingbackgroundWorld" + (world + 1) + ".png"));
    }

    Texture getLevelSelectBackground() { return levelSelectBackground; }

    int getWorldID() { return worldID; }

    void dispose() {
        levelSelectBackground.dispose();
    }

}
