package com.avito.qainternship.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiClient {
    private final RequestSpecification requestSpecification;

    public ApiClient(String baseUrl) {
        this.requestSpecification = new RequestSpecBuilder().
                setBaseUri(baseUrl).
                setAccept(ContentType.JSON).
                setContentType(ContentType.JSON).
                addFilter(new AllureRestAssured()).
                build();
    }

    public Response createItem(Object body) {
        return given().spec(requestSpecification).body(body).when().post("/api/1/item");
    }

    public Response getItemById(String id) {
        return given().spec(requestSpecification).when().get("/api/1/item/{id}", id);
    }

    public Response getItemsBySellerId(Object sellerId) {
        return given().spec(requestSpecification).when().get("/api/1/{sellerId}/item", sellerId);
    }

    public Response getStatisticsByItemId(String id) {
        return given().spec(requestSpecification).when().get("/api/1/statistic/{id}", id);
    }

    public Response deleteItemById(String id) {
        return given().spec(requestSpecification).when().delete("/api/2/item/{id}", id);
    }
}
