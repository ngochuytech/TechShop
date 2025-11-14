package com.project.techstore.repositories;

import com.project.techstore.models.Order;
import com.project.techstore.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByStatus(String status, Pageable pageable);
    
    long countByStatus(String status);

    Page<Order> findByUserId(String userId, Pageable pageable);

    @EntityGraph(attributePaths = { "orderItems" })
    Optional<Order> findById(String id);

    long countByUserAndStatus(User user, String status);

    long countByUserAndStatusNot(User user, String status);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o where o.status = 'DELIVERED'")
    Long sumTotalRevenue();

    @Query(value = "SELECT * FROM Orders o ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Order> findRecentOrders(@Param("limit") int limit);

    // Tính tổng doanh thu trong khoảng thời gian
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = 'DELIVERED'")
    Long sumTotalAmountByDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Đếm số đơn hàng trong khoảng thời gian
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o " +
            "WHERE 1=1" +
            "AND (:status IS NULL OR o.status = :status)" +
            "AND (:customerName IS NULL OR o.user.fullName LIKE %:customerName% OR o.user.email LIKE %:customerName%) " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate)")
    Page<Order> searchOrders(@Param("status") String status, @Param("customerName") String customerName, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Lấy doanh thu theo tháng trong năm
     * Trả về: [tháng, tổng doanh thu]
     */
    @Query("SELECT MONTH(o.createdAt) as month, SUM(o.totalPrice) as revenue " +
            "FROM Order o " +
            "WHERE YEAR(o.createdAt) = :year AND o.status = 'DELIVERED' " +
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY MONTH(o.createdAt)")
    List<Object[]> getMonthlyRevenueByYear(@Param("year") Integer year);

    /**
     * Lấy doanh thu theo ngày trong tháng
     * Trả về: [ngày, tổng doanh thu]
     */
    @Query("SELECT DAY(o.createdAt) as day, SUM(o.totalPrice) as revenue " +
            "FROM Order o " +
            "WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month " +
            "AND o.status = 'DELIVERED' " +
            "GROUP BY DAY(o.createdAt) " +
            "ORDER BY DAY(o.createdAt)")
    List<Object[]> getDailyRevenueByMonth(
            @Param("year") Integer year,
            @Param("month") Integer month);

    /**
     * Lấy doanh thu theo tuần trong tháng
     */
    @Query("SELECT WEEK(o.createdAt) as week, SUM(o.totalPrice) as revenue " +
            "FROM Order o " +
            "WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month " +
            "AND o.status = 'DELIVERED' " +
            "GROUP BY WEEK(o.createdAt) " +
            "ORDER BY WEEK(o.createdAt)")
    List<Object[]> getWeeklyRevenueByMonth(
            @Param("year") Integer year,
            @Param("month") Integer month);

    /**
     * Lấy top sản phẩm bán chạy nhất
     * Trả về: [product_id, total_quantity_sold]
     */
    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalSold " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = 'DELIVERED' " +
            "GROUP BY oi.product.id " +
            "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();

    /**
     * Lấy top sản phẩm bán chạy với limit
     */
    @Query(value = "SELECT oi.product_id, SUM(oi.quantity) as total_sold " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status = 'DELIVERED' " +
            "GROUP BY oi.product_id " +
            "ORDER BY total_sold DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopSellingProductsWithLimit(@Param("limit") int limit);

    /**
     * Đếm số lượng đã bán của một sản phẩm
     */
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE oi.product.id = :productId AND o.status = 'DELIVERED'")
    Long countSoldByProductId(@Param("productId") String productId);

    //Lấy số đơn hàng theo status cho user trong một query
    @Query("SELECT o.status, COUNT(o.id) " +
            "FROM Order o " +
            "WHERE o.user.id = :userId " +
            "GROUP BY o.status")
    List<Object[]> getOrderCountByStatusForUser(@Param("userId") String userId);
}
