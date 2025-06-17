package com.financetrackingbackend.dao.impl;

import com.example.model.Account;
import com.financetrackingbackend.configuration.AwsConfig;
import com.financetrackingbackend.dao.AccountDao;
import com.financetrackingbackend.exceptions.ResourceConflictException;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;
import com.financetrackingbackend.util.AuthenticationUtil;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
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

import static com.financetrackingbackend.util.AppConstants.ACCOUNTS;
import static com.financetrackingbackend.util.AppConstants.CHECK_ID_EXISTS_EXPRESSION;
import static com.financetrackingbackend.util.AppConstants.CHECK_USER_EXPRESSION;
import static com.financetrackingbackend.util.AppConstants.CONFLICT_MSG;
import static com.financetrackingbackend.util.AppConstants.CONNECTION_ERROR_MSG;
import static com.financetrackingbackend.util.AppConstants.UPDATE_EXPRESSION;
import static com.financetrackingbackend.util.AppConstants.USERNAME_VALUE_ALIAS;
import static com.financetrackingbackend.util.AppConstants.USER_ALIAS;
import static com.financetrackingbackend.util.AppConstants.USER_ATTR;

@Component
public class AccountDaoImpl implements AccountDao {
    private final DynamoDbTable<Account> accountDynamoDbTable;
    private final AuthenticationUtil authUtil;

    public AccountDaoImpl(AwsConfig awsConfig) {
        this.authUtil = new AuthenticationUtil();
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create(awsConfig.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsConfig.getAccessKey(), awsConfig.getSecretKey())))
                .region(Region.of(awsConfig.getRegion()))
                .build();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.accountDynamoDbTable = enhancedClient.table(ACCOUNTS, TableSchema.fromBean(Account.class));
    }

    @Override
    public Account getAccount(String id) {
        String username = authUtil.getCurrentUsername();
        Account account;
        try {
            account = accountDynamoDbTable.getItem(buildKey(id));
        } catch (SdkClientException e) {
            throw new ServiceUnavailableException(CONNECTION_ERROR_MSG);
        }
        if (account != null && username.equals(account.getUser())) {
            return account;
        }
        return null;
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
        } catch (SdkClientException e) {
            throw new ServiceUnavailableException(CONNECTION_ERROR_MSG);
        }

        return account;
    }

    @Override
    public Account deleteAccount(String id) {
        String username = authUtil.getCurrentUsername();
        DeleteItemEnhancedRequest request = DeleteItemEnhancedRequest.builder()
                .key(buildKey(id))
                .conditionExpression(checkUserExpression(username))
                .build();
        
        try {
            return accountDynamoDbTable.deleteItem(request);
        } catch (SdkClientException e) {
            throw new ServiceUnavailableException(CONNECTION_ERROR_MSG);
        }
    }

    @Override
    public Account updateAccount(String id, Account updatedAccount) {
        String username = authUtil.getCurrentUsername();
        updatedAccount.setId(id);
        updatedAccount.setUser(username);

        UpdateItemEnhancedRequest<Account> request = UpdateItemEnhancedRequest.builder(Account.class)
                .item(updatedAccount)
                .conditionExpression(buildExpression(UPDATE_EXPRESSION, null, null))
                .conditionExpression(checkUserExpression(username))
                .ignoreNulls(true)
                .build();

        try {
            return accountDynamoDbTable.updateItem(request);
        } catch (SdkClientException e) {
            throw new ServiceUnavailableException(CONNECTION_ERROR_MSG);
        }
    }

    private Key buildKey(String id) {
        return Key.builder()
                .partitionValue(id)
                .build();
    }

    private Expression checkUserExpression(String username) {
        return buildExpression(
                CHECK_USER_EXPRESSION,
                Collections.singletonMap(USER_ALIAS, USER_ATTR),
                Collections.singletonMap(USERNAME_VALUE_ALIAS, AttributeValue.builder().s(username).build())
        );
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
