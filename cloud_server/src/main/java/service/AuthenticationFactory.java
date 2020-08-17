package service;

/**
 * AuthenticationFactory
 */
public class AuthenticationFactory {

    public static AuthenticationService createAuthenticationService() {
        return new SqlDaoServiceLoggingProxy();
    }
}