package com.platypi.exodus;

import java.util.ArrayList;

class PixelLevelData {

    private ArrayList<Level> levels;

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
                break;

                default: break;
        }
    }

    Level getLevel(int levelID) {
        for (Level level : levels)
            if (level.getLevelID() == levelID)
                return level;
        return null;
    }

    int size() { return levels.size(); }

}
