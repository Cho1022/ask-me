package com.cho1022.askme.order.dto;

import com.cho1022.askme.order.domain.OrderChannel;
import com.cho1022.askme.order.domain.PaymentMethod;
import com.cho1022.askme.order.domain.ServiceMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateOrderRequest(
        @NotEmpty @Size(max = 50) List<@Valid CreateOrderItemRequest> items,
        @Size(max = 2000) String originalTranscript,
        @NotNull OrderChannel orderChannel,
        @NotNull ServiceMode serviceMode,
        @NotNull PaymentMethod paymentMethod
) {
}
