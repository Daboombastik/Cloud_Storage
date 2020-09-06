import java.sql.*;
import java.util.logging.Logger;

/**
 * Класс для взаимодействия с таблицей Users в базе данных
 * для реализации механизма аутентификации в приложении
 */
public class DBHandler {

    private Logger logger = Logger.getLogger(DBHandler.class.getName());
    private Connection connection;

    public DBHandler() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/cloudstorage_users",
                    "root",
                    "************");
            System.out.println("DB with URL " + "cloudstorage_users" + " successfully connected");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
                connection.close();
            } catch (SQLException err) {
                System.out.println(err.getMessage() + " " + err.getSQLState());
            }
        }
    }

    private PreparedStatement createPreparedStatement(String sql, String... parameters) throws SQLException {
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setString(i + 1, parameters[i]);
        }
        return preparedStatement;
    }

    public void insertUser(String name, String password) {
        String sqlString = "INSERT into users (name, password) VALUES (?, ?);";
        logger.info(String.format("Insert new user with name '%s' and passord '%s'", name,password));
        try {
            PreparedStatement preparedStatement = createPreparedStatement(sqlString, name, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User selectUserByName(String login) {
        String sqlString = "select login_fld, password_fld, rootpath_fld "
                + "from `cloudstorage_users`.`users` "
                + "where login_fld =?";
        logger.info(String.format("call for user with name '%s'", login));
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

    public boolean authentication(String login, String password) {
        User user = selectUserByName(login);
        logger.info(String.format("authentication request from user '%s'", login));
        if(user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }

    public boolean changePass(String login, String oldPass, String newPass) {
        logger.info(String.format("change password from '%s' to '%s' request from user '%s'", oldPass, newPass, login));
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

    private static class User {
        String login;
        String password;

        public String getLogin() {
            return login;
        }
        public void setLogin(String login) {
            this.login = login;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}