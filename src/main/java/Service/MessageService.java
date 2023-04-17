package Service;

import DAO.MessageDAO;
import Model.Message;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private static MessageDAO messageDAO;

    /**
     * no-args constructor for creating a new AuthorService with a new AuthorDAO.
     * There is no need to change this constructor.
     */
    public MessageService() {
        messageDAO = new MessageDAO();
    }

    /**
     * Inserts a new message in the database.
     *
     * @param message The message to be inserted.
     * @return The inserted message object with the generated message_id.
     */
    public static Message insertMessage(Message message) {
        return MessageDAO.insertMessage(message);
    }


    /**
     * Deletes a message from the database by its ID.
     *
     * @param ctx        The context of the application.
     * @param message_id The ID of the message to delete.
     * @return true if the message was successfully deleted, false otherwise.
     */
    public static boolean deleteMessage(Context ctx, int message_id) {
        boolean isDeleted = MessageDAO.deleteMessage(message_id);
        return isDeleted;
    }

    /**
     * Retrieves a message by its unique ID.
     *
     * @param ctx the context of the application
     * @param id  the ID of the message to retrieve
     * @return the message with the specified ID, or null if the message does not exist
     * @throws SQLException if there is an error executing the SQL query
     */
    public static Message getMessageById(Context ctx, int id) throws SQLException {
        return messageDAO.getMessageById(id);
    }


    /**
     * Retrieves all messages from the message table
     *
     * @return a List of all messages in the message table, or an empty List if there are none
     */
    public static List<Message> getAllMessages() {
        MessageDAO messageDAO = new MessageDAO();
        List<Message> messages = messageDAO.getAllMessages();
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }


    /**
     * Updates the text of a message with the given message ID and returns a boolean value indicating whether the update was successful or not.
     *
     * @param ctx       the context of the application
     * @param messageId the ID of the message to be updated
     * @param newText   the new text for the message
     * @return true if the update was successful, false otherwise
     */
    public static boolean updateMessageText(Context ctx, int messageId, String newText) {
        MessageDAO messageDAO = new MessageDAO();
        return messageDAO.updateMessageText(messageId, newText);
    }


    /**
     * Retrieves all messages for a specified user
     *
     * @param ctx         the context
     * @param accountUser the user for whom to retrieve messages
     * @return a list of messages for the specified user
     * @throws SQLException if there is an error retrieving messages from the database
     */
    public static List<Message> getAllMessagesForUser(Context ctx, int accountUser) throws SQLException {
        MessageDAO messageDAO = new MessageDAO();
        List<Message> messages = messageDAO.retriveAllMessagesForUser(accountUser);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }


}
