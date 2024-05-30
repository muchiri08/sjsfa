package com.muchiri.sjsfa.users.service;

import com.muchiri.sjsfa.DBConnection;
import com.muchiri.sjsfa.users.model.User;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.sql.SQLException;

/**
 *
 * @author muchiri
 */
public class UserService {

    @Inject
    private Pbkdf2PasswordHash passwordHasher;

    public boolean newUser(User user) {
        DBConnection connection = DBConnection.getInstance();
        String query = "INSERT INTO app_user(username, password, role) VALUES(?, ?, ?)";
        try (var conn = connection.get(); var ps = conn.prepareStatement(query)) {
            ps.setString(1, user.getUsername());
            var passwordHash = passwordHasher.generate(user.getPassword().toCharArray());
            ps.setString(2, passwordHash);
            ps.setString(3, user.getRole().name());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            //Never do this. You should handle exceptions.
            e.printStackTrace();
            return false;
        }
    }
}
