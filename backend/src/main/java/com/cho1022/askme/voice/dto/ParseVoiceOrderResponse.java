package com.cho1022.askme.voice.dto;

import com.cho1022.askme.voice.domain.ParseStatus;
import java.util.List;

public record ParseVoiceOrderResponse(
        String transcript,
        String normalizedTranscript,
        ParseStatus status,
        List<ParsedOrderItemResponse> items,
        List<String> unresolvedTerms,
        String message
) {
}
