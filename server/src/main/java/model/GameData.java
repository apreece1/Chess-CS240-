package model;

import chess.ChessGame;
import java.util.Objects;

public class GameData  {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;


    public GameData(int gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
    }

    public GameData(int gameID)


    public int getGameID() { return gameID; }
    public String getGameName() { return gameName; }
    public String getWhiteUsername() { return whiteUsername; }
    public void setWhiteUsername(String whiteUsername) { this.whiteUsername = whiteUsername; }
    public String getBlackUsername() { return blackUsername; }
    public void setBlackUsername(String blackUsername) { this.blackUsername = blackUsername; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameData)) return false;
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID);
    }

}

