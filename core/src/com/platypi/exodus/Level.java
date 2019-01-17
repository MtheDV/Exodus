package com.platypi.exodus;

class Level {

    private String fileName;
    private int levelID;
    private boolean unlocked;

    Level(String fileName, int levelID, boolean unlocked) {
        this.fileName = "Images/Tilemap/" + fileName;
        this.levelID  = levelID;
        this.unlocked = unlocked;
    }

    int getLevelID() { return levelID; }

    String getFileName() { return fileName; }

    boolean isUnlocked() {
        return unlocked;
    }

    void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

}
