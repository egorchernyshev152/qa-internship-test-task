package com.avito.qainternship.tests;

import com.avito.qainternship.base.BaseApiTest;
import com.avito.qainternship.model.AdRequest;
import com.avito.qainternship.model.AdResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.avito.qainternship.util.TestDataFactory.generateSellerId;
import static com.avito.qainternship.util.TestDataFactory.validAdRequestWithSellerId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Avito QA Internship API")
@Feature("Get items by sellerID")
@Tag("api")
@Tag("seller")
public class GetItemsBySellerIdTests extends BaseApiTest {

    @Test
    @Story("TC-API-013")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-013: should return seller items")
    @Description("Проверка получения списка объявлений конкретного продавца.")
    void shouldReturnItemsForSeller() {
        int sellerId = generateSellerId();
        AdRequest request = validAdRequestWithSellerId(sellerId);
        AdResponse created = createItemAndExtract(request);

        Response response = apiClient.getItemsBySellerId(sellerId);

        assertEquals(200, response.statusCode());

        List<AdResponse> items = response.jsonPath().getList("", AdResponse.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());

        assertTrue(items.stream().allMatch(item -> sellerId == item.getSellerId()));
        assertTrue(items.stream().anyMatch(item -> created.getId().equals(item.getId())));
    }

    @Test
    @Story("TC-API-014")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-014: should return both created items for seller")
    @Description("Проверка получения нескольких объявлений одного продавца.")
    void shouldReturnBothCreatedItemsForSeller() {
        int sellerId = generateSellerId();
        AdResponse firstCreated = createItemAndExtract(validAdRequestWithSellerId(sellerId));
        AdResponse secondCreated = createItemAndExtract(validAdRequestWithSellerId(sellerId));

        Response response = apiClient.getItemsBySellerId(sellerId);

        assertEquals(200, response.statusCode());

        List<AdResponse> items = response.jsonPath().getList("", AdResponse.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());

        assertTrue(items.stream().anyMatch(item -> firstCreated.getId().equals(item.getId())));
        assertTrue(items.stream().anyMatch(item -> secondCreated.getId().equals(item.getId())));
        assertTrue(items.stream().allMatch(item -> sellerId == item.getSellerId()));
    }

    @Test
    @Story("TC-API-015")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-015: should return 400 for invalid sellerID format")
    @Description("Проверка обработки невалидного sellerID в path-параметре.")
    void shouldReturn400ForInvalidSellerIdFormat() {
        Response response = apiClient.getItemsBySellerId("invalid-seller-id");

        assertEquals(400, response.statusCode());
    }
}
