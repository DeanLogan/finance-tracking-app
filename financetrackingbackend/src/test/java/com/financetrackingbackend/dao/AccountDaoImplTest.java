package com.financetrackingbackend.dao;

import com.example.model.Account;
import com.financetrackingbackend.configuration.AwsConfig;
import com.financetrackingbackend.dao.impl.AccountDaoImpl;
import com.financetrackingbackend.exceptions.ResourceConflictException;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;
import com.financetrackingbackend.util.AuthenticationUtil;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class AccountDaoImplTest {
    @Mock
    private DynamoDbTable<Account> accountDynamoDbTable;
    @Mock
    private AuthenticationUtil authUtil;
    @Mock
    private AwsConfig awsConfig;

    private AccountDaoImpl accountDao;

    private static final String ID = "123";
    private static final String LOCAL_ENDPOINT = "http://localhost:8000";
    private static final String DUMMY_CREDENTIAL = "dummy";
    private static final String AWS_REGION = "us-east-1";
    private static final String DYNAMO_TABLE_FIELD = "accountDynamoDbTable";
    private static final String USER = "user";
    private static final String CONNECTION_ERROR_MSG = "Could not connect the database";
    private static final String CONFLICT_MSG = "Account already exists with id: 123";
    private static final String EMPTY_STRING = "";

    @BeforeEach
    void setup() {
        when(awsConfig.getEndpoint()).thenReturn(LOCAL_ENDPOINT);
        when(awsConfig.getAccessKey()).thenReturn(DUMMY_CREDENTIAL);
        when(awsConfig.getSecretKey()).thenReturn(DUMMY_CREDENTIAL);
        when(awsConfig.getRegion()).thenReturn(AWS_REGION);
        
        accountDao = new AccountDaoImpl(awsConfig, authUtil);
        setField(accountDao, DYNAMO_TABLE_FIELD, accountDynamoDbTable);
    }

    @Test
    void getAccount_success() {
        Account expectedResult = Instancio.of(Account.class)
                .set(field(Account::getUser), USER)
                .create();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        when(accountDynamoDbTable.getItem(any(Key.class))).thenReturn(expectedResult);

        Account result = accountDao.getAccount(ID);

        assertEquals(expectedResult, result);
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).getItem(any(Key.class));
    }

    @Test
    void getAccount_handlesSdkException() {
        SdkClientException sdkClientException = SdkClientException.builder()
                .message(CONNECTION_ERROR_MSG)
                .build();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        when(accountDynamoDbTable.getItem(any(Key.class))).thenThrow(sdkClientException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> accountDao.getAccount(ID));

        assertEquals(CONNECTION_ERROR_MSG, e.getMessage());
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).getItem(any(Key.class));
    }

    @Test
    void getAccount_handlesNullAccount() {
        when(authUtil.getCurrentUsername()).thenReturn(USER);
        when(accountDynamoDbTable.getItem(any(Key.class))).thenReturn(null);

        Account result = accountDao.getAccount(ID);

        assertNull(result);
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).getItem(any(Key.class));
    }

    @Test
    void getAccount_ensuresUsernameHasToBeTheSame() {
        Account expectedResult = Instancio.of(Account.class)
                .set(field(Account::getUser), USER)
                .create();

        when(authUtil.getCurrentUsername()).thenReturn(DUMMY_CREDENTIAL);
        when(accountDynamoDbTable.getItem(any(Key.class))).thenReturn(expectedResult);

        Account result = accountDao.getAccount(ID);

        assertNull(result);
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).getItem(any(Key.class));
    }

    @Test
    void addAccount_success() {
        Account account = Instancio.of(Account.class)
                .set(field(Account::getUser), USER)
                .create();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        doNothing().when(accountDynamoDbTable).putItem(any(PutItemEnhancedRequest.class));

        Account result = accountDao.addAccount(account);

        assertEquals(account, result);
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void addAccount_handlesConditionalCheckException() {
        ConditionalCheckFailedException conditionalCheckFailedException = ConditionalCheckFailedException.builder()
                .message(CONNECTION_ERROR_MSG)
                .build();
        Account account = Instancio.of(Account.class)
                .set(field(Account::getId), ID)
                .create();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        doThrow(conditionalCheckFailedException).when(accountDynamoDbTable).putItem(any(PutItemEnhancedRequest.class));

        ResourceConflictException e = assertThrows(ResourceConflictException.class, () -> accountDao.addAccount(account));

        assertEquals(CONFLICT_MSG, e.getMessage());
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void addAccount_handlesSdkException() {
        SdkClientException sdkClientException = SdkClientException.builder()
                .message(CONNECTION_ERROR_MSG)
                .build();
        Account account = Instancio.of(Account.class)
                .set(field(Account::getId), ID)
                .create();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        doThrow(sdkClientException).when(accountDynamoDbTable).putItem(any(PutItemEnhancedRequest.class));

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> accountDao.addAccount(account));

        assertEquals(CONNECTION_ERROR_MSG, e.getMessage());
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void deleteAccount_success() {
        Account expectedResult = Instancio.of(Account.class)
                .set(field(Account::getUser), USER)
                .create();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        when(accountDynamoDbTable.deleteItem(any(DeleteItemEnhancedRequest.class))).thenReturn(expectedResult);

        Account result = accountDao.deleteAccount(ID);

        assertEquals(expectedResult, result);
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).deleteItem(any(DeleteItemEnhancedRequest.class));
    }

    @Test
    void deleteAccount_handlesSdkException() {
        SdkClientException sdkClientException = SdkClientException.builder()
                .message(CONNECTION_ERROR_MSG)
                .build();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        when(accountDynamoDbTable.deleteItem(any(DeleteItemEnhancedRequest.class))).thenThrow(sdkClientException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> accountDao.deleteAccount(ID));

        assertEquals(CONNECTION_ERROR_MSG, e.getMessage());
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).deleteItem(any(DeleteItemEnhancedRequest.class));
    }

    @Test
    void updateAccount_success() {
        Account account = Instancio.of(Account.class)
                .set(field(Account::getUser), USER)
                .create();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        when(accountDynamoDbTable.updateItem(any(UpdateItemEnhancedRequest.class))).thenReturn(account);

        Account result = accountDao.updateAccount(ID, account);

        assertEquals(account, result);
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).updateItem(any(UpdateItemEnhancedRequest.class));
    }

    @Test
    void updateAccount_handlesSdkException() {
        SdkClientException sdkClientException = SdkClientException.builder()
                .message(CONNECTION_ERROR_MSG)
                .build();
        Account account = Instancio.of(Account.class)
                .set(field(Account::getUser), USER)
                .create();

        when(authUtil.getCurrentUsername()).thenReturn(USER);
        when(accountDynamoDbTable.updateItem(any(UpdateItemEnhancedRequest.class))).thenThrow(sdkClientException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> accountDao.updateAccount(ID, account));

        assertEquals(CONNECTION_ERROR_MSG, e.getMessage());
        verify(authUtil).getCurrentUsername();
        verify(accountDynamoDbTable).updateItem(any(UpdateItemEnhancedRequest.class));
    }
}