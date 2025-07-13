package com.financetrackingbackend.services;

import com.example.model.Account;
import com.financetrackingbackend.dao.AccountDao;
import com.financetrackingbackend.exceptions.ResourceNotFoundException;
import com.financetrackingbackend.services.impl.AccountServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    private AccountDao accountDao;
    @InjectMocks
    private AccountServiceImpl accountService;

    private final static String ID = "123";
    private final static String EXPECTED_ERROR_MSG = "Account was not found with id: 123";

    @Test
    void addAccount_callsAccountDaoAddAccount(){
        Account account = Instancio.of(Account.class).create();
        when(accountDao.addAccount(any())).thenReturn(account);
        Account result = accountService.addAccount(account);
        assertEquals(account, result);
        verify(accountDao, times(1)).addAccount(account);
    }

    @Test
    void getAccount_callsAccountDaoGetAccount(){
        Account account = Instancio.of(Account.class).create();
        when(accountDao.getAccount(any())).thenReturn(account);
        Account result = accountService.getAccount(ID);
        assertEquals(account, result);
        verify(accountDao, times(1)).getAccount(ID);
    }

    @Test
    void getAccount_handlesNullReturnFromAccountDao(){
        when(accountDao.getAccount(any())).thenReturn(null);
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> accountService.getAccount(ID));
        assertEquals(EXPECTED_ERROR_MSG, e.getMessage());
        verify(accountDao, times(1)).getAccount(ID);
    }

    @Test
    void deleteAccount_callsAccountDaoDeleteAccount(){
        Account account = Instancio.of(Account.class).create();
        when(accountDao.deleteAccount(any())).thenReturn(account);
        Account result = accountService.deleteAccount(ID);
        assertEquals(account, result);
        verify(accountDao, times(1)).deleteAccount(ID);
    }

    @Test
    void deleteAccount_handlesNullReturnFromAccountDao(){
        when(accountDao.deleteAccount(any())).thenThrow(ConditionalCheckFailedException.class);
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccount(ID));
        assertEquals(EXPECTED_ERROR_MSG, e.getMessage());
        verify(accountDao, times(1)).deleteAccount(ID);
    }

    @Test
    void updateAccount_callsAccountDaoUpdateAccount(){
        Account account = Instancio.of(Account.class).create();
        when(accountDao.updateAccount(anyString(), any())).thenReturn(account);
        Account result = accountService.updateAccount(ID, account);
        assertEquals(account, result);
        verify(accountDao, times(1)).updateAccount(ID, account);
    }

    @Test
    void updateAccount_handlesNullReturnFromAccountDao(){
        Account account = Instancio.of(Account.class).create();
        when(accountDao.updateAccount(anyString(), any())).thenThrow(ConditionalCheckFailedException.class);
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> accountService.updateAccount(ID, account));
        assertEquals(EXPECTED_ERROR_MSG, e.getMessage());
        verify(accountDao, times(1)).updateAccount(ID, account);
    }
}
