package com.platypi.exodus;

import java.util.ArrayList;

class PixelLevelData {

    private ArrayList<Level> levels;

    PixelLevelData() {
        levels = new ArrayList<Level>();
        levels.add(
                new Level("introMapOne.tmx", levels.size() + 1)
        );
        levels.add(
                new Level("platformerMap.tmx", levels.size() + 1)
        );
        levels.add(
                new Level("platformerMap.tmx", levels.size() + 1)
        );
    }

    Level getLevel(int levelID) {
        for (Level level : levels)
            if (level.getLevelID() == levelID)
                return level;
        return null;
    }

    int size() { return levels.size(); }

}
