package com.muchiri.sjsfa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 *
 * @author muchiri
 */
@ApplicationScoped
public class AppIdentityStore implements IdentityStore {

    @Inject
    private Pbkdf2PasswordHash passwordHasher;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof UsernamePasswordCredential)) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        var upc = (UsernamePasswordCredential) credential;
        String username = upc.getCaller();
        String password = upc.getPasswordAsString();
        System.out.println("credential password: " + password);

        switch (verify(username, password)) {
            case 0: //when validation fails
                return CredentialValidationResult.INVALID_RESULT;
            case 1: //when validation is succesful
                String userRole = getUserRole(username);
                return new CredentialValidationResult(username, Set.of(userRole));
            case -1: //when there was sql error during validation
                //Never do this
                throw new RuntimeException("something went wrong when verifying the user");
            default:
                //If it reached here you are cursed
                throw new IllegalArgumentException("something went wrong;");
        }
    }

    private int verify(String username, String password) {
        DBConnection connection = DBConnection.getInstance();
        String query = "SELECT password FROM app_user WHERE username = ?";
        try (var conn = connection.get(); var ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return 0;
            }

            String dbPassword = rs.getString("password");
            if (!passwordHasher.verify(password.toCharArray(), dbPassword)) {
                return 0;
            }
            return 1;

        } catch (SQLException e) {
            //Doing nothing
            e.printStackTrace();
            return -1;
        }
    }

    private String getUserRole(String username) {
        DBConnection connection = DBConnection.getInstance();
        try (var conn = connection.get(); var ps = conn.prepareStatement("SELECT role FROM app_user WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }

            return null;

        } catch (SQLException e) {
            //Doing nothing
            e.printStackTrace();
            return null;
        }
    }

}
