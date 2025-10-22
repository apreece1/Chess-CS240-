package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO {

    private final Map<String, AuthData> auths = new HashMap<>();


    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public Collection<AuthData> listAuths() throws DataAccessException {
        return List.of();
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
