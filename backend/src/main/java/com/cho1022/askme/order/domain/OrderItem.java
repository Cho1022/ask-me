package com.cho1022.askme.order.domain;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DrinkSize size;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemOption> options = new ArrayList<>();

    protected OrderItem() {
    }

    public OrderItem(Long menuId, String menuName, int quantity, DrinkSize size, int unitPrice) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.quantity = quantity;
        this.size = size;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice * quantity;
    }

    void attachTo(Order order) {
        this.order = order;
    }

    public void addOption(OrderItemOption option) {
        options.add(option);
        option.attachTo(this);
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
