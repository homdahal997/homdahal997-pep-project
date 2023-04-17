package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {


    /**
     * Inserts a new message into the database.
     *
     * @param message the message object to be inserted
     * @return the inserted message object with a newly generated message_id
     */
    public static Message insertMessage(Message message) {
        // Get a database connection
        Connection connection = ConnectionUtil.getConnection();
        try {
            // The SQL query to insert a new row into the message table
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

            // Prepare the SQL statement
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set the values of the parameters in the SQL statement
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            // Execute the SQL statement
            preparedStatement.executeUpdate();

            // Get the generated message ID from the database
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if (pkeyResultSet.next()) {
                int generated_message_id = (int) pkeyResultSet.getLong(1);

                // Create a new Message object with the generated message ID and return it
                return new Message(generated_message_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
        } catch (SQLException e) {
            // If there was an error, print the error message to the console
            System.out.println(e.getMessage());
        }

        // If the insert was not successful, return null
        return null;
    }


    /**
     * Deletes a message with the given ID from the 'message' table in the database.
     *
     * @param message_id the ID of the message to be deleted
     * @return true if the message was successfully deleted, false otherwise
     */
    public static boolean deleteMessage(int message_id) {
        // Establish a database connection
        Connection connection = ConnectionUtil.getConnection();

        try {
            // Prepare a SQL statement to delete a message with the given ID
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);

            // Execute the SQL statement and get the number of rows affected
            int rowsAffected = preparedStatement.executeUpdate();

            // Return true if at least one row was affected
            return rowsAffected > 0;

        } catch (SQLException e) {
            // If an SQL exception occurs, print the error message and return false
            System.out.println(e.getMessage());
        }

        // If no rows were affected or an exception occurred, return false
        return false;
    }


    /**
     * Retrieves a message from the database with the given ID.
     *
     * @param messageId the ID of the message to retrieve
     * @return a Message object containing the message data, or null if the ID was not found
     * @throws SQLException if there was an error executing the SQL statement
     */
    public static Message getMessageById(int messageId) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        try {
            // Prepare a SQL statement to retrieve the message with the given ID
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);

            // Execute the SQL statement and get the results
            ResultSet rs = preparedStatement.executeQuery();

            // Loop through the results (there should only be one) and create a Message object
            while (rs.next()) {
                int message_id = rs.getInt("message_id");
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");

                return new Message(message_id, posted_by, message_text, time_posted_epoch);

            }
        } catch (SQLException e) {
            // If an SQL exception occurs, print the error message and throw a new SQLException
            System.out.println(e.getMessage());
            throw new SQLException("Error executing SQL statement: " + e.getMessage());
        }
        // If no message was found, return null
        return null;
    }


    /**
     * Retrieves all messages from the database.
     *
     * @return a List of Message objects containing the message data
     */
    public List<Message> getAllMessages() {
        // Establish a database connection
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            // Prepare a SQL statement to retrieve all messages
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Execute the SQL statement and get the results
            ResultSet rs = preparedStatement.executeQuery();

            // Loop through the results and create a Message object for each row
            while (rs.next()) {
                int message_id = rs.getInt("message_id");
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");
                Message message = new Message(message_id, posted_by, message_text, time_posted_epoch);
                messages.add(message);
            }
        } catch (SQLException e) {
            // If an SQL exception occurs, print the error message
            System.out.println(e.getMessage());
        }
        // Return the List of Message objects
        return messages;
    }

    /**
     * Updates the message text for a given message ID.
     *
     * @param messageId the ID of the message to be updated
     * @param newText   the new text to replace the existing message text
     * @return true if the message was successfully updated, false otherwise
     */
    public boolean updateMessageText(int messageId, String newText) {
        // Establish a database connection
        Connection connection = ConnectionUtil.getConnection();
        try {
            // Prepare a SQL statement to update the message text for the given message ID
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newText);
            preparedStatement.setInt(2, messageId);

            // Execute the SQL statement and get the number of rows updated
            int numRowsUpdated = preparedStatement.executeUpdate();

            // Return true if exactly one row was updated
            return numRowsUpdated == 1;
        } catch (SQLException e) {
            // If an SQL exception occurs, print the error message
            System.out.println(e.getMessage());
        }

        // If no rows were updated or an exception occurred, return false
        return false;
    }


    /**
     * Retrieves all messages posted by a given user.
     *
     * @param AccountUser the ID of the user whose messages are to be retrieved
     * @return a list of Message objects representing the user's messages
     * @throws SQLException if an error occurs while accessing the database
     */
    public List<Message> retriveAllMessagesForUser(int AccountUser) throws SQLException {
        // Get a database connection
        Connection connection = ConnectionUtil.getConnection();

        // Create an empty list to store the user's messages
        List<Message> messages = new ArrayList<>();

        try {
            // Prepare an SQL statement to retrieve the user's messages
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, AccountUser);

            // Execute the SQL statement and iterate through the result set
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                // Retrieve the message details from the result set
                int messageId = rs.getInt("message_id");
                int postedBy = rs.getInt("posted_by");
                String messageText = rs.getString("message_text");
                long timePostedEpoch = rs.getLong("time_posted_epoch");

                // Create a Message object to represent the message
                Message message = new Message(messageId, postedBy, messageText, timePostedEpoch);

                // Add the message to the list of messages
                messages.add(message);
            }
        } catch (SQLException e) {
            // Handle any exceptions that occur during the process
            System.out.println(e.getMessage());
        }

        // Return the list of messages
        return messages;
    }


}// end MessageDAO
