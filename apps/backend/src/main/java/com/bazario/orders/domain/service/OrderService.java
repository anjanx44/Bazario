package com.bazario.orders.domain.service;

import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.ports.in.ProductUseCase;
import com.bazario.customers.domain.model.CustomerAddress;
import com.bazario.customers.ports.in.CustomerUseCase;
import com.bazario.inventory.ports.in.InventoryUseCase;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderItem;
import com.bazario.orders.domain.model.OrderStatus;
import com.bazario.orders.ports.in.OrderUseCase;
import com.bazario.orders.ports.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final CustomerUseCase customerUseCase;
    private final ProductUseCase productUseCase;
    private final InventoryUseCase inventoryUseCase;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        log.info("Placing new order for customer: {}", order.getCustomerId());

        // 1. Validate Customer existence
        customerUseCase.getCustomerById(order.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + order.getCustomerId()));

        // 2. Fetch Product Prices and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            Product product = productUseCase.getProductById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));
            
            item.setProductName(product.getName());
            item.setPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getBasePrice());
            item.setCreatedAt(ZonedDateTime.now());
            
            totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            // 3. Deduct Stock from Inventory
            inventoryUseCase.updateStock(item.getProductId(), -item.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // 4. Snapshot the customer's default shipping address
        String addressSnapshot = customerUseCase.getAddressesByCustomerId(order.getCustomerId()).stream()
                .filter(CustomerAddress::isDefault)
                .findFirst()
                .map(addr -> String.format("%s, %s, %s, %s, %s", 
                        addr.getAddressLine1(), addr.getAddressLine2(), addr.getCity(), addr.getPostalCode(), addr.getCountry()))
                .orElseThrow(() -> new IllegalStateException("Customer has no default shipping address"));
        
        order.setShippingAddressSnapshot(addressSnapshot);
        order.setCreatedAt(ZonedDateTime.now());
        order.setUpdatedAt(ZonedDateTime.now());

        // 5. Save Order
        return orderRepositoryPort.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(UUID id) {
        return orderRepositoryPort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(UUID customerId) {
        return orderRepositoryPort.findByCustomerId(customerId);
    }
}
