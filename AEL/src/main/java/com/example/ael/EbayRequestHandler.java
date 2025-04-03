package com.example.ael;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.ebay.api.client.auth.oauth2.model.RefreshToken;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class EbayRequestHandler {

    // Scope : https://api.ebay.com/oauth/api_scope/sell.fulfillment

    HttpClient client =
            HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

    public EbayRequestHandler(){

    }
    public AmazonOrder[] sendRequest(String accessToken, String lastRequest) throws URISyntaxException, IOException, InterruptedException {

        URI uri = new URI("https://api.sandbox.ebay.com/sell/fulfillment/v1/order?filter=creationdate:%5B"
                + lastRequest + "%5D");

        HttpRequest request =
            HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .build();

        HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());

        EbayJsonHandler handler = new EbayJsonHandler();

       return handler.handleEbayResponse(response.body());

    }

    public URI getAuthUri() throws FileNotFoundException, URISyntaxException {
        OAuth2Api oauth2Api = new OAuth2Api();
        CredentialUtil.load(new FileInputStream(
                "src/main/resources/com/example/ael/ebay/ebay-config-sample.yaml"));

        URI authUri = new URI(oauth2Api.generateUserAuthorizationUrl(Environment.SANDBOX,
                Collections.singletonList("https://api.ebay.com/oauth/api_scope/sell.fulfillment.readonly"),
                Optional.of("")));

        return authUri;
    }

    public OAuthResponse getAccessAndRefreshTokens(String urlWithCode) throws IOException {

        OAuth2Api oauth2Api = new OAuth2Api();
        CredentialUtil.load(new FileInputStream(
                "src/main/resources/com/example/ael/ebay/ebay-config-sample.yaml"));


        URL codeURL = new URL(urlWithCode);
        String[] queries = codeURL.getQuery().split("&");

        String[] codeQuery = queries[1].split("=");

        OAuthResponse token = oauth2Api.exchangeCodeForAccessToken(Environment.SANDBOX, codeQuery[1]);

        return token;
    }

    public OAuthResponse getAccessTokenFromRefresh(RefreshToken token) throws IOException {

        OAuth2Api oAuth2Api = new OAuth2Api();

        CredentialUtil.load(new FileInputStream(
                "src/main/resources/com/example/ael/ebay/ebay-config-sample.yaml"));
        return oAuth2Api.getAccessToken(Environment.SANDBOX,
                    token.getToken(),
                    Collections.singletonList("https://api.ebay.com/oauth/api_scope/sell.fulfillment"));
    }

    public void createOauthProperties(OAuthResponse response, String accountName){
        Properties tokens = new Properties();

        tokens.put("timeLimit", response.getRefreshToken().get().getExpiresOn().toString());
        if(Objects.equals(response.getRefreshToken().get().getToken(), null)){
            tokens.put("Foo", "Bar");
        }else {
            tokens.put("RefreshToken", response.getRefreshToken().get().getToken());
        }
        try(FileWriter output = new FileWriter("src/refreshTokens/Ebay/" +
                accountName +
                ".properties")){
            tokens.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

