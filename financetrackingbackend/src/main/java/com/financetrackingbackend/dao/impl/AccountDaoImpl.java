package com.financetrackingbackend.dao.impl;

import com.financetrackingbackend.dao.AccountDao;
import com.financetrackingbackend.schemas.dynamodb.Account;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;

import java.net.URI;

@Component
public class AccountDaoImpl implements AccountDao {
    private final DynamoDbTable<Account> accountDynamoDbTable;
    private static final String UPDATE_EXPRESSION = "attribute_exists(id)";

    public AccountDaoImpl() {
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
        Key key = Key.builder()
                .partitionValue(id)
                .build();
        return accountDynamoDbTable.getItem(key);
    }

    @Override
    public Account addAccount(Account account) {
        accountDynamoDbTable.putItem(account);
        return account;
    }

    @Override
    public void deleteAccount(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAccount'");
    }

    @Override
    public Account updateAccount(String id, Account updatedAccount) {
        updatedAccount.setId(id);

        Expression expression = Expression.builder()
            .expression(UPDATE_EXPRESSION)
            .build();

        UpdateItemEnhancedRequest<Account> request = UpdateItemEnhancedRequest.builder(Account.class)
                .item(updatedAccount)
                .conditionExpression(expression)
                .ignoreNulls(true)
                .build();

        return accountDynamoDbTable.updateItem(request);
    }
}
