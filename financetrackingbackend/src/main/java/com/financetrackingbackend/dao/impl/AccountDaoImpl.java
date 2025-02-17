package com.financetrackingbackend.dao.impl;

import com.financetrackingbackend.dao.AccountDao;
import com.financetrackingbackend.exceptions.ResourceConflictException;
import com.financetrackingbackend.schemas.dynamodb.Account;
import com.financetrackingbackend.util.AuthenticationUtil;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Component
public class AccountDaoImpl implements AccountDao {
    private final DynamoDbTable<Account> accountDynamoDbTable;
    private final AuthenticationUtil authUtil;
    private static final String UPDATE_EXPRESSION = "attribute_exists(id)";
    private static final String CHECK_ID_EXISTS_EXPRESSION = "attribute_not_exists(id)";
    private static final String CONFLICT_MSG = "Account already exists with id: ";
    private static final String CHECK_USER_EXPRESSION = "#usr = :username";
    private static final String USER_ATTR = "user";
    private static final String USER_ALIAS = "#usr";
    private static final String USERNAME_VALUE_ALIAS = ":username";

    public AccountDaoImpl() {
        this.authUtil = new AuthenticationUtil();
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .region(Region.EU_WEST_1)
                .build();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.accountDynamoDbTable = enhancedClient.table("Accounts", TableSchema.fromBean(Account.class));
    }

    @Override
    public Account getAccount(String id) {
        return accountDynamoDbTable.getItem(buildKey(id));
    }

    @Override
    public Account addAccount(Account account) {
        String username = authUtil.getCurrentUsername();
        account.setUser(username);

        try {
            PutItemEnhancedRequest<Account> request = PutItemEnhancedRequest.builder(Account.class)
                    .item(account)
                    .conditionExpression(buildExpression(CHECK_ID_EXISTS_EXPRESSION, null, null))
                    .build();
            accountDynamoDbTable.putItem(request);
        } catch (ConditionalCheckFailedException e) {
            throw new ResourceConflictException(CONFLICT_MSG+account.getId());
        }

        return account;
    }

    @Override
    public Account deleteAccount(String id) {
        String username = authUtil.getCurrentUsername();
        DeleteItemEnhancedRequest request = DeleteItemEnhancedRequest.builder()
                .key(buildKey(id))
                .conditionExpression(buildExpression(
                        CHECK_USER_EXPRESSION,
                        Collections.singletonMap(USER_ALIAS, USER_ATTR),
                        Collections.singletonMap(USERNAME_VALUE_ALIAS, AttributeValue.builder().s(username).build())
                ))
                .build();
        return accountDynamoDbTable.deleteItem(request);
    }

    @Override
    public Account updateAccount(String id, Account updatedAccount) {
        String username = authUtil.getCurrentUsername();
        updatedAccount.setId(id);
        updatedAccount.setUser(username);

        UpdateItemEnhancedRequest<Account> request = UpdateItemEnhancedRequest.builder(Account.class)
                .item(updatedAccount)
                .conditionExpression(buildExpression(UPDATE_EXPRESSION, null, null))
                .conditionExpression(buildExpression(
                        CHECK_USER_EXPRESSION,
                        Collections.singletonMap(USER_ALIAS, USER_ATTR),
                        Collections.singletonMap(USERNAME_VALUE_ALIAS, AttributeValue.builder().s(username).build())
                ))
                .ignoreNulls(true)
                .build();

        return accountDynamoDbTable.updateItem(request);
    }

    private Key buildKey(String id) {
        return Key.builder()
                .partitionValue(id)
                .build();
    }

    private Expression buildExpression(String expression, Map<String, String> expressionNames, Map<String, AttributeValue> expressionValues) {
        Expression.Builder builder = Expression.builder()
                .expression(expression);

        if (expressionNames != null && !expressionNames.isEmpty()) {
            builder.expressionNames(expressionNames);
        }

        if (expressionValues != null && !expressionValues.isEmpty()) {
            builder.expressionValues(expressionValues);
        }

        return builder.build();
    }
}
