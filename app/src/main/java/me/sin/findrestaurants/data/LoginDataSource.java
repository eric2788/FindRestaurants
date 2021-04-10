package me.sin.findrestaurants.data;

import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.data.model.LoggedInUser;
import me.sin.findrestaurants.service.AuthService;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final AuthService authService = ServiceLocator.getService(AuthService.class);

    public Result<LoggedInUser> login(String username, String password) {

        try {
            boolean success = authService.login(username, password);
            if (!success) throw new IllegalStateException("unknown password or username");
            LoggedInUser user = new LoggedInUser(username);
            return new Result.Success(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in: "+e.getMessage()));
        }
    }

    public Result<LoggedInUser> register(String username, String password){
        boolean registered = authService.register(username, password);
        if (registered) return new Result.Success(new LoggedInUser(username));
        return new Result.Error(new IOException("Username already exist."));
    }

    public void logout() {
        authService.logout();
    }
}