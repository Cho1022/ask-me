package com.cho1022.askme.menu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_options")
public class MenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MenuOptionType type;

    @Column(name = "additional_price", nullable = false)
    private int additionalPrice;

    @Column(nullable = false)
    private boolean active;

    protected MenuOption() {
    }

    public Long getId() {
        return id;
    }

    public Long getMenuId() {
        return menu.getId();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public MenuOptionType getType() {
        return type;
    }

    public int getAdditionalPrice() {
        return additionalPrice;
    }

    public boolean isActive() {
        return active;
    }
}
