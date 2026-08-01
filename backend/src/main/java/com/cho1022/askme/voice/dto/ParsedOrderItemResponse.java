package com.cho1022.askme.voice.dto;

import com.cho1022.askme.order.domain.DrinkSize;
import com.cho1022.askme.voice.domain.VoiceOrderAction;
import java.util.List;

public record ParsedOrderItemResponse(
        Long menuId,
        String menuName,
        int quantity,
        DrinkSize size,
        VoiceOrderAction action,
        List<ParsedOptionResponse> options
) {
}
