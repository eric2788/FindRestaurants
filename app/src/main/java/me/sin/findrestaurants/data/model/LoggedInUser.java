package me.sin.findrestaurants.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private final String userId;

    public LoggedInUser(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}