package com.avito.qainternship.tests;

import com.avito.qainternship.base.BaseApiTest;
import com.avito.qainternship.model.AdRequest;
import com.avito.qainternship.model.AdResponse;
import com.avito.qainternship.model.Statistics;
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
@Feature("E2E flows")
@Tag("api")
@Tag("e2e")
public class EndToEndApiTests extends BaseApiTest {

    @Test
    @Story("TC-API-018")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-018: E2E create -> get by id")
    @Description("Создать объявление, затем получить его по id и проверить консистентность данных.")
    void shouldCreateAndFetchTheSameItem() {
        AdRequest request = validAdRequestWithSellerId(generateSellerId());
        AdResponse created = createItemAndExtract(request);

        Response getResponse = apiClient.getItemById(created.getId());
        assertEquals(200, getResponse.statusCode());

        List<AdResponse> items = getResponse.jsonPath().getList("", AdResponse.class);
        AdResponse actual = items.stream().
                filter(item -> created.getId().equals(item.getId())).
                findFirst().
                orElseThrow(() -> new AssertionError("Created item was not found in item-by-id response"));

        assertEquals(request.getSellerId(), actual.getSellerId());
        assertEquals(request.getName(), actual.getName());
        assertEquals(request.getPrice(), actual.getPrice());
        assertEquals(request.getStatistics().getLikes(), actual.getStatistics().getLikes());
        assertEquals(request.getStatistics().getViewCount(), actual.getStatistics().getViewCount());
        assertEquals(request.getStatistics().getContacts(), actual.getStatistics().getContacts());
    }

    @Test
    @Story("TC-API-019")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-019: E2E create x2 -> get by sellerID")
    @Description("Создать два объявления с одним sellerID и убедиться, что оба доступны в seller collection.")
    void shouldCreateTwoItemsAndFindThemInSellerCollection() {
        int sellerId = generateSellerId();
        AdResponse firstCreated = createItemAndExtract(validAdRequestWithSellerId(sellerId));
        AdResponse secondCreated = createItemAndExtract(validAdRequestWithSellerId(sellerId));

        Response sellerResponse = apiClient.getItemsBySellerId(sellerId);
        assertEquals(200, sellerResponse.statusCode());

        List<AdResponse> items = sellerResponse.jsonPath().getList("", AdResponse.class);
        assertTrue(items.stream().anyMatch(item -> firstCreated.getId().equals(item.getId())));
        assertTrue(items.stream().anyMatch(item -> secondCreated.getId().equals(item.getId())));
    }

    @Test
    @Story("TC-API-020")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-020: E2E create -> get statistics")
    @Description("Создать объявление, затем получить статистику по его id.")
    void shouldCreateItemAndFetchStatistics() {
        AdResponse created = createItemAndExtract(validAdRequestWithSellerId(generateSellerId()));

        Response statisticsResponse = apiClient.getStatisticsByItemId(created.getId());
        assertEquals(200, statisticsResponse.statusCode());

        List<Statistics> statisticsList = statisticsResponse.jsonPath().getList("", Statistics.class);
        assertNotNull(statisticsList);
        assertFalse(statisticsList.isEmpty());
    }
}
