package model;

import chess.ChessGame;

public record GameData {
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;
    ChessGame game;

    public GameData(int gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
    }

    public int getGameID() { return gameID; }
    public String getGameName() { return gameName; }
    public String getWhiteUsername() { return whiteUsername; }
    public void setWhiteUsername(String whiteUsername) { this.whiteUsername = whiteUsername; }
    public String getBlackUsername() { return blackUsername; }
    public void setBlackUsername(String blackUsername)


}

