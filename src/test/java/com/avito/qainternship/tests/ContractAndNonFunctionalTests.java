package com.avito.qainternship.tests;

import com.avito.qainternship.base.BaseApiTest;
import com.avito.qainternship.model.AdResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.avito.qainternship.util.TestDataFactory.generateSellerId;
import static com.avito.qainternship.util.TestDataFactory.validAdRequestWithSellerId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Avito QA Internship API")
@Feature("Contract and non-functional checks")
@Tag("api")
@Tag("non-functional")
public class ContractAndNonFunctionalTests extends BaseApiTest {

    @Test
    @Story("TC-API-021")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("TC-API-021: should respond to create within five seconds")
    @Description("Базовая нефункциональная проверка времени ответа ручки создания объявления.")
    void shouldRespondToCreateWithinFiveSeconds() {
        Response response = apiClient.createItem(validAdRequestWithSellerId(generateSellerId()));

        assertEquals(200, response.statusCode());
        assertTrue(response.time() < 5_000, "Create response time should be below 5000 ms");
        extractCreatedItem(response);
    }

    @Test
    @Story("TC-API-022")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("TC-API-022: should return application/json for successful responses")
    @Description("Контрактная проверка Content-Type для успешных ответов основных ручек.")
    void shouldReturnJsonContentTypeForSuccessfulResponses() {
        AdResponse created = createItemAndExtract(validAdRequestWithSellerId(generateSellerId()));

        Response itemResponse = apiClient.getItemById(created.getId());
        Response sellerResponse = apiClient.getItemsBySellerId(created.getSellerId());
        Response statisticsResponse = apiClient.getStatisticsByItemId(created.getId());

        assertTrue(itemResponse.contentType().contains("application/json"));
        assertTrue(sellerResponse.contentType().contains("application/json"));
        assertTrue(statisticsResponse.contentType().contains("application/json"));
    }
}
