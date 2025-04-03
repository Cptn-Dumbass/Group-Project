package com.example.ael;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EbayJsonHandler {

    //TODO give these database calls

    ObjectMapper objectMapper = new ObjectMapper();
    public EbayJsonHandler(){

        objectMapper.configure(DeserializationFeature
                .FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public AmazonOrder[] handleEbayResponse(String response) throws IOException {

        JsonNode responseNode = new ObjectMapper().readTree(response);

        ArrayNode orderArray = (ArrayNode) responseNode.get("orders");

        AmazonOrder[] orders = new AmazonOrder[orderArray.size()];

        for (int i = 0; i < orders.length; i++) {

            orders[i] = new AmazonOrder();

            orders[i].setAmazonOrderId(
                    orderArray
                            .get(i)
                            .get("orderId").asText());

            orders[i].setOrderStatus(
                    orderArray
                            .get(i)
                            .get("orderFulfillmentStatus").asText());

            orders[i].setPurchaseDate(
                    orderArray
                            .get(i)
                            .get("creationDate").asText());

            orders[i].setPaymentMethod(
                    orderArray
                            .get(i)
                            .get("paymentSummary")
                            .get("payments")
                            .get(0)
                            .get("paymentMethod").asText());

            orders[i].setAmount(
                    orderArray
                            .get(i)
                            .get("paymentSummary")
                            .get("totalDueSeller")
                            .get("value").asText());

            orders[i].setSalesChannel("Ebay");

            BuyerInfo buyerInfo = new BuyerInfo(orderArray
                    .get(i)
                    .get("buyer")
                    .get("buyerRegistrationAddress")
                    .get("email").asText(),

                    orderArray
                            .get(i)
                            .get("buyer")
                            .get("buyerRegistrationAddress")
                            .get("fullName").asText());

            orders[i].setBuyerInfo(buyerInfo);

            JsonNode itemSubArray = orderArray.get(i).get("lineItems");

            String itemString = itemSubArray.toString();

            AmazonOrderItem[] item = objectMapper.readValue(itemString, AmazonOrderItem[].class);

            orders[i].setItem(item);
        }

        return orders;
    }

    public AmazonOrderItem[] handleOrderItems(String response) throws JsonProcessingException {

        JsonNode itemNode = new ObjectMapper().readTree(response);

        ArrayNode itemArray = (ArrayNode) itemNode.get("orders");

        List<AmazonOrderItem> items = new ArrayList<>();

        for (int i = 0; i < itemArray.size(); i++) {
            ArrayNode itemSubArray = (ArrayNode) itemNode.get("lineItems");

            for (int j = 0; j < itemSubArray.size(); j++) {
                AmazonOrderItem item = new AmazonOrderItem();

                item.setAmazonOrderId(itemArray
                        .get(i)
                        .get("orderId").asText());

                item.setSku(itemSubArray
                        .get(j)
                        .get("sku").asText());

                item.setQuantity(itemSubArray
                        .get(j)
                        .get("quantity").asText());

                items.add(item);
            }
        }
        return items.toArray(new AmazonOrderItem[0]);
    }
    public BuyerInfo[] getBuyerInfo(String json) throws JsonProcessingException {
        JsonNode itemNode = new ObjectMapper().readTree(json);

        ArrayNode orderArray = (ArrayNode) itemNode.get("orders");

        BuyerInfo[] buyerInfos = new BuyerInfo[orderArray.size()];

        for (int i=0; i< buyerInfos.length; i++){
            buyerInfos[i] = new BuyerInfo(orderArray
                    .get(i)
                    .get("buyer")
                    .get("buyerRegistrationAddress")
                    .get("email").asText(),

                    orderArray
                            .get(i)
                            .get("buyer")
                            .get("buyerRegistrationAddress")
                            .get("fullName").asText());
        }
        return buyerInfos;
    }

}
