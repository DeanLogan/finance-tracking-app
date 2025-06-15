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
    public static final String SCOPE = "scope";
    public static final String ACCOUNTS_SCOPE = "accounts";
    public static final String OPENID_ACCOUNTS_SCOPE = "openid accounts";
    public static final String CODE = "code";
    public static final String GRANT_TYPE = "grant_type";
    public static final String ACCOUNT_ACCESS_CONSENTS_PATH = "open-banking/v3.1/aisp/account-access-consents";
    public static final String DATA = "Data";
    public static final String RISK = "Risk";
    public static final String PERMISSIONS = "Permissions";
    public static final String TRANSACTIONS_ENDPOINT = "/transactions";
    public static final String BALANCES_ENDPOINT = "/balances";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String AUTHORIZATION_CODE = "authorization_code";

    public static final List<String> PERMISSION_ARR = Arrays.asList(
            "ReadAccountsDetail",
            "ReadBalances",
            "ReadTransactionsCredits",
            "ReadTransactionsDebits",
            "ReadTransactionsDetail"
    );
}
