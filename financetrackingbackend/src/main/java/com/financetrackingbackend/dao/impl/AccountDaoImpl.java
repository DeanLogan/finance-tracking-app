package com.financetrackingbackend.dao.impl;

import com.financetrackingbackend.dao.AccountDao;
import com.financetrackingbackend.schemas.dynamodb.Account;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.Map;

@Component
public class AccountDaoImpl implements AccountDao {
    private final DynamoDbTable<Account> accountDynamoDbTable;

    public AccountDaoImpl() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.create();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.accountDynamoDbTable = enhancedClient.table("Users", TableSchema.fromBean(Account.class));
    }

    @Override
    public Account getAccount(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'getAccount'");
    }

    @Override
    public void addAccount(Account account) {
        throw new UnsupportedOperationException("Unimplemented method 'addAccount'");
    }

    @Override
    public void deleteAccount(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAccount'");
    }

    @Override
    public void updateAccount(String id, Map<String, Object> updatedFields) {
        throw new UnsupportedOperationException("Unimplemented method 'updateAccount'");
    }
}
