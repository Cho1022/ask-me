package com.cho1022.askme.order.dto;

import com.cho1022.askme.order.domain.DrinkSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderItemRequest(
        @NotNull Long menuId,
        @Min(1) @Max(99) int quantity,
        @NotNull DrinkSize size,
        List<Long> optionIds
) {
    public List<Long> safeOptionIds() {
        return optionIds == null ? List.of() : optionIds;
    }
}
