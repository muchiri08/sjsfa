package com.muchiri.sjsfa.web.controller;

import com.muchiri.sjsfa.users.model.Role;
import com.muchiri.sjsfa.users.model.User;
import com.muchiri.sjsfa.users.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author muchiri
 */
@Named("user")
@RequestScoped
public class UserController {

    private String username;
    private String password;
    private String role;

    @Inject
    private UserService userService;
    @Inject
    private FacesContext facesContext;
    @Inject
    private Flash flash;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String signup() {
        User user = new User(this.username, this.password, Role.valueOf(this.role));
        boolean success = userService.newUser(user);

        if (success) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User created successfully. You can now login.", null));
            flash.setKeepMessages(true);
            return "login?faces-redirect=true";
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while signing up", null));
        return "signup";
    }

}
