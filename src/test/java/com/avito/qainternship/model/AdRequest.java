package com.avito.qainternship.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdRequest {
    @JsonProperty("sellerID")
    private Integer sellerId;

    private String name;
    private Integer price;
    private Statistics statistics;

    public AdRequest() { }

    public AdRequest(Integer sellerId, String name, Integer price, Statistics statistics) {
        this.sellerId = sellerId;
        this.name = name;
        this.price = price;
        this.statistics = statistics;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
