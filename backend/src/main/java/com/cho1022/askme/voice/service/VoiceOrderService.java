package com.cho1022.askme.voice.service;

import com.cho1022.askme.menu.service.MenuService;
import com.cho1022.askme.voice.dto.ParseVoiceOrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VoiceOrderService {

    private final MenuService menuService;
    private final OrderParser orderParser;

    public VoiceOrderService(MenuService menuService, OrderParser orderParser) {
        this.menuService = menuService;
        this.orderParser = orderParser;
    }

    public ParseVoiceOrderResponse parse(String transcript) {
        return orderParser.parse(transcript, menuService.getActiveMenuEntities());
    }
}
