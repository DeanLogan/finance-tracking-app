package com.financetrackingbackend.services;

import com.example.model.UlsterbankAccount;
import com.example.model.UlsterbankAmount;
import com.example.model.UlsterbankBalance;
import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.services.impl.UlsterbankAccountServiceImpl;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UlsterbankAccountServiceImplTest {
    @Mock
    private UlsterbankDao ulsterbankDao;
    @InjectMocks
    private UlsterbankAccountServiceImpl ulsterbankAccountService;
    private Model<UlsterbankAccount> accountModel;
    private Model<UlsterbankBalance> balanceModel;
    private Model<UlsterbankAmount> amountModel;

    private static final String ACCESS_TOKEN = "token";
    private static final String ACCOUNT_ID_ONE = "123";
    private static final String ACCOUNT_ID_TWO = "321";

    @BeforeEach
    void setup() {
        accountModel = Instancio.of(UlsterbankAccount.class)
                .set(field(UlsterbankAccount::getAccountId), ACCOUNT_ID_ONE)
                .toModel();

        amountModel = Instancio.of(UlsterbankAmount.class)
                .set(field(UlsterbankAmount::getAmount), 22.0f)
                .toModel();

        balanceModel = Instancio.of(UlsterbankBalance.class)
                .set(field(UlsterbankBalance::getAccountId), ACCOUNT_ID_ONE)
                .set(field(UlsterbankBalance::getAmount), Instancio.of(amountModel).create())
                .toModel();
    }

    @Test
    void getBalanceForAllAccounts_correctlyCalcsBalance() {
        List<UlsterbankAccount> accounts = Instancio.ofList(accountModel).size(1).create();
        List<UlsterbankBalance> balances = Instancio.ofList(balanceModel).size(5).create();

        when(ulsterbankDao.getAccounts(anyString())).thenReturn(accounts);
        when(ulsterbankDao.getBalances(anyString(), anyString())).thenReturn(balances);

        float result = ulsterbankAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);

        assertEquals(110.0f, result);
        verify(ulsterbankDao).getAccounts(ACCESS_TOKEN);
        verify(ulsterbankDao).getBalances(ACCESS_TOKEN, ACCOUNT_ID_ONE);
    }

    @Test
    void getBalanceForAllAccounts_emptyAccountReturnsZero() {
        when(ulsterbankDao.getAccounts(anyString())).thenReturn(Collections.emptyList());

        float result = ulsterbankAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);

        assertEquals(0.0f, result);
        verify(ulsterbankDao).getAccounts(ACCESS_TOKEN);
        verifyNoMoreInteractions(ulsterbankDao);
    }

    @Test
    void getBalanceForAllAccounts_nullBalancesReturnsZero() {
        List<UlsterbankAccount> accounts = Instancio.ofList(accountModel).size(1).create();

        when(ulsterbankDao.getAccounts(anyString())).thenReturn(accounts);
        when(ulsterbankDao.getBalances(anyString(), anyString())).thenReturn(null);

        float result = ulsterbankAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);

        assertEquals(0.0f, result);
        verify(ulsterbankDao).getAccounts(ACCESS_TOKEN);
        verify(ulsterbankDao).getBalances(ACCESS_TOKEN, ACCOUNT_ID_ONE);
    }

    @Test
    void getBalanceForAllAccounts_balancesWithNullAmountsIsIgnored() {
        List<UlsterbankAccount> accounts = Instancio.ofList(accountModel).size(1).create();
        List<UlsterbankBalance> balances = Instancio.ofList(balanceModel).size(5).create();

        UlsterbankAmount amountWithNull = Instancio.of(amountModel)
                .set(field(UlsterbankAmount::getAmount), null)
                .create();

        UlsterbankBalance balanceWithNullAmount = Instancio.of(UlsterbankBalance.class)
                .set(field(UlsterbankBalance::getAccountId), ACCOUNT_ID_ONE)
                .set(field(UlsterbankBalance::getAmount), amountWithNull)
                .create();

        balances.add(balanceWithNullAmount);

        when(ulsterbankDao.getAccounts(anyString())).thenReturn(accounts);
        when(ulsterbankDao.getBalances(anyString(), anyString())).thenReturn(balances);

        float result = ulsterbankAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);

        assertEquals(110.0f, result);
        verify(ulsterbankDao).getAccounts(ACCESS_TOKEN);
        verify(ulsterbankDao).getBalances(ACCESS_TOKEN, ACCOUNT_ID_ONE);
    }

    @Test
    void getBalanceForAllAccounts_balancesWithNullUlsterbankAmountIsIgnored() {
        List<UlsterbankAccount> accounts = Instancio.ofList(accountModel).size(1).create();
        List<UlsterbankBalance> balances = Instancio.ofList(balanceModel).size(5).create();

        UlsterbankBalance balanceWithNullAmount = Instancio.of(UlsterbankBalance.class)
                .set(field(UlsterbankBalance::getAccountId), ACCOUNT_ID_ONE)
                .set(field(UlsterbankBalance::getAmount), null)
                .create();

        balances.add(balanceWithNullAmount);

        when(ulsterbankDao.getAccounts(anyString())).thenReturn(accounts);
        when(ulsterbankDao.getBalances(anyString(), anyString())).thenReturn(balances);

        float result = ulsterbankAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);

        assertEquals(110.0f, result);
        verify(ulsterbankDao).getAccounts(ACCESS_TOKEN);
        verify(ulsterbankDao).getBalances(ACCESS_TOKEN, ACCOUNT_ID_ONE);
    }

    @Test
    void getBalanceForAllAccounts_balancesWithNullBalanceIsIgnored() {
        List<UlsterbankAccount> accounts = Instancio.ofList(accountModel).size(1).create();
        List<UlsterbankBalance> balances = Instancio.ofList(balanceModel).size(5).create();

        balances.add(null);

        when(ulsterbankDao.getAccounts(anyString())).thenReturn(accounts);
        when(ulsterbankDao.getBalances(anyString(), anyString())).thenReturn(balances);

        float result = ulsterbankAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);

        assertEquals(110.0f, result);
        verify(ulsterbankDao).getAccounts(ACCESS_TOKEN);
        verify(ulsterbankDao).getBalances(ACCESS_TOKEN, ACCOUNT_ID_ONE);
    }

    @Test
    void getBalanceForAllAccounts_multipleAccounts_sumsAll() {
        List<UlsterbankAccount> accounts = Instancio.ofList(accountModel).size(1).create();
        List<UlsterbankBalance> balancesForAccountOne = Instancio.ofList(balanceModel).size(5).create();
        
        UlsterbankAccount accountTwo = Instancio.of(accountModel)
                .set(field(UlsterbankAccount::getAccountId), "321")
                .create();

        accounts.add(accountTwo);

        UlsterbankAmount amountTwo = Instancio.of(amountModel)
                .set(field(UlsterbankAmount::getAmount), 20.0f)
                .create();

        List<UlsterbankBalance> balancesForAccountTwo = Instancio.ofList(balanceModel)
                .size(5)
                .set(field(UlsterbankBalance::getAccountId), ACCOUNT_ID_TWO)
                .set(field(UlsterbankBalance::getAmount), amountTwo)
                .create();
        
        when(ulsterbankDao.getAccounts(anyString())).thenReturn(accounts);
        when(ulsterbankDao.getBalances(anyString(), eq(ACCOUNT_ID_ONE))).thenReturn(balancesForAccountOne);
        when(ulsterbankDao.getBalances(anyString(), eq(ACCOUNT_ID_TWO))).thenReturn(balancesForAccountTwo);

        float result = ulsterbankAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);

        assertEquals(210.0f, result);
        verify(ulsterbankDao).getAccounts(ACCESS_TOKEN);
        verify(ulsterbankDao).getBalances(ACCESS_TOKEN, ACCOUNT_ID_ONE);
        verify(ulsterbankDao).getBalances(ACCESS_TOKEN, ACCOUNT_ID_TWO);
    }
}
