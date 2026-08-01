package com.cho1022.askme.voice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParseVoiceOrderRequest(
        @NotBlank @Size(max = 2000) String transcript
) {
}
