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

    public GameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game){
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = game;
    }


    public int getGameID() { return gameID; }
    public void setGameID(int gameID) { this.gameID = gameID; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public String getWhiteUsername() { return whiteUsername; }
    public void setWhiteUsername(String whiteUsername) { this.whiteUsername = whiteUsername; }

    public String getBlackUsername() { return blackUsername; }
    public void setBlackUsername(String blackUsername) { this.blackUsername = blackUsername; }

    public ChessGame getGame() { return game;}
    public void setGame(ChessGame game) {this.game = game; }

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

