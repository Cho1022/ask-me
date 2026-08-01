package com.cho1022.askme.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_item_options")
public class OrderItemOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(name = "option_code", nullable = false, length = 50)
    private String optionCode;

    @Column(name = "option_name", nullable = false, length = 100)
    private String optionName;

    @Column(name = "additional_price", nullable = false)
    private int additionalPrice;

    protected OrderItemOption() {
    }

    public OrderItemOption(Long optionId, String optionCode, String optionName, int additionalPrice) {
        this.optionId = optionId;
        this.optionCode = optionCode;
        this.optionName = optionName;
        this.additionalPrice = additionalPrice;
    }

    void attachTo(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
