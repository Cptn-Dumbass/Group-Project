package com.example.ael;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.jar.Attributes;

public class ShippingAddress {

    @JsonProperty("Name")
    private String Name;

    @JsonProperty("AddressLine1")
    private String AddressLine1;

    @JsonProperty("City")
    private String City;

    @JsonProperty("StateOrRegion")
    private String StateOrRegion;

    @JsonProperty("PostalCode")
    private String PostalCode;

    @JsonProperty("CountryCode")
    private String CountryCode;

    ShippingAddress(){
        super();
    }

    String getShippingAddress(){
        return Name + "\n" +
                AddressLine1 + "\n" +
                City + "\n" +
                StateOrRegion + "\n" +
                PostalCode + "\n" +
                CountryCode;
    }
}
