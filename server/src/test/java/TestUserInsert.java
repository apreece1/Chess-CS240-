package test;

import dataaccess.MySqlUserDAO;
import dataaccess.DataAccessException;
import model.UserData;

public class TestUserInsert {
    public static void main(String[] args) throws DataAccessException {
        MySqlUserDAO dao = new MySqlUserDAO();
        dao.clear(); // clears the table so we start fresh

        UserData testUser = new UserData("testuser", "password123", "test@example.com");
        dao.createUser(testUser);

        boolean exists = dao.getUser("testuser") != null;
        System.out.println("Inserted user? " + exists);
    }
}
