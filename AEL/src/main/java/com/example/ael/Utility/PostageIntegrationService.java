package com.example.ael.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PostageIntegrationService {
    private static final String SHIPPO_TEST_TOKEN = "shippo_test_c72b3c47237a3f0b88be78d491c7fccbfd8b86c3";
    private static final String ROYAL_MAIL_CARRIER_ACCOUNT = "carrier_account_id";
    private static final String ROYAL_MAIL_SERVICE_LEVEL_TOKEN = "service_level_id";

    public String createParcel(int length, int width, int height, int weight) {
        if (length <= 0 || width <= 0 || height <= 0 || weight <= 0) {
            return "Invalid parcel dimensions. Length, width, height, and weight must be positive values.";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("https://api.goshippo.com/parcels/");
            request.addHeader("Authorization", "ShippoToken " + SHIPPO_TEST_TOKEN);
            request.addHeader("Content-Type", "application/json");

            String data = String.format("{\"length\":%d,\"width\":%d,\"height\":%d,\"distance_unit\":\"cm\",\"weight\":%d,\"mass_unit\":\"kg\",\"template\":\"\",\"metadata\":\"\"}",
                    length, width, height, weight);

            request.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getEntity() != null) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    return extractTrackingNumber(responseBody);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while making API call: " + e.getMessage();
        }
        return null;
    }

    private String extractTrackingNumber(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody);
        if (jsonResponse.has("object_id")) {
            return jsonResponse.getString("object_id");
        } else {
            return "Tracking number not found in response.";
        }
    }


    public String createShippingLabel(String senderName, String senderAddress, String senderCity, String senderState, String senderZip) {
        if (senderName.isEmpty() || senderAddress.isEmpty() || senderCity.isEmpty() || senderState.isEmpty() || senderZip.isEmpty()) {
            return "Sender information is incomplete. Please provide all required fields.";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("https://api.goshippo.com/transactions");
            request.addHeader("Authorization", "ShippoToken " + SHIPPO_TEST_TOKEN);
            request.addHeader("Content-Type", "application/json");

            String data = String.format("{\"shipment\":{\"address_from\":{\"name\":\"%s\",\"street1\":\"%s\",\"city\":\"%s\",\"state\":\"%s\",\"zip\":\"%s\",\"country\":\"US\"},\"address_to\":{\"name\":\"Recipient Name\",\"street1\":\"Recipient Address\",\"city\":\"Recipient City\",\"state\":\"Recipient State\",\"zip\":\"Recipient Zip\",\"country\":\"US\"},\"parcels\":[{\"length\":\"5\",\"width\":\"5\",\"height\":\"5\",\"distance_unit\":\"in\",\"weight\":\"2\",\"mass_unit\":\"lb\"}]},\"carrier_account\":\"%s\",\"servicelevel_token\":\"%s\"}",
                    senderName, senderAddress, senderCity, senderState, senderZip, ROYAL_MAIL_CARRIER_ACCOUNT, ROYAL_MAIL_SERVICE_LEVEL_TOKEN);

            request.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return handleApiResponse(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while making API call: " + e.getMessage();
        }
    }

    private String handleApiResponse(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String responseBody = EntityUtils.toString(entity);
            JSONObject jsonResponse = new JSONObject(responseBody);
            if (jsonResponse.has("object_id")) {
                String objectId = jsonResponse.getString("object_id");
                return objectId;
            } else {
                // Error occurred, download error response to PDF
                downloadToPdf(responseBody, "error_response.pdf");
                return "Error occurred in API call. Error response downloaded to error_response.pdf";
            }
        } else {
            return "Empty response received from API call";
        }
    }

    private void downloadToPdf(String content, String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String downloadLabelPDF(String labelUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(labelUrl);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = entity.getContent();
                    String pdfFilePath = "label.pdf";
                    FileOutputStream outputStream = new FileOutputStream(pdfFilePath);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    return pdfFilePath;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while downloading label PDF: " + e.getMessage();
        }
        return null;
    }
}
