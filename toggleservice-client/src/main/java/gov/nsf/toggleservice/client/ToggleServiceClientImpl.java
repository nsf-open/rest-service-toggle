package gov.nsf.toggleservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.mynsf.common.restclient.NsfRestTemplate;
import gov.nsf.toggleservice.api.model.Toggles;
import gov.nsf.toggleservice.api.service.ToggleService;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jacklinden on 3/21/17.
 */
public class ToggleServiceClientImpl implements ToggleService {

    private static final Logger LOGGER = Logger.getLogger(ToggleServiceClientImpl.class);


    private String toggleServiceURL;
    private String toggleServiceUserName;
    private String toggleServicePassword;
    private boolean authenticationRequired;
    private int requestTimeout;

    @Override
    public Toggles getFeatureToggles() {

        String requestUrl = toggleServiceURL + "/toggles/";
        String responseBody = sendRequest(requestUrl, HttpMethod.GET, null);

        return extractToggles(responseBody);
    }

    @Override
    public Boolean toggleIsActive(String toggleName) {
        Toggles toggles = getFeatureToggles();
        if( toggles == null || !toggles.getFeatures().containsKey(toggleName) ){
            return null;
        }

        return toggles.getFeatures().get(toggleName);
    }

    private Toggles extractToggles(String responseBody){
        Toggles toggles = null;
        try {
            JsonNode node = new ObjectMapper().readTree(responseBody).findValue("toggles");
            toggles = new ObjectMapper().readValue(node.toString(), Toggles.class);
        } catch (IOException e) {
            LOGGER.error("Error extracting Toggles from JSON response.",e);
        }

        return toggles;
    }

    private HttpEntity<String> createHttpEntityWithAuthAndBody(String userName, String password, String body) {
        String auth = userName + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = getBaseHeaders();
        headers.set("Authorization", authHeader);
        return new HttpEntity<String>(body, headers);
    }

    private HttpHeaders getBaseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }

    private String sendRequest(String URL, HttpMethod httpMethod, String jsonBody ) {

        RestTemplate emailServiceClient = null;

        try {
            emailServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired, requestTimeout);
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            LOGGER.error("Error initializing NSFRestTemplate",e);
            throw new RuntimeException("Error initializing NSFRestTemplate",e);
        }

        HttpEntity<String> httpEntity = null;
        if (authenticationRequired) {
            httpEntity = createHttpEntityWithAuthAndBody(toggleServiceUserName, toggleServicePassword, jsonBody);
        } else {
            httpEntity = new HttpEntity<String>(jsonBody, getBaseHeaders());
        }

        ResponseEntity<String> response = null;
        String responseBody = null;

        try {
            response = emailServiceClient.exchange(URL, httpMethod, httpEntity, String.class);
            responseBody = response.getBody();
        } catch (HttpClientErrorException ex) {
            responseBody = ex.getResponseBodyAsString();
        } catch (HttpServerErrorException ex){
            responseBody = ex.getResponseBodyAsString();
        }

        return responseBody;
    }

    public String getToggleServiceURL() {
        return toggleServiceURL;
    }

    public void setToggleServiceURL(String toggleServiceURL) {
        this.toggleServiceURL = toggleServiceURL;
    }

    public String getToggleServiceUserName() {
        return toggleServiceUserName;
    }

    public void setToggleServiceUserName(String toggleServiceUserName) {
        this.toggleServiceUserName = toggleServiceUserName;
    }

    public String getToggleServicePassword() {
        return toggleServicePassword;
    }

    public void setToggleServicePassword(String toggleServicePassword) {
        this.toggleServicePassword = toggleServicePassword;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
