package com.bazario.orders.application.model;

/**
 * Application-layer read model for aggregated admin dashboard KPI metrics.
 *
 * Lives in the orders application layer because the OrderUseCase is the
 * natural owner of revenue and order-count aggregations.
 *
 * Architectural rule: this type must NEVER import anything from *.adapters.*
 * The AdminController is responsible for mapping this to AdminDtos.DashboardMetricsResponse.
 */
public record DashboardMetrics(
        double totalRevenue,
        double revenueChange,
        long   totalOrders,
        double ordersChange,
        long   totalCustomers,
        double customersChange,
        long   activeProducts,
        double productsChange
) {}
