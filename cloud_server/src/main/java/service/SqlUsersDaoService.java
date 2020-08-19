package service;

import java.sql.*;


/**
 * Класс для взаимодействия с таблицей Users в базе данных
 * для реализации механизма аутентификации в приложении
 */
public class SqlUsersDaoService implements AuthenticationService {	


	private Connection connection;

	public SqlUsersDaoService() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cloudstorage_users", "root", "**********");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("DB with URL " + "cloudstorage_users" + " successfully connected");
	}

	private PreparedStatement createPreparedStatement(String sql, String... parameters) throws SQLException {
		PreparedStatement preparedStatement = null;
			preparedStatement = connection.prepareStatement(sql);
			for (int i = 0; i < parameters.length; i++) {
				preparedStatement.setString(i + 1, parameters[i]);
			}
		return preparedStatement;
	}

	@Override
	public void insertUser(String name, String password) {
		String sqlString = "INSERT into users (name, password) VALUES (?, ?);";
		try {
			PreparedStatement preparedStatement = createPreparedStatement(sqlString, name, password);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteUserByName(String login) {
		String sqlString = "DELETE FROM users WHERE name = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = createPreparedStatement(sqlString, login);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public User selectUserByName(String login) {
		String sqlString = "select login_fld, password_fld, rootpath_fld "
				+ "from `cloudstorage_users`.`users` "
				+ "where login_fld =?";
		User user = null;
		try {
			PreparedStatement preparedStatement = createPreparedStatement(sqlString, login);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				user = new User();
				user.setLogin(resultSet.getString("login_fld"));
				user.setPassword(resultSet.getString("password_fld"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public boolean authentication(String login, String password) {
		User user = selectUserByName(login);
		if(user == null) {
			return false;
		}
		return user.getPassword().equals(password);
	}
	
	@Override
	public boolean changePass(String login, String oldPass, String newPass) {
		if(authentication(login, oldPass)) {
			String sqlString = "UPDATE users SET password = ? WHERE name = ?";
			try {
				PreparedStatement preparedStatement = createPreparedStatement(sqlString, newPass, login);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {	
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isLogin(String login) {
		User user = selectUserByName(login);
		if(user == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean checkPassword(String login, String password) {
		User user = selectUserByName(login);
		return user.getPassword().equals(password);
	}

}
