package com.cho1022.askme.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "original_transcript", length = 2000)
    private String originalTranscript;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_channel", nullable = false, length = 20)
    private OrderChannel orderChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_mode", nullable = false, length = 20)
    private ServiceMode serviceMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at", nullable = false)
    private LocalDateTime confirmedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(
            String orderNumber,
            String originalTranscript,
            OrderChannel orderChannel,
            ServiceMode serviceMode,
            PaymentMethod paymentMethod
    ) {
        this.orderNumber = orderNumber;
        this.originalTranscript = originalTranscript;
        this.orderChannel = orderChannel;
        this.serviceMode = serviceMode;
        this.paymentMethod = paymentMethod;
        this.status = OrderStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
        this.confirmedAt = createdAt;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.attachTo(this);
        totalPrice += item.getTotalPrice();
    }

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
