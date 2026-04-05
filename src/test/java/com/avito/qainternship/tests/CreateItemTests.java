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
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.avito.qainternship.util.TestDataFactory.validAdRequest;
import static com.avito.qainternship.util.TestDataFactory.validAdRequestAsMap;
import static com.avito.qainternship.util.TestDataFactory.validAdRequestWithSellerId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Avito QA Internship API")
@Feature("Create item")
@Tag("api")
@Tag("create")
public class CreateItemTests extends BaseApiTest {

    @Test
    @Story("TC-API-001")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-001: should create item with valid data")
    @Description("Проверка успешного создания объявления с валидным телом запроса.")
    void shouldCreateItemWithValidData() {
        AdRequest request = validAdRequest();

        Response response = apiClient.createItem(request);

        assertEquals(200, response.statusCode());
        assertTrue(response.contentType().contains("application/json"));

        AdResponse created = extractCreatedItem(response);

        assertNotNull(created.getId());
        assertEquals(request.getSellerId(), created.getSellerId());
        assertEquals(request.getName(), created.getName());
        assertEquals(request.getPrice(), created.getPrice());
        assertNotNull(created.getStatistics());
        assertEquals(request.getStatistics().getLikes(), created.getStatistics().getLikes());
        assertEquals(request.getStatistics().getViewCount(), created.getStatistics().getViewCount());
        assertEquals(request.getStatistics().getContacts(), created.getStatistics().getContacts());
        assertNotNull(created.getCreatedAt());
        assertFalse(created.getCreatedAt().isBlank());
    }

    @Test
    @Story("TC-API-002")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("TC-API-002: should create two different items from same payload")
    @Description("Повторный POST с одинаковым payload должен создавать отдельную запись с новым id.")
    void shouldCreateTwoDifferentItemsFromSamePayload() {
        AdRequest request = validAdRequest();

        Response firstResponse = apiClient.createItem(request);
        Response secondResponse = apiClient.createItem(request);

        assertEquals(200, firstResponse.statusCode());
        assertEquals(200, secondResponse.statusCode());

        AdResponse firstCreated = extractCreatedItem(firstResponse);
        AdResponse secondCreated = extractCreatedItem(secondResponse);

        assertNotNull(firstCreated.getId());
        assertNotNull(secondCreated.getId());
        assertNotEquals(firstCreated.getId(), secondCreated.getId());
    }

    @Test
    @Story("TC-API-003")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("TC-API-003: should create item with min sellerID")
    void shouldCreateItemWithMinSellerId() {
        AdRequest request = validAdRequestWithSellerId(111111);

        Response response = apiClient.createItem(request);

        assertEquals(200, response.statusCode());

        AdResponse created = extractCreatedItem(response);

        assertEquals(111111, created.getSellerId());
    }

    @Test
    @Story("TC-API-004")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("TC-API-004: should create item with max sellerID")
    void shouldCreateItemWithMaxSellerId() {
        AdRequest request = validAdRequestWithSellerId(999999);

        Response response = apiClient.createItem(request);

        assertEquals(200, response.statusCode());

        AdResponse created = extractCreatedItem(response);

        assertEquals(999999, created.getSellerId());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("missingRequiredFieldCases")
    @Story("TC-API-005/006/007/008")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Required field validation")
    @Description("Проверка, что обязательные поля не могут быть пропущены.")
    void shouldRejectCreateWithoutRequiredField(String caseName, Consumer<Map<String, Object>> modifier) {
        Map<String, Object> body = validAdRequestAsMap();
        modifier.accept(body);

        Response response = apiClient.createItem(body);

        assertEquals(400, response.statusCode(), caseName);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTypeCases")
    @Story("TC-API-009/010")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Invalid type validation")
    @Description("Проверка, что API отклоняет некорректные типы данных в обязательных полях.")
    void shouldRejectCreateWithInvalidFieldType(String caseName, Map<String, Object> body) {
        Response response = apiClient.createItem(body);

        assertEquals(400, response.statusCode(), caseName);
    }

    private static Stream<Arguments> missingRequiredFieldCases() {
        return Stream.of(
                Arguments.of(
                        "Missing sellerID should return 400",
                        (Consumer<Map<String, Object>>) body -> body.remove("sellerID")),
                Arguments.of(
                        "Missing name should return 400",
                        (Consumer<Map<String, Object>>) body -> body.remove("name")),
                Arguments.of(
                        "Missing price should return 400",
                        (Consumer<Map<String, Object>>) body -> body.remove("price")),
                Arguments.of(
                        "Missing statistics should return 400",
                        (Consumer<Map<String, Object>>) body -> body.remove("statistics")));
    }

    private static Stream<Arguments> invalidTypeCases() {
        Map<String, Object> invalidSellerIdBody = validAdRequestAsMap();
        invalidSellerIdBody.put("sellerID", "not-an-integer");

        Map<String, Object> invalidPriceBody = validAdRequestAsMap();
        invalidPriceBody.put("price", "not-an-integer");

        return Stream.of(
                Arguments.of("sellerID with invalid type should return 400", invalidSellerIdBody),
                Arguments.of("price with invalid type should return 400", invalidPriceBody));
    }
}
