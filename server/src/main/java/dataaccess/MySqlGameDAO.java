package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.dataaccess.DatabaseManager;
import model.GameData;
import model.ChessGame;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO{

    private final Gson gson;

    public MySqlGameDAO() {
        // Create Gson instance (add type adapters if needed for interfaces)
        this.gson = new GsonBuilder().create();
    }

    
}
