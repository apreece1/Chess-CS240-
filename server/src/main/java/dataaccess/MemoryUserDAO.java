package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class MemoryUserDAO implements AuthDAO {

    private final Map<String, AuthData> auths = new HashMap<>();


    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (auths.containsKey(auth.authToken())){
            throw new DataAccessException("Auth token already exists");
        }
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var auth = auths.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Auth token not found");
        }
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
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
