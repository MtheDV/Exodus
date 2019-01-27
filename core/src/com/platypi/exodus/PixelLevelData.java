package com.platypi.exodus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

class PixelLevelData {

    private ArrayList<Level> levels;

    private Texture levelSelectBackground;

    private int totalLevels;
    private int completedLevels;

    private boolean unlocked;

    PixelLevelData(int world) {
        levels = new ArrayList<Level>();

        unlocked = (world == 0);

        // add levels based on world
        switch (world + 1) {
            case 1:
                for (int i = 0; i < 3; i++) {
                    if (i == 0)
                        levels.add(
                                new Level("world" + (world + 1) + "-" + (i + 1) + ".tmx", levels.size() + 1, true)
                        );
                    else
                        levels.add(
                                new Level("world" + (world + 1) + "-" + (i + 1) + ".tmx", levels.size() + 1, false)
                        );
                }
                break;
            case 2:
                for (int i = 0; i < 3; i++) {
                    if (i == 0)
                        levels.add(
                                new Level("world" + (world + 1) + "-" + (i + 1) + ".tmx", levels.size() + 1, true)
                        );
                    else
                        levels.add(
                                new Level("world" + (world + 1) + "-" + (i + 1) + ".tmx", levels.size() + 1, false)
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

    private void setCompletedLevels(int completedLevels) { this.completedLevels = completedLevels; }

    void addCompletedLevel() {
        completedLevels++;

        if (completedLevels >= levels.size())
            completedLevels = levels.size();
    }

    boolean isUnlocked() { return unlocked; }
    void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    Texture getLevelSelectBackground() { return levelSelectBackground; }

    int size() { return levels.size(); }

    void read(Preferences prefs, int worldID) {
        setCompletedLevels(prefs.getInteger("completedLevels" + worldID));
        for (int i = 0; i < levels.size(); i++) {
            if (i + 1 <= prefs.getInteger("completedLevels" + worldID)) {
                levels.get(i).setUnlocked(true);
                if (i + 2 < levels.size())
                    levels.get(i + 1).setUnlocked(true);
            }
        }
    }

    void write(Preferences prefs, int worldID) {
        prefs.putInteger("completedLevels" + worldID, getCompletedLevels());
    }

    void dispose() {
        levelSelectBackground.dispose();
    }
}
