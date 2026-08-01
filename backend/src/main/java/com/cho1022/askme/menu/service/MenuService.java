package com.cho1022.askme.menu.service;

import com.cho1022.askme.common.exception.ResourceNotFoundException;
import com.cho1022.askme.menu.domain.Menu;
import com.cho1022.askme.menu.dto.MenuResponse;
import com.cho1022.askme.menu.repository.MenuRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuResponse> getActiveMenus() {
        return getActiveMenuEntities().stream().map(MenuResponse::from).toList();
    }

    public List<Menu> getActiveMenuEntities() {
        return menuRepository.findByActiveTrueOrderBySortOrderAscIdAsc();
    }

    public Menu getActiveMenu(Long menuId) {
        return menuRepository.findByIdAndActiveTrue(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("판매 중인 메뉴를 찾을 수 없습니다: " + menuId));
    }
}
