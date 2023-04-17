package DAO;

import java.sql.*;


import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {
    /**
     * Register a new account by inserting a new row into the database
     *
     * @param account the account object to be registered
     * @return an Account object with the generated primary key if registration is successful, null otherwise
     */

    public static Account registerAccount(Account account) {
        // Gets a database connection using the ConnectionUtil class
        Connection connection;
        connection = ConnectionUtil.getConnection();
        try {
            // The SQL query to insert a new row into the account table
            String sql = "INSERT INTO account (username, password) VALUES (?,?)";

            // Create a PreparedStatement object with the SQL statement
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Sets the values of the prepared statement to the account's username and password
            preparedStatement.setString(1, account.username);
            preparedStatement.setString(2, account.password);

            // Executes the SQL query
            preparedStatement.executeUpdate();

            // Gets the generated primary key of the newly inserted row
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generated_account_id = (int) pkeyResultSet.getLong(1);

                // Returns a new account object with the generated primary key
                return new Account(generated_account_id,
                        account.getUsername(),
                        account.getPassword());
            }
        }catch(SQLException e){
            // Prints the error message if there's an issue with the SQL query
            System.out.println(e.getMessage());
        }
        // Returns null if the account couldn't be registered
        return null;
    }


    /**
     * Retrieves an Account object from the database with the specified username and password.
     *
     * @param username the username of the account to retrieve
     * @param password the password of the account to retrieve
     * @return an Account object with the specified username and password, or null if no account is found
     */
    public static Account getAccountByUserNameAndPassword(String username, String password) {

            // Get a connection to the database using the ConnectionUtil class
            Connection conn = ConnectionUtil.getConnection();
        try {
            // Construct the SQL statement to retrieve the account from the database
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";

            // Create a PreparedStatement object with the SQL statement
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            // Set the parameters of the PreparedStatement object with the specified username and password
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the prepared statement and retrieve the result set
            ResultSet rs = preparedStatement.executeQuery();

            // Iterate through the result set and return an Account object with the specified username and password
            while(rs.next()) {
                return new Account(rs.getInt("account_id"), username, password);
            }

            // If no account is found, return null
            return null;
        } catch (SQLException e) {
            // Handle any SQL exceptions that may occur
            System.out.println(e.getMessage());
            return null;
        }
    }
}
