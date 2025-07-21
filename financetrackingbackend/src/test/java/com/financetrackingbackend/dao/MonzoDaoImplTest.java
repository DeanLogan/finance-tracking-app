package com.financetrackingbackend.dao;

import com.example.model.MonzoAccessToken;
import com.example.model.MonzoAccount;
import com.example.model.MonzoAccounts;
import com.example.model.MonzoPots;
import com.example.model.MonzoTransactionsResponse;
import com.example.model.WhoAmI;
import com.financetrackingbackend.configuration.MonzoConfig;
import com.financetrackingbackend.dao.impl.MonzoDaoImpl;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;

import reactor.core.publisher.Mono;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.just;

@ExtendWith(MockitoExtension.class)
public class MonzoDaoImplTest {
    @Mock
    private WebClient webClient;
    @Mock
    private MonzoConfig config;
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient.RequestBodyUriSpec uriSpecForPost;
    @Mock
    private WebClient.RequestHeadersUriSpec uriSpecForGet;
    @Mock
    private WebClient.RequestBodySpec bodySpec;
    @Mock
    private WebClient.RequestHeadersSpec headersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private MonzoDaoImpl monzoDao;

    private static final String AUTH_CODE = "authorization_code";
    private static final String ID = "123";
    private static final String EMPTY_STRING = "";
    private static final String REDIRECT_URL = "localhost/monzo/oauth/callback";
    private static final String AUTH_TOKEN_PATH =  "/oauth2/token";
    private static final String GRANT_TYPE =  "grant_type";
    private static final String CLIENT_ID =  "client_id";
    private static final String CLIENT_SECRET =  "client_secret";
    private static final String REDIRECT_URI =  "redirect_uri";
    private static final String REFRESH_TOKEN =  "refresh_token";
    private static final String CODE =  "code";
    private static final String UNAUTHORIZED_REQUEST =  "Unauthorized Request";
    private static final String INCORRECT_TOKEN =  "Incorrect token: Unauthorized Request";
    private static final String SERVICE_UNAVAILABLE = "Service unavailable";
    private static final String MONZO_REQUEST_FAIL = "Monzo request failed: Service unavailable";
    private static final String REQUIRED_INFO_BLANK_ERROR_MSG = "Required information for request is blank";
    private static final String WHO_AM_I_PATH = "/ping/whoami?=";
    private static final String ACCOUNTS_PATH = "/accounts?=";
    private static final String POTS_PATH = "/pots?current_account_id=123";
    private static final String BALANCE_PATH = "/balance?account_id=123";
    private static final String TRANSACTIONS_PATH = "/transactions?account_id=123";

    @BeforeEach
    void setup() {
        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        monzoDao = new MonzoDaoImpl(webClientBuilder, config);
    }

