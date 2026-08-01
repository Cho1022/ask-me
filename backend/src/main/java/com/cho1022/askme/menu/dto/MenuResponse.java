package com.cho1022.askme.menu.dto;

import com.cho1022.askme.menu.domain.Menu;
import com.cho1022.askme.menu.domain.MenuOption;
import com.cho1022.askme.menu.domain.Temperature;
import java.util.List;

public record MenuResponse(
        Long id,
        String name,
        String groupName,
        String category,
        String description,
        int basePrice,
        String imageUrl,
        Temperature temperature,
        List<MenuOptionResponse> options
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getGroupName(),
                menu.getCategory(),
                menu.getDescription(),
                menu.getBasePrice(),
                menu.getImageUrl(),
                menu.getTemperature(),
                menu.getOptions().stream()
                        .filter(MenuOption::isActive)
                        .map(MenuOptionResponse::from)
                        .toList()
        );
    }
}
