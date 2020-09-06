
/**
 * Тип сообщения для обмена с сервером информации об аутентификации пользователей
 */

public class AuthenticationMessage extends AbstractMessage {	
	
	private static final long serialVersionUID = 1122033184461537593L;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getLogin() {
		return login;
	}
	public String getPassword() {
		return password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public AuthCommandType getAuthCommandType() {
		return authCommandType;
	}

	public enum AuthCommandType {
		REGISTRATION, CHANGE_PASS, AUTHORIZATION
	}	
	
	private boolean status;
	private String login;
	private String password;
	private String newPassword;	
	private AuthCommandType authCommandType;

	public AuthenticationMessage(String login, String password, String newPassword, AuthCommandType authCommandType) {
		this.login = login;
		this.password = password;
		this.newPassword = newPassword;
		this.authCommandType = authCommandType;
	}

	public AuthenticationMessage(String login, String password, AuthCommandType authCommandType) {
		this.login = login;
		this.password = password;
		this.authCommandType = authCommandType;
	}
}
