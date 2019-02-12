package com.platypi.exodus;

import com.badlogic.gdx.Preferences;

import java.util.ArrayList;

class PixelWorlds {

    private ArrayList<PixelLevelData> worldLevels;

    private static final int totalWorlds = 4;

    PixelWorlds() {
        // initialize the worlds list
        worldLevels = new ArrayList<PixelLevelData>();

        // initialize the worlds
        for (int i = 0; i < totalWorlds; i++)
            worldLevels.add(new PixelLevelData(i));
    }

    PixelLevelData getWorld(int worldID) {
        return worldLevels.get(worldID);
    }

    int getTotalWorlds() { return totalWorlds; }

    void read(Preferences prefs) {
        for (int i = 0; i < worldLevels.size(); i++)
            worldLevels.get(i).read(prefs, i + 1);
    }

    void write(Preferences prefs) {
        for (int i = 0; i < worldLevels.size(); i++)
            worldLevels.get(i).write(prefs, i + 1);
    }

    void dispose() {
        for (PixelLevelData worlds : worldLevels) {
            worlds.dispose();
        }
    }
}
