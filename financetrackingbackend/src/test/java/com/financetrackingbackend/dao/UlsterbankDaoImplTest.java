package com.financetrackingbackend.dao;

import com.example.model.UlsterbankAccessToken;
import com.example.model.UlsterbankAccount;
import com.example.model.UlsterbankBalance;
import com.example.model.UlsterbankData;
import com.example.model.UlsterbankGeneralResponse;
import com.example.model.UlsterbankTransaction;
import com.financetrackingbackend.configuration.UlsterbankConfig;
import com.financetrackingbackend.dao.impl.UlsterbankDaoImpl;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters.FormInserter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.just;

@ExtendWith(MockitoExtension.class)
public class UlsterbankDaoImplTest {
    @Mock private WebClient webClient;
    @Mock private Builder webClientBuilder;
    @Mock private RequestBodyUriSpec uriSpecForPost;
    @Mock private RequestHeadersUriSpec uriSpecForGet;
    @Mock private RequestBodySpec bodySpec;
    @Mock private RequestHeadersSpec headersSpec;
    @Mock private ResponseSpec responseSpec;
    @Mock private UlsterbankConfig config;

    private UlsterbankDaoImpl ulsterbankDao;

    private static final String ID = "123";
    private static final String CODE = "code";
    private static final String TOKEN_PATH = "/token";
    private static final String ACCOUNT_ACCESS_CONSENTS_PATH = "open-banking/v3.1/aisp/account-access-consents";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String SCOPE = "scope";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ACCOUNTS = "accounts";
    private static final String OPENID_ACCOUNTS = "openid accounts";
    private static final String DATA = "data";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String AUTH_CODE = "authorization_code";
    private static final String AUTH = "Authorization";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String UNSUPPORTED_GRANT_TYPE = "Unsupported grant type: code";
    private static final String UB_ERROR_MSG = "UB request failed: Service unavailable";
    private static final String SERVICE_UNAVAILABLE = "Service unavailable";
    private static final String ACCOUNTS_PATH = "accounts/";
    private static final String BEARER = "Bearer 123";
    private static final String APPLICATION_JSON = "application/json";
    private static final String TRANSACTIONS_PATH = "accounts/123/transactions";
    private static final String BALANCES_PATH = "accounts/123/balances";

    @BeforeEach
    void setup() {
        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        ulsterbankDao = new UlsterbankDaoImpl(webClientBuilder, config);
    }

    @Test
    void tokenRequest_clientCredSuccess() {
        UlsterbankAccessToken expectedToken = Instancio.create(UlsterbankAccessToken.class);

        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(CONTENT_TYPE, FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UlsterbankAccessToken.class)).thenReturn(just(expectedToken));

        UlsterbankAccessToken result = ulsterbankDao.tokenRequest(CODE, CLIENT_CREDENTIALS);

