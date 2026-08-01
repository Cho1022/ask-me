package com.cho1022.askme.menu.controller;

import com.cho1022.askme.menu.dto.MenuResponse;
import com.cho1022.askme.menu.service.MenuService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuResponse> getMenus() {
        return menuService.getActiveMenus();
    }
}
