package Service;

import java.sql.SQLException;

import DAO.AccountDAO;
import Model.Account;
import io.javalin.http.Context;

public class AccountService {

    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }


    /**
     * Calls the registerAccount method of the AccountDAO class to register a new account
     *
     * @param account the account object to be registered
     * @return an Account object with the generated primary key if registration is successful, null otherwise
     */
    public static Account registerAccount(Account account) {
        return AccountDAO.registerAccount(account);
    }

    /**
     * This method handles the login request for a user.
     *
     * @param ctx      The context of the request.
     * @param username The username of the account.
     * @param password The password of the account.
     * @throws SQLException If there is an error with the SQL query.
     */
    public static void login(Context ctx, String username, String password) throws SQLException {
        // Get the account associated with the provided username and password
        Account account = AccountDAO.getAccountByUserNameAndPassword(username, password);

        // If the account is null, return a 401 response indicating that the login credentials were incorrect
        if (account == null) {
            ctx.status(401);
            ctx.result("");
        }
        // If the account is not null, return the account as JSON in the response body
        else {

            ctx.json(account);
        }

    }
}
