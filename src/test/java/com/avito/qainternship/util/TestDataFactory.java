package com.avito.qainternship.util;

import com.avito.qainternship.model.AdRequest;
import com.avito.qainternship.model.Statistics;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataFactory {
    private static final int SELLER_ID_MIN = 111111;
    private static final int SELLER_ID_MAX = 999999;

    private TestDataFactory() { }

    public static int generateSellerId() {
        return ThreadLocalRandom.current().nextInt(SELLER_ID_MIN, SELLER_ID_MAX + 1);
    }

    public static String generateItemName() {
        return "qa-item-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    public static int generatePrice() {
        return ThreadLocalRandom.current().nextInt(1, 100_000);
    }

    public static Statistics generateStatistics() {
        return new Statistics(
                ThreadLocalRandom.current().nextInt(0, 1_000),
                ThreadLocalRandom.current().nextInt(0, 10_000),
                ThreadLocalRandom.current().nextInt(0, 1_000));
    }

    public static AdRequest validAdRequest() {
        return new AdRequest(generateSellerId(), generateItemName(), generatePrice(), generateStatistics());
    }

    public static AdRequest validAdRequestWithSellerId(int sellerId) {
        return new AdRequest(sellerId, generateItemName(), generatePrice(), generateStatistics());
    }

    public static Map<String, Object> validAdRequestAsMap() {
        AdRequest request = validAdRequest();
        return toMap(request);
    }

    public static Map<String, Object> toMap(AdRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("sellerID", request.getSellerId());
        body.put("name", request.getName());
        body.put("price", request.getPrice());

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("likes", request.getStatistics().getLikes());
        statistics.put("viewCount", request.getStatistics().getViewCount());
        statistics.put("contacts", request.getStatistics().getContacts());

        body.put("statistics", statistics);
        return body;
    }

    public static String generateUnknownItemId() {
        return UUID.randomUUID().toString();
    }
}
