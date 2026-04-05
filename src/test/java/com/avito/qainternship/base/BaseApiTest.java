package com.avito.qainternship.base;

import com.avito.qainternship.client.ApiClient;
import com.avito.qainternship.model.AdRequest;
import com.avito.qainternship.model.AdResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseApiTest {
    protected ApiClient apiClient;
    private final List<String> createdItemIds = new ArrayList<>();
    private static final Pattern CREATED_ITEM_ID_PATTERN =
            Pattern.compile("([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$");

    @BeforeEach
    void setUpApiClient() {
        String baseUrl = System.getProperty("baseUrl", "https://qa-internship.avito.com");
        apiClient = new ApiClient(baseUrl);
    }

    @AfterEach
    void cleanCreatedItems() {
        for (String itemId : createdItemIds) {
            try {
                apiClient.deleteItemById(itemId);
            } catch (Exception ignored) {
                // Cleanup is best-effort because DELETE is not part of the mandatory task scope.
            }
        }
        createdItemIds.clear();
    }

    @Step("Создать объявление и извлечь id")
    protected AdResponse createItemAndExtract(AdRequest request) {
        Response response = apiClient.createItem(request);
        assertEquals(200, response.statusCode(), "Create item should return 200 OK");
        assertTrue(response.contentType().contains("application/json"), "Create item should return JSON");
        return extractCreatedItem(response);
    }

    @Step("Зарегистрировать id для cleanup: {itemId}")
    protected void registerCreatedItem(String itemId) {
        createdItemIds.add(itemId);
    }

    @Step("Извлечь созданное объявление из ответа")
    protected AdResponse extractCreatedItem(Response response) {
        AdResponse responseBody = response.as(AdResponse.class);
        String itemId = resolveCreatedItemId(responseBody);
        assertNotNull(itemId, "Created item id must not be null");
        registerCreatedItem(itemId);
        return fetchItemById(itemId);
    }

    private String resolveCreatedItemId(AdResponse responseBody) {
        if (responseBody == null) {
            return null;
        }
        if (responseBody.getId() != null && !responseBody.getId().isBlank()) {
            return responseBody.getId();
        }
        String status = responseBody.getStatus();
        if (status == null || status.isBlank()) {
            return null;
        }
        Matcher matcher = CREATED_ITEM_ID_PATTERN.matcher(status.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private AdResponse fetchItemById(String itemId) {
        Response getResponse = apiClient.getItemById(itemId);
        assertEquals(200, getResponse.statusCode(), "Created item should be available via GET /item/{id}");

        List<AdResponse> items;
        try {
            items = getResponse.jsonPath().getList("", AdResponse.class);
        } catch (Exception ignored) {
            items = null;
        }

        if (items != null && !items.isEmpty()) {
            return items.stream().
                    filter(item -> itemId.equals(item.getId())).
                    findFirst().
                    orElseThrow(() -> new AssertionError("Created item was not found in GET /item/{id} response"));
        }

        AdResponse singleItem = getResponse.as(AdResponse.class);
        if (singleItem != null && itemId.equals(singleItem.getId())) {
            return singleItem;
        }

        throw new AssertionError("GET /item/{id} did not return created item details");
    }
}
