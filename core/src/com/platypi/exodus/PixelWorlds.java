package com.platypi.exodus;

import java.util.ArrayList;

class PixelWorlds {

    private ArrayList<PixelLevelData> worldLevels;

    private static final int totalWorlds = 2;

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

    void dispose() {
        for (PixelLevelData worlds : worldLevels) {
            worlds.dispose();
        }
    }

}
