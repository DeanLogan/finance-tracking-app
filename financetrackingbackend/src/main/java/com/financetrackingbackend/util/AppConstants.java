package com.financetrackingbackend.util;

import java.util.Arrays;
import java.util.List;

public final class AppConstants {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String EMPTY_STRING = "";
    public static final String UB_ERROR_MSG = "UB request failed: ";
    public static final String GRANT_TYPE_ERROR = "Unsupported grant type: ";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String TOKEN_PATH = "/token";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String CURRENT_ACCOUNT_ID = "current_account_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String SCOPE = "scope";
    public static final String ACCOUNTS_SCOPE = "accounts";
    public static final String OPENID_ACCOUNTS_SCOPE = "openid accounts";
    public static final String CODE = "code";
    public static final String GRANT_TYPE = "grant_type";
    public static final String ACCOUNT_ACCESS_CONSENTS_PATH = "open-banking/v3.1/aisp/account-access-consents";
    public static final String DATA = "Data";
    public static final String RISK = "Risk";
    public static final String PERMISSIONS = "Permissions";
    public static final String TRANSACTIONS_PATH = "/transactions";
    public static final String BALANCES_PATH = "/balances";
    public static final String BALANCE_PATH = "/balance";
    public static final String ACCOUNTS_PATH = "/accounts";
    public static final String MONZO_WHO_AM_I_PATH = "/ping/whoami";
    public static final String POTS_PATH = "/pots";
    public static final String AUTH_TOKEN_PATH = "/oauth2/token";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String AUTH_CODE = "authorization_code";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String REQUIRED_INFO_BLANK_ERROR_MSG = "Required information for request is blank";
    public static final String MONZO_REQUEST_FAIL = "Monzo request failed: ";
    public static final String INCORRECT_TOKEN = "Incorrect token: ";
    public static final String QUESTION_MARK = "?";
    public static final String EQUALS_SIGN = "=";
    public static final String ACCOUNTS = "Accounts";
    public static final String UPDATE_EXPRESSION = "attribute_exists(id)";
    public static final String CHECK_ID_EXISTS_EXPRESSION = "attribute_not_exists(id)";
    public static final String CONFLICT_MSG = "Account already exists with id: ";
    public static final String CHECK_USER_EXPRESSION = "#usr = :username";
    public static final String USER_ATTR = "user";
    public static final String USER_ALIAS = "#usr";
    public static final String USERNAME_VALUE_ALIAS = ":username";
    public static final String CONNECTION_ERROR_MSG = "Could not connect the database";
    public static final String AUTH_ERROR_MSG = "Problem authenticating user";

    public static final String UB_REDIRECT_URI = "http://localhost:8080/ulsterbank/oauth/callback.html";
    public static final String UB_BASE_URL = "https://api.sandbox.ulsterbank.co.uk/authorize";
    public static final String CODE_ID_TOKEN = "code id_token";
    public static final String OPENID_ACCOUNTS = "openid accounts";
    public static final String AUTH_URL_TEMPLATE = "%s?client_id=%s&response_type=%s&scope=%s&redirect_uri=%s&request=%s";
    public static final String UB_NULL_RESPONSE_ERROR_MSG = "UB Response is null";

    public static final List<String> PERMISSION_ARR = Arrays.asList(
            "ReadAccountsDetail",
            "ReadBalances",
            "ReadTransactionsCredits",
            "ReadTransactionsDebits",
            "ReadTransactionsDetail"
    );
}