    @Test
    void exchangeAuthCode_success(){
        MonzoAccessToken expectedResponse = Instancio.of(MonzoAccessToken.class).create();

        MultiValueMap<String, String> expectedFormData = new LinkedMultiValueMap<>();
        expectedFormData.add(GRANT_TYPE, AUTH_CODE);
        expectedFormData.add(CLIENT_ID, ID);
        expectedFormData.add(CLIENT_SECRET, ID);
        expectedFormData.add(REDIRECT_URI, REDIRECT_URL);
        expectedFormData.add(CODE, ID);

        when(config.getRedirectUrl()).thenReturn(REDIRECT_URL);
        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(AUTH_TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.contentType(any())).thenReturn(bodySpec); 
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MonzoAccessToken.class)).thenReturn(just(expectedResponse));

        MonzoAccessToken result = monzoDao.exchangeAuthCode(ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForPost).uri(AUTH_TOKEN_PATH);
        verify(bodySpec).bodyValue(expectedFormData);
    }

    @Test
    void exchangeAuthCode_unauthorizedRequest(){
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                UNAUTHORIZED_REQUEST,
                401,
                UNAUTHORIZED_REQUEST,
                null, null, null, null
        );

        when(config.getRedirectUrl()).thenReturn(REDIRECT_URL);
        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(AUTH_TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenThrow(webClientResponseException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> monzoDao.exchangeAuthCode(ID));
        assertEquals(INCORRECT_TOKEN, e.getMessage());
    }

    @Test
    void exchangeAuthCode_handlesWebClientResponseException(){
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                SERVICE_UNAVAILABLE,
                503,
                SERVICE_UNAVAILABLE,
                null, null, null, null
        );

        when(config.getRedirectUrl()).thenReturn(REDIRECT_URL);
        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(AUTH_TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenThrow(webClientResponseException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> monzoDao.exchangeAuthCode(ID));
        assertEquals(MONZO_REQUEST_FAIL, e.getMessage());
    }

    @Test
    void exchangeAuthCode_handlesWebClientException(){
        WebClientException webClientException = new WebClientException(SERVICE_UNAVAILABLE) {};
    
        when(config.getRedirectUrl()).thenReturn(REDIRECT_URL);
        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);
    
        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(AUTH_TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenThrow(webClientException);
    
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> monzoDao.exchangeAuthCode(ID));
        assertEquals(MONZO_REQUEST_FAIL, e.getMessage());
    }

    @Test
    void exchangeAuthCode_correctErrForBlankAuthCode(){
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> monzoDao.exchangeAuthCode(EMPTY_STRING));
        assertEquals(REQUIRED_INFO_BLANK_ERROR_MSG, e.getMessage());
    }

    @Test
    void refreshAccessToken_success(){
        MonzoAccessToken expectedResponse = Instancio.of(MonzoAccessToken.class).create();

        MultiValueMap<String, String> expectedFormData = new LinkedMultiValueMap<>();
        expectedFormData.add(GRANT_TYPE, REFRESH_TOKEN);
        expectedFormData.add(CLIENT_ID, ID);
        expectedFormData.add(CLIENT_SECRET, ID);
        expectedFormData.add(REDIRECT_URI, EMPTY_STRING);
        expectedFormData.add(REFRESH_TOKEN, ID);

        when(config.getClientId()).thenReturn(ID);
        when(config.getClientSecret()).thenReturn(ID);

        when(webClient.post()).thenReturn(uriSpecForPost);
        when(uriSpecForPost.uri(AUTH_TOKEN_PATH)).thenReturn(bodySpec);
        when(bodySpec.contentType(any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MonzoAccessToken.class)).thenReturn(just(expectedResponse));

        MonzoAccessToken result = monzoDao.refreshAccessToken(ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForPost).uri(AUTH_TOKEN_PATH);
        verify(bodySpec).bodyValue(expectedFormData);
    }

    @Test
    void refreshAccessToken_correctErrForBlankRefreshCode(){
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> monzoDao.refreshAccessToken(EMPTY_STRING));
        assertEquals(REQUIRED_INFO_BLANK_ERROR_MSG, e.getMessage());
    }

    @Test
    void getWhoAmI_success() {
        WhoAmI expectedResponse = Instancio.of(WhoAmI.class).create();
    
        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(WHO_AM_I_PATH)).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers);
            return bodySpec;
        });
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WhoAmI.class)).thenReturn(just(expectedResponse));
    
        WhoAmI result = monzoDao.getWhoAmI(ID);
    
        assertEquals(expectedResponse, result);
        verify(uriSpecForGet).uri(WHO_AM_I_PATH);
    }

    @Test
    void getWhoAmI_handlesWebClientResponseException() {
        WebClientResponseException webClientResponseException = new WebClientResponseException(
                SERVICE_UNAVAILABLE,
                503,
                SERVICE_UNAVAILABLE,
                null, null, null, null
        );

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(WHO_AM_I_PATH)).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers);
            return bodySpec;
        });
        when(bodySpec.retrieve()).thenThrow(webClientResponseException);

        ServiceUnavailableException e = assertThrows(ServiceUnavailableException.class, () -> monzoDao.getWhoAmI(ID));
        assertEquals(MONZO_REQUEST_FAIL, e.getMessage());
    }

    @Test
    void getAccounts_success() {
        MonzoAccounts monzoAccounts = Instancio.of(MonzoAccounts.class).create();
        List<MonzoAccount> expectedResponse = monzoAccounts.getAccounts();

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(ACCOUNTS_PATH)).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers);
            return bodySpec;
        });
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MonzoAccounts.class)).thenReturn(just(monzoAccounts));

        List<MonzoAccount> result = monzoDao.getAccounts(ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForGet).uri(ACCOUNTS_PATH);
    }

    @Test
    void getAccounts_handlesNullAccounts() {
        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(ACCOUNTS_PATH)).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers);
            return bodySpec;
        });
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        
        Mono<MonzoAccounts> mockedMono = mock(Mono.class);
        when(responseSpec.bodyToMono(MonzoAccounts.class)).thenReturn(mockedMono);
        when(mockedMono.block()).thenReturn(null);
        List<MonzoAccount> result = monzoDao.getAccounts(ID);

        assertEquals(Collections.emptyList(), result);
        verify(uriSpecForGet).uri(ACCOUNTS_PATH);
        verify(bodySpec).retrieve();
    }

    @Test
    void getAllPots_success() {
        MonzoPots expectedResponse = Instancio.of(MonzoPots.class).create();

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(POTS_PATH)).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers);
            return bodySpec;
        });
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MonzoPots.class)).thenReturn(just(expectedResponse));

        MonzoPots result = monzoDao.getAllPots(ID, ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForGet).uri(POTS_PATH);
    }

    @Test
    void getAccount_success() {
        MonzoAccount expectedResponse = Instancio.of(MonzoAccount.class).create();

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(BALANCE_PATH)).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers);
            return bodySpec;
        });
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MonzoAccount.class)).thenReturn(just(expectedResponse));

        MonzoAccount result = monzoDao.getAccount(ID, ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForGet).uri(BALANCE_PATH);
    }

    @Test
    void getTransactions_success() {
        MonzoTransactionsResponse expectedResponse = Instancio.of(MonzoTransactionsResponse.class).create();

        when(webClient.get()).thenReturn(uriSpecForGet);
        when(uriSpecForGet.uri(TRANSACTIONS_PATH)).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers);
            return bodySpec;
        });
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MonzoTransactionsResponse.class)).thenReturn(just(expectedResponse));

        MonzoTransactionsResponse result = monzoDao.getTransactions(ID, ID);

        assertEquals(expectedResponse, result);
        verify(uriSpecForGet).uri(TRANSACTIONS_PATH);
    }
}
