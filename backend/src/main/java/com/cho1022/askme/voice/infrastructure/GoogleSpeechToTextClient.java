package com.cho1022.askme.voice.infrastructure;

import com.cho1022.askme.common.exception.ServiceUnavailableException;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.google-stt.enabled", havingValue = "true")
public class GoogleSpeechToTextClient implements SpeechToTextClient, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(GoogleSpeechToTextClient.class);

    private final String languageCode;
    private final long timeoutSeconds;
    private volatile SpeechClient speechClient;

    public GoogleSpeechToTextClient(
            @Value("${app.google-stt.language-code}") String languageCode,
            @Value("${app.google-stt.timeout-seconds}") long timeoutSeconds
    ) {
        this.languageCode = languageCode;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public String transcribe(byte[] audio, String mimeType) {
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(encodingFor(mimeType))
                .setSampleRateHertz(48000)
                .setLanguageCode(languageCode)
                .setModel("latest_short")
                .setEnableAutomaticPunctuation(true)
                .build();
        RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                .setContent(ByteString.copyFrom(audio))
                .build();
        RecognizeRequest request = RecognizeRequest.newBuilder()
                .setConfig(config)
                .setAudio(recognitionAudio)
                .build();
        GrpcCallContext callContext = GrpcCallContext.createDefault()
                .withTimeoutDuration(Duration.ofSeconds(timeoutSeconds));

        try {
            RecognizeResponse response = getSpeechClient().recognizeCallable().call(request, callContext);
            return response.getResultsList().stream()
                    .filter(result -> result.getAlternativesCount() > 0)
                    .map(result -> result.getAlternatives(0).getTranscript())
                    .reduce("", (left, right) -> (left + " " + right).trim());
        } catch (IOException | RuntimeException exception) {
            log.warn("Google Speech-to-Text request failed", exception);
            throw new ServiceUnavailableException("Google 음성 인식에 연결하지 못했습니다.", exception);
        }
    }

    private SpeechClient getSpeechClient() throws IOException {
        SpeechClient current = speechClient;
        if (current != null) {
            return current;
        }
        synchronized (this) {
            if (speechClient == null) {
                speechClient = SpeechClient.create();
            }
            return speechClient;
        }
    }

    @Override
    public void destroy() {
        SpeechClient current = speechClient;
        if (current != null) {
            current.close();
            speechClient = null;
        }
    }

    private RecognitionConfig.AudioEncoding encodingFor(String mimeType) {
        return mimeType != null && mimeType.toLowerCase().startsWith("audio/ogg")
                ? RecognitionConfig.AudioEncoding.OGG_OPUS
                : RecognitionConfig.AudioEncoding.WEBM_OPUS;
    }
}
