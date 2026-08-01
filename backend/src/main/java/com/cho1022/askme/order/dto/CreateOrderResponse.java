package com.cho1022.askme.order.dto;

import com.cho1022.askme.order.domain.Order;
import java.time.LocalDateTime;

public record CreateOrderResponse(
        Long orderId,
        String orderNumber,
        int totalPrice,
        LocalDateTime createdAt,
        String message
) {
    public static CreateOrderResponse from(Order order) {
        return new CreateOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                "주문이 확정되었습니다."
        );
    }
}
