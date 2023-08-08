package com.pocketbudget.constant;

public class ErrorMessages {
//    Account validation messages
    public static final String BALANCE_NOT_NEGATIVE = "Balance must not be negative.";
    public static final String INVALID_CURRENCY = "Invalid currency.";

//    Record validation messages
    public static final String ACTION_MUST_NOT_BE_NULL = "Action must not be null!";
    public static final String AMOUNT_NOT_NEGATIVE = "Amount must not be negative!";
    public static final String CATEGORY_MUST_NOT_BE_NULL = "Category must not be null!";

//    User validation messages
    public static final String USERNAME_SIZE_VALIDATION = "Username must be between 6 and 20 characters long.";
    public static final String PASSWORD_MIN_SIZE = "Password must be between 6 and 25 characters long.";
    public static final String EMAIL_VALIDATION = "Email must be a valid email address.";
    public static final String FIRST_NAME_VALIDATION = "First name must be between 2 and 30 characters long.";
    public static final String LAST_NAME_VALIDATION = "Last name must be between 2 and 30 characters long.";

//    Exception messages
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds available in the account.";
    public static final String INVALID_ACCOUNT = "Invalid target account.";
    public static final String ACCOUNT_OWNED_BY_THE_USER = "Target account not owned by the user.";
}
