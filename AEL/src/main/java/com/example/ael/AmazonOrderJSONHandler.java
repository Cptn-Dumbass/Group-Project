package com.example.ael;


import com.example.ael.DataModels.OrderDataModel;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONParserConfiguration;


import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class AmazonOrderJSONHandler {

    ObjectMapper objectMapper = new ObjectMapper();


    public AmazonOrderJSONHandler(){

        objectMapper.configure(DeserializationFeature
                .FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public OrderDataModel[] handleOrderForTest(AmazonOrder[] orderArray) {

        OrderDataModel[] orderDataModel
                = new OrderDataModel[orderArray.length];
        for(int i=0; i< orderArray.length; i++) {
            orderDataModel[i] =
                   new OrderDataModel(orderArray[i].getAmazonOrderId(),
                            orderArray[i].getOrderStatus(),
                            orderArray[i].getPurchaseDate(),
                            "Amazon",
                            "AEL",
                           "extRef",
                           "channelRef",
                           orderArray[i].getPaymentMethod(),
                           orderArray[i].getAmount(),
                           Date.from(Instant.now()).toString());

        }

        return orderDataModel;
    }

    public AmazonOrder[] getObjectArray(String jsonString) throws JsonProcessingException {

        AmazonOrder[] orders =
                objectMapper.readValue(jsonString, AmazonOrder[].class);


        return orders;
    }
    public AmazonOrderItem[] getOrderItemArrayForTest(String jsonString) throws JsonProcessingException{

        JsonNode orderItemNode = new ObjectMapper().readTree(jsonString);
        String orderItem = orderItemNode.get("OrderItems").toString();
        AmazonOrderItem[] items =
                objectMapper.readValue(orderItem, AmazonOrderItem[].class);
        return items;
    }

     protected AmazonOrderItem[] getOrderItemArray(String jsonString) throws JsonProcessingException{

        JsonNode orderItemNode = new ObjectMapper().readTree(jsonString);
        String orderItem = orderItemNode.get("OrderItems").toString();
        AmazonOrderItem[] items =
                objectMapper.readValue(orderItem, AmazonOrderItem[].class);
        return items;
    }

}
