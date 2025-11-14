package com.project.techstore.services.statistic;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.techstore.models.Role;
import com.project.techstore.repositories.OrderRepository;
import com.project.techstore.repositories.ProductModelRepository;
import com.project.techstore.repositories.ProductRepository;
import com.project.techstore.repositories.ProductVariantRepository;
import com.project.techstore.repositories.ReviewRepository;
import com.project.techstore.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticService implements IStatisticService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductModelRepository productModelRepository;

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    private final ReviewRepository reviewRepository;

    /**
     * Tính phần trăm tăng trưởng so với tháng trước
     * Formula: ((currentMonth - lastMonth) / lastMonth) * 100
     */
    private Map<String, Double> calculateMonthlyGrowth() {
        Map<String, Double> growth = new HashMap<>();

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfCurrentMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime startOfLastMonth = startOfCurrentMonth.minusMonths(1);

            // Khi có Order entity, tính như sau:

            // 1. Tăng trưởng doanh thu
            Long currentMonthRevenue = orderRepository.sumTotalAmountByDateBetween(
                    startOfCurrentMonth, now);
            Long lastMonthRevenue = orderRepository.sumTotalAmountByDateBetween(
                    startOfLastMonth, startOfCurrentMonth);
            double revenueGrowth = calculateGrowthRate(currentMonthRevenue,
                    lastMonthRevenue);
            growth.put("revenue", revenueGrowth);

            // 2. Tăng trưởng đơn hàng
            long currentMonthOrders = orderRepository.countByCreatedAtBetween(
                    startOfCurrentMonth, now);
            long lastMonthOrders = orderRepository.countByCreatedAtBetween(
                    startOfLastMonth, startOfCurrentMonth);
            double ordersGrowth = calculateGrowthRate(currentMonthOrders,
                    lastMonthOrders);
            growth.put("orders", ordersGrowth);

            // 3. Tăng trưởng khách hàng mới
            long currentMonthCustomers = userRepository.countByRole_NameAndCreatedAtBetween(
                    "CUSTOMER", startOfCurrentMonth, now);
            long lastMonthCustomers = userRepository.countByRole_NameAndCreatedAtBetween(
                    "CUSTOMER", startOfLastMonth, startOfCurrentMonth);
            double customersGrowth = calculateGrowthRate(currentMonthCustomers,
                    lastMonthCustomers);
            growth.put("customers", customersGrowth);

        } catch (Exception e) {
            // Nếu có lỗi, trả về 0% growth
            growth.put("revenue", 0.0);
            growth.put("orders", 0.0);
            growth.put("customers", 0.0);
        }

        return growth;
    }

    /**
     * Helper method để tính phần trăm tăng trưởng
     * 
     * @param currentValue  Giá trị hiện tại
     * @param previousValue Giá trị trước đó
     * @return Phần trăm tăng trưởng (làm tròn 1 chữ số thập phân)
     */
    private double calculateGrowthRate(Number currentValue, Number previousValue) {
        if (previousValue == null || previousValue.doubleValue() == 0) {
            return currentValue != null && currentValue.doubleValue() > 0 ? 100.0 : 0.0;
        }

        double current = currentValue != null ? currentValue.doubleValue() : 0.0;
        double previous = previousValue.doubleValue();

        double growthRate = ((current - previous) / previous) * 100;

        // Làm tròn đến 1 chữ số thập phân
        return Math.round(growthRate * 10.0) / 10.0;
    }

    @Override
    public Map<String, Object> getOverviewStatistics() throws Exception {
        Map<String, Object> statistics = new HashMap<>();

        try {
            long totalOrders = orderRepository.count();
            statistics.put("totalOrders", totalOrders);

            Long totalRevenue = orderRepository.sumTotalRevenue();
            statistics.put("totalRevenue", totalRevenue != null ? totalRevenue : 0L);

            long totalUsers = userRepository.countByRole_Name(Role.ADMIN);
            statistics.put("totalCustomers", totalUsers);

            long totalProductModels = productModelRepository.countByIsDeletedFalse();
            statistics.put("totalProductModels", totalProductModels);

            long totalProducts = productRepository.countByIsDeletedFalse();
            statistics.put("totalProducts", totalProducts);

            long totalReviews = reviewRepository.count();
            statistics.put("totalReviews", totalReviews);

            long totalVariants = productVariantRepository.countByIsDeletedFalse();
            statistics.put("totalVariants", totalVariants);

            long lowStockProducts = productRepository.countByStockLessThan(10);
            statistics.put("lowStockProducts", lowStockProducts);

            Double averageRating = reviewRepository.findAverageRating();
            statistics.put("averageRating", averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);

            Map<String, Double> monthlyGrowth = calculateMonthlyGrowth();
            statistics.put("monthlyGrowth", monthlyGrowth);
        } catch (Exception e) {
            throw new RuntimeException("Error getting overview statistics: " + e.getMessage());
        }

        return statistics;
    }

    /**
     * Lấy doanh thu theo 12 tháng trong năm
     */
    private Map<String, Object> getYearlyRevenue(Integer year) {
        Map<String, Object> result = new HashMap<>();

        // Lấy dữ liệu từ database
        List<Object[]> revenueData = orderRepository.getMonthlyRevenueByYear(year);

        // Initialize arrays với 12 tháng
        List<String> labels = Arrays.asList(
                "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
                "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8",
                "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12");

        // Initialize values với 0 cho tất cả tháng
        Long[] values = new Long[12];
        Arrays.fill(values, 0L);

        // Fill dữ liệu thực vào đúng tháng
        for (Object[] row : revenueData) {
            Integer monthNum = (Integer) row[0]; // Tháng (1-12)
            Long revenue = ((Number) row[1]).longValue(); // Doanh thu
            values[monthNum - 1] = revenue; // Index từ 0-11
        }

        result.put("labels", labels);
        result.put("values", Arrays.asList(values));
        result.put("period", "year");
        result.put("year", year);

        return result;
    }

    /**
     * Lấy doanh thu theo từng ngày trong tháng
     */
    private Map<String, Object> getMonthlyRevenue(Integer year, Integer month) {
        Map<String, Object> result = new HashMap<>();

        // Lấy dữ liệu từ database
        List<Object[]> revenueData = orderRepository.getDailyRevenueByMonth(year, month);

        // Tính số ngày trong tháng
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Initialize labels và values
        List<String> labels = new ArrayList<>();
        Long[] values = new Long[daysInMonth];
        Arrays.fill(values, 0L);

        // Tạo labels cho tất cả ngày
        for (int i = 1; i <= daysInMonth; i++) {
            labels.add("Ngày " + i);
        }

        // Fill dữ liệu thực
        for (Object[] row : revenueData) {
            Integer dayNum = (Integer) row[0]; // Ngày (1-31)
            Long revenue = ((Number) row[1]).longValue(); // Doanh thu
            values[dayNum - 1] = revenue;
        }

        result.put("labels", labels);
        result.put("values", Arrays.asList(values));
        result.put("period", "month");
        result.put("year", year);
        result.put("month", month);

        return result;
    }

    /**
     * Lấy doanh thu theo tuần trong tháng
     */
    private Map<String, Object> getWeeklyRevenue(Integer year, Integer month) {
        Map<String, Object> result = new HashMap<>();

        // Lấy dữ liệu từ database
        List<Object[]> revenueData = orderRepository.getWeeklyRevenueByMonth(year, month);

        // Group theo tuần (thường 4-5 tuần trong tháng)
        Map<Integer, Long> weeklyMap = new HashMap<>();
        for (Object[] row : revenueData) {
            Integer weekNum = (Integer) row[0];
            Long revenue = ((Number) row[1]).longValue();
            weeklyMap.put(weekNum, revenue);
        }

        // Tạo labels và values
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        weeklyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    labels.add("Tuần " + entry.getKey());
                    values.add(entry.getValue());
                });

        result.put("labels", labels);
        result.put("values", values);
        result.put("period", "week");
        result.put("year", year);
        result.put("month", month);

        return result;
    }

    @Override
    public Map<String, Object> getRevenueStatistics(String period, Integer month, Integer year) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Đảm bảo year có giá trị mặc định
            if (year == null) {
                year = LocalDateTime.now().getYear();
            }

            if ("year".equals(period)) {
                // Lấy doanh thu theo 12 tháng trong năm
                result = getYearlyRevenue(year);

            } else if ("month".equals(period)) {
                // Lấy doanh thu theo từng ngày trong tháng
                if (month == null) {
                    month = LocalDateTime.now().getMonthValue();
                }
                result = getMonthlyRevenue(year, month);

            } else if ("week".equals(period)) {
                // Lấy doanh thu theo tuần trong tháng
                if (month == null) {
                    month = LocalDateTime.now().getMonthValue();
                }
                result = getWeeklyRevenue(year, month);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error getting revenue statistics: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getProductsByCategory() {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // Sử dụng custom query (hiệu suất tốt hơn)
            List<Object[]> categoryData = productRepository.countProductsByCategory();

            for (Object[] row : categoryData) {
                String categoryName = (String) row[0];
                Long count = ((Number) row[1]).longValue();

                Map<String, Object> item = new HashMap<>();
                item.put("category", categoryName);
                item.put("count", count);
                result.add(item);
            }

            // Nếu không có dữ liệu, trả về empty list
            if (result.isEmpty()) {
                // Optional: Return mock data or empty list
                return result;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error getting products by category: " + e.getMessage());
        }

        return result;
    }

    /**
     * Fallback method: Lấy products theo stock cao nhất (khi chưa có đơn hàng)
     */
    private List<Map<String, Object>> getTopProductsByStock(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();

        var products = productRepository.findAll();

        products.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()))
                .limit(limit)
                .forEach(product -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", product.getId());
                    item.put("name", product.getName());
                    item.put("configuration_summary", product.getConfigurationSummary());
                    item.put("price", product.getPrice());
                    item.put("stock", product.getStock());
                    item.put("soldCount", 0L);

                    // Tính average rating
                    Double avgRating = reviewRepository.findAverageRatingByProductId(product.getId());
                    item.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);

                    String productImageUrl = product.getMediaList().stream()
                            .filter(media -> media.getIsPrimary() != null && media.getIsPrimary())
                            .findFirst()
                            .map(media -> media.getMediaPath())
                            .orElse(null);
                    item.put("productImage", productImageUrl);

                    result.add(item);
                });

        return result;
    }

    @Override
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // Lấy top products bán chạy từ OrderRepository
            List<Object[]> topSellingData = orderRepository.findTopSellingProductsWithLimit(limit);

            if (topSellingData.isEmpty()) {
                // Nếu chưa có đơn hàng nào, lấy products theo stock cao nhất
                return getTopProductsByStock(limit);
            }

            // Lặp qua từng product và lấy thông tin chi tiết
            for (Object[] row : topSellingData) {
                String productId = (String) row[0];
                Long soldCount = ((Number) row[1]).longValue();

                // Lấy thông tin product
                var productOpt = productRepository.findById(productId);
                if (productOpt.isPresent()) {
                    var product = productOpt.get();

                    Map<String, Object> item = new HashMap<>();
                    item.put("id", product.getId());
                    item.put("name", product.getName());
                    item.put("configuration_summary", product.getConfigurationSummary());
                    item.put("price", product.getPrice());
                    item.put("stock", product.getStock());
                    item.put("soldCount", soldCount);

                    // Tính average rating
                    Double avgRating = reviewRepository.findAverageRatingByProductId(product.getId());
                    item.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);

                    String productImageUrl = product.getMediaList().stream()
                            .filter(media -> media.getIsPrimary() != null && media.getIsPrimary())
                            .findFirst()
                            .map(media -> media.getMediaPath())
                            .orElse(null);
                    item.put("productImage", productImageUrl);

                    result.add(item);
                }
            }

        } catch (Exception e) {
            // Fallback to stock-based ranking if error
            return getTopProductsByStock(limit);
        }

        return result;
    }

}
