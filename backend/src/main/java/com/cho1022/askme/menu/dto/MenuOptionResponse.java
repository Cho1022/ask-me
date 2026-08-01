package com.cho1022.askme.menu.dto;

import com.cho1022.askme.menu.domain.MenuOption;
import com.cho1022.askme.menu.domain.MenuOptionType;

public record MenuOptionResponse(
        Long id,
        String code,
        String name,
        MenuOptionType type,
        int additionalPrice
) {
    public static MenuOptionResponse from(MenuOption option) {
        return new MenuOptionResponse(
                option.getId(),
                option.getCode(),
                option.getName(),
                option.getType(),
                option.getAdditionalPrice()
        );
    }
}
