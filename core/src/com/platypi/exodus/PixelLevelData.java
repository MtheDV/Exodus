package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

class PixelLevelData {

    private ArrayList<Level> levels;

    private Texture levelSelectBackground;

    private int totalLevels;
    private int completedLevels;

    PixelLevelData(int world) {
        levels = new ArrayList<Level>();

        // add levels based on world
        switch (world + 1) {
            case 1:
                for (int i = 0; i < 3; i++) {
                    levels.add(
                            new Level("world" + (world + 1) + "-" + (i + 1) + ".tmx", levels.size() + 1)
                    );
                }
                break;
            case 2:
                for (int i = 0; i < 3; i++) {
                    levels.add(
                            new Level("world" + (world + 1) + "-" + (i + 1) + ".tmx", levels.size() + 1)
                    );
                }
                break;

                default: break;
        }

        // level data
        completedLevels = 0;
        totalLevels = levels.size();

        // set the scrolling background for the world
        levelSelectBackground = new Texture(Gdx.files.internal("Images/Tilemap/scrollingbackgroundWorld" + (world + 1) + ".png"));
    }

    Level getLevel(int levelID) {
        for (Level level : levels)
            if (level.getLevelID() == levelID)
                return level;
        return null;
    }

    int getCompletedLevels() { return completedLevels; }
    int getTotalLevels() { return totalLevels; }

    Texture getLevelSelectBackground() { return levelSelectBackground; }

    int size() { return levels.size(); }

    void dispose() {
        levelSelectBackground.dispose();
    }
}
