package com.cho1022.askme.voice;

import static org.assertj.core.api.Assertions.assertThat;

import com.cho1022.askme.TestcontainersConfiguration;
import com.cho1022.askme.order.domain.DrinkSize;
import com.cho1022.askme.voice.domain.ParseStatus;
import com.cho1022.askme.voice.domain.VoiceOrderAction;
import com.cho1022.askme.voice.dto.ParseVoiceOrderResponse;
import com.cho1022.askme.voice.service.VoiceOrderService;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class OrderParserIntegrationTest {

    @Autowired
    private VoiceOrderService voiceOrderService;

    @ParameterizedTest
    @MethodSource("voiceOrderCases")
    void parsesVoiceOrderCases(
            String transcript,
            long expectedMenuId,
            int expectedQuantity,
            DrinkSize expectedSize,
            VoiceOrderAction expectedAction,
            String expectedOptionCode
    ) {
        ParseVoiceOrderResponse result = voiceOrderService.parse(transcript);

        assertThat(result.status())
                .as(result.toString())
                .isEqualTo(ParseStatus.CONFIRMATION_REQUIRED);
        assertThat(result.items()).isNotEmpty();
        assertThat(result.items().getFirst().menuId()).isEqualTo(expectedMenuId);
        assertThat(result.items().getFirst().quantity()).isEqualTo(expectedQuantity);
        assertThat(result.items().getFirst().size()).isEqualTo(expectedSize);
        assertThat(result.items().getFirst().action()).isEqualTo(expectedAction);
        if (expectedOptionCode == null) {
            assertThat(result.items().getFirst().options()).isEmpty();
        } else {
            assertThat(result.items().getFirst().options())
                    .extracting(option -> option.code())
                    .containsExactly(expectedOptionCode);
        }
    }

    private static Stream<Arguments> voiceOrderCases() {
        return Stream.of(
                Arguments.of("아이스 아메리카노 두 잔 주세요", 1L, 2, DrinkSize.REGULAR, VoiceOrderAction.ADD, null),
                Arguments.of("아아 두 잔하고 카페라떼 한 잔 주세요", 1L, 2, DrinkSize.REGULAR, VoiceOrderAction.ADD, null),
                Arguments.of("라떼 큰 걸로 한 잔하고 샷 추가", 3L, 1, DrinkSize.LARGE, VoiceOrderAction.ADD, "EXTRA_SHOT"),
                Arguments.of("아메리카노 말고 딸기라떼 한 잔", 10L, 1, DrinkSize.REGULAR, VoiceOrderAction.ADD, null),
                Arguments.of("아이스 아메리카노 한 잔 빼줘", 1L, 1, DrinkSize.REGULAR, VoiceOrderAction.REMOVE, null),
                Arguments.of("아이스 아메리카노 한 잔 시럽 빼고", 1L, 1, DrinkSize.REGULAR, VoiceOrderAction.ADD, "NO_SYRUP")
        );
    }
}
