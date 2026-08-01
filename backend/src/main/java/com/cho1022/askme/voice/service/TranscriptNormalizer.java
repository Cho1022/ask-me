package com.cho1022.askme.voice.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class TranscriptNormalizer {

    private static final Map<String, String> PHRASE_REPLACEMENTS = new LinkedHashMap<>();
    private static final Map<String, Integer> KOREAN_NUMBERS = new LinkedHashMap<>();

    static {
        PHRASE_REPLACEMENTS.put("아이스아메리카노", "아이스 아메리카노");
        PHRASE_REPLACEMENTS.put("아이스 아메", "아이스 아메리카노");
        PHRASE_REPLACEMENTS.put("아아", "아이스 아메리카노");
        PHRASE_REPLACEMENTS.put("뜨거운 아메리카노", "핫 아메리카노");
        PHRASE_REPLACEMENTS.put("따뜻한 아메리카노", "핫 아메리카노");
        PHRASE_REPLACEMENTS.put("뜨아", "핫 아메리카노");
        PHRASE_REPLACEMENTS.put("카페 라떼", "카페라떼");
        PHRASE_REPLACEMENTS.put("큰 사이즈", "라지");
        PHRASE_REPLACEMENTS.put("큰 걸로", "라지");
        PHRASE_REPLACEMENTS.put("큰걸로", "라지");
        PHRASE_REPLACEMENTS.put("큰 거", "라지");

        KOREAN_NUMBERS.put("열두", 12);
        KOREAN_NUMBERS.put("열한", 11);
        KOREAN_NUMBERS.put("아홉", 9);
        KOREAN_NUMBERS.put("여덟", 8);
        KOREAN_NUMBERS.put("일곱", 7);
        KOREAN_NUMBERS.put("여섯", 6);
        KOREAN_NUMBERS.put("다섯", 5);
        KOREAN_NUMBERS.put("하나", 1);
        KOREAN_NUMBERS.put("둘", 2);
        KOREAN_NUMBERS.put("셋", 3);
        KOREAN_NUMBERS.put("넷", 4);
        KOREAN_NUMBERS.put("열", 10);
        KOREAN_NUMBERS.put("네", 4);
        KOREAN_NUMBERS.put("세", 3);
        KOREAN_NUMBERS.put("두", 2);
        KOREAN_NUMBERS.put("한", 1);
    }

    public String normalize(String transcript) {
        String normalized = transcript == null ? "" : transcript.toLowerCase().trim();
        normalized = normalized.replaceAll("[^0-9a-z가-힣,+ ]", " ");
        for (Map.Entry<String, String> entry : PHRASE_REPLACEMENTS.entrySet()) {
            String phrasePattern = Pattern.quote(entry.getKey()) + "(?=$|[^0-9a-z가-힣])";
            normalized = normalized.replaceAll(phrasePattern, Matcher.quoteReplacement(entry.getValue()));
        }
        for (Map.Entry<String, Integer> entry : KOREAN_NUMBERS.entrySet()) {
            String quantityPattern = Pattern.quote(entry.getKey()) + "\\s*(?=잔|개|컵)";
            normalized = normalized.replaceAll(quantityPattern, String.valueOf(entry.getValue()) + " ");
        }
        return normalized.replaceAll("\\s+", " ").trim();
    }
}
