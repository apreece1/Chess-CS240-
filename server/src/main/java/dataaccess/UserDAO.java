package dataaccess;

import model.UserData;
import java.util.Collection;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;

}
