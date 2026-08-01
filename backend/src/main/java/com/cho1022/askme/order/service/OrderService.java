package com.cho1022.askme.order.service;

import com.cho1022.askme.common.exception.BadRequestException;
import com.cho1022.askme.menu.domain.Menu;
import com.cho1022.askme.menu.domain.MenuOption;
import com.cho1022.askme.menu.service.MenuService;
import com.cho1022.askme.order.domain.DrinkSize;
import com.cho1022.askme.order.domain.Order;
import com.cho1022.askme.order.domain.OrderItem;
import com.cho1022.askme.order.domain.OrderItemOption;
import com.cho1022.askme.order.dto.CreateOrderItemRequest;
import com.cho1022.askme.order.dto.CreateOrderRequest;
import com.cho1022.askme.order.dto.CreateOrderResponse;
import com.cho1022.askme.order.repository.OrderRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final int LARGE_SURCHARGE = 500;

    private final OrderRepository orderRepository;
    private final MenuService menuService;

    public OrderService(OrderRepository orderRepository, MenuService menuService) {
        this.orderRepository = orderRepository;
        this.menuService = menuService;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order(
                newOrderNumber(),
                blankToNull(request.originalTranscript()),
                request.orderChannel(),
                request.serviceMode(),
                request.paymentMethod()
        );

        for (CreateOrderItemRequest itemRequest : request.items()) {
            Menu menu = menuService.getActiveMenu(itemRequest.menuId());
            Map<Long, MenuOption> availableOptions = menu.getOptions().stream()
                    .filter(MenuOption::isActive)
                    .collect(Collectors.toMap(MenuOption::getId, Function.identity()));

            int unitPrice = menu.getBasePrice();
            if (itemRequest.size() == DrinkSize.LARGE) {
                unitPrice += LARGE_SURCHARGE;
            }
            var selectedOptions = itemRequest.safeOptionIds().stream().distinct().map(optionId -> {
                MenuOption option = availableOptions.get(optionId);
                if (option == null) {
                    throw new BadRequestException(menu.getName() + "에서 선택할 수 없는 옵션입니다: " + optionId);
                }
                return option;
            }).toList();
            unitPrice += selectedOptions.stream().mapToInt(MenuOption::getAdditionalPrice).sum();

            OrderItem orderItem = new OrderItem(
                    menu.getId(),
                    menu.getName(),
                    itemRequest.quantity(),
                    itemRequest.size(),
                    unitPrice
            );
            for (MenuOption option : selectedOptions) {
                orderItem.addOption(new OrderItemOption(
                        option.getId(),
                        option.getCode(),
                        option.getName(),
                        option.getAdditionalPrice()
                ));
            }
            order.addItem(orderItem);
        }

        return CreateOrderResponse.from(orderRepository.save(order));
    }

    private String newOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "K" + date + "-" + suffix;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
