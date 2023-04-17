package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.//
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     *
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);

        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::messageHandler);
        app.get("/messages", this::getAllMessageHandler);
        app.get("/messages/{message_id}", this::getMessageByMessageIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesForUserHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


    /**
     * This is a handler for registering a new account.
     * It reads the request body to get an Account object, registers the account with the AccountService,
     * and returns a JSON response containing the registered account (if successful).
     * If the account is invalid (e.g. username is blank or password is too short), it returns a 400 Bad Request response.
     *
     * @param ctx The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException if there is an error parsing the request body as JSON.
     */
    private void registerHandler(Context ctx) throws JsonProcessingException {

        // Create an ObjectMapper to read and write JSON.
        ObjectMapper objectMapper = new ObjectMapper();

        // Read the request body as an Account object.
        Account account = objectMapper.readValue(ctx.body(), Account.class);

        // Register the account with the AccountService and get the registered account (if successful).
        Account registeredAccount = AccountService.registerAccount(account);

        // If the username is blank, return a 400 Bad Request response.
        if (account.getUsername().isBlank()) {
            ctx.status(400);
            ctx.result("");
        }
        // If the password is too short, return a 400 Bad Request response.
        else if (account.password.length() < 4) {
            ctx.status(400);
            ctx.result("");
        }
        // Otherwise, return a JSON response containing the registered account (if successful)
        // or a 400 Bad Request response (if unsuccessful).
        else {
            if (registeredAccount != null) {
                ctx.json(objectMapper.writeValueAsString(registeredAccount));
            } else {
                ctx.status(400);
            }
        }
    }

    /**
     * This method handles user login requests.
     * It reads the JSON payload from the request body and deserializes it into an Account object.
     * If the username or password is empty, it returns a 400 Bad Request response.
     * If the username and password are valid, it attempts to log the user in.
     * If there is an error during the login process, it returns a 401 Unauthorized response.
     *
     * @param ctx The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException if there is an error while parsing the JSON payload.
     */
    private void loginHandler(Context ctx) throws JsonProcessingException {

        // Create an ObjectMapper to read and write JSON.
        ObjectMapper objectMapper = new ObjectMapper();

        // Read the request body as an Account object.
        Account account = objectMapper.readValue(ctx.body(), Account.class);

        // Check if the username or password is empty.
        if (account.username.length() == 0) {
            ctx.status(400);
            ctx.result("");
        } else if (account.password.length() == 0) {
            ctx.status(400);
            ctx.result("");
        } else {
            try {
                // Attempt to log the user in.
                AccountService.login(ctx, account.username, account.password);
            } catch (SQLException e) {
                // If there is an error during the login process, return a 401 Unauthorized response.
                System.out.println(e.getMessage());
                ctx.status(401);
                ctx.result("Error");
            }
        }
    }


    /**
     * This method handles message requests.
     * It reads the JSON payload from the request body and deserializes it into a Message object.
     * It then passes the Message object to the MessageService to insert it into the database.
     * If the message text is empty or longer than 254 characters, it returns a 400 Bad Request response.
     * If the message was successfully inserted into the database, it returns the inserted message as a JSON response.
     * If there was an error inserting the message into the database, it returns a 400 Bad Request response.
     *
     * @param ctx The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException if there is an error while parsing the JSON payload.
     */
    private void messageHandler(Context ctx) throws JsonProcessingException {

        // Create an ObjectMapper to read and write JSON.
        ObjectMapper objectMapper = new ObjectMapper();

        // Read the request body as an Message object.
        Message message = objectMapper.readValue(ctx.body(), Message.class);

        // Insert the message into the database using the MessageService.
        Message addedMessage = MessageService.insertMessage(message);

        // Check if the message text is empty or longer than 254 characters.
        if ((message.getMessage_text().isBlank() || message.getMessage_text().length() > 254)) {
            ctx.status(400);
        } else if (addedMessage != null) {
            // If the message was successfully inserted into the database, return the inserted message as a JSON response.
            ctx.json(objectMapper.writeValueAsString(addedMessage));
        } else {
            // If there was an error inserting the message into the database, return a 400 Bad Request response.
            ctx.status(400);
        }
    }


    /**
     * This method handles requests to delete a message by its ID.
     * It first reads the message ID from the path parameter and tries to retrieve the message from the database.
     * If the message exists, it deletes it using the MessageService and returns a 200 OK response along with the deleted message.
     * If the message does not exist, it returns a 200 OK response with no content.
     *
     * @param ctx The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException if there is an error while deleting the message from the database.
     */
    private void deleteMessageHandler(Context ctx) throws SQLException {
        // Get the message ID from the path parameter.
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));

        // Try to retrieve the message from the database.
        Message existingMessage = MessageService.getMessageById(ctx, messageId);

        if (existingMessage != null) {
            // If the message exists, delete it using the MessageService.
            boolean messageDeleted = MessageService.deleteMessage(ctx, messageId);

            if (messageDeleted) {
                // If the message was deleted successfully, return a 200 OK response along with the deleted message.
                ctx.status(200);
                ctx.json(existingMessage);
            }
        } else {
            // If the message does not exist, return a 200 OK response with no content.
            ctx.status(200);
        }
    }


    /**
     * This method handles requests to get all messages from the database.
     * It retrieves all messages using the MessageService and returns them in the response.
     * If there are no messages in the database, it returns an empty list and a 200 OK response.
     *
     * @param ctx The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getAllMessageHandler(Context ctx) {
        // Retrieve all messages from the database.
        List<Message> messages = MessageService.getAllMessages();

        if (messages.isEmpty()) {
            // If there are no messages in the database, return an empty list and a 200 OK response.
            ctx.json(Collections.EMPTY_LIST);
            ctx.status(200);
        } else {
            // If there are messages in the database, return them in the response.
            ctx.json(messages);
        }
    }

    /**
     * This method handles requests to get a message by its ID from the database.
     * It retrieves the message using the MessageService and returns it in the response.
     * If there is no message with the specified ID in the database, it returns a 200 OK response.
     *
     * @param ctx The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException if an error occurs while retrieving the message from the database.
     */
    private void getMessageByMessageIdHandler(Context ctx) throws SQLException {
        // Parse the message ID from the request path parameter.
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));

        // Retrieve the message with the specified ID from the database.
        Message messageById = MessageService.getMessageById(ctx, messageId);

        if (messageById != null) {
            // If a message with the specified ID exists in the database, return it in the response.
            ctx.json(messageById);
        } else {
            // If there is no message with the specified ID in the database, return a 200 OK response.
            ctx.status(200);
        }
    }


    /**
     * This function updates a message in the system.
     * It reads a Message object from the request body and sets its message_id property to the ID extracted from the path parameter.
     * If the message text is missing, empty, or too long, it returns a 400 Bad Request response.
     * If the message is updated successfully, it returns a JSON representation of the updated message.
     * Otherwise, it returns a 400 Bad Request response.
     *
     * @param ctx The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException            if a database access error occurs
     * @throws JsonProcessingException if an error occurs during JSON serialization or deserialization
     */
    public void updateMessageHandler(Context ctx) throws SQLException, JsonProcessingException {
        // Create an ObjectMapper to read and write JSON.
        ObjectMapper objectMapper = new ObjectMapper();

        // Read the message from the request body and set its ID to the ID extracted from the path parameter
        Message message = objectMapper.readValue(ctx.body(), Message.class);
        message.setMessage_id(Integer.parseInt(ctx.pathParam("message_id")));

        // Check if the message text is missing, empty, or too long, and return a 400 Bad Request response if necessary
        if (message.message_text == null || message.message_text.trim().isEmpty() || message.message_text.length() >= 255) {
            ctx.status(400).result("");
            return;
        }

        // Update the message in the system and return a JSON representation of the updated message if successful
        if (MessageService.updateMessageText(ctx, message.message_id, message.message_text)) {
            Message updatedMessage = MessageService.getMessageById(ctx, message.message_id);
            ctx.json(objectMapper.writeValueAsString(updatedMessage));
        } else {
            // Return a 400 Bad Request response if the message could not be updated
            ctx.status(400).result("");
        }
    }


    /**
     * This method retrieves all messages for a specific account.
     *
     * @param ctx The Javalin Context object manages information about both the HTTP request and response.
     * @throws SQLException If there is an error executing the SQL statement.
     */
    public void getAllMessagesForUserHandler(Context ctx) throws SQLException {
        // Retrieve the account ID from the path parameter.
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));

        // Retrieve all messages for the account user.
        List<Message> messagesByAccountUser = MessageService.getAllMessagesForUser(ctx, accountId);

        // Set the response status to 200 and return the messages in the response body.
        ctx.status(200);
        ctx.json(messagesByAccountUser);
    }


}// end socialMediaController