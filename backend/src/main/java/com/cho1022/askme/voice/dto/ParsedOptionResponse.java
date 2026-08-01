package com.cho1022.askme.voice.dto;

public record ParsedOptionResponse(
        Long optionId,
        String code,
        String name
) {
}
