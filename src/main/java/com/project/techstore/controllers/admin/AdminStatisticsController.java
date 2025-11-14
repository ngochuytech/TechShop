package com.project.techstore.controllers.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.statistic.IStatisticService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {
    private final IStatisticService statisticService;

    @GetMapping("/overview")
    public ResponseEntity<?> getOverviewStatistics() throws Exception {
        return ResponseEntity.ok(ApiResponse.ok(statisticService.getOverviewStatistics()));
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueStatistics(
            @RequestParam(defaultValue = "year") String period,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "2024") Integer year) {
        return ResponseEntity.ok(ApiResponse.ok(statisticService.getRevenueStatistics(period, month, year)));
    }


    @GetMapping("/products/by-category")
    public ResponseEntity<?> getProductsByCategory() {
        return ResponseEntity.ok(ApiResponse.ok(statisticService.getProductsByCategory()));
    }

    @GetMapping("/products/top-selling")
    public ResponseEntity<?> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(statisticService.getTopSellingProducts(limit)));
    }
}
