package com.example.ael;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.methods.HttpPost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class AmazonRequest {

    //https://sandbox.sellingpartnerapi-na.amazon.com/orders/v0/orders?CreatedAfter=TEST_CASE_200&MarketplaceIds=ATVPDKIKX0DER

    private String baseUrl = "https://sandbox.sellingpartnerapi-na.amazon.com";

    private String APIName;

    private String marketplaceId;

    private int status;

    private HttpClient client;

    public AmazonRequest(String APIName, String marketplaceId){
        this.APIName = APIName;
        this.marketplaceId = marketplaceId;

        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public AmazonRequest() {

    }

    public AmazonOrder[] sendRequest(String createdAfter) throws IOException, URISyntaxException, InterruptedException {

        String urlString = baseUrl + APIName +
                "?CreatedAfter=" + createdAfter +
                "&MarketplaceIds=" + marketplaceId;

        URI uri = new URI(urlString);
        HttpRequest request = HttpRequest.newBuilder()
                .header("key", "x-amz-access-token")
                .header("value", "2YgYW55IGNhcm5hbCBwbGVhc3VyZS4")
                .header("type", "text")
                .header("description", "access_token value")
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());

        status = response.statusCode();

        AmazonOrderJSONHandler jsonHandler = new AmazonOrderJSONHandler();

       if(status == 200){
           AmazonOrder[] orders = jsonHandler.getObjectArray(response.body());

           for (int i=0; i<orders.length; i++){
               String itemURL = baseUrl + "/orders/v0/orders/" +
                       orders[i].getAmazonOrderId() + "/orderItems" +
                       "?CreatedAfter=" + createdAfter +
                       "&MarketplaceIds=" + marketplaceId;
               URI itemURI = new URI(itemURL);
               HttpRequest itemRequest = HttpRequest.newBuilder()
                       .GET()
                       .uri(itemURI)
                       .header("key", "x-amz-access-token")
                       .header("value", "2YgYW55IGNhcm5hbCBwbGVhc3VyZS4")
                       .header("type", "text")
                       .header("description", "access_token value")
                       .build();
               HttpResponse<String> itemResponse =
                       client.send(itemRequest, HttpResponse.BodyHandlers.ofString());
               AmazonOrderItem[] orderItems =
                       jsonHandler.getOrderItemArray(itemResponse.body());
               orders[i].setItem(orderItems);
               //This requests the orderItems, and adds them to the order object
           }
           return orders;
       }else {
           return null;
       }
    }
    public int getStatus(){
        return status;
    }

    public void getAmazonAccessToken(String refreshToken, String clientId, String clientSecret)
            throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI("https://api.amazon.com/auth/o2/token");
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=refresh_token" +
                        "&refresh_token=" + refreshToken +
                        "&client_id=" + clientId +
                        "&client_secret=" + clientSecret))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        //TODO This couldn't be tested due to a lack of permissions, but AEL should have a dev account now
    }
}


