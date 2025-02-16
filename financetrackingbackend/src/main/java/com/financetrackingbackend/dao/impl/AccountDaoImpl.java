package com.financetrackingbackend.dao.impl;

import com.financetrackingbackend.dao.AccountDao;
import com.financetrackingbackend.schemas.dynamodb.Account;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.net.URI;
import java.util.Map;

@Component
public class AccountDaoImpl implements AccountDao {
    private final DynamoDbTable<Account> accountDynamoDbTable;

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
        System.out.println("key:"+key);
        System.out.println("account:"+accountDynamoDbTable.getItem(key));
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
    public Account updateAccount(String id, Map<String, Object> updatedFields) {
        throw new UnsupportedOperationException("Unimplemented method 'updateAccount'");
    }
}
