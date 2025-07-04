package com.financetrackingbackend.services;

import com.example.model.Currency;
import com.example.model.MonzoAccount;
import com.example.model.MonzoPot;
import com.example.model.MonzoPots;
import com.example.model.MonzoTransaction;
import com.example.model.MonzoTransactionsResponse;
import com.example.model.MonzoUserInfoResponse;
import com.financetrackingbackend.dao.MonzoDao;
import com.financetrackingbackend.services.impl.MonzoAccountServiceImpl;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonzoAccountServiceImplTest {
    @Mock
    private MonzoDao monzoDao;
    @InjectMocks
    private MonzoAccountServiceImpl monzoAccountService;

    private Model<MonzoAccount> monzoAccountModel;
    private Model<MonzoPot> monzoPotModel;
    private Model<MonzoPots> monzoPotsModel;
    private Model<MonzoUserInfoResponse> monzoUserInfoModel;
    private Model<MonzoTransactionsResponse> monzoTransactionsResponseModel;
    private Model<MonzoTransaction> monzoTransactionModel;

    private static final List<String> COMPARE_ACCOUNT_FIELDS = List.of("balance", "totalBalance", "currency", "spendToday");
    private static final List<String> COMPARE_INFO_FIELDS = List.of("accounts.id", "totalBalance");
    private static final String ACCESS_TOKEN = "123abc";
    private static final String ID = "123";
    private static final String GBP = "GBP";
    private static final String POTS_BALANCE = "pots.balance";

    @BeforeEach
    void setup() {
        monzoAccountModel = Instancio.of(MonzoAccount.class)
                .set(field(MonzoAccount::getBalance),100F)
                .set(field(MonzoAccount::getTotalBalance),100F)
                .set(field(MonzoAccount::getCurrency),Currency.valueOf(GBP))
                .set(field(MonzoAccount::getSpendToday),100)
                .set(field(MonzoAccount::getId),ID)
                .toModel();

        monzoPotModel = Instancio.of(MonzoPot.class)
                .set(field(MonzoPot::getDeleted),false)
                .set(field(MonzoPot::getBalance),100F)
                .toModel();

        List<MonzoPot> pots = Instancio.ofList(monzoPotModel).size(5).create();
        MonzoPot deletedPot = Instancio.of(monzoPotModel)
                .set(field(MonzoPot::getDeleted),true)
                .create();
        pots.add(deletedPot);

        monzoPotsModel = Instancio.of(MonzoPots.class)
                .set(field(MonzoPots::getPots),pots)
                .toModel();

        monzoUserInfoModel = Instancio.of(MonzoUserInfoResponse.class)
                .set(field(MonzoUserInfoResponse::getTotalBalance), 5F)
                .set(field(MonzoUserInfoResponse::getAccounts), Instancio.ofList(monzoAccountModel).size(5).create())
                .toModel();

        monzoTransactionModel = Instancio.of(MonzoTransaction.class)
                .set(field(MonzoTransaction::getId), ID)
                .toModel();

        monzoTransactionsResponseModel = Instancio.of(MonzoTransactionsResponse.class)
                .set(field(MonzoTransactionsResponse::getTransactions), Instancio.ofList(monzoTransactionModel).size(5).create())
                .toModel();
    }

    @Test
    void getBalanceForAccount_updatesFieldsWhenNotNull(){
        MonzoAccount updateFields = Instancio.of(monzoAccountModel).create();

        when(monzoDao.getAccount(anyString(), anyString())).thenReturn(updateFields);

        MonzoAccount expectedResult = Instancio.of(monzoAccountModel)
                .set(field(MonzoAccount::getBalance),1F)
                .set(field(MonzoAccount::getTotalBalance),1F)
                .set(field(MonzoAccount::getSpendToday),1)
                .create();

        MonzoAccount result = monzoAccountService.getBalanceForAccount(ACCESS_TOKEN, Instancio.of(MonzoAccount.class).create());
        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(COMPARE_ACCOUNT_FIELDS.toArray(new String[0]))
                .isEqualTo(expectedResult);
    }

    @Test
    void getBalanceForAccount_returnsAccountIfUpdatedFieldsIsNull(){
        when(monzoDao.getAccount(anyString(), anyString())).thenReturn(null);

        MonzoAccount expectedResult = Instancio.of(monzoAccountModel).create();

        MonzoAccount result = monzoAccountService.getBalanceForAccount(ACCESS_TOKEN, Instancio.of(monzoAccountModel).create());
        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(COMPARE_ACCOUNT_FIELDS.toArray(new String[0]))
                .isEqualTo(expectedResult);
    }

    @Test
    void getBalanceForAccount_updatesFieldsHandlesNullBalance(){
        MonzoAccount updateFields = Instancio.of(monzoAccountModel)
                .set(field(MonzoAccount::getBalance),null)
                .create();

        when(monzoDao.getAccount(anyString(), anyString())).thenReturn(updateFields);

        MonzoAccount expectedResult = Instancio.of(monzoAccountModel)
                .set(field(MonzoAccount::getBalance),0F)
                .set(field(MonzoAccount::getTotalBalance),1F)
                .set(field(MonzoAccount::getSpendToday),1)
                .create();

        MonzoAccount result = monzoAccountService.getBalanceForAccount(ACCESS_TOKEN, Instancio.of(MonzoAccount.class).create());
        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(COMPARE_ACCOUNT_FIELDS.toArray(new String[0]))
                .isEqualTo(expectedResult);
    }

    @Test
    void getBalanceForAccount_updatesFieldsHandlesNullTotalBalance(){
        MonzoAccount updateFields = Instancio.of(monzoAccountModel)
                .set(field(MonzoAccount::getTotalBalance),null)
                .create();

        when(monzoDao.getAccount(anyString(), anyString())).thenReturn(updateFields);

        MonzoAccount expectedResult = Instancio.of(monzoAccountModel)
                .set(field(MonzoAccount::getBalance),1F)
                .set(field(MonzoAccount::getTotalBalance),0F)
                .set(field(MonzoAccount::getSpendToday),1)
                .create();

        MonzoAccount result = monzoAccountService.getBalanceForAccount(ACCESS_TOKEN, Instancio.of(MonzoAccount.class).create());
        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(COMPARE_ACCOUNT_FIELDS.toArray(new String[0]))
                .isEqualTo(expectedResult);
    }

    @Test
    void getBalanceForAccount_updatesFieldsHandlesNullSpendToday(){
        MonzoAccount updateFields = Instancio.of(monzoAccountModel)
                .set(field(MonzoAccount::getSpendToday),null)
                .create();

        when(monzoDao.getAccount(anyString(), anyString())).thenReturn(updateFields);

        MonzoAccount expectedResult = Instancio.of(monzoAccountModel)
                .set(field(MonzoAccount::getBalance),1F)
                .set(field(MonzoAccount::getTotalBalance),1F)
                .set(field(MonzoAccount::getSpendToday),0)
                .create();

        MonzoAccount result = monzoAccountService.getBalanceForAccount(ACCESS_TOKEN, Instancio.of(MonzoAccount.class).create());
        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(COMPARE_ACCOUNT_FIELDS.toArray(new String[0]))
                .isEqualTo(expectedResult);
    }

    @Test
    void getBalanceForAllAccounts_correctlyCalcsBalance(){
        List<MonzoAccount> accounts = Instancio.ofList(monzoAccountModel).size(5).create();
        when(monzoDao.getAccounts(anyString())).thenReturn(accounts);
        float result = monzoAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);
        assertEquals(500F, result);
    }

    @Test
    void getBalanceForAllAccounts_handlesNullAccounts(){
        when(monzoDao.getAccounts(anyString())).thenReturn(null);
        float result = monzoAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);
        assertEquals(0F, result);
    }

    @Test
    void getBalanceForAllAccounts_handlesEmptyAccounts(){
        when(monzoDao.getAccounts(anyString())).thenReturn(Collections.emptyList());
        float result = monzoAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);
        assertEquals(0F, result);
    }

    @Test
    void getBalanceForAllAccounts_handlesAccountWithNullBalance(){
        List<MonzoAccount> accounts = Instancio.ofList(monzoAccountModel).size(5).create();
        MonzoAccount account = Instancio.of(monzoAccountModel)
                .set(field(MonzoAccount::getBalance),null)
                .create();
        accounts.add(account);

        when(monzoDao.getAccounts(anyString())).thenReturn(accounts);

        float result = monzoAccountService.getBalanceForAllAccounts(ACCESS_TOKEN);
        assertEquals(500F, result);
    }

    @Test
    void getAllActivePotsForAccount_correctlyUpdatesBalanceAndFiltersPots(){
        MonzoPots monzoPots = Instancio.of(monzoPotsModel).create();

        when(monzoDao.getAllPots(anyString(), anyString())).thenReturn(monzoPots);

        List<MonzoPot> pots = Instancio.ofList(monzoPotModel)
                .size(5)
                .set(field(MonzoPot::getBalance), 1F)
                .create();

        MonzoPots expectedResult = Instancio.of(MonzoPots.class)
                .set(field(MonzoPots::getPots),pots)
                .create();

        MonzoPots result = monzoAccountService.getAllActivePotsForAccount(ACCESS_TOKEN, ID);
        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(POTS_BALANCE)
                .isEqualTo(expectedResult);
    }

    @Test
    void getAllActivePotsForAccount_handlesNullBalance(){
        List<MonzoPot> testPots = Instancio.ofList(monzoPotModel).size(5).create();

        MonzoPot testPot = Instancio.of(monzoPotModel)
                .set(field(MonzoPot::getBalance), null)
                .create();
        testPots.add(testPot);

        MonzoPots monzoPots = Instancio.of(monzoPotsModel)
                .set(field(MonzoPots::getPots), testPots)
                .create();

        when(monzoDao.getAllPots(anyString(), anyString())).thenReturn(monzoPots);

        List<MonzoPot> expectedPots = Instancio.ofList(monzoPotModel)
                .size(5)
                .set(field(MonzoPot::getBalance), 1F)
                .create();

        MonzoPot expectedPot = Instancio.of(monzoPotModel)
                .set(field(MonzoPot::getBalance), 0F)
                .create();
        expectedPots.add(expectedPot);

        MonzoPots expectedResult = Instancio.of(MonzoPots.class)
                .set(field(MonzoPots::getPots),expectedPots)
                .create();

        MonzoPots result = monzoAccountService.getAllActivePotsForAccount(ACCESS_TOKEN, ID);
        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(POTS_BALANCE)
                .isEqualTo(expectedResult);
    }

    @Test
    void getAllActivePotsForAccount_handlesEmptyPotsList(){
        MonzoPots monzoPots = Instancio.of(monzoPotsModel)
                .set(field(MonzoPots::getPots), Collections.emptyList())
                .create();

        when(monzoDao.getAllPots(anyString(), anyString())).thenReturn(monzoPots);

        MonzoPots result = monzoAccountService.getAllActivePotsForAccount(ACCESS_TOKEN, ID);
        assertTrue(result.getPots().isEmpty());
    }

    @Test
    void getAllActivePotsForAccount_handlesNullPotList(){
        MonzoPots monzoPots = Instancio.of(monzoPotsModel)
                .set(field(MonzoPots::getPots), null)
                .create();

        when(monzoDao.getAllPots(anyString(), anyString())).thenReturn(monzoPots);

        MonzoPots result = monzoAccountService.getAllActivePotsForAccount(ACCESS_TOKEN, ID);
        assertTrue(result.getPots().isEmpty());
    }

    @Test
    void getAllActivePotsForAccount_handlesNullPots(){
        when(monzoDao.getAllPots(anyString(), anyString())).thenReturn(null);

        MonzoPots result = monzoAccountService.getAllActivePotsForAccount(ACCESS_TOKEN, ID);
        assertTrue(result.getPots().isEmpty());
    }

    @Test
    void getUserInfo_correctlyReturnsInfo(){
        List<MonzoAccount> accounts = Instancio.ofList(monzoAccountModel).size(5).create();
        MonzoAccount updateFields = Instancio.of(monzoAccountModel).create();
        MonzoPots monzoPots = Instancio.of(monzoPotsModel).create();

        when(monzoDao.getAccounts(anyString())).thenReturn(accounts);
        when(monzoDao.getAccount(anyString(), anyString())).thenReturn(updateFields);
        when(monzoDao.getAllPots(anyString(), anyString())).thenReturn(monzoPots);

        MonzoUserInfoResponse result = monzoAccountService.getUserInfo(ACCESS_TOKEN);

        MonzoUserInfoResponse expectedResult = Instancio.of(monzoUserInfoModel).create();

        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(COMPARE_INFO_FIELDS.toArray(new String[0]))
                .isEqualTo(expectedResult);
    }

    @Test
    void getUserInfo_handlesNullAccount(){
        List<MonzoAccount> accounts = Instancio.ofList(monzoAccountModel).size(5).create();
        MonzoAccount updateFields = Instancio.of(monzoAccountModel).create();
        MonzoPots monzoPots = Instancio.of(monzoPotsModel).create();
        accounts.add(null);

        when(monzoDao.getAccounts(anyString())).thenReturn(accounts);
        when(monzoDao.getAccount(anyString(), anyString()))
                .thenReturn(updateFields, updateFields, updateFields, updateFields, updateFields, null);
        when(monzoDao.getAllPots(anyString(), anyString())).thenReturn(monzoPots);

        MonzoUserInfoResponse result = monzoAccountService.getUserInfo(ACCESS_TOKEN);

        MonzoUserInfoResponse expectedResult = Instancio.of(monzoUserInfoModel).create();

        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(COMPARE_INFO_FIELDS.toArray(new String[0]))
                .isEqualTo(expectedResult);
    }

    @Test
    void listTransactions_correctlyListTransactions(){
        MonzoTransactionsResponse monzoTransactionsResponse = Instancio.of(monzoTransactionsResponseModel).create();

        when(monzoDao.getTransactions(anyString(), anyString())).thenReturn(monzoTransactionsResponse);

        List<MonzoTransaction> result = monzoAccountService.listTransactions(ACCESS_TOKEN, ID);
        List<MonzoTransaction> expectedResponse = monzoTransactionsResponse.getTransactions();

        assertEquals(result, expectedResponse);
        verify(monzoDao, times(1)).getTransactions(ACCESS_TOKEN, ID);
    }

    @Test
    void listTransactions_handlesNullTransactionResponse(){
        when(monzoDao.getTransactions(anyString(), anyString())).thenReturn(null);
        List<MonzoTransaction> result = monzoAccountService.listTransactions(ACCESS_TOKEN, ID);
        assertTrue(result.isEmpty());
        verify(monzoDao, times(1)).getTransactions(ACCESS_TOKEN, ID);
    }

    @Test
    void listTransactions_handlesNullTransactionList(){
        MonzoTransactionsResponse monzoTransactionsResponse = Instancio.of(monzoTransactionsResponseModel)
                .set(field(MonzoTransactionsResponse::getTransactions), null)
                .create();

        when(monzoDao.getTransactions(anyString(), anyString())).thenReturn(monzoTransactionsResponse);

        List<MonzoTransaction> result = monzoAccountService.listTransactions(ACCESS_TOKEN, ID);
        assertTrue(result.isEmpty());
        verify(monzoDao, times(1)).getTransactions(ACCESS_TOKEN, ID);
    }
}
