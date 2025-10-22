package dataaccess;

import model.AuthData;
import java.util.Collection;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    Collection<AuthData> listAuths() throws DataAccessException;
    void clear() throws DataAccessException;
}
