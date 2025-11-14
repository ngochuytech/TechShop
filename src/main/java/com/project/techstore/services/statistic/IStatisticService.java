package com.project.techstore.services.statistic;

import java.util.List;
import java.util.Map;

public interface IStatisticService {
    Map<String, Object> getOverviewStatistics() throws Exception;
    Map<String, Object> getRevenueStatistics(String period, Integer month, Integer year);
    List<Map<String, Object>> getProductsByCategory();
    List<Map<String, Object>> getTopSellingProducts(int limit);
}
