package com.cho1022.askme.voice.service;

import com.cho1022.askme.menu.domain.Menu;
import com.cho1022.askme.menu.domain.MenuOption;
import com.cho1022.askme.menu.domain.Temperature;
import com.cho1022.askme.order.domain.DrinkSize;
import com.cho1022.askme.voice.domain.ParseStatus;
import com.cho1022.askme.voice.domain.VoiceOrderAction;
import com.cho1022.askme.voice.dto.ParseVoiceOrderResponse;
import com.cho1022.askme.voice.dto.ParsedOptionResponse;
import com.cho1022.askme.voice.dto.ParsedOrderItemResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class OrderParser {

    private static final Pattern QUANTITY_PATTERN = Pattern.compile("(\\d+)\\s*(?:잔|개|컵)?");
    private static final Set<String> FILLERS = Set.of(
            "주세요", "주문", "추가", "해줘", "해 주세요", "부탁해", "부탁합니다", "먹고 싶어", "마시고 싶어"
    );

    private final TranscriptNormalizer normalizer;

    public OrderParser(TranscriptNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    public ParseVoiceOrderResponse parse(String transcript, List<Menu> menus) {
        String normalized = normalizer.normalize(transcript);
        String effectiveText = applyCorrection(normalized);
        List<MenuMatch> matches = findMatches(effectiveText, menus);

        if (matches.isEmpty()) {
            return new ParseVoiceOrderResponse(
                    transcript,
                    normalized,
                    ParseStatus.NO_MATCH,
                    List.of(),
                    List.of(effectiveText),
                    "메뉴를 찾지 못했습니다. 메뉴 이름을 다시 말씀해 주세요."
            );
        }

        List<ParsedOrderItemResponse> items = new ArrayList<>();
        for (int index = 0; index < matches.size(); index++) {
            MenuMatch match = matches.get(index);
            int contextEnd = index + 1 < matches.size() ? matches.get(index + 1).start() : effectiveText.length();
            String context = effectiveText.substring(match.start(), contextEnd);
            items.add(new ParsedOrderItemResponse(
                    match.menu().getId(),
                    match.menu().getName(),
                    extractQuantity(context),
                    detectSize(context),
                    detectAction(context),
                    detectOptions(context, match.menu())
            ));
        }

        List<String> unresolved = unresolvedTerms(effectiveText, matches);
        ParseStatus status = unresolved.isEmpty()
                ? ParseStatus.CONFIRMATION_REQUIRED
                : ParseStatus.CLARIFICATION_REQUIRED;
        String message = status == ParseStatus.CONFIRMATION_REQUIRED
                ? "주문 내용을 확인한 뒤 장바구니에 반영해 주세요."
                : "인식하지 못한 표현이 있습니다. 표시된 내용을 확인해 주세요.";

        return new ParseVoiceOrderResponse(transcript, normalized, status, items, unresolved, message);
    }

    private String applyCorrection(String text) {
        String[] correctionParts = text.split("(?:말고|아니고|대신)");
        return correctionParts.length > 1 ? correctionParts[correctionParts.length - 1].trim() : text;
    }

    private List<MenuMatch> findMatches(String text, List<Menu> menus) {
        List<MenuMatch> candidates = new ArrayList<>();
        for (Menu menu : menus) {
            Set<String> aliases = new HashSet<>();
            aliases.add(menu.getName());
            aliases.add(menu.getGroupName());
            menu.getAliases().forEach(alias -> aliases.add(alias.getAlias()));

            for (String alias : aliases) {
                String normalizedAlias = normalizer.normalize(alias);
                int cursor = 0;
                while (!normalizedAlias.isBlank()) {
                    int start = text.indexOf(normalizedAlias, cursor);
                    if (start < 0) {
                        break;
                    }
                    candidates.add(new MenuMatch(menu, start, start + normalizedAlias.length(), normalizedAlias));
                    cursor = start + normalizedAlias.length();
                }
            }
        }

        candidates.sort(Comparator
                .comparingInt(MenuMatch::start)
                .thenComparing((MenuMatch match) -> -match.alias().length())
                .thenComparing(match -> temperatureRank(text, match))
                .thenComparing(match -> match.menu().getId()));

        List<MenuMatch> accepted = new ArrayList<>();
        for (MenuMatch candidate : candidates) {
            boolean overlaps = accepted.stream().anyMatch(existing -> rangesOverlap(existing, candidate));
            if (!overlaps) {
                accepted.add(candidate);
            }
        }
        accepted.sort(Comparator.comparingInt(MenuMatch::start));
        return accepted;
    }

    private int temperatureRank(String text, MenuMatch match) {
        int contextStart = Math.max(0, match.start() - 5);
        int contextEnd = Math.min(text.length(), match.end() + 5);
        String context = text.substring(contextStart, contextEnd);
        if (context.contains("아이스") && match.menu().getTemperature() == Temperature.ICE) {
            return 0;
        }
        if ((context.contains("핫") || context.contains("따뜻")) && match.menu().getTemperature() == Temperature.HOT) {
            return 0;
        }
        return match.menu().getTemperature() == Temperature.ICE ? 1 : 2;
    }

    private boolean rangesOverlap(MenuMatch left, MenuMatch right) {
        return left.start() < right.end() && right.start() < left.end();
    }

    private int extractQuantity(String context) {
        Matcher matcher = QUANTITY_PATTERN.matcher(context);
        while (matcher.find()) {
            int quantity = Integer.parseInt(matcher.group(1));
            if (quantity > 0) {
                return Math.min(quantity, 99);
            }
        }
        return 1;
    }

    private DrinkSize detectSize(String context) {
        return context.contains("라지") ? DrinkSize.LARGE : DrinkSize.REGULAR;
    }

    private VoiceOrderAction detectAction(String context) {
        return context.matches(".*(?:취소|빼줘|빼 주|삭제|제거|지워).*?")
                ? VoiceOrderAction.REMOVE
                : VoiceOrderAction.ADD;
    }

    private List<ParsedOptionResponse> detectOptions(String context, Menu menu) {
        List<ParsedOptionResponse> options = new ArrayList<>();
        if (context.contains("샷") && context.matches(".*(?:추가|더|하나).*")) {
            optionByCode(menu, "EXTRA_SHOT").ifPresent(options::add);
        }
        if (context.contains("시럽") && context.matches(".*(?:빼|없이|제외).*")) {
            optionByCode(menu, "NO_SYRUP").ifPresent(options::add);
        }
        return options;
    }

    private java.util.Optional<ParsedOptionResponse> optionByCode(Menu menu, String code) {
        return menu.getOptions().stream()
                .filter(MenuOption::isActive)
                .filter(option -> option.getCode().equals(code))
                .findFirst()
                .map(option -> new ParsedOptionResponse(option.getId(), option.getCode(), option.getName()));
    }

    private List<String> unresolvedTerms(String text, List<MenuMatch> matches) {
        StringBuilder remaining = new StringBuilder(text);
        matches.stream()
                .sorted(Comparator.comparingInt(MenuMatch::start).reversed())
                .forEach(match -> remaining.replace(match.start(), match.end(), " "));
        String cleaned = remaining.toString()
                .replaceAll("\\d+\\s*(잔|개|컵)?", " ")
                .replaceAll("(아이스|핫|라지|레귤러|하고|이랑|랑|그리고|,|\\+)", " ")
                .replaceAll("(샷|시럽|없이|제외|취소|빼고|빼줘|빼 주|삭제|제거|지워)", " ");
        for (String filler : FILLERS) {
            cleaned = cleaned.replace(filler, " ");
        }
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return cleaned.length() >= 2 ? List.of(cleaned) : List.of();
    }

    private record MenuMatch(Menu menu, int start, int end, String alias) {
    }
}
