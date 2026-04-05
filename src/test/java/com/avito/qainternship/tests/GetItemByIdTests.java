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

import static com.avito.qainternship.util.TestDataFactory.generateUnknownItemId;
import static com.avito.qainternship.util.TestDataFactory.validAdRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Avito QA Internship API")
@Feature("Get item by id")
@Tag("api")
@Tag("get-by-id")
public class GetItemByIdTests extends BaseApiTest {

    @Test
    @Story("TC-API-011")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-011: should return created item by id")
    @Description("Проверка получения ранее созданного объявления по идентификатору.")
    void shouldReturnCreatedItemById() {
        AdRequest request = validAdRequest();
        AdResponse created = createItemAndExtract(request);

        Response response = apiClient.getItemById(created.getId());

        assertEquals(200, response.statusCode());
        assertTrue(response.contentType().contains("application/json"));

        List<AdResponse> items = response.jsonPath().getList("", AdResponse.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());

        AdResponse actualItem = items.stream().
                filter(item -> created.getId().equals(item.getId())).
                findFirst().
                orElseThrow(() -> new AssertionError("Created item was not found in GET /item/{id} response"));

        assertEquals(request.getSellerId(), actualItem.getSellerId());
        assertEquals(request.getName(), actualItem.getName());
        assertEquals(request.getPrice(), actualItem.getPrice());
        assertNotNull(actualItem.getStatistics());
        assertEquals(request.getStatistics().getLikes(), actualItem.getStatistics().getLikes());
        assertEquals(request.getStatistics().getViewCount(), actualItem.getStatistics().getViewCount());
        assertEquals(request.getStatistics().getContacts(), actualItem.getStatistics().getContacts());
    }

    @Test
    @Story("TC-API-012")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-012: should return 404 for unknown item id")
    @Description("Проверка обработки запроса по несуществующему id.")
    void shouldReturn404ForUnknownItemId() {
        Response response = apiClient.getItemById(generateUnknownItemId());

        assertEquals(404, response.statusCode());
    }
}
