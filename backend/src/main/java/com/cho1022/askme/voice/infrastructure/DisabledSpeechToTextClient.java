package com.cho1022.askme.voice.infrastructure;

import com.cho1022.askme.common.exception.ServiceUnavailableException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.google-stt.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledSpeechToTextClient implements SpeechToTextClient {

    @Override
    public String transcribe(byte[] audio, String mimeType) {
        throw new ServiceUnavailableException(
                "Google STT가 비활성화되어 있습니다. GOOGLE_STT_ENABLED와 인증 정보를 설정해 주세요."
        );
    }
}