        assertEquals(expectedToken, result);
        verify(bodySpec).body(argThat(arg -> bodyHasAllKeyValues(arg, Map.of(
                SCOPE, ACCOUNTS,
                CLIENT_ID, ID,
                CLIENT_SECRET, ID,
                GRANT_TYPE, CLIENT_CREDENTIALS
        ))));
    }

    @Test
    void tokenRequest_authCodeSuccess() {
        UlsterbankAccessToken expectedToken = Instancio.create(UlsterbankAccessToken.class);

        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(CONTENT_TYPE, FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UlsterbankAccessToken.class)).thenReturn(just(expectedToken));

        UlsterbankAccessToken result = ulsterbankDao.tokenRequest(CODE, AUTH_CODE);

        assertEquals(expectedToken, result);
        verify(bodySpec).body(argThat(arg -> bodyHasAllKeyValues(arg, Map.of(
                SCOPE, OPENID_ACCOUNTS,
                CLIENT_ID, ID,
                CLIENT_SECRET, ID,
                CODE, CODE,
                GRANT_TYPE, AUTH_CODE
        ))));
    }

    @Test
    void tokenRequest_refreshCodeSuccess() {
        UlsterbankAccessToken expectedToken = Instancio.create(UlsterbankAccessToken.class);

        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(CONTENT_TYPE, FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UlsterbankAccessToken.class)).thenReturn(just(expectedToken));

        UlsterbankAccessToken result = ulsterbankDao.tokenRequest(CODE, REFRESH_TOKEN);

        assertEquals(expectedToken, result);
        verify(bodySpec).body(argThat(arg -> bodyHasAllKeyValues(arg, Map.of(
                REFRESH_TOKEN, CODE,
                CLIENT_ID, ID,
                CLIENT_SECRET, ID,
                GRANT_TYPE, REFRESH_TOKEN
        ))));
    }

    @Test
    void tokenRequest_invalidCode() {
        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ulsterbankDao.tokenRequest(CODE, CODE));
        assertEquals(UNSUPPORTED_GRANT_TYPE, e.getMessage());
    }

    @Test
    void tokenRequest_serviceUnavailableExceptionThrown() {
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                SERVICE_UNAVAILABLE,
                503,
                SERVICE_UNAVAILABLE,
                null, null, null, null
        );
        
        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(CONTENT_TYPE, FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenThrow(webClientResponseException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> ulsterbankDao.tokenRequest(CODE, CLIENT_CREDENTIALS));
        assertEquals(UB_ERROR_MSG, e.getMessage());
    }

    @Test
    void getConsentResponse_successRequest() {
        UlsterbankGeneralResponse expectedResponse = Instancio.create(UlsterbankGeneralResponse.class);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(ACCOUNT_ACCESS_CONSENTS_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(AUTH, BEARER)).thenReturn(bodySpec);
        when(bodySpec.header(CONTENT_TYPE, APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UlsterbankGeneralResponse.class)).thenReturn(just(expectedResponse));

        UlsterbankGeneralResponse result = ulsterbankDao.getConsentResponse(ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForPost).uri(ACCOUNT_ACCESS_CONSENTS_PATH);
        verify(bodySpec).header(AUTH, BEARER);
        verify(bodySpec).header(CONTENT_TYPE, APPLICATION_JSON);
    }

    @Test
    void getConsentResponse_serviceUnavailableExceptionThrown() {
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                SERVICE_UNAVAILABLE,
                503,
                SERVICE_UNAVAILABLE,
                null, null, null, null
        );

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(ACCOUNT_ACCESS_CONSENTS_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(AUTH, BEARER)).thenReturn(bodySpec);
        when(bodySpec.header(CONTENT_TYPE, APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenThrow(webClientResponseException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> ulsterbankDao.getConsentResponse(ID));
        assertEquals(UB_ERROR_MSG, e.getMessage());
    }

    @Test
    void getAccounts_successRequest() {
        List<UlsterbankAccount> expectedResponse = Instancio.ofList(UlsterbankAccount.class).create();
        UlsterbankData data = Instancio.of(UlsterbankData.class)
                .set(field(UlsterbankData::getAccounts), expectedResponse)
                .create();
        UlsterbankGeneralResponse generalResponse = Instancio.of(UlsterbankGeneralResponse.class)
                .set(field(UlsterbankGeneralResponse::getData), data)
                .create();

        when(config.getAccountsUrl()).thenReturn(ACCOUNTS);

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(ACCOUNTS_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(AUTH, BEARER)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UlsterbankGeneralResponse.class)).thenReturn(just(generalResponse));

        List<UlsterbankAccount> result = ulsterbankDao.getAccounts(ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForGet).uri(ACCOUNTS_PATH);
        verify(bodySpec).header(AUTH, BEARER);
    }

    @Test
    void getAccounts_serviceUnavailableExceptionThrown() {
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                SERVICE_UNAVAILABLE,
                503,
                SERVICE_UNAVAILABLE,
                null, null, null, null
        );

        when(config.getAccountsUrl()).thenReturn(ACCOUNTS);

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(ACCOUNTS_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(AUTH, BEARER)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenThrow(webClientResponseException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> ulsterbankDao.getAccounts(ID));
        assertEquals(UB_ERROR_MSG, e.getMessage());
    }

    @Test
    void getTransactions_successRequest() {
        List<UlsterbankTransaction> expectedResponse = Instancio.ofList(UlsterbankTransaction.class).create();
        UlsterbankData data = Instancio.of(UlsterbankData.class)
                .set(field(UlsterbankData::getTransactions), expectedResponse)
                .create();
        UlsterbankGeneralResponse generalResponse = Instancio.of(UlsterbankGeneralResponse.class)
                .set(field(UlsterbankGeneralResponse::getData), data)
                .create();

        when(config.getAccountsUrl()).thenReturn(ACCOUNTS);

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(TRANSACTIONS_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(AUTH, BEARER)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UlsterbankGeneralResponse.class)).thenReturn(just(generalResponse));

        List<UlsterbankTransaction> result = ulsterbankDao.getTransactions(ID, ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForGet).uri(TRANSACTIONS_PATH);
        verify(bodySpec).header(AUTH, BEARER);
    }

    @Test
    void getBalances_successRequest() {
        List<UlsterbankBalance> expectedResponse = Instancio.ofList(UlsterbankBalance.class).create();
        UlsterbankData data = Instancio.of(UlsterbankData.class)
                .set(field(UlsterbankData::getBalances), expectedResponse)
                .create();
        UlsterbankGeneralResponse generalResponse = Instancio.of(UlsterbankGeneralResponse.class)
                .set(field(UlsterbankGeneralResponse::getData), data)
                .create();

        when(config.getAccountsUrl()).thenReturn(ACCOUNTS);

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(BALANCES_PATH)).thenReturn(bodySpec);
        when(bodySpec.header(AUTH, BEARER)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UlsterbankGeneralResponse.class)).thenReturn(just(generalResponse));

        List<UlsterbankBalance> result = ulsterbankDao.getBalances(ID, ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForGet).uri(BALANCES_PATH);
        verify(bodySpec).header(AUTH, BEARER);
    }

    private boolean bodyHasAllKeyValues(Object arg, Map<String, String> expected) {
        var formData = extractBody(arg);
        System.out.println("Actual formData: " + formData);
        System.out.println("Expected: " + expected);
        return expected.entrySet().stream()
                .allMatch(e -> e.getValue().equals(formData.get(e.getKey())));
    }

    private Map<String, String> extractBody(Object arg) {
        if (arg instanceof FormInserter<?> formInserter) {
            try {
                Field dataField = formInserter.getClass().getDeclaredField(DATA);
                dataField.setAccessible(true);
                Object data = dataField.get(formInserter);
                if (data instanceof MultiValueMap<?, ?> map) {
                    Map<String, String> result = new HashMap<>();
                    for (Object k : map.keySet()) {
                        if (k instanceof String) {
                            List<?> vList = map.get(k);
                            if (vList != null && !vList.isEmpty() && vList.get(0) instanceof String) {
                                result.put((String) k, (String) vList.get(0));
                            }
                        }
                    }
                    return result;
                }
            } catch (Exception e) {
                return Collections.emptyMap();
            }
        }
        return Collections.emptyMap();
    }
}
