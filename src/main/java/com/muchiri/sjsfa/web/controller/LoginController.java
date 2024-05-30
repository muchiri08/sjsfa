package com.muchiri.sjsfa.web.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import static jakarta.security.enterprise.AuthenticationStatus.SEND_CONTINUE;
import static jakarta.security.enterprise.AuthenticationStatus.SEND_FAILURE;
import static jakarta.security.enterprise.AuthenticationStatus.SUCCESS;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author muchiri
 */
@Named("login")
@RequestScoped
public class LoginController {

    private String username;
    private String password;

    @Inject
    private SecurityContext securityContext;
    @Inject
    private FacesContext facesContext;

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

    public void authenticate() {
        switch (processAuthentication()) {
            case SEND_CONTINUE:
                facesContext.responseComplete();
                break;
            case SEND_FAILURE:
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid credentials", null));
                break;
            case SUCCESS:
                try {
                    String home = getExternalContext().getRequestContextPath() + "/app/home";
                    getExternalContext().redirect(home);
                } catch (IOException ex) {
                    System.err.format("error during log in. error message => %s", ex.getMessage());
                    //you redirect to server error page to notify the user
                }
                break;
        }
    }

    private AuthenticationStatus processAuthentication() {
        ExternalContext ctx = getExternalContext();
        return securityContext.authenticate(
                (HttpServletRequest) ctx.getRequest(),
                (HttpServletResponse) ctx.getResponse(),
                AuthenticationParameters.withParams().credential(new UsernamePasswordCredential(username, password)));
    }

    private ExternalContext getExternalContext() {
        return facesContext.getExternalContext();
    }

}
