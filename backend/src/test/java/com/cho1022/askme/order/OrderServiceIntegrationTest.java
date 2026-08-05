package com.cho1022.askme.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.cho1022.askme.TestcontainersConfiguration;
import com.cho1022.askme.common.exception.BadRequestException;
import com.cho1022.askme.menu.domain.Menu;
import com.cho1022.askme.menu.domain.MenuOption;
import com.cho1022.askme.menu.service.MenuService;
import com.cho1022.askme.order.domain.DrinkSize;
import com.cho1022.askme.order.domain.OrderChannel;
import com.cho1022.askme.order.domain.PaymentMethod;
import com.cho1022.askme.order.domain.ServiceMode;
import com.cho1022.askme.order.dto.CreateOrderItemRequest;
import com.cho1022.askme.order.dto.CreateOrderRequest;
import com.cho1022.askme.order.dto.CreateOrderResponse;
import com.cho1022.askme.order.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuService menuService;

    @Test
    void recalculatesPriceOnServerAndStoresOrder() {
        Menu americano = menuService.getActiveMenu(1L);
        Long shotOptionId = americano.getOptions().stream()
                .filter(option -> option.getCode().equals("EXTRA_SHOT"))
                .map(MenuOption::getId)
                .findFirst()
                .orElseThrow();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest(1L, 2, DrinkSize.LARGE, List.of(shotOptionId))),
                "아아 큰 걸로 두 잔, 샷 추가",
                OrderChannel.VOICE,
                ServiceMode.TAKEOUT,
                PaymentMethod.CARD
        );

        CreateOrderResponse response = orderService.createOrder(request);

        assertThat(response.orderId()).isNotNull();
        assertThat(response.orderNumber()).startsWith("K");
        assertThat(response.totalPrice()).isEqualTo(8_000);
    }

    @Test
    void createsAMultiMenuOrderWithServerCalculatedTotal() {
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(1L, 2, DrinkSize.REGULAR, List.of()),
                        new CreateOrderItemRequest(3L, 1, DrinkSize.LARGE, List.of())
                ),
                null,
                OrderChannel.TOUCH,
                ServiceMode.DINE_IN,
                PaymentMethod.CARD
        );

        CreateOrderResponse response = orderService.createOrder(request);

        assertThat(response.totalPrice()).isEqualTo(10_300);
    }

    @Test
    void rejectsAnOptionThatBelongsToAnotherMenu() {
        Menu latte = menuService.getActiveMenu(3L);
        Long latteOptionId = latte.getOptions().stream()
                .filter(option -> option.getCode().equals("EXTRA_SHOT"))
                .map(MenuOption::getId)
                .findFirst()
                .orElseThrow();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest(1L, 1, DrinkSize.REGULAR, List.of(latteOptionId))),
                null,
                OrderChannel.TOUCH,
                ServiceMode.DINE_IN,
                PaymentMethod.CARD
        );

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("선택할 수 없는 옵션");
    }
}
