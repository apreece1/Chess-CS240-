package client;

import model.GameData;

public interface GameplayObserver {
    void onLoadGame(GameData game);
    void onNotification(String message);
    void onError(String errorMessage);
}
