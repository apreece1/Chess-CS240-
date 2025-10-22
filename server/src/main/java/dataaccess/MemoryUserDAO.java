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
        var auth = auths.get(username);
        if (auth == null) {
            throw new DataAccessException("Auth token not found");
        }
        return auth;
    }

    @Override
    public void deleteUser(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found");
        }
        auths.remove(authToken);
    }

    @Override
    public Collection<AuthData> listAuths() throws DataAccessException {
        return auths.values();
    }

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }
}
