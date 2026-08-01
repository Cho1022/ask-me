package com.cho1022.askme.menu.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "base_price", nullable = false)
    private int basePrice;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Temperature temperature;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<MenuAlias> aliases = new LinkedHashSet<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<MenuOption> options = new LinkedHashSet<>();

    protected Menu() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public boolean isActive() {
        return active;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public Set<MenuAlias> getAliases() {
        return Set.copyOf(aliases);
    }

    public Set<MenuOption> getOptions() {
        return Set.copyOf(options);
    }
}
