package service;

/**
 * AuthenticationService
 */
public interface AuthenticationService {

    void insertUser(String name, String password);

	void deleteUserByName(String login);

	public User selectUserByName(String login);

	public boolean authentication(String login, String password);
	
	public boolean changePass(String login, String oldPass, String newPass);

	public boolean isLogin(String login);

	public boolean checkPassword(String login, String password);
    
}