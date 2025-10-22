package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO {

    private final Map<String, UserData> users = new HashMap<>();


    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var user = users.get(username);
        if (user == null) {
            throw new DataAccessException("User not found");
        }
        return user;
    }

    @Override
    public Collection<UserData> listUsers() throws DataAccessException {
        return users.values();
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }
}
