package com.platypi.exodus;

class Level {

    private String fileName;
    private int levelID;

    Level(String fileName, int levelID) {
        this.fileName = "Images/Tilemap/" + fileName;
        this.levelID  = levelID;
    }

    int getLevelID() { return levelID; }

    String getFileName() { return fileName; }

}
