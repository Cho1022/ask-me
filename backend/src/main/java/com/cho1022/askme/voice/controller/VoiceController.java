package com.cho1022.askme.voice.controller;

import com.cho1022.askme.common.exception.BadRequestException;
import com.cho1022.askme.voice.dto.ParseVoiceOrderRequest;
import com.cho1022.askme.voice.dto.ParseVoiceOrderResponse;
import com.cho1022.askme.voice.dto.TranscriptionResponse;
import com.cho1022.askme.voice.infrastructure.SpeechToTextClient;
import com.cho1022.askme.voice.service.VoiceOrderService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    private static final Set<String> ALLOWED_MIME_PREFIXES = Set.of("audio/webm", "audio/ogg");

    private final SpeechToTextClient speechToTextClient;
    private final VoiceOrderService voiceOrderService;
    private final long maxAudioBytes;
    private final long maxDurationMs;

    public VoiceController(
            SpeechToTextClient speechToTextClient,
            VoiceOrderService voiceOrderService,
            @Value("${app.google-stt.max-audio-bytes}") long maxAudioBytes,
            @Value("${app.google-stt.max-duration-ms}") long maxDurationMs
    ) {
        this.speechToTextClient = speechToTextClient;
        this.voiceOrderService = voiceOrderService;
        this.maxAudioBytes = maxAudioBytes;
        this.maxDurationMs = maxDurationMs;
    }

    @PostMapping(path = "/transcriptions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TranscriptionResponse transcribe(
            @RequestPart("audio") MultipartFile audio,
            @RequestParam("durationMs") long durationMs
    ) {
        validateAudio(audio, durationMs);
        try {
            String transcript = speechToTextClient.transcribe(audio.getBytes(), audio.getContentType());
            if (transcript.isBlank()) {
                throw new BadRequestException("음성을 인식하지 못했습니다. 다시 말씀해 주세요.");
            }
            return new TranscriptionResponse(transcript);
        } catch (IOException exception) {
            throw new BadRequestException("음성 파일을 읽지 못했습니다.");
        }
    }

    @PostMapping("/orders/parse")
    public ParseVoiceOrderResponse parse(@Valid @RequestBody ParseVoiceOrderRequest request) {
        return voiceOrderService.parse(request.transcript());
    }

    private void validateAudio(MultipartFile audio, long durationMs) {
        if (audio == null || audio.isEmpty()) {
            throw new BadRequestException("음성 파일이 비어 있습니다.");
        }
        if (audio.getSize() > maxAudioBytes) {
            throw new BadRequestException("음성 파일은 5MB를 넘을 수 없습니다.");
        }
        if (durationMs <= 0 || durationMs > maxDurationMs) {
            throw new BadRequestException("녹음 시간은 20초 이하여야 합니다.");
        }
        String contentType = audio.getContentType() == null ? "" : audio.getContentType().toLowerCase();
        boolean allowed = ALLOWED_MIME_PREFIXES.stream().anyMatch(contentType::startsWith);
        if (!allowed) {
            throw new BadRequestException("webm/opus 또는 ogg/opus 음성만 업로드할 수 있습니다.");
        }
    }
}
