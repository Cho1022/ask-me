package com.cho1022.askme.menu.repository;

import com.cho1022.askme.menu.domain.Menu;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @EntityGraph(attributePaths = {"aliases", "options"})
    List<Menu> findByActiveTrueOrderBySortOrderAscIdAsc();

    @EntityGraph(attributePaths = {"aliases", "options"})
    Optional<Menu> findByIdAndActiveTrue(Long id);
}
