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

import static com.avito.qainternship.util.TestDataFactory.generateUnknownItemId;
import static com.avito.qainternship.util.TestDataFactory.validAdRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Avito QA Internship API")
@Feature("Get statistics by item id")
@Tag("api")
@Tag("statistics")
public class GetStatisticsTests extends BaseApiTest {

    @Test
    @Story("TC-API-016")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-016: should return statistics for created item")
    @Description("Проверка получения статистики по существующему объявлению.")
    void shouldReturnStatisticsForCreatedItem() {
        AdRequest request = validAdRequest();
        AdResponse created = createItemAndExtract(request);

        Response response = apiClient.getStatisticsByItemId(created.getId());

        assertEquals(200, response.statusCode());
        assertTrue(response.contentType().contains("application/json"));

        List<Statistics> statisticsList = response.jsonPath().getList("", Statistics.class);
        assertNotNull(statisticsList);
        assertFalse(statisticsList.isEmpty());

        for (Statistics statistics : statisticsList) {
            assertNotNull(statistics.getLikes());
            assertNotNull(statistics.getViewCount());
            assertNotNull(statistics.getContacts());
        }
    }

    @Test
    @Story("TC-API-017")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-017: should return 404 for unknown statistics id")
    @Description("Проверка обработки запроса статистики по несуществующему item id.")
    void shouldReturn404ForUnknownItemStatistics() {
        Response response = apiClient.getStatisticsByItemId(generateUnknownItemId());

        assertEquals(404, response.statusCode());
    }
}
